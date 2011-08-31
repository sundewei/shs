package com.sap.shs;

import com.sap.hadoop.conf.ConfigurationManager;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: I827779
 * Date: 8/29/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginPass {
    private String username;
    private String password;
    private ConfigurationManager configurationManager;
    private String messageDigestedPassword;
    private boolean validPass;
    private boolean loadFromCookie;
    private boolean loadFromRequest;

    public boolean isLoadFromCookie() {
        return loadFromCookie;
    }

    public void setLoadFromCookie(boolean loadFromCookie) {
        this.loadFromCookie = loadFromCookie;
    }

    public boolean isLoadFromRequest() {
        return loadFromRequest;
    }

    public void setLoadFromRequest(boolean loadFromRequest) {
        this.loadFromRequest = loadFromRequest;
    }

    public LoginPass validate() {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(messageDigestedPassword)) {
            validPass = false;
        } else {
            if (configurationManager == null) {
                configurationManager = new ConfigurationManager(username, password);
            }
            validPass = true;
        }
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public String getMessageDigestedPassword() {
        return messageDigestedPassword;
    }

    public void setMessageDigestedPassword(String messageDigestedPassword) {
        this.messageDigestedPassword = messageDigestedPassword;
    }

    public boolean isValidPass() {
        return validPass;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("username=").append(username).append("\n");
        sb.append("password=").append(password).append("\n");
        sb.append("messageDigestedPassword=").append(messageDigestedPassword).append("\n");
        sb.append("validPass=").append(validPass).append("\n");
        sb.append("loadFromRequest=").append(loadFromRequest).append("\n");
        sb.append("loadFromCookie=").append(loadFromCookie).append("\n");
        sb.append("configurationManager=").append(configurationManager).append("\n\n");
        return sb.toString();
    }
}
