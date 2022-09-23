// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.util.ArrayList;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.lib.CombineFileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.hive.io.HiveIOExceptionHandlerUtil;
import java.lang.reflect.Constructor;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RecordReader;
import java.io.DataOutput;
import java.io.DataInput;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.CombineFileSplit;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.Credentials;
import java.security.AccessControlException;
import org.apache.hadoop.fs.DefaultFileAccess;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileStatus;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.fs.FsShell;
import java.net.URI;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.commons.logging.Log;

public abstract class HadoopShimsSecure implements HadoopShims
{
    static final Log LOG;
    
    @Override
    public abstract JobTrackerState getJobTrackerState(final ClusterStatus p0) throws Exception;
    
    @Override
    public abstract TaskAttemptContext newTaskAttemptContext(final Configuration p0, final Progressable p1);
    
    @Override
    public abstract JobContext newJobContext(final Job p0);
    
    @Override
    public abstract boolean isLocalMode(final Configuration p0);
    
    @Override
    public abstract void setJobLauncherRpcAddress(final Configuration p0, final String p1);
    
    @Override
    public abstract String getJobLauncherHttpAddress(final Configuration p0);
    
    @Override
    public abstract String getJobLauncherRpcAddress(final Configuration p0);
    
    @Override
    public abstract short getDefaultReplication(final FileSystem p0, final Path p1);
    
    @Override
    public abstract long getDefaultBlockSize(final FileSystem p0, final Path p1);
    
    @Override
    public abstract boolean moveToAppropriateTrash(final FileSystem p0, final Path p1, final Configuration p2) throws IOException;
    
    @Override
    public abstract FileSystem createProxyFileSystem(final FileSystem p0, final URI p1);
    
    @Override
    public abstract FileSystem getNonCachedFileSystem(final URI p0, final Configuration p1) throws IOException;
    
    protected void run(final FsShell shell, final String[] command) throws Exception {
        HadoopShimsSecure.LOG.debug(ArrayUtils.toString(command));
        final int retval = shell.run(command);
        HadoopShimsSecure.LOG.debug("Return value is :" + retval);
    }
    
    private static String[] dedup(final String[] locations) throws IOException {
        final Set<String> dedup = new HashSet<String>();
        Collections.addAll(dedup, locations);
        return dedup.toArray(new String[dedup.size()]);
    }
    
    @Override
    public void checkFileAccess(final FileSystem fs, final FileStatus stat, final FsAction action) throws IOException, AccessControlException, Exception {
        DefaultFileAccess.checkFileAccess(fs, stat, action);
    }
    
    @Override
    public abstract void addDelegationTokens(final FileSystem p0, final Credentials p1, final String p2) throws IOException;
    
    static {
        LOG = LogFactory.getLog(HadoopShimsSecure.class);
    }
    
    public static class InputSplitShim extends CombineFileSplit
    {
        long shrinkedLength;
        boolean _isShrinked;
        
        public InputSplitShim() {
            this._isShrinked = false;
        }
        
        public InputSplitShim(final JobConf conf, final Path[] paths, final long[] startOffsets, final long[] lengths, final String[] locations) throws IOException {
            super(conf, paths, startOffsets, lengths, dedup(locations));
            this._isShrinked = false;
        }
        
        public void shrinkSplit(final long length) {
            this._isShrinked = true;
            this.shrinkedLength = length;
        }
        
        public boolean isShrinked() {
            return this._isShrinked;
        }
        
        public long getShrinkedLength() {
            return this.shrinkedLength;
        }
        
        public void readFields(final DataInput in) throws IOException {
            super.readFields(in);
            this._isShrinked = in.readBoolean();
            if (this._isShrinked) {
                this.shrinkedLength = in.readLong();
            }
        }
        
        public void write(final DataOutput out) throws IOException {
            super.write(out);
            out.writeBoolean(this._isShrinked);
            if (this._isShrinked) {
                out.writeLong(this.shrinkedLength);
            }
        }
    }
    
    public static class CombineFileRecordReader<K, V> implements RecordReader<K, V>
    {
        static final Class[] constructorSignature;
        protected CombineFileSplit split;
        protected JobConf jc;
        protected Reporter reporter;
        protected Class<RecordReader<K, V>> rrClass;
        protected Constructor<RecordReader<K, V>> rrConstructor;
        protected FileSystem fs;
        protected int idx;
        protected long progress;
        protected RecordReader<K, V> curReader;
        protected boolean isShrinked;
        protected long shrinkedLength;
        
        public boolean next(final K key, final V value) throws IOException {
            while (this.curReader == null || !this.doNextWithExceptionHandler(((CombineHiveKey)key).getKey(), value)) {
                if (!this.initNextRecordReader(key)) {
                    return false;
                }
            }
            return true;
        }
        
        public K createKey() {
            final K newKey = (K)this.curReader.createKey();
            return (K)new CombineHiveKey(newKey);
        }
        
        public V createValue() {
            return (V)this.curReader.createValue();
        }
        
        public long getPos() throws IOException {
            return this.progress;
        }
        
        public void close() throws IOException {
            if (this.curReader != null) {
                this.curReader.close();
                this.curReader = null;
            }
        }
        
        public float getProgress() throws IOException {
            return Math.min(1.0f, this.progress / (float)this.split.getLength());
        }
        
        public CombineFileRecordReader(final JobConf job, final CombineFileSplit split, final Reporter reporter, final Class<RecordReader<K, V>> rrClass) throws IOException {
            this.split = split;
            this.jc = job;
            this.rrClass = rrClass;
            this.reporter = reporter;
            this.idx = 0;
            this.curReader = null;
            this.progress = 0L;
            this.isShrinked = false;
            assert split instanceof InputSplitShim;
            if (((InputSplitShim)split).isShrinked()) {
                this.isShrinked = true;
                this.shrinkedLength = ((InputSplitShim)split).getShrinkedLength();
            }
            try {
                (this.rrConstructor = rrClass.getDeclaredConstructor((Class<?>[])CombineFileRecordReader.constructorSignature)).setAccessible(true);
            }
            catch (Exception e) {
                throw new RuntimeException(rrClass.getName() + " does not have valid constructor", e);
            }
            this.initNextRecordReader(null);
        }
        
        private boolean doNextWithExceptionHandler(final K key, final V value) throws IOException {
            try {
                return this.curReader.next((Object)key, (Object)value);
            }
            catch (Exception e) {
                return HiveIOExceptionHandlerUtil.handleRecordReaderNextException(e, this.jc);
            }
        }
        
        protected boolean initNextRecordReader(final K key) throws IOException {
            if (this.curReader != null) {
                this.curReader.close();
                this.curReader = null;
                if (this.idx > 0) {
                    this.progress += this.split.getLength(this.idx - 1);
                }
            }
            if (this.idx == this.split.getNumPaths() || (this.isShrinked && this.progress > this.shrinkedLength)) {
                return false;
            }
            try {
                this.curReader = this.rrConstructor.newInstance(this.split, this.jc, this.reporter, this.idx);
                if (key != null) {
                    final K newKey = (K)this.curReader.createKey();
                    ((CombineHiveKey)key).setKey(newKey);
                }
                this.jc.set("map.input.file", this.split.getPath(this.idx).toString());
                this.jc.setLong("map.input.start", this.split.getOffset(this.idx));
                this.jc.setLong("map.input.length", this.split.getLength(this.idx));
            }
            catch (Exception e) {
                this.curReader = (RecordReader<K, V>)HiveIOExceptionHandlerUtil.handleRecordReaderCreationException(e, this.jc);
            }
            ++this.idx;
            return true;
        }
        
        static {
            constructorSignature = new Class[] { InputSplit.class, Configuration.class, Reporter.class, Integer.class };
        }
    }
    
    public abstract static class CombineFileInputFormatShim<K, V> extends CombineFileInputFormat<K, V> implements HadoopShims.CombineFileInputFormatShim<K, V>
    {
        public Path[] getInputPathsShim(final JobConf conf) {
            try {
                return FileInputFormat.getInputPaths(conf);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public void createPool(final JobConf conf, final PathFilter... filters) {
            super.createPool(conf, filters);
        }
        
        public CombineFileSplit[] getSplits(final JobConf job, final int numSplits) throws IOException {
            final long minSize = job.getLong((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZE"), 0L);
            if (job.getLong((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZEPERNODE"), 0L) == 0L) {
                super.setMinSplitSizeNode(minSize);
            }
            if (job.getLong((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMINSPLITSIZEPERRACK"), 0L) == 0L) {
                super.setMinSplitSizeRack(minSize);
            }
            if (job.getLong((String)ShimLoader.getHadoopShims().getHadoopConfNames().get("MAPREDMAXSPLITSIZE"), 0L) == 0L) {
                super.setMaxSplitSize(minSize);
            }
            final InputSplit[] splits = super.getSplits(job, numSplits);
            final ArrayList<InputSplitShim> inputSplitShims = new ArrayList<InputSplitShim>();
            for (int pos = 0; pos < splits.length; ++pos) {
                final CombineFileSplit split = (CombineFileSplit)splits[pos];
                if (split.getPaths().length > 0) {
                    inputSplitShims.add(new InputSplitShim(job, split.getPaths(), split.getStartOffsets(), split.getLengths(), split.getLocations()));
                }
            }
            return inputSplitShims.toArray(new InputSplitShim[inputSplitShims.size()]);
        }
        
        public InputSplitShim getInputSplitShim() throws IOException {
            return new InputSplitShim();
        }
        
        public RecordReader getRecordReader(final JobConf job, final CombineFileSplit split, final Reporter reporter, final Class<RecordReader<K, V>> rrClass) throws IOException {
            final CombineFileSplit cfSplit = split;
            return (RecordReader)new CombineFileRecordReader(job, cfSplit, reporter, (Class<org.apache.hadoop.mapred.RecordReader<Object, Object>>)rrClass);
        }
    }
}
