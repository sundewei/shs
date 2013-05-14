<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="com.sap.shs.LoginPass" %>
<%@ page import="com.sap.shs.demo.AmazonMovie" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.sap.hadoop.conf.ConfigurationManager" %>
<%@ page import="com.sap.shs.demo.Utilities" %>
<%
    // CSS test
    String theme = request.getParameter("theme");
    if (theme == null || theme.length() == 0) {
        theme = "sap";
    }
    LoginPass loginPass = (LoginPass)session.getAttribute(ShsContext.LOGIN_PASS);
    String employeeId = loginPass.getUsername();
    String hdfsPersonFolder = ShsContext.getPersonHdfsFolder(employeeId);
    ConfigurationManager configurationManager = loginPass.getConfigurationManager();
%>
<html>
<head>
    <script language="javascript">
        var hdfsPersonFolder = "<%=hdfsPersonFolder%>";
        var nowFolder = "<%=hdfsPersonFolder%>";
    </script>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <script language="javascript" src="../resources/js/jquery-ui.custom.js"></script>
    <script language="javascript" src="../resources/js/jshashtable-2.1.js"></script>
    <script language="javascript" src="../resources/js/jquery.rating.pack.js"></script>
    <script language="javascript" src='../resources/js/jquery.MetaData.js'></script>
    <link rel="stylesheet" href="../resources/css/<%=theme%>/jquery-ui-custom.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/jquery.rating.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/dashboard.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/sap.jquery.fileupload-ui.css" type="text/css"/>
</head>
<body>
    <div id="tabs">
        <ul>
            <li><a href="#tabs-1">HDFS Admin</a></li>
            <li><a href="#tabs-2">Submit a Task</a></li>
            <li><a href="#tabs-3">Task History</a></li>
            <!--<li><a href="#tabs-4">Procedures</a></li>-->
            <!--<li><a href="#tabs-5">Mahout</a></li>-->
        </ul>
        <div id="tabs-1">
            <table>
                <tr>
                    <td>
                        <form id="file_upload" action="/shs/js/uuu" method="POST" enctype="multipart/form-data">
                            <input type="file" name="file" multiple>
                            <input type="hidden" id="nowFolder" name="nowFolder" value="<%=hdfsPersonFolder%>">
                            <div class="js">Upload Input Files</div>
                        </form>
                    </td>
                    <td align="right" width=400>
                        <div class=normal id="logout" />
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
                        <form id="jar_upload" action="/shs/js/uuuJar" method="POST" enctype="multipart/form-data">
                            <input type=hidden name="isJar" value="true">
                            <input type="file" name="file" accept="jar">
                            <div class="js">Upload a Jar File</div>
                        </form>
                    </td>
                    <td align="right" width=400>
                        <div class=normal id="logout" />
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
                        <div class=info>Click task id to see details</div>
                    </td>
                    <td align="right" width=648>
                        <div class=normal id="logout" />
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
                            <table id="jobDetail" class="status" width="500 px">
                                <tr>
                                    <td>No job selected!</td>
                                </tr>
                            </table>
                        </fieldset>
                    </td>
                </tr>
            </table>
        </div>
        <!--
        <div id="tabs-4">
            <table>
                <tr>
                    <td>
                        <div class=info>Select a predefined procedure.</div>
                    </td>
                    <td align="right" width=648>
                        <div class=normal id="logout" />
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <span id="procedure" />
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <fieldset>
                            <legend>Plugin Details</legend>
                            <table id="procedureForm" class="status" width="500 px">
                                <jsp:include page="ApacheAccessLogParser.jsp" flush="true" />
                            </table>
                        </fieldset>
                    </td>
                </tr>
            </table>
        </div>

        <div id="tabs-5">
            <table>
                <tr>
                    <td>
                        <div class=info>Select a Mahout Theme</div>
                    </td>
                    <td align="right" width=648>
                        <div class=normal id="logout" />
                    </td>
                </tr>
                <tr>
                    <td align=center valign=top>
                        <span class='text'>Theme:</span> <select name='mahoutClassName' id='themeSelector' onChange='populateThemeForm()'>
                            <option selected value='-'>-</option>
                            <option value='org.apache.mahout.cf.taste.hadoop.item.RecommenderJob'>RecommenderJob</option>
                        </select>
                    </td>
                    <td>
                        <span id="mahoutThemeForm"></span>
                    </td>
                </tr>
            </table>
        </div>

        -->
    </div>

    <script language="javascript" src="../resources/js/dashboard.js"></script>
    <script language="javascript" src="../resources/js/jquery.fileupload.js"></script>
    <script language="javascript" src="../resources/js/jquery.fileupload-ui.js"></script>
    <script>
        // Prepare the Ajax call for reloading the HDFS folder content
        $.ajaxSetup ({
            cache: false
        });
        /*global $ */
        $(function () {
            $("div#logout").html('<span class="rightText">Hi, <%=employeeId%>  |  <a href="/shs/jsp/login.jsp" title="logoff">Logout</a></span>');
            $("span#procedure").html(getProcedureSelection());
            $("span#mahoutThemeForm").html(getThemesSelection());
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
