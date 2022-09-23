// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.mail.MethodNotSupportedException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

public class SunV3BodyPart extends MimeBodyPart
{
    public SunV3BodyPart(final InternetHeaders headers, final byte[] content) throws MessagingException {
        super(headers, content);
    }
    
    public int getSize() throws MessagingException {
        final String s = this.getHeader("X-Sun-Content-Length", null);
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    public int getLineCount() throws MessagingException {
        final String s = this.getHeader("X-Sun-Content-Lines", null);
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    public String getContentType() throws MessagingException {
        String ct = this.getHeader("Content-Type", null);
        if (ct == null) {
            ct = this.getHeader("X-Sun-Data-Type", null);
        }
        if (ct == null) {
            ct = "text/plain";
        }
        else if (ct.indexOf(47) < 0) {
            ct = MimeV3Map.toMime(ct);
        }
        return ct;
    }
    
    public String getEncoding() throws MessagingException {
        String enc = super.getEncoding();
        if (enc == null) {
            enc = this.getHeader("X-Sun-Encoding-Info", null);
        }
        return enc;
    }
    
    public String getDescription() throws MessagingException {
        String desc = super.getDescription();
        if (desc == null) {
            desc = this.getHeader("X-Sun-Data-Description", null);
        }
        return desc;
    }
    
    public void setDescription(final String description) throws MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart not writable");
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart not writable");
    }
    
    public String getFileName() throws MessagingException {
        String name = super.getFileName();
        if (name == null) {
            name = this.getHeader("X-Sun-Data-Name", null);
        }
        return name;
    }
    
    public void setFileName(final String filename) throws MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart not writable");
    }
    
    public void setDataHandler(final DataHandler dh) throws MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart not writable");
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart writeTo");
    }
    
    protected void updateHeaders() throws MessagingException {
        throw new MethodNotSupportedException("SunV3BodyPart updateHeaders");
    }
    
    static class MimeV3Map
    {
        String mime;
        String v3;
        private static MimeV3Map[] MimeV3Table;
        
        MimeV3Map(final String mime, final String v3) {
            this.mime = mime;
            this.v3 = v3;
        }
        
        static String toMime(final String s) {
            for (int i = 0; i < MimeV3Map.MimeV3Table.length; ++i) {
                if (MimeV3Map.MimeV3Table[i].v3.equalsIgnoreCase(s)) {
                    return MimeV3Map.MimeV3Table[i].mime;
                }
            }
            return "application/x-" + s;
        }
        
        static String toV3(final String s) {
            for (int i = 0; i < MimeV3Map.MimeV3Table.length; ++i) {
                if (MimeV3Map.MimeV3Table[i].mime.equalsIgnoreCase(s)) {
                    return MimeV3Map.MimeV3Table[i].v3;
                }
            }
            return s;
        }
        
        static {
            MimeV3Map.MimeV3Table = new MimeV3Map[] { new MimeV3Map("text/plain", "text"), new MimeV3Map("text/plain", "default"), new MimeV3Map("multipart/x-sun-attachment", "X-sun-attachment"), new MimeV3Map("application/postscript", "postscript-file"), new MimeV3Map("image/gif", "gif-file") };
        }
    }
}
