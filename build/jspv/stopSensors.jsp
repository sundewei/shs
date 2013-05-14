<%@ page import="java.util.Map" %>
<%@ page import="com.sap.shield.data.GeneratorBase" %>
<%@ page import="org.apache.hadoop.fs.Path" %>
<%@ page import="com.sap.hadoop.conf.ConfigurationManager" %>
<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="com.sap.shs.LoginPass" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.apache.hadoop.fs.FileSystem" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.apache.hadoop.conf.Configuration" %>
<%@ page import="com.sap.shield.Constants" %>
<%@ page import="org.apache.commons.csv.CSVParser" %>
<%@ page import="org.apache.commons.csv.CSVRecord" %>
<%!
    String getData(Path hdfsPath, Configuration c) throws IOException {
        FileSystem fs = hdfsPath.getFileSystem(c);
        InputStream in = fs.open(hdfsPath);
        String data = IOUtils.toString(in);
        in.close();
        return data;
    }
%>
<%
	String runnableMapName = "tmn_runnable";
    String result = "ok";
    LoginPass loginPass = (LoginPass)session.getAttribute(ShsContext.LOGIN_PASS);
    ConfigurationManager configurationManager = loginPass.getConfigurationManager();
	String sensorFilename = request.getParameter("sensorFilename");
    int stopped = 0;
    Map<String, Runnable> runnableMap = (Map<String, Runnable>)session.getAttribute(runnableMapName);
    if (runnableMap != null) {
        if (sensorFilename != null) {
            Path path = new Path(sensorFilename);
            String data = getData(path, configurationManager.getConfiguration());
            CSVParser csvParser = new CSVParser(data, Constants.XS_ENGINE_CSV_FORMAT);
            for (CSVRecord csvRecord: csvParser.getRecords()) {
                GeneratorBase base = (GeneratorBase)runnableMap.get(csvRecord.get(0));
                base.setStopRunning(true);
                stopped ++;
            }
        } else {
            try {
                for (Map.Entry<String, Runnable> entry: runnableMap.entrySet()) {
                    GeneratorBase base = (GeneratorBase)entry.getValue();
                    base.setStopRunning(true);
                    stopped ++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
        }
    }
    result += ", stopped " + stopped + " threads...";
%>
<%=result%>