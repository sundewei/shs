package com.sap.shs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/26/11
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetJobHistory extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String employeeId = (String) session.getAttribute(ShsContext.EMPLOYEE_ID);

        String jobFolder = ShsContext.getPersonalJobFolder(employeeId);
        File jobFolderFile = new File(jobFolder);
        Set<String> historyJobIds = new TreeSet<String>();
        Map<String, String> jsonKeyValMap = new LinkedHashMap<String, String>();
        for (File logFile: jobFolderFile.listFiles(new ConfFileFilter())) {
            // job_201104260138_0015_conf.xml
            String filename = logFile.getName();
            int confIdx = filename.indexOf("_conf.xml");
            String jobId = filename.substring(0, confIdx);
            historyJobIds.add(jobId);
        }
        // For better ordering
        int count = 0;
        for (String id: historyJobIds) {
            jsonKeyValMap.put("jobid" + count, id);
System.out.println("put(jobid) = "+ id);
            count++;
        }
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        out.write(ShsContext.getJsonString(jsonKeyValMap));
        out.close();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    private class ConfFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name != null && !name.startsWith(".") && name.indexOf("_conf.xml") > 0;
        }
    }
}
