package redis.embedded.util;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
public class OSArchitecture {
    public static final OSArchitecture UNIX_AMD64 = new OSArchitecture(OS.UNIX, Architecture.AMD64);

    public static final OSArchitecture UNIX_ARM64 = new OSArchitecture(OS.UNIX, Architecture.ARM64);


    public static final OSArchitecture MAC_OSX_AMD64 = new OSArchitecture(OS.MAC_OSX, Architecture.AMD64);

    public static final OSArchitecture MAC_OSX_ARM64 = new OSArchitecture(OS.MAC_OSX, Architecture.ARM64);


    private final OS os;
    private final Architecture arch;

    public OSArchitecture(OS os, Architecture arch) {
        Preconditions.checkNotNull(os);
        Preconditions.checkNotNull(arch);

        this.os = os;
        this.arch = arch;
    }

    public static OSArchitecture detect() {
        OS os = OSDetector.getOS();
        Architecture arch = OSDetector.getArchitecture();
        log.debug("os: {} arch: {}", os, arch);
        return new OSArchitecture(os, arch);
    }

    public OS os() {
        return os;
    }

    public Architecture arch() {
        return arch;
    }
}
