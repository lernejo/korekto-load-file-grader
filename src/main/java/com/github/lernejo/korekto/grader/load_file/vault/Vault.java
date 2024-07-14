package com.github.lernejo.korekto.grader.load_file.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.lernejo.korekto.toolkit.misc.SubjectForToolkitInclusion;
import com.github.lernejo.korekto.toolkit.misc.ThrowingFunction;
import org.eclipse.jgit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@SubjectForToolkitInclusion
public abstract class Vault<T> implements Comparable<Vault<T>> {

    private static final Logger logger = LoggerFactory.getLogger(Vault.class);
    private static String encodedKey = System.getProperty("korekto_vault_key");

    private static final String ALGORITHM = "AES";
    private static final ObjectMapper OM = new ObjectMapper();

    private final Type _type;

    /**
     * @see com.fasterxml.jackson.core.type.TypeReference
     */
    public Vault() {
        Type superClass = getClass().getGenericSuperclass();
        _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public void reloadKey() {
        encodedKey = System.getProperty("korekto_vault_key");
    }

    private static <T> T safeJson(ThrowingFunction<ObjectMapper, T> f) {
        return ThrowingFunction.sneaky(f).apply(OM);
    }

    private static Optional<SecretKey> extractSecretKeyFromProperties() {
        if (encodedKey == null) {
            return Optional.empty();
        }
        byte[] key = Base64.decode(encodedKey);
        return Optional.of(new SecretKeySpec(key, "AES"));
    }

    public static boolean isClear() {
        return encodedKey == null;
    }

    private static byte[] readEncryptedSecret(Path path) {
        try {
            return Base64.decode(Files.readString(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void write(Path path, String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String decrypt(SecretKey key, byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainText = cipher.doFinal(encrypted);
            return new String(plainText);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] encrypt(SecretKey key, String text) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text.getBytes());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey key = keyGenerator.generateKey();
            return java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public T load(Path path, Path backupPath) {
        Optional<SecretKey> key = extractSecretKeyFromProperties();
        ObjectReader reader = OM.readerFor(OM.constructType(_type));
        if (key.isEmpty()) {
            logger.info("Loading Vault from clear file");
            return safeJson(om -> reader.readValue(backupPath.toFile()));
        }
        byte[] encrypted = readEncryptedSecret(path);

        String rawResults = decrypt(key.get(), encrypted);

        logger.info("Loading Vault from encrypted file");
        return safeJson(om -> reader.readValue(rawResults));
    }

    public void store(Path path, T secret) {
        String rawResults = safeJson(om -> om.writeValueAsString(secret));
        Optional<SecretKey> key = extractSecretKeyFromProperties();
        if (key.isEmpty()) {
            throw new IllegalStateException("Key is required to encrypt secret");
        }

        byte[] encrypted = encrypt(key.get(), rawResults);

        write(path, Base64.encodeBytes(encrypted));
    }

    public void storeBackup(Path path, T input) {
        String rawResults = safeJson(om -> om.writeValueAsString(input));
        write(path, rawResults);
    }

    /**
     * @see com.fasterxml.jackson.core.type.TypeReference
     */
    @Override
    public int compareTo(Vault<T> o) {
        return 0;
    }
}
