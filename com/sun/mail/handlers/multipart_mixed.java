// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.handlers;

import java.io.OutputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class multipart_mixed implements DataContentHandler
{
    private ActivationDataFlavor myDF;
    
    public multipart_mixed() {
        this.myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { this.myDF };
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        if (this.myDF.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }
    
    public Object getContent(final DataSource ds) throws IOException {
        try {
            return new MimeMultipart(ds);
        }
        catch (MessagingException e) {
            final IOException ioex = new IOException("Exception while constructing MimeMultipart");
            ioex.initCause(e);
            throw ioex;
        }
    }
    
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (obj instanceof MimeMultipart) {
            try {
                ((MimeMultipart)obj).writeTo(os);
            }
            catch (MessagingException e) {
                throw new IOException(e.toString());
            }
        }
    }
}
