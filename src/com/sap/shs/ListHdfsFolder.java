package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFile;
import com.sap.hadoop.conf.IFileSystem;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/21/11
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListHdfsFolder extends BaseServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hdfsFolder = request.getParameter("hdfsFolder");
        HttpSession session = request.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();
        PrintWriter out = response.getWriter();
        Map<String, String> jsonKeyValMap = new LinkedHashMap<String, String>();
        try {
            IFileSystem filesystem = configurationManager.getFileSystem();
            IFile[] files = filesystem.listFiles(hdfsFolder);
            response.setContentType("application/json");
            int count = 0;
            for (IFile file : files) {
                jsonKeyValMap.put("fileName" + count, file.getFilename());
                jsonKeyValMap.put("fileOwner" + count, file.getOwner());
                jsonKeyValMap.put("fileLen" + count, FileUtils.byteCountToDisplaySize(file.getLen()));
                jsonKeyValMap.put("fileModificationTime" + count, ShsContext.DATE_FORMAT.format(new Date(file.getModificationTime())));
                jsonKeyValMap.put("isDir" + count, file.isDir() ? "0" : "1");
                count++;
            }
        } catch (Exception ee) {
            IOException iee = new IOException(ee);
            iee.setStackTrace(ee.getStackTrace());
            throw iee;
        }
        out.write(ShsContext.getJsonString(jsonKeyValMap));
        out.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
