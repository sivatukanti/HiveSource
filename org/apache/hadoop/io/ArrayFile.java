// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.util.Progressable;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ArrayFile extends MapFile
{
    protected ArrayFile() {
    }
    
    public static class Writer extends MapFile.Writer
    {
        private LongWritable count;
        
        public Writer(final Configuration conf, final FileSystem fs, final String file, final Class<? extends Writable> valClass) throws IOException {
            super(conf, new Path(file), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(LongWritable.class), MapFile.Writer.valueClass(valClass) });
            this.count = new LongWritable(0L);
        }
        
        public Writer(final Configuration conf, final FileSystem fs, final String file, final Class<? extends Writable> valClass, final SequenceFile.CompressionType compress, final Progressable progress) throws IOException {
            super(conf, new Path(file), new SequenceFile.Writer.Option[] { MapFile.Writer.keyClass(LongWritable.class), MapFile.Writer.valueClass(valClass), MapFile.Writer.compression(compress), MapFile.Writer.progressable(progress) });
            this.count = new LongWritable(0L);
        }
        
        public synchronized void append(final Writable value) throws IOException {
            super.append(this.count, value);
            this.count.set(this.count.get() + 1L);
        }
    }
    
    public static class Reader extends MapFile.Reader
    {
        private LongWritable key;
        
        public Reader(final FileSystem fs, final String file, final Configuration conf) throws IOException {
            super(new Path(file), conf, new SequenceFile.Reader.Option[0]);
            this.key = new LongWritable();
        }
        
        public synchronized void seek(final long n) throws IOException {
            this.key.set(n);
            this.seek(this.key);
        }
        
        public synchronized Writable next(final Writable value) throws IOException {
            return this.next(this.key, value) ? value : null;
        }
        
        public synchronized long key() throws IOException {
            return this.key.get();
        }
        
        public synchronized Writable get(final long n, final Writable value) throws IOException {
            this.key.set(n);
            return this.get(this.key, value);
        }
    }
}
