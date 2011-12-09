package com.sap.shs;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/11/11
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadJarFile extends BaseServlet {

    private FileNameMap fileNameMap = null;

    private static final String jsonStringFormat = "{\"name\":\"%s\",\"type\":\"%s\",\"size\":\"%d\"}";

    public void init() {
        fileNameMap = URLConnection.getFileNameMap();
        ShsContext.BASE_STORAGE_DIRECTORY = getInitParameter("baseStorageDirectory");
        if (StringUtils.isEmpty(ShsContext.BASE_STORAGE_DIRECTORY)) {
            throw new RuntimeException("'baseStorageDirectory' was not defined in the web.xml for UploadFile servlet");
        }
        File initStorageDirFile = new File(ShsContext.BASE_STORAGE_DIRECTORY);
        if (!initStorageDirFile.exists()) {
            if (!initStorageDirFile.mkdirs()) {
                throw new RuntimeException("Unable to create baseStorageDirectory: " + ShsContext.BASE_STORAGE_DIRECTORY);
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String employeeId = ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getUsername();

        String storageDir = ShsContext.getStorageDir(employeeId);
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            String jarFilename = null;
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    String filename = FilenameUtils.getName(item.getName());
                    String destinationFilename = storageDir + filename;
                    if (!StringUtils.isEmpty(filename)) {
                        jarFilename = destinationFilename;
                        InputStream fileContent = item.getInputStream();
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(destinationFilename));
                        IOUtils.copy(fileContent, output);
                        fileContent.close();
                        output.close();
                        postCheckFile(destinationFilename);
                    }
                }
            }
            String[] jsonObject = null;
            preCheckFilename(jarFilename);
            jsonObject = getLocalJson(jarFilename);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonObject[0]);
            out.flush();
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
    }

    private String[] getLocalJson(String fullFilename) throws IOException {
        File localFile = new File(fullFilename);
        return new String[]{
                String.format(jsonStringFormat, localFile.getName(),
                        fileNameMap.getContentTypeFor(fullFilename),
                        localFile.length())};
    }

    private void preCheckFilename(String filename) throws IOException {
        if (filename == null) {
            throw new IOException("Filename is null");
        }

        String upperFilename = filename.toUpperCase();

        if (upperFilename.endsWith(".JAR") || upperFilename.endsWith(".ZIP")) {
            // Ok
        } else {
            throw new IOException("Only jar or zip file is allowed: " + filename);
        }
    }

    private void postCheckFile(String filename) throws IOException {
        if (filename == null) {
            throw new IOException("No file was uploaded");
        }

        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("Upload failed: '" + filename + "' does not exist");
        }

        if (file.length() == 0) {
            throw new IOException("Upload failed: '" + filename + "' is empty");
        }
    }

}
