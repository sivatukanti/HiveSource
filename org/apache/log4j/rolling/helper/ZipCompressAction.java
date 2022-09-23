// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling.helper;

import org.apache.log4j.helpers.LogLog;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public final class ZipCompressAction extends ActionBase
{
    private final File source;
    private final File destination;
    private final boolean deleteSource;
    
    public ZipCompressAction(final File source, final File destination, final boolean deleteSource) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        this.source = source;
        this.destination = destination;
        this.deleteSource = deleteSource;
    }
    
    public boolean execute() throws IOException {
        return execute(this.source, this.destination, this.deleteSource);
    }
    
    public static boolean execute(final File source, final File destination, final boolean deleteSource) throws IOException {
        if (source.exists()) {
            final FileInputStream fis = new FileInputStream(source);
            final FileOutputStream fos = new FileOutputStream(destination);
            final ZipOutputStream zos = new ZipOutputStream(fos);
            final ZipEntry zipEntry = new ZipEntry(source.getName());
            zos.putNextEntry(zipEntry);
            final byte[] inbuf = new byte[8102];
            int n;
            while ((n = fis.read(inbuf)) != -1) {
                zos.write(inbuf, 0, n);
            }
            zos.close();
            fis.close();
            if (deleteSource && !source.delete()) {
                LogLog.warn("Unable to delete " + source.toString() + ".");
            }
            return true;
        }
        return false;
    }
    
    protected void reportException(final Exception ex) {
        LogLog.warn("Exception during compression of '" + this.source.toString() + "'.", ex);
    }
}
