package com.sap.shs;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/22/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListJarClass extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String employeeId = (String) session.getAttribute(ShsContext.EMPLOYEE_ID);

        String storageDir = ShsContext.getStorageDir(employeeId);

        String jarFilename = request.getParameter("jarFilename");
        jarFilename = storageDir + jarFilename;

        ClassResourceBean bean = ShsContext.getClassResourceBean(jarFilename);
        List<String> classes = bean.getResourceEntries();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int count = 0;
        for (String s : classes) {
            if (s.endsWith(".class") && s.indexOf("$") < 0) {
                s = s.replace(".class", "");
                s = s.replace("/", ".");
                sb.append("\"class").append(count).append("\":\"").append(s).append("\",");
                count++;
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.write(sb.toString());
        out.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static void main(String[] arg) throws Exception {
        ClassResourceBean bean = ShsContext.getClassResourceBean("C:\\projects\\sadoopClient\\dist\\sap_hadoop_example.jar");
        List<String> list = bean.getResourceEntries();
        for (String ent: list) {
            System.out.println(ent);
        }
    }
}
