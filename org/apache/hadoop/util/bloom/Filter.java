// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public abstract class Filter implements Writable
{
    private static final int VERSION = -1;
    protected int vectorSize;
    protected HashFunction hash;
    protected int nbHash;
    protected int hashType;
    
    protected Filter() {
    }
    
    protected Filter(final int vectorSize, final int nbHash, final int hashType) {
        this.vectorSize = vectorSize;
        this.nbHash = nbHash;
        this.hashType = hashType;
        this.hash = new HashFunction(this.vectorSize, this.nbHash, this.hashType);
    }
    
    public abstract void add(final Key p0);
    
    public abstract boolean membershipTest(final Key p0);
    
    public abstract void and(final Filter p0);
    
    public abstract void or(final Filter p0);
    
    public abstract void xor(final Filter p0);
    
    public abstract void not();
    
    public void add(final List<Key> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("ArrayList<Key> may not be null");
        }
        for (final Key key : keys) {
            this.add(key);
        }
    }
    
    public void add(final Collection<Key> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Collection<Key> may not be null");
        }
        for (final Key key : keys) {
            this.add(key);
        }
    }
    
    public void add(final Key[] keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Key[] may not be null");
        }
        for (int i = 0; i < keys.length; ++i) {
            this.add(keys[i]);
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(-1);
        out.writeInt(this.nbHash);
        out.writeByte(this.hashType);
        out.writeInt(this.vectorSize);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int ver = in.readInt();
        if (ver > 0) {
            this.nbHash = ver;
            this.hashType = 0;
        }
        else {
            if (ver != -1) {
                throw new IOException("Unsupported version: " + ver);
            }
            this.nbHash = in.readInt();
            this.hashType = in.readByte();
        }
        this.vectorSize = in.readInt();
        this.hash = new HashFunction(this.vectorSize, this.nbHash, this.hashType);
    }
}
