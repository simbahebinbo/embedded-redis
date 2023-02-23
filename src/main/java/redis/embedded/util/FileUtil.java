package redis.embedded.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static File getFile(String path) throws IOException {
        return fileExists(path) ?
                new File(path) :
                JarUtil.extractFileFromJar(path);
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }
}

