<html>
<head>
    <script src="../resources/jquery/jquery.js"></script>
    <script src="../resources/jquery/center.js"></script>
    <link id="css" rel="stylesheet" href="../resources/css/login.css" type="text/css">
    <script language="javascript">
        $(document).ready(function() {
            $('.left, .content input, .content textarea, .content select').focus(
                function(){
                    $(this).parents('.row').addClass("over");
                }).blur(function(){
                    $(this).parents('.row').removeClass("over");
                });
            });
        function submitLoginForm() {
            document.forms.loginForm.method = "post";
            document.forms.loginForm.dest.value = "/shs/jspv/upload.jsp"
            document.forms.loginForm.action = "/shs/lll";
            document.forms.loginForm.submit();
        }
    </script>

</head>
<body>
    <form name="loginForm">
        <input type="hidden" name="dest">
    </form>

    <script language="javascript">
        document.forms.loginForm.username.value = "I123456";
        document.forms.loginForm.username.readOnly = true;
        document.forms.loginForm.password.readOnly = true;
    </script>

    <center>
    <div id="loginDiv">
        <hr />
            <div class="content">
                <div class="row">
                    <div class="left">Username</div>
                    <div class="right"><input name="username" type="text" class="text" /></div>
                </div>
                <div class="row">
                    <div class="left">Password</div>
                    <div class="right"><input name="password" type="password" class="text" /></div>
                </div>
                <div class="row">
                    <div class="left">&nbsp;</div>
                    <div class="right"><input name="login" type="button" value="Login" /></div>
                </div>
            </div>
        <hr />
    </div>
    </center>
    <script language="javascript">
        resize("loginDiv", 400, 400);
    </script>
</body>
