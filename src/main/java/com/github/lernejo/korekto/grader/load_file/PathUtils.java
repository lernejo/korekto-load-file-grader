package com.github.lernejo.korekto.grader.load_file;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static Path resourceToPath(String resourceName) {
        try {
            return Paths.get(WeatherComputationData.class.getClassLoader().getResource(resourceName).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
