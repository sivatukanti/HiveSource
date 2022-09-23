// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters.mapreduce;

import parquet.hadoop.util.counters.BenchmarkCounter;
import org.apache.hadoop.mapreduce.JobContext;
import parquet.hadoop.util.ContextUtil;
import parquet.hadoop.util.counters.ICounter;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import parquet.hadoop.util.counters.CounterLoader;

public class MapReduceCounterLoader implements CounterLoader
{
    private TaskInputOutputContext<?, ?, ?, ?> context;
    
    public MapReduceCounterLoader(final TaskInputOutputContext<?, ?, ?, ?> context) {
        this.context = context;
    }
    
    @Override
    public ICounter getCounterByNameAndFlag(final String groupName, final String counterName, final String counterFlag) {
        if (ContextUtil.getConfiguration((JobContext)this.context).getBoolean(counterFlag, true)) {
            return new MapReduceCounterAdapter(ContextUtil.getCounter(this.context, groupName, counterName));
        }
        return new BenchmarkCounter.NullCounter();
    }
}
