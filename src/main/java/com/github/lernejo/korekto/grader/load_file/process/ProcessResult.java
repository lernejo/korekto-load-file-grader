package com.github.lernejo.korekto.grader.load_file.process;

public record ProcessResult(int exitCode, String stdout, String stderr, Exception cause) {
    public static ProcessResult error(Exception cause) {
        return new ProcessResult(-1, "", "", cause);
    }

    public static ProcessResult error(int exitCode, String stdout, String stderr) {
        return new ProcessResult(exitCode, stdout, stderr, null);
    }

    public static ProcessResult success(String stdout, String stderr) {
        return new ProcessResult(0, stdout, stderr, null);
    }

    public String getOutput() {
        return stderr + stdout;
    }
}
