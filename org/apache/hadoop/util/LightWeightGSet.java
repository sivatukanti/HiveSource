// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.ConcurrentModificationException;
import java.util.AbstractCollection;
import java.util.Arrays;
import com.google.common.annotations.VisibleForTesting;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.Collection;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class LightWeightGSet<K, E extends K> implements GSet<K, E>
{
    static final int MAX_ARRAY_LENGTH = 1073741824;
    static final int MIN_ARRAY_LENGTH = 1;
    protected LinkedElement[] entries;
    protected int hash_mask;
    protected int size;
    protected int modification;
    private Collection<E> values;
    
    protected LightWeightGSet() {
        this.size = 0;
        this.modification = 0;
    }
    
    public LightWeightGSet(final int recommended_length) {
        this.size = 0;
        this.modification = 0;
        final int actual = actualArrayLength(recommended_length);
        if (LightWeightGSet.LOG.isDebugEnabled()) {
            LightWeightGSet.LOG.debug("recommended=" + recommended_length + ", actual=" + actual);
        }
        this.entries = new LinkedElement[actual];
        this.hash_mask = this.entries.length - 1;
    }
    
    protected static int actualArrayLength(final int recommended) {
        if (recommended > 1073741824) {
            return 1073741824;
        }
        if (recommended < 1) {
            return 1;
        }
        final int a = Integer.highestOneBit(recommended);
        return (a == recommended) ? a : (a << 1);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    protected int getIndex(final K key) {
        return key.hashCode() & this.hash_mask;
    }
    
    protected E convert(final LinkedElement e) {
        final E r = (E)e;
        return r;
    }
    
    @Override
    public E get(final K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        final int index = this.getIndex(key);
        for (LinkedElement e = this.entries[index]; e != null; e = e.getNext()) {
            if (e.equals(key)) {
                return this.convert(e);
            }
        }
        return null;
    }
    
    @Override
    public boolean contains(final K key) {
        return this.get(key) != null;
    }
    
    @Override
    public E put(final E element) {
        if (element == null) {
            throw new NullPointerException("Null element is not supported.");
        }
        LinkedElement e = null;
        try {
            e = (LinkedElement)element;
        }
        catch (ClassCastException ex) {
            throw new HadoopIllegalArgumentException("!(element instanceof LinkedElement), element.getClass()=" + element.getClass());
        }
        final int index = this.getIndex(element);
        final E existing = this.remove(index, element);
        ++this.modification;
        ++this.size;
        e.setNext(this.entries[index]);
        this.entries[index] = e;
        return existing;
    }
    
    protected E remove(final int index, final K key) {
        if (this.entries[index] == null) {
            return null;
        }
        if (this.entries[index].equals(key)) {
            ++this.modification;
            --this.size;
            final LinkedElement e = this.entries[index];
            this.entries[index] = e.getNext();
            e.setNext(null);
            return this.convert(e);
        }
        LinkedElement prev = this.entries[index];
        for (LinkedElement curr = prev.getNext(); curr != null; curr = curr.getNext()) {
            if (curr.equals(key)) {
                ++this.modification;
                --this.size;
                prev.setNext(curr.getNext());
                curr.setNext(null);
                return this.convert(curr);
            }
            prev = curr;
        }
        return null;
    }
    
    @Override
    public E remove(final K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return this.remove(this.getIndex(key), key);
    }
    
    @Override
    public Collection<E> values() {
        if (this.values == null) {
            this.values = new Values();
        }
        return this.values;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new SetIterator();
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder(this.getClass().getSimpleName());
        b.append("(size=").append(this.size).append(String.format(", %08x", this.hash_mask)).append(", modification=").append(this.modification).append(", entries.length=").append(this.entries.length).append(")");
        return b.toString();
    }
    
    public void printDetails(final PrintStream out) {
        out.print(this + ", entries = [");
        for (int i = 0; i < this.entries.length; ++i) {
            if (this.entries[i] != null) {
                LinkedElement e = this.entries[i];
                out.print("\n  " + i + ": " + e);
                for (e = e.getNext(); e != null; e = e.getNext()) {
                    out.print(" -> " + e);
                }
            }
        }
        out.println("\n]");
    }
    
    public static int computeCapacity(final double percentage, final String mapName) {
        return computeCapacity(Runtime.getRuntime().maxMemory(), percentage, mapName);
    }
    
    @VisibleForTesting
    static int computeCapacity(final long maxMemory, final double percentage, final String mapName) {
        if (percentage > 100.0 || percentage < 0.0) {
            throw new HadoopIllegalArgumentException("Percentage " + percentage + " must be greater than or equal to 0  and less than or equal to 100");
        }
        if (maxMemory < 0L) {
            throw new HadoopIllegalArgumentException("Memory " + maxMemory + " must be greater than or equal to 0");
        }
        if (percentage == 0.0 || maxMemory == 0L) {
            return 0;
        }
        final String vmBit = System.getProperty("sun.arch.data.model");
        final double percentDivisor = 100.0 / percentage;
        final double percentMemory = maxMemory / percentDivisor;
        final int e1 = (int)(Math.log(percentMemory) / Math.log(2.0) + 0.5);
        final int e2 = e1 - ("32".equals(vmBit) ? 2 : 3);
        final int exponent = (e2 < 0) ? 0 : ((e2 > 30) ? 30 : e2);
        final int c = 1 << exponent;
        LightWeightGSet.LOG.info("Computing capacity for map " + mapName);
        LightWeightGSet.LOG.info("VM type       = " + vmBit + "-bit");
        LightWeightGSet.LOG.info(percentage + "% max memory " + StringUtils.TraditionalBinaryPrefix.long2String(maxMemory, "B", 1) + " = " + StringUtils.TraditionalBinaryPrefix.long2String((long)percentMemory, "B", 1));
        LightWeightGSet.LOG.info("capacity      = 2^" + exponent + " = " + c + " entries");
        return c;
    }
    
    @Override
    public void clear() {
        ++this.modification;
        Arrays.fill(this.entries, null);
        this.size = 0;
    }
    
    private final class Values extends AbstractCollection<E>
    {
        @Override
        public Iterator<E> iterator() {
            return LightWeightGSet.this.iterator();
        }
        
        @Override
        public int size() {
            return LightWeightGSet.this.size;
        }
        
        @Override
        public boolean contains(final Object o) {
            return LightWeightGSet.this.contains(o);
        }
        
        @Override
        public void clear() {
            LightWeightGSet.this.clear();
        }
    }
    
    public class SetIterator implements Iterator<E>
    {
        private int iterModification;
        private int index;
        private LinkedElement cur;
        private LinkedElement next;
        private boolean trackModification;
        
        public SetIterator() {
            this.iterModification = LightWeightGSet.this.modification;
            this.index = -1;
            this.cur = null;
            this.next = this.nextNonemptyEntry();
            this.trackModification = true;
        }
        
        private LinkedElement nextNonemptyEntry() {
            ++this.index;
            while (this.index < LightWeightGSet.this.entries.length && LightWeightGSet.this.entries[this.index] == null) {
                ++this.index;
            }
            return (this.index < LightWeightGSet.this.entries.length) ? LightWeightGSet.this.entries[this.index] : null;
        }
        
        private void ensureNext() {
            if (this.trackModification && LightWeightGSet.this.modification != this.iterModification) {
                throw new ConcurrentModificationException("modification=" + LightWeightGSet.this.modification + " != iterModification = " + this.iterModification);
            }
            if (this.next != null) {
                return;
            }
            if (this.cur == null) {
                return;
            }
            this.next = this.cur.getNext();
            if (this.next == null) {
                this.next = this.nextNonemptyEntry();
            }
        }
        
        @Override
        public boolean hasNext() {
            this.ensureNext();
            return this.next != null;
        }
        
        @Override
        public E next() {
            this.ensureNext();
            if (this.next == null) {
                throw new IllegalStateException("There are no more elements");
            }
            this.cur = this.next;
            this.next = null;
            return LightWeightGSet.this.convert(this.cur);
        }
        
        @Override
        public void remove() {
            this.ensureNext();
            if (this.cur == null) {
                throw new IllegalStateException("There is no current element to remove");
            }
            LightWeightGSet.this.remove(this.cur);
            ++this.iterModification;
            this.cur = null;
        }
        
        public void setTrackModification(final boolean trackModification) {
            this.trackModification = trackModification;
        }
    }
    
    public interface LinkedElement
    {
        void setNext(final LinkedElement p0);
        
        LinkedElement getNext();
    }
}
