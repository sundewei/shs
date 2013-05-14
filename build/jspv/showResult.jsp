<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="com.sap.shs.LoginPass" %>
<%@ page import="com.sap.hadoop.conf.ConfigurationManager" %>
<%@ page import="org.apache.hadoop.fs.FileSystem" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.hadoop.fs.Path" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="org.apache.http.entity.StringEntity" %>
<%@ page import="org.apache.commons.io.FileUtils" %>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.http.HttpResponse" %>
<%@ page import="org.apache.http.HttpEntity" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="org.apache.http.impl.client.DefaultHttpClient" %>
<%@ page import="org.apache.hadoop.conf.Configuration" %>

<%!
    String getData(Path hdfsPath, Configuration c) throws IOException {
        FileSystem fs = hdfsPath.getFileSystem(c);
        InputStream in = fs.open(hdfsPath);
        String data = IOUtils.toString(in);
        in.close();
        return data;
    }

    String getQuery(String restUrl, String xmlData) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(restUrl);
        httpPost.addHeader("Content-Type", "application/xml");
        StringEntity stringEntity = new StringEntity(xmlData);
        httpPost.setEntity(stringEntity);
        HttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println(response.getStatusLine().getReasonPhrase());
        }
        HttpEntity entity = response.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(entity.getContent(), writer);
//System.out.println("writer.toString()="+writer.toString());
        return writer.toString();
    }
%>
<%
    // CSS test
    String theme = request.getParameter("theme");
    if (theme == null || theme.length() == 0) {
        theme = "sap";
    }
    String getResult = null;
    String xmlDataPath = request.getParameter("xmlDataPath");
    String queryRestUrl = request.getParameter("queryRestUrl");
    String loopRestUrl = request.getParameter("loopRestUrl");
    LoginPass loginPass = (LoginPass)session.getAttribute(ShsContext.LOGIN_PASS);
    ConfigurationManager configurationManager = loginPass.getConfigurationManager();
    Configuration configuration = configurationManager.getConfiguration();
    Path path = new Path(xmlDataPath);
    String data = getData(path, configuration);

    if (loopRestUrl == null) {
        getResult = getQuery(queryRestUrl, data);
    }
%>
<html>
<head>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/jquery-ui.custom.js"></script>
    <script language="javascript" src="../resources/js/jshashtable-2.1.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <script language="javascript">

    </script>
    <link rel="stylesheet" href="../resources/css/<%=theme%>/jquery-ui-custom.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/dashboard.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/jquery.rating.css" type="text/css"/>
</head>
<body>
<div align=middle>
    <table bgcolor="#FFFFFF" width="60%">
        <tr>
            <td>
                <fieldset>
                    <legend>XML Representation of The Query Request</legend>
                    <table id="xml" class="historyTable" width="100%">
                        <tr>
                            <td>
                                <%=StringEscapeUtils.escapeHtml4(data).replaceAll(" ", "&nbsp;").replaceAll("\n", "<br />")%>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </td>
        </tr>
        <tr>
            <td>
                <fieldset>
                    <legend>Query Result</legend>
                    <table id="result" class="historyTable" width="100%">
                        <tr>
                            <td height="360px" valign="top">
                                <% if (loopRestUrl != null) { %>
                                    <iframe height="100%" width="100%" src="/shs/js/sq?xmlDataPath=<%=xmlDataPath%>&queryRestUrl=<%=queryRestUrl%>&loopRestUrl=<%=loopRestUrl%>">
                                    </iframe>
                                <% } else { %>
                                    <%=getResult%>
                                <% } %>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </td>
        </tr>
    </table>
</div>
</body>
</html>