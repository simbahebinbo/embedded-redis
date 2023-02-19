package redis.embedded.ports;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.PortProvider;
import redis.embedded.common.CommonConstant;

import java.util.concurrent.atomic.AtomicInteger;

//按顺序递增1 获取端口
@Slf4j
@NoArgsConstructor
public class SequencePortProvider implements PortProvider {
    private final AtomicInteger currentPort =
            new AtomicInteger(CommonConstant.DEFAULT_REDIS_SENTINEL_PORT);

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
