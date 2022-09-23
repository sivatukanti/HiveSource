// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.fs.Path;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SetFile extends MapFile
{
    protected SetFile() {
    }
    
    public static class Writer extends MapFile.Writer
    {
        @Deprecated
        public Writer(final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass) throws IOException {
            super(new Configuration(), fs, dirName, keyClass, NullWritable.class);
        }
        
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final Class<? extends WritableComparable> keyClass, final SequenceFile.CompressionType compress) throws IOException {
            this(conf, fs, dirName, WritableComparator.get(keyClass, conf), compress);
        }
        
        public Writer(final Configuration conf, final FileSystem fs, final String dirName, final WritableComparator comparator, final SequenceFile.CompressionType compress) throws IOException {
            super(conf, new Path(dirName), new SequenceFile.Writer.Option[] { MapFile.Writer.comparator(comparator), MapFile.Writer.valueClass(NullWritable.class), MapFile.Writer.compression(compress) });
        }
        
        public void append(final WritableComparable key) throws IOException {
            this.append(key, NullWritable.get());
        }
    }
    
    public static class Reader extends MapFile.Reader
    {
        public Reader(final FileSystem fs, final String dirName, final Configuration conf) throws IOException {
            super(fs, dirName, conf);
        }
        
        public Reader(final FileSystem fs, final String dirName, final WritableComparator comparator, final Configuration conf) throws IOException {
            super(new Path(dirName), conf, new SequenceFile.Reader.Option[] { MapFile.Reader.comparator(comparator) });
        }
        
        @Override
        public boolean seek(final WritableComparable key) throws IOException {
            return super.seek(key);
        }
        
        public boolean next(final WritableComparable key) throws IOException {
            return this.next(key, NullWritable.get());
        }
        
        public WritableComparable get(final WritableComparable key) throws IOException {
            if (this.seek(key)) {
                this.next(key);
                return key;
            }
            return null;
        }
    }
}
