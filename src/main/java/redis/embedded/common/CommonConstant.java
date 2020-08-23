package redis.embedded.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

  /** 分隔符 */
  public static final String SEPARATOR_SEMICOLON = ";";

  public static final String SEPARATOR_QUESTION_MARK = "?";

  public static final String SEPARATOR_COLON = ":";

  public static final String SEPARATOR_COLON2 = "::";

  public static final String SEPARATOR_COMMA = ",";

  public static final String SEPARATOR_AMPERSAND = "&";

  public static final String SEPARATOR_EQUAL_SIGN = "=";

  /** 斜线 */
  public static final String SEPARATOR_VIRGULE = "/";

  public static final String SEPARATOR_BLANK = "";

  public static final String SEPARATOR_ASTERISK = "*";

  /** 竖线 */
  public static final String SEPARATOR_VERTICAL_LINE = "|";

  /** 下划线 */
  public static final String SEPARATOR_UNDERSCORE = "_";
  /** 连字符 中横线 */
  public static final String SEPARATOR_HYPHEN = "-";

  /** 默认redis的端口和地址 */
  public static final int DEFAULT_REDIS_PORT = 6379;

  public static final String DEFAULT_REDIS_HOST = "127.0.0.1";
}
