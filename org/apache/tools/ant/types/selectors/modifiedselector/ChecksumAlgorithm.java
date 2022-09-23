// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.CheckedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.zip.Adler32;
import org.apache.tools.ant.BuildException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.Locale;
import java.util.zip.Checksum;

public class ChecksumAlgorithm implements Algorithm
{
    private String algorithm;
    private Checksum checksum;
    
    public ChecksumAlgorithm() {
        this.algorithm = "CRC";
        this.checksum = null;
    }
    
    public void setAlgorithm(final String algorithm) {
        this.algorithm = ((algorithm != null) ? algorithm.toUpperCase(Locale.ENGLISH) : null);
    }
    
    public void initChecksum() {
        if (this.checksum != null) {
            return;
        }
        if ("CRC".equals(this.algorithm)) {
            this.checksum = new CRC32();
        }
        else {
            if (!"ADLER".equals(this.algorithm)) {
                throw new BuildException(new NoSuchAlgorithmException());
            }
            this.checksum = new Adler32();
        }
    }
    
    public boolean isValid() {
        return "CRC".equals(this.algorithm) || "ADLER".equals(this.algorithm);
    }
    
    public String getValue(final File file) {
        this.initChecksum();
        String rval = null;
        try {
            if (file.canRead()) {
                this.checksum.reset();
                final FileInputStream fis = new FileInputStream(file);
                final CheckedInputStream check = new CheckedInputStream(fis, this.checksum);
                final BufferedInputStream in = new BufferedInputStream(check);
                while (in.read() != -1) {}
                rval = Long.toString(check.getChecksum().getValue());
                in.close();
            }
        }
        catch (Exception e) {
            rval = null;
        }
        return rval;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("<ChecksumAlgorithm:");
        buf.append("algorithm=").append(this.algorithm);
        buf.append(">");
        return buf.toString();
    }
}
