package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.PortProvider;
import redis.embedded.exceptions.RedisBuildingException;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

//实现定义一组端口。使用时按顺序获取一个端口
@Slf4j
public class PredefinedPortProvider implements PortProvider {

    private final Iterator<Integer> current;

    public PredefinedPortProvider(Set<Integer> ports) {
        Set<Integer> portSet = new LinkedHashSet<>(ports);
        this.current = portSet.iterator();
    }

    @Override
    public synchronized Integer next() {
        if (!current.hasNext()) {
            String msg = "Run out of Redis ports!";
            log.warn(msg);
            throw new RedisBuildingException(msg);
        }
        return current.next();
    }
}
