// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import org.apache.tools.ant.taskdefs.email.Header;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.taskdefs.email.Mailer;
import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.mail.MailMessage;
import org.apache.tools.ant.util.StringUtils;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Hashtable;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class MailLogger extends DefaultLogger
{
    private StringBuffer buffer;
    private static final String DEFAULT_MIME_TYPE = "text/plain";
    
    public MailLogger() {
        this.buffer = new StringBuffer();
    }
    
    @Override
    public void buildFinished(final BuildEvent event) {
        super.buildFinished(event);
        final Project project = event.getProject();
        final Hashtable<String, Object> properties = project.getProperties();
        final Properties fileProperties = new Properties();
        final String filename = properties.get("MailLogger.properties.file");
        if (filename != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(filename);
                fileProperties.load(is);
            }
            catch (IOException ioe) {}
            finally {
                FileUtils.close(is);
            }
        }
        final Enumeration<?> e = fileProperties.keys();
        while (e.hasMoreElements()) {
            final String key = (String)e.nextElement();
            final String value = fileProperties.getProperty(key);
            properties.put(key, project.replaceProperties(value));
        }
        final boolean success = event.getException() == null;
        final String prefix = success ? "success" : "failure";
        try {
            final boolean notify = Project.toBoolean(this.getValue(properties, prefix + ".notify", "on"));
            if (!notify) {
                return;
            }
            final Values values = new Values().mailhost(this.getValue(properties, "mailhost", "localhost")).port(Integer.parseInt(this.getValue(properties, "port", String.valueOf(25)))).user(this.getValue(properties, "user", "")).password(this.getValue(properties, "password", "")).ssl(Project.toBoolean(this.getValue(properties, "ssl", "off"))).starttls(Project.toBoolean(this.getValue(properties, "starttls.enable", "off"))).from(this.getValue(properties, "from", null)).replytoList(this.getValue(properties, "replyto", "")).toList(this.getValue(properties, prefix + ".to", null)).mimeType(this.getValue(properties, "mimeType", "text/plain")).charset(this.getValue(properties, "charset", "")).body(this.getValue(properties, prefix + ".body", "")).subject(this.getValue(properties, prefix + ".subject", success ? "Build Success" : "Build Failure"));
            if (values.user().equals("") && values.password().equals("") && !values.ssl() && !values.starttls()) {
                this.sendMail(values, this.buffer.substring(0));
            }
            else {
                this.sendMimeMail(event.getProject(), values, this.buffer.substring(0));
            }
        }
        catch (Exception e2) {
            System.out.println("MailLogger failed to send e-mail!");
            e2.printStackTrace(System.err);
        }
    }
    
    @Override
    protected void log(final String message) {
        this.buffer.append(message).append(StringUtils.LINE_SEP);
    }
    
    private String getValue(final Hashtable<String, Object> properties, final String name, final String defaultValue) throws Exception {
        final String propertyName = "MailLogger." + name;
        String value = properties.get(propertyName);
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            throw new Exception("Missing required parameter: " + propertyName);
        }
        return value;
    }
    
    private void sendMail(final Values values, final String message) throws IOException {
        final MailMessage mailMessage = new MailMessage(values.mailhost(), values.port());
        mailMessage.setHeader("Date", DateUtils.getDateForHeader());
        mailMessage.from(values.from());
        if (!values.replytoList().equals("")) {
            final StringTokenizer t = new StringTokenizer(values.replytoList(), ", ", false);
            while (t.hasMoreTokens()) {
                mailMessage.replyto(t.nextToken());
            }
        }
        final StringTokenizer t = new StringTokenizer(values.toList(), ", ", false);
        while (t.hasMoreTokens()) {
            mailMessage.to(t.nextToken());
        }
        mailMessage.setSubject(values.subject());
        if (values.charset().length() > 0) {
            mailMessage.setHeader("Content-Type", values.mimeType() + "; charset=\"" + values.charset() + "\"");
        }
        else {
            mailMessage.setHeader("Content-Type", values.mimeType());
        }
        final PrintStream ps = mailMessage.getPrintStream();
        ps.println((values.body().length() > 0) ? values.body() : message);
        mailMessage.sendAndClose();
    }
    
    private void sendMimeMail(final Project project, final Values values, final String message) {
        Mailer mailer = null;
        try {
            mailer = (Mailer)ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.MimeMailer", MailLogger.class.getClassLoader(), Mailer.class);
        }
        catch (BuildException e) {
            final Throwable t = (e.getCause() == null) ? e : e.getCause();
            this.log("Failed to initialise MIME mail: " + t.getMessage());
            return;
        }
        final Vector<EmailAddress> replyToList = this.vectorizeEmailAddresses(values.replytoList());
        mailer.setHost(values.mailhost());
        mailer.setPort(values.port());
        mailer.setUser(values.user());
        mailer.setPassword(values.password());
        mailer.setSSL(values.ssl());
        mailer.setEnableStartTLS(values.starttls());
        final Message mymessage = new Message((values.body().length() > 0) ? values.body() : message);
        mymessage.setProject(project);
        mymessage.setMimeType(values.mimeType());
        if (values.charset().length() > 0) {
            mymessage.setCharset(values.charset());
        }
        mailer.setMessage(mymessage);
        mailer.setFrom(new EmailAddress(values.from()));
        mailer.setReplyToList(replyToList);
        final Vector<EmailAddress> toList = this.vectorizeEmailAddresses(values.toList());
        mailer.setToList(toList);
        mailer.setCcList(new Vector<EmailAddress>());
        mailer.setBccList(new Vector<EmailAddress>());
        mailer.setFiles(new Vector<File>());
        mailer.setSubject(values.subject());
        mailer.setHeaders(new Vector<Header>());
        mailer.send();
    }
    
    private Vector<EmailAddress> vectorizeEmailAddresses(final String listString) {
        final Vector<EmailAddress> emailList = new Vector<EmailAddress>();
        final StringTokenizer tokens = new StringTokenizer(listString, ",");
        while (tokens.hasMoreTokens()) {
            emailList.addElement(new EmailAddress(tokens.nextToken()));
        }
        return emailList;
    }
    
    private static class Values
    {
        private String mailhost;
        private int port;
        private String user;
        private String password;
        private boolean ssl;
        private String from;
        private String replytoList;
        private String toList;
        private String subject;
        private String charset;
        private String mimeType;
        private String body;
        private boolean starttls;
        
        public String mailhost() {
            return this.mailhost;
        }
        
        public Values mailhost(final String mailhost) {
            this.mailhost = mailhost;
            return this;
        }
        
        public int port() {
            return this.port;
        }
        
        public Values port(final int port) {
            this.port = port;
            return this;
        }
        
        public String user() {
            return this.user;
        }
        
        public Values user(final String user) {
            this.user = user;
            return this;
        }
        
        public String password() {
            return this.password;
        }
        
        public Values password(final String password) {
            this.password = password;
            return this;
        }
        
        public boolean ssl() {
            return this.ssl;
        }
        
        public Values ssl(final boolean ssl) {
            this.ssl = ssl;
            return this;
        }
        
        public String from() {
            return this.from;
        }
        
        public Values from(final String from) {
            this.from = from;
            return this;
        }
        
        public String replytoList() {
            return this.replytoList;
        }
        
        public Values replytoList(final String replytoList) {
            this.replytoList = replytoList;
            return this;
        }
        
        public String toList() {
            return this.toList;
        }
        
        public Values toList(final String toList) {
            this.toList = toList;
            return this;
        }
        
        public String subject() {
            return this.subject;
        }
        
        public Values subject(final String subject) {
            this.subject = subject;
            return this;
        }
        
        public String charset() {
            return this.charset;
        }
        
        public Values charset(final String charset) {
            this.charset = charset;
            return this;
        }
        
        public String mimeType() {
            return this.mimeType;
        }
        
        public Values mimeType(final String mimeType) {
            this.mimeType = mimeType;
            return this;
        }
        
        public String body() {
            return this.body;
        }
        
        public Values body(final String body) {
            this.body = body;
            return this;
        }
        
        public boolean starttls() {
            return this.starttls;
        }
        
        public Values starttls(final boolean starttls) {
            this.starttls = starttls;
            return this;
        }
    }
}
