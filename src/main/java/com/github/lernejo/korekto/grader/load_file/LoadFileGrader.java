package com.github.lernejo.korekto.grader.load_file;

import com.github.lernejo.korekto.grader.load_file.parts.Part1Grader;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.Grader;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.HumanReadableDuration;
import com.github.lernejo.korekto.toolkit.partgrader.JacocoCoveragePartGrader;
import com.github.lernejo.korekto.toolkit.partgrader.MavenCompileAndTestPartGrader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class LoadFileGrader implements Grader<LaunchingContext> {

    private final Logger logger = LoggerFactory.getLogger(LoadFileGrader.class);

    @Override
    public String name() {
        return "korekto-load-file-grader";
    }

    @Override
    public String slugToRepoUrl(String login) {
        return "https://github.com/" + login + "/java_load_file_training";
    }

    @Override
    public LaunchingContext gradingContext(GradingConfiguration configuration) {
        return new LaunchingContext(configuration);
    }

    @Override
    public void run(LaunchingContext context) {
        context.getGradeDetails().getParts().addAll(grade(context));
    }

    private Collection<? extends GradePart> grade(LaunchingContext context) {
        return graders().stream()
            .map(g -> applyPartGrader(context, g))
            .toList();
    }

    private GradePart applyPartGrader(LaunchingContext context, PartGrader<LaunchingContext> g) {
        long startTime = System.currentTimeMillis();
        try {
            return g.grade(context);
        } finally {
            logger.debug("{} in {}", g.name(), HumanReadableDuration.toString(System.currentTimeMillis() - startTime));
        }
    }

    private Collection<? extends PartGrader<LaunchingContext>> graders() {
        return List.of(
            new MavenCompileAndTestPartGrader<>(
                "Compilation & Tests",
                1.0D),
            new JacocoCoveragePartGrader<>("Code Coverage", 4.0D, 0.85D),
            new Part1Grader("Part 1 - Cat program", 4.0D)
        );
    }

    @Override
    public boolean needsWorkspaceReset() {
        return false;
    }
}
