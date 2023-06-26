package tmmi.skyice.survivalexpansion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private LogUtil() {
    }
    public static final Logger LOGGER = LoggerFactory.getLogger("SurvivalExpansion");

    public  static  Logger getLogger() {
        return LOGGER;
    }
    public static void info(String s,Object... o) {
        LOGGER.info(s,o);
    }
    public static void warn(String s,Object... o) {
        LOGGER.warn(s,o);
    }
    public static void error(String s,Object... o) {
        LOGGER.error(s,o);
    }
    public static void debug(String s,Object... o) {
        LOGGER.debug(s,o);
    }

}
