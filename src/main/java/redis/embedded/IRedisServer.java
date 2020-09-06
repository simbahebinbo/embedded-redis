package redis.embedded;

import java.util.List;
import redis.embedded.exceptions.EmbeddedRedisException;

public interface IRedisServer {
  boolean isActive();

  void start() throws EmbeddedRedisException;

  void stop() throws EmbeddedRedisException;

  List<Integer> ports();
}
