package im.zhaojun.zfile.module.config.utils;

import im.zhaojun.zfile.core.util.RequestHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaojun
 */
public class SpringMvcUtils {

    public static String getExtractPathWithinPattern() {
        // 通过 RequestHolder 获取当前的 HttpServletRequest 对象
        HttpServletRequest httpServletRequest = RequestHolder.getRequest();
        // 获取请求中的路径
        String path = (String) httpServletRequest.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 获取请求路径的最佳匹配模式
        String bestMatchPattern = (String) httpServletRequest.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 创建 AntPathMatcher 对象
        AntPathMatcher apm = new AntPathMatcher();
        // 使用 AntPathMatcher 提取在模式中的路径
        return apm.extractPathWithinPattern(bestMatchPattern, path);
    }

}
