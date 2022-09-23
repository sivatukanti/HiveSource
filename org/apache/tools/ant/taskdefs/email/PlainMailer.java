// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.io.File;
import org.apache.tools.ant.BuildException;
import java.io.IOException;
import org.apache.tools.mail.MailMessage;

class PlainMailer extends Mailer
{
    @Override
    public void send() {
        try {
            final MailMessage mailMessage = new MailMessage(this.host, this.port);
            mailMessage.from(this.from.toString());
            boolean atLeastOneRcptReached = false;
            Enumeration e = this.replyToList.elements();
            while (e.hasMoreElements()) {
                mailMessage.replyto(e.nextElement().toString());
            }
            e = this.toList.elements();
            while (e.hasMoreElements()) {
                final String to = e.nextElement().toString();
                try {
                    mailMessage.to(to);
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(to, ex);
                }
            }
            e = this.ccList.elements();
            while (e.hasMoreElements()) {
                final String to = e.nextElement().toString();
                try {
                    mailMessage.cc(to);
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(to, ex);
                }
            }
            e = this.bccList.elements();
            while (e.hasMoreElements()) {
                final String to = e.nextElement().toString();
                try {
                    mailMessage.bcc(to);
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(to, ex);
                }
            }
            if (!atLeastOneRcptReached) {
                throw new BuildException("Couldn't reach any recipient");
            }
            if (this.subject != null) {
                mailMessage.setSubject(this.subject);
            }
            mailMessage.setHeader("Date", this.getDate());
            if (this.message.getCharset() != null) {
                mailMessage.setHeader("Content-Type", this.message.getMimeType() + "; charset=\"" + this.message.getCharset() + "\"");
            }
            else {
                mailMessage.setHeader("Content-Type", this.message.getMimeType());
            }
            if (this.headers != null) {
                e = this.headers.elements();
                while (e.hasMoreElements()) {
                    final Header h = e.nextElement();
                    mailMessage.setHeader(h.getName(), h.getValue());
                }
            }
            final PrintStream out = mailMessage.getPrintStream();
            this.message.print(out);
            e = this.files.elements();
            while (e.hasMoreElements()) {
                this.attach(e.nextElement(), out);
            }
            mailMessage.sendAndClose();
        }
        catch (IOException ioe) {
            throw new BuildException("IO error sending mail", ioe);
        }
    }
    
    protected void attach(final File file, final PrintStream out) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"" + file.getName() + "\" does not exist or is not " + "readable.");
        }
        if (this.includeFileNames) {
            out.println();
            final String filename = file.getName();
            final int filenamelength = filename.length();
            out.println(filename);
            for (int star = 0; star < filenamelength; ++star) {
                out.print('=');
            }
            out.println();
        }
        final int maxBuf = 1024;
        final byte[] buf = new byte[1024];
        final FileInputStream finstr = new FileInputStream(file);
        try {
            final BufferedInputStream in = new BufferedInputStream(finstr, buf.length);
            int length;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        }
        finally {
            finstr.close();
        }
    }
    
    private void badRecipient(final String rcpt, final IOException reason) {
        String msg = "Failed to send mail to " + rcpt;
        if (this.shouldIgnoreInvalidRecipients()) {
            msg = msg + " because of :" + reason.getMessage();
            if (this.task != null) {
                this.task.log(msg, 1);
            }
            else {
                System.err.println(msg);
            }
            return;
        }
        throw new BuildException(msg, reason);
    }
}
