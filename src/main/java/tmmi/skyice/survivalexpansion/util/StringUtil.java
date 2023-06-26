package tmmi.skyice.survivalexpansion.util;

public class StringUtil {
    public static String underlineToCamel(String str) {
        StringBuilder sb = new StringBuilder();
        boolean needUpper = false;
        //遍历字符串,如果遇到下划线,则下一个字母大写,如果当前是大写,true,如果当前是小写,false
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                needUpper = true;
            } else if (needUpper) {
                sb.append(Character.toUpperCase(c));
                needUpper = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String camelToUnderline(String str) {
        StringBuilder sb = new StringBuilder();
        //遍历字符串,如果遇到大写,则在前面加上下划线
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
}
