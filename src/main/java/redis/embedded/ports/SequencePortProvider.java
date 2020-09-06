package redis.embedded.ports;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.PortProvider;

@Slf4j
@NoArgsConstructor
public class SequencePortProvider implements PortProvider {
  private final AtomicInteger currentPort = new AtomicInteger(26379);

  public SequencePortProvider(int currentPort) {
    this.currentPort.set(currentPort);
  }

  public void setCurrentPort(int port) {
    this.currentPort.set(port);
  }

  @Override
  public int next() {
    return this.currentPort.getAndIncrement();
  }
}
