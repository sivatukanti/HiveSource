// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.store;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.fs.FileChecksum;

public class EtagChecksum extends FileChecksum
{
    private static final String ETAG = "etag";
    private String eTag;
    
    public EtagChecksum() {
        this.eTag = "";
    }
    
    public EtagChecksum(final String eTag) {
        this.eTag = "";
        this.eTag = eTag;
    }
    
    @Override
    public String getAlgorithmName() {
        return "etag";
    }
    
    @Override
    public int getLength() {
        return this.eTag.getBytes(StandardCharsets.UTF_8).length;
    }
    
    @Override
    public byte[] getBytes() {
        return (this.eTag != null) ? this.eTag.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeUTF((this.eTag != null) ? this.eTag : "");
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.eTag = in.readUTF();
    }
    
    @Override
    public String toString() {
        return "etag: \"" + this.eTag + '\"';
    }
}
