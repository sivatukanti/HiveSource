// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.StringTokenizer;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import java.util.Vector;
import org.apache.tools.ant.Task;

public class EmailTask extends Task
{
    private static final int SMTP_PORT = 25;
    public static final String AUTO = "auto";
    public static final String MIME = "mime";
    public static final String UU = "uu";
    public static final String PLAIN = "plain";
    private String encoding;
    private String host;
    private Integer port;
    private String subject;
    private Message message;
    private boolean failOnError;
    private boolean includeFileNames;
    private String messageMimeType;
    private EmailAddress from;
    private Vector replyToList;
    private Vector toList;
    private Vector ccList;
    private Vector bccList;
    private Vector headers;
    private Path attachments;
    private String charset;
    private String user;
    private String password;
    private boolean ssl;
    private boolean starttls;
    private boolean ignoreInvalidRecipients;
    
    public EmailTask() {
        this.encoding = "auto";
        this.host = "localhost";
        this.port = null;
        this.subject = null;
        this.message = null;
        this.failOnError = true;
        this.includeFileNames = false;
        this.messageMimeType = null;
        this.from = null;
        this.replyToList = new Vector();
        this.toList = new Vector();
        this.ccList = new Vector();
        this.bccList = new Vector();
        this.headers = new Vector();
        this.attachments = null;
        this.charset = null;
        this.user = null;
        this.password = null;
        this.ssl = false;
        this.starttls = false;
        this.ignoreInvalidRecipients = false;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setSSL(final boolean ssl) {
        this.ssl = ssl;
    }
    
    public void setEnableStartTLS(final boolean b) {
        this.starttls = b;
    }
    
    public void setEncoding(final Encoding encoding) {
        this.encoding = encoding.getValue();
    }
    
    public void setMailport(final int port) {
        this.port = new Integer(port);
    }
    
    public void setMailhost(final String host) {
        this.host = host;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public void setMessage(final String message) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        (this.message = new Message(message)).setProject(this.getProject());
    }
    
    public void setMessageFile(final File file) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        (this.message = new Message(file)).setProject(this.getProject());
    }
    
    public void setMessageMimeType(final String type) {
        this.messageMimeType = type;
    }
    
    public void addMessage(final Message message) throws BuildException {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        this.message = message;
    }
    
    public void addFrom(final EmailAddress address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = address;
    }
    
    public void setFrom(final String address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = new EmailAddress(address);
    }
    
    public void addReplyTo(final EmailAddress address) {
        this.replyToList.add(address);
    }
    
    public void setReplyTo(final String address) {
        this.replyToList.add(new EmailAddress(address));
    }
    
    public void addTo(final EmailAddress address) {
        this.toList.addElement(address);
    }
    
    public void setToList(final String list) {
        final StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.toList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }
    
    public void addCc(final EmailAddress address) {
        this.ccList.addElement(address);
    }
    
    public void setCcList(final String list) {
        final StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.ccList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }
    
    public void addBcc(final EmailAddress address) {
        this.bccList.addElement(address);
    }
    
    public void setBccList(final String list) {
        final StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.bccList.addElement(new EmailAddress(tokens.nextToken()));
        }
    }
    
    public void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    public void setFiles(final String filenames) {
        final StringTokenizer t = new StringTokenizer(filenames, ", ");
        while (t.hasMoreTokens()) {
            this.createAttachments().add(new FileResource(this.getProject().resolveFile(t.nextToken())));
        }
    }
    
    public void addFileset(final FileSet fs) {
        this.createAttachments().add(fs);
    }
    
    public Path createAttachments() {
        if (this.attachments == null) {
            this.attachments = new Path(this.getProject());
        }
        return this.attachments.createPath();
    }
    
    public Header createHeader() {
        final Header h = new Header();
        this.headers.add(h);
        return h;
    }
    
    public void setIncludefilenames(final boolean includeFileNames) {
        this.includeFileNames = includeFileNames;
    }
    
    public boolean getIncludeFileNames() {
        return this.includeFileNames;
    }
    
    public void setIgnoreInvalidRecipients(final boolean b) {
        this.ignoreInvalidRecipients = b;
    }
    
    @Override
    public void execute() {
        final Message savedMessage = this.message;
        try {
            Mailer mailer = null;
            boolean autoFound = false;
            Label_0089: {
                if (!this.encoding.equals("mime")) {
                    if (!this.encoding.equals("auto") || autoFound) {
                        break Label_0089;
                    }
                }
                try {
                    Class.forName("javax.activation.DataHandler");
                    Class.forName("javax.mail.internet.MimeMessage");
                    mailer = (Mailer)ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.MimeMailer", EmailTask.class.getClassLoader(), Mailer.class);
                    autoFound = true;
                    this.log("Using MIME mail", 3);
                }
                catch (BuildException e) {
                    this.logBuildException("Failed to initialise MIME mail: ", e);
                }
            }
            if (!autoFound && (this.user != null || this.password != null) && (this.encoding.equals("uu") || this.encoding.equals("plain"))) {
                throw new BuildException("SMTP auth only possible with MIME mail");
            }
            if (!autoFound && (this.ssl || this.starttls) && (this.encoding.equals("uu") || this.encoding.equals("plain"))) {
                throw new BuildException("SSL and STARTTLS only possible with MIME mail");
            }
            Label_0261: {
                if (!this.encoding.equals("uu")) {
                    if (!this.encoding.equals("auto") || autoFound) {
                        break Label_0261;
                    }
                }
                try {
                    mailer = (Mailer)ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.UUMailer", EmailTask.class.getClassLoader(), Mailer.class);
                    autoFound = true;
                    this.log("Using UU mail", 3);
                }
                catch (BuildException e) {
                    this.logBuildException("Failed to initialise UU mail: ", e);
                }
            }
            if (this.encoding.equals("plain") || (this.encoding.equals("auto") && !autoFound)) {
                mailer = new PlainMailer();
                autoFound = true;
                this.log("Using plain mail", 3);
            }
            if (mailer == null) {
                throw new BuildException("Failed to initialise encoding: " + this.encoding);
            }
            if (this.message == null) {
                (this.message = new Message()).setProject(this.getProject());
            }
            if (this.from == null || this.from.getAddress() == null) {
                throw new BuildException("A from element is required");
            }
            if (this.toList.isEmpty() && this.ccList.isEmpty() && this.bccList.isEmpty()) {
                throw new BuildException("At least one of to, cc or bcc must be supplied");
            }
            if (this.messageMimeType != null) {
                if (this.message.isMimeTypeSpecified()) {
                    throw new BuildException("The mime type can only be specified in one location");
                }
                this.message.setMimeType(this.messageMimeType);
            }
            if (this.charset != null) {
                if (this.message.getCharset() != null) {
                    throw new BuildException("The charset can only be specified in one location");
                }
                this.message.setCharset(this.charset);
            }
            final Vector<File> files = new Vector<File>();
            if (this.attachments != null) {
                for (final Resource r : this.attachments) {
                    files.addElement(r.as(FileProvider.class).getFile());
                }
            }
            this.log("Sending email: " + this.subject, 2);
            this.log("From " + this.from, 3);
            this.log("ReplyTo " + this.replyToList, 3);
            this.log("To " + this.toList, 3);
            this.log("Cc " + this.ccList, 3);
            this.log("Bcc " + this.bccList, 3);
            mailer.setHost(this.host);
            if (this.port != null) {
                mailer.setPort(this.port);
                mailer.setPortExplicitlySpecified(true);
            }
            else {
                mailer.setPort(25);
                mailer.setPortExplicitlySpecified(false);
            }
            mailer.setUser(this.user);
            mailer.setPassword(this.password);
            mailer.setSSL(this.ssl);
            mailer.setEnableStartTLS(this.starttls);
            mailer.setMessage(this.message);
            mailer.setFrom(this.from);
            mailer.setReplyToList(this.replyToList);
            mailer.setToList(this.toList);
            mailer.setCcList(this.ccList);
            mailer.setBccList(this.bccList);
            mailer.setFiles(files);
            mailer.setSubject(this.subject);
            mailer.setTask(this);
            mailer.setIncludeFileNames(this.includeFileNames);
            mailer.setHeaders(this.headers);
            mailer.setIgnoreInvalidRecipients(this.ignoreInvalidRecipients);
            mailer.send();
            final int count = files.size();
            this.log("Sent email with " + count + " attachment" + ((count == 1) ? "" : "s"), 2);
        }
        catch (BuildException e2) {
            this.logBuildException("Failed to send email: ", e2);
            if (this.failOnError) {
                throw e2;
            }
        }
        catch (Exception e3) {
            this.log("Failed to send email: " + e3.getMessage(), 1);
            if (this.failOnError) {
                throw new BuildException(e3);
            }
        }
        finally {
            this.message = savedMessage;
        }
    }
    
    private void logBuildException(final String reason, final BuildException e) {
        final Throwable t = (e.getCause() == null) ? e : e.getCause();
        this.log(reason + t.getMessage(), 1);
    }
    
    public void setCharset(final String charset) {
        this.charset = charset;
    }
    
    public String getCharset() {
        return this.charset;
    }
    
    public static class Encoding extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "auto", "mime", "uu", "plain" };
        }
    }
}
