package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class RedisClient implements IRedisClient {
    private static final String REDIS_CLIENT_READY_PATTERN = "All 16384 slots covered";

    protected List<String> args;
    private Process redisProcess;

    private ExecutorService executor;

    private volatile boolean active = false;


    RedisClient(List<String> args) {
        this.args = new ArrayList<>(args);
        log.debug("args: " + this.args);
    }

    public boolean isActive() {
        return active;
    }

    public synchronized void run() throws EmbeddedRedisException {
        if (active) {
            log.warn("This redis client instance is already running...");
            throw new EmbeddedRedisException("This redis client instance is already running...");
        }
        try {
            redisProcess = createRedisProcessBuilder().start();
            installExitHook();
            logStandardError();
            awaitRedisClientReady();
            active = true;
        } catch (IOException e) {
            log.warn("Failed to start Redis Client instance. exception: {}", e.getMessage(), e);
            throw new EmbeddedRedisException("Failed to start Redis Client instance", e);
        }
    }

    private void installExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "RedisClientInstanceCleaner"));
    }

    private void logStandardError() {
        final InputStream errorStream = redisProcess.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        Runnable printReaderTask = new PrintReaderRunnable(reader);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(printReaderTask);
    }

    private void awaitRedisClientReady() throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(redisProcess.getInputStream()));
        try {
            StringBuilder outputStringBuffer = new StringBuilder();
            String outputLine;
            do {
                outputLine = reader.readLine();
                if (outputLine == null) {
                    log.warn(
                            "Can't start redis client. Check logs for details. Redis process log: "
                                    + outputStringBuffer);
                    // Something goes wrong. Stream is ended before server was activated.
                    throw new RuntimeException(
                            "Can't start redis client. Check logs for details. Redis process log: "
                                    + outputStringBuffer);
                } else {
                    outputStringBuffer.append("\n");
                    outputStringBuffer.append(outputLine);
                }
                log.debug(outputLine);
            } while (!outputLine.contains(redisReadyPattern()));
        } finally {
            IOUtils.closeQuietly(reader, null);
        }
    }

    protected String redisReadyPattern() {
        return REDIS_CLIENT_READY_PATTERN;
    }

    private ProcessBuilder createRedisProcessBuilder() {
        File executable = new File(args.get(0));
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(executable.getParentFile());
        return pb;
    }

    public synchronized void stop() throws EmbeddedRedisException {
        if (active) {
            if ((executor != null) && (!executor.isShutdown())) {
                executor.shutdown();
            }
            redisProcess.destroy();
            tryWaitFor();
            active = false;
        }
    }

    private void tryWaitFor() {
        try {
            redisProcess.waitFor();
        } catch (InterruptedException e) {
            log.warn("Failed to stop redis client instance. exception: {}", e.getMessage(), e);
            throw new EmbeddedRedisException("Failed to stop redis client instance", e);
        }
    }


    private static class PrintReaderRunnable implements Runnable {
        private final BufferedReader reader;

        private PrintReaderRunnable(BufferedReader reader) {
            this.reader = reader;
        }

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
