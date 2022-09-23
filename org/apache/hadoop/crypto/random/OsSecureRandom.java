// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.random;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;
import java.io.Closeable;
import java.util.Random;

@InterfaceAudience.Private
public class OsSecureRandom extends Random implements Closeable, Configurable
{
    public static final Logger LOG;
    private static final long serialVersionUID = 6391500337172057900L;
    private transient Configuration conf;
    private final int RESERVOIR_LENGTH = 8192;
    private String randomDevPath;
    private transient FileInputStream stream;
    private final byte[] reservoir;
    private int pos;
    
    private void fillReservoir(final int min) {
        if (this.pos >= this.reservoir.length - min) {
            try {
                if (this.stream == null) {
                    this.stream = new FileInputStream(new File(this.randomDevPath));
                }
                IOUtils.readFully(this.stream, this.reservoir, 0, this.reservoir.length);
            }
            catch (IOException e) {
                throw new RuntimeException("failed to fill reservoir", e);
            }
            this.pos = 0;
        }
    }
    
    public OsSecureRandom() {
        this.reservoir = new byte[8192];
        this.pos = this.reservoir.length;
    }
    
    @Override
    public synchronized void setConf(final Configuration conf) {
        this.conf = conf;
        this.randomDevPath = conf.get("hadoop.security.random.device.file.path", "/dev/urandom");
        this.close();
    }
    
    @Override
    public synchronized Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public synchronized void nextBytes(final byte[] bytes) {
        for (int off = 0, n = 0; off < bytes.length; off += n, this.pos += n) {
            this.fillReservoir(0);
            n = Math.min(bytes.length - off, this.reservoir.length - this.pos);
            System.arraycopy(this.reservoir, this.pos, bytes, off, n);
        }
    }
    
    @Override
    protected synchronized int next(final int nbits) {
        this.fillReservoir(4);
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n = (n << 8 | (this.reservoir[this.pos++] & 0xFF));
        }
        return n & -1 >> 32 - nbits;
    }
    
    @Override
    public synchronized void close() {
        if (this.stream != null) {
            IOUtils.cleanupWithLogger(OsSecureRandom.LOG, this.stream);
            this.stream = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(OsSecureRandom.class);
    }
}
