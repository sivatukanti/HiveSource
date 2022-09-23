// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Random;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class RetouchedBloomFilter extends BloomFilter implements RemoveScheme
{
    List<Key>[] fpVector;
    List<Key>[] keyVector;
    double[] ratio;
    private Random rand;
    
    public RetouchedBloomFilter() {
    }
    
    public RetouchedBloomFilter(final int vectorSize, final int nbHash, final int hashType) {
        super(vectorSize, nbHash, hashType);
        this.rand = null;
        this.createVector();
    }
    
    @Override
    public void add(final Key key) {
        if (key == null) {
            throw new NullPointerException("key can not be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            this.bits.set(h[i]);
            this.keyVector[h[i]].add(key);
        }
    }
    
    public void addFalsePositive(final Key key) {
        if (key == null) {
            throw new NullPointerException("key can not be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            this.fpVector[h[i]].add(key);
        }
    }
    
    public void addFalsePositive(final Collection<Key> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection<Key> can not be null");
        }
        for (final Key k : coll) {
            this.addFalsePositive(k);
        }
    }
    
    public void addFalsePositive(final List<Key> keys) {
        if (keys == null) {
            throw new NullPointerException("ArrayList<Key> can not be null");
        }
        for (final Key k : keys) {
            this.addFalsePositive(k);
        }
    }
    
    public void addFalsePositive(final Key[] keys) {
        if (keys == null) {
            throw new NullPointerException("Key[] can not be null");
        }
        for (int i = 0; i < keys.length; ++i) {
            this.addFalsePositive(keys[i]);
        }
    }
    
    public void selectiveClearing(final Key k, final short scheme) {
        if (k == null) {
            throw new NullPointerException("Key can not be null");
        }
        if (!this.membershipTest(k)) {
            throw new IllegalArgumentException("Key is not a member");
        }
        int index = 0;
        final int[] h = this.hash.hash(k);
        switch (scheme) {
            case 0: {
                index = this.randomRemove();
                break;
            }
            case 1: {
                index = this.minimumFnRemove(h);
                break;
            }
            case 2: {
                index = this.maximumFpRemove(h);
                break;
            }
            case 3: {
                index = this.ratioRemove(h);
                break;
            }
            default: {
                throw new AssertionError((Object)"Undefined selective clearing scheme");
            }
        }
        this.clearBit(index);
    }
    
    private int randomRemove() {
        if (this.rand == null) {
            this.rand = new Random();
        }
        return this.rand.nextInt(this.nbHash);
    }
    
    private int minimumFnRemove(final int[] h) {
        int minIndex = Integer.MAX_VALUE;
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < this.nbHash; ++i) {
            final double keyWeight = this.getWeight(this.keyVector[h[i]]);
            if (keyWeight < minValue) {
                minIndex = h[i];
                minValue = keyWeight;
            }
        }
        return minIndex;
    }
    
    private int maximumFpRemove(final int[] h) {
        int maxIndex = Integer.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < this.nbHash; ++i) {
            final double fpWeight = this.getWeight(this.fpVector[h[i]]);
            if (fpWeight > maxValue) {
                maxValue = fpWeight;
                maxIndex = h[i];
            }
        }
        return maxIndex;
    }
    
    private int ratioRemove(final int[] h) {
        this.computeRatio();
        int minIndex = Integer.MAX_VALUE;
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < this.nbHash; ++i) {
            if (this.ratio[h[i]] < minValue) {
                minValue = this.ratio[h[i]];
                minIndex = h[i];
            }
        }
        return minIndex;
    }
    
    private void clearBit(final int index) {
        if (index < 0 || index >= this.vectorSize) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final List<Key> kl = this.keyVector[index];
        final List<Key> fpl = this.fpVector[index];
        for (int listSize = kl.size(), i = 0; i < listSize && !kl.isEmpty(); ++i) {
            this.removeKey(kl.get(0), this.keyVector);
        }
        kl.clear();
        this.keyVector[index].clear();
        for (int listSize = fpl.size(), i = 0; i < listSize && !fpl.isEmpty(); ++i) {
            this.removeKey(fpl.get(0), this.fpVector);
        }
        fpl.clear();
        this.fpVector[index].clear();
        this.ratio[index] = 0.0;
        this.bits.clear(index);
    }
    
    private void removeKey(final Key k, final List<Key>[] vector) {
        if (k == null) {
            throw new NullPointerException("Key can not be null");
        }
        if (vector == null) {
            throw new NullPointerException("ArrayList<Key>[] can not be null");
        }
        final int[] h = this.hash.hash(k);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            vector[h[i]].remove(k);
        }
    }
    
    private void computeRatio() {
        for (int i = 0; i < this.vectorSize; ++i) {
            final double keyWeight = this.getWeight(this.keyVector[i]);
            final double fpWeight = this.getWeight(this.fpVector[i]);
            if (keyWeight > 0.0 && fpWeight > 0.0) {
                this.ratio[i] = keyWeight / fpWeight;
            }
        }
    }
    
    private double getWeight(final List<Key> keyList) {
        double weight = 0.0;
        for (final Key k : keyList) {
            weight += k.getWeight();
        }
        return weight;
    }
    
    private void createVector() {
        this.fpVector = (List<Key>[])new List[this.vectorSize];
        this.keyVector = (List<Key>[])new List[this.vectorSize];
        this.ratio = new double[this.vectorSize];
        for (int i = 0; i < this.vectorSize; ++i) {
            this.fpVector[i] = Collections.synchronizedList(new ArrayList<Key>());
            this.keyVector[i] = Collections.synchronizedList(new ArrayList<Key>());
            this.ratio[i] = 0.0;
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        for (int i = 0; i < this.fpVector.length; ++i) {
            final List<Key> list = this.fpVector[i];
            out.writeInt(list.size());
            for (final Key k : list) {
                k.write(out);
            }
        }
        for (int i = 0; i < this.keyVector.length; ++i) {
            final List<Key> list = this.keyVector[i];
            out.writeInt(list.size());
            for (final Key k : list) {
                k.write(out);
            }
        }
        for (int i = 0; i < this.ratio.length; ++i) {
            out.writeDouble(this.ratio[i]);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        this.createVector();
        for (int i = 0; i < this.fpVector.length; ++i) {
            final List<Key> list = this.fpVector[i];
            for (int size = in.readInt(), j = 0; j < size; ++j) {
                final Key k = new Key();
                k.readFields(in);
                list.add(k);
            }
        }
        for (int i = 0; i < this.keyVector.length; ++i) {
            final List<Key> list = this.keyVector[i];
            for (int size = in.readInt(), j = 0; j < size; ++j) {
                final Key k = new Key();
                k.readFields(in);
                list.add(k);
            }
        }
        for (int i = 0; i < this.ratio.length; ++i) {
            this.ratio[i] = in.readDouble();
        }
    }
}
