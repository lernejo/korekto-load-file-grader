package com.github.lernejo.korekto.grader.load_file.process;

import com.github.lernejo.korekto.toolkit.misc.SubjectForToolkitInclusion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SubjectForToolkitInclusion(additionalInfo = "in replacement of Processes")
public class ProcessLauncher {
    private final List<String> command = new ArrayList<>();
    private Path workingDirectory;

    public static ProcessLauncher withCommand(List<String> command) {
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.command.addAll(command);
        return processLauncher;
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

    public String readStream(InputStream inputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(1);
            var firstByte = bis.read();
            if (firstByte != -1) {
                bis.reset();
                return new Scanner(bis, Charset.defaultCharset()).useDelimiter("\\A").next();
            } else {
                return "";
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
