// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.avro.io.Encoder;
import org.apache.avro.Schema;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.file.DataFileReader;
import org.apache.hadoop.fs.AvroFSInput;
import org.apache.hadoop.fs.FileContext;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.file.FileReader;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import java.util.zip.GZIPInputStream;
import java.io.EOFException;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.OutputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.InputStream;
import org.apache.hadoop.fs.PathIsDirectoryException;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class Display extends FsCommand
{
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Cat.class, "-cat");
        factory.addClass(Text.class, "-text");
        factory.addClass(Checksum.class, "-checksum");
    }
    
    public static class Cat extends Display
    {
        public static final String NAME = "cat";
        public static final String USAGE = "[-ignoreCrc] <src> ...";
        public static final String DESCRIPTION = "Fetch all files that match the file pattern <src> and display their content on stdout.\n";
        private boolean verifyChecksum;
        
        public Cat() {
            this.verifyChecksum = true;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "ignoreCrc" });
            cf.parse(args);
            this.verifyChecksum = !cf.getOpt("ignoreCrc");
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (item.stat.isDirectory()) {
                throw new PathIsDirectoryException(item.toString());
            }
            item.fs.setVerifyChecksum(this.verifyChecksum);
            this.printToStdout(this.getInputStream(item));
        }
        
        private void printToStdout(final InputStream in) throws IOException {
            try {
                IOUtils.copyBytes(in, this.out, this.getConf(), false);
            }
            finally {
                in.close();
            }
        }
        
        protected InputStream getInputStream(final PathData item) throws IOException {
            return item.fs.open(item.path);
        }
    }
    
    public static class Text extends Cat
    {
        public static final String NAME = "text";
        public static final String USAGE = "[-ignoreCrc] <src> ...";
        public static final String DESCRIPTION = "Takes a source file and outputs the file in text format.\nThe allowed formats are zip and TextRecordInputStream and Avro.";
        
        @Override
        protected InputStream getInputStream(final PathData item) throws IOException {
            final FSDataInputStream i = (FSDataInputStream)super.getInputStream(item);
            short leadBytes;
            try {
                leadBytes = i.readShort();
            }
            catch (EOFException e) {
                i.seek(0L);
                return i;
            }
            Label_0168: {
                switch (leadBytes) {
                    case 8075: {
                        i.seek(0L);
                        return new GZIPInputStream(i);
                    }
                    case 21317: {
                        if (i.readByte() == 81) {
                            i.close();
                            return new TextRecordInputStream(item.stat);
                        }
                        break;
                    }
                    case 20322: {
                        if (i.readByte() == 106) {
                            i.close();
                            return new AvroFileInputStream(item.stat);
                        }
                        break Label_0168;
                    }
                }
                final CompressionCodecFactory cf = new CompressionCodecFactory(this.getConf());
                final CompressionCodec codec = cf.getCodec(item.path);
                if (codec != null) {
                    i.seek(0L);
                    return codec.createInputStream(i);
                }
            }
            i.seek(0L);
            return i;
        }
    }
    
    public static class Checksum extends Display
    {
        public static final String NAME = "checksum";
        public static final String USAGE = "<src> ...";
        public static final String DESCRIPTION = "Dump checksum information for files that match the file pattern <src> to stdout. Note that this requires a round-trip to a datanode storing each block of the file, and thus is not efficient to run on a large number of files. The checksum of a file depends on its content, block size and the checksum algorithm and parameters used for creating the file.";
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (item.stat.isDirectory()) {
                throw new PathIsDirectoryException(item.toString());
            }
            final FileChecksum checksum = item.fs.getFileChecksum(item.path);
            if (checksum == null) {
                this.out.printf("%s\tNONE\t%n", item.toString());
            }
            else {
                final String checksumString = StringUtils.byteToHexString(checksum.getBytes(), 0, checksum.getLength());
                this.out.printf("%s\t%s\t%s%n", item.toString(), checksum.getAlgorithmName(), checksumString);
            }
        }
    }
    
    protected class TextRecordInputStream extends InputStream
    {
        SequenceFile.Reader r;
        Writable key;
        Writable val;
        DataInputBuffer inbuf;
        DataOutputBuffer outbuf;
        
        public TextRecordInputStream(final FileStatus f) throws IOException {
            final Path fpath = f.getPath();
            final Configuration lconf = Display.this.getConf();
            this.r = new SequenceFile.Reader(lconf, new SequenceFile.Reader.Option[] { SequenceFile.Reader.file(fpath) });
            this.key = ReflectionUtils.newInstance(this.r.getKeyClass().asSubclass(Writable.class), lconf);
            this.val = ReflectionUtils.newInstance(this.r.getValueClass().asSubclass(Writable.class), lconf);
            this.inbuf = new DataInputBuffer();
            this.outbuf = new DataOutputBuffer();
        }
        
        @Override
        public int read() throws IOException {
            int ret;
            if (null == this.inbuf || -1 == (ret = this.inbuf.read())) {
                if (!this.r.next(this.key, this.val)) {
                    return -1;
                }
                byte[] tmp = this.key.toString().getBytes(StandardCharsets.UTF_8);
                this.outbuf.write(tmp, 0, tmp.length);
                this.outbuf.write(9);
                tmp = this.val.toString().getBytes(StandardCharsets.UTF_8);
                this.outbuf.write(tmp, 0, tmp.length);
                this.outbuf.write(10);
                this.inbuf.reset(this.outbuf.getData(), this.outbuf.getLength());
                this.outbuf.reset();
                ret = this.inbuf.read();
            }
            return ret;
        }
        
        @Override
        public void close() throws IOException {
            this.r.close();
            super.close();
        }
    }
    
    protected static class AvroFileInputStream extends InputStream
    {
        private int pos;
        private byte[] buffer;
        private ByteArrayOutputStream output;
        private FileReader<?> fileReader;
        private DatumWriter<Object> writer;
        private JsonEncoder encoder;
        
        public AvroFileInputStream(final FileStatus status) throws IOException {
            this.pos = 0;
            this.buffer = new byte[0];
            final GenericDatumReader<Object> reader = new GenericDatumReader<Object>();
            final FileContext fc = FileContext.getFileContext(new Configuration());
            this.fileReader = DataFileReader.openReader(new AvroFSInput(fc, status.getPath()), (DatumReader<?>)reader);
            final Schema schema = this.fileReader.getSchema();
            this.writer = new GenericDatumWriter<Object>(schema);
            this.output = new ByteArrayOutputStream();
            this.encoder = EncoderFactory.get().jsonEncoder(schema, this.output);
        }
        
        @Override
        public int read() throws IOException {
            if (this.pos < this.buffer.length) {
                return this.buffer[this.pos++];
            }
            if (!this.fileReader.hasNext()) {
                return -1;
            }
            this.writer.write(this.fileReader.next(), this.encoder);
            this.encoder.flush();
            if (!this.fileReader.hasNext()) {
                this.output.write(System.getProperty("line.separator").getBytes(StandardCharsets.UTF_8));
                this.output.flush();
            }
            this.pos = 0;
            this.buffer = this.output.toByteArray();
            this.output.reset();
            return this.read();
        }
        
        @Override
        public void close() throws IOException {
            this.fileReader.close();
            this.output.close();
            super.close();
        }
    }
}
