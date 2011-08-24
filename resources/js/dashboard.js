function getJobHistoryContent(jsonObj) {
    var count = 0;
    var content = "";
    while (true) {
        var jobid = eval("jsonObj.jobid"+count);
        if (!jobid) {
            break;
        } else {
            content += "<tr><td class='field' text-align='center'><a name='" + jobid + "' onClick='loadJobDetail(\"" + jobid + "\")'><span>" + jobid + "</span></a></td></tr>";
        }
        count++;
    }

    return content;
}

function loadJobDetail(jobid) {
    // show loading image
    $("table#jobDetail").html("<tr><td><img src='../resources/gfx/spinner.gif'></td></tr>");

    $.getJSON(jobStatusUrl, {"jobid": jobid},  function(json){
        $("table#jobDetail").html(getHistoryJobDetail(json));
        $("table#jobDetail").fadeIn(2000);
    });
}

function getHistoryJobDetail(jsonObj) {
    var content = "";
    // Additional info
    if (jsonObj.jobname) {
        content += "<tr class='important'><td>Job ID</td><td>" + jsonObj.jobid + "</td></tr>";
        content += "<tr><td>Job Name</td><td>" + jsonObj.jobname + "</td></tr>";
        if (jsonObj.job_status) {
            content += "<tr class='alert'><td>State</td><td>" + jsonObj.job_status + "</td></tr>";
        }
        if (jsonObj.tracking_url) {
            content += "<tr><td>Tracking URL</td><td><a href='" + jsonObj.tracking_url + "' target='_new'>" + jsonObj.tracking_url + "</a></td></tr>";
        }
        if (jsonObj.setup_progress) {
            content += "<tr><td>Setup Progress</td><td>" + jsonObj.setup_progress + "</td></tr>";
        }
        if (jsonObj.map_progress) {
            content += "<tr><td>Map Progress</td><td>" + jsonObj.map_progress + "</td></tr>";
        }
        if (jsonObj.reduce_progress) {
            content += "<tr><td>Reduce Progress</td><td>" + jsonObj.reduce_progress + "</td></tr>";
        }
        if (jsonObj.cleanup_progress) {
            content += "<tr><td>CleanUp Progress</td><td>" + jsonObj.cleanup_progress + "</td></tr>";
        }
        if (jsonObj.is_completed) {
            content += "<tr><td>Completed</td><td>" + jsonObj.is_completed + "</td></tr>";
        }
        if (jsonObj.is_successful) {
            content += "<tr><td>Successful</td><td>" + jsonObj.is_successful + "</td></tr>";
        }
        if (jsonObj.job_priority) {
            content += "<tr><td>job_priority</td><td>" + jsonObj.job_priority + "</td></tr>";
        }
        if (jsonObj.total_maps) {
            content += "<tr><td>total_maps</td><td>" + jsonObj.total_maps + "</td></tr>";
        }
        if (jsonObj.total_reduces) {
            content += "<tr><td>total_reduces</td><td>" + jsonObj.total_reduces + "</td></tr>";
        }
        if (jsonObj.finished_maps) {
            content += "<tr><td>finished_maps</td><td>" + jsonObj.finished_maps + "</td></tr>";
        }
        if (jsonObj.finished_reduces) {
            content += "<tr><td>finished_reduces</td><td>" + jsonObj.finished_reduces + "</td></tr>";
        }
        if (jsonObj.launch_time) {
            content += "<tr><td>launch_time</td><td>" + jsonObj.launch_time + "</td></tr>";
        }
        if (jsonObj.submit_time) {
            content += "<tr><td>submit_time</td><td>" + jsonObj.submit_time + "</td></tr>";
        }
        if (jsonObj.finish_time) {
            content += "<tr><td>finish_time</td><td>" + jsonObj.finish_time + "</td></tr>";
        }
        if (jsonObj.duration) {
            content += "<tr><td>duration</td><td>" + jsonObj.duration + "</td></tr>";
        }
    }
    return content;
}

function hideOldContent() {
    $("div#fileExtError").hide();
    $("div#selectorError").hide();
    $("table#currentJobStatus").hide();
}

function getLoadingImage() {
    var content = "<tr><td width='300px' height='200px'><img src='../resources/gfx/spinner.gif'></td></tr>";
    return content;
}

function getCurrentJobStatusTableContent(jsonObj) {
    var count = 0;
    var content = "";
    if (jsonObj.jobid && !jsonObj.job_status) {
        getJobStatus(jsonObj.jobid);
        return;
    }
    if (jsonObj.error_msg) {
        content += "<tr><td>Error</td><td>" + jsonObj.error_msg + "</td></tr>";
    }

    // Additional info
    if (jsonObj.jobname) {
        content += "<tr><td>Job ID</td><td>" + jsonObj.jobid + "</td></tr>";
        content += "<tr><td>Job Name</td><td>" + jsonObj.jobname + "</td></tr>";
        if (jsonObj.job_status) {
            content += "<tr><td>State</td><td>" + jsonObj.job_status + "</td></tr>";
        }
        if (jsonObj.tracking_url) {
            content += "<tr><td>Tracking URL</td><td><a href='" + jsonObj.tracking_url + "' target='_new'>" + jsonObj.tracking_url + "</a></td></tr>";
        }
        if (jsonObj.setup_progress) {
            content += "<tr><td>Setup Progress</td><td>" + jsonObj.setup_progress + "</td></tr>";
        }
        if (jsonObj.map_progress) {
            content += "<tr><td>Map Progress</td><td>" + jsonObj.map_progress + "</td></tr>";
        }
        if (jsonObj.reduce_progress) {
            content += "<tr><td>Reduce Progress</td><td>" + jsonObj.reduce_progress + "</td></tr>";
        }
        if (jsonObj.cleanup_progress) {
            content += "<tr><td>CleanUp Progress</td><td>" + jsonObj.cleanup_progress + "</td></tr>";
        }
        if (jsonObj.is_completed) {
            content += "<tr><td>Completed</td><td>" + jsonObj.is_completed + "</td></tr>";
        }
        if (jsonObj.is_successful) {
            content += "<tr><td>Successful</td><td>" + jsonObj.is_successful + "</td></tr>";
        }
        if (jsonObj.job_priority) {
            content += "<tr><td>job_priority</td><td>" + jsonObj.job_priority + "</td></tr>";
        }
        if (jsonObj.total_maps) {
            content += "<tr><td>total_maps</td><td>" + jsonObj.total_maps + "</td></tr>";
        }
        if (jsonObj.total_reduces) {
            content += "<tr><td>total_reduces</td><td>" + jsonObj.total_reduces + "</td></tr>";
        }
        if (jsonObj.finished_maps) {
            content += "<tr><td>finished_maps</td><td>" + jsonObj.finished_maps + "</td></tr>";
        }
        if (jsonObj.finished_reduces) {
            content += "<tr><td>finished_reduces</td><td>" + jsonObj.finished_reduces + "</td></tr>";
        }
        if (jsonObj.launch_time) {
            content += "<tr><td>launch_time</td><td>" + jsonObj.launch_time + "</td></tr>";
        }
        if (jsonObj.submit_time) {
            content += "<tr><td>submit_time</td><td>" + jsonObj.submit_time + "</td></tr>";
        }
        if (jsonObj.finish_time) {
            content += "<tr><td>finish_time</td><td>" + jsonObj.finish_time + "</td></tr>";
        }
        if (jsonObj.duration) {
            content += "<tr><td>duration</td><td>" + jsonObj.duration + "</td></tr>";
        }
        content += "<tr><td><a class='sapButton' id='refreshJobStatus' onClick='getJobStatus(\"" + jsonObj.jobid + "\")'><span>Refresh Status</span></a></td><td>&nbsp;</td></tr>";
        //content += "<tr><td span='2'>&nbsp;</td></tr>";
        //content += "<tr><td colspan='2'><table class='emailAlert'><tr><td><input id='alertEmail' type='text' size='40' value='Your email here'/>&nbsp;&nbsp;<a id='alertButton' onClick='saveAlertEmail(\"" + jsonObj.jobid + "\")'><span>Add Email Alert</span></a></td></tr></table></td></tr>";
    }
    // Reload the job history table
    $.getJSON(jobHistoryUrl, {}, function(json){
      $("#jobHistory").html(getJobHistoryContent(json));
    });
    return content;
}

function getJobStatus(jobid) {
    $("table#currentJobStatus").html(getLoadingImage());

    if (!jobid) {
        var jobid = $("a#refreshJobStatus").attr("name");
    }

    $.getJSON(jobStatusUrl, {"jobid": jobid}, function(json){
            $("table#currentJobStatus").html(getCurrentJobStatusTableContent(json));
            $("table#currentJobStatus").show();
        }
    );
}

function getHdfsTableContent(jsonObj, hdfsPersonFolder) {
    var count = 0;
    var content = "";
    content += "<tr><td class='headline'>File(s)</td><td class='headline'>File Size</td><td class='headline'>Modification Time</td></tr>";
    while (true) {
        var fileName = eval("jsonObj.fileName"+count);
        var fileLen = eval("jsonObj.fileLen"+count);
        var fileModificationTime = eval("jsonObj.fileModificationTime"+count);
        if (!fileName) {
            break;
        } else {
            var displayFilename = fileName.substring(fileName.indexOf(hdfsPersonFolder));
            content += "<tr><td class='field'><div id='deleteFile" + count + "' title='"+fileName+"'><a href='javascript:deleteFile(\"" + fileName + "\")'>X</a>&nbsp;&nbsp;" + displayFilename + "</div></td><td class='field'>" + fileLen + "</td><td class='field'>" + fileModificationTime + "</td></tr>";
        }
        count++;
    }

    if (count == 0) {
        content += "<tr><td class='field'>No file found!</td><td class='field'>&nbsp;</td><td class='field'>&nbsp;</td></tr>";
    }
    return content;
}

function getJarClassTableContent(jsonObj, filename) {
    var count = 0;
    var content = "";
    content += "<tr><td colspan=2 class='headline'>Steps</td></tr>";
    content += "<tr><td class='field'>1. Upload a Jar File</td><td class='field'>"+filename+"<input type=hidden id='uploadedJarFilename' value='" + filename + "'></td></tr>";
    content += "<tr><td class='field'>2. MapReduce Class</td><td class='field'><select id='classSelector'>";
    content += "<option value=''> Please select... </option>";
    while (true) {
        var className = eval("jsonObj.class"+count);
        if (!className) {
            break;
        } else {
            content += "<option value='" + className + "'>" + className + "</option>";
        }
        count++;
    }
    content += "</select></td></tr>";
    content += "<tr><td class='field'>3. Addnl. Params</td><td class='field'><input id='extraParams' type='text' size='90'></td></tr>";
    content += "<tr><td colspan=2 class='field'><a id='submitTask' class='sapButton'><span>Submit Task</span></a></td></tr>";
    return content;
}

function refreshFileBrowser() {
    $.getJSON(listHdfsUrl, {hdfsFolder: hdfsPersonFolder}, function(json){
        $("table#hdfsFiles").hide();
        $("table#hdfsFiles").html(getHdfsTableContent(json));
        $("table#hdfsFiles").fadeIn(2000);
    });
}

function deleteFile(filename) {
    if(confirm("Delete " + hdfsPersonFolder + filename + "?")) {
        $.post("/shs/ddd", {"hdfsFolder": hdfsPersonFolder, "filenameToDelete": filename, "action": "delete"},
            function(){
                refreshFileBrowser();
            }, "text");
    }
}