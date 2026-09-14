package io.github.arrayv.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Available video encoders and the shared ffmpeg command/process launcher.
 * Falls back to software x264 if a selected hardware encoder is unavailable.
 */
public enum VideoEncoder {
    X264_VERYFAST("x264 veryfast", "-c:v", "libx264", "-preset", "veryfast", "-crf", "18"),
    X264_ULTRAFAST("x264 ultrafast", "-c:v", "libx264", "-preset", "ultrafast", "-crf", "20"),
    NVENC("NVIDIA NVENC", "-c:v", "h264_nvenc", "-preset", "p4", "-cq", "19"),
    QSV("Intel Quick Sync", "-c:v", "h264_qsv", "-preset", "veryfast"),
    AMF("AMD AMF", "-c:v", "h264_amf", "-quality", "speed");

    private final String displayName;
    private final String[] arguments;

    VideoEncoder(String displayName, String... arguments) {
        this.displayName = displayName;
        this.arguments = arguments;
    }

    public void apply(List<String> command) {
        for (String argument : this.arguments) {
            command.add(argument);
        }
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    /**
     * Spawns ffmpeg for a raw BGR24 stream. If the selected hardware encoder is
     * not usable, retries with x264 veryfast and reports the fallback.
     */
    public static Result startProcess(int fps, int width, int height, File output, VideoEncoder selected)
            throws IOException {
        VideoEncoder encoder = selected == null ? X264_VERYFAST : selected;
        if (encoder != X264_VERYFAST && !isAvailable(encoder)) {
            Process process = new ProcessBuilder(buildCommand(fps, width, height, output, X264_VERYFAST)).start();
            return new Result(process, X264_VERYFAST, "Encoder " + encoder + " is not available on this system.");
        }
        Process process = new ProcessBuilder(buildCommand(fps, width, height, output, encoder)).start();
        return new Result(process, encoder, null);
    }

    /**
     * Functionally checks whether an encoder can actually encode a frame, by
     * running ffmpeg on a single tiny raw frame to a null output. A spawn-only
     * check is not enough: some encoders (e.g. AMF) start fine and only fail
     * once they receive a frame.
     */
    public static boolean isAvailable(VideoEncoder encoder) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-hide_banner");
        command.add("-loglevel");
        command.add("error");
        command.add("-f");
        command.add("rawvideo");
        command.add("-pix_fmt");
        command.add("bgr24");
        command.add("-s");
        command.add("64x64");
        command.add("-r");
        command.add("1");
        command.add("-i");
        command.add("-");
        command.add("-frames:v");
        command.add("1");
        encoder.apply(command);
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-f");
        command.add("null");
        command.add("-");

        Process process;
        try {
            process = new ProcessBuilder(command).start();
        } catch (IOException e) {
            return false;
        }
        // Capture stderr so ffmpeg can't block on a full pipe.
        Thread drain = new Thread(() -> {
            try (InputStream in = process.getErrorStream()) {
                byte[] buffer = new byte[4096];
                while (in.read(buffer) != -1) {
                    // discard
                }
            } catch (IOException ignored) {
            }
        });
        drain.setDaemon(true);
        drain.start();
        try (OutputStream out = process.getOutputStream()) {
            out.write(new byte[64 * 64 * 3]);
        } catch (IOException ignored) {
        }
        try {
            if (!process.waitFor(15, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return false;
        }
        return process.exitValue() == 0;
    }

    private static List<String> buildCommand(int fps, int width, int height, File output, VideoEncoder encoder) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-loglevel");
        command.add("error");
        command.add("-f");
        command.add("rawvideo");
        command.add("-pix_fmt");
        command.add("bgr24");
        command.add("-s");
        command.add(width + "x" + height);
        command.add("-r");
        command.add(Integer.toString(fps));
        command.add("-i");
        command.add("-");
        command.add("-an");
        encoder.apply(command);
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-movflags");
        command.add("+faststart");
        command.add(output.getAbsolutePath());
        return command;
    }

    public static final class Result {
        public final Process process;
        public final VideoEncoder encoder;
        public final String fallbackError;

        Result(Process process, VideoEncoder encoder, String fallbackError) {
            this.process = process;
            this.encoder = encoder;
            this.fallbackError = fallbackError;
        }
    }
}
