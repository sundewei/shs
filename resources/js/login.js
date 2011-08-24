function submitForm(){
    // Add a dest hidden field
    var destInput = $('<input>');
    destInput.attr('type','hidden');
    destInput.attr('name','dest');
    destInput.attr('value','/shs/jspv/dashboard.jsp');

    // Make the form ready
    var loginForm = $("#loginForm");
    loginForm.attr("action","/shs/lll");
    loginForm.attr("method","post");
    loginForm.append(destInput);
    loginForm.submit();
}

// Make the image click-able as a submit button
$("#loginImgButton").click(
    function () {
        submitForm();
    }
);


//document.forms.loginForm.username.value = "I123456";
//document.forms.loginForm.password.value = "hadoopsap";
//document.forms.loginForm.username.readOnly = true;
//document.forms.loginForm.password.readOnly = true;
