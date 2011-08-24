<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.PrintStream" %>
<%@ page isErrorPage="true" %>
<html>
<head>
    <title>JSP Error Page</title>
    <meta http-equiv="Pragma" content="no-cache">
    <style type="text/css">
        body {
            background-color: #cccccc;
            font-family: verdana, sans-serif, monospace;
            font-size: medium;
        }
    </style>
</head>

<body bgcolor=#ffffff>
<font face="Verdana">
    <center>
        <h2><font color=#DB1260>Error Page</font></h2>
    </center>
    <p> An exception was thrown: <b> <%=exception %>
        <p> With the following stack trace:
<pre>

<%
    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    exception.printStackTrace(new PrintStream(ostr));
    out.print(ostr);
%>
</pre>
        <br><a class="Verdana" href="/shs/jsp/login.jsp">Back to Login Page</a>
</body>
</html>