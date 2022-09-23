// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.fs.ChecksumFileSystem;
import org.apache.hadoop.fs.LocalDirAllocator;
import org.apache.hadoop.util.Progress;
import org.apache.hadoop.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import org.apache.hadoop.util.MergeSort;
import org.apache.hadoop.fs.ChecksumException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configurable;
import java.io.EOFException;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.compress.DefaultCodec;
import java.io.BufferedOutputStream;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.OutputStream;
import org.apache.hadoop.io.serializer.SerializationFactory;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.io.compress.GzipCodec;
import java.nio.charset.StandardCharsets;
import java.rmi.server.UID;
import org.apache.hadoop.util.Time;
import java.security.MessageDigest;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.fs.Syncable;
import java.io.Closeable;
import java.io.DataInput;
import java.util.Iterator;
import java.util.Map;
import java.io.DataOutput;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.InputStream;
import org.apache.hadoop.io.compress.CompressionInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.util.EnumSet;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import java.io.IOException;
import org.apache.hadoop.util.Options;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SequenceFile
{
    private static final Logger LOG;
    private static final byte BLOCK_COMPRESS_VERSION = 4;
    private static final byte CUSTOM_COMPRESS_VERSION = 5;
    private static final byte VERSION_WITH_METADATA = 6;
    private static byte[] VERSION;
    private static final int SYNC_ESCAPE = -1;
    private static final int SYNC_HASH_SIZE = 16;
    private static final int SYNC_SIZE = 20;
    public static final int SYNC_INTERVAL = 102400;
    
    private SequenceFile() {
    }
    
    public static CompressionType getDefaultCompressionType(final Configuration job) {
        final String name = job.get("io.seqfile.compression.type");
        return (name == null) ? CompressionType.RECORD : CompressionType.valueOf(name);
    }
    
    public static void setDefaultCompressionType(final Configuration job, final CompressionType val) {
        job.set("io.seqfile.compression.type", val.toString());
    }
    
    public static Writer createWriter(final Configuration conf, Writer.Option... opts) throws IOException {
        final Writer.CompressionOption compressionOption = Options.getOption(Writer.CompressionOption.class, opts);
        CompressionType kind;
        if (compressionOption != null) {
            kind = compressionOption.getValue();
        }
        else {
            kind = getDefaultCompressionType(conf);
            opts = Options.prependOptions(opts, Writer.compression(kind));
        }
        switch (kind) {
            default: {
                return new Writer(conf, opts);
            }
            case RECORD: {
                return new RecordCompressWriter(conf, opts);
            }
            case BLOCK: {
                return new BlockCompressWriter(conf, opts);
            }
        }
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass) throws IOException {
        return createWriter(conf, filesystem(fs), Writer.file(name), Writer.keyClass(keyClass), Writer.valueClass(valClass));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType) throws IOException {
        return createWriter(conf, filesystem(fs), Writer.file(name), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType, final Progressable progress) throws IOException {
        return createWriter(conf, Writer.file(name), filesystem(fs), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType), Writer.progressable(progress));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec) throws IOException {
        return createWriter(conf, Writer.file(name), filesystem(fs), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType, codec));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec, final Progressable progress, final Metadata metadata) throws IOException {
        return createWriter(conf, Writer.file(name), filesystem(fs), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType, codec), Writer.progressable(progress), Writer.metadata(metadata));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final int bufferSize, final short replication, final long blockSize, final CompressionType compressionType, final CompressionCodec codec, final Progressable progress, final Metadata metadata) throws IOException {
        return createWriter(conf, Writer.file(name), filesystem(fs), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.bufferSize(bufferSize), Writer.replication(replication), Writer.blockSize(blockSize), Writer.compression(compressionType, codec), Writer.progressable(progress), Writer.metadata(metadata));
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final int bufferSize, final short replication, final long blockSize, final boolean createParent, final CompressionType compressionType, final CompressionCodec codec, final Metadata metadata) throws IOException {
        return createWriter(FileContext.getFileContext(fs.getUri(), conf), conf, name, keyClass, valClass, compressionType, codec, metadata, EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE), org.apache.hadoop.fs.Options.CreateOpts.bufferSize(bufferSize), createParent ? org.apache.hadoop.fs.Options.CreateOpts.createParent() : org.apache.hadoop.fs.Options.CreateOpts.donotCreateParent(), org.apache.hadoop.fs.Options.CreateOpts.repFac(replication), org.apache.hadoop.fs.Options.CreateOpts.blockSize(blockSize));
    }
    
    public static Writer createWriter(final FileContext fc, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec, final Metadata metadata, final EnumSet<CreateFlag> createFlag, final org.apache.hadoop.fs.Options.CreateOpts... opts) throws IOException {
        return createWriter(conf, fc.create(name, createFlag, opts), keyClass, valClass, compressionType, codec, metadata).ownStream();
    }
    
    @Deprecated
    public static Writer createWriter(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec, final Progressable progress) throws IOException {
        return createWriter(conf, Writer.file(name), filesystem(fs), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType, codec), Writer.progressable(progress));
    }
    
    @Deprecated
    public static Writer createWriter(final Configuration conf, final FSDataOutputStream out, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec, final Metadata metadata) throws IOException {
        return createWriter(conf, Writer.stream(out), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType, codec), Writer.metadata(metadata));
    }
    
    @Deprecated
    public static Writer createWriter(final Configuration conf, final FSDataOutputStream out, final Class keyClass, final Class valClass, final CompressionType compressionType, final CompressionCodec codec) throws IOException {
        return createWriter(conf, Writer.stream(out), Writer.keyClass(keyClass), Writer.valueClass(valClass), Writer.compression(compressionType, codec));
    }
    
    private static int getBufferSize(final Configuration conf) {
        return conf.getInt("io.file.buffer.size", 4096);
    }
    
    static {
        LOG = LoggerFactory.getLogger(SequenceFile.class);
        SequenceFile.VERSION = new byte[] { 83, 69, 81, 6 };
    }
    
    public enum CompressionType
    {
        NONE, 
        RECORD, 
        BLOCK;
    }
    
    private static class UncompressedBytes implements ValueBytes
    {
        private int dataSize;
        private byte[] data;
        
        private UncompressedBytes() {
            this.data = null;
            this.dataSize = 0;
        }
        
        private void reset(final DataInputStream in, final int length) throws IOException {
            if (this.data == null) {
                this.data = new byte[length];
            }
            else if (length > this.data.length) {
                this.data = new byte[Math.max(length, this.data.length * 2)];
            }
            this.dataSize = -1;
            in.readFully(this.data, 0, length);
            this.dataSize = length;
        }
        
        @Override
        public int getSize() {
            return this.dataSize;
        }
        
        @Override
        public void writeUncompressedBytes(final DataOutputStream outStream) throws IOException {
            outStream.write(this.data, 0, this.dataSize);
        }
        
        @Override
        public void writeCompressedBytes(final DataOutputStream outStream) throws IllegalArgumentException, IOException {
            throw new IllegalArgumentException("UncompressedBytes cannot be compressed!");
        }
    }
    
    private static class CompressedBytes implements ValueBytes
    {
        private int dataSize;
        private byte[] data;
        DataInputBuffer rawData;
        CompressionCodec codec;
        CompressionInputStream decompressedStream;
        
        private CompressedBytes(final CompressionCodec codec) {
            this.rawData = null;
            this.codec = null;
            this.decompressedStream = null;
            this.data = null;
            this.dataSize = 0;
            this.codec = codec;
        }
        
        private void reset(final DataInputStream in, final int length) throws IOException {
            if (this.data == null) {
                this.data = new byte[length];
            }
            else if (length > this.data.length) {
                this.data = new byte[Math.max(length, this.data.length * 2)];
            }
            this.dataSize = -1;
            in.readFully(this.data, 0, length);
            this.dataSize = length;
        }
        
        @Override
        public int getSize() {
            return this.dataSize;
        }
        
        @Override
        public void writeUncompressedBytes(final DataOutputStream outStream) throws IOException {
            if (this.decompressedStream == null) {
                this.rawData = new DataInputBuffer();
                this.decompressedStream = this.codec.createInputStream(this.rawData);
            }
            else {
                this.decompressedStream.resetState();
            }
            this.rawData.reset(this.data, 0, this.dataSize);
            final byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = this.decompressedStream.read(buffer, 0, 8192)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
        
        @Override
        public void writeCompressedBytes(final DataOutputStream outStream) throws IllegalArgumentException, IOException {
            outStream.write(this.data, 0, this.dataSize);
        }
    }
    
    public static class Metadata implements Writable
    {
        private TreeMap<Text, Text> theMetadata;
        
        public Metadata() {
            this(new TreeMap<Text, Text>());
        }
        
        public Metadata(final TreeMap<Text, Text> arg) {
            if (arg == null) {
                this.theMetadata = new TreeMap<Text, Text>();
            }
            else {
                this.theMetadata = arg;
            }
        }
        
        public Text get(final Text name) {
            return this.theMetadata.get(name);
        }
        
        public void set(final Text name, final Text value) {
            this.theMetadata.put(name, value);
        }
        
        public TreeMap<Text, Text> getMetadata() {
            return new TreeMap<Text, Text>(this.theMetadata);
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            out.writeInt(this.theMetadata.size());
            for (final Map.Entry<Text, Text> en : this.theMetadata.entrySet()) {
                en.getKey().write(out);
                en.getValue().write(out);
            }
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            final int sz = in.readInt();
            if (sz < 0) {
                throw new IOException("Invalid size: " + sz + " for file metadata object");
            }
            this.theMetadata = new TreeMap<Text, Text>();
            for (int i = 0; i < sz; ++i) {
                final Text key = new Text();
                final Text val = new Text();
                key.readFields(in);
                val.readFields(in);
                this.theMetadata.put(key, val);
            }
        }
        
        @Override
        public boolean equals(final Object other) {
            return other != null && other.getClass() == this.getClass() && this.equals((Metadata)other);
        }
        
        public boolean equals(final Metadata other) {
            if (other == null) {
                return false;
            }
            if (this.theMetadata.size() != other.theMetadata.size()) {
                return false;
            }
            final Iterator<Map.Entry<Text, Text>> iter1 = this.theMetadata.entrySet().iterator();
            final Iterator<Map.Entry<Text, Text>> iter2 = other.theMetadata.entrySet().iterator();
            while (iter1.hasNext() && iter2.hasNext()) {
                final Map.Entry<Text, Text> en1 = iter1.next();
                final Map.Entry<Text, Text> en2 = iter2.next();
                if (!en1.getKey().equals(en2.getKey())) {
                    return false;
                }
                if (!en1.getValue().equals(en2.getValue())) {
                    return false;
                }
            }
            return !iter1.hasNext() && !iter2.hasNext();
        }
        
        @Override
        public int hashCode() {
            assert false : "hashCode not designed";
            return 42;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("size: ").append(this.theMetadata.size()).append("\n");
            for (final Map.Entry<Text, Text> en : this.theMetadata.entrySet()) {
                sb.append("\t").append(en.getKey().toString()).append("\t").append(en.getValue().toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }
    
    public static class Writer implements Closeable, Syncable
    {
        private Configuration conf;
        FSDataOutputStream out;
        boolean ownOutputStream;
        DataOutputBuffer buffer;
        Class keyClass;
        Class valClass;
        private final CompressionType compress;
        CompressionCodec codec;
        CompressionOutputStream deflateFilter;
        DataOutputStream deflateOut;
        Metadata metadata;
        Compressor compressor;
        private boolean appendMode;
        protected Serializer keySerializer;
        protected Serializer uncompressedValSerializer;
        protected Serializer compressedValSerializer;
        long lastSyncPos;
        byte[] sync;
        @VisibleForTesting
        int syncInterval;
        
        public static Option file(final Path value) {
            return new FileOption(value);
        }
        
        @Deprecated
        private static Option filesystem(final FileSystem fs) {
            return new FileSystemOption(fs);
        }
        
        public static Option bufferSize(final int value) {
            return new BufferSizeOption(value);
        }
        
        public static Option stream(final FSDataOutputStream value) {
            return new StreamOption(value);
        }
        
        public static Option replication(final short value) {
            return new ReplicationOption(value);
        }
        
        public static Option appendIfExists(final boolean value) {
            return new AppendIfExistsOption(value);
        }
        
        public static Option blockSize(final long value) {
            return new BlockSizeOption(value);
        }
        
        public static Option progressable(final Progressable value) {
            return new ProgressableOption(value);
        }
        
        public static Option keyClass(final Class<?> value) {
            return new KeyClassOption(value);
        }
        
        public static Option valueClass(final Class<?> value) {
            return new ValueClassOption(value);
        }
        
        public static Option metadata(final Metadata value) {
            return new MetadataOption(value);
        }
        
        public static Option compression(final CompressionType value) {
            return new CompressionOption(value);
        }
        
        public static Option compression(final CompressionType value, final CompressionCodec codec) {
            return new CompressionOption(value, codec);
        }
        
        public static Option syncInterval(final int value) {
            return new SyncIntervalOption(value);
        }
        
        Writer(final Configuration conf, final Option... opts) throws IOException {
            this.ownOutputStream = true;
            this.buffer = new DataOutputBuffer();
            this.codec = null;
            this.deflateFilter = null;
            this.deflateOut = null;
            this.metadata = null;
            this.compressor = null;
            this.appendMode = false;
            try {
                final MessageDigest digester = MessageDigest.getInstance("MD5");
                final long time = Time.now();
                digester.update((new UID() + "@" + time).getBytes(StandardCharsets.UTF_8));
                this.sync = digester.digest();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            final BlockSizeOption blockSizeOption = Options.getOption(BlockSizeOption.class, opts);
            final BufferSizeOption bufferSizeOption = Options.getOption(BufferSizeOption.class, opts);
            final ReplicationOption replicationOption = Options.getOption(ReplicationOption.class, opts);
            final ProgressableOption progressOption = Options.getOption(ProgressableOption.class, opts);
            final FileOption fileOption = Options.getOption(FileOption.class, opts);
            final AppendIfExistsOption appendIfExistsOption = Options.getOption(AppendIfExistsOption.class, opts);
            final FileSystemOption fsOption = Options.getOption(FileSystemOption.class, opts);
            final StreamOption streamOption = Options.getOption(StreamOption.class, opts);
            final KeyClassOption keyClassOption = Options.getOption(KeyClassOption.class, opts);
            final ValueClassOption valueClassOption = Options.getOption(ValueClassOption.class, opts);
            MetadataOption metadataOption = Options.getOption(MetadataOption.class, opts);
            final CompressionOption compressionTypeOption = Options.getOption(CompressionOption.class, opts);
            final SyncIntervalOption syncIntervalOption = Options.getOption(SyncIntervalOption.class, opts);
            if (fileOption == null == (streamOption == null)) {
                throw new IllegalArgumentException("file or stream must be specified");
            }
            if (fileOption == null && (blockSizeOption != null || bufferSizeOption != null || replicationOption != null || progressOption != null)) {
                throw new IllegalArgumentException("file modifier options not compatible with stream");
            }
            final boolean ownStream = fileOption != null;
            FSDataOutputStream out;
            if (ownStream) {
                final Path p = fileOption.getValue();
                FileSystem fs;
                if (fsOption != null) {
                    fs = fsOption.getValue();
                }
                else {
                    fs = p.getFileSystem(conf);
                }
                final int bufferSize = (bufferSizeOption == null) ? getBufferSize(conf) : bufferSizeOption.getValue();
                final short replication = (replicationOption == null) ? fs.getDefaultReplication(p) : ((short)replicationOption.getValue());
                final long blockSize = (blockSizeOption == null) ? fs.getDefaultBlockSize(p) : blockSizeOption.getValue();
                final Progressable progress = (progressOption == null) ? null : progressOption.getValue();
                if (appendIfExistsOption != null && appendIfExistsOption.getValue() && fs.exists(p)) {
                    final Reader reader = new Reader(conf, new Reader.Option[] { Reader.file(p), new Reader.OnlyHeaderOption() });
                    try {
                        if (keyClassOption.getValue() != reader.getKeyClass() || valueClassOption.getValue() != reader.getValueClass()) {
                            throw new IllegalArgumentException("Key/value class provided does not match the file");
                        }
                        if (reader.getVersion() != SequenceFile.VERSION[3]) {
                            throw new VersionMismatchException(SequenceFile.VERSION[3], reader.getVersion());
                        }
                        if (metadataOption != null) {
                            SequenceFile.LOG.info("MetaData Option is ignored during append");
                        }
                        metadataOption = (MetadataOption)metadata(reader.getMetadata());
                        final CompressionOption readerCompressionOption = new CompressionOption(reader.getCompressionType(), reader.getCompressionCodec());
                        if (readerCompressionOption.value != compressionTypeOption.value || (readerCompressionOption.value != CompressionType.NONE && readerCompressionOption.codec.getClass() != compressionTypeOption.codec.getClass())) {
                            throw new IllegalArgumentException("Compression option provided does not match the file");
                        }
                        this.sync = reader.getSync();
                    }
                    finally {
                        reader.close();
                    }
                    out = fs.append(p, bufferSize, progress);
                    this.appendMode = true;
                }
                else {
                    out = fs.create(p, true, bufferSize, replication, blockSize, progress);
                }
            }
            else {
                out = streamOption.getValue();
            }
            final Class<?> keyClass = (keyClassOption == null) ? Object.class : keyClassOption.getValue();
            final Class<?> valueClass = (valueClassOption == null) ? Object.class : valueClassOption.getValue();
            final Metadata metadata = (metadataOption == null) ? new Metadata() : metadataOption.getValue();
            this.compress = compressionTypeOption.getValue();
            final CompressionCodec codec = compressionTypeOption.getCodec();
            if (codec != null && codec instanceof GzipCodec && !NativeCodeLoader.isNativeCodeLoaded() && !ZlibFactory.isNativeZlibLoaded(conf)) {
                throw new IllegalArgumentException("SequenceFile doesn't work with GzipCodec without native-hadoop code!");
            }
            this.init(conf, out, ownStream, keyClass, valueClass, codec, metadata, this.syncInterval = ((syncIntervalOption == null) ? 102400 : syncIntervalOption.getValue()));
        }
        
        @Deprecated
        public Writer(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass) throws IOException {
            this.ownOutputStream = true;
            this.buffer = new DataOutputBuffer();
            this.codec = null;
            this.deflateFilter = null;
            this.deflateOut = null;
            this.metadata = null;
            this.compressor = null;
            this.appendMode = false;
            try {
                final MessageDigest digester = MessageDigest.getInstance("MD5");
                final long time = Time.now();
                digester.update((new UID() + "@" + time).getBytes(StandardCharsets.UTF_8));
                this.sync = digester.digest();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.compress = CompressionType.NONE;
            this.init(conf, fs.create(name), true, keyClass, valClass, null, new Metadata(), 102400);
        }
        
        @Deprecated
        public Writer(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final Progressable progress, final Metadata metadata) throws IOException {
            this.ownOutputStream = true;
            this.buffer = new DataOutputBuffer();
            this.codec = null;
            this.deflateFilter = null;
            this.deflateOut = null;
            this.metadata = null;
            this.compressor = null;
            this.appendMode = false;
            try {
                final MessageDigest digester = MessageDigest.getInstance("MD5");
                final long time = Time.now();
                digester.update((new UID() + "@" + time).getBytes(StandardCharsets.UTF_8));
                this.sync = digester.digest();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.compress = CompressionType.NONE;
            this.init(conf, fs.create(name, progress), true, keyClass, valClass, null, metadata, 102400);
        }
        
        @Deprecated
        public Writer(final FileSystem fs, final Configuration conf, final Path name, final Class keyClass, final Class valClass, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Metadata metadata) throws IOException {
            this.ownOutputStream = true;
            this.buffer = new DataOutputBuffer();
            this.codec = null;
            this.deflateFilter = null;
            this.deflateOut = null;
            this.metadata = null;
            this.compressor = null;
            this.appendMode = false;
            try {
                final MessageDigest digester = MessageDigest.getInstance("MD5");
                final long time = Time.now();
                digester.update((new UID() + "@" + time).getBytes(StandardCharsets.UTF_8));
                this.sync = digester.digest();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.compress = CompressionType.NONE;
            this.init(conf, fs.create(name, true, bufferSize, replication, blockSize, progress), true, keyClass, valClass, null, metadata, 102400);
        }
        
        boolean isCompressed() {
            return this.compress != CompressionType.NONE;
        }
        
        boolean isBlockCompressed() {
            return this.compress == CompressionType.BLOCK;
        }
        
        Writer ownStream() {
            this.ownOutputStream = true;
            return this;
        }
        
        private void writeFileHeader() throws IOException {
            this.out.write(SequenceFile.VERSION);
            Text.writeString(this.out, this.keyClass.getName());
            Text.writeString(this.out, this.valClass.getName());
            this.out.writeBoolean(this.isCompressed());
            this.out.writeBoolean(this.isBlockCompressed());
            if (this.isCompressed()) {
                Text.writeString(this.out, this.codec.getClass().getName());
            }
            this.metadata.write(this.out);
            this.out.write(this.sync);
            this.out.flush();
        }
        
        void init(final Configuration config, final FSDataOutputStream outStream, final boolean ownStream, final Class key, final Class val, final CompressionCodec compCodec, final Metadata meta, final int syncIntervalVal) throws IOException {
            this.conf = config;
            this.out = outStream;
            this.ownOutputStream = ownStream;
            this.keyClass = key;
            this.valClass = val;
            this.codec = compCodec;
            this.metadata = meta;
            this.syncInterval = syncIntervalVal;
            final SerializationFactory serializationFactory = new SerializationFactory(config);
            this.keySerializer = serializationFactory.getSerializer((Class<Object>)this.keyClass);
            if (this.keySerializer == null) {
                throw new IOException("Could not find a serializer for the Key class: '" + this.keyClass.getCanonicalName() + "'. Please ensure that the configuration '" + "io.serializations" + "' is properly configured, if you're usingcustom serialization.");
            }
            this.keySerializer.open(this.buffer);
            this.uncompressedValSerializer = serializationFactory.getSerializer((Class<Object>)this.valClass);
            if (this.uncompressedValSerializer == null) {
                throw new IOException("Could not find a serializer for the Value class: '" + this.valClass.getCanonicalName() + "'. Please ensure that the configuration '" + "io.serializations" + "' is properly configured, if you're usingcustom serialization.");
            }
            this.uncompressedValSerializer.open(this.buffer);
            if (this.codec != null) {
                ReflectionUtils.setConf(this.codec, this.conf);
                this.compressor = CodecPool.getCompressor(this.codec);
                this.deflateFilter = this.codec.createOutputStream(this.buffer, this.compressor);
                this.deflateOut = new DataOutputStream(new BufferedOutputStream(this.deflateFilter));
                this.compressedValSerializer = serializationFactory.getSerializer((Class<Object>)this.valClass);
                if (this.compressedValSerializer == null) {
                    throw new IOException("Could not find a serializer for the Value class: '" + this.valClass.getCanonicalName() + "'. Please ensure that the configuration '" + "io.serializations" + "' is properly configured, if you're usingcustom serialization.");
                }
                this.compressedValSerializer.open(this.deflateOut);
            }
            if (this.appendMode) {
                this.sync();
            }
            else {
                this.writeFileHeader();
            }
        }
        
        public Class getKeyClass() {
            return this.keyClass;
        }
        
        public Class getValueClass() {
            return this.valClass;
        }
        
        public CompressionCodec getCompressionCodec() {
            return this.codec;
        }
        
        public void sync() throws IOException {
            if (this.sync != null && this.lastSyncPos != this.out.getPos()) {
                this.out.writeInt(-1);
                this.out.write(this.sync);
                this.lastSyncPos = this.out.getPos();
            }
        }
        
        @Deprecated
        public void syncFs() throws IOException {
            if (this.out != null) {
                this.out.hflush();
            }
        }
        
        @Override
        public void hsync() throws IOException {
            if (this.out != null) {
                this.out.hsync();
            }
        }
        
        @Override
        public void hflush() throws IOException {
            if (this.out != null) {
                this.out.hflush();
            }
        }
        
        Configuration getConf() {
            return this.conf;
        }
        
        @Override
        public synchronized void close() throws IOException {
            this.keySerializer.close();
            this.uncompressedValSerializer.close();
            if (this.compressedValSerializer != null) {
                this.compressedValSerializer.close();
            }
            CodecPool.returnCompressor(this.compressor);
            this.compressor = null;
            if (this.out != null) {
                if (this.ownOutputStream) {
                    this.out.close();
                }
                else {
                    this.out.flush();
                }
                this.out = null;
            }
        }
        
        synchronized void checkAndWriteSync() throws IOException {
            if (this.sync != null && this.out.getPos() >= this.lastSyncPos + this.syncInterval) {
                this.sync();
            }
        }
        
        public void append(final Writable key, final Writable val) throws IOException {
            this.append(key, (Object)val);
        }
        
        public synchronized void append(final Object key, final Object val) throws IOException {
            if (key.getClass() != this.keyClass) {
                throw new IOException("wrong key class: " + key.getClass().getName() + " is not " + this.keyClass);
            }
            if (val.getClass() != this.valClass) {
                throw new IOException("wrong value class: " + val.getClass().getName() + " is not " + this.valClass);
            }
            this.buffer.reset();
            this.keySerializer.serialize(key);
            final int keyLength = this.buffer.getLength();
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed: " + key);
            }
            if (this.compress == CompressionType.RECORD) {
                this.deflateFilter.resetState();
                this.compressedValSerializer.serialize(val);
                this.deflateOut.flush();
                this.deflateFilter.finish();
            }
            else {
                this.uncompressedValSerializer.serialize(val);
            }
            this.checkAndWriteSync();
            this.out.writeInt(this.buffer.getLength());
            this.out.writeInt(keyLength);
            this.out.write(this.buffer.getData(), 0, this.buffer.getLength());
        }
        
        public synchronized void appendRaw(final byte[] keyData, final int keyOffset, final int keyLength, final ValueBytes val) throws IOException {
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed: " + keyLength);
            }
            final int valLength = val.getSize();
            this.checkAndWriteSync();
            this.out.writeInt(keyLength + valLength);
            this.out.writeInt(keyLength);
            this.out.write(keyData, keyOffset, keyLength);
            val.writeUncompressedBytes(this.out);
        }
        
        public synchronized long getLength() throws IOException {
            return this.out.getPos();
        }
        
        static class FileOption extends Options.PathOption implements Option
        {
            FileOption(final Path path) {
                super(path);
            }
        }
        
        @Deprecated
        private static class FileSystemOption implements Option
        {
            private final FileSystem value;
            
            protected FileSystemOption(final FileSystem value) {
                this.value = value;
            }
            
            public FileSystem getValue() {
                return this.value;
            }
        }
        
        static class StreamOption extends Options.FSDataOutputStreamOption implements Option
        {
            StreamOption(final FSDataOutputStream stream) {
                super(stream);
            }
        }
        
        static class BufferSizeOption extends Options.IntegerOption implements Option
        {
            BufferSizeOption(final int value) {
                super(value);
            }
        }
        
        static class BlockSizeOption extends Options.LongOption implements Option
        {
            BlockSizeOption(final long value) {
                super(value);
            }
        }
        
        static class ReplicationOption extends Options.IntegerOption implements Option
        {
            ReplicationOption(final int value) {
                super(value);
            }
        }
        
        static class AppendIfExistsOption extends Options.BooleanOption implements Option
        {
            AppendIfExistsOption(final boolean value) {
                super(value);
            }
        }
        
        static class KeyClassOption extends Options.ClassOption implements Option
        {
            KeyClassOption(final Class<?> value) {
                super(value);
            }
        }
        
        static class ValueClassOption extends Options.ClassOption implements Option
        {
            ValueClassOption(final Class<?> value) {
                super(value);
            }
        }
        
        static class MetadataOption implements Option
        {
            private final Metadata value;
            
            MetadataOption(final Metadata value) {
                this.value = value;
            }
            
            Metadata getValue() {
                return this.value;
            }
        }
        
        static class ProgressableOption extends Options.ProgressableOption implements Option
        {
            ProgressableOption(final Progressable value) {
                super(value);
            }
        }
        
        private static class CompressionOption implements Option
        {
            private final CompressionType value;
            private final CompressionCodec codec;
            
            CompressionOption(final CompressionType value) {
                this(value, null);
            }
            
            CompressionOption(final CompressionType value, final CompressionCodec codec) {
                this.value = value;
                this.codec = ((CompressionType.NONE != value && null == codec) ? new DefaultCodec() : codec);
            }
            
            CompressionType getValue() {
                return this.value;
            }
            
            CompressionCodec getCodec() {
                return this.codec;
            }
        }
        
        private static class SyncIntervalOption extends Options.IntegerOption implements Option
        {
            SyncIntervalOption(final int val) {
                super((val < 0) ? 102400 : val);
            }
        }
        
        public interface Option
        {
        }
    }
    
    static class RecordCompressWriter extends Writer
    {
        RecordCompressWriter(final Configuration conf, final Option... options) throws IOException {
            super(conf, options);
        }
        
        @Override
        public synchronized void append(final Object key, final Object val) throws IOException {
            if (key.getClass() != this.keyClass) {
                throw new IOException("wrong key class: " + key.getClass().getName() + " is not " + this.keyClass);
            }
            if (val.getClass() != this.valClass) {
                throw new IOException("wrong value class: " + val.getClass().getName() + " is not " + this.valClass);
            }
            this.buffer.reset();
            this.keySerializer.serialize(key);
            final int keyLength = this.buffer.getLength();
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed: " + key);
            }
            this.deflateFilter.resetState();
            this.compressedValSerializer.serialize(val);
            this.deflateOut.flush();
            this.deflateFilter.finish();
            this.checkAndWriteSync();
            this.out.writeInt(this.buffer.getLength());
            this.out.writeInt(keyLength);
            this.out.write(this.buffer.getData(), 0, this.buffer.getLength());
        }
        
        @Override
        public synchronized void appendRaw(final byte[] keyData, final int keyOffset, final int keyLength, final ValueBytes val) throws IOException {
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed: " + keyLength);
            }
            final int valLength = val.getSize();
            this.checkAndWriteSync();
            this.out.writeInt(keyLength + valLength);
            this.out.writeInt(keyLength);
            this.out.write(keyData, keyOffset, keyLength);
            val.writeCompressedBytes(this.out);
        }
    }
    
    static class BlockCompressWriter extends Writer
    {
        private int noBufferedRecords;
        private DataOutputBuffer keyLenBuffer;
        private DataOutputBuffer keyBuffer;
        private DataOutputBuffer valLenBuffer;
        private DataOutputBuffer valBuffer;
        private final int compressionBlockSize;
        
        BlockCompressWriter(final Configuration conf, final Option... options) throws IOException {
            super(conf, options);
            this.noBufferedRecords = 0;
            this.keyLenBuffer = new DataOutputBuffer();
            this.keyBuffer = new DataOutputBuffer();
            this.valLenBuffer = new DataOutputBuffer();
            this.valBuffer = new DataOutputBuffer();
            this.compressionBlockSize = conf.getInt("io.seqfile.compress.blocksize", 1000000);
            this.keySerializer.close();
            this.keySerializer.open(this.keyBuffer);
            this.uncompressedValSerializer.close();
            this.uncompressedValSerializer.open(this.valBuffer);
        }
        
        private synchronized void writeBuffer(final DataOutputBuffer uncompressedDataBuffer) throws IOException {
            this.deflateFilter.resetState();
            this.buffer.reset();
            this.deflateOut.write(uncompressedDataBuffer.getData(), 0, uncompressedDataBuffer.getLength());
            this.deflateOut.flush();
            this.deflateFilter.finish();
            WritableUtils.writeVInt(this.out, this.buffer.getLength());
            this.out.write(this.buffer.getData(), 0, this.buffer.getLength());
        }
        
        @Override
        public synchronized void sync() throws IOException {
            if (this.noBufferedRecords > 0) {
                super.sync();
                WritableUtils.writeVInt(this.out, this.noBufferedRecords);
                this.writeBuffer(this.keyLenBuffer);
                this.writeBuffer(this.keyBuffer);
                this.writeBuffer(this.valLenBuffer);
                this.writeBuffer(this.valBuffer);
                this.out.flush();
                this.keyLenBuffer.reset();
                this.keyBuffer.reset();
                this.valLenBuffer.reset();
                this.valBuffer.reset();
                this.noBufferedRecords = 0;
            }
        }
        
        @Override
        public synchronized void close() throws IOException {
            if (this.out != null) {
                this.sync();
            }
            super.close();
        }
        
        @Override
        public synchronized void append(final Object key, final Object val) throws IOException {
            if (key.getClass() != this.keyClass) {
                throw new IOException("wrong key class: " + key + " is not " + this.keyClass);
            }
            if (val.getClass() != this.valClass) {
                throw new IOException("wrong value class: " + val + " is not " + this.valClass);
            }
            final int oldKeyLength = this.keyBuffer.getLength();
            this.keySerializer.serialize(key);
            final int keyLength = this.keyBuffer.getLength() - oldKeyLength;
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed: " + key);
            }
            WritableUtils.writeVInt(this.keyLenBuffer, keyLength);
            final int oldValLength = this.valBuffer.getLength();
            this.uncompressedValSerializer.serialize(val);
            final int valLength = this.valBuffer.getLength() - oldValLength;
            WritableUtils.writeVInt(this.valLenBuffer, valLength);
            ++this.noBufferedRecords;
            final int currentBlockSize = this.keyBuffer.getLength() + this.valBuffer.getLength();
            if (currentBlockSize >= this.compressionBlockSize) {
                this.sync();
            }
        }
        
        @Override
        public synchronized void appendRaw(final byte[] keyData, final int keyOffset, final int keyLength, final ValueBytes val) throws IOException {
            if (keyLength < 0) {
                throw new IOException("negative length keys not allowed");
            }
            final int valLength = val.getSize();
            WritableUtils.writeVInt(this.keyLenBuffer, keyLength);
            this.keyBuffer.write(keyData, keyOffset, keyLength);
            WritableUtils.writeVInt(this.valLenBuffer, valLength);
            val.writeUncompressedBytes(this.valBuffer);
            ++this.noBufferedRecords;
            final int currentBlockSize = this.keyBuffer.getLength() + this.valBuffer.getLength();
            if (currentBlockSize >= this.compressionBlockSize) {
                this.sync();
            }
        }
    }
    
    public static class Reader implements Closeable
    {
        private String filename;
        private FSDataInputStream in;
        private DataOutputBuffer outBuf;
        private byte version;
        private String keyClassName;
        private String valClassName;
        private Class keyClass;
        private Class valClass;
        private CompressionCodec codec;
        private Metadata metadata;
        private byte[] sync;
        private byte[] syncCheck;
        private boolean syncSeen;
        private long headerEnd;
        private long end;
        private int keyLength;
        private int recordLength;
        private boolean decompress;
        private boolean blockCompressed;
        private Configuration conf;
        private int noBufferedRecords;
        private boolean lazyDecompress;
        private boolean valuesDecompressed;
        private int noBufferedKeys;
        private int noBufferedValues;
        private DataInputBuffer keyLenBuffer;
        private CompressionInputStream keyLenInFilter;
        private DataInputStream keyLenIn;
        private Decompressor keyLenDecompressor;
        private DataInputBuffer keyBuffer;
        private CompressionInputStream keyInFilter;
        private DataInputStream keyIn;
        private Decompressor keyDecompressor;
        private DataInputBuffer valLenBuffer;
        private CompressionInputStream valLenInFilter;
        private DataInputStream valLenIn;
        private Decompressor valLenDecompressor;
        private DataInputBuffer valBuffer;
        private CompressionInputStream valInFilter;
        private DataInputStream valIn;
        private Decompressor valDecompressor;
        private Deserializer keyDeserializer;
        private Deserializer valDeserializer;
        
        public static Option file(final Path value) {
            return new FileOption(value);
        }
        
        public static Option stream(final FSDataInputStream value) {
            return new InputStreamOption(value);
        }
        
        public static Option start(final long value) {
            return new StartOption(value);
        }
        
        public static Option length(final long value) {
            return new LengthOption(value);
        }
        
        public static Option bufferSize(final int value) {
            return new BufferSizeOption(value);
        }
        
        public Reader(final Configuration conf, final Option... opts) throws IOException {
            this.outBuf = new DataOutputBuffer();
            this.codec = null;
            this.metadata = null;
            this.sync = new byte[16];
            this.syncCheck = new byte[16];
            this.noBufferedRecords = 0;
            this.lazyDecompress = true;
            this.valuesDecompressed = true;
            this.noBufferedKeys = 0;
            this.noBufferedValues = 0;
            this.keyLenBuffer = null;
            this.keyLenInFilter = null;
            this.keyLenIn = null;
            this.keyLenDecompressor = null;
            this.keyBuffer = null;
            this.keyInFilter = null;
            this.keyIn = null;
            this.keyDecompressor = null;
            this.valLenBuffer = null;
            this.valLenInFilter = null;
            this.valLenIn = null;
            this.valLenDecompressor = null;
            this.valBuffer = null;
            this.valInFilter = null;
            this.valIn = null;
            this.valDecompressor = null;
            final FileOption fileOpt = Options.getOption(FileOption.class, opts);
            final InputStreamOption streamOpt = Options.getOption(InputStreamOption.class, opts);
            final StartOption startOpt = Options.getOption(StartOption.class, opts);
            final LengthOption lenOpt = Options.getOption(LengthOption.class, opts);
            final BufferSizeOption bufOpt = Options.getOption(BufferSizeOption.class, opts);
            final OnlyHeaderOption headerOnly = Options.getOption(OnlyHeaderOption.class, opts);
            if (fileOpt == null == (streamOpt == null)) {
                throw new IllegalArgumentException("File or stream option must be specified");
            }
            if (fileOpt == null && bufOpt != null) {
                throw new IllegalArgumentException("buffer size can only be set when a file is specified.");
            }
            Path filename = null;
            long len;
            FSDataInputStream file;
            if (fileOpt != null) {
                filename = fileOpt.getValue();
                final FileSystem fs = filename.getFileSystem(conf);
                final int bufSize = (bufOpt == null) ? getBufferSize(conf) : bufOpt.getValue();
                len = ((null == lenOpt) ? fs.getFileStatus(filename).getLen() : lenOpt.getValue());
                file = this.openFile(fs, filename, bufSize, len);
            }
            else {
                len = ((null == lenOpt) ? Long.MAX_VALUE : lenOpt.getValue());
                file = streamOpt.getValue();
            }
            final long start = (startOpt == null) ? 0L : startOpt.getValue();
            this.initialize(filename, file, start, len, conf, headerOnly != null);
        }
        
        @Deprecated
        public Reader(final FileSystem fs, final Path file, final Configuration conf) throws IOException {
            this(conf, new Option[] { file(fs.makeQualified(file)) });
        }
        
        @Deprecated
        public Reader(final FSDataInputStream in, final int buffersize, final long start, final long length, final Configuration conf) throws IOException {
            this(conf, new Option[] { stream(in), start(start), length(length) });
        }
        
        private void initialize(final Path filename, final FSDataInputStream in, final long start, final long length, final Configuration conf, final boolean tempReader) throws IOException {
            if (in == null) {
                throw new IllegalArgumentException("in == null");
            }
            this.filename = ((filename == null) ? "<unknown>" : filename.toString());
            this.in = in;
            this.conf = conf;
            boolean succeeded = false;
            try {
                this.seek(start);
                this.end = this.in.getPos() + length;
                if (this.end < length) {
                    this.end = Long.MAX_VALUE;
                }
                this.init(tempReader);
                succeeded = true;
            }
            finally {
                if (!succeeded) {
                    IOUtils.cleanupWithLogger(SequenceFile.LOG, this.in);
                }
            }
        }
        
        protected FSDataInputStream openFile(final FileSystem fs, final Path file, final int bufferSize, final long length) throws IOException {
            return fs.open(file, bufferSize);
        }
        
        private void init(final boolean tempReader) throws IOException {
            final byte[] versionBlock = new byte[SequenceFile.VERSION.length];
            final String exceptionMsg = this + " not a SequenceFile";
            try {
                this.in.readFully(versionBlock);
            }
            catch (EOFException e) {
                throw new EOFException(exceptionMsg);
            }
            if (versionBlock[0] != SequenceFile.VERSION[0] || versionBlock[1] != SequenceFile.VERSION[1] || versionBlock[2] != SequenceFile.VERSION[2]) {
                throw new IOException(this + " not a SequenceFile");
            }
            this.version = versionBlock[3];
            if (this.version > SequenceFile.VERSION[3]) {
                throw new VersionMismatchException(SequenceFile.VERSION[3], this.version);
            }
            if (this.version < 4) {
                final UTF8 className = new UTF8();
                className.readFields(this.in);
                this.keyClassName = className.toStringChecked();
                className.readFields(this.in);
                this.valClassName = className.toStringChecked();
            }
            else {
                this.keyClassName = Text.readString(this.in);
                this.valClassName = Text.readString(this.in);
            }
            if (this.version > 2) {
                this.decompress = this.in.readBoolean();
            }
            else {
                this.decompress = false;
            }
            if (this.version >= 4) {
                this.blockCompressed = this.in.readBoolean();
            }
            else {
                this.blockCompressed = false;
            }
            if (this.decompress) {
                if (this.version >= 5) {
                    final String codecClassname = Text.readString(this.in);
                    try {
                        final Class<? extends CompressionCodec> codecClass = this.conf.getClassByName(codecClassname).asSubclass(CompressionCodec.class);
                        this.codec = ReflectionUtils.newInstance(codecClass, this.conf);
                    }
                    catch (ClassNotFoundException cnfe) {
                        throw new IllegalArgumentException("Unknown codec: " + codecClassname, cnfe);
                    }
                }
                else {
                    this.codec = new DefaultCodec();
                    ((Configurable)this.codec).setConf(this.conf);
                }
            }
            this.metadata = new Metadata();
            if (this.version >= 6) {
                this.metadata.readFields(this.in);
            }
            if (this.version > 1) {
                this.in.readFully(this.sync);
                this.headerEnd = this.in.getPos();
            }
            if (!tempReader) {
                this.valBuffer = new DataInputBuffer();
                if (this.decompress) {
                    this.valDecompressor = CodecPool.getDecompressor(this.codec);
                    this.valInFilter = this.codec.createInputStream(this.valBuffer, this.valDecompressor);
                    this.valIn = new DataInputStream(this.valInFilter);
                }
                else {
                    this.valIn = this.valBuffer;
                }
                if (this.blockCompressed) {
                    this.keyLenBuffer = new DataInputBuffer();
                    this.keyBuffer = new DataInputBuffer();
                    this.valLenBuffer = new DataInputBuffer();
                    this.keyLenDecompressor = CodecPool.getDecompressor(this.codec);
                    this.keyLenInFilter = this.codec.createInputStream(this.keyLenBuffer, this.keyLenDecompressor);
                    this.keyLenIn = new DataInputStream(this.keyLenInFilter);
                    this.keyDecompressor = CodecPool.getDecompressor(this.codec);
                    this.keyInFilter = this.codec.createInputStream(this.keyBuffer, this.keyDecompressor);
                    this.keyIn = new DataInputStream(this.keyInFilter);
                    this.valLenDecompressor = CodecPool.getDecompressor(this.codec);
                    this.valLenInFilter = this.codec.createInputStream(this.valLenBuffer, this.valLenDecompressor);
                    this.valLenIn = new DataInputStream(this.valLenInFilter);
                }
                final SerializationFactory serializationFactory = new SerializationFactory(this.conf);
                this.keyDeserializer = this.getDeserializer(serializationFactory, this.getKeyClass());
                if (this.keyDeserializer == null) {
                    throw new IOException("Could not find a deserializer for the Key class: '" + this.getKeyClass().getCanonicalName() + "'. Please ensure that the configuration '" + "io.serializations" + "' is properly configured, if you're using custom serialization.");
                }
                if (!this.blockCompressed) {
                    this.keyDeserializer.open(this.valBuffer);
                }
                else {
                    this.keyDeserializer.open(this.keyIn);
                }
                this.valDeserializer = this.getDeserializer(serializationFactory, this.getValueClass());
                if (this.valDeserializer == null) {
                    throw new IOException("Could not find a deserializer for the Value class: '" + this.getValueClass().getCanonicalName() + "'. Please ensure that the configuration '" + "io.serializations" + "' is properly configured, if you're using custom serialization.");
                }
                this.valDeserializer.open(this.valIn);
            }
        }
        
        private Deserializer getDeserializer(final SerializationFactory sf, final Class c) {
            return sf.getDeserializer((Class<Object>)c);
        }
        
        @Override
        public synchronized void close() throws IOException {
            CodecPool.returnDecompressor(this.keyLenDecompressor);
            CodecPool.returnDecompressor(this.keyDecompressor);
            CodecPool.returnDecompressor(this.valLenDecompressor);
            CodecPool.returnDecompressor(this.valDecompressor);
            final Decompressor decompressor = null;
            this.keyDecompressor = decompressor;
            this.keyLenDecompressor = decompressor;
            final Decompressor decompressor2 = null;
            this.valDecompressor = decompressor2;
            this.valLenDecompressor = decompressor2;
            if (this.keyDeserializer != null) {
                this.keyDeserializer.close();
            }
            if (this.valDeserializer != null) {
                this.valDeserializer.close();
            }
            this.in.close();
        }
        
        public String getKeyClassName() {
            return this.keyClassName;
        }
        
        public synchronized Class<?> getKeyClass() {
            if (null == this.keyClass) {
                try {
                    this.keyClass = WritableName.getClass(this.getKeyClassName(), this.conf);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return (Class<?>)this.keyClass;
        }
        
        public String getValueClassName() {
            return this.valClassName;
        }
        
        public synchronized Class<?> getValueClass() {
            if (null == this.valClass) {
                try {
                    this.valClass = WritableName.getClass(this.getValueClassName(), this.conf);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return (Class<?>)this.valClass;
        }
        
        public boolean isCompressed() {
            return this.decompress;
        }
        
        public boolean isBlockCompressed() {
            return this.blockCompressed;
        }
        
        public CompressionCodec getCompressionCodec() {
            return this.codec;
        }
        
        private byte[] getSync() {
            return this.sync;
        }
        
        private byte getVersion() {
            return this.version;
        }
        
        public CompressionType getCompressionType() {
            if (this.decompress) {
                return this.blockCompressed ? CompressionType.BLOCK : CompressionType.RECORD;
            }
            return CompressionType.NONE;
        }
        
        public Metadata getMetadata() {
            return this.metadata;
        }
        
        Configuration getConf() {
            return this.conf;
        }
        
        private synchronized void readBuffer(final DataInputBuffer buffer, final CompressionInputStream filter) throws IOException {
            final DataOutputBuffer dataBuffer = new DataOutputBuffer();
            try {
                final int dataBufferLength = WritableUtils.readVInt(this.in);
                dataBuffer.write(this.in, dataBufferLength);
                buffer.reset(dataBuffer.getData(), 0, dataBuffer.getLength());
            }
            finally {
                dataBuffer.close();
            }
            filter.resetState();
        }
        
        private synchronized void readBlock() throws IOException {
            if (this.lazyDecompress && !this.valuesDecompressed) {
                this.in.seek(WritableUtils.readVInt(this.in) + this.in.getPos());
                this.in.seek(WritableUtils.readVInt(this.in) + this.in.getPos());
            }
            this.noBufferedKeys = 0;
            this.noBufferedValues = 0;
            this.noBufferedRecords = 0;
            this.valuesDecompressed = false;
            if (this.sync != null) {
                this.in.readInt();
                this.in.readFully(this.syncCheck);
                if (!Arrays.equals(this.sync, this.syncCheck)) {
                    throw new IOException("File is corrupt!");
                }
            }
            this.syncSeen = true;
            this.noBufferedRecords = WritableUtils.readVInt(this.in);
            this.readBuffer(this.keyLenBuffer, this.keyLenInFilter);
            this.readBuffer(this.keyBuffer, this.keyInFilter);
            this.noBufferedKeys = this.noBufferedRecords;
            if (!this.lazyDecompress) {
                this.readBuffer(this.valLenBuffer, this.valLenInFilter);
                this.readBuffer(this.valBuffer, this.valInFilter);
                this.noBufferedValues = this.noBufferedRecords;
                this.valuesDecompressed = true;
            }
        }
        
        private synchronized void seekToCurrentValue() throws IOException {
            if (!this.blockCompressed) {
                if (this.decompress) {
                    this.valInFilter.resetState();
                }
                this.valBuffer.reset();
            }
            else {
                if (this.lazyDecompress && !this.valuesDecompressed) {
                    this.readBuffer(this.valLenBuffer, this.valLenInFilter);
                    this.readBuffer(this.valBuffer, this.valInFilter);
                    this.noBufferedValues = this.noBufferedRecords;
                    this.valuesDecompressed = true;
                }
                int skipValBytes = 0;
                final int currentKey = this.noBufferedKeys + 1;
                for (int i = this.noBufferedValues; i > currentKey; --i) {
                    skipValBytes += WritableUtils.readVInt(this.valLenIn);
                    --this.noBufferedValues;
                }
                if (skipValBytes > 0 && this.valIn.skipBytes(skipValBytes) != skipValBytes) {
                    throw new IOException("Failed to seek to " + currentKey + "(th) value!");
                }
            }
        }
        
        public synchronized void getCurrentValue(final Writable val) throws IOException {
            if (val instanceof Configurable) {
                ((Configurable)val).setConf(this.conf);
            }
            this.seekToCurrentValue();
            if (!this.blockCompressed) {
                val.readFields(this.valIn);
                if (this.valIn.read() > 0) {
                    SequenceFile.LOG.info("available bytes: " + this.valIn.available());
                    throw new IOException(val + " read " + (this.valBuffer.getPosition() - this.keyLength) + " bytes, should read " + (this.valBuffer.getLength() - this.keyLength));
                }
            }
            else {
                final int valLength = WritableUtils.readVInt(this.valLenIn);
                val.readFields(this.valIn);
                --this.noBufferedValues;
                if (valLength < 0 && SequenceFile.LOG.isDebugEnabled()) {
                    SequenceFile.LOG.debug(val + " is a zero-length value");
                }
            }
        }
        
        public synchronized Object getCurrentValue(Object val) throws IOException {
            if (val instanceof Configurable) {
                ((Configurable)val).setConf(this.conf);
            }
            this.seekToCurrentValue();
            if (!this.blockCompressed) {
                val = this.deserializeValue(val);
                if (this.valIn.read() > 0) {
                    SequenceFile.LOG.info("available bytes: " + this.valIn.available());
                    throw new IOException(val + " read " + (this.valBuffer.getPosition() - this.keyLength) + " bytes, should read " + (this.valBuffer.getLength() - this.keyLength));
                }
            }
            else {
                final int valLength = WritableUtils.readVInt(this.valLenIn);
                val = this.deserializeValue(val);
                --this.noBufferedValues;
                if (valLength < 0 && SequenceFile.LOG.isDebugEnabled()) {
                    SequenceFile.LOG.debug(val + " is a zero-length value");
                }
            }
            return val;
        }
        
        private Object deserializeValue(final Object val) throws IOException {
            return this.valDeserializer.deserialize(val);
        }
        
        public synchronized boolean next(final Writable key) throws IOException {
            if (key.getClass() != this.getKeyClass()) {
                throw new IOException("wrong key class: " + key.getClass().getName() + " is not " + this.keyClass);
            }
            if (!this.blockCompressed) {
                this.outBuf.reset();
                this.keyLength = this.next(this.outBuf);
                if (this.keyLength < 0) {
                    return false;
                }
                this.valBuffer.reset(this.outBuf.getData(), this.outBuf.getLength());
                key.readFields(this.valBuffer);
                this.valBuffer.mark(0);
                if (this.valBuffer.getPosition() != this.keyLength) {
                    throw new IOException(key + " read " + this.valBuffer.getPosition() + " bytes, should read " + this.keyLength);
                }
            }
            else {
                this.syncSeen = false;
                if (this.noBufferedKeys == 0) {
                    try {
                        this.readBlock();
                    }
                    catch (EOFException eof) {
                        return false;
                    }
                }
                final int keyLength = WritableUtils.readVInt(this.keyLenIn);
                if (keyLength < 0) {
                    return false;
                }
                key.readFields(this.keyIn);
                --this.noBufferedKeys;
            }
            return true;
        }
        
        public synchronized boolean next(final Writable key, final Writable val) throws IOException {
            if (val.getClass() != this.getValueClass()) {
                throw new IOException("wrong value class: " + val + " is not " + this.valClass);
            }
            final boolean more = this.next(key);
            if (more) {
                this.getCurrentValue(val);
            }
            return more;
        }
        
        private synchronized int readRecordLength() throws IOException {
            if (this.in.getPos() >= this.end) {
                return -1;
            }
            int length = this.in.readInt();
            if (this.version > 1 && this.sync != null && length == -1) {
                this.in.readFully(this.syncCheck);
                if (!Arrays.equals(this.sync, this.syncCheck)) {
                    throw new IOException("File is corrupt!");
                }
                this.syncSeen = true;
                if (this.in.getPos() >= this.end) {
                    return -1;
                }
                length = this.in.readInt();
            }
            else {
                this.syncSeen = false;
            }
            return length;
        }
        
        @Deprecated
        synchronized int next(final DataOutputBuffer buffer) throws IOException {
            if (this.blockCompressed) {
                throw new IOException("Unsupported call for block-compressed SequenceFiles - use SequenceFile.Reader.next(DataOutputStream, ValueBytes)");
            }
            try {
                final int length = this.readRecordLength();
                if (length == -1) {
                    return -1;
                }
                final int keyLength = this.in.readInt();
                buffer.write(this.in, length);
                return keyLength;
            }
            catch (ChecksumException e) {
                this.handleChecksumException(e);
                return this.next(buffer);
            }
        }
        
        public ValueBytes createValueBytes() {
            ValueBytes val = null;
            if (!this.decompress || this.blockCompressed) {
                val = new UncompressedBytes();
            }
            else {
                val = new CompressedBytes(this.codec);
            }
            return val;
        }
        
        public synchronized int nextRaw(final DataOutputBuffer key, final ValueBytes val) throws IOException {
            if (!this.blockCompressed) {
                final int length = this.readRecordLength();
                if (length == -1) {
                    return -1;
                }
                final int keyLength = this.in.readInt();
                final int valLength = length - keyLength;
                key.write(this.in, keyLength);
                if (this.decompress) {
                    final CompressedBytes value = (CompressedBytes)val;
                    value.reset(this.in, valLength);
                }
                else {
                    final UncompressedBytes value2 = (UncompressedBytes)val;
                    value2.reset(this.in, valLength);
                }
                return length;
            }
            else {
                this.syncSeen = false;
                if (this.noBufferedKeys == 0) {
                    if (this.in.getPos() >= this.end) {
                        return -1;
                    }
                    try {
                        this.readBlock();
                    }
                    catch (EOFException eof) {
                        return -1;
                    }
                }
                final int keyLength2 = WritableUtils.readVInt(this.keyLenIn);
                if (keyLength2 < 0) {
                    throw new IOException("zero length key found!");
                }
                key.write(this.keyIn, keyLength2);
                --this.noBufferedKeys;
                this.seekToCurrentValue();
                final int valLength2 = WritableUtils.readVInt(this.valLenIn);
                final UncompressedBytes rawValue = (UncompressedBytes)val;
                rawValue.reset(this.valIn, valLength2);
                --this.noBufferedValues;
                return keyLength2 + valLength2;
            }
        }
        
        public synchronized int nextRawKey(final DataOutputBuffer key) throws IOException {
            if (!this.blockCompressed) {
                this.recordLength = this.readRecordLength();
                if (this.recordLength == -1) {
                    return -1;
                }
                this.keyLength = this.in.readInt();
                key.write(this.in, this.keyLength);
                return this.keyLength;
            }
            else {
                this.syncSeen = false;
                if (this.noBufferedKeys == 0) {
                    if (this.in.getPos() >= this.end) {
                        return -1;
                    }
                    try {
                        this.readBlock();
                    }
                    catch (EOFException eof) {
                        return -1;
                    }
                }
                final int keyLength = WritableUtils.readVInt(this.keyLenIn);
                if (keyLength < 0) {
                    throw new IOException("zero length key found!");
                }
                key.write(this.keyIn, keyLength);
                --this.noBufferedKeys;
                return keyLength;
            }
        }
        
        public synchronized Object next(Object key) throws IOException {
            if (key != null && key.getClass() != this.getKeyClass()) {
                throw new IOException("wrong key class: " + key.getClass().getName() + " is not " + this.keyClass);
            }
            if (!this.blockCompressed) {
                this.outBuf.reset();
                this.keyLength = this.next(this.outBuf);
                if (this.keyLength < 0) {
                    return null;
                }
                this.valBuffer.reset(this.outBuf.getData(), this.outBuf.getLength());
                key = this.deserializeKey(key);
                this.valBuffer.mark(0);
                if (this.valBuffer.getPosition() != this.keyLength) {
                    throw new IOException(key + " read " + this.valBuffer.getPosition() + " bytes, should read " + this.keyLength);
                }
            }
            else {
                this.syncSeen = false;
                if (this.noBufferedKeys == 0) {
                    try {
                        this.readBlock();
                    }
                    catch (EOFException eof) {
                        return null;
                    }
                }
                final int keyLength = WritableUtils.readVInt(this.keyLenIn);
                if (keyLength < 0) {
                    return null;
                }
                key = this.deserializeKey(key);
                --this.noBufferedKeys;
            }
            return key;
        }
        
        private Object deserializeKey(final Object key) throws IOException {
            return this.keyDeserializer.deserialize(key);
        }
        
        public synchronized int nextRawValue(final ValueBytes val) throws IOException {
            this.seekToCurrentValue();
            if (!this.blockCompressed) {
                final int valLength = this.recordLength - this.keyLength;
                if (this.decompress) {
                    final CompressedBytes value = (CompressedBytes)val;
                    value.reset(this.in, valLength);
                }
                else {
                    final UncompressedBytes value2 = (UncompressedBytes)val;
                    value2.reset(this.in, valLength);
                }
                return valLength;
            }
            final int valLength = WritableUtils.readVInt(this.valLenIn);
            final UncompressedBytes rawValue = (UncompressedBytes)val;
            rawValue.reset(this.valIn, valLength);
            --this.noBufferedValues;
            return valLength;
        }
        
        private void handleChecksumException(final ChecksumException e) throws IOException {
            if (this.conf.getBoolean("io.skip.checksum.errors", false)) {
                SequenceFile.LOG.warn("Bad checksum at " + this.getPosition() + ". Skipping entries.");
                this.sync(this.getPosition() + this.conf.getInt("io.bytes.per.checksum", 512));
                return;
            }
            throw e;
        }
        
        synchronized void ignoreSync() {
            this.sync = null;
        }
        
        public synchronized void seek(final long position) throws IOException {
            this.in.seek(position);
            if (this.blockCompressed) {
                this.noBufferedKeys = 0;
                this.valuesDecompressed = true;
            }
        }
        
        public synchronized void sync(final long position) throws IOException {
            if (position + 20L >= this.end) {
                this.seek(this.end);
                return;
            }
            if (position < this.headerEnd) {
                this.in.seek(this.headerEnd);
                this.syncSeen = true;
                return;
            }
            try {
                this.seek(position + 4L);
                this.in.readFully(this.syncCheck);
                final int syncLen = this.sync.length;
                int i = 0;
                while (this.in.getPos() < this.end) {
                    int j;
                    for (j = 0; j < syncLen && this.sync[j] == this.syncCheck[(i + j) % syncLen]; ++j) {}
                    if (j == syncLen) {
                        this.in.seek(this.in.getPos() - 20L);
                        return;
                    }
                    this.syncCheck[i % syncLen] = this.in.readByte();
                    ++i;
                }
            }
            catch (ChecksumException e) {
                this.handleChecksumException(e);
            }
        }
        
        public synchronized boolean syncSeen() {
            return this.syncSeen;
        }
        
        public synchronized long getPosition() throws IOException {
            return this.in.getPos();
        }
        
        @Override
        public String toString() {
            return this.filename;
        }
        
        private static class FileOption extends Options.PathOption implements Option
        {
            private FileOption(final Path value) {
                super(value);
            }
        }
        
        private static class InputStreamOption extends Options.FSDataInputStreamOption implements Option
        {
            private InputStreamOption(final FSDataInputStream value) {
                super(value);
            }
        }
        
        private static class StartOption extends Options.LongOption implements Option
        {
            private StartOption(final long value) {
                super(value);
            }
        }
        
        private static class LengthOption extends Options.LongOption implements Option
        {
            private LengthOption(final long value) {
                super(value);
            }
        }
        
        private static class BufferSizeOption extends Options.IntegerOption implements Option
        {
            private BufferSizeOption(final int value) {
                super(value);
            }
        }
        
        private static class OnlyHeaderOption extends Options.BooleanOption implements Option
        {
            private OnlyHeaderOption() {
                super(true);
            }
        }
        
        public interface Option
        {
        }
    }
    
    public static class Sorter
    {
        private RawComparator comparator;
        private MergeSort mergeSort;
        private Path[] inFiles;
        private Path outFile;
        private int memory;
        private int factor;
        private FileSystem fs;
        private Class keyClass;
        private Class valClass;
        private Configuration conf;
        private Metadata metadata;
        private Progressable progressable;
        
        public Sorter(final FileSystem fs, final Class<? extends WritableComparable> keyClass, final Class valClass, final Configuration conf) {
            this(fs, WritableComparator.get(keyClass, conf), keyClass, valClass, conf);
        }
        
        public Sorter(final FileSystem fs, final RawComparator comparator, final Class keyClass, final Class valClass, final Configuration conf) {
            this(fs, comparator, keyClass, valClass, conf, new Metadata());
        }
        
        public Sorter(final FileSystem fs, final RawComparator comparator, final Class keyClass, final Class valClass, final Configuration conf, final Metadata metadata) {
            this.fs = null;
            this.progressable = null;
            this.fs = fs;
            this.comparator = comparator;
            this.keyClass = keyClass;
            this.valClass = valClass;
            if (conf.get("io.sort.mb") != null) {
                this.memory = conf.getInt("io.sort.mb", 100) * 1024 * 1024;
            }
            else {
                this.memory = conf.getInt("seq.io.sort.mb", 100) * 1024 * 1024;
            }
            if (conf.get("io.sort.factor") != null) {
                this.factor = conf.getInt("io.sort.factor", 100);
            }
            else {
                this.factor = conf.getInt("seq.io.sort.factor", 100);
            }
            this.conf = conf;
            this.metadata = metadata;
        }
        
        public void setFactor(final int factor) {
            this.factor = factor;
        }
        
        public int getFactor() {
            return this.factor;
        }
        
        public void setMemory(final int memory) {
            this.memory = memory;
        }
        
        public int getMemory() {
            return this.memory;
        }
        
        public void setProgressable(final Progressable progressable) {
            this.progressable = progressable;
        }
        
        public void sort(final Path[] inFiles, final Path outFile, final boolean deleteInput) throws IOException {
            if (this.fs.exists(outFile)) {
                throw new IOException("already exists: " + outFile);
            }
            this.inFiles = inFiles;
            this.outFile = outFile;
            final int segments = this.sortPass(deleteInput);
            if (segments > 1) {
                this.mergePass(outFile.getParent());
            }
        }
        
        public RawKeyValueIterator sortAndIterate(final Path[] inFiles, final Path tempDir, final boolean deleteInput) throws IOException {
            final Path outFile = new Path(tempDir + "/" + "all.2");
            if (this.fs.exists(outFile)) {
                throw new IOException("already exists: " + outFile);
            }
            this.inFiles = inFiles;
            this.outFile = outFile;
            final int segments = this.sortPass(deleteInput);
            if (segments > 1) {
                return this.merge(outFile.suffix(".0"), outFile.suffix(".0.index"), tempDir);
            }
            if (segments == 1) {
                return this.merge(new Path[] { outFile }, true, tempDir);
            }
            return null;
        }
        
        public void sort(final Path inFile, final Path outFile) throws IOException {
            this.sort(new Path[] { inFile }, outFile, false);
        }
        
        private int sortPass(final boolean deleteInput) throws IOException {
            if (SequenceFile.LOG.isDebugEnabled()) {
                SequenceFile.LOG.debug("running sort pass");
            }
            final SortPass sortPass = new SortPass();
            sortPass.setProgressable(this.progressable);
            this.mergeSort = new MergeSort(sortPass.new SeqFileComparator());
            try {
                return sortPass.run(deleteInput);
            }
            finally {
                sortPass.close();
            }
        }
        
        public RawKeyValueIterator merge(final List<SegmentDescriptor> segments, final Path tmpDir) throws IOException {
            final MergeQueue mQueue = new MergeQueue(segments, tmpDir, this.progressable);
            return mQueue.merge();
        }
        
        public RawKeyValueIterator merge(final Path[] inNames, final boolean deleteInputs, final Path tmpDir) throws IOException {
            return this.merge(inNames, deleteInputs, (inNames.length < this.factor) ? inNames.length : this.factor, tmpDir);
        }
        
        public RawKeyValueIterator merge(final Path[] inNames, final boolean deleteInputs, final int factor, final Path tmpDir) throws IOException {
            final ArrayList<SegmentDescriptor> a = new ArrayList<SegmentDescriptor>();
            for (int i = 0; i < inNames.length; ++i) {
                final SegmentDescriptor s = new SegmentDescriptor(0L, this.fs.getFileStatus(inNames[i]).getLen(), inNames[i]);
                s.preserveInput(!deleteInputs);
                s.doSync();
                a.add(s);
            }
            this.factor = factor;
            final MergeQueue mQueue = new MergeQueue(a, tmpDir, this.progressable);
            return mQueue.merge();
        }
        
        public RawKeyValueIterator merge(final Path[] inNames, final Path tempDir, final boolean deleteInputs) throws IOException {
            this.outFile = new Path(tempDir + "/" + "merged");
            final ArrayList<SegmentDescriptor> a = new ArrayList<SegmentDescriptor>();
            for (int i = 0; i < inNames.length; ++i) {
                final SegmentDescriptor s = new SegmentDescriptor(0L, this.fs.getFileStatus(inNames[i]).getLen(), inNames[i]);
                s.preserveInput(!deleteInputs);
                s.doSync();
                a.add(s);
            }
            this.factor = ((inNames.length < this.factor) ? inNames.length : this.factor);
            final MergeQueue mQueue = new MergeQueue(a, tempDir, this.progressable);
            return mQueue.merge();
        }
        
        public Writer cloneFileAttributes(final Path inputFile, final Path outputFile, final Progressable prog) throws IOException {
            final Reader reader = new Reader(this.conf, new Reader.Option[] { Reader.file(inputFile), new Reader.OnlyHeaderOption() });
            final CompressionType compress = reader.getCompressionType();
            final CompressionCodec codec = reader.getCompressionCodec();
            reader.close();
            final Writer writer = SequenceFile.createWriter(this.conf, Writer.file(outputFile), Writer.keyClass(this.keyClass), Writer.valueClass(this.valClass), Writer.compression(compress, codec), Writer.progressable(prog));
            return writer;
        }
        
        public void writeFile(final RawKeyValueIterator records, final Writer writer) throws IOException {
            while (records.next()) {
                writer.appendRaw(records.getKey().getData(), 0, records.getKey().getLength(), records.getValue());
            }
            writer.sync();
        }
        
        public void merge(final Path[] inFiles, final Path outFile) throws IOException {
            if (this.fs.exists(outFile)) {
                throw new IOException("already exists: " + outFile);
            }
            final RawKeyValueIterator r = this.merge(inFiles, false, outFile.getParent());
            final Writer writer = this.cloneFileAttributes(inFiles[0], outFile, null);
            this.writeFile(r, writer);
            writer.close();
        }
        
        private int mergePass(final Path tmpDir) throws IOException {
            if (SequenceFile.LOG.isDebugEnabled()) {
                SequenceFile.LOG.debug("running merge pass");
            }
            final Writer writer = this.cloneFileAttributes(this.outFile.suffix(".0"), this.outFile, null);
            final RawKeyValueIterator r = this.merge(this.outFile.suffix(".0"), this.outFile.suffix(".0.index"), tmpDir);
            this.writeFile(r, writer);
            writer.close();
            return 0;
        }
        
        private RawKeyValueIterator merge(final Path inName, final Path indexIn, final Path tmpDir) throws IOException {
            final SegmentContainer container = new SegmentContainer(inName, indexIn);
            final MergeQueue mQueue = new MergeQueue(container.getSegmentList(), tmpDir, this.progressable);
            return mQueue.merge();
        }
        
        private class SortPass
        {
            private int memoryLimit;
            private int recordLimit;
            private DataOutputBuffer rawKeys;
            private byte[] rawBuffer;
            private int[] keyOffsets;
            private int[] pointers;
            private int[] pointersCopy;
            private int[] keyLengths;
            private ValueBytes[] rawValues;
            private ArrayList segmentLengths;
            private Reader in;
            private FSDataOutputStream out;
            private FSDataOutputStream indexOut;
            private Path outName;
            private Progressable progressable;
            
            private SortPass() {
                this.memoryLimit = Sorter.this.memory / 4;
                this.recordLimit = 1000000;
                this.rawKeys = new DataOutputBuffer();
                this.keyOffsets = new int[1024];
                this.pointers = new int[this.keyOffsets.length];
                this.pointersCopy = new int[this.keyOffsets.length];
                this.keyLengths = new int[this.keyOffsets.length];
                this.rawValues = new ValueBytes[this.keyOffsets.length];
                this.segmentLengths = new ArrayList();
                this.in = null;
                this.out = null;
                this.indexOut = null;
                this.progressable = null;
            }
            
            public int run(final boolean deleteInput) throws IOException {
                int segments = 0;
                int currentFile = 0;
                boolean atEof = currentFile >= Sorter.this.inFiles.length;
                CompressionCodec codec = null;
                this.segmentLengths.clear();
                if (atEof) {
                    return 0;
                }
                this.in = new Reader(Sorter.this.fs, Sorter.this.inFiles[currentFile], Sorter.this.conf);
                final CompressionType compressionType = this.in.getCompressionType();
                codec = this.in.getCompressionCodec();
                for (int i = 0; i < this.rawValues.length; ++i) {
                    this.rawValues[i] = null;
                }
                while (!atEof) {
                    int count = 0;
                    int bytesProcessed = 0;
                    this.rawKeys.reset();
                    while (!atEof && bytesProcessed < this.memoryLimit && count < this.recordLimit) {
                        final int keyOffset = this.rawKeys.getLength();
                        final ValueBytes rawValue = (count == this.keyOffsets.length || this.rawValues[count] == null) ? this.in.createValueBytes() : this.rawValues[count];
                        final int recordLength = this.in.nextRaw(this.rawKeys, rawValue);
                        if (recordLength == -1) {
                            this.in.close();
                            if (deleteInput) {
                                Sorter.this.fs.delete(Sorter.this.inFiles[currentFile], true);
                            }
                            atEof = (++currentFile >= Sorter.this.inFiles.length);
                            if (!atEof) {
                                this.in = new Reader(Sorter.this.fs, Sorter.this.inFiles[currentFile], Sorter.this.conf);
                            }
                            else {
                                this.in = null;
                            }
                        }
                        else {
                            final int keyLength = this.rawKeys.getLength() - keyOffset;
                            if (count == this.keyOffsets.length) {
                                this.grow();
                            }
                            this.keyOffsets[count] = keyOffset;
                            this.pointers[count] = count;
                            this.keyLengths[count] = keyLength;
                            this.rawValues[count] = rawValue;
                            bytesProcessed += recordLength;
                            ++count;
                        }
                    }
                    if (SequenceFile.LOG.isDebugEnabled()) {
                        SequenceFile.LOG.debug("flushing segment " + segments);
                    }
                    this.rawBuffer = this.rawKeys.getData();
                    this.sort(count);
                    if (this.progressable != null) {
                        this.progressable.progress();
                    }
                    this.flush(count, bytesProcessed, compressionType, codec, segments == 0 && atEof);
                    ++segments;
                }
                return segments;
            }
            
            public void close() throws IOException {
                if (this.in != null) {
                    this.in.close();
                }
                if (this.out != null) {
                    this.out.close();
                }
                if (this.indexOut != null) {
                    this.indexOut.close();
                }
            }
            
            private void grow() {
                final int newLength = this.keyOffsets.length * 3 / 2;
                this.keyOffsets = this.grow(this.keyOffsets, newLength);
                this.pointers = this.grow(this.pointers, newLength);
                this.pointersCopy = new int[newLength];
                this.keyLengths = this.grow(this.keyLengths, newLength);
                this.rawValues = this.grow(this.rawValues, newLength);
            }
            
            private int[] grow(final int[] old, final int newLength) {
                final int[] result = new int[newLength];
                System.arraycopy(old, 0, result, 0, old.length);
                return result;
            }
            
            private ValueBytes[] grow(final ValueBytes[] old, final int newLength) {
                final ValueBytes[] result = new ValueBytes[newLength];
                System.arraycopy(old, 0, result, 0, old.length);
                for (int i = old.length; i < newLength; ++i) {
                    result[i] = null;
                }
                return result;
            }
            
            private void flush(final int count, final int bytesProcessed, final CompressionType compressionType, final CompressionCodec codec, final boolean done) throws IOException {
                if (this.out == null) {
                    this.outName = (done ? Sorter.this.outFile : Sorter.this.outFile.suffix(".0"));
                    this.out = Sorter.this.fs.create(this.outName);
                    if (!done) {
                        this.indexOut = Sorter.this.fs.create(this.outName.suffix(".index"));
                    }
                }
                final long segmentStart = this.out.getPos();
                final Writer writer = SequenceFile.createWriter(Sorter.this.conf, Writer.stream(this.out), Writer.keyClass(Sorter.this.keyClass), Writer.valueClass(Sorter.this.valClass), Writer.compression(compressionType, codec), Writer.metadata(done ? Sorter.this.metadata : new Metadata()));
                if (!done) {
                    writer.sync = null;
                }
                for (int i = 0; i < count; ++i) {
                    final int p = this.pointers[i];
                    writer.appendRaw(this.rawBuffer, this.keyOffsets[p], this.keyLengths[p], this.rawValues[p]);
                }
                writer.close();
                if (!done) {
                    WritableUtils.writeVLong(this.indexOut, segmentStart);
                    WritableUtils.writeVLong(this.indexOut, this.out.getPos() - segmentStart);
                    this.indexOut.flush();
                }
            }
            
            private void sort(final int count) {
                System.arraycopy(this.pointers, 0, this.pointersCopy, 0, count);
                Sorter.this.mergeSort.mergeSort(this.pointersCopy, this.pointers, 0, count);
            }
            
            public void setProgressable(final Progressable progressable) {
                this.progressable = progressable;
            }
            
            class SeqFileComparator implements Comparator<IntWritable>
            {
                @Override
                public int compare(final IntWritable I, final IntWritable J) {
                    return Sorter.this.comparator.compare(SortPass.this.rawBuffer, SortPass.this.keyOffsets[I.get()], SortPass.this.keyLengths[I.get()], SortPass.this.rawBuffer, SortPass.this.keyOffsets[J.get()], SortPass.this.keyLengths[J.get()]);
                }
            }
        }
        
        private class MergeQueue extends PriorityQueue implements RawKeyValueIterator
        {
            private boolean compress;
            private boolean blockCompress;
            private DataOutputBuffer rawKey;
            private ValueBytes rawValue;
            private long totalBytesProcessed;
            private float progPerByte;
            private Progress mergeProgress;
            private Path tmpDir;
            private Progressable progress;
            private SegmentDescriptor minSegment;
            private Map<SegmentDescriptor, Void> sortedSegmentSizes;
            
            public void put(final SegmentDescriptor stream) throws IOException {
                if (this.size() == 0) {
                    this.compress = stream.in.isCompressed();
                    this.blockCompress = stream.in.isBlockCompressed();
                }
                else if (this.compress != stream.in.isCompressed() || this.blockCompress != stream.in.isBlockCompressed()) {
                    throw new IOException("All merged files must be compressed or not.");
                }
                super.put(stream);
            }
            
            public MergeQueue(final List<SegmentDescriptor> segments, final Path tmpDir, final Progressable progress) {
                this.rawKey = new DataOutputBuffer();
                this.mergeProgress = new Progress();
                this.progress = null;
                this.sortedSegmentSizes = new TreeMap<SegmentDescriptor, Void>();
                for (int size = segments.size(), i = 0; i < size; ++i) {
                    this.sortedSegmentSizes.put(segments.get(i), null);
                }
                this.tmpDir = tmpDir;
                this.progress = progress;
            }
            
            @Override
            protected boolean lessThan(final Object a, final Object b) {
                if (this.progress != null) {
                    this.progress.progress();
                }
                final SegmentDescriptor msa = (SegmentDescriptor)a;
                final SegmentDescriptor msb = (SegmentDescriptor)b;
                return Sorter.this.comparator.compare(msa.getKey().getData(), 0, msa.getKey().getLength(), msb.getKey().getData(), 0, msb.getKey().getLength()) < 0;
            }
            
            @Override
            public void close() throws IOException {
                SegmentDescriptor ms;
                while ((ms = this.pop()) != null) {
                    ms.cleanup();
                }
                this.minSegment = null;
            }
            
            @Override
            public DataOutputBuffer getKey() throws IOException {
                return this.rawKey;
            }
            
            @Override
            public ValueBytes getValue() throws IOException {
                return this.rawValue;
            }
            
            @Override
            public boolean next() throws IOException {
                if (this.size() == 0) {
                    return false;
                }
                if (this.minSegment != null) {
                    this.adjustPriorityQueue(this.minSegment);
                    if (this.size() == 0) {
                        this.minSegment = null;
                        return false;
                    }
                }
                this.minSegment = this.top();
                final long startPos = this.minSegment.in.getPosition();
                this.rawKey = this.minSegment.getKey();
                if (this.rawValue == null) {
                    this.rawValue = this.minSegment.in.createValueBytes();
                }
                this.minSegment.nextRawValue(this.rawValue);
                final long endPos = this.minSegment.in.getPosition();
                this.updateProgress(endPos - startPos);
                return true;
            }
            
            @Override
            public Progress getProgress() {
                return this.mergeProgress;
            }
            
            private void adjustPriorityQueue(final SegmentDescriptor ms) throws IOException {
                final long startPos = ms.in.getPosition();
                final boolean hasNext = ms.nextRawKey();
                final long endPos = ms.in.getPosition();
                this.updateProgress(endPos - startPos);
                if (hasNext) {
                    this.adjustTop();
                }
                else {
                    this.pop();
                    ms.cleanup();
                }
            }
            
            private void updateProgress(final long bytesProcessed) {
                this.totalBytesProcessed += bytesProcessed;
                if (this.progPerByte > 0.0f) {
                    this.mergeProgress.set(this.totalBytesProcessed * this.progPerByte);
                }
            }
            
            public RawKeyValueIterator merge() throws IOException {
                int numSegments = this.sortedSegmentSizes.size();
                final int origFactor = Sorter.this.factor;
                int passNo = 1;
                final LocalDirAllocator lDirAlloc = new LocalDirAllocator("io.seqfile.local.dir");
                List<SegmentDescriptor> segmentsToMerge;
                while (true) {
                    Sorter.this.factor = this.getPassFactor(passNo, numSegments);
                    segmentsToMerge = new ArrayList<SegmentDescriptor>();
                    int segmentsConsidered = 0;
                    int numSegmentsToConsider = Sorter.this.factor;
                    while (true) {
                        final SegmentDescriptor[] mStream = this.getSegmentDescriptors(numSegmentsToConsider);
                        for (int i = 0; i < mStream.length; ++i) {
                            if (mStream[i].nextRawKey()) {
                                segmentsToMerge.add(mStream[i]);
                                ++segmentsConsidered;
                                this.updateProgress(mStream[i].in.getPosition());
                            }
                            else {
                                mStream[i].cleanup();
                                --numSegments;
                            }
                        }
                        if (segmentsConsidered == Sorter.this.factor || this.sortedSegmentSizes.size() == 0) {
                            break;
                        }
                        numSegmentsToConsider = Sorter.this.factor - segmentsConsidered;
                    }
                    this.initialize(segmentsToMerge.size());
                    this.clear();
                    for (int j = 0; j < segmentsToMerge.size(); ++j) {
                        this.put(segmentsToMerge.get(j));
                    }
                    if (numSegments <= Sorter.this.factor) {
                        break;
                    }
                    long approxOutputSize = 0L;
                    for (final SegmentDescriptor s : segmentsToMerge) {
                        approxOutputSize += (long)(s.segmentLength + ChecksumFileSystem.getApproxChkSumLength(s.segmentLength));
                    }
                    final Path tmpFilename = new Path(this.tmpDir, "intermediate").suffix("." + passNo);
                    final Path outputFile = lDirAlloc.getLocalPathForWrite(tmpFilename.toString(), approxOutputSize, Sorter.this.conf);
                    if (SequenceFile.LOG.isDebugEnabled()) {
                        SequenceFile.LOG.debug("writing intermediate results to " + outputFile);
                    }
                    final Writer writer = Sorter.this.cloneFileAttributes(Sorter.this.fs.makeQualified(segmentsToMerge.get(0).segmentPathName), Sorter.this.fs.makeQualified(outputFile), null);
                    writer.sync = null;
                    Sorter.this.writeFile(this, writer);
                    writer.close();
                    this.close();
                    final SegmentDescriptor tempSegment = new SegmentDescriptor(0L, Sorter.this.fs.getFileStatus(outputFile).getLen(), outputFile);
                    this.sortedSegmentSizes.put(tempSegment, null);
                    numSegments = this.sortedSegmentSizes.size();
                    ++passNo;
                    Sorter.this.factor = origFactor;
                }
                long totalBytes = 0L;
                for (int k = 0; k < segmentsToMerge.size(); ++k) {
                    totalBytes += segmentsToMerge.get(k).segmentLength;
                }
                if (totalBytes != 0L) {
                    this.progPerByte = 1.0f / totalBytes;
                }
                Sorter.this.factor = origFactor;
                return this;
            }
            
            public int getPassFactor(final int passNo, final int numSegments) {
                if (passNo > 1 || numSegments <= Sorter.this.factor || Sorter.this.factor == 1) {
                    return Sorter.this.factor;
                }
                final int mod = (numSegments - 1) % (Sorter.this.factor - 1);
                if (mod == 0) {
                    return Sorter.this.factor;
                }
                return mod + 1;
            }
            
            public SegmentDescriptor[] getSegmentDescriptors(int numDescriptors) {
                if (numDescriptors > this.sortedSegmentSizes.size()) {
                    numDescriptors = this.sortedSegmentSizes.size();
                }
                final SegmentDescriptor[] SegmentDescriptors = new SegmentDescriptor[numDescriptors];
                final Iterator iter = this.sortedSegmentSizes.keySet().iterator();
                int i = 0;
                while (i < numDescriptors) {
                    SegmentDescriptors[i++] = iter.next();
                    iter.remove();
                }
                return SegmentDescriptors;
            }
        }
        
        public class SegmentDescriptor implements Comparable
        {
            long segmentOffset;
            long segmentLength;
            Path segmentPathName;
            boolean ignoreSync;
            private Reader in;
            private DataOutputBuffer rawKey;
            private boolean preserveInput;
            
            public SegmentDescriptor(final long segmentOffset, final long segmentLength, final Path segmentPathName) {
                this.ignoreSync = true;
                this.in = null;
                this.rawKey = null;
                this.preserveInput = false;
                this.segmentOffset = segmentOffset;
                this.segmentLength = segmentLength;
                this.segmentPathName = segmentPathName;
            }
            
            public void doSync() {
                this.ignoreSync = false;
            }
            
            public void preserveInput(final boolean preserve) {
                this.preserveInput = preserve;
            }
            
            public boolean shouldPreserveInput() {
                return this.preserveInput;
            }
            
            @Override
            public int compareTo(final Object o) {
                final SegmentDescriptor that = (SegmentDescriptor)o;
                if (this.segmentLength != that.segmentLength) {
                    return (this.segmentLength < that.segmentLength) ? -1 : 1;
                }
                if (this.segmentOffset != that.segmentOffset) {
                    return (this.segmentOffset < that.segmentOffset) ? -1 : 1;
                }
                return this.segmentPathName.toString().compareTo(that.segmentPathName.toString());
            }
            
            @Override
            public boolean equals(final Object o) {
                if (!(o instanceof SegmentDescriptor)) {
                    return false;
                }
                final SegmentDescriptor that = (SegmentDescriptor)o;
                return this.segmentLength == that.segmentLength && this.segmentOffset == that.segmentOffset && this.segmentPathName.toString().equals(that.segmentPathName.toString());
            }
            
            @Override
            public int hashCode() {
                return 629 + (int)(this.segmentOffset ^ this.segmentOffset >>> 32);
            }
            
            public boolean nextRawKey() throws IOException {
                if (this.in == null) {
                    final int bufferSize = getBufferSize(Sorter.this.conf);
                    final Reader reader = new Reader(Sorter.this.conf, new Reader.Option[] { Reader.file(this.segmentPathName), Reader.bufferSize(bufferSize), Reader.start(this.segmentOffset), Reader.length(this.segmentLength) });
                    if (this.ignoreSync) {
                        reader.ignoreSync();
                    }
                    if (reader.getKeyClass() != Sorter.this.keyClass) {
                        throw new IOException("wrong key class: " + reader.getKeyClass() + " is not " + Sorter.this.keyClass);
                    }
                    if (reader.getValueClass() != Sorter.this.valClass) {
                        throw new IOException("wrong value class: " + reader.getValueClass() + " is not " + Sorter.this.valClass);
                    }
                    this.in = reader;
                    this.rawKey = new DataOutputBuffer();
                }
                this.rawKey.reset();
                final int keyLength = this.in.nextRawKey(this.rawKey);
                return keyLength >= 0;
            }
            
            public int nextRawValue(final ValueBytes rawValue) throws IOException {
                final int valLength = this.in.nextRawValue(rawValue);
                return valLength;
            }
            
            public DataOutputBuffer getKey() {
                return this.rawKey;
            }
            
            private void close() throws IOException {
                this.in.close();
                this.in = null;
            }
            
            public void cleanup() throws IOException {
                this.close();
                if (!this.preserveInput) {
                    Sorter.this.fs.delete(this.segmentPathName, true);
                }
            }
        }
        
        private class LinkedSegmentsDescriptor extends SegmentDescriptor
        {
            SegmentContainer parentContainer;
            
            public LinkedSegmentsDescriptor(final long segmentOffset, final long segmentLength, final Path segmentPathName, final SegmentContainer parent) {
                super(segmentOffset, segmentLength, segmentPathName);
                this.parentContainer = null;
                this.parentContainer = parent;
            }
            
            @Override
            public void cleanup() throws IOException {
                SegmentDescriptor.this.close();
                if (super.shouldPreserveInput()) {
                    return;
                }
                this.parentContainer.cleanup();
            }
            
            @Override
            public boolean equals(final Object o) {
                return o instanceof LinkedSegmentsDescriptor && super.equals(o);
            }
        }
        
        private class SegmentContainer
        {
            private int numSegmentsCleanedUp;
            private int numSegmentsContained;
            private Path inName;
            private ArrayList<SegmentDescriptor> segments;
            
            public SegmentContainer(final Path inName, final Path indexIn) throws IOException {
                this.numSegmentsCleanedUp = 0;
                this.segments = new ArrayList<SegmentDescriptor>();
                final FSDataInputStream fsIndexIn = Sorter.this.fs.open(indexIn);
                final long end = Sorter.this.fs.getFileStatus(indexIn).getLen();
                while (fsIndexIn.getPos() < end) {
                    final long segmentOffset = WritableUtils.readVLong(fsIndexIn);
                    final long segmentLength = WritableUtils.readVLong(fsIndexIn);
                    final Path segmentName = inName;
                    this.segments.add(new LinkedSegmentsDescriptor(segmentOffset, segmentLength, segmentName, this));
                }
                fsIndexIn.close();
                Sorter.this.fs.delete(indexIn, true);
                this.numSegmentsContained = this.segments.size();
                this.inName = inName;
            }
            
            public List<SegmentDescriptor> getSegmentList() {
                return this.segments;
            }
            
            public void cleanup() throws IOException {
                ++this.numSegmentsCleanedUp;
                if (this.numSegmentsCleanedUp == this.numSegmentsContained) {
                    Sorter.this.fs.delete(this.inName, true);
                }
            }
        }
        
        public interface RawKeyValueIterator
        {
            DataOutputBuffer getKey() throws IOException;
            
            ValueBytes getValue() throws IOException;
            
            boolean next() throws IOException;
            
            void close() throws IOException;
            
            Progress getProgress();
        }
    }
    
    public interface ValueBytes
    {
        void writeUncompressedBytes(final DataOutputStream p0) throws IOException;
        
        void writeCompressedBytes(final DataOutputStream p0) throws IllegalArgumentException, IOException;
        
        int getSize();
    }
}
