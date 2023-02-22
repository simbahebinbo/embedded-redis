package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EphemeralPortProviderTest {

    @Test
    public void nextShouldGiveNextFreeEphemeralPort() {
        final int portCount = 20;
        final EphemeralPortProvider provider = new EphemeralPortProvider();

        final Set<Integer> ports = new HashSet<>();
        for (int i = 0; i < portCount; i++) {
            ports.add(provider.next());
        }

        log.info(ports.toString());
        Assertions.assertEquals(portCount, ports.size());
    }
}
