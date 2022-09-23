// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.util.bloom.Filter;
import java.io.DataInputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import org.apache.hadoop.util.hash.Hash;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.bloom.DynamicBloomFilter;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class BloomMapFile
{
    private static final Logger LOG;
    public static final String BLOOM_FILE_NAME = "bloom";
    public static final int HASH_COUNT = 5;
    
    public static void delete(final FileSystem fs, final String name) throws IOException {
        final Path dir = new Path(name);
        final Path data = new Path(dir, "data");
        final Path index = new Path(dir, "index");
        final Path bloom = new Path(dir, "bloom");
        fs.delete(data, true);
        fs.delete(index, true);
        fs.delete(bloom, true);
        fs.delete(dir, true);
    }
    
    private static byte[] byteArrayForBloomKey(final DataOutputBuffer buf) {
        final int cleanLength = buf.getLength();
        byte[] ba = buf.getData();
        if (cleanLength != ba.length) {
            ba = new byte[cleanLength];
            System.arraycopy(buf.getData(), 0, ba, 0, cleanLength);
        }
        return ba;
    }
    
    static {
        LOG = LoggerFactory.getLogger(BloomMapFile.class);
    }
    
    public static class Writer extends MapFile.Writer
    {
        private DynamicBloomFilter bloomFilter;
        private int numKeys;
        private int vectorSize;
        private Key bloomKey;
        private DataOutputBuffer buf;
        private FileSystem fs;
        private Path dir;
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class<? extends Writable> valClass, final SequenceFile.CompressionType compress, final CompressionCodec codec, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(keyClass), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress, codec), MapFile.Writer.progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass, final SequenceFile.CompressionType compress, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(keyClass), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress), MapFile.Writer.progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass, final SequenceFile.CompressionType compress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(keyClass), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress, final CompressionCodec codec, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.comparator(comparator), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress, codec), MapFile.Writer.progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress, final Progressable progress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.comparator(comparator), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress), MapFile.Writer.progressable(progress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass, final SequenceFile.CompressionType compress) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.comparator(comparator), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final Class valClass) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.comparator(comparator), MapFile.Writer.valueClass(valClass) });
        }
        
        @Deprecated
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final Class valClass) throws IOException {
            this(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(keyClass), MapFile.Writer.valueClass(valClass) });
        }
        
        public Writer(final Configuration conf, final Path dir, final SequenceFile.Writer.Option... options) throws IOException {
            super(conf, dir, options);
            this.bloomKey = new Key();
            this.buf = new DataOutputBuffer();
            this.fs = dir.getFileSystem(conf);
            this.dir = dir;
            this.initBloomFilter(conf);
        }
        
        private synchronized void initBloomFilter(final Configuration conf) {
            this.numKeys = conf.getInt("io.mapfile.bloom.size", 1048576);
            final float errorRate = conf.getFloat("io.mapfile.bloom.error.rate", 0.005f);
            this.vectorSize = (int)Math.ceil(-5 * this.numKeys / Math.log(1.0 - Math.pow(errorRate, 0.2)));
            this.bloomFilter = new DynamicBloomFilter(this.vectorSize, 5, Hash.getHashType(conf), this.numKeys);
        }
        
        @Override
        public synchronized void append(final WritableComparable key, final Writable val) throws IOException {
            super.append(key, val);
            this.buf.reset();
            key.write(this.buf);
            this.bloomKey.set(byteArrayForBloomKey(this.buf), 1.0);
            this.bloomFilter.add(this.bloomKey);
        }
        
        @Override
        public synchronized void close() throws IOException {
            super.close();
            DataOutputStream out = this.fs.create(new Path(this.dir, "bloom"), true);
            try {
                this.bloomFilter.write(out);
                out.flush();
                out.close();
                out = null;
            }
            finally {
                IOUtils.closeStream(out);
            }
        }
    }
    
    public static class Reader extends MapFile.Reader
    {
        private DynamicBloomFilter bloomFilter;
        private DataOutputBuffer buf;
        private Key bloomKey;
        
        public Reader(final Path dir, final Configuration conf, final SequenceFile.Reader.Option... options) throws IOException {
            super(dir, conf, options);
            this.buf = new DataOutputBuffer();
            this.bloomKey = new Key();
            this.initBloomFilter(dir, conf);
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final String dirName, final Configuration conf) throws IOException {
            this(new Path(dirName), conf, new SequenceFile.Reader.Option[0]);
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final String dirName, final WritableComparator comparator, final Configuration conf, final boolean open) throws IOException {
            this(new Path(dirName), conf, new SequenceFile.Reader.Option[] { MapFile.Reader.comparator(comparator) });
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final String dirName, final WritableComparator comparator, final Configuration conf) throws IOException {
            this(new Path(dirName), conf, new SequenceFile.Reader.Option[] { MapFile.Reader.comparator(comparator) });
        }
        
        private void initBloomFilter(final Path dirName, final Configuration conf) {
            DataInputStream in = null;
            try {
                final FileSystem fs = dirName.getFileSystem(conf);
                in = fs.open(new Path(dirName, "bloom"));
                (this.bloomFilter = new DynamicBloomFilter()).readFields(in);
                in.close();
                in = null;
            }
            catch (IOException ioe) {
                BloomMapFile.LOG.warn("Can't open BloomFilter: " + ioe + " - fallback to MapFile.");
                this.bloomFilter = null;
            }
            finally {
                IOUtils.closeStream(in);
            }
        }
        
        public boolean probablyHasKey(final WritableComparable key) throws IOException {
            if (this.bloomFilter == null) {
                return true;
            }
            this.buf.reset();
            key.write(this.buf);
            this.bloomKey.set(byteArrayForBloomKey(this.buf), 1.0);
            return this.bloomFilter.membershipTest(this.bloomKey);
        }
        
        @Override
        public synchronized Writable get(final WritableComparable key, final Writable val) throws IOException {
            if (!this.probablyHasKey(key)) {
                return null;
            }
            return super.get(key, val);
        }
        
        public Filter getBloomFilter() {
            return this.bloomFilter;
        }
    }
}
