// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters.mapred;

import org.apache.hadoop.mapred.Counters;
import parquet.hadoop.util.counters.ICounter;

public class MapRedCounterAdapter implements ICounter
{
    private Counters.Counter adaptee;
    
    public MapRedCounterAdapter(final Counters.Counter adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void increment(final long val) {
        this.adaptee.increment(val);
    }
    
    @Override
    public long getCount() {
        return this.adaptee.getCounter();
    }
}
