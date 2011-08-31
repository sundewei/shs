package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/25/11
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetJobStatus extends BaseServlet {
    private static final NumberFormat PERCENTAGE_FORMAT = NumberFormat.getPercentInstance();

    private String hadoopLogDirectory;
    private String hadoopLogHistoryDirectory;
    private static final String HADOOP_LOG_DIRECTORY = "hadoopLogDirectory";

    public void init() {
        hadoopLogDirectory = getInitParameter(HADOOP_LOG_DIRECTORY);
        if (StringUtils.isEmpty(hadoopLogDirectory)) {
            throw new RuntimeException("'hadoopLogDirectory' was not defined in the web.xml for GetJobStatus servlet");
        }
        hadoopLogHistoryDirectory = hadoopLogDirectory + "history/";
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jobIdString = request.getParameter("jobid");
        HttpSession session = request.getSession(true);
        String employeeId = ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getUsername();
        ConfigurationManager configurationManager =
                ((LoginPass) session.getAttribute(ShsContext.LOGIN_PASS)).getConfigurationManager();
        RunningJob runningJob = ShsContext.getRunningJob(jobIdString, configurationManager);
        Map<String, String> jsonKeyValMap = null;
        if (runningJob != null) {
            jsonKeyValMap = getJsonMap(jobIdString, runningJob);
        } else {
            jsonKeyValMap = getJsonMap(ShsContext.getStatusMap(getHistoryJobStatusFile(jobIdString)));
        }
        copyJobLog(employeeId, jobIdString);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        out.write(ShsContext.getJsonString(jsonKeyValMap));
        out.close();

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private String getHistoryJobStatusFile(String jobId) throws IOException {
        File historyFolder = new File(hadoopLogHistoryDirectory);
        File[] statusFiles = historyFolder.listFiles(new JobStatusFilter(jobId));
        if (statusFiles != null && statusFiles.length > 0) {
            return statusFiles[0].getAbsolutePath();
        }
        return null;
    }

    private Map<String, String> getJsonMap(Map<String, String> statusMap) throws IOException {
        /*
        "jobid":"job_201104260138_0016",
        "jobname":"sort1",
        "user":"ccc",
        "submit_time":"1303837502137",
        "jobconf":"hdfs://dewei0:9000/usr/local/hadoop_tmp/mapred/system/job_201104260138_0016/job.xml",
        "job_priority":"normal",
        "launch_time":"1303837502312",
        "total_maps":"35",
        "total_reduces":"1",
        "job_status":"killed",
        "finish_time":"1303837548174",
        "finished_maps":"0",
        "finished_reduces":"0"
         */
        Map<String, String> jsonKeyValMap = new HashMap<String, String>();
        long submitTimeMs = getLong(statusMap.get("submit_time"));
        long finishTimeMs = getLong(statusMap.get("finish_time"));
        long launchTimeMs = getLong(statusMap.get("launch_time"));
        long durationMs = finishTimeMs - submitTimeMs;

        jsonKeyValMap.put("jobid", statusMap.get("jobid"));
        jsonKeyValMap.put("jobname", statusMap.get("jobname"));
        if (submitTimeMs > 0) {
            jsonKeyValMap.put("submit_time", ShsContext.DATE_FORMAT.format(new Date(submitTimeMs)));
        }
        jsonKeyValMap.put("job_priority", statusMap.get("job_priority"));
        if (launchTimeMs > 0) {
            jsonKeyValMap.put("launch_time", ShsContext.DATE_FORMAT.format(new Date(launchTimeMs)));
        }
        jsonKeyValMap.put("total_maps", statusMap.get("total_maps"));
        jsonKeyValMap.put("total_reduces", statusMap.get("total_reduces"));
        jsonKeyValMap.put("job_status", statusMap.get("job_status"));
        if (finishTimeMs > 0) {
            jsonKeyValMap.put("finish_time", ShsContext.DATE_FORMAT.format(new Date(finishTimeMs)));
        }
        if (submitTimeMs > 0 && finishTimeMs > 0) {
            long durationSec = (durationMs / 1000);
            String durationString = String.format("%d:%02d:%02d", durationSec / 3600, (durationSec % 3600) / 60, (durationSec % 60));
            jsonKeyValMap.put("duration", durationString);
        }
        jsonKeyValMap.put("finished_maps", statusMap.get("finished_maps"));
        jsonKeyValMap.put("finished_reduces", statusMap.get("finished_reduces"));
        return jsonKeyValMap;
    }

    private long getLong(String ms) {
        long launchTimeMs = -1;
        try {
            launchTimeMs = Long.parseLong(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return launchTimeMs;
    }

    private Map<String, String> getJsonMap(String jobIdString, RunningJob runningJob) throws IOException {
        Map<String, String> jsonKeyValMap = new HashMap<String, String>();
        jsonKeyValMap.put("jobid", jobIdString);
        jsonKeyValMap.put("tracking_url", runningJob.getTrackingURL());
        jsonKeyValMap.put("is_completed", String.valueOf(runningJob.isComplete()));
        jsonKeyValMap.put("jobname", runningJob.getJobName());

        int state = runningJob.getJobState();
        if (state == JobStatus.FAILED) {
            jsonKeyValMap.put("job_status", "Failed");
        } else if (state == JobStatus.KILLED) {
            jsonKeyValMap.put("job_status", "Killed");
        } else if (state == JobStatus.PREP) {
            jsonKeyValMap.put("job_status", "Preparing");
        } else if (state == JobStatus.RUNNING) {
            jsonKeyValMap.put("job_status", "Running");
        } else if (state == JobStatus.SUCCEEDED) {
            jsonKeyValMap.put("job_status", "Succeeded");
        }

        jsonKeyValMap.put("is_successful", String.valueOf(runningJob.isSuccessful()));
        jsonKeyValMap.put("map_progress", PERCENTAGE_FORMAT.format(runningJob.mapProgress()));
        jsonKeyValMap.put("reduce_progress", PERCENTAGE_FORMAT.format(runningJob.reduceProgress()));
        jsonKeyValMap.put("cleanup_progress", PERCENTAGE_FORMAT.format(runningJob.cleanupProgress()));
        jsonKeyValMap.put("setup_progress", PERCENTAGE_FORMAT.format(runningJob.setupProgress()));
        return jsonKeyValMap;
    }

    private void copyJobLog(String eid, String jid) throws IOException {
        String jobFolder = ShsContext.getPersonalJobFolder(eid);
        // job_<job_ID>_conf.xml
        String jobConfFilename = jid + "_conf.xml";
        File jobConfFile = new File(jobFolder + jobConfFilename);
        // Look at the Hadoop log folder
        String hadoopConf = hadoopLogDirectory + jobConfFilename;
        File hadoopConfFile = new File(hadoopConf);
        if (hadoopConfFile.exists()) {
            if (!jobConfFile.exists() ||
                    hadoopConfFile.length() != jobConfFile.length() ||
                    hadoopConfFile.lastModified() != jobConfFile.lastModified()) {
                FileUtils.copyFileToDirectory(hadoopConfFile, new File(jobFolder));
            }
        }
    }

    private class JobStatusFilter implements FilenameFilter {
        private String jobId;

        public JobStatusFilter(String jobId) {
            this.jobId = jobId;
        }

        public boolean accept(File dir, String name) {
            // dewei0_1303839757537_job_201104261042_0001_ccc_sort1
            if (name.indexOf(".") < 0 && name.indexOf(jobId) > 0) {
                return true;
            }
            return false;
        }
    }
}
