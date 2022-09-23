// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.random;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.io.InputStream;
import org.slf4j.Logger;

public class NativeRandom implements RandomProvider
{
    private static final Logger LOG;
    private InputStream input;
    private String randFile;
    
    public NativeRandom() {
        this.randFile = "/dev/urandom";
    }
    
    @Override
    public void init() {
        try {
            this.input = Files.newInputStream(Paths.get(this.randFile, new String[0]), new OpenOption[0]);
        }
        catch (IOException e) {
            NativeRandom.LOG.error("Failed to init from file: " + this.randFile + ". " + e.toString());
        }
    }
    
    @Override
    public void setSeed(final byte[] seed) {
        OutputStream output = null;
        try {
            output = Files.newOutputStream(Paths.get(this.randFile, new String[0]), new OpenOption[0]);
            output.write(seed);
            output.flush();
        }
        catch (IOException e) {
            NativeRandom.LOG.error("Failed to write seed to the file: " + this.randFile + ". " + e.toString());
            if (output != null) {
                try {
                    output.close();
                }
                catch (IOException e) {
                    NativeRandom.LOG.error("Failed to close output stream. " + e.toString());
                }
            }
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (IOException e2) {
                    NativeRandom.LOG.error("Failed to close output stream. " + e2.toString());
                }
            }
        }
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        try {
            if (this.input.read(bytes) == -1) {
                throw new IOException();
            }
        }
        catch (IOException e) {
            NativeRandom.LOG.error("Failed to read nextBytes. " + e.toString());
        }
    }
    
    @Override
    public void destroy() {
        try {
            this.input.close();
        }
        catch (IOException e) {
            NativeRandom.LOG.error("Failed to close input stream. " + e.toString());
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(NativeRandom.class);
    }
}
