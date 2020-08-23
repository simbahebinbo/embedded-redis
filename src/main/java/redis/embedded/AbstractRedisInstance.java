package redis.embedded;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import redis.embedded.exceptions.EmbeddedRedisException;

@Slf4j
abstract class AbstractRedisInstance implements Redis {

  protected List<String> args = Collections.emptyList();
  private volatile boolean active = false;
  private Process redisProcess;
  private final int port;

  private ExecutorService executor;

  protected AbstractRedisInstance(int port) {
    this.port = port;
  }

  public boolean isActive() {
    return active;
  }

  public synchronized void start() throws EmbeddedRedisException {
    if (active) {
      log.warn("This redis server instance is already running...");
      throw new EmbeddedRedisException("This redis server instance is already running...");
    }
    try {
      redisProcess = createRedisProcessBuilder().start();
      installExitHook();
      logStandardError();
      awaitRedisServerReady();
      active = true;
    } catch (IOException e) {
      log.warn("Failed to start Redis instance. exception: {}", e.getMessage(), e);
      throw new EmbeddedRedisException("Failed to start Redis instance", e);
    }
  }

  private void installExitHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "RedisInstanceCleaner"));
  }

  private void logStandardError() {
    final InputStream errorStream = redisProcess.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
    Runnable printReaderTask = new PrintReaderRunnable(reader);
    executor = Executors.newSingleThreadExecutor();
    executor.submit(printReaderTask);
  }

  private void awaitRedisServerReady() throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(redisProcess.getInputStream()));
    try {
      StringBuffer outputStringBuffer = new StringBuffer();
      String outputLine;
      do {
        outputLine = reader.readLine();
        if (outputLine == null) {
          log.warn(
              "Can't start redis server. Check logs for details. Redis process log: "
                  + outputStringBuffer.toString());
          // Something goes wrong. Stream is ended before server was activated.
          throw new RuntimeException(
              "Can't start redis server. Check logs for details. Redis process log: "
                  + outputStringBuffer.toString());
        } else {
          outputStringBuffer.append("\n");
          outputStringBuffer.append(outputLine);
        }
        log.debug(outputLine);
      } while (!outputLine.matches(redisReadyPattern()));
    } finally {
      IOUtils.closeQuietly(reader, null);
    }
  }

  protected abstract String redisReadyPattern();

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
      log.warn("Failed to stop redis instance. exception: {}", e.getMessage(), e);
      throw new EmbeddedRedisException("Failed to stop redis instance", e);
    }
  }

  public List<Integer> ports() {
    return Collections.singletonList(port);
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
