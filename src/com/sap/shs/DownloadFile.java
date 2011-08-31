package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFileSystem;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 8/25/11
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadFile extends BaseServlet {
    // 4MB
    private static final int BUFSIZE = 1024 * 1024 * 4;

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();
        IFileSystem filesystem = null;
        try {
            filesystem = configurationManager.getFileSystem();
        } catch (Exception e) {
            IOException ioe = new IOException(e);
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        String hdfsFilename = req.getParameter("hdfsFilename");
        int length = 0;
        ServletOutputStream op = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(hdfsFilename);


        //  Set the response and go!
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        try {
            resp.setContentLength((int) filesystem.getSize(hdfsFilename));
        } catch (Exception e) {
            IOException ioe = new IOException(e);
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + hdfsFilename + "\"");

        //  Stream to the requester.
        byte[] buffer = new byte[BUFSIZE];
        // Read the file from HDFS
        DataInputStream in = null;
        try {
            in = new DataInputStream(filesystem.getInputStream(hdfsFilename));
        } catch (Exception e) {
            IOException ioe = new IOException(e);
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        while ((in != null) && ((length = in.read(buffer)) != -1)) {
            op.write(buffer, 0, length);
        }

        in.close();
        op.flush();
        op.close();
    }
}
