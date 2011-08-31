package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import org.apache.commons.lang.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/12/11
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginPassFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        ShsContext.LOGIN_JSP = filterConfig.getInitParameter("loginJsp");
        if (StringUtils.isEmpty(ShsContext.LOGIN_JSP)) {
            throw new ServletException("'loginJsp' is not defined in the web.xml for 'LoginPassFilter'!");
        }

        ShsContext.SECRET_KEY = filterConfig.getInitParameter("secretKey");
        if (StringUtils.isEmpty(ShsContext.SECRET_KEY)) {
            throw new ServletException("'secretKey' is not defined in the web.xml for 'LoginPassFilter'!");
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws java.io.IOException, javax.servlet.ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        LoginPass loginPass = (LoginPass)session.getAttribute(ShsContext.LOGIN_PASS);
        // When LoginPass is not in the session, try to build one from available information in request
        if (loginPass == null) {
            Map<String, String[]> paramMap = req.getParameterMap();
            Cookie[] cookies = req.getCookies();
            try {
                loginPass = ShsContext.getLoginPass(paramMap, cookies, null);
                req.getSession().setAttribute(ShsContext.LOGIN_PASS, loginPass);
            } catch (Exception e) {
                ServletException se = new ServletException(e);
                se.setStackTrace(e.getStackTrace());
                throw se;
            }

            if (!loginPass.isLoadFromCookie()) {
                Cookie[] neededCookies = ShsContext.getLoginCookies(loginPass);
                for (Cookie cookie: neededCookies) {
                    res.addCookie(cookie);
                }
            }
        }

        if (!loginPass.isValidPass()){
            res.sendRedirect(ShsContext.LOGIN_JSP);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }

}
