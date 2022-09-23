// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class DynamicBloomFilter extends Filter
{
    private int nr;
    private int currentNbRecord;
    private BloomFilter[] matrix;
    
    public DynamicBloomFilter() {
    }
    
    public DynamicBloomFilter(final int vectorSize, final int nbHash, final int hashType, final int nr) {
        super(vectorSize, nbHash, hashType);
        this.nr = nr;
        this.currentNbRecord = 0;
        (this.matrix = new BloomFilter[1])[0] = new BloomFilter(this.vectorSize, this.nbHash, this.hashType);
    }
    
    @Override
    public void add(final Key key) {
        if (key == null) {
            throw new NullPointerException("Key can not be null");
        }
        BloomFilter bf = this.getActiveStandardBF();
        if (bf == null) {
            this.addRow();
            bf = this.matrix[this.matrix.length - 1];
            this.currentNbRecord = 0;
        }
        bf.add(key);
        ++this.currentNbRecord;
    }
    
    @Override
    public void and(final Filter filter) {
        if (filter == null || !(filter instanceof DynamicBloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be and-ed");
        }
        final DynamicBloomFilter dbf = (DynamicBloomFilter)filter;
        if (dbf.matrix.length != this.matrix.length || dbf.nr != this.nr) {
            throw new IllegalArgumentException("filters cannot be and-ed");
        }
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].and(dbf.matrix[i]);
        }
    }
    
    @Override
    public boolean membershipTest(final Key key) {
        if (key == null) {
            return true;
        }
        for (int i = 0; i < this.matrix.length; ++i) {
            if (this.matrix[i].membershipTest(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void not() {
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].not();
        }
    }
    
    @Override
    public void or(final Filter filter) {
        if (filter == null || !(filter instanceof DynamicBloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be or-ed");
        }
        final DynamicBloomFilter dbf = (DynamicBloomFilter)filter;
        if (dbf.matrix.length != this.matrix.length || dbf.nr != this.nr) {
            throw new IllegalArgumentException("filters cannot be or-ed");
        }
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].or(dbf.matrix[i]);
        }
    }
    
    @Override
    public void xor(final Filter filter) {
        if (filter == null || !(filter instanceof DynamicBloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be xor-ed");
        }
        final DynamicBloomFilter dbf = (DynamicBloomFilter)filter;
        if (dbf.matrix.length != this.matrix.length || dbf.nr != this.nr) {
            throw new IllegalArgumentException("filters cannot be xor-ed");
        }
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].xor(dbf.matrix[i]);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.matrix.length; ++i) {
            res.append(this.matrix[i]);
            res.append(13);
        }
        return res.toString();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(this.nr);
        out.writeInt(this.currentNbRecord);
        out.writeInt(this.matrix.length);
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i].write(out);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        this.nr = in.readInt();
        this.currentNbRecord = in.readInt();
        final int len = in.readInt();
        this.matrix = new BloomFilter[len];
        for (int i = 0; i < this.matrix.length; ++i) {
            (this.matrix[i] = new BloomFilter()).readFields(in);
        }
    }
    
    private void addRow() {
        final BloomFilter[] tmp = new BloomFilter[this.matrix.length + 1];
        for (int i = 0; i < this.matrix.length; ++i) {
            tmp[i] = this.matrix[i];
        }
        tmp[tmp.length - 1] = new BloomFilter(this.vectorSize, this.nbHash, this.hashType);
        this.matrix = tmp;
    }
    
    private BloomFilter getActiveStandardBF() {
        if (this.currentNbRecord >= this.nr) {
            return null;
        }
        return this.matrix[this.matrix.length - 1];
    }
}
