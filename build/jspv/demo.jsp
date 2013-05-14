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
    if ("true".equals(request.getParameter("refreshMovies")) || !AmazonMovie.isMovieReady()) {
        Utilities.refreshMovieRecommender(configurationManager, "/user/hadoop/taskForceData/amazonMovieDemo/", true);
    }
    int movieIndex = 0;
    String selectedOne = "selected";
%>
<html>
<head>
    <script language="javascript" src="../resources/js/jquery.js"></script>
    <script language="javascript" src="../resources/js/common.js"></script>
    <script language="javascript" src="../resources/js/jquery-ui.custom.js"></script>
    <script language="javascript" src="../resources/js/jshashtable-2.1.js"></script>
    <script language="javascript" src="../resources/js/jquery.rating.pack.js"></script>
    <script language="javascript" src='../resources/js/jquery.MetaData.js'></script>
    <script language="javascript" src='../resources/js/jquery.countdown.pack.js'></script>
    <link rel="stylesheet" href="../resources/css/<%=theme%>/jquery-ui-custom.css" type="text/css">
    <link rel="stylesheet" href="../resources/css/common.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/jquery.rating.css" type="text/css"/>
    <link rel="stylesheet" href="../resources/css/demo.css" type="text/css"/>
</head>
<body>
    <div id="tabs">
        <ul>
            <li><a href="#tabs-6">Mahout Demo 1</a></li>
        </ul>
        <div id="tabs-6">
            <script type="text/javascript">
                var movieIdNameHashtable = new Hashtable();
                var userRatingHashtable = new Hashtable();
            </script>
            <table>
                <tr>
                    <td>
                        <a class='sapButton' id='movieRecommenderButton' onclick='runMovieRecommenderJob()'><span>Run Recommender</span></a>
                    </td>
                    <td align="right" width=648>
                        <div class=normal id="logout" />
                    </td>
                </tr>
                <tr>
                    <td align=center valign=top>
                        <span id='askToRate' class='text'>Please rate 10 movies: <span class='alert'>0/10</span></span>
                        <div id='movieRecommendationNotification' class='notification' onclick="toggleMovieList()"><span id="minimal"></span></div>
                        <div id='movieRecommendationContent' class='result'></div>
                        <div class="styled-select">
                            <select id='movieSelector' onChange='populateMoviePreviewAndRating()' multiple='multiple' size='47'>
                                <% for (AmazonMovie movie: AmazonMovie.movies()) { %>
                                    <% if (movieIndex > 0) { selectedOne = ""; } %>
                                    <option id='movieOption<%=movie.getId()%>' value='<%=movie.getId()%>' <%=selectedOne%>><%=movie.getShortName(30, false)%></option>
                                    <script type="text/javascript">
                                        movieIdNameHashtable.put('<%=movie.getId()%>', '<%=movie.getShortName(30, true)%>');
                                    </script>
                                    <% movieIndex++; %>
                                <% } %>
                            </select>
                        </div>
                    </td>
                    <td>
                        <table>
                            <tr>
                                <td>
                                    <span class='text'>How do you rate this movie?&nbsp;&nbsp;&nbsp;</span>
                                </td>
                                <td>
                                    <span>
                                        <input id='oneStar' name='star' type='radio' class='star' value='1 star' />
                                        <input id='twoStar' name='star' type='radio' class='star' value='2 star' />
                                        <input id='threeStar' name='star' type='radio' class='star' value='3 star' />
                                        <input id='fourStar' name='star' type='radio' class='star' value='4 star' />
                                        <input id='fiveStar' name='star' type='radio' class='star' value='5 star' />
                                        <script type="text/javascript">
                                            $('.star').rating({
                                                callback:
                                                        function(value, link){
                                                            setScore(value.charAt(0));
                                                        },
                                                showCancel: true,
                                                cancelValue: ''
                                            });
                                        </script>
                                    </span>
                                </td>
                            </tr>
                        </table>
                        <div id="movieRating"></div>
                        <div id="moviePreview"></div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <script language="javascript" src="../resources/js/demo.js"></script>
    <script>
        // Prepare the Ajax call for reloading the HDFS folder content
        $.ajaxSetup ({
            cache: false
        });
        var listHdfsUrl = "/shs/js/hfl";
        var parseJarUrl = "/shs/js/jjj";
        var submitJobUrl = "/shs/js/sss";
        var jobStatusUrl = "/shs/js/ggg";
        var jobHistoryUrl = "/shs/js/hhh";
        var hdfsPersonFolder = "<%=hdfsPersonFolder%>";
        var deleteFileUrl = "/shs/js/ddd";
        var downloadFileUrl = "/shs/js/dwn";
        var createFolderUrl = "/shs/js/mkdirs";
        var nowFolder = "<%=hdfsPersonFolder%>";
        var submitPluginUrl = "/shs/js/runPlugin";
        var movieRecommenderUrl = "/shs/js/mr";
        var movieHtmlUrl = "/shs/js/movie";

        /*global $ */
        $(function () {
            $("div#logout").html('<span class="rightText">Hi, <%=employeeId%>  |  <a href="/shs/jsp/login.jsp" title="logoff">Logout</a></span>');
            $("#tabs").tabs();
            $("div#movieRecommendationNotification").hide();
            $("div#movieRecommendationContent").hide();
            // Load the movie preview
            populateMoviePreviewAndRating();
        });

    </script>
</body>
</html>
