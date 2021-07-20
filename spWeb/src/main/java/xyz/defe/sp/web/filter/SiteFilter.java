package xyz.defe.sp.web.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.Constant;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
public class SiteFilter implements Filter {
    @Autowired
    private Gson gson;
    @Autowired
    private Cache cache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        ResponseData responseData = new ResponseData();
        responseData.setStatus(500);
        String token = request.getParameter( "token");
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        if (!path.equals("/login") && !path.equals("/product/list")) {
            if (Strings.isNullOrEmpty(token)) {
                responseData.setError("token is empty!");
                response.getWriter().write(gson.toJson(responseData));
                return;
            } else if (null == cache.get(Constant.TOKEN_PREFIX + token)) {
                responseData.setError("token not valid!");
                response.getWriter().write(gson.toJson(responseData));
                return;
            } else {
                chain.doFilter(req, res);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }
}
