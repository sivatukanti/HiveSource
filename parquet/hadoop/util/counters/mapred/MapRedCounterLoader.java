// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters.mapred;

import org.apache.hadoop.mapred.Counters;
import parquet.hadoop.util.counters.BenchmarkCounter;
import parquet.hadoop.util.counters.ICounter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.Reporter;
import parquet.hadoop.util.counters.CounterLoader;

public class MapRedCounterLoader implements CounterLoader
{
    private Reporter reporter;
    private Configuration conf;
    
    public MapRedCounterLoader(final Reporter reporter, final Configuration conf) {
        this.reporter = reporter;
        this.conf = conf;
    }
    
    @Override
    public ICounter getCounterByNameAndFlag(final String groupName, final String counterName, final String counterFlag) {
        if (this.conf.getBoolean(counterFlag, true)) {
            final Counters.Counter counter = this.reporter.getCounter(groupName, counterName);
            if (counter != null) {
                return new MapRedCounterAdapter(this.reporter.getCounter(groupName, counterName));
            }
        }
        return new BenchmarkCounter.NullCounter();
    }
}
