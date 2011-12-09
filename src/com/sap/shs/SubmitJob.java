package com.sap.shs;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/13/11
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubmitJob extends BaseServlet {
    public void init() {
        ShsContext.SCRIPT_FILENAME = getInitParameter("scriptFilename");
        if (StringUtils.isEmpty(ShsContext.SCRIPT_FILENAME)) {
            throw new RuntimeException("'scriptFilename' was not defined in the web.xml for SubmitJob servlet");
        }

        ShsContext.HADOOP_TASK_URL = getInitParameter("hadoopTaskUrl");
        if (StringUtils.isEmpty(ShsContext.HADOOP_TASK_URL)) {
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
        String command = ShsContext.getCommand(ShsContext.SCRIPT_FILENAME, resourceFile, className, additionalParameters);

System.out.println("command=\n" + command);
        Process process = ShsContext.execute(command);
        InputStream in = process.getInputStream();
        InputStream error = process.getErrorStream();
        String jobId = null;
        if (in.available() > 0) {
            System.out.println("Trying to get jobId from InputStream...");
            jobId = ShsContext.getJobId(in);
        } else {
            System.out.println("Trying to get jobId from ErrorInputStream...");
            jobId = ShsContext.getJobId(error);
        }

        if (jobId != null) {
            jsonKeyValMap.put("jobid", jobId);
        } else {
            jsonKeyValMap.put("error_msg", "Failed to submit the MapReduce task, please contact administrator for more details.");
        }
        in.close();
        error.close();
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
