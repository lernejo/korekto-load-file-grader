package com.github.lernejo.korekto.grader.load_file;

import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;
import com.github.lernejo.korekto.toolkit.partgrader.MavenContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.lernejo.korekto.grader.load_file.PathUtils.resourceToPath;

public class LaunchingContext extends GradingContext implements MavenContext {
    private final List<String> dictionary;
    private final List<WeatherComputationData> dataset = WeatherComputationData.Loader.load();
    private boolean compilationFailed;
    private boolean testFailed;
    public static final Path DATA_FILE_PATH = resourceToPath("open-meteo-52.55N13.41E38m.csv").toAbsolutePath();

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

    public Set<WeatherComputationData> getDataset(int size) {
        Set<WeatherComputationData> r = new HashSet<>();
        while (r.size() != size) {
            int index = getRandomSource().nextInt(dataset.size());
            r.add(dataset.get(index));
        }
        return r;
    }
}
