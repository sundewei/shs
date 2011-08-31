package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 8/26/11
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateFolder extends BaseServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();

        String hdfsFolder = request.getParameter("hdfsFolder");
        String folderName = request.getParameter("folderName");

        if (!hdfsFolder.endsWith("/")) {
            hdfsFolder = hdfsFolder + "/";
        }
        String fullFolderName = hdfsFolder + folderName;
        try {
            configurationManager.getFileSystem().mkdirs(fullFolderName);
        } catch (Exception e) {
            IOException ioe = new IOException(e);
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }
}
