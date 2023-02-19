package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.PortProvider;
import redis.embedded.exceptions.RedisBuildingException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//实现定义一组端口。使用时按顺序获取一个端口
@Slf4j
public class PredefinedPortProvider implements PortProvider {

    private final Iterator<Integer> current;

    public PredefinedPortProvider(Collection<Integer> ports) {
        List<Integer> portList = new LinkedList<>(ports);
        this.current = portList.iterator();
    }

    @Override
    public synchronized int next() {
        if (!current.hasNext()) {
            log.warn("Run out of Redis ports!");
            throw new RedisBuildingException("Run out of Redis ports!");
        }
        return current.next();
    }
}
