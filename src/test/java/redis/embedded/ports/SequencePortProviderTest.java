package redis.embedded.ports;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class SequencePortProviderTest {

    @Test
    public void nextShouldIncrementPorts() {
        final int startPort = 10;
        final int portCount = 101;
        final SequencePortProvider provider = new SequencePortProvider(startPort);

        int max = 0;
        for (int i = 0; i < portCount; i++) {
            int port = provider.next();
            if (port > max) {
                max = port;
            }
        }

        Assertions.assertEquals(portCount + startPort - 1, max);
    }
}
