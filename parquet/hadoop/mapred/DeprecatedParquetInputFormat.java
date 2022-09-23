// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.mapred;

import java.io.DataOutput;
import java.io.DataInput;
import org.apache.hadoop.mapred.FileSplit;
import parquet.hadoop.ParquetRecordReader;
import java.util.Arrays;
import java.util.Iterator;
import parquet.hadoop.Footer;
import java.util.List;
import parquet.hadoop.ParquetInputSplit;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.InputSplit;
import parquet.hadoop.ParquetInputFormat;
import org.apache.hadoop.mapred.FileInputFormat;

public class DeprecatedParquetInputFormat<V> extends FileInputFormat<Void, Container<V>>
{
    protected ParquetInputFormat<V> realInputFormat;
    
    public DeprecatedParquetInputFormat() {
        this.realInputFormat = new ParquetInputFormat<V>();
    }
    
    public RecordReader<Void, Container<V>> getRecordReader(final InputSplit split, final JobConf job, final Reporter reporter) throws IOException {
        return (RecordReader<Void, Container<V>>)new RecordReaderWrapper(split, job, reporter);
    }
    
    public InputSplit[] getSplits(final JobConf job, final int numSplits) throws IOException {
        if (isTaskSideMetaData(job)) {
            return super.getSplits(job, numSplits);
        }
        final List<Footer> footers = this.getFooters(job);
        final List<ParquetInputSplit> splits = this.realInputFormat.getSplits((Configuration)job, footers);
        if (splits == null) {
            return null;
        }
        final InputSplit[] resultSplits = new InputSplit[splits.size()];
        int i = 0;
        for (final ParquetInputSplit split : splits) {
            resultSplits[i++] = (InputSplit)new ParquetInputSplitWrapper(split);
        }
        return resultSplits;
    }
    
    public List<Footer> getFooters(final JobConf job) throws IOException {
        return this.realInputFormat.getFooters((Configuration)job, Arrays.asList(super.listStatus(job)));
    }
    
    public static boolean isTaskSideMetaData(final JobConf job) {
        return job.getBoolean("parquet.task.side.metadata", (boolean)Boolean.TRUE);
    }
    
    private static class RecordReaderWrapper<V> implements RecordReader<Void, Container<V>>
    {
        private ParquetRecordReader<V> realReader;
        private long splitLen;
        private Container<V> valueContainer;
        private boolean firstRecord;
        private boolean eof;
        
        public RecordReaderWrapper(final InputSplit oldSplit, final JobConf oldJobConf, final Reporter reporter) throws IOException {
            this.valueContainer = null;
            this.firstRecord = false;
            this.eof = false;
            this.splitLen = oldSplit.getLength();
            try {
                this.realReader = new ParquetRecordReader<V>(ParquetInputFormat.getReadSupportInstance((Configuration)oldJobConf), ParquetInputFormat.getFilter((Configuration)oldJobConf));
                if (oldSplit instanceof ParquetInputSplitWrapper) {
                    this.realReader.initialize((org.apache.hadoop.mapreduce.InputSplit)((ParquetInputSplitWrapper)oldSplit).realSplit, (Configuration)oldJobConf, reporter);
                }
                else {
                    if (!(oldSplit instanceof FileSplit)) {
                        throw new IllegalArgumentException("Invalid split (not a FileSplit or ParquetInputSplitWrapper): " + oldSplit);
                    }
                    this.realReader.initialize((org.apache.hadoop.mapreduce.InputSplit)oldSplit, (Configuration)oldJobConf, reporter);
                }
                if (this.realReader.nextKeyValue()) {
                    this.firstRecord = true;
                    (this.valueContainer = new Container<V>()).set(this.realReader.getCurrentValue());
                }
                else {
                    this.eof = true;
                }
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
        
        public void close() throws IOException {
            this.realReader.close();
        }
        
        public Void createKey() {
            return null;
        }
        
        public Container<V> createValue() {
            return this.valueContainer;
        }
        
        public long getPos() throws IOException {
            return (long)(this.splitLen * this.getProgress());
        }
        
        public float getProgress() throws IOException {
            try {
                return this.realReader.getProgress();
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
        
        public boolean next(final Void key, final Container<V> value) throws IOException {
            if (this.eof) {
                return false;
            }
            if (this.firstRecord) {
                this.firstRecord = false;
                return true;
            }
            try {
                if (this.realReader.nextKeyValue()) {
                    if (value != null) {
                        value.set(this.realReader.getCurrentValue());
                    }
                    return true;
                }
            }
            catch (InterruptedException e) {
                throw new IOException(e);
            }
            this.eof = true;
            return false;
        }
    }
    
    private static class ParquetInputSplitWrapper implements InputSplit
    {
        ParquetInputSplit realSplit;
        
        public ParquetInputSplitWrapper() {
        }
        
        public ParquetInputSplitWrapper(final ParquetInputSplit realSplit) {
            this.realSplit = realSplit;
        }
        
        public long getLength() throws IOException {
            return this.realSplit.getLength();
        }
        
        public String[] getLocations() throws IOException {
            return this.realSplit.getLocations();
        }
        
        public void readFields(final DataInput in) throws IOException {
            (this.realSplit = new ParquetInputSplit()).readFields(in);
        }
        
        public void write(final DataOutput out) throws IOException {
            this.realSplit.write(out);
        }
    }
}
