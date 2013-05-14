import org.apache.commons.csv.CSVParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The LogProcessor MapReduce class, the goal is to find what products have many "mind-changing" events
 */
public class LogProcessor {
    // The input file path on the HDFS, can be changed to be a pass-in parameter
    private static String INPUT_PATH = "/user/hadoop/2012SapHadoopCourse/data/access.log";
    // Employee's I Number for uniquely identify the running folder
    private static String MY_I_NUMBER;

    // The date format used in the Apache Access Log
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]");

    // The output format for the sessionization file
    private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public LogProcessor(String iNumber) {
        MY_I_NUMBER = iNumber;
    }

    /**
     * The Mapper class
     */
    public static class LogMapper extends Mapper<LongWritable, Text, Text, Text> {
        // static variables for reusing
        private static final Text MAP_OUT_KEY = new Text();
        private static final Text MAP_OUT_VALUE = new Text();
        private static final StringBuilder BUFFER = new StringBuilder();

        /**
         * To select only the valid lines (with product id) from the access log and use IP to identify every key-value
         * entry.  The value is a combined text of "timestamp" + "_" + "product asin"
         * @param inKey the line number of the access log
         * @param inValue the actual content of each line
         * @param context the object to write mapper's key-value pairs
         * @throws IOException if HDFS reading or writing exception occurred
         * @throws InterruptedException if the exccution is interrupted by anything
         */
        public void map(LongWritable inKey, Text inValue, Context context)
                throws IOException, InterruptedException {
            // Get the line content as a String
            String line = inValue.toString();
            // Parse each line into an AccessEntry object so picking different fields as the key-value pairs easier
            AccessEntry accessEntry = null;
            try {
                accessEntry = getAccessEntry(line);
            } catch (IOException ioe) {
                // Just print the exception since we didn't setup a logger for this example
                ioe.printStackTrace();
            }

            if (accessEntry != null) {
                String itemAsin = getItemAsin(accessEntry.getAttribute("resource"));
                // Only when a valid asin is found then we keep processing
                if (itemAsin != null) {
                    // Clean up the string buffer
                    BUFFER.setLength(0);
                    // the key-value pair looks like: "237.33.20.14" : "1324535665131_B004I7XY7C"
                    MAP_OUT_KEY.set(accessEntry.getAttribute("ip"));

                    BUFFER.append(accessEntry.getAttribute("timestamp")).append("_").append(itemAsin);
                    MAP_OUT_VALUE.set(BUFFER.toString());

                    // Write to the context, this is the mapper's output
                    context.write(MAP_OUT_KEY, MAP_OUT_VALUE);
                }
            }
        }
    }

    /**
     * The Reducer class
     */
    public static class LogReducer extends Reducer<Text, Text, Text, Text> {
        // static variables for reusing
        private static final Text REDUCE_OUT_KEY = new Text("");
        private static final Text REDUCE_OUT_VALUE = new Text();
        private static final StringBuilder BUFFER = new StringBuilder();

        private static TreeMap<Long, String> TIMED_ASINS = new TreeMap<Long, String>();

        public void reduce(Text inKey, Iterable<Text> inValues, Context context)
                throws IOException, InterruptedException {
            Iterator<Text> inValueIt = inValues.iterator();
            while (inValueIt.hasNext()) {
                // Find all timestamp +"_" + asin from the value list and use their timestamps as keys to get the asins
                // in order
                String[] values = inValueIt.next().toString().split("_");
                TIMED_ASINS.put(Long.parseLong(values[0]), values[1]);
            }

            // Use 30 minutes as the session length, all asins browsed in a session (can be longer than 30 minutes if a
            // user keeps having browsing activities, the session will keep extending until session is idle for 30
            // minutes)
            List<Session> sessions = getSessions(TIMED_ASINS, 30);
            TIMED_ASINS = new TreeMap<Long, String>();

            for (Session session : sessions) {
                BUFFER.setLength(0);
                if (session.timestamps.size() > 0) {
                    for (String itemLookup: session.itemAsins) {
                        BUFFER.append(itemLookup).append(",");
                    }
                    // Set the first product's datestamp at the end
                    BUFFER.append(SIMPLE_DATE_FORMAT.format(session.timestamps.get(0)));
                    REDUCE_OUT_VALUE.set(BUFFER.toString());
                    context.write(REDUCE_OUT_KEY, REDUCE_OUT_VALUE);
                }
            }
        }
    }

    public Job getMapReduceJob() throws Exception {
        final Configuration conf = new Configuration();

        // Set up personal job name and output directory
        String jobName = "myFirstMrJob by " + MY_I_NUMBER;
        String myOutputDirectory = "/user/hadoop/2012SapHadoopCourse/results/" + MY_I_NUMBER;
        Path myOutputPath = new Path(myOutputDirectory);

        // Delete the output directory if your personal directory does exist
        FileSystem fileSystem = myOutputPath.getFileSystem(conf);
        if (fileSystem.exists(myOutputPath)) {
            fileSystem.delete(myOutputPath, true);
        }

        // Create the job
        Job job = new Job(conf, jobName);

        // Tell Hadoop that the job class is in the jar specified from the command line
        job.setJarByClass(LogProcessor.class);

        // Set the mapper class
        job.setMapperClass(LogMapper.class);
        // Set the reducer class
        job.setReducerClass(LogReducer.class);

        // Tell FileInputFormat to read the log file
        FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
        // Tell FileOutputFormat to write result to your personal folder on HDFS
        FileOutputFormat.setOutputPath(job, myOutputPath);

        // Now set the TextInputFormat as our InputFormat class,
        // TextInputFormat is a subclass of FileInputFormat therefore it
        // inherits the InputPath we set to FileInputFormat
        job.setInputFormatClass(TextInputFormat.class);

        // Now set the TextOutputFormat as our OutputFormat class, again,
        // TextOutputFormat is a subclass of FileOutputFormat so the OutputPath
        // we set to FileOutputFormat will be used here
        job.setOutputFormatClass(TextOutputFormat.class);

        // Mapper's output key-value types
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer's output key-value types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        return job;
    }

    /**
     * The main method
     * @param arg the parameters from the command line
     * @throws Exception if the execution is interrupted by anything
     */
    public static void main(String[] arg) throws Exception {
        // The SAP I Number
        if (arg.length < 1) {
            System.out.println("Please specify your SAP I number.");
            System.exit(1);
        }
        LogProcessor myJob = new LogProcessor(arg[0]);
        // Wait for the job to complete
        myJob.getMapReduceJob().waitForCompletion(true);
    }

    // Construct an AccessEntry from a line in the log
    private static AccessEntry getAccessEntry(String line) throws IOException{
        AccessEntry accessEntry = null;
        try {
            // An AccessData object will be created for each line if possible
            accessEntry = new AccessEntry();
            // Parse the value separated line using space as the delimiter
            CSVParser csvParser = new CSVParser(new StringReader(line));
            csvParser.getStrategy().setDelimiter(' ');

            // Now get all the values from the line
            String[] values = csvParser.getLine();

            // Get the IP
            accessEntry.ip = values[0];

            // The time is split into 2 values so they have to be combined
            // then sent to match the time regular expression
            // "[02/Aug/2011:00:00:04" + " -0700]" = "[02/Aug/2011:00:00:04 -0700]"
            accessEntry.timestamp = new Timestamp(DATE_FORMAT.parse(values[3] + " " + values[4]).getTime());

            // The resource filed has 3 fields (HTTP Method, Page and HTTP protocol)
            // so it has to be further split by spaces
            String reqInfo = values[5];
            String[] reqInfoArr = reqInfo.split(" ");

            // Get the HTTP method
            accessEntry.method = reqInfoArr[0];

            // Get the page requested
            accessEntry.resource = reqInfoArr[1];

            // Get the HTTP response code
            accessEntry.httpCode = Integer.parseInt(values[6]);

            // Try to get the response data size in bytes, if a hyphen shows up,
            // that means the client has a cache of this page and no data is
            // sent back
            try {
                accessEntry.dataLength = Long.parseLong(values[7]);
            } catch (NumberFormatException nfe) {
                accessEntry.dataLength = 0;
            }

            if (values.length >= 9) {
                accessEntry.referrer = values[8];
            }

            if (values.length >= 10) {
                accessEntry.userAgent = values[9];
            }
            return accessEntry;
        } catch (Exception e) {
            IOException ioe = new IOException();
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    // Get the Item Asin number from a line in the log
    private static String getItemAsin(String line) {
        String key = "PPSID=";
        int dpIdx = line.indexOf("PPSID=");
        int idEnd = line.indexOf("&", dpIdx + 1);
        if (idEnd < 0) {
            line.indexOf("\"", dpIdx + 1);
            idEnd = line.length();
        }
        if (dpIdx >= 0) {
            return line.substring(dpIdx + key.length(), idEnd);
        }
        return null;
    }

    // The key method which is to get find sessions from the sorted lines in the log
    private static List<Session> getSessions(TreeMap<Long, String> sortedMap, int sessionInMin) {
        Session session = null;
        List<Session> sessionList = new LinkedList<Session>();
        long prevTs = -1l;
        long entryTs = -1l;
        long endTs = -1l;
        long length = sessionInMin * 60 * 1000;
        for (Map.Entry<Long, String> entry : sortedMap.entrySet()) {
            if (session == null) {
                session = new Session();
            }
            entryTs = entry.getKey();
            if (prevTs == -1l) {
                session.addItemAsin(entryTs, entry.getValue());
            } else {
                endTs = prevTs + length;
                if (entryTs <= endTs && entryTs > prevTs) {
                    // Within session
                    session.addItemAsin(entryTs, entry.getValue());
                } else {
                    sessionList.add(session);
                    session = new Session();
                    session.addItemAsin(entryTs, entry.getValue());

                }
            }
            prevTs = entryTs;
        }
        // Add the last session
        if (session.itemAsins.size() > 0) {
            sessionList.add(session);
        }
        return sessionList;
    }

    // Inner class
    private static class AccessEntry {
        public String ip;
        public Timestamp timestamp;
        public String method;
        public String resource;
        public int httpCode;
        public long dataLength;
        public String referrer;
        public String userAgent;

        public String getAttribute(String name) {
            if ("ip".equalsIgnoreCase(name)) {
                return ip;
            } else if ("timestamp".equalsIgnoreCase(name)) {
                return String.valueOf(timestamp.getTime());
            } else if ("method".equalsIgnoreCase(name)) {
                return method;
            } else if ("resource".equalsIgnoreCase(name)) {
                return resource;
            } else if ("httpCode".equalsIgnoreCase(name)) {
                return String.valueOf(httpCode);
            } else if ("dataLength".equalsIgnoreCase(name)) {
                return String.valueOf(dataLength);
            } else if ("referrer".equalsIgnoreCase(name)) {
                return String.valueOf(referrer);
            } else if ("userAgent".equalsIgnoreCase(name)) {
                return String.valueOf(userAgent);
            }

            return null;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("IP : ").append(ip).append("\n");
            sb.append("Timestamp : ").append(timestamp).append("\n");
            sb.append("Method : ").append(method).append("\n");
            sb.append("Resource : ").append(resource).append("\n");
            sb.append("HttpCode : ").append(httpCode).append("\n");
            sb.append("Length : ").append(dataLength).append("\n");
            sb.append("Referrer : ").append(referrer).append("\n");
            sb.append("UserAgent : ").append(userAgent).append("\n");
            return sb.toString();
        }
    }
    // Inner class
    private static class Session {
        List<Long> timestamps = new LinkedList<Long>();
        List<String> itemAsins = new LinkedList<String>();

        public void addItemAsin(Long ts, String itemLookup) {
            timestamps.add(ts);
            itemAsins.add(itemLookup);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < timestamps.size(); i++) {
                sb.append(timestamps.get(i)).append(":").append(itemAsins.get(i)).append("\n");
            }
            return sb.toString();
        }
    }
}