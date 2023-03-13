package xyz.defe.sp.web.filter;

import com.google.common.base.Strings;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.web.Constant;

import java.io.IOException;
import java.util.List;

@WebFilter
public class SiteFilter implements Filter {
    @Value("${allowPath}")
    private List<String> allowPath;

    @Autowired
    private Cache cache;

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
        System.out.println("path="+path);
        if (!startWithAllowPath(path, allowPath)) {
            if (Strings.isNullOrEmpty(token)) {
                ExceptionUtil.warn("token is empty!");
            } else if (null == cache.get(Constant.TOKEN_PREFIX + token)) {
                ExceptionUtil.warn("token not valid!");
            } else {
                chain.doFilter(req, res);
            }
        } else {
            chain.doFilter(req, res);
        }
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
