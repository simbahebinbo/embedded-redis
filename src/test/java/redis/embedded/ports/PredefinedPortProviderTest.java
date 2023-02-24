package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.embedded.exceptions.RedisBuildingException;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class PredefinedPortProviderTest {

    @Test
    public void nextShouldGiveNextPortFromAssignedList() {
        Set<Integer> ports = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final PredefinedPortProvider provider = new PredefinedPortProvider(ports);

        final Set<Integer> returnedPorts = new LinkedHashSet<>();
        for (int i = 0; i < ports.size(); i++) {
            returnedPorts.add(provider.next());
        }

        Assertions.assertEquals(ports, returnedPorts);
    }

    @Test
    public void nextShouldThrowExceptionWhenRunOutsOfPorts() {
        Set<Integer> ports = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final PredefinedPortProvider provider = new PredefinedPortProvider(ports);

        for (int i = 0; i < ports.size(); i++) {
            provider.next();
        }

        Assertions.assertThrows(RedisBuildingException.class, provider::next);
    }
}
