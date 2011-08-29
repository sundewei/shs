<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="com.sap.hadoop.conf.ConfigurationManager" %>
<%
    // CSS test
    String theme = request.getParameter("theme");
    if (theme == null || theme.length() == 0) {
        theme = "sap";
    }
    String employeeId = (String) session.getAttribute(ShsContext.EMPLOYEE_ID);
    String hdfsPersonFolder = ShsContext.getPersonHdfsFolder(employeeId);
    String[] hdfsPersonalFiles =
            ShsContext.listHdfsFolder(hdfsPersonFolder,
                                      (ConfigurationManager)session.getAttribute(ShsContext.CONFIGURATION_MANAGER));
    int fileCount = 0;
    if (hdfsPersonalFiles != null && hdfsPersonalFiles.length > 0) {
        fileCount = hdfsPersonalFiles.length;
    }
%>
<html>
<head>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/jquery-ui.custom.js"></script>
    <link rel="stylesheet" href="../resources/css/<%=theme%>/jquery-ui-custom.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/dashboard.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/sap.jquery.fileupload-ui.css" type="text/css"/>
</head>
<body>
    <div id="tabs">
        <ul>
            <li><a href="#tabs-1">HDFS Admin</a></li>
            <li><a href="#tabs-2">Submit a Task</a></li>
            <li><a href="#tabs-3">Task History</a></li>
        </ul>
        <div id="tabs-1">
            <table>
                <tr>
                    <td>
                        <form id="file_upload" action="/shs/uuu" method="POST" enctype="multipart/form-data">
                            <input type="file" name="file" multiple>
                            <input type="hidden" id="nowFolder" name="nowFolder" value="<%=hdfsPersonFolder%>">
                            <div class="js">Upload Input Files</div>
                        </form>
                    </td>
                    <td align="right" width=400>
                        <span class="rightText">Hi, <%=employeeId%>  |  <a href="/shs/jsp/login.jsp" title="logoff">Louout</a></span>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table id="currentFolderName" class="currentFolderName">
                        </table>
                        <table id="hdfsFiles" class="fileBrowser">
                        </table>
                        <table id="files">
                        </table>
                    </td>
                    <td valign="top">
                        <div class="info">
                            - Your root directory: <strong><font color="blue"><%=hdfsPersonFolder%></font></strong><br />
                            - Click a filename to download
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <div id="tabs-2">
            <table>
                <tr>
                    <td>
                        <form id="jar_upload" action="/shs/uuuJar" method="POST" enctype="multipart/form-data">
                            <input type=hidden name="isJar" value="true">
                            <input type="file" name="file" accept="jar">
                            <div class="js">Upload a Jar File</div>
                        </form>
                    </td>
                    <td align="right" width=400>
                        <div>
                            <span class="rightText">Hi, <%=employeeId%>  |  <a href="/shs/jsp/login.jsp" title="logoff">Louout</a></span>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table id="jarFile" class="fileBrowser">
                            <tr><td colspan=2 class='headline'>Steps</td></tr>
                            <tr><td class='field'>No jar file uploaded!</td><td class='field'>&nbsp;</td></tr>
                        </table>
                        <table id="jars"></table>
                    </td>
                    <td valign="top">
                        <div class="info">
                            1. Upload a jar file.<br>
                            2. Select a class to run.<br>
                            3. Enter command line parameters.
                        </div>
                        <div id="fileExtError" class="error">
                            Only a jar or zip file is allowed!
                        </div>
                        <div id="selectorError" class="error">
                            No MapReduce class selected!
                        </div>
                    </td>
                </tr>
                <tr>
                    <!--LEFT-->
                    <td valign="top">
                        <table id="currentJobStatus" class='status'>
                        </table>
                    </td>
                    <!--RIGHT-->
                    <td></td>
                </tr>
            </table>
        </div>
        <div id="tabs-3">
            <table>
                <tr>
                    <td>
                        <div class=info>Click on the task id to see the details</div>
                    </td>
                    <td align="right" width=648>
                        <div>
                            <span class="rightText">Hi, <%=employeeId%>  |  <a href="/shs/jsp/login.jsp" title="logoff">Louout</a></span>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <fieldset>
                            <legend>Submitted Task(s)</legend>
                            <table id="jobHistory" class="historyTable">
                            </table>
                        </fieldset>
                    </td>
                    <td valign="top">
                        <fieldset>
                            <legend>Job Details</legend>
                            <table id="jobDetail" class="status" width="500 px" height="300 px">
                                <tr>
                                    <td text-align="center">No job selected!</td>
                                </tr>
                            </table>
                        </fieldset>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <script language="javascript" src="../resources/js/dashboard.js"></script>
    <script language="javascript" src="../resources/js/jquery.fileupload.js"></script>
    <script language="javascript" src="../resources/js/jquery.fileupload-ui.js"></script>
    <script>
        // Prepare the Ajax call for reloading the HDFS folder content
        $.ajaxSetup ({
            cache: false
        });
        var listHdfsUrl = "/shs/hfl";
        var parseJarUrl = "/shs/jjj";
        var submitJobUrl = "/shs/sss";
        var jobStatusUrl = "/shs/ggg";
        var jobHistoryUrl = "/shs/hhh";
        var hdfsPersonFolder = "<%=hdfsPersonFolder%>";
        var nowFolder = "<%=hdfsPersonFolder%>";

        /*global $ */
        $(function () {
            $("#tabs").tabs();
            $("table#hdfsFiles").show("slow");
            $("#hdfs_status").addClass("fileBrowser").show("slow");
            hideOldContent();

            // Build the HDFS folder content for the first time
            refreshFileBrowser();

            // Build the Task History table
            $.getJSON(jobHistoryUrl, {}, function(json){
                $("#jobHistory").html(getJobHistoryContent(json));
            });
            //////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////
            $('#file_upload').fileUploadUI({
                uploadTable: $('#files'),
                buildUploadRow: function (files, index) {
                    return $('<tr><td>' + files[index].name + '<\/td>' +
                            '<td class="file_upload_progress"><div><\/div><\/td>' +
                            '<td class="file_upload_cancel">' +
                            '<button class="ui-state-default ui-corner-all" title="Cancel">' +
                            '<span class="ui-icon ui-icon-cancel">Cancel<\/span>' +
                            '<\/button><\/td><\/tr>');
                },
                buildDownloadRow: function (file) {
                    refreshFileBrowser();
                    return $('');
                }
            });

            //////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////
            $('#jar_upload').fileUploadUI({
                onSend: function (event, files, index, xhr, handler) {
                    hideOldContent();
                    //alert("files["+index+"].name="+files[index].name);
                    var dotIdx = files[index].name.lastIndexOf(".");

                    if (dotIdx >= 0) {
                        var ext = files[index].name.substring(dotIdx+1, files[index].name.length);
                        if (ext.toLowerCase() == 'jar' || ext.toLowerCase() == 'zip') {
                            return true;
                        }
                    }
                    $("div#fileExtError").show();
                    $("table#jars").html("");
                    return false;
                },
                uploadTable: $('#jars'),
                buildUploadRow: function (files, index) {
                    hideOldContent();
                    //$("table#jarFile").html("<tr><td colspan=2 class='headline'>Steps</td></tr><tr><td class='field'>No jar file uploaded!</td><td class='field'>&nbsp;</td></tr>");
                    return $('<tr><td>' + files[index].name + '<\/td>' +
                            '<td class="file_upload_progress"><div><\/div><\/td>' +
                            '<td class="file_upload_cancel">' +
                            '<button class="ui-state-default ui-corner-all" title="Cancel">' +
                            '<span class="ui-icon ui-icon-cancel">Cancel<\/span>' +
                            '<\/button><\/td><\/tr>');
                },
                buildDownloadRow: function (file) {
                    hideOldContent();
                    $.getJSON(parseJarUrl, {jarFilename: file.name}, function(json){
                        $("table#jarFile").hide();
                        $("table#jarFile").html(getJarClassTableContent(json, file.name));
                        $("table#jarFile").fadeIn(2000);

                        $("a#submitTask").click(
                            function () {
                                $("table#currentJobStatus").show();
                                $("table#currentJobStatus").html(getLoadingImage());
                                var className = $("#classSelector option:selected").val();
                                var uploadedJarFilename = $("#uploadedJarFilename").val();
                                var extraParams = $("#extraParams").val();

                                //alert('className='+className);
                                //alert('uploadedJarFilename='+uploadedJarFilename);
                                //alert('extraParams='+extraParams);

                                if (className == '') {
                                    $("div#selectorError").show();
                                    return;
                                }
                                //alert("all inputs valid.");
                                $.getJSON(submitJobUrl,
                                          {"<%=ShsContext.CLASS_NAME%>": className, "<%=ShsContext.JAR_FILENAME%>": uploadedJarFilename, "<%=ShsContext.ADDITIONAL_PARAMETERS%>": extraParams},
                                          function(json){
                                              $("table#currentJobStatus").hide();
                                              $("table#currentJobStatus").html(getCurrentJobStatusTableContent(json));
                                              $("table#currentJobStatus").fadeIn(2000);
                                          }
                                );
                            });
                    });
                    return $('');
                }
            });
        });

    </script>
</body>
</html>
