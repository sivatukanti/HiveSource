// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail.internet;

import javax.mail.Part;
import java.net.UnknownServiceException;
import java.io.OutputStream;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import javax.mail.MessageContext;
import javax.mail.MessageAware;
import javax.activation.DataSource;

public class MimePartDataSource implements DataSource, MessageAware
{
    protected MimePart part;
    private MessageContext context;
    private static boolean ignoreMultipartEncoding;
    
    public MimePartDataSource(final MimePart part) {
        this.part = part;
    }
    
    public InputStream getInputStream() throws IOException {
        try {
            InputStream is;
            if (this.part instanceof MimeBodyPart) {
                is = ((MimeBodyPart)this.part).getContentStream();
            }
            else {
                if (!(this.part instanceof MimeMessage)) {
                    throw new MessagingException("Unknown part");
                }
                is = ((MimeMessage)this.part).getContentStream();
            }
            final String encoding = restrictEncoding(this.part.getEncoding(), this.part);
            if (encoding != null) {
                return MimeUtility.decode(is, encoding);
            }
            return is;
        }
        catch (MessagingException mex) {
            throw new IOException(mex.getMessage());
        }
    }
    
    private static String restrictEncoding(final String encoding, final MimePart part) throws MessagingException {
        if (!MimePartDataSource.ignoreMultipartEncoding || encoding == null) {
            return encoding;
        }
        if (encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit") || encoding.equalsIgnoreCase("binary")) {
            return encoding;
        }
        final String type = part.getContentType();
        if (type == null) {
            return encoding;
        }
        try {
            final ContentType cType = new ContentType(type);
            if (cType.match("multipart/*") || cType.match("message/*")) {
                return null;
            }
        }
        catch (ParseException ex) {}
        return encoding;
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException();
    }
    
    public String getContentType() {
        try {
            return this.part.getContentType();
        }
        catch (MessagingException mex) {
            return "application/octet-stream";
        }
    }
    
    public String getName() {
        try {
            if (this.part instanceof MimeBodyPart) {
                return ((MimeBodyPart)this.part).getFileName();
            }
        }
        catch (MessagingException ex) {}
        return "";
    }
    
    public synchronized MessageContext getMessageContext() {
        if (this.context == null) {
            this.context = new MessageContext(this.part);
        }
        return this.context;
    }
    
    static {
        MimePartDataSource.ignoreMultipartEncoding = true;
        try {
            final String s = System.getProperty("mail.mime.ignoremultipartencoding");
            MimePartDataSource.ignoreMultipartEncoding = (s == null || !s.equalsIgnoreCase("false"));
        }
        catch (SecurityException ex) {}
    }
}
