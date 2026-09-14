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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;

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
 * Records the visualizer framebuffer to an MP4 video by piping raw frames to
 * an external {@code ffmpeg} process. The framebuffer is sampled on a fixed
 * frame-rate clock, so the resulting video matches the current visual delay in
 * real time.
 */
public final class VideoRecorder {
    private final ArrayVisualizer arrayVisualizer;

    private volatile boolean recording;
    private boolean stopping;

    private Process process;
    private OutputStream stdin;
    private Thread captureThread;
    private Thread errorThread;
    private ByteArrayOutputStream stderrBuffer;
    private Throwable failure;
    private File output;

    private int width;
    private int height;
    private int fps;

    public VideoRecorder(ArrayVisualizer arrayVisualizer) {
        this.arrayVisualizer = arrayVisualizer;
    }

    public boolean isRecording() {
        return this.recording;
    }

    public File getOutput() {
        return this.output;
    }

    public synchronized boolean start(File file, int fps, VideoEncoder encoder) {
        if (this.recording || this.stopping) {
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
            JErrorPane.invokeCustomErrorMessage("The render buffer is too small to record.");
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
            JErrorPane.invokeErrorMessage(e, "Record Video");
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
        this.recording = true;

        final ByteArrayOutputStream stderrBuffer = this.stderrBuffer;
        Thread errorThread = new Thread(() -> {
            try (InputStream in = process.getErrorStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    // Capture ffmpeg's stderr so it never blocks on a full pipe
                    // and we can report a meaningful error if it fails.
                    stderrBuffer.write(buffer, 0, read);
                }
            } catch (IOException ignored) {
            }
        }, "FFmpeg-Stderr");
        errorThread.setDaemon(true);
        this.errorThread = errorThread;
        errorThread.start();

        this.captureThread = new Thread(this::captureLoop, "VideoCapture");
        this.captureThread.setDaemon(true);
        this.captureThread.start();
        return true;
    }

    private void captureLoop() {
        long frameNanos = 1_000_000_000L / Math.max(1, this.fps);
        BufferedImage scratch = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = scratch.createGraphics();
        byte[] data = ((DataBufferByte) scratch.getRaster().getDataBuffer()).getData();
        Object frameLock = this.arrayVisualizer.getFrameLock();
        OutputStream stdin = this.stdin;
        long next = System.nanoTime();
        try {
            while (this.recording) {
                boolean captured = false;
                synchronized (frameLock) {
                    BufferedImage framebuffer = this.arrayVisualizer.getFramebuffer();
                    if (framebuffer != null) {
                        graphics.drawImage(framebuffer, 0, 0, this.width, this.height, null);
                        captured = true;
                    }
                }
                if (captured) {
                    stdin.write(data);
                }
                next += frameNanos;
                long sleep = next - System.nanoTime();
                if (sleep > 0) {
                    LockSupport.parkNanos(sleep);
                } else {
                    next = System.nanoTime();
                }
            }
        } catch (IOException e) {
            if (this.recording) {
                this.failure = e;
            }
        } finally {
            graphics.dispose();
        }
    }

    public void stop() {
        stop(true);
    }

    public void stop(boolean notify) {
        Thread captureThread;
        Thread errorThread;
        OutputStream stdin;
        Process process;
        synchronized (this) {
            if (this.stopping || !this.recording) {
                return;
            }
            this.recording = false;
            this.stopping = true;
            captureThread = this.captureThread;
            errorThread = this.errorThread;
            stdin = this.stdin;
            process = this.process;
            try {
                if (stdin != null) {
                    // Closing stdin unblocks the capture thread if it is stuck writing.
                    stdin.close();
                }
            } catch (IOException ignored) {
            }
        }

        if (captureThread != null) {
            try {
                captureThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        boolean processTimedOut = false;
        if (process != null) {
            try {
                if (!process.waitFor(15, TimeUnit.SECONDS)) {
                    processTimedOut = true;
                    process.destroyForcibly();
                    process.waitFor(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                processTimedOut = true;
                process.destroyForcibly();
            }
        }
        if (errorThread != null) {
            try {
                errorThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Throwable failure;
        File output;
        synchronized (this) {
            failure = this.failure;
            output = this.output;
            if (failure == null) {
                if (processTimedOut) {
                    failure = new IOException("ffmpeg timed out while finalizing the video.");
                } else if (process != null) {
                    int exitCode;
                    try {
                        exitCode = process.exitValue();
                    } catch (IllegalThreadStateException e) {
                        exitCode = 0;
                    }
                    if (exitCode != 0) {
                        ByteArrayOutputStream buffer = this.stderrBuffer;
                        String stderr = buffer == null
                            ? ""
                            : new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
                        failure = new IOException("ffmpeg exited with code " + exitCode
                            + (stderr.isEmpty() ? "" : ":\n" + stderr));
                    }
                }
            }
            this.failure = null;
            this.process = null;
            this.stdin = null;
            this.captureThread = null;
            this.errorThread = null;
            this.stderrBuffer = null;
            this.stopping = false;
        }
        if (failure != null) {
            JErrorPane.invokeErrorMessage(failure, "Render Video");
        } else if (notify && output != null) {
            final File saved = output;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Video saved to:\n" + saved.getAbsolutePath(),
                "Render Video",
                JOptionPane.INFORMATION_MESSAGE));
        }
    }
}
