package redis.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.util.Architecture;
import redis.embedded.util.JarUtil;
import redis.embedded.util.OS;
import redis.embedded.util.OSArchitecture;

@Slf4j
abstract class RedisExecProvider {

  protected final Map<OSArchitecture, String> executables = Maps.newHashMap();

  protected abstract void initExecutables();

  public RedisExecProvider override(OS os, String executable) {
    Preconditions.checkNotNull(executable);
    for (Architecture arch : Architecture.values()) {
      override(os, arch, executable);
    }
    return this;
  }

  public RedisExecProvider override(OS os, Architecture arch, String executable) {
    Preconditions.checkNotNull(executable);
    executables.put(new OSArchitecture(os, arch), executable);
    return this;
  }

  public File get() throws IOException {
    OSArchitecture osArch = OSArchitecture.detect();

    if (!executables.containsKey(osArch)) {
      log.warn("No Redis executable found for " + osArch);
      throw new IllegalArgumentException("No Redis executable found for " + osArch);
    }

    String executablePath = executables.get(osArch);

    return fileExists(executablePath)
        ? new File(executablePath)
        : JarUtil.extractExecutableFromJar(executablePath);
  }

  private boolean fileExists(String executablePath) {
    return new File(executablePath).exists();
  }
}
