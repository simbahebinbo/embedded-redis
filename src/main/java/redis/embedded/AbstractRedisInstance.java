package redis.embedded;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@NoArgsConstructor
abstract class AbstractRedisInstance {
    @Getter
    private List<String> arguments;
    private Process redisProcess;

    @Getter
    private volatile boolean active = false;
    private ExecutorService executor;

    AbstractRedisInstance(List<String> args) {
        arguments = new LinkedList<>(args);
        log.debug("args: " + arguments);
    }

    public void doStart() throws EmbeddedRedisException {
        if (active) {
            String msg = "This redis instance is already running...";
            log.warn(msg);
            throw new EmbeddedRedisException(msg);
        }
        try {
            redisProcess = createRedisProcessBuilder().start();
            installExitHook();
            logStandardError();
            awaitRedisInstanceReady();
            active = true;
        } catch (IOException e) {
            String msg = "Failed to start Redis instance";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new EmbeddedRedisException(msg, e);
        }
    }

    public void installExitHook() {
        String name = "RedisInstanceCleaner";
        Runtime.getRuntime().addShutdownHook(new Thread(this::doStop, name));
    }

    public void logStandardError() {
        final InputStream errorStream = redisProcess.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        Runnable printReaderTask = new PrintReaderRunnable(reader);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(printReaderTask);
    }


    public void awaitRedisInstanceReady() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(redisProcess.getInputStream()));
        try {
            StringBuilder outputStringBuffer = new StringBuilder();
            String outputLine;
            do {
                outputLine = reader.readLine();
                if (outputLine == null) {
                    String msg = "Can't start redis instance. Check logs for details. Redis process log: "
                            + outputStringBuffer;
                    log.warn(msg);
                    // Something goes wrong. Stream is ended before server was activated.
                    throw new RuntimeException(msg);
                } else {
                    outputStringBuffer.append("\n");
                    outputStringBuffer.append(outputLine);
                }
                log.debug(outputLine);
            } while (!outputLine.matches(redisInstanceReadyPattern()));
        } finally {
            IOUtils.closeQuietly(reader, null);
        }
    }

    protected abstract String redisInstanceReadyPattern();


    public ProcessBuilder createRedisProcessBuilder() {
        File executable = new File(arguments.get(0));
        ProcessBuilder pb = new ProcessBuilder(arguments);
        pb.directory(executable.getParentFile());
        return pb;
    }

    public synchronized void doStop() throws EmbeddedRedisException {
        if (active) {
            if ((executor != null) && (!executor.isShutdown())) {
                executor.shutdown();
            }
            redisProcess.destroy();
            tryWaitFor();
            active = false;
        }
    }

    public void tryWaitFor() {
        try {
            redisProcess.waitFor();
        } catch (InterruptedException e) {
            String msg = "Failed to stop redis instance";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new EmbeddedRedisException(msg, e);
        }
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class PrintReaderRunnable implements Runnable {
        private final BufferedReader reader;

        @Override
        public void run() {
            try {
                readLines();
            } finally {
                IOUtils.closeQuietly(reader, null);
            }
        }

        public void readLines() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.warn("Failed to readLines. exception: {}", e.getMessage(), e);
            }
        }
    }
}
