package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFileSystem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/21/11
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteHdfsFile extends BaseServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // data: {"hdfsFolder": hdfsPersonFolder, "filenameToDelete": filename, "action": "delete"},
        String filenameToDelete = request.getParameter("filenameToDelete");
        String action = request.getParameter("action");
        HttpSession session = request.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        String resText = "false";
        try {
            if ("delete".equalsIgnoreCase(action)) {
                IFileSystem filesystem = configurationManager.getFileSystem();
                if (filesystem.exists(filenameToDelete)) {
                    resText = String.valueOf(filesystem.deleteFile(filenameToDelete));
                }
            }
        } catch (Exception ee) {
            IOException iee = new IOException(ee);
            iee.setStackTrace(ee.getStackTrace());
            throw iee;
        }
        out.write(resText);
        out.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
