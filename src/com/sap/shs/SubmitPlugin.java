package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFileSystem;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/13/11
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubmitPlugin extends BaseServlet {

    public void init() {
        ShsContext.PLUGIN_JAR = getInitParameter("pluginJar");
        if (StringUtils.isEmpty(ShsContext.PLUGIN_JAR)) {
            throw new RuntimeException("'pluginJar' was not defined in the web.xml for SubmitPlugin servlet");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();
        String className = request.getParameter("className");
        Properties properties = ShsContext.getProperties(request.getParameterMap());
        properties.setProperty("className", className);
        String propertiesFile = ShsContext.BASE_STORAGE_DIRECTORY +
                configurationManager.getUsername() + "_" + className + "_" + System.currentTimeMillis() + ".properties";
        properties.store(new FileOutputStream(propertiesFile), "Plugin Submission for " + configurationManager.getUsername());
        Map<String, String> jsonMap = new HashMap<String, String>();
        try {
            String command = ShsContext.getCommand(ShsContext.SCRIPT_FILENAME, ShsContext.PLUGIN_JAR,
                    className, propertiesFile);
System.out.println("Plugin Command: \n " + command);
            Process process = ShsContext.execute(command);
            InputStream in = process.getInputStream();
            String jobId = ShsContext.getJobId(in);
            if (jobId != null) {
                jsonMap.put("jobid", jobId);
            } else {
                jsonMap.put("error_msg", "Failed to submit the MapReduce task, please contact administrator for more details.");
            }
            in.close();
        } catch (Exception e) {
            IOException ioe = new IOException(e);
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.write(ShsContext.getJsonString(jsonMap));
System.out.println("ShsContext.getJsonString(jsonMap)=\n"+ShsContext.getJsonString(jsonMap));
        out.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
