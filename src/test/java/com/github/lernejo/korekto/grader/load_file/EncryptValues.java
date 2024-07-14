package com.github.lernejo.korekto.grader.load_file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EncryptValues {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(args[0]);
        List<WeatherComputationData> content = new ObjectMapper().readValue(path.toFile(), new TypeReference<>() {
        });
        WeatherComputationData.Loader.vault.store(Paths.get("src/main/resources/vault/results.encrypted"), content);
    }
}
