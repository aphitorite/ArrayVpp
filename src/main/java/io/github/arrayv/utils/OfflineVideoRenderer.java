package io.github.arrayv.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.panes.RenderProgressDialog;

/*
 *
MIT License

Copyright (c) 2026 ArrayV Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

/**
 * Deterministic, non-real-time video renderer. Frames are captured
 * synchronously by the sorting thread (see {@link ArrayVisualizer#updateNow()})
 * together with a virtual timestamp, buffered, and streamed to an external
 * {@code ffmpeg} process. Frame timing is derived from the virtual clock (the
 * sum of the visual delays) rather than from wall-clock time, so the render can
 * take as long as it needs without dropping or time-compressing frames.
 *
 * <p>The stream is constant frame rate at the chosen {@code fps}; a captured
 * frame is repeated enough times to cover its virtual duration using cumulative
 * rounding (so the total duration is exact). Updates shorter than {@code 1/fps}
 * are merged, which is the temporal resolution limit of the chosen rate.</p>
 */
public final class OfflineVideoRenderer {
    private static final int QUEUE_CAPACITY = 8;

    private final ArrayVisualizer arrayVisualizer;

    private Process process;
    private OutputStream stdin;
    private Thread encoderThread;
    private Thread errorThread;
    private ByteArrayOutputStream stderrBuffer;

    private final ArrayBlockingQueue<Frame> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final ConcurrentLinkedQueue<byte[]> pool = new ConcurrentLinkedQueue<>();

    private volatile boolean active;
    private volatile boolean ended;
    private volatile Throwable failure;
    private volatile long framesWritten;
    private volatile long startNanos;

    private long queuedFrames;
    private byte[] pending;
    private double pendingTime;
    private double baseTime;

    private int width;
    private int height;
    private int fps;
    private File output;

    private BufferedImage scratch;
    private Graphics2D scratchGraphics;
    private byte[] scratchData;

    private RenderProgressDialog dialog;

    public OfflineVideoRenderer(ArrayVisualizer arrayVisualizer) {
        this.arrayVisualizer = arrayVisualizer;
    }

    public synchronized boolean start(File file, int fps, VideoEncoder encoder) {
        if (this.active) {
            return false;
        }
        BufferedImage framebuffer = this.arrayVisualizer.getFramebuffer();
        if (framebuffer == null) {
            JErrorPane.invokeCustomErrorMessage("The render buffer is not ready yet.");
            return false;
        }
        int width = framebuffer.getWidth() & ~1;
        int height = framebuffer.getHeight() & ~1;
        if (width <= 0 || height <= 0) {
            JErrorPane.invokeCustomErrorMessage("The render buffer is too small to render.");
            return false;
        }

        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        VideoEncoder.Result result;
        try {
            result = VideoEncoder.startProcess(fps, width, height, file, encoder);
        } catch (IOException e) {
            JErrorPane.invokeErrorMessage(e, "Render Video");
            JErrorPane.invokeCustomErrorMessage("Failed to start ffmpeg. Make sure ffmpeg is installed and on your PATH.");
            return false;
        }
        Process process = result.process;
        if (result.fallbackError != null) {
            String message = result.fallbackError;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "The selected video encoder was unavailable; using software x264 instead.\n" + message,
                "Video Encoder",
                JOptionPane.WARNING_MESSAGE));
        }

        this.process = process;
        this.stdin = new BufferedOutputStream(process.getOutputStream(), 1 << 20);
        this.stderrBuffer = new ByteArrayOutputStream();
        this.failure = null;
        this.output = file;
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.framesWritten = 0;
        this.queuedFrames = 0;
        this.pending = null;
        this.baseTime = 0;
        this.pendingTime = 0;
        this.startNanos = System.nanoTime();
        this.scratch = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        this.scratchGraphics = this.scratch.createGraphics();
        this.scratchData = ((DataBufferByte) this.scratch.getRaster().getDataBuffer()).getData();
        this.ended = false;
        this.active = true;

        final ByteArrayOutputStream stderrBuffer = this.stderrBuffer;
        Thread errorThread = new Thread(() -> {
            try (InputStream in = process.getErrorStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    stderrBuffer.write(buffer, 0, read);
                }
            } catch (IOException ignored) {
            }
        }, "FFmpeg-Stderr");
        errorThread.setDaemon(true);
        this.errorThread = errorThread;
        errorThread.start();

        this.encoderThread = new Thread(this::encoderLoop, "VideoEncoder");
        this.encoderThread.setDaemon(true);
        this.encoderThread.start();

        SwingUtilities.invokeLater(() -> this.dialog = new RenderProgressDialog(this.arrayVisualizer, this));
        return true;
    }

    /**
     * Submit a rendered frame at the given virtual timestamp (in milliseconds).
     * Called on the sorting thread.
     */
    public void submit(BufferedImage image, double virtualTime) {
        if (!this.active || image == null) {
            return;
        }
        byte[] buffer = this.acquireBuffer();
        this.copyToBgr(image, buffer);

        if (this.pending == null) {
            this.pending = buffer;
            this.pendingTime = virtualTime;
            this.baseTime = virtualTime;
            return;
        }

        long boundary = Math.round((virtualTime - this.baseTime) * this.fps / 1000.0);
        long copies = boundary - this.queuedFrames;
        this.queuedFrames = boundary;
        Frame frame = this.frameOrFree(this.pending, copies);
        this.pending = buffer;
        this.pendingTime = virtualTime;
        this.enqueue(frame);
    }

    private Frame frameOrFree(byte[] data, long copies) {
        if (copies <= 0) {
            this.pool.offer(data);
            return null;
        }
        return new Frame(data, copies);
    }

    private void enqueue(Frame frame) {
        if (frame == null) {
            return;
        }
        while (true) {
            if (this.failure != null || this.arrayVisualizer.sortCanceled()) {
                this.pool.offer(frame.data);
                throw new StopSort();
            }
            try {
                if (this.queue.offer(frame, 100, TimeUnit.MILLISECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.pool.offer(frame.data);
                throw new StopSort();
            }
        }
    }

    private void enqueueBlocking(byte[] data, long copies) {
        if (copies <= 0) {
            this.pool.offer(data);
            return;
        }
        Frame frame = new Frame(data, copies);
        try {
            while (!this.queue.offer(frame, 100, TimeUnit.MILLISECONDS)) {
                if (this.failure != null) {
                    this.pool.offer(data);
                    return;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.pool.offer(data);
        }
    }

    private void encoderLoop() {
        try {
            while (true) {
                Frame frame = this.queue.poll(100, TimeUnit.MILLISECONDS);
                if (frame == null) {
                    if (this.ended && this.queue.isEmpty()) {
                        break;
                    }
                    continue;
                }
                for (long i = 0; i < frame.copies; i++) {
                    this.stdin.write(frame.data);
                }
                this.framesWritten += frame.copies;
                this.pool.offer(frame.data);
            }
        } catch (IOException e) {
            if (this.active) {
                this.failure = e;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void finish(double finalTime) {
        synchronized (this) {
            if (!this.active) {
                return;
            }
            this.active = false;
        }

        if (this.pending != null) {
            long boundary = Math.round((finalTime - this.baseTime) * this.fps / 1000.0);
            long copies = boundary - this.queuedFrames;
            if (copies < 1) {
                copies = 1;
            }
            this.enqueueBlocking(this.pending, copies);
            this.pending = null;
        }

        this.ended = true;
        if (this.encoderThread != null) {
            try {
                this.encoderThread.join(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        this.finalizeProcess();

        SwingUtilities.invokeLater(() -> {
            if (this.dialog != null) {
                this.dialog.close();
                this.dialog = null;
            }
        });

        Throwable failure = this.failure;
        File output = this.output;
        if (failure != null) {
            JErrorPane.invokeErrorMessage(failure, "Render Video");
        } else if (output != null) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Video saved to:\n" + output.getAbsolutePath(),
                "Render Video",
                JOptionPane.INFORMATION_MESSAGE));
        }
        this.cleanup();
    }

    private void finalizeProcess() {
        try {
            if (this.stdin != null) {
                this.stdin.close();
            }
        } catch (IOException ignored) {
        }
        if (this.process == null) {
            return;
        }
        boolean timedOut = false;
        try {
            if (!this.process.waitFor(15, TimeUnit.SECONDS)) {
                timedOut = true;
                this.process.destroyForcibly();
                this.process.waitFor(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            timedOut = true;
            this.process.destroyForcibly();
        }
        if (this.errorThread != null) {
            try {
                this.errorThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.failure != null) {
            return;
        }
        if (timedOut) {
            this.failure = new IOException("ffmpeg timed out while finalizing the video.");
            return;
        }
        int exitCode;
        try {
            exitCode = this.process.exitValue();
        } catch (IllegalThreadStateException e) {
            exitCode = 0;
        }
        if (exitCode != 0) {
            String stderr = this.stderrBuffer == null
                ? ""
                : new String(this.stderrBuffer.toByteArray(), StandardCharsets.UTF_8).trim();
            this.failure = new IOException("ffmpeg exited with code " + exitCode
                + (stderr.isEmpty() ? "" : ":\n" + stderr));
        }
    }

    private void cleanup() {
        if (this.scratchGraphics != null) {
            this.scratchGraphics.dispose();
        }
        this.scratch = null;
        this.scratchGraphics = null;
        this.scratchData = null;
        this.stdin = null;
        this.process = null;
        this.encoderThread = null;
        this.errorThread = null;
        this.stderrBuffer = null;
        this.pool.clear();
        this.queue.clear();
    }

    private void copyToBgr(BufferedImage image, byte[] destination) {
        this.scratchGraphics.drawImage(image, 0, 0, this.width, this.height, null);
        System.arraycopy(this.scratchData, 0, destination, 0, destination.length);
    }

    private byte[] acquireBuffer() {
        byte[] buffer = this.pool.poll();
        if (buffer == null) {
            buffer = new byte[this.width * this.height * 3];
        }
        return buffer;
    }

    public long getFramesWritten() {
        return this.framesWritten;
    }

    public int getFps() {
        return this.fps;
    }

    public long getElapsedMillis() {
        return (System.nanoTime() - this.startNanos) / 1_000_000L;
    }

    private static final class Frame {
        private final byte[] data;
        private final long copies;

        private Frame(byte[] data, long copies) {
            this.data = data;
            this.copies = copies;
        }
    }
}
