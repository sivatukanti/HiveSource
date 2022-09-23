// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters;

import parquet.hadoop.util.counters.mapred.MapRedCounterLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.Reporter;
import parquet.hadoop.util.counters.mapreduce.MapReduceCounterLoader;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;

public class BenchmarkCounter
{
    private static final String ENABLE_BYTES_READ_COUNTER = "parquet.benchmark.bytes.read";
    private static final String ENABLE_BYTES_TOTAL_COUNTER = "parquet.benchmark.bytes.total";
    private static final String ENABLE_TIME_READ_COUNTER = "parquet.benchmark.time.read";
    private static final String COUNTER_GROUP_NAME = "parquet";
    private static final String BYTES_READ_COUNTER_NAME = "bytesread";
    private static final String BYTES_TOTAL_COUNTER_NAME = "bytestotal";
    private static final String TIME_READ_COUNTER_NAME = "timeread";
    private static ICounter bytesReadCounter;
    private static ICounter totalBytesCounter;
    private static ICounter timeCounter;
    private static CounterLoader counterLoader;
    
    public static void initCounterFromContext(final TaskInputOutputContext<?, ?, ?, ?> context) {
        BenchmarkCounter.counterLoader = new MapReduceCounterLoader(context);
        loadCounters();
    }
    
    public static void initCounterFromReporter(final Reporter reporter, final Configuration configuration) {
        BenchmarkCounter.counterLoader = new MapRedCounterLoader(reporter, configuration);
        loadCounters();
    }
    
    private static void loadCounters() {
        BenchmarkCounter.bytesReadCounter = getCounterWhenFlagIsSet("parquet", "bytesread", "parquet.benchmark.bytes.read");
        BenchmarkCounter.totalBytesCounter = getCounterWhenFlagIsSet("parquet", "bytestotal", "parquet.benchmark.bytes.total");
        BenchmarkCounter.timeCounter = getCounterWhenFlagIsSet("parquet", "timeread", "parquet.benchmark.time.read");
    }
    
    private static ICounter getCounterWhenFlagIsSet(final String groupName, final String counterName, final String counterFlag) {
        return BenchmarkCounter.counterLoader.getCounterByNameAndFlag(groupName, counterName, counterFlag);
    }
    
    public static void incrementTotalBytes(final long val) {
        BenchmarkCounter.totalBytesCounter.increment(val);
    }
    
    public static long getTotalBytes() {
        return BenchmarkCounter.totalBytesCounter.getCount();
    }
    
    public static void incrementBytesRead(final long val) {
        BenchmarkCounter.bytesReadCounter.increment(val);
    }
    
    public static long getBytesRead() {
        return BenchmarkCounter.bytesReadCounter.getCount();
    }
    
    public static void incrementTime(final long val) {
        BenchmarkCounter.timeCounter.increment(val);
    }
    
    public static long getTime() {
        return BenchmarkCounter.timeCounter.getCount();
    }
    
    static {
        BenchmarkCounter.bytesReadCounter = new NullCounter();
        BenchmarkCounter.totalBytesCounter = new NullCounter();
        BenchmarkCounter.timeCounter = new NullCounter();
    }
    
    public static class NullCounter implements ICounter
    {
        @Override
        public void increment(final long val) {
        }
        
        @Override
        public long getCount() {
            return 0L;
        }
    }
}
