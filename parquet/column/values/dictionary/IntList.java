// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.dictionary;

import java.util.ArrayList;
import java.util.List;

public class IntList
{
    private static final int SLAB_SIZE = 65536;
    private List<int[]> slabs;
    private int[] currentSlab;
    private int currentSlabPos;
    
    public IntList() {
        this.slabs = new ArrayList<int[]>();
        this.initSlab();
    }
    
    private void initSlab() {
        this.currentSlab = new int[65536];
        this.currentSlabPos = 0;
    }
    
    public void add(final int i) {
        if (this.currentSlabPos == this.currentSlab.length) {
            this.slabs.add(this.currentSlab);
            this.initSlab();
        }
        this.currentSlab[this.currentSlabPos] = i;
        ++this.currentSlabPos;
    }
    
    public IntIterator iterator() {
        final int[][] itSlabs = this.slabs.toArray(new int[this.slabs.size() + 1][]);
        itSlabs[this.slabs.size()] = this.currentSlab;
        return new IntIterator(itSlabs, 65536 * this.slabs.size() + this.currentSlabPos);
    }
    
    public int size() {
        return 65536 * this.slabs.size() + this.currentSlabPos;
    }
    
    public static class IntIterator
    {
        private final int[][] slabs;
        private int current;
        private final int count;
        
        public IntIterator(final int[][] slabs, final int count) {
            this.slabs = slabs;
            this.count = count;
        }
        
        public boolean hasNext() {
            return this.current < this.count;
        }
        
        public int next() {
            final int result = this.slabs[this.current / 65536][this.current % 65536];
            ++this.current;
            return result;
        }
    }
}
