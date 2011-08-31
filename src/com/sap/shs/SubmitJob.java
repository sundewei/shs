package com.sap.shs;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/13/11
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubmitJob extends BaseServlet {

    private static final String SCRIPT_FILENAME = "scriptFilename";
    private static final String HADOOP_TASK_URL = "hadoopTaskUrl";

    private String scriptFilename;
    private String hadoopTaskUrl;

    // Since hadoop 0.21.0 is not compatible with Hive 0.7 yet, we ended up having to get the job id by checking the
    // output from the submit_job.hs script
    private static final String JOB_ID_KEYWORD = "mapred.JobClient: Running job: ";

    public void init() {
        scriptFilename = getInitParameter(SCRIPT_FILENAME);
        if (StringUtils.isEmpty(scriptFilename)) {
            throw new RuntimeException("'scriptFilename' was not defined in the web.xml for SubmitJob servlet");
        }

        hadoopTaskUrl = getInitParameter(HADOOP_TASK_URL);
        if (StringUtils.isEmpty(hadoopTaskUrl)) {
            throw new RuntimeException("'hadoopTaskUrl' was not defined in the web.xml for SubmitJob servlet");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String employeeId = ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getUsername();

        Map<String, String> jsonKeyValMap = new HashMap<String, String>();
        String resourceFile = ShsContext.getStorageDir(employeeId) + request.getParameter(ShsContext.JAR_FILENAME);
        String className = request.getParameter(ShsContext.CLASS_NAME);
        String additionalParameters = request.getParameter(ShsContext.ADDITIONAL_PARAMETERS);
        if (StringUtils.isEmpty(additionalParameters)) {
            additionalParameters = "";
        }
        String command = scriptFilename + " " + resourceFile + " " + className.replace(".class", "") + " " + additionalParameters;
System.out.println("command=\n" + command);
        Process process = Runtime.getRuntime().exec(command);

        String line = null;
        String jobId = null;
        // get its output (your input) stream
        DataInputStream errorIn = new DataInputStream(process.getErrorStream());
        StringBuilder sb = new StringBuilder();
        String firstLine = null;
        while ((line = errorIn.readLine()) != null) {
            if (firstLine == null) {
                firstLine = line;
            }
            sb.append(line).append("\n");
            if (line.indexOf(JOB_ID_KEYWORD) > 0) {
                jobId = line.substring(line.indexOf(JOB_ID_KEYWORD) + JOB_ID_KEYWORD.length());
                break;
            }
        }
        if (jobId != null) {
            jsonKeyValMap.put("jobid", jobId);
        } else {
            jsonKeyValMap.put("error_msg", firstLine.replace("\"", "'"));
        }
        errorIn.close();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.write(ShsContext.getJsonString(jsonKeyValMap));
        out.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    /*
    private JobControl runJob() throws Exception {
        String jarFullFilename = "C:\\projects\\sadoopClient\\dist\\sap_hadoop_example.jar";
        String urlPath = "jar:file://" + jarFullFilename + "!/";
        URLClassLoader clazzLoader = new URLClassLoader(new URL[]{new URL(urlPath)});
        Class clazz = clazzLoader.loadClass("com.sap.mapred.NumberSort");
        ITask task = (ITask) clazz.newInstance();
        Job job = task.getMapReduceJob();
        job.submit();
        job.
    }

    private void checkStatus() throws IOException {
        {
            Configuration conf = new Configuration();
            conf.set(ConfigManager.getInstance().get(), "hdfs://dewei0:9001");

            JobClient client = new JobClient(new JobConf(conf));

            JobStatus[] jobStatuses = client.getAllJobs();
            for (JobStatus jobStatus : jobStatuses) {

                long lastTaskEndTime = 0L;

                TaskReport[] mapReports = client.getMapTaskReports(jobStatus.getJobID());
                for (TaskReport r : mapReports) {
                    if (lastTaskEndTime < r.getFinishTime()) {
                        lastTaskEndTime = r.getFinishTime();
                    }
                }

                TaskReport[] reduceReports = client.getReduceTaskReports(jobStatus.getJobID());
                for (TaskReport r : reduceReports) {
                    if (lastTaskEndTime < r.getFinishTime()) {
                        lastTaskEndTime = r.getFinishTime();
                    }
                }
                client.getSetupTaskReports(jobStatus.getJobID());
                client.getCleanupTaskReports(jobStatus.getJobID());

                System.out.println("JobID: " + jobStatus.getJobID().toString() +
                        ", status: " + jobStatus.getRunState() +
                        ", username: " + jobStatus.getUsername() +
                        ", startTime: " + jobStatus.getStartTime() +
                        ", endTime: " + lastTaskEndTime +
                        ", Durration: " + (lastTaskEndTime - jobStatus.getStartTime()));
            }
        }
    }
    */

}
