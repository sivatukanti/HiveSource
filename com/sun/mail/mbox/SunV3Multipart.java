// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.internet.InternetHeaders;
import com.sun.mail.util.LineInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.mail.BodyPart;
import javax.mail.MethodNotSupportedException;
import javax.mail.MessagingException;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;

public class SunV3Multipart extends MimeMultipart
{
    private static final String boundary = "----------";
    
    public SunV3Multipart(final DataSource ds) throws MessagingException {
        super(ds);
    }
    
    public void setSubType(final String subtype) throws MessagingException {
        throw new MethodNotSupportedException("can't change SunV3Multipart subtype");
    }
    
    public synchronized BodyPart getBodyPart(final String CID) throws MessagingException {
        throw new MethodNotSupportedException("SunV3Multipart doesn't support Content-ID");
    }
    
    protected void updateHeaders() throws MessagingException {
        throw new MethodNotSupportedException("SunV3Multipart not writable");
    }
    
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        throw new MethodNotSupportedException("SunV3Multipart writeTo not supported");
    }
    
    protected synchronized void parse() throws MessagingException {
        if (this.parsed) {
            return;
        }
        InputStream in = null;
        try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream) && !(in instanceof BufferedInputStream)) {
                in = new BufferedInputStream(in);
            }
        }
        catch (Exception ex) {
            throw new MessagingException("No inputstream from datasource");
        }
        final int bl = "----------".length();
        final byte[] bndbytes = new byte[bl];
        "----------".getBytes(0, bl, bndbytes, 0);
        try {
            final LineInputStream lin = new LineInputStream(in);
            String line;
            while ((line = lin.readLine()) != null && !line.trim().equals("----------")) {}
            if (line == null) {
                throw new MessagingException("Missing start boundary");
            }
            int b;
            do {
                final InternetHeaders headers = new InternetHeaders(in);
                if (!in.markSupported()) {
                    throw new MessagingException("Stream doesn't support mark");
                }
                final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                while ((b = in.read()) >= 0) {
                    if (b == 13 || b == 10) {
                        in.mark(bl + 4 + 1);
                        if (b == 13 && in.read() != 10) {
                            in.reset();
                            in.mark(bl + 4);
                        }
                        int i;
                        for (i = 0; i < bl && in.read() == bndbytes[i]; ++i) {}
                        if (i == bl) {
                            final int b2 = in.read();
                            if (b2 == 10) {
                                break;
                            }
                            if (b2 == 13) {
                                in.mark(1);
                                if (in.read() != 10) {
                                    in.reset();
                                    break;
                                }
                                break;
                            }
                        }
                        in.reset();
                    }
                    buf.write(b);
                }
                final SunV3BodyPart body = new SunV3BodyPart(headers, buf.toByteArray());
                this.addBodyPart(body);
            } while (b >= 0);
        }
        catch (IOException e) {
            throw new MessagingException("IO Error");
        }
        this.parsed = true;
    }
}
