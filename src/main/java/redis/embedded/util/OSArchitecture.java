package redis.embedded.util;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
public class OSArchitecture {

  public static final OSArchitecture UNIX_X86 = new OSArchitecture(OS.UNIX, Architecture.X86);
  public static final OSArchitecture UNIX_AMD64 = new OSArchitecture(OS.UNIX, Architecture.AMD64);

  public static final OSArchitecture MAC_OSX_X86 = new OSArchitecture(OS.MAC_OSX, Architecture.X86);
  public static final OSArchitecture MAC_OSX_AMD64 =
      new OSArchitecture(OS.MAC_OSX, Architecture.AMD64);

  private final OS os;
  private final Architecture arch;

  public static OSArchitecture detect() {
    OS os = OSDetector.getOS();
    Architecture arch = OSDetector.getArchitecture();
    return new OSArchitecture(os, arch);
  }

  public OSArchitecture(OS os, Architecture arch) {
    Preconditions.checkNotNull(os);
    Preconditions.checkNotNull(arch);

    this.os = os;
    this.arch = arch;
  }
}
