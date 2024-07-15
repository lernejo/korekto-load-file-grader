package com.github.lernejo.korekto.grader.load_file.parts;

import com.github.lernejo.korekto.grader.load_file.LaunchingContext;
import com.github.lernejo.korekto.grader.load_file.WeatherComputationData;
import com.github.lernejo.korekto.grader.load_file.process.JavaProcessLauncher;
import com.github.lernejo.korekto.grader.load_file.process.ProcessResult;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.lernejo.korekto.grader.load_file.StringUtils.safeEscapeElide;
import static com.github.lernejo.korekto.grader.load_file.StringUtils.safeLowerTrim;

public record Part3Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    @Override
    public GradePart grade(LaunchingContext context) {
        if (context.hasCompilationFailed()) {
            return result(List.of("Not available when there is compilation failures"), 0.0D);
        }

        Optional<Path> jarPath = context.jarPath();
        if (jarPath.isEmpty()) {
            return result(List.of("Missing packaged JAR in /target"), 0.0D);
        }

        Set<WeatherComputationData> dataset = context.getDataset(2);

        List<String> resultErrors = verifyDataset(dataset, jarPath.get());

        return result(resultErrors, maxGrade - resultErrors.size() * (maxGrade / dataset.size()));
    }

    private List<String> verifyDataset(Set<WeatherComputationData> dataset, Path jarPath) {
        List<String> errors = new ArrayList<>();
        for (WeatherComputationData data : dataset) {
            ProcessResult processResult = JavaProcessLauncher
                .withClasspath(jarPath)
                .withMaxHeap("4M")
                .withMainClass("fr.lernejo.file.CsvReader")
                .withParameters(
                    LaunchingContext.DATA_FILE_PATH.toString(),
                    data.start(),
                    data.end(),
                    data.metric(),
                    data.selector(),
                    data.agg()
                )
                .start();
            StringBuilder sb = new StringBuilder();
            if (processResult.exitCode() != 0) {
                sb.append("expecting exit code to be `0` but was `").append(processResult.exitCode()).append('`');
            }

            if (!data.result().trim().toLowerCase().equals(safeLowerTrim(processResult.stdout()))) {
                if (!sb.isEmpty()) {
                    sb.append(" and ");
                }
                sb.append("expecting output to be `").append(data.result()).append("` but was `").append(safeEscapeElide(processResult.getOutput())).append('`');
            }
            if (!sb.isEmpty()) {
                errors.add("In the case of " + data.asDescription() + ", " + sb);
            }
        }
        return errors;
    }
}
