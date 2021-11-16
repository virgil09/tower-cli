/*
 * Copyright (c) 2021, Seqera Labs.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli;

import io.seqera.tower.cli.utils.ErrorReporting;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static io.seqera.tower.cli.Tower.buildCommandLine;

@ExtendWith(MockServerExtension.class)
public abstract class BaseCmdTest {

    @TempDir
    Path tempDir;

    protected byte[] loadResource(String name) {
        return loadResource(name, "json");
    }

    protected byte[] loadResource(String name, String ext) {
        try (InputStream stream = this.getClass().getResourceAsStream("/runcmd/" + name + "." + ext)) {
            return stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    protected String tempFile(String content, String prefix, String suffix) throws IOException {
        Path file = Files.createTempFile(tempDir, prefix, suffix);
        Files.writeString(file, content);
        return file.toAbsolutePath().toString();
    }

    protected String url(MockServerClient mock) {
        return String.format("http://localhost:%d", mock.getPort());
    }

    protected String token() {
        return "fake_auth_token";
    }

    protected ExecOut exec(MockServerClient mock, String... args) {

        // Run binary command line
        if (System.getenv().containsKey("TOWER_CLI")) {
            return execBinary(mock, System.getenv("TOWER_CLI"), args);
        }

        // Run java version
        StringWriter stdOut = new StringWriter();
        StringWriter stdErr = new StringWriter();
        CommandLine cmd = buildCommandLine();
        cmd.setOut(new PrintWriter(stdOut));
        cmd.setErr(new PrintWriter(stdErr));

        int exitCode = cmd.execute(ArrayUtils.insert(0, args, String.format("--url=%s", url(mock)), String.format("--access-token=%s", token())));

        return new ExecOut()
                .app(cmd.getCommand())
                .stdOut(StringUtils.chop(stdOut.toString()))
                .stdErr(StringUtils.chop(stdErr.toString()))
                .exitCode(exitCode);
    }

    private ExecOut execBinary(MockServerClient mock, String command, String... args) {

        try {
            StringWriter stdOut = new StringWriter();
            StringWriter stdErr = new StringWriter();

            PrintWriter outWriter = new PrintWriter(stdOut);
            PrintWriter errWriter = new PrintWriter(stdErr);

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(ArrayUtils.insert(0, args, command, String.format("--url=%s", url(mock)), String.format("--access-token=%s", token())));
            Process process = builder.start();

            StreamGobbler consumeOut = new StreamGobbler(process.getInputStream(), outWriter::println);
            StreamGobbler consumeErr = new StreamGobbler(process.getErrorStream(), errWriter::println);
            Future<?> taskOut = Executors.newSingleThreadExecutor().submit(consumeOut);
            Future<?> taskErr = Executors.newSingleThreadExecutor().submit(consumeErr);
            int exitCode = process.waitFor();
            try {
                taskErr.get(5, TimeUnit.SECONDS);
                taskOut.get(5, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException e) {
                // Ignore this
            }

            if (exitCode == 255) {
                exitCode = -1;
            }

            outWriter.close();
            errWriter.close();

            return new ExecOut()
                    .stdOut(StringUtils.chop(stdOut.toString()))
                    .stdErr(StringUtils.chop(stdErr.toString()))
                    .exitCode(exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ExecOut()
                    .stdOut("")
                    .stdErr(e.getMessage())
                    .exitCode(-1);
        }
    }

    protected String errorMessage(Tower app, Exception e) {
        StringWriter out = new StringWriter();
        ErrorReporting.errorMessage(app, new PrintWriter(out), e);
        return StringUtils.chop(out.toString());
    }

    public static class ExecOut {
        public Tower app;
        public String stdOut;
        public String stdErr;
        public int exitCode;

        public ExecOut stdOut(String value) {
            this.stdOut = value;
            return this;
        }

        public ExecOut stdErr(String value) {
            this.stdErr = value;
            return this;
        }

        public ExecOut exitCode(int value) {
            this.exitCode = value;
            return this;
        }

        public ExecOut app(Tower app) {
            this.app = app;
            return this;
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }

}
