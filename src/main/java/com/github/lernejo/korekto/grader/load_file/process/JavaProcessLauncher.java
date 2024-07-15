package com.github.lernejo.korekto.grader.load_file.process;

import com.github.lernejo.korekto.toolkit.misc.OS;
import com.github.lernejo.korekto.toolkit.misc.SubjectForToolkitInclusion;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@SubjectForToolkitInclusion
public class JavaProcessLauncher {

    private final List<Path> classpath = new ArrayList<>();
    private final List<String> parameters = new ArrayList<>();
    private String mainClass;
    private Path workingDirectory;
    private String xmx;

    public static JavaProcessLauncher withClasspath(Path... classpath) {
        JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();
        javaProcessLauncher.classpath.addAll(asList(classpath));
        return javaProcessLauncher;
    }

    public JavaProcessLauncher withMaxHeap(String xmx) {
        this.xmx = xmx;
        return this;
    }

    public JavaProcessLauncher withWorkingDirectory(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public JavaProcessLauncher withMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public JavaProcessLauncher withParameters(String... parameters) {
        this.parameters.addAll(asList(parameters));
        return this;
    }

    public ProcessResult start() {
        Path binPath = Paths.get(System.getProperty("java.home")).resolve("bin");
        final Path javaPath;
        if (OS.WINDOWS.isCurrentOs()) {
            javaPath = binPath.resolve("java.exe");
        } else {
            javaPath = binPath.resolve("java");
        }
        List<String> command = new ArrayList<>(List.of(
            javaPath.toString(),
            "-Duser.country=UK",
            "-Duser.language=en",
            "-DFile.Encoding=UTF-8"
        ));
        if (xmx != null) {
            command.add("-Xmx" + xmx);
        }
        command.add("-cp");
        command.add(classpath.stream().map(Object::toString).collect(Collectors.joining(File.pathSeparator)));
        command.add(mainClass);
        command.addAll(parameters);

        return ProcessLauncher
            .withCommand(command)
            .withWorkingDirectory(workingDirectory)
            .start();
    }
}
