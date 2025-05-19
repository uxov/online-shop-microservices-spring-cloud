package xyz.defe.sp.web.filter;

import com.google.common.base.Strings;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.web.Constant;

import java.io.IOException;
import java.util.List;

@WebFilter
public class SiteFilter implements Filter {
    @Value("${allowPath}")
    private List<String> allowPath;

    @Autowired
    private RedissonClient redisson;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    // catch exceptions by xyz.defe.sp.common.exception.FilterExceptionHandler
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        log.debug(path);
        if (!startWithAllowPath(path, allowPath)) {
            if (Strings.isNullOrEmpty(token)) {
                ExceptionUtil.warn("token is empty!");
            }
            if (!redisson.getBucket(Constant.TOKEN_PREFIX + token).isExists()) {
                ExceptionUtil.warn("token not valid!");
            }
        }
        chain.doFilter(req, res);
    }

    private boolean startWithAllowPath(String path, List<String> allowPath) {
        for (String s : allowPath) {
            if (path.startsWith(s)) {return true;}
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
