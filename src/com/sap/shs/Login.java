package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import org.apache.commons.lang.StringUtils;

import javax.security.auth.login.Configuration;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/12/11
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Login extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.equals("")) {
            Cookie usernameCookie = getCookie(request.getCookies(), ShsContext.EMPLOYEE_ID);
            if (usernameCookie != null) {
                username = usernameCookie.getValue();
            }
        }
        if (username == null || !username.matches(ShsContext.EMPLOYEE_ID_REGEX)) {
            response.sendRedirect(request.getRequestURI());
            return;
        }
        String password = request.getParameter("password");
        ConfigurationManager configurationManager = new ConfigurationManager(username, password);

        String dest = request.getParameter("dest");
        request.getSession().setAttribute(ShsContext.EMPLOYEE_ID, username);
        request.getSession().setAttribute(ShsContext.CONFIGURATION_MANAGER, configurationManager);
        // Creating the local personal folder and set it in session
        try {
            ShsContext.createPersonalHdfsFolder(configurationManager);
        } catch (Exception ee) {
            IOException iee = new IOException(ee);
            iee.setStackTrace(ee.getStackTrace());
            throw iee;
        }
        response.sendRedirect(dest);
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
}
