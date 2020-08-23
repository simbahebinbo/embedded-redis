package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.embedded.exceptions.RedisBuildingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class PredefinedPortProviderTest {

  @Test
  public void nextShouldGiveNextPortFromAssignedList() {
    Collection<Integer> ports = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    final PredefinedPortProvider provider = new PredefinedPortProvider(ports);

    final List<Integer> returnedPorts = new ArrayList<>();
    for (int i = 0; i < ports.size(); i++) {
      returnedPorts.add(provider.next());
    }

    Assertions.assertEquals(ports, returnedPorts);
  }

  @Test
  public void nextShouldThrowExceptionWhenRunOutsOfPorts() {
    Collection<Integer> ports = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    final PredefinedPortProvider provider = new PredefinedPortProvider(ports);

    for (int i = 0; i < ports.size(); i++) {
      provider.next();
    }

    Assertions.assertThrows(RedisBuildingException.class, provider::next);
  }
}
