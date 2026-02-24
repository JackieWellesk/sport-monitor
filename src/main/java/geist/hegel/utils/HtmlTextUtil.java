package geist.hegel.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTextUtil {

    // 去掉所有 HTML 标签（含 <p>、</br> 等）
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");

    /** 去除HTML标签并截取前 n 个字符（按 Java char 截取） */
    public static String stripHtmlAndTake(String html, int n) {
        if (html == null) return "";
        String text = TAG_PATTERN.matcher(html).replaceAll("");
        text = text.replace("&nbsp;", " ").trim(); // 可选：处理常见实体
        if (text.length() <= n) return text;
        return text.substring(0, n);
    }

    // 匹配 <img ... src="xxx" ...>
    private static final Pattern IMG_SRC_PATTERN = Pattern.compile(
            "<img[^>]*?src\\s*=\\s*['\"]?([^'\">\\s]+)['\"]?[^>]*>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 提取第一个 img 标签的 src
     */
    public static String extractFirstImgSrc(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }

        Matcher matcher = IMG_SRC_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}