<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%!
    String getCookieValue(Cookie[] cookies, String name) {
        for(Cookie cookie: cookies) {
            if (cookie.getName().equals(name)) {
                 return cookie.getValue();
            }
        }
        return null;
    }
%>
<%
    if ("out".equalsIgnoreCase(request.getParameter("action"))) {
        Cookie usernameCookie = new Cookie(ShsContext.EMPLOYEE_ID, "");
        usernameCookie.setMaxAge( 0 );

        Cookie mdPassword = new Cookie(ShsContext.MD_PASSWORD, "");
        mdPassword.setMaxAge( 0 );

        response.addCookie(usernameCookie);
        response.addCookie(mdPassword);
    }
    Cookie[] cookies = request.getCookies();
    String destination = getCookieValue(cookies, "destination");
%>
<html>
<head>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/login.css" type="text/css">
    <script language="javascript">
        var destination = "/shs/jspv/dashboard.jsp";

        <% if (!StringUtils.isEmpty(destination) && !destination.contains("login.jsp")) { %>
            destination = "<%=destination%>";
        <% } %>
    </script>
</head>
<!-- <%=destination%> -->
<body>
    <form id="loginForm">
		<fieldset class="login">
			<legend>SAP Task Force Login</legend>
			<div>
				<label>User Name</label> <input type="text" id="username" name="<%=ShsContext.EMPLOYEE_ID%>">
			</div>
			<div>
				<label>Password</label> <input type="password" id="password" name="<%=ShsContext.PASSWORD%>">
			</div>
			<div>
			    <div><label>&nbsp;</label><img id="loginImgButton" src="../resources/gfx/login_button.gif"></div>
			</div>
		</fieldset>
	</form>
    <script language="javascript" src="../resources/js/login.js"></script>
</body>
</html>