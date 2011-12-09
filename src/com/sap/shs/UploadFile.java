package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFileSystem;
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
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/11/11
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadFile extends BaseServlet {

    private FileNameMap fileNameMap = null;

    private String jsonStringFormat = "{\"name\":\"%s\",\"type\":\"%s\",\"size\":\"%d\"}";

    // 5 MB buffer size
    private static final int BUFF_SIZE = 64 * 1024 * 1024;

    public void init() {
        fileNameMap = URLConnection.getFileNameMap();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();
        String nowFolder = null;
        List<String> filenames;
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            filenames = new ArrayList<String>();
            for (FileItem item : items) {
                // If the field is not a form field, then it is a "file"
                if (!item.isFormField()) {
                    String filename = FilenameUtils.getName(item.getName());
                    if (!StringUtils.isEmpty(filename)) {
                        filenames.add(filename);
                        InputStream fileContent = item.getInputStream();
                        try {
                            // Upload the file from request directly to HDFS
                            uploadToHdfs(nowFolder, filename, fileContent, configurationManager);
                        } catch (Exception ee) {
                            IOException iee = new IOException(ee);
                            iee.setStackTrace(ee.getStackTrace());
                        }
                        fileContent.close();
                    }
                } else {
                    if (item.getFieldName().equals("nowFolder")) {
                        nowFolder = item.getString();
                    }
                }
            }

            if (nowFolder == null || nowFolder.length() == 0) {
                throw new ServletException("Destination HDFS folder is null in UploadFile servlet.");
            } else {
                if (!nowFolder.endsWith("/")) {
                    nowFolder = nowFolder + "/";
                }
            }
            String[] jsonObject = null;
            // Read the files in HDFS person folder
            try {
                jsonObject = getHdfsJsons(nowFolder, filenames, configurationManager);
            } catch (Exception ee) {
                IOException iee = new IOException(ee);
                iee.setStackTrace(ee.getStackTrace());
                throw iee;
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonObject[0]);
            out.flush();
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
    }

    private String[] getHdfsJsons(String hdfsFolder, List<String> filenames, ConfigurationManager cm) throws Exception {
        IFileSystem filesystem = cm.getFileSystem();
        String[] jsons = new String[filenames.size()];
        int i = 0;
        // {"name":"picture.jpg","type":"image/jpeg","size":"123456789"}
        for (String filename : filenames) {
            String remoteFilename = hdfsFolder + filename.substring(filename.lastIndexOf(File.separator) + 1);
            jsons[i] =
                    String.format(jsonStringFormat, remoteFilename,
                            fileNameMap.getContentTypeFor(remoteFilename),
                            filesystem.getSize(remoteFilename));
            i++;
        }
        return jsons;
    }

    private void uploadToHdfs(String hdfsFolder, String filename, InputStream in, ConfigurationManager cm) throws Exception {
        IFileSystem filesystem = cm.getFileSystem();
        String simpleFilename = filename.substring(filename.lastIndexOf(File.separator) + 1);
        if (!hdfsFolder.endsWith("/")) {
            hdfsFolder = hdfsFolder + "/";
        }
        String remoteFilename = hdfsFolder + simpleFilename;
        OutputStream out = filesystem.getOutputStream(remoteFilename);
        IOUtils.copy(in, out);
        in.close();
        //out.flush();
        out.close();
        /*
        if (in.available() <= BUFF_SIZE) {
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } else {
            copyLarge(in, out);
        }
        */
    }

    private void copyLarge(InputStream in, OutputStream out) throws IOException {
        // get an channel from the stream
        final ReadableByteChannel inputChannel = Channels.newChannel(in);
        final WritableByteChannel outputChannel = Channels.newChannel(out);
        // copy the channels
        fastChannelCopy(inputChannel, outputChannel);
        // closing the channels
        inputChannel.close();
        outputChannel.close();
        out.flush();
    }

    public void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFF_SIZE);

        // JavaVM does its best to do this as native I/O operations.
        while (src.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }

        // EOF will leave buffer in fill state
        buffer.flip();

        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}
