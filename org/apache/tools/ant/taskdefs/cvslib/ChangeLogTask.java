// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.util.FileUtils;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Enumeration;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.apache.tools.ant.types.FileSet;
import java.util.Date;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class ChangeLogTask extends AbstractCvsTask
{
    private File usersFile;
    private Vector cvsUsers;
    private File inputDir;
    private File destFile;
    private Date startDate;
    private Date endDate;
    private boolean remote;
    private String startTag;
    private String endTag;
    private final Vector filesets;
    
    public ChangeLogTask() {
        this.cvsUsers = new Vector();
        this.remote = false;
        this.filesets = new Vector();
    }
    
    public void setDir(final File inputDir) {
        this.inputDir = inputDir;
    }
    
    public void setDestfile(final File destFile) {
        this.destFile = destFile;
    }
    
    public void setUsersfile(final File usersFile) {
        this.usersFile = usersFile;
    }
    
    public void addUser(final CvsUser user) {
        this.cvsUsers.addElement(user);
    }
    
    public void setStart(final Date start) {
        this.startDate = start;
    }
    
    public void setEnd(final Date endDate) {
        this.endDate = endDate;
    }
    
    public void setDaysinpast(final int days) {
        final long time = System.currentTimeMillis() - days * 24L * 60L * 60L * 1000L;
        this.setStart(new Date(time));
    }
    
    public void setRemote(final boolean remote) {
        this.remote = remote;
    }
    
    public void setStartTag(final String start) {
        this.startTag = start;
    }
    
    public void setEndTag(final String end) {
        this.endTag = end;
    }
    
    public void addFileset(final FileSet fileSet) {
        this.filesets.addElement(fileSet);
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedDir = this.inputDir;
        try {
            this.validate();
            final Properties userList = new Properties();
            this.loadUserlist(userList);
            for (int size = this.cvsUsers.size(), i = 0; i < size; ++i) {
                final CvsUser user = this.cvsUsers.get(i);
                user.validate();
                userList.put(user.getUserID(), user.getDisplayname());
            }
            if (!this.remote) {
                this.setCommand("log");
                if (this.getTag() != null) {
                    final CvsVersion myCvsVersion = new CvsVersion();
                    myCvsVersion.setProject(this.getProject());
                    myCvsVersion.setTaskName("cvsversion");
                    myCvsVersion.setCvsRoot(this.getCvsRoot());
                    myCvsVersion.setCvsRsh(this.getCvsRsh());
                    myCvsVersion.setPassfile(this.getPassFile());
                    myCvsVersion.setDest(this.inputDir);
                    myCvsVersion.execute();
                    if (myCvsVersion.supportsCvsLogWithSOption()) {
                        this.addCommandArgument("-S");
                    }
                }
            }
            else {
                this.setCommand("");
                this.addCommandArgument("rlog");
                this.addCommandArgument("-S");
                this.addCommandArgument("-N");
            }
            if (null != this.startTag || null != this.endTag) {
                final String startValue = (this.startTag == null) ? "" : this.startTag;
                final String endValue = (this.endTag == null) ? "" : this.endTag;
                this.addCommandArgument("-r" + startValue + "::" + endValue);
            }
            else if (null != this.startDate) {
                final SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");
                final String dateRange = ">=" + outputDate.format(this.startDate);
                this.addCommandArgument("-d");
                this.addCommandArgument(dateRange);
            }
            if (!this.filesets.isEmpty()) {
                final Enumeration e = this.filesets.elements();
                while (e.hasMoreElements()) {
                    final FileSet fileSet = e.nextElement();
                    final DirectoryScanner scanner = fileSet.getDirectoryScanner(this.getProject());
                    final String[] files = scanner.getIncludedFiles();
                    for (int j = 0; j < files.length; ++j) {
                        this.addCommandArgument(files[j]);
                    }
                }
            }
            final ChangeLogParser parser = new ChangeLogParser(this.remote, this.getPackage(), this.getModules());
            final RedirectingStreamHandler handler = new RedirectingStreamHandler(parser);
            this.log(this.getCommand(), 3);
            this.setDest(this.inputDir);
            this.setExecuteStreamHandler(handler);
            try {
                super.execute();
            }
            finally {
                final String errors = handler.getErrors();
                if (null != errors) {
                    this.log(errors, 0);
                }
            }
            final CVSEntry[] entrySet = parser.getEntrySetAsArray();
            final CVSEntry[] filteredEntrySet = this.filterEntrySet(entrySet);
            this.replaceAuthorIdWithName(userList, filteredEntrySet);
            this.writeChangeLog(filteredEntrySet);
        }
        finally {
            this.inputDir = savedDir;
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.inputDir) {
            this.inputDir = this.getProject().getBaseDir();
        }
        if (null == this.destFile) {
            final String message = "Destfile must be set.";
            throw new BuildException("Destfile must be set.");
        }
        if (!this.inputDir.exists()) {
            final String message = "Cannot find base dir " + this.inputDir.getAbsolutePath();
            throw new BuildException(message);
        }
        if (null != this.usersFile && !this.usersFile.exists()) {
            final String message = "Cannot find user lookup list " + this.usersFile.getAbsolutePath();
            throw new BuildException(message);
        }
        if ((null != this.startTag || null != this.endTag) && (null != this.startDate || null != this.endDate)) {
            final String message = "Specify either a tag or date range, not both";
            throw new BuildException("Specify either a tag or date range, not both");
        }
    }
    
    private void loadUserlist(final Properties userList) throws BuildException {
        if (null != this.usersFile) {
            try {
                userList.load(new FileInputStream(this.usersFile));
            }
            catch (IOException ioe) {
                throw new BuildException(ioe.toString(), ioe);
            }
        }
    }
    
    private CVSEntry[] filterEntrySet(final CVSEntry[] entrySet) {
        final Vector results = new Vector();
        for (int i = 0; i < entrySet.length; ++i) {
            final CVSEntry cvsEntry = entrySet[i];
            final Date date = cvsEntry.getDate();
            if (null != date) {
                if (null == this.startDate || !this.startDate.after(date)) {
                    if (null == this.endDate || !this.endDate.before(date)) {
                        results.addElement(cvsEntry);
                    }
                }
            }
        }
        final CVSEntry[] resultArray = new CVSEntry[results.size()];
        results.copyInto(resultArray);
        return resultArray;
    }
    
    private void replaceAuthorIdWithName(final Properties userList, final CVSEntry[] entrySet) {
        for (int i = 0; i < entrySet.length; ++i) {
            final CVSEntry entry = entrySet[i];
            if (userList.containsKey(entry.getAuthor())) {
                entry.setAuthor(userList.getProperty(entry.getAuthor()));
            }
        }
    }
    
    private void writeChangeLog(final CVSEntry[] entrySet) throws BuildException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(this.destFile);
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));
            final ChangeLogWriter serializer = new ChangeLogWriter();
            serializer.printChangeLog(writer, entrySet);
            if (writer.checkError()) {
                throw new IOException("Encountered an error writing changelog");
            }
        }
        catch (UnsupportedEncodingException uee) {
            this.getProject().log(uee.toString(), 0);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString(), ioe);
        }
        finally {
            FileUtils.close(output);
        }
    }
}
