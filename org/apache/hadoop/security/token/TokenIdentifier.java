// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.apache.commons.codec.digest.DigestUtils;
import java.util.Arrays;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class TokenIdentifier implements Writable
{
    private String trackingId;
    
    public TokenIdentifier() {
        this.trackingId = null;
    }
    
    public abstract Text getKind();
    
    public abstract UserGroupInformation getUser();
    
    public byte[] getBytes() {
        final DataOutputBuffer buf = new DataOutputBuffer(4096);
        try {
            this.write(buf);
        }
        catch (IOException ie) {
            throw new RuntimeException("i/o error in getBytes", ie);
        }
        return Arrays.copyOf(buf.getData(), buf.getLength());
    }
    
    public String getTrackingId() {
        if (this.trackingId == null) {
            this.trackingId = DigestUtils.md5Hex(this.getBytes());
        }
        return this.trackingId;
    }
}
