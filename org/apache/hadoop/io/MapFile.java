// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.io.EOFException;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.DataInput;
import java.io.DataOutput;
import org.apache.hadoop.util.Options;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.util.Progressable;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class MapFile
{
    private static final Logger LOG;
    public static final String INDEX_FILE_NAME = "index";
    public static final String DATA_FILE_NAME = "data";
    
    protected MapFile() {
    }
    
    public static void rename(final FileSystem fs, final String oldName, final String newName) throws IOException {
        final Path oldDir = new Path(oldName);
        final Path newDir = new Path(newName);
        if (!fs.rename(oldDir, newDir)) {
            throw new IOException("Could not rename " + oldDir + " to " + newDir);
        }
    }
    
    public static void delete(final FileSystem fs, final String name) throws IOException {
        final Path dir = new Path(name);
        final Path data = new Path(dir, "data");
        final Path index = new Path(dir, "index");
        fs.delete(data, true);
        fs.delete(index, true);
        fs.delete(dir, true);
    }
    
    public static long fix(final FileSystem fs, final Path dir, final Class<? extends Writable> keyClass, final Class<? extends Writable> valueClass, final boolean dryrun, final Configuration conf) throws Exception {
        final String dr = dryrun ? "[DRY RUN ] " : "";
        final Path data = new Path(dir, "data");
        final Path index = new Path(dir, "index");
        final int indexInterval = conf.getInt("io.map.index.interval", 128);
        if (!fs.exists(data)) {
            throw new Exception(dr + "Missing data file in " + dir + ", impossible to fix this.");
        }
        if (fs.exists(index)) {
            return -1L;
        }
        final SequenceFile.Reader dataReader = new SequenceFile.Reader(conf, new SequenceFile.Reader.Option[] { SequenceFile.Reader.file(data) });
        if (!dataReader.getKeyClass().equals(keyClass)) {
            throw new Exception(dr + "Wrong key class in " + dir + ", expected" + keyClass.getName() + ", got " + dataReader.getKeyClass().getName());
        }
        if (!dataReader.getValueClass().equals(valueClass)) {
            throw new Exception(dr + "Wrong value class in " + dir + ", expected" + valueClass.getName() + ", got " + dataReader.getValueClass().getName());
        }
        long cnt = 0L;
        final Writable key = ReflectionUtils.newInstance(keyClass, conf);
        final Writable value = ReflectionUtils.newInstance(valueClass, conf);
        SequenceFile.Writer indexWriter = null;
        if (!dryrun) {
            indexWriter = SequenceFile.createWriter(conf, SequenceFile.Writer.file(index), SequenceFile.Writer.keyClass(keyClass), SequenceFile.Writer.valueClass(LongWritable.class));
        }
        try {
            long lastIndexPos = -1L;
            long lastIndexKeyCount = Long.MIN_VALUE;
            long pos = dataReader.getPosition();
            final LongWritable position = new LongWritable();
            long nextBlock = pos;
            final boolean blockCompressed = dataReader.isBlockCompressed();
            while (dataReader.next(key, value)) {
                if (blockCompressed) {
                    final long curPos = dataReader.getPosition();
                    if (curPos > nextBlock) {
                        pos = nextBlock;
                        nextBlock = curPos;
                    }
                }
                if (cnt >= lastIndexKeyCount + indexInterval && pos > lastIndexPos) {
                    position.set(pos);
                    if (!dryrun) {
                        indexWriter.append(key, position);
                    }
                    lastIndexPos = pos;
                    lastIndexKeyCount = cnt;
                }
                if (!blockCompressed) {
                    pos = dataReader.getPosition();
                }
                ++cnt;
            }
        }
        catch (Throwable t) {}
        dataReader.close();
        if (!dryrun) {
            indexWriter.close();
        }
        return cnt;
    }
    
    public static void main(final String[] args) throws Exception {
        final String usage = "Usage: MapFile inFile outFile";
        if (args.length != 2) {
            System.err.println(usage);
            System.exit(-1);
        }
        final String in = args[0];
        final String out = args[1];
        final Configuration conf = new Configuration();
        final FileSystem fs = FileSystem.getLocal(conf);
        Reader reader = null;
        try {
            reader = new Reader(fs, in, conf);
            final WritableComparable<?> key = ReflectionUtils.newInstance(reader.getKeyClass().asSubclass(WritableComparable.class), conf);
            final Writable value = ReflectionUtils.newInstance(reader.getValueClass().asSubclass(Writable.class), conf);
            try (final Writer writer = new Writer(conf, fs, out, reader.getKeyClass().asSubclass(WritableComparable.class), reader.getValueClass())) {
                while (reader.next(key, value)) {
                    writer.append(key, value);
                }
            }
        }
        finally {
            IOUtils.cleanupWithLogger(MapFile.LOG, reader);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MapFile.class);
    }
    
    public static class Writer implements Closeable
    {
        private SequenceFile.Writer data;
        private SequenceFile.Writer index;
        private static final String INDEX_INTERVAL = "io.map.index.interval";
        private int indexInterval;
        private long size;
        private LongWritable position;
        private WritableComparator comparator;
        private DataInputBuffer inBuf;
        private DataOutputBuffer outBuf;
        private WritableComparable lastKey;
        private long lastIndexPos;
        private long lastIndexKeyCount;
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { keyClass(keyClass), valueClass(valClass) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass, final SequenceFile.CompressionType compress, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { keyClass(keyClass), valueClass(valClass), compression(compress), progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass, final SequenceFile.CompressionType compress, final CompressionCodec codec, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { keyClass(keyClass), valueClass(valClass), compression(compress, codec), progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass, final SequenceFile.CompressionType compress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { keyClass(keyClass), valueClass(valClass), compression(compress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { comparator(comparator), valueClass(valClass) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { comparator(comparator), valueClass(valClass), compression(compress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { comparator(comparator), valueClass(valClass), compression(compress), progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress, final CompressionCodec codec, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { comparator(comparator), valueClass(valClass), compression(compress, codec), progressable(progress) });
        }
        
        public static Option keyClass(final Class<? extends WritableComparable> value) {
            return new KeyClassOption(value);
        }
        
        public static Option comparator(final WritableComparator value) {
            return new ComparatorOption(value);
        }
        
        public static SequenceFile.Writer.Option valueClass(final Class<?> value) {
            return SequenceFile.Writer.valueClass(value);
        }
        
        public static SequenceFile.Writer.Option compression(final SequenceFile.CompressionType type) {
            return SequenceFile.Writer.compression(type);
        }
        
        public static SequenceFile.Writer.Option compression(final SequenceFile.CompressionType type, final CompressionCodec codec) {
            return SequenceFile.Writer.compression(type, codec);
        }
        
        public static SequenceFile.Writer.Option progressable(final Progressable value) {
            return SequenceFile.Writer.progressable(value);
        }
        
        public Writer(final Configuration conf, final Path dirName, final SequenceFile.Writer.Option... opts) throws IOException {
            this.indexInterval = 128;
            this.position = new LongWritable();
            this.inBuf = new DataInputBuffer();
            this.outBuf = new DataOutputBuffer();
            this.lastIndexPos = -1L;
            this.lastIndexKeyCount = Long.MIN_VALUE;
            final KeyClassOption keyClassOption = Options.getOption(KeyClassOption.class, opts);
            final ComparatorOption comparatorOption = Options.getOption(ComparatorOption.class, opts);
            if (keyClassOption == null == (comparatorOption == null)) {
                throw new IllegalArgumentException("key class or comparator option must be set");
            }
            this.indexInterval = conf.getInt("io.map.index.interval", this.indexInterval);
            Class<? extends WritableComparable> keyClass;
            if (keyClassOption == null) {
                this.comparator = comparatorOption.getValue();
                keyClass = this.comparator.getKeyClass();
            }
            else {
                keyClass = (Class<? extends WritableComparable>)keyClassOption.getValue();
                this.comparator = WritableComparator.get(keyClass, conf);
            }
            this.lastKey = this.comparator.newKey();
            final FileSystem fs = dirName.getFileSystem(conf);
            if (!fs.mkdirs(dirName)) {
                throw new IOException("Mkdirs failed to create directory " + dirName);
            }
            final Path dataFile = new Path(dirName, "data");
            final Path indexFile = new Path(dirName, "index");
            final SequenceFile.Writer.Option[] dataOptions = Options.prependOptions(opts, SequenceFile.Writer.file(dataFile), SequenceFile.Writer.keyClass(keyClass));
            this.data = SequenceFile.createWriter(conf, dataOptions);
            final SequenceFile.Writer.Option[] indexOptions = Options.prependOptions(opts, SequenceFile.Writer.file(indexFile), SequenceFile.Writer.keyClass(keyClass), SequenceFile.Writer.valueClass(LongWritable.class), SequenceFile.Writer.compression(SequenceFile.CompressionType.BLOCK));
            this.index = SequenceFile.createWriter(conf, indexOptions);
        }
        
        public int getIndexInterval() {
            return this.indexInterval;
        }
        
        public void setIndexInterval(final int interval) {
            this.indexInterval = interval;
        }
        
        public static void setIndexInterval(final Configuration conf, final int interval) {
            conf.setInt("io.map.index.interval", interval);
        }
        
        @Override
        public synchronized void close() throws IOException {
            this.data.close();
            this.index.close();
        }
        
        public synchronized void append(final WritableComparable key, final Writable val) throws IOException {
            this.checkKey(key);
            final long pos = this.data.getLength();
            if (this.size >= this.lastIndexKeyCount + this.indexInterval && pos > this.lastIndexPos) {
                this.position.set(pos);
                this.index.append(key, this.position);
                this.lastIndexPos = pos;
                this.lastIndexKeyCount = this.size;
            }
            this.data.append(key, val);
            ++this.size;
        }
        
        private void checkKey(final WritableComparable key) throws IOException {
            if (this.size != 0L && this.comparator.compare(this.lastKey, key) > 0) {
                throw new IOException("key out of order: " + key + " after " + this.lastKey);
            }
            this.outBuf.reset();
            key.write(this.outBuf);
            this.inBuf.reset(this.outBuf.getData(), this.outBuf.getLength());
            this.lastKey.readFields(this.inBuf);
        }
        
        private static class KeyClassOption extends Options.ClassOption implements Option
        {
            KeyClassOption(final Class<?> value) {
                super(value);
            }
        }
        
        private static class ComparatorOption implements Option
        {
            private final WritableComparator value;
            
            ComparatorOption(final WritableComparator value) {
                this.value = value;
            }
            
            WritableComparator getValue() {
                return this.value;
            }
        }
        
        public interface Option extends SequenceFile.Writer.Option
        {
        }
    }
    
    public static class Reader implements Closeable
    {
        private int INDEX_SKIP;
        private WritableComparator comparator;
        private WritableComparable nextKey;
        private long seekPosition;
        private int seekIndex;
        private long firstPosition;
        private SequenceFile.Reader data;
        private SequenceFile.Reader index;
        private boolean indexClosed;
        private int count;
        private WritableComparable[] keys;
        private long[] positions;
        
        public Class<?> getKeyClass() {
            return this.data.getKeyClass();
        }
        
        public Class<?> getValueClass() {
            return this.data.getValueClass();
        }
        
        public static Option comparator(final WritableComparator value) {
            return new ComparatorOption(value);
        }
        
        public Reader(final Path dir, final Configuration conf, final SequenceFile.Reader.Option... opts) throws IOException {
            this.INDEX_SKIP = 0;
            this.seekPosition = -1L;
            this.seekIndex = -1;
            this.indexClosed = false;
            this.count = -1;
            final ComparatorOption comparatorOption = Options.getOption(ComparatorOption.class, opts);
            final WritableComparator comparator = (comparatorOption == null) ? null : comparatorOption.getValue();
            this.INDEX_SKIP = conf.getInt("io.map.index.skip", 0);
            this.open(dir, comparator, conf, opts);
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final String dirName, final Configuration conf) throws IOException {
            this(new Path(dirName), conf, new SequenceFile.Reader.Option[0]);
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final String dirName, final WritableComparator comparator, final Configuration conf) throws IOException {
            this(new Path(dirName), conf, new SequenceFile.Reader.Option[] { comparator(comparator) });
        }
        
        protected synchronized void open(final Path dir, final WritableComparator comparator, final Configuration conf, final SequenceFile.Reader.Option... options) throws IOException {
            final Path dataFile = new Path(dir, "data");
            final Path indexFile = new Path(dir, "index");
            this.data = this.createDataFileReader(dataFile, conf, options);
            this.firstPosition = this.data.getPosition();
            if (comparator == null) {
                final Class<? extends WritableComparable> cls = this.data.getKeyClass().asSubclass(WritableComparable.class);
                this.comparator = WritableComparator.get(cls, conf);
            }
            else {
                this.comparator = comparator;
            }
            final SequenceFile.Reader.Option[] indexOptions = Options.prependOptions(options, SequenceFile.Reader.file(indexFile));
            this.index = new SequenceFile.Reader(conf, indexOptions);
        }
        
        protected SequenceFile.Reader createDataFileReader(final Path dataFile, final Configuration conf, final SequenceFile.Reader.Option... options) throws IOException {
            final SequenceFile.Reader.Option[] newOptions = Options.prependOptions(options, SequenceFile.Reader.file(dataFile));
            return new SequenceFile.Reader(conf, newOptions);
        }
        
        private void readIndex() throws IOException {
            if (this.keys != null) {
                return;
            }
            this.count = 0;
            this.positions = new long[1024];
            try {
                int skip = this.INDEX_SKIP;
                final LongWritable position = new LongWritable();
                WritableComparable lastKey = null;
                final long lastIndex = -1L;
                final ArrayList<WritableComparable> keyBuilder = new ArrayList<WritableComparable>(1024);
                while (true) {
                    final WritableComparable k = this.comparator.newKey();
                    if (!this.index.next(k, position)) {
                        this.keys = keyBuilder.toArray(new WritableComparable[this.count]);
                        this.positions = Arrays.copyOf(this.positions, this.count);
                        break;
                    }
                    if (lastKey != null && this.comparator.compare(lastKey, k) > 0) {
                        throw new IOException("key out of order: " + k + " after " + lastKey);
                    }
                    lastKey = k;
                    if (skip > 0) {
                        --skip;
                    }
                    else {
                        skip = this.INDEX_SKIP;
                        if (position.get() == lastIndex) {
                            continue;
                        }
                        if (this.count == this.positions.length) {
                            this.positions = Arrays.copyOf(this.positions, this.positions.length * 2);
                        }
                        keyBuilder.add(k);
                        this.positions[this.count] = position.get();
                        ++this.count;
                    }
                }
            }
            catch (EOFException e) {
                MapFile.LOG.warn("Unexpected EOF reading " + this.index + " at entry #" + this.count + ".  Ignoring.");
            }
            finally {
                this.indexClosed = true;
                this.index.close();
            }
        }
        
        public synchronized void reset() throws IOException {
            this.data.seek(this.firstPosition);
        }
        
        public synchronized WritableComparable midKey() throws IOException {
            this.readIndex();
            if (this.count == 0) {
                return null;
            }
            return this.keys[(this.count - 1) / 2];
        }
        
        public synchronized void finalKey(final WritableComparable key) throws IOException {
            final long originalPosition = this.data.getPosition();
            try {
                this.readIndex();
                if (this.count > 0) {
                    this.data.seek(this.positions[this.count - 1]);
                }
                else {
                    this.reset();
                }
                while (this.data.next(key)) {}
            }
            finally {
                this.data.seek(originalPosition);
            }
        }
        
        public synchronized boolean seek(final WritableComparable key) throws IOException {
            return this.seekInternal(key) == 0;
        }
        
        private synchronized int seekInternal(final WritableComparable key) throws IOException {
            return this.seekInternal(key, false);
        }
        
        private synchronized int seekInternal(final WritableComparable key, final boolean before) throws IOException {
            this.readIndex();
            if (this.seekIndex == -1 || this.seekIndex + 1 >= this.count || this.comparator.compare(key, this.keys[this.seekIndex + 1]) >= 0 || this.comparator.compare(key, this.nextKey) < 0) {
                this.seekIndex = this.binarySearch(key);
                if (this.seekIndex < 0) {
                    this.seekIndex = -this.seekIndex - 2;
                }
                if (this.seekIndex == -1) {
                    this.seekPosition = this.firstPosition;
                }
                else {
                    this.seekPosition = this.positions[this.seekIndex];
                }
            }
            this.data.seek(this.seekPosition);
            if (this.nextKey == null) {
                this.nextKey = this.comparator.newKey();
            }
            long prevPosition = -1L;
            long curPosition = this.seekPosition;
            while (this.data.next(this.nextKey)) {
                final int c = this.comparator.compare(key, this.nextKey);
                if (c <= 0) {
                    if (before && c != 0) {
                        if (prevPosition != -1L) {
                            this.data.seek(prevPosition);
                            this.data.next(this.nextKey);
                            return 1;
                        }
                        this.data.seek(curPosition);
                    }
                    return c;
                }
                if (!before) {
                    continue;
                }
                prevPosition = curPosition;
                curPosition = this.data.getPosition();
            }
            return 1;
        }
        
        private int binarySearch(final WritableComparable key) {
            int low = 0;
            int high = this.count - 1;
            while (low <= high) {
                final int mid = low + high >>> 1;
                final WritableComparable midVal = this.keys[mid];
                final int cmp = this.comparator.compare(midVal, key);
                if (cmp < 0) {
                    low = mid + 1;
                }
                else {
                    if (cmp <= 0) {
                        return mid;
                    }
                    high = mid - 1;
                }
            }
            return -(low + 1);
        }
        
        public synchronized boolean next(final WritableComparable key, final Writable val) throws IOException {
            return this.data.next(key, val);
        }
        
        public synchronized Writable get(final WritableComparable key, final Writable val) throws IOException {
            if (this.seek(key)) {
                this.data.getCurrentValue(val);
                return val;
            }
            return null;
        }
        
        public synchronized WritableComparable getClosest(final WritableComparable key, final Writable val) throws IOException {
            return this.getClosest(key, val, false);
        }
        
        public synchronized WritableComparable getClosest(final WritableComparable key, final Writable val, final boolean before) throws IOException {
            final int c = this.seekInternal(key, before);
            if ((!before && c > 0) || (before && c < 0)) {
                return null;
            }
            this.data.getCurrentValue(val);
            return this.nextKey;
        }
        
        @Override
        public synchronized void close() throws IOException {
            if (!this.indexClosed) {
                this.index.close();
            }
            this.data.close();
        }
        
        static class ComparatorOption implements Option
        {
            private final WritableComparator value;
            
            ComparatorOption(final WritableComparator value) {
                this.value = value;
            }
            
            WritableComparator getValue() {
                return this.value;
            }
        }
        
        public interface Option extends SequenceFile.Reader.Option
        {
        }
    }
    
    public static class Merger
    {
        private Configuration conf;
        private WritableComparator comparator;
        private Reader[] inReaders;
        private Writer outWriter;
        private Class<Writable> valueClass;
        private Class<WritableComparable> keyClass;
        
        public Merger(final Configuration conf) throws IOException {
            this.comparator = null;
            this.valueClass = null;
            this.keyClass = null;
            this.conf = conf;
        }
        
        public void merge(final Path[] inMapFiles, final boolean deleteInputs, final Path outMapFile) throws IOException {
            try {
                this.open(inMapFiles, outMapFile);
                this.mergePass();
            }
            finally {
                this.close();
            }
            if (deleteInputs) {
                for (int i = 0; i < inMapFiles.length; ++i) {
                    final Path path = inMapFiles[i];
                    MapFile.delete(path.getFileSystem(this.conf), path.toString());
                }
            }
        }
        
        private void open(final Path[] inMapFiles, final Path outMapFile) throws IOException {
            this.inReaders = new Reader[inMapFiles.length];
            for (int i = 0; i < inMapFiles.length; ++i) {
                final Reader reader = new Reader(inMapFiles[i], this.conf, new SequenceFile.Reader.Option[0]);
                if (this.keyClass == null || this.valueClass == null) {
                    this.keyClass = (Class<WritableComparable>)reader.getKeyClass();
                    this.valueClass = (Class<Writable>)reader.getValueClass();
                }
                else if (this.keyClass != reader.getKeyClass() || this.valueClass != reader.getValueClass()) {
                    throw new HadoopIllegalArgumentException("Input files cannot be merged as they have different Key and Value classes");
                }
                this.inReaders[i] = reader;
            }
            if (this.comparator == null) {
                final Class<? extends WritableComparable> cls = this.keyClass.asSubclass(WritableComparable.class);
                this.comparator = WritableComparator.get(cls, this.conf);
            }
            else if (this.comparator.getKeyClass() != this.keyClass) {
                throw new HadoopIllegalArgumentException("Input files cannot be merged as they have different Key class compared to specified comparator");
            }
            this.outWriter = new Writer(this.conf, outMapFile, new SequenceFile.Writer.Option[] { Writer.keyClass(this.keyClass), Writer.valueClass(this.valueClass) });
        }
        
        private void mergePass() throws IOException {
            final WritableComparable[] keys = new WritableComparable[this.inReaders.length];
            final Writable[] values = new Writable[this.inReaders.length];
            for (int i = 0; i < this.inReaders.length; ++i) {
                keys[i] = ReflectionUtils.newInstance(this.keyClass, null);
                values[i] = ReflectionUtils.newInstance(this.valueClass, null);
                if (!this.inReaders[i].next(keys[i], values[i])) {
                    keys[i] = null;
                    values[i] = null;
                }
            }
            while (true) {
                int currentEntry = -1;
                WritableComparable currentKey = null;
                Writable currentValue = null;
                for (int j = 0; j < keys.length; ++j) {
                    if (keys[j] != null) {
                        if (currentKey == null || this.comparator.compare(currentKey, keys[j]) > 0) {
                            currentEntry = j;
                            currentKey = keys[j];
                            currentValue = values[j];
                        }
                    }
                }
                if (currentKey == null) {
                    break;
                }
                this.outWriter.append(currentKey, currentValue);
                if (this.inReaders[currentEntry].next(keys[currentEntry], values[currentEntry])) {
                    continue;
                }
                keys[currentEntry] = null;
                values[currentEntry] = null;
            }
        }
        
        private void close() throws IOException {
            for (int i = 0; i < this.inReaders.length; ++i) {
                IOUtils.closeStream(this.inReaders[i]);
                this.inReaders[i] = null;
            }
            if (this.outWriter != null) {
                this.outWriter.close();
                this.outWriter = null;
            }
        }
    }
}
