// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters.mapreduce;

import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapreduce.Counter;
import parquet.hadoop.util.counters.ICounter;

public class MapReduceCounterAdapter implements ICounter
{
    private Counter adaptee;
    
    public MapReduceCounterAdapter(final Counter adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void increment(final long val) {
        ContextUtil.incrementCounter(this.adaptee, val);
    }
    
    @Override
    public long getCount() {
        return this.adaptee.getValue();
    }
}
