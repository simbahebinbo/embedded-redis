package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;
import java.util.stream.IntStream;

@Slf4j
public class SequencePortProviderTest {

    @Test
    public void nextShouldIncrementPorts() {
        final int startPort = 10;
        final int portCount = 101;
        final SequencePortProvider provider = new SequencePortProvider(startPort);

        TreeSet<Integer> ports = new TreeSet<>();
        IntStream.range(0, portCount).forEach(i -> {
            int port = provider.next();
            ports.add(port);
        });
        int max = ports.last();

        Assertions.assertEquals(portCount + startPort - 1, max);
    }
}
