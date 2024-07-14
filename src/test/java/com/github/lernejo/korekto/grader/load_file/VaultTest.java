package com.github.lernejo.korekto.grader.load_file;

import com.github.lernejo.korekto.grader.load_file.vault.Vault;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VaultTest {

    @Test
    void store_then_load() {
        Path path = Paths.get("target/test.encrypted");
        Vault<Map<String, Double>> vault = new Vault<>() {
        };
        Map<String, Double> input = Map.of("toto", 24.1D, "titi", 0.0D);

        System.setProperty("korekto_vault_key", Vault.generateRandomKey());
        vault.reloadKey();

        vault.store(path, input);

        Map<String, Double> loaded = vault.load(path, Paths.get("ignored"));

        assertThat(loaded).containsExactlyInAnyOrderEntriesOf(input);
    }

    @Test
    void store_then_load_backup() {
        Path path = Paths.get("target/test.backup");
        Vault<Map<String, Double>> vault = new Vault<>() {
        };
        Map<String, Double> backup = Map.of("tutu", 12.4D);
        vault.storeBackup(path, backup);

        System.clearProperty("korekto_vault_key");
        vault.reloadKey();

        Map<String, Double> loaded = vault.load(Paths.get("ignored"), path);

        assertThat(loaded).containsExactlyInAnyOrderEntriesOf(backup);
    }
}
