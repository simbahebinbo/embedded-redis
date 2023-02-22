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
            log.warn("Unrecognized OS: " + osName);
            throw new OsDetectionException("Unrecognized OS: " + osName);
        }
    }

    public static Architecture getArchitecture() {
        OS os = getOS();
        switch (os) {
            case UNIX:
                return getUnixArchitecture();
            case MAC_OSX:
                return getMacOSXArchitecture();
            default:
                log.warn("Unrecognized OS: " + os);
                throw new OsDetectionException("Unrecognized OS: " + os);
        }
    }

    private static Architecture getUnixArchitecture() {
        BufferedReader input = null;
        try {
            String line;
            Process proc = Runtime.getRuntime().exec("uname -m");
            input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.length() > 0) {
                    if (line.contains("64")) {
                        return Architecture.AMD64;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("get unix architecture fail. exception: {}", e.getMessage(), e);
            throw new OsDetectionException(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                log.warn("get unix architecture fail. exception: {}", e.getMessage(), e);
            }
        }

        return Architecture.X86;
    }

    private static Architecture getMacOSXArchitecture() {
        BufferedReader input = null;
        try {
            String line;
            Process proc = Runtime.getRuntime().exec("sysctl hw");
            input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.length() > 0) {
                    if ((line.contains("cpu64bit_capable")) && (line.trim().endsWith("1"))) {
                        return Architecture.AMD64;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("get mac os x architecture fail. exception: {}", e.getMessage(), e);
            throw new OsDetectionException(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                log.warn("get mac os x architecture fail. exception: {}", e.getMessage(), e);
            }
        }

        return Architecture.X86;
    }
}
