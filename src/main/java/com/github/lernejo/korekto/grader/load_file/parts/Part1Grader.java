package com.github.lernejo.korekto.grader.load_file.parts;

import com.github.lernejo.korekto.grader.load_file.LaunchingContext;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.Processes;
import kotlin.Triple;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public record Part1Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    private static final String TEST_FILENAME = "test.txt";
    private static final String TEST_LONG_FILENAME = "test_long.txt";

    private static String safeEscapeElide(String s) {
        if (s == null) {
            return "";
        } else {
            String s1 = s.trim().replaceAll("\n", Matcher.quoteReplacement("\\n"));
            if (s1.length() > 80) {
                return s1.substring(0, 77) + "...";
            }
            return s1;
        }
    }

    private static String safeLowerTrim(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim().toLowerCase();
        }
    }

    private List<Feature> features(String content, String directoryName) {
        return List.of(
            new Feature("no argument", 3, "Missing argument"),
            new Feature("2 arguments", 4, "Too many arguments", "toto", "titi"),
            new Feature("a non-existing file", 5, "File not found", "not_existing_file"),
            new Feature("a directory", 6, "A file is required", directoryName),
            new Feature("a large file", 7, "File too large", TEST_LONG_FILENAME),
            new Feature("a normal file", 0, content, TEST_FILENAME)
        );
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (context.hasCompilationFailed()) {
            return result(List.of("Not available when there is compilation failures"), 0.0D);
        }

        Optional<Path> jarPath = context.jarPath();
        if (jarPath.isEmpty()) {
            return result(List.of("Missing packaged JAR in /target"), 0.0D);
        }

        Triple<Path, String, String> setup = setupWorkingDirectory(context);

        List<Feature> features = features(setup.component2(), setup.component3());

        List<String> featureErrors = verifyFeatures(context, features, jarPath.get(), setup.component1());

        return result(featureErrors, maxGrade - featureErrors.size() * (maxGrade / features.size()));
    }

    private List<String> verifyFeatures(LaunchingContext context, List<Feature> features, Path jarPath, Path workingDirectory) {
        List<String> featureErrors = new ArrayList<>();
        for (Feature feature : features) {
            Processes.ProcessResult result = context.launchJava(workingDirectory, jarPath, "fr.lernejo.file.Cat", feature.arguments);
            StringBuilder sb = new StringBuilder();
            if (result.getExitCode() != feature.exitCode) {
                sb.append("expecting exit code to be `").append(feature.exitCode).append("` but was `").append(result.getExitCode()).append('`');
            }

            if (!feature.content.trim().toLowerCase().equals(safeLowerTrim(result.getOutput()))) {
                if (!sb.isEmpty()) {
                    sb.append(" and ");
                }
                sb.append("expecting output to be `").append(safeEscapeElide(feature.content)).append("` but was `").append(safeEscapeElide(result.getOutput())).append('`');
            }
            if (!sb.isEmpty()) {
                featureErrors.add("In the case of " + feature.name + " " + sb);
            }
        }
        return featureErrors;
    }

    private Triple<Path, String, String> setupWorkingDirectory(LaunchingContext context) {
        try {
            Path workingDirectory = Files.createTempDirectory("korekto");
            String content = context.writeRandomWords(workingDirectory.resolve(TEST_FILENAME), 27);
            context.writeRandomWords(workingDirectory.resolve(TEST_LONG_FILENAME), 23_563);
            String directoryName = context.getRandomWord();
            Files.createDirectory(workingDirectory.resolve(directoryName));
            return new Triple<>(workingDirectory, content, directoryName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    record Feature(String name, int exitCode, String content, String... arguments) {
    }
}
