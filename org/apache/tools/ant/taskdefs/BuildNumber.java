// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class BuildNumber extends Task
{
    private static final String DEFAULT_PROPERTY_NAME = "build.number";
    private static final String DEFAULT_FILENAME = "build.number";
    private static final FileUtils FILE_UTILS;
    private File myFile;
    
    public void setFile(final File file) {
        this.myFile = file;
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedFile = this.myFile;
        this.validate();
        final Properties properties = this.loadProperties();
        final int buildNumber = this.getBuildNumber(properties);
        properties.put("build.number", String.valueOf(buildNumber + 1));
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(this.myFile);
            final String header = "Build Number for ANT. Do not edit!";
            properties.store(output, "Build Number for ANT. Do not edit!");
        }
        catch (IOException ioe) {
            final String message = "Error while writing " + this.myFile;
            throw new BuildException(message, ioe);
        }
        finally {
            if (null != output) {
                try {
                    output.close();
                }
                catch (IOException ioe2) {
                    this.log("error closing output stream " + ioe2, 0);
                }
            }
            this.myFile = savedFile;
        }
        this.getProject().setNewProperty("build.number", String.valueOf(buildNumber));
    }
    
    private int getBuildNumber(final Properties properties) throws BuildException {
        final String buildNumber = properties.getProperty("build.number", "0").trim();
        try {
            return Integer.parseInt(buildNumber);
        }
        catch (NumberFormatException nfe) {
            final String message = this.myFile + " contains a non integer build number: " + buildNumber;
            throw new BuildException(message, nfe);
        }
    }
    
    private Properties loadProperties() throws BuildException {
        FileInputStream input = null;
        try {
            final Properties properties = new Properties();
            input = new FileInputStream(this.myFile);
            properties.load(input);
            return properties;
        }
        catch (IOException ioe) {
            throw new BuildException(ioe);
        }
        finally {
            if (null != input) {
                try {
                    input.close();
                }
                catch (IOException ioe2) {
                    this.log("error closing input stream " + ioe2, 0);
                }
            }
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.myFile) {
            this.myFile = BuildNumber.FILE_UTILS.resolveFile(this.getProject().getBaseDir(), "build.number");
        }
        if (!this.myFile.exists()) {
            try {
                BuildNumber.FILE_UTILS.createNewFile(this.myFile);
            }
            catch (IOException ioe) {
                final String message = this.myFile + " doesn't exist and new file can't be created.";
                throw new BuildException(message, ioe);
            }
        }
        if (!this.myFile.canRead()) {
            final String message2 = "Unable to read from " + this.myFile + ".";
            throw new BuildException(message2);
        }
        if (!this.myFile.canWrite()) {
            final String message2 = "Unable to write to " + this.myFile + ".";
            throw new BuildException(message2);
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
