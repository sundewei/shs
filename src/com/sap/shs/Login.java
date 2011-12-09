package com.sap.shs;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/12/11
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Login extends BaseServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        Map<String, String[]> paramMap = request.getParameterMap();
        Cookie[] cookies = request.getCookies();
        LoginPass loginPass = null;
        try {
            loginPass = ShsContext.getLoginPass(paramMap, cookies, null);
        } catch (Exception e) {
            ServletException se = new ServletException(e);
            se.setStackTrace(e.getStackTrace());
            throw se;
        }
        if (!loginPass.isValidPass()) {
            response.sendRedirect(ShsContext.LOGIN_JSP);
        } else {
            String dest = request.getParameter("dest");
            session.setAttribute(ShsContext.LOGIN_PASS, loginPass);

            Cookie[] neededCookies = ShsContext.getLoginCookies(loginPass);
            for (Cookie cookie : neededCookies) {
                response.addCookie(cookie);
            }
            // Creating the local personal folder and set it in session
            try {
                ShsContext.createPersonalHdfsFolder(loginPass.getConfigurationManager());
            } catch (Exception ee) {
                IOException iee = new IOException(ee);
                iee.setStackTrace(ee.getStackTrace());
                throw iee;
            }
            response.sendRedirect(dest);
        }
    }
}
