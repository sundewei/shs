<%@ page import="com.sap.shs.ShsContext" %>
<%@ page import="com.sap.shs.LoginPass" %>
<%@ page import="com.sap.shs.demo.AmazonMovie" %>
<%@ page import="com.sap.hadoop.conf.ConfigurationManager" %>
<%@ page import="com.sap.shs.demo.Utilities" %>
<%@ page import="com.sap.shield.cache.ShieldColumns" %>
<%@ page import="com.sap.shield.cache.ShieldTableColumns" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.hadoop.fs.Path" %>
<%@ page import="org.apache.hadoop.conf.Configuration" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.apache.hadoop.fs.FileSystem" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.csv.CSVParser" %>
<%@ page import="org.apache.commons.csv.CSVRecord" %>
<%@ page import="com.sap.shield.Constants" %>
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
    String logFolder = !StringUtils.isEmpty(request.getParameter("logFolder")) ? request.getParameter("logFolder") : "data";
    String theme = !StringUtils.isEmpty(request.getParameter("theme")) ? request.getParameter("theme") : "sap";
    LoginPass loginPass = (LoginPass)session.getAttribute(ShsContext.LOGIN_PASS);
    ConfigurationManager configurationManager = loginPass.getConfigurationManager();
    Configuration configuration = configurationManager.getConfiguration();

    // CSS test
    String threadMapName = "tmn";
    Map<String, Thread> map = (Map<String, Thread>)session.getAttribute(threadMapName);

    Path path = new Path("/data/shield/requests/queryTable.xml");
    String data = getData(path, configuration);

    Path oilPath = new Path("/data/shield/oilSensors/oil.csv");
    String oilData = getData(oilPath, configuration);

    Path windmillPath = new Path("/data/shield/windmillSensors/windmills.csv");
    String windmillData = getData(windmillPath, configuration);

    TreeSet<ShieldTableColumns.Row> shieldTableColumnRows = ShieldTableColumns.getRowByTableId(22);
    List<String> rowNames = new ArrayList<String>();
    List<String> rowTypes = new ArrayList<String>();
    if (shieldTableColumnRows != null) {
        for (ShieldTableColumns.Row row: shieldTableColumnRows) {
            ShieldColumns.Row cRow = ShieldColumns.getRow(row.getColumnId());
            rowNames.add(cRow.getName());
            rowTypes.add(cRow.getDataType());
        }
    }
    CSVParser oilCsvParser = new CSVParser(oilData, Constants.XS_ENGINE_CSV_FORMAT);
    CSVParser windmillCsvParser = new CSVParser(windmillData, Constants.XS_ENGINE_CSV_FORMAT);
%>
<html>
<head>
    <script language="javascript" src="../resources/js/jshashtable-2.1.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/jquery.csv-0.71.min.js"></script>
    <script language="javascript" src="../resources/js/dashboard.js"></script>
    <script language="javascript" src="../resources/js/shield.js"></script>
    <script language="javascript" src="../resources/js/jquery-ui.custom.js"></script>
    <script language="javascript">
        var queryRestUrl = "http://LLBPAL36:8080/shs/rest/query/22";
        var getRestUrl = "http://LLBPAL36:8080/shs/rest/get/22";
        var loopRestUrl = "http://LLBPAL36:8080/shs/rest/result";
        var restUrl = "http://llbpal36:8080/shs/rest/table/22";
        var tableSchemaUrl = "http://llbpal36:8080/shs/rest/metadata/schema/demoSensorData";
        var writeHdfsUrl = "http://llbpal36:8080/shs/js/wthf";

        var disableEditQuery = true;
        var reminderText2 = "Be sure to save the query.";
        var reminderText1 = "";        
    </script>

    <script language="javascript">
        var statusCount = 0;
        var statusCountLimit = 3;
        var threadMapName = "<%=threadMapName%>";
        var fileSpans = new Hashtable();
        var actionSpans = new Hashtable();
        var runningFiles = new Array();
        var clickedFiles = new Array();
		var oilSids = new Array();
		var windmillSids = new Array();
        <% if (map != null) { %>
            <% for (Map.Entry<String, Thread> entry: map.entrySet()) { %>
                runningFiles[runningFiles.length] = "<%=entry.getKey()%>";
                if (!contains("<%=entry.getKey()%>", clickedFiles)) {
                    clickedFiles[clickedFiles.length] = "<%=entry.getKey()%>";
                }
            <% } %>
        <% } %>
        var demoTableExist = false;
    </script>

    <link rel="stylesheet" href="../resources/css/<%=theme%>/jquery-ui-custom.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/dashboard.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/jquery.rating.css" type="text/css"/>
    <script language="javascript">
        //getFiles(listHdfsUrl, "/data/shield/<%=logFolder%>/");
    </script>
</head>
<body>	
    <div id="tabs">
        <ul>
            <li><a href="#tabs-1">Sensor Panel</a></li>
            <li><a href="#tabs-2">Analysis Panel</a></li>
            <!--<li><a href="#tabs-5">Mahout</a></li>-->
        </ul>
        <div id="tabs-1">
            <table>
                <tr>
                    <td>
                        <table class='fileBrowser'>
                            <tr>
                                <td width="240px">
                                    <a class="sapButton" href='javascript:createDemoTable()' id='createDemoTableBtn'><span>Create demo table</span></a>
                                    <a class="sapButton" href='javascript:deleteDemoTable()' id='deleteDemoTableBtn'><span>Delete demo table</span></a>
                                </td>
                                <td width="240px">
                                    <a class="sapButton" href='javascript:updateDemoTable()' id='updateDemoTableBtn'><span>Update demo table</span></a>
                                </td>
                                <td width="240px">
                                    <a class="sapButton" href='javascript:getSensorSignalCounts()' id='refreshCountBtn'><span>Count Sensor Signals</span></a>
                                </td>
                            </tr>
							<!-- Divider -->
							<tr><td colspan="3"><hr /></td></tr>
							<!-- Divider -->
							
							<tr align="left">
								<td colspan="3">
									<img src="/shs/resources/gfx/icon-whitearrowright.png"><img src="/shs/resources/gfx/icon-whitearrowright.png"><img src="/shs/resources/gfx/icon-whitearrowright.png">
									<b>DemoSensorData:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="tableStatus" />
								</td>
							</tr>
							<tr align="left">
								<td colspan="3">
									<table>
										<tr id="columnNameTr" />
										<tr id="columnTypeTr" />
									</table>
								</td>
							</tr>
							
							<!-- Divider -->
							<tr><td colspan="3"><hr /></td></tr>
							<!-- Divider -->
                            <tr>
                                <td valign="top" class="boxed">
                                    <!--<div id="originalLogFiles" />-->
                                    <div id="oilSensors">
                                        <table>
											<tr><td class="boxed" colspan="3" >Oil/Gas Sensors&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="oilSensorSwitch" onclick="startOil()" /></td></tr>
                                            <tr><td class='headline'>System Id</td><td class='headline'>State</td><td class='headline'>Well Type</td></tr>
                                            <%  List<CSVRecord> oilList = oilCsvParser.getRecords(); %>
                                            <% for (CSVRecord csvRecord: oilList) { %>
												<% boolean firstValue = true; %>
                                                <tr>
                                                    <% for (String value: csvRecord) { %>
														<% if (firstValue) { %>
															<% firstValue = false; %>															
															<td class='field'>
																<% if ("oil".equalsIgnoreCase(csvRecord.get(2))) { %>																	
																	<img width="20" height="20" src="/shs/resources/gfx/oil-drilling.jpg" />																	
																<% } else { %>
																	<img width="20" height="20" src="/shs/resources/gfx/gas-well.jpg" />																	
																<% } %>
																<%=value%><span class="green" id="<%=value%>" />
															</td>
															<script language="javascript">
																oilSids.push("<%=value%>");
															</script>
														<% } else { %>														
															<td class='field'><%=value%></td>
														<% } %>
                                                    <% } %>
                                                </tr>
                                            <% } %>
                                        </table>
                                    </div>
                                </td>
                                <td valign="top" colspan="2" class="boxed">
									<div id="windmillSensors">
                                        <table>
											<tr><td class="boxed" colspan="4">Windmill Farm Sensors &nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="windmillSensorSwitch" onclick="startWindmill()" /></td></tr>
                                            <tr><td class='headline'>System Id</td><td class='headline'>Farm Name</td><td class='headline'>Max MegaWatt</td><td class='headline'>State</td></tr>
                                            <%  List<CSVRecord> windmillList = windmillCsvParser.getRecords(); %>
                                            <% for (CSVRecord csvRecord: windmillList) { %>
                                                <% boolean firstValue = true; %>
												<tr>
                                                    <% for (String value: csvRecord) { %>
                                                        <% if (firstValue) { %>
															<% firstValue = false; %>
															<td class='field'>
																<img width="20" height="20" src="/shs/resources/gfx/windmill.jpg" />
																<%=value%><span class="green" id="<%=value%>" />
															</td>
																<script language="javascript">
																	windmillSids.push("<%=value%>");
																</script>
														<% } else { %>														
															<td class='field'><%=value%></td>
														<% } %>
                                                    <% } %>
                                                </tr>
                                            <% } %>
                                        </table>
                                    </div>
                                </td>                                
                            </tr>
                        </table>
                    </td>					
                </tr>
            </table>
        </div>

        <div id="tabs-2">
            <table>
                <tr>
                    <td class="boxed">
                        <img src="/shs/resources/gfx/icon-whitearrowright.png"><img src="/shs/resources/gfx/icon-whitearrowright.png"><img src="/shs/resources/gfx/icon-whitearrowright.png">
                        <b>DemoSensorData:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="tableStatusQ" />
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table>
                            <tr id="columnNameTrQ" />
                            <tr id="columnTypeTrQ" />
                        </table>
                    </td>
                </tr>
                <!-- Divider -->
                <tr><td colspan="2"><hr /></td></tr>
                <!-- Divider -->
                <tr>
                    <td class="boxed" colspan="2">
                        Query Xml:
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <textarea class="styled resizeVertical" id="queryXml"><%=data%></textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                        <table>
                            <tr>
                                <td colspan="2">
                                    <table>
                                        <tr>
                                            <td>
                                                <a class="sapButton" id="editAndSave" onClick="switchEditing()"><span id="editLabel">Edit Query</span></a>
                                            </td>
                                            <td>
                                                <a class="sapButton" id="queryBtn" title="Find the product rows with your query"><span>Preview Data</span></a>
                                            </td>
                                            <td class="middleField">
                                                <a class="sapButton" id="createHanaTableBtn" title="Load query data to HANA"><span>Sync to HANA...</span></a>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td id="reminder" class="leftText important" />
                </tr>
            </table>
        </div>
    </div>
	<div id="responseStatus" class="browserCenter" valign="top" />
	<div id="syncPrompt" class="browserCenter" valign="top" >
		<table>
			<tr>
				<td>
					Destination Table Name: 
				</td>
				<td>
					<input type="text" id="destTableName" />
				</td>
			</tr>			
			<tr>
				<td>
					Frequency in Seconds: 
				</td>
				<td>
					<input type="text" id="jobFrequencyInSeconds" value="Run Just Once" />
				</td>
			</tr>
			<tr>
				<td></td><td><a class="sapButton" id="syncJobBtn" onClick="setSyncJob()"><span>Set Recurring Job</span></a></td>
			</tr>
			<tr>
				<td></td><td><a class="sapButton" id="syncJobBtn" onClick="setSyncJob()"><span>Set Recurring Job</span></a></td>
			</tr>
		</table>
	</div>	
	<!--<span id="responseStatus" class="browserCenter"></span>-->
    <script>
        // Prepare the Ajax call for reloading the HDFS folder content
        $.ajaxSetup ({
            cache: false
        });
        /*global $ */
        $(function () {			
            $("#tabs").tabs();
            getTableSchema();
            $('#queryBtn').click(function() {
                if (!disableEditQuery) {
                    alert("Please save your query xml first.");
                    return;
                }
                window.open('showResult.jsp?xmlDataPath=' +
                        escape("/data/shield/requests/queryTable.xml") +
                        '&queryRestUrl=' + escape(queryRestUrl) +
                        '&loopRestUrl=' + escape(loopRestUrl), 'Query Result', '');
                return false;
            });

            $('#createHanaTableBtn').click(function(){
                if (!disableEditQuery) {
                    alert("Please save your query xml first.");
                    return;
                }

                var tableName = window.prompt("Please enter desired table name: ");
				var urlToOpen = 'getXsEngine.jsp?tableName=' + tableName +
                        '&xmlDataPath=' + escape("/data/shield/requests/queryTable.xml") +
                        '&queryRestUrl=' + escape(queryRestUrl);
                window.open(urlToOpen, 'Create a HANA Table', '');
                return false;
            });

            $("textarea#queryXml").attr("disabled", disableEditQuery);
            $("td#reminder").text(reminderText1);

            displayButtons();
			displaySwitchs();
			//getSensorSignalCounts();
        });
    </script>
</body>
</html>