<%@ page import="com.sap.shs.ShsContext" %>
<html>
<head>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <link id="css" rel="stylesheet" href="../resources/css/common.css" type="text/css">
    <link id="css" rel="stylesheet" href="../resources/css/login.css" type="text/css">
</head>
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