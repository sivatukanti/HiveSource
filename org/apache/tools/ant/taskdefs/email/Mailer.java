// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import java.io.File;
import java.util.Vector;

public abstract class Mailer
{
    protected String host;
    protected int port;
    protected String user;
    protected String password;
    protected boolean SSL;
    protected Message message;
    protected EmailAddress from;
    protected Vector<EmailAddress> replyToList;
    protected Vector<EmailAddress> toList;
    protected Vector<EmailAddress> ccList;
    protected Vector<EmailAddress> bccList;
    protected Vector<File> files;
    protected String subject;
    protected Task task;
    protected boolean includeFileNames;
    protected Vector<Header> headers;
    private boolean ignoreInvalidRecipients;
    private boolean starttls;
    private boolean portExplicitlySpecified;
    
    public Mailer() {
        this.host = null;
        this.port = -1;
        this.user = null;
        this.password = null;
        this.SSL = false;
        this.replyToList = null;
        this.toList = null;
        this.ccList = null;
        this.bccList = null;
        this.files = null;
        this.subject = null;
        this.includeFileNames = false;
        this.headers = null;
        this.ignoreInvalidRecipients = false;
        this.starttls = false;
        this.portExplicitlySpecified = false;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setPortExplicitlySpecified(final boolean explicit) {
        this.portExplicitlySpecified = explicit;
    }
    
    protected boolean isPortExplicitlySpecified() {
        return this.portExplicitlySpecified;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setSSL(final boolean ssl) {
        this.SSL = ssl;
    }
    
    public void setEnableStartTLS(final boolean b) {
        this.starttls = b;
    }
    
    protected boolean isStartTLSEnabled() {
        return this.starttls;
    }
    
    public void setMessage(final Message m) {
        this.message = m;
    }
    
    public void setFrom(final EmailAddress from) {
        this.from = from;
    }
    
    public void setReplyToList(final Vector<EmailAddress> list) {
        this.replyToList = list;
    }
    
    public void setToList(final Vector<EmailAddress> list) {
        this.toList = list;
    }
    
    public void setCcList(final Vector<EmailAddress> list) {
        this.ccList = list;
    }
    
    public void setBccList(final Vector<EmailAddress> list) {
        this.bccList = list;
    }
    
    public void setFiles(final Vector<File> files) {
        this.files = files;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public void setTask(final Task task) {
        this.task = task;
    }
    
    public void setIncludeFileNames(final boolean b) {
        this.includeFileNames = b;
    }
    
    public void setHeaders(final Vector<Header> v) {
        this.headers = v;
    }
    
    public abstract void send() throws BuildException;
    
    public void setIgnoreInvalidRecipients(final boolean b) {
        this.ignoreInvalidRecipients = b;
    }
    
    protected boolean shouldIgnoreInvalidRecipients() {
        return this.ignoreInvalidRecipients;
    }
    
    protected final String getDate() {
        return DateUtils.getDateForHeader();
    }
}
