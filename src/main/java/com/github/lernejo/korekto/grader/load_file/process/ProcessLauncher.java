package com.github.lernejo.korekto.grader.load_file.process;

import com.github.lernejo.korekto.toolkit.misc.SubjectForToolkitInclusion;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SubjectForToolkitInclusion(additionalInfo = "in replacement of Processes")
public class ProcessLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ProcessLauncher.class);
    private final List<String> command = new ArrayList<>();
    private Path workingDirectory;

    public static ProcessLauncher withCommand(List<String> command) {
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.command.addAll(command);
        return processLauncher;
    }

    public static byte[] toBytes(InputStream inputStream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return buffer.toByteArray();
    }

    public static String readStream(InputStream inputStream) {
        byte[] bytes = toBytes(inputStream);
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes))) {
            Charset charset = Optional.ofNullable(UniversalDetector.detectCharset(new ByteArrayInputStream(bytes)))
                .map(Charset::forName)
                .orElse(StandardCharsets.UTF_8);
            if (!StandardCharsets.UTF_8.equals(charset)) {
                logger.debug("Detected !UTF-8 charset: {}", charset);
            }
            bis.mark(1);
            var firstByte = bis.read();
            if (firstByte != -1) {
                bis.reset();
                return new Scanner(bis, charset).useDelimiter("\\A").next();
            } else {
                return "";
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ProcessLauncher withWorkingDirectory(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public ProcessResult start() {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory.toFile());
        }

        try {
            var process = processBuilder.start();
            int exitCode = process.waitFor();
            var stdout = readStream(process.getInputStream());
            var stderr = readStream(process.getErrorStream());

            if (exitCode != 0) {
                return ProcessResult.error(exitCode, stdout, stderr);
            }
            return ProcessResult.success(stdout, stderr);
        } catch (IOException | InterruptedException e) {
            return ProcessResult.error(e);
        }
    }
}
