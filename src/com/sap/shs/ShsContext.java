package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import com.sap.hadoop.conf.IFile;
import com.sap.hadoop.conf.IFileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/12/11
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShsContext {
    public static final String EMPLOYEE_ID_REGEX = "I\\d{6}";

    public static final String LOGIN_PASS = "Leopard";
    public static final String EMPLOYEE_ID = "UralFieldMouse";
    public static final String MD_PASSWORD = "ProboscisMonkey";
    public static final String PASSWORD = "PussyCat";
    public static final String JAR_FILENAME = "jfn";
    public static final String CLASS_NAME = "className";
    public static final String ADDITIONAL_PARAMETERS = "addP";

    public static final String HDFS_PERSON_FOLDER_BASE = "/user/";

    private static final ShsContext INSTANCE = new ShsContext();

    private static final Map<String, String> HDFS_PERSONAL_FOLDERS = new HashMap<String, String>();
    private static final Map<String, RunningJob> JOB_ID_RUNNING_JOB_MAP = new HashMap<String, RunningJob>();

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    public static String BASE_STORAGE_DIRECTORY;

    public static String LOGIN_JSP;

    public static String SECRET_KEY;

    private ShsContext() {
    }

    public static void setBaseStorageDirectory(String s) {
        BASE_STORAGE_DIRECTORY = s;
    }

    public static String getStorageDir(String employeeId) throws IOException {
        String storageDir;
        if (BASE_STORAGE_DIRECTORY.endsWith(File.separator)) {
            storageDir = BASE_STORAGE_DIRECTORY + employeeId + File.separator;
        } else {
            storageDir = BASE_STORAGE_DIRECTORY + File.separator + employeeId + File.separator;
        }
        File dir = new File(storageDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Unable to create upload directory '" + storageDir + "'");
            }
        }

        return storageDir;
    }

    public static ShsContext getInstance() {
        return INSTANCE;
    }


    public static void throwIfEmptyInSession(String name, String value) throws IOException {
        if (StringUtils.isEmpty(value)) {
            throw new IOException("In session, variable: '" + name + "' is null or empty");
        }
    }

    public static String getCookieValue(String cookieName, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String[] listHdfsFolder(String folder, ConfigurationManager cm) throws Exception {
        IFileSystem fs = cm.getFileSystem();
        IFile[] files = fs.listFiles(folder);
        List<String> fileList = new ArrayList<String>();
        for (IFile file : files) {
            fileList.add(file.getName());
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    public static ClassResourceBean getClassResourceBean(String filename) throws IOException {
        String upperFilename = filename.toUpperCase();
        File file = new File(filename);
        List<String> resourceEntries = null;
        if (upperFilename.endsWith(".JAR")) {
            resourceEntries = getResourceEntryFromJar(file);
        } else {
            resourceEntries = getResourceEntryFromZip(file);
        }
        ClassResourceBean bean = new ClassResourceBean();
        bean.setFullFilename(filename);
        bean.setResourceEntries(resourceEntries);
        return bean;
    }

    private static List<String> getResourceEntryFromJar(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> resourceEntries = new ArrayList<String>();

        while (entries.hasMoreElements()) {
            resourceEntries.add(entries.nextElement().toString());

        }
        if (resourceEntries.size() == 0) {
            throw new IOException("No jar entry found in the uploaded jar file: " + file);
        }
        return resourceEntries;
    }

    private static List<String> getResourceEntryFromZip(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration entries = zipFile.entries();
        List<String> resourceEntries = new ArrayList<String>();

        while (entries.hasMoreElements()) {
            resourceEntries.add(entries.nextElement().toString());
        }
        if (resourceEntries.size() == 0) {
            throw new IOException("No zip entry found in the uploaded zip file: " + file);
        }
        return resourceEntries;
    }

    public static String getPersonalJobFolder(String employeeId) throws IOException {
        String personalFolder = getStorageDir(employeeId);
        String jobFilder = personalFolder + "job" + File.separator;
        File jobFolderFile = new File(jobFilder);
        jobFolderFile.mkdirs();
        if (!jobFolderFile.exists()) {
            throw new IOException("Unable to create job folder: " + jobFilder);
        }
        return jobFilder;
    }

    public static String getPersonHdfsFolder(String employeeId) {
        if (!HDFS_PERSONAL_FOLDERS.containsKey(employeeId)) {
            String hdfsPersonalFolder = HDFS_PERSON_FOLDER_BASE + employeeId + "/";
            HDFS_PERSONAL_FOLDERS.put(employeeId, hdfsPersonalFolder);
        }
        return HDFS_PERSONAL_FOLDERS.get(employeeId);
    }

    public static String createPersonalHdfsFolder(ConfigurationManager cm) throws Exception {
        // If personal folder has already been created
        if (HDFS_PERSONAL_FOLDERS.containsKey(cm.getUsername())) {
            return HDFS_PERSONAL_FOLDERS.get(cm.getUsername());
        }
        // otherwise
        IFileSystem filesystem = cm.getFileSystem();
        String hdfsPersonalFolder = HDFS_PERSON_FOLDER_BASE + cm.getUsername() + "/";
        boolean createOk = filesystem.mkdirs(hdfsPersonalFolder);
        if (createOk) {
            HDFS_PERSONAL_FOLDERS.put(cm.getUsername(), hdfsPersonalFolder);
            return hdfsPersonalFolder;
        } else {
            throw new IOException("Failed to create remote HDFS folder '" + hdfsPersonalFolder + "'");
        }
    }

    public static String getJsonString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String, String> getStatusMap(String jobStatusFilename) throws IOException {
        File jobStatusFile = new File(jobStatusFilename);
        List<String> lines = FileUtils.readLines(jobStatusFile);
        Map<String, String> statusMap = new LinkedHashMap<String, String>();
        for (String line : lines) {
            int firstSpaceIdx = line.indexOf(" ");
            String name = line.substring(0, firstSpaceIdx);
            line = line.substring(firstSpaceIdx + 1);
            String[] tokens = line.split(" ");
            for (String token : tokens) {
                if (name.equals("Job") && !token.equals(".")) {
                    String[] keyValue = token.split("=");
                    if (keyValue.length == 2) {
                        statusMap.put(keyValue[0].toLowerCase(), keyValue[1].replaceAll("\"", ""));
                    }
                }
            }
        }
        return statusMap;
    }

    public static RunningJob getRunningJob(String jobIdString, ConfigurationManager cm) throws IOException {
        RunningJob rj = JOB_ID_RUNNING_JOB_MAP.get(jobIdString);
        if (rj == null) {
            JobID jobID = JobID.forName(jobIdString);
            JobConf jobConf = new JobConf(cm.getConfiguration());
            JobClient client = new JobClient(jobConf);
            rj = client.getJob(jobID);
            JOB_ID_RUNNING_JOB_MAP.put(jobIdString, rj);
        }
        return rj;
    }

    private static LoginPass getRequestLoginPass(Map<String, String[]> requestParaMap)
            throws NoSuchAlgorithmException {
        LoginPass loginPass = new LoginPass();

        loginPass.setLoadFromRequest(true);

        String username = requestParaMap.get(EMPLOYEE_ID) != null ? requestParaMap.get(EMPLOYEE_ID)[0] : null;
        String password = requestParaMap.get(PASSWORD) != null ? requestParaMap.get(PASSWORD)[0] : null;
        String realPassword = findPassword(username);
        if (!StringUtils.isEmpty(password) && password.equals(realPassword)) {
            loginPass.setUsername(username);
            loginPass.setPassword(password);
            String mdPassword = getDigestedString(realPassword);
            loginPass.setMessageDigestedPassword(mdPassword);
        }
        loginPass.validate();
        return loginPass;
    }

    private static LoginPass getCookieLoginPassFrom(Cookie[] cookies, ConfigurationManager cm)
            throws NoSuchAlgorithmException {
        LoginPass loginPass = new LoginPass();

        loginPass.setLoadFromCookie(true);

        Cookie usernameCookie = getCookie(cookies, ShsContext.EMPLOYEE_ID);
        Cookie mdPasswordCookie = getCookie(cookies, ShsContext.MD_PASSWORD);

        String username = usernameCookie != null ? usernameCookie.getValue() : null;
        String mdPassword = mdPasswordCookie != null ? mdPasswordCookie.getValue() : null;
        String realPassword = findPassword(username);
        String mdRealPassword = realPassword != null ? getDigestedString(realPassword) : null;

        if (!StringUtils.isEmpty(mdPassword) && mdPassword.equals(mdRealPassword)) {
            loginPass.setUsername(username);
            loginPass.setPassword(realPassword);
            loginPass.setMessageDigestedPassword(mdPassword);
        }
        loginPass.validate();
        return loginPass;
    }

    public static LoginPass getLoginPass(Map<String, String[]> requestParaMap,
                                         Cookie[] cookies,
                                         ConfigurationManager cm) throws Exception {
        LoginPass loginPass = getRequestLoginPass(requestParaMap);
        if (!loginPass.isValidPass()) {
            loginPass = getCookieLoginPassFrom(cookies, cm);
        }
        return loginPass;
    }

    //TODO
    private static String findPassword(String username) {
        if (username == null || username.length() == 0) {
            return null;
        }
        return "hadoopsap";
    }

    public static Cookie[] getLoginCookies(LoginPass loginPass) {
        if (StringUtils.isEmpty(loginPass.getUsername()) ||
                StringUtils.isEmpty(loginPass.getMessageDigestedPassword())) {
            return new Cookie[0];
        }

        Cookie usernameCookie = new Cookie(EMPLOYEE_ID, loginPass.getUsername());
        Cookie enPwdCookie = new Cookie(MD_PASSWORD, loginPass.getMessageDigestedPassword());
        usernameCookie.setMaxAge(-1);
        enPwdCookie.setMaxAge(-1);
        usernameCookie.setPath("/shs");
        enPwdCookie.setPath("/shs");
        return new Cookie[]{usernameCookie, enPwdCookie};
    }

    public static String getDigestedString(String pass) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		byte[] data = pass.getBytes();
		m.update(data,0,data.length);
		BigInteger i = new BigInteger(1,m.digest());
		return String.format("%1$032X", i);
	}

    static Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies == null || cookies.length == 0) {
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
