package com.sap.shs;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 4/13/11
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassResourceBean {
    private String filename;
    private String fullFilename;
    private List<String> resourceEntries;

    public String getFilename() {
        return filename;
    }

    public String getFullFilename() {
        return fullFilename;
    }

    public void setFullFilename(String fullFilename) {
        this.fullFilename = fullFilename;

        int lastSlashIdx = fullFilename.lastIndexOf(File.separator);

        if (lastSlashIdx > 0) {
            filename = fullFilename.substring(lastSlashIdx + 1);
        } else {
            filename = fullFilename;
        }
    }

    public List<String> getResourceEntries() {
        return resourceEntries;
    }

    public void setResourceEntries(List<String> resourceEntries) {
        this.resourceEntries = resourceEntries;
    }
}
