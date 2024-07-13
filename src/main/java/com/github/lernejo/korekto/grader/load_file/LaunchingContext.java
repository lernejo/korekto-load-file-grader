package com.github.lernejo.korekto.grader.load_file;

import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;
import com.github.lernejo.korekto.toolkit.misc.OS;
import com.github.lernejo.korekto.toolkit.misc.Processes;
import com.github.lernejo.korekto.toolkit.partgrader.MavenContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LaunchingContext extends GradingContext implements MavenContext {
    private final List<String> dictionary;
    private boolean compilationFailed;
    private boolean testFailed;

    public LaunchingContext(GradingConfiguration configuration) {
        super(configuration);
        try {
            Path dictionaryPath = Paths.get(LaunchingContext.class.getClassLoader().getResource("words.txt").toURI());
            dictionary = Files.readAllLines(dictionaryPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasCompilationFailed() {
        return compilationFailed;
    }

    @Override
    public boolean hasTestFailed() {
        return testFailed;
    }

    @Override
    public void markAsCompilationFailed() {
        compilationFailed = true;
    }

    @Override
    public void markAsTestFailed() {
        testFailed = true;
    }

    public Optional<Path> jarPath() {
        try (Stream<Path> files = Files.list(getExercise().getRoot().resolve("target"))) {
            return files
                .filter(p -> p.toString().endsWith(".jar"))
                .min(Comparator.comparing(p -> p.toString().length()).thenComparing(Object::toString));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Processes.ProcessResult launchJava(Path workingDirectory, Path jarPath, String mainClass, String... arguments) {
        Path binPath = Paths.get(System.getProperty("java.home")).resolve("bin");
        final Path javaPath;
        if (OS.WINDOWS.isCurrentOs()) {
            javaPath = binPath.resolve("java.exe");
        } else {
            javaPath = binPath.resolve("java");
        }
        List<String> commandPrefix = List.of(
            javaPath.toString(),
            "-Duser.country=UK",
            "-Duser.language=en",
            "-cp",
            jarPath.toString(),
            mainClass
        );
        List<String> command = Stream.concat(commandPrefix.stream(), Arrays.stream(arguments)).toList();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory.toFile());

        try {
            var process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                var stdout = readStream(process.getInputStream());
                var stderr = readStream(process.getErrorStream());
                return Processes.ProcessResult.Companion.error(exitCode, stderr + stdout);
            }
            var stdout = readStream(process.getInputStream());
            return Processes.ProcessResult.Companion.success(stdout);
        } catch (IOException | InterruptedException e) {
            return Processes.ProcessResult.Companion.error(e);
        }
    }

    public String readStream(InputStream inputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(1);
            var firstByte = bis.read();
            if (firstByte != -1) {
                bis.reset();
                return new Scanner(bis, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            } else {
                return "";
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getRandomWord() {
        int index = getRandomSource().nextInt(dictionary.size());
        return dictionary.get(index);
    }

    public String writeRandomWords(Path path, int nbWords) {
        try {
            Supplier<String> wordSource = () -> {
                if (getRandomSource().nextInt(7) == 0) {
                    return "\n";
                }
                return getRandomWord();
            };
            String content = IntStream.range(0, nbWords)
                .mapToObj(i -> wordSource.get())
                .collect(Collectors.joining(" "));
            Files.writeString(path, content);
            return content;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
