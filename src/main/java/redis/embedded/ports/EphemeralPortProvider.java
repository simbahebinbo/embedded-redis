package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.PortProvider;
import redis.embedded.exceptions.RedisBuildingException;

import java.io.IOException;
import java.net.ServerSocket;

//获取一个临时端口
@Slf4j
public class EphemeralPortProvider implements PortProvider {
    @Override
    public int next() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            //选择一个当前可用的端口
            socket.setReuseAddress(false);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            // should not ever happen
            String msg = "Could not provide ephemeral port";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new RedisBuildingException(msg, e);
        }
    }
}
