package com.github.lernejo.korekto.grader.load_file;

import com.github.lernejo.korekto.grader.load_file.vault.Vault;

import java.util.List;

import static com.github.lernejo.korekto.grader.load_file.PathUtils.resourceToPath;

public record WeatherComputationData(String start, String end, String metric, String selector, String agg,
                                     String result) {

    public String asDescription() {
        String period = Vault.isClear() ? start + '/' + end : "(period undisclosed)";
        return period + '/' + metric + '/' + selector + '/' + agg;
    }

    public static final class Loader {
        public static final Vault<List<WeatherComputationData>> vault = new Vault<>() {
        };

        public static List<WeatherComputationData> load() {
            return vault.load(resourceToPath("vault/results.encrypted"), resourceToPath("vault/results.clear"));
        }
    }
}
