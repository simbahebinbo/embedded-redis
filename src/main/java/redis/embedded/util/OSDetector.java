package redis.embedded.util;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.common.CommonConstant;
import redis.embedded.exceptions.OsDetectionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class OSDetector {

    public static OS getOS() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains(CommonConstant.OS_NIX)
                || osName.contains(CommonConstant.OS_NUX)
                || osName.contains(CommonConstant.OS_AIX)) {
            return OS.UNIX;
        } else if (CommonConstant.OS_MAC_OSX.equalsIgnoreCase(osName)) {
            return OS.MAC_OSX;
        } else {
            String msg = "Unrecognized OS: " + osName;
            log.warn(msg);
            throw new OsDetectionException(msg);
        }
    }

    public static Architecture getArchitecture() {
        OS os = getOS();
        switch (os) {
            case UNIX, MAC_OSX -> {
                return detectionArchitecture();
            }
            default -> {
                String msg = "Unrecognized OS: " + os;
                log.warn(msg);
                throw new OsDetectionException(msg);
            }
        }
    }

    private static Architecture detectionArchitecture() {
        try {
            Process proc = Runtime.getRuntime().exec("uname -m");
            try (BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String machine = input.readLine();
                log.debug("machine: " + machine);
                switch (machine) {
                    case CommonConstant.ARCHITECTURE_AARCH64, CommonConstant.ARCHITECTURE_ARM64 -> {
                        return Architecture.ARM64;
                    }
                    case CommonConstant.ARCHITECTURE_X86_64 -> {
                        return Architecture.AMD64;
                    }
                    default -> {
                        String msg = "unsupported architecture: " + machine;
                        log.warn(msg);
                        throw new OsDetectionException(msg);
                    }
                }
            }
        } catch (Exception e) {
            String msg = "get unix architecture fail";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            if (e instanceof OsDetectionException) {
                throw (OsDetectionException) e;
            }
            throw new OsDetectionException(msg, e);
        }
    }
}
