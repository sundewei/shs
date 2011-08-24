package com.sap.shs;

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

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/12/11
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashCodeFilter implements Filter {

    private String loginJsp = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        loginJsp = filterConfig.getInitParameter("loginJsp");
        if (StringUtils.isEmpty(loginJsp)) {
            throw new ServletException("'loginJsp' is not defined in the web.xml");
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws java.io.IOException, javax.servlet.ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        String employeeId = (String)session.getAttribute(ShsContext.EMPLOYEE_ID);
        /*
        if (StringUtils.isEmpty(employeeId)) {
            Cookie empIdCookie = getCookie(req.getCookies(), ShsContext.EMPLOYEE_ID);
            if (empIdCookie != null) {
                employeeId = empIdCookie.getValue();
            }
        }
        */
        if (StringUtils.isEmpty(employeeId)) {
            res.sendRedirect(loginJsp);
            return;
        } else {
            Cookie hashCookie = new Cookie(ShsContext.EMPLOYEE_ID, employeeId);
            // Will get deleted when user closes the browser
            hashCookie.setMaxAge(-1);
            res.addCookie(hashCookie);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies == null || cookies.length == 0) {
            return null;
        } else {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public void destroy() {

    }

}
