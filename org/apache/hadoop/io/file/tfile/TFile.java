// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.util.Iterator;
import org.apache.hadoop.io.DataOutputBuffer;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.WritableComparator;
import java.io.EOFException;
import java.io.InputStream;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.BytesWritable;
import java.io.DataInputStream;
import java.io.DataInput;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.DataOutput;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.BoundedByteArrayOutputStream;
import java.io.Closeable;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Comparator;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class TFile
{
    static final Logger LOG;
    private static final String CHUNK_BUF_SIZE_ATTR = "tfile.io.chunk.size";
    private static final String FS_INPUT_BUF_SIZE_ATTR = "tfile.fs.input.buffer.size";
    private static final String FS_OUTPUT_BUF_SIZE_ATTR = "tfile.fs.output.buffer.size";
    private static final int MAX_KEY_SIZE = 65536;
    static final Utils.Version API_VERSION;
    public static final String COMPRESSION_GZ = "gz";
    public static final String COMPRESSION_LZO = "lzo";
    public static final String COMPRESSION_NONE = "none";
    public static final String COMPARATOR_MEMCMP = "memcmp";
    public static final String COMPARATOR_JCLASS = "jclass:";
    
    static int getChunkBufferSize(final Configuration conf) {
        final int ret = conf.getInt("tfile.io.chunk.size", 1048576);
        return (ret > 0) ? ret : 1048576;
    }
    
    static int getFSInputBufferSize(final Configuration conf) {
        return conf.getInt("tfile.fs.input.buffer.size", 262144);
    }
    
    static int getFSOutputBufferSize(final Configuration conf) {
        return conf.getInt("tfile.fs.output.buffer.size", 262144);
    }
    
    public static Comparator<RawComparable> makeComparator(final String name) {
        return TFileMeta.makeComparator(name);
    }
    
    private TFile() {
    }
    
    public static String[] getSupportedCompressionAlgorithms() {
        return Compression.getSupportedAlgorithms();
    }
    
    public static void main(final String[] args) {
        System.out.printf("TFile Dumper (TFile %s, BCFile %s)%n", TFile.API_VERSION.toString(), BCFile.API_VERSION.toString());
        if (args.length == 0) {
            System.out.println("Usage: java ... org.apache.hadoop.io.file.tfile.TFile tfile-path [tfile-path ...]");
            System.exit(0);
        }
        final Configuration conf = new Configuration();
        for (final String file : args) {
            System.out.println("===" + file + "===");
            try {
                TFileDumper.dumpInfo(file, System.out, conf);
            }
            catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TFile.class);
        API_VERSION = new Utils.Version((short)1, (short)0);
    }
    
    @InterfaceStability.Evolving
    public static class Writer implements Closeable
    {
        private final int sizeMinBlock;
        final TFileIndex tfileIndex;
        final TFileMeta tfileMeta;
        private BCFile.Writer writerBCF;
        BCFile.Writer.BlockAppender blkAppender;
        long blkRecordCount;
        BoundedByteArrayOutputStream currentKeyBufferOS;
        BoundedByteArrayOutputStream lastKeyBufferOS;
        private byte[] valueBuffer;
        State state;
        Configuration conf;
        long errorCount;
        
        public Writer(final FSDataOutputStream fsdos, final int minBlockSize, final String compressName, final String comparator, final Configuration conf) throws IOException {
            this.state = State.READY;
            this.errorCount = 0L;
            this.sizeMinBlock = minBlockSize;
            this.tfileMeta = new TFileMeta(comparator);
            this.tfileIndex = new TFileIndex(this.tfileMeta.getComparator());
            this.writerBCF = new BCFile.Writer(fsdos, compressName, conf);
            this.currentKeyBufferOS = new BoundedByteArrayOutputStream(65536);
            this.lastKeyBufferOS = new BoundedByteArrayOutputStream(65536);
            this.conf = conf;
        }
        
        @Override
        public void close() throws IOException {
            if (this.state == State.CLOSED) {
                return;
            }
            try {
                if (this.errorCount == 0L) {
                    if (this.state != State.READY) {
                        throw new IllegalStateException("Cannot close TFile in the middle of key-value insertion.");
                    }
                    this.finishDataBlock(true);
                    final BCFile.Writer.BlockAppender outMeta = this.writerBCF.prepareMetaBlock("TFile.meta", "none");
                    try {
                        this.tfileMeta.write(outMeta);
                    }
                    finally {
                        outMeta.close();
                    }
                    final BCFile.Writer.BlockAppender outIndex = this.writerBCF.prepareMetaBlock("TFile.index");
                    try {
                        this.tfileIndex.write(outIndex);
                    }
                    finally {
                        outIndex.close();
                    }
                    this.writerBCF.close();
                }
            }
            finally {
                IOUtils.cleanupWithLogger(TFile.LOG, this.blkAppender, this.writerBCF);
                this.blkAppender = null;
                this.writerBCF = null;
                this.state = State.CLOSED;
            }
        }
        
        public void append(final byte[] key, final byte[] value) throws IOException {
            this.append(key, 0, key.length, value, 0, value.length);
        }
        
        public void append(final byte[] key, final int koff, final int klen, final byte[] value, final int voff, final int vlen) throws IOException {
            if ((koff | klen | koff + klen | key.length - (koff + klen)) < 0) {
                throw new IndexOutOfBoundsException("Bad key buffer offset-length combination.");
            }
            if ((voff | vlen | voff + vlen | value.length - (voff + vlen)) < 0) {
                throw new IndexOutOfBoundsException("Bad value buffer offset-length combination.");
            }
            try {
                final DataOutputStream dosKey = this.prepareAppendKey(klen);
                try {
                    ++this.errorCount;
                    dosKey.write(key, koff, klen);
                    --this.errorCount;
                }
                finally {
                    dosKey.close();
                }
                final DataOutputStream dosValue = this.prepareAppendValue(vlen);
                try {
                    ++this.errorCount;
                    dosValue.write(value, voff, vlen);
                    --this.errorCount;
                }
                finally {
                    dosValue.close();
                }
            }
            finally {
                this.state = State.READY;
            }
        }
        
        public DataOutputStream prepareAppendKey(final int length) throws IOException {
            if (this.state != State.READY) {
                throw new IllegalStateException("Incorrect state to start a new key: " + this.state.name());
            }
            this.initDataBlock();
            final DataOutputStream ret = new KeyRegister(length);
            this.state = State.IN_KEY;
            return ret;
        }
        
        public DataOutputStream prepareAppendValue(final int length) throws IOException {
            if (this.state != State.END_KEY) {
                throw new IllegalStateException("Incorrect state to start a new value: " + this.state.name());
            }
            DataOutputStream ret;
            if (length < 0) {
                if (this.valueBuffer == null) {
                    this.valueBuffer = new byte[TFile.getChunkBufferSize(this.conf)];
                }
                ret = new ValueRegister(new Chunk.ChunkEncoder(this.blkAppender, this.valueBuffer));
            }
            else {
                ret = new ValueRegister(new Chunk.SingleChunkEncoder(this.blkAppender, length));
            }
            this.state = State.IN_VALUE;
            return ret;
        }
        
        public DataOutputStream prepareMetaBlock(final String name, final String compressName) throws IOException, MetaBlockAlreadyExists {
            if (this.state != State.READY) {
                throw new IllegalStateException("Incorrect state to start a Meta Block: " + this.state.name());
            }
            this.finishDataBlock(true);
            final DataOutputStream outputStream = this.writerBCF.prepareMetaBlock(name, compressName);
            return outputStream;
        }
        
        public DataOutputStream prepareMetaBlock(final String name) throws IOException, MetaBlockAlreadyExists {
            if (this.state != State.READY) {
                throw new IllegalStateException("Incorrect state to start a Meta Block: " + this.state.name());
            }
            this.finishDataBlock(true);
            return this.writerBCF.prepareMetaBlock(name);
        }
        
        private void initDataBlock() throws IOException {
            if (this.blkAppender == null) {
                this.blkAppender = this.writerBCF.prepareDataBlock();
            }
        }
        
        void finishDataBlock(final boolean bForceFinish) throws IOException {
            if (this.blkAppender == null) {
                return;
            }
            if (bForceFinish || this.blkAppender.getCompressedSize() >= this.sizeMinBlock) {
                final TFileIndexEntry keyLast = new TFileIndexEntry(this.lastKeyBufferOS.getBuffer(), 0, this.lastKeyBufferOS.size(), this.blkRecordCount);
                this.tfileIndex.addEntry(keyLast);
                this.blkAppender.close();
                this.blkAppender = null;
                this.blkRecordCount = 0L;
            }
        }
        
        private enum State
        {
            READY, 
            IN_KEY, 
            END_KEY, 
            IN_VALUE, 
            CLOSED;
        }
        
        private class KeyRegister extends DataOutputStream
        {
            private final int expectedLength;
            private boolean closed;
            
            public KeyRegister(final int len) {
                super(Writer.this.currentKeyBufferOS);
                this.closed = false;
                if (len >= 0) {
                    Writer.this.currentKeyBufferOS.reset(len);
                }
                else {
                    Writer.this.currentKeyBufferOS.reset();
                }
                this.expectedLength = len;
            }
            
            @Override
            public void close() throws IOException {
                if (this.closed) {
                    return;
                }
                try {
                    final Writer this$0 = Writer.this;
                    ++this$0.errorCount;
                    final byte[] key = Writer.this.currentKeyBufferOS.getBuffer();
                    final int len = Writer.this.currentKeyBufferOS.size();
                    if (this.expectedLength >= 0 && this.expectedLength != len) {
                        throw new IOException("Incorrect key length: expected=" + this.expectedLength + " actual=" + len);
                    }
                    Utils.writeVInt(Writer.this.blkAppender, len);
                    Writer.this.blkAppender.write(key, 0, len);
                    if (Writer.this.tfileIndex.getFirstKey() == null) {
                        Writer.this.tfileIndex.setFirstKey(key, 0, len);
                    }
                    if (Writer.this.tfileMeta.isSorted() && Writer.this.tfileMeta.getRecordCount() > 0L) {
                        final byte[] lastKey = Writer.this.lastKeyBufferOS.getBuffer();
                        final int lastLen = Writer.this.lastKeyBufferOS.size();
                        if (Writer.this.tfileMeta.getComparator().compare(key, 0, len, lastKey, 0, lastLen) < 0) {
                            throw new IOException("Keys are not added in sorted order");
                        }
                    }
                    final BoundedByteArrayOutputStream tmp = Writer.this.currentKeyBufferOS;
                    Writer.this.currentKeyBufferOS = Writer.this.lastKeyBufferOS;
                    Writer.this.lastKeyBufferOS = tmp;
                    final Writer this$2 = Writer.this;
                    --this$2.errorCount;
                }
                finally {
                    this.closed = true;
                    Writer.this.state = State.END_KEY;
                }
            }
        }
        
        private class ValueRegister extends DataOutputStream
        {
            private boolean closed;
            
            public ValueRegister(final OutputStream os) {
                super(os);
                this.closed = false;
            }
            
            @Override
            public void flush() {
            }
            
            @Override
            public void close() throws IOException {
                if (this.closed) {
                    return;
                }
                try {
                    final Writer this$0 = Writer.this;
                    ++this$0.errorCount;
                    super.close();
                    final Writer this$2 = Writer.this;
                    ++this$2.blkRecordCount;
                    Writer.this.tfileMeta.incRecordCount();
                    Writer.this.finishDataBlock(false);
                    final Writer this$3 = Writer.this;
                    --this$3.errorCount;
                }
                finally {
                    this.closed = true;
                    Writer.this.state = State.READY;
                }
            }
        }
    }
    
    @InterfaceStability.Evolving
    public static class Reader implements Closeable
    {
        final BCFile.Reader readerBCF;
        TFileIndex tfileIndex;
        final TFileMeta tfileMeta;
        final CompareUtils.BytesComparator comparator;
        private final Location begin;
        private final Location end;
        
        public Reader(final FSDataInputStream fsdis, final long fileLength, final Configuration conf) throws IOException {
            this.tfileIndex = null;
            this.readerBCF = new BCFile.Reader(fsdis, fileLength, conf);
            final BCFile.Reader.BlockReader brMeta = this.readerBCF.getMetaBlock("TFile.meta");
            try {
                this.tfileMeta = new TFileMeta(brMeta);
            }
            finally {
                brMeta.close();
            }
            this.comparator = this.tfileMeta.getComparator();
            this.begin = new Location(0, 0L);
            this.end = new Location(this.readerBCF.getBlockCount(), 0L);
        }
        
        @Override
        public void close() throws IOException {
            this.readerBCF.close();
        }
        
        Location begin() {
            return this.begin;
        }
        
        Location end() {
            return this.end;
        }
        
        public String getComparatorName() {
            return this.tfileMeta.getComparatorString();
        }
        
        public boolean isSorted() {
            return this.tfileMeta.isSorted();
        }
        
        public long getEntryCount() {
            return this.tfileMeta.getRecordCount();
        }
        
        synchronized void checkTFileDataIndex() throws IOException {
            if (this.tfileIndex == null) {
                final BCFile.Reader.BlockReader brIndex = this.readerBCF.getMetaBlock("TFile.index");
                try {
                    this.tfileIndex = new TFileIndex(this.readerBCF.getBlockCount(), brIndex, this.tfileMeta.getComparator());
                }
                finally {
                    brIndex.close();
                }
            }
        }
        
        public RawComparable getFirstKey() throws IOException {
            this.checkTFileDataIndex();
            return this.tfileIndex.getFirstKey();
        }
        
        public RawComparable getLastKey() throws IOException {
            this.checkTFileDataIndex();
            return this.tfileIndex.getLastKey();
        }
        
        public Comparator<Scanner.Entry> getEntryComparator() {
            if (!this.isSorted()) {
                throw new RuntimeException("Entries are not comparable for unsorted TFiles");
            }
            return new Comparator<Scanner.Entry>() {
                @Override
                public int compare(final Scanner.Entry o1, final Scanner.Entry o2) {
                    return Reader.this.comparator.compare(o1.getKeyBuffer(), 0, o1.getKeyLength(), o2.getKeyBuffer(), 0, o2.getKeyLength());
                }
            };
        }
        
        public Comparator<RawComparable> getComparator() {
            return this.comparator;
        }
        
        public DataInputStream getMetaBlock(final String name) throws IOException, MetaBlockDoesNotExist {
            return this.readerBCF.getMetaBlock(name);
        }
        
        Location getBlockContainsKey(final RawComparable key, final boolean greater) throws IOException {
            if (!this.isSorted()) {
                throw new RuntimeException("Seeking in unsorted TFile");
            }
            this.checkTFileDataIndex();
            final int blkIndex = greater ? this.tfileIndex.upperBound(key) : this.tfileIndex.lowerBound(key);
            if (blkIndex < 0) {
                return this.end;
            }
            return new Location(blkIndex, 0L);
        }
        
        Location getLocationByRecordNum(final long recNum) throws IOException {
            this.checkTFileDataIndex();
            return this.tfileIndex.getLocationByRecordNum(recNum);
        }
        
        long getRecordNumByLocation(final Location location) throws IOException {
            this.checkTFileDataIndex();
            return this.tfileIndex.getRecordNumByLocation(location);
        }
        
        int compareKeys(final byte[] a, final int o1, final int l1, final byte[] b, final int o2, final int l2) {
            if (!this.isSorted()) {
                throw new RuntimeException("Cannot compare keys for unsorted TFiles.");
            }
            return this.comparator.compare(a, o1, l1, b, o2, l2);
        }
        
        int compareKeys(final RawComparable a, final RawComparable b) {
            if (!this.isSorted()) {
                throw new RuntimeException("Cannot compare keys for unsorted TFiles.");
            }
            return this.comparator.compare(a, b);
        }
        
        Location getLocationNear(final long offset) {
            final int blockIndex = this.readerBCF.getBlockIndexNear(offset);
            if (blockIndex == -1) {
                return this.end;
            }
            return new Location(blockIndex, 0L);
        }
        
        public long getRecordNumNear(final long offset) throws IOException {
            return this.getRecordNumByLocation(this.getLocationNear(offset));
        }
        
        public RawComparable getKeyNear(final long offset) throws IOException {
            final int blockIndex = this.readerBCF.getBlockIndexNear(offset);
            if (blockIndex == -1) {
                return null;
            }
            this.checkTFileDataIndex();
            return new ByteArray(this.tfileIndex.getEntry(blockIndex).key);
        }
        
        public Scanner createScanner() throws IOException {
            return new Scanner(this, this.begin, this.end);
        }
        
        public Scanner createScannerByByteRange(final long offset, final long length) throws IOException {
            return new Scanner(this, offset, offset + length);
        }
        
        @Deprecated
        public Scanner createScanner(final byte[] beginKey, final byte[] endKey) throws IOException {
            return this.createScannerByKey(beginKey, endKey);
        }
        
        public Scanner createScannerByKey(final byte[] beginKey, final byte[] endKey) throws IOException {
            return this.createScannerByKey((beginKey == null) ? null : new ByteArray(beginKey, 0, beginKey.length), (endKey == null) ? null : new ByteArray(endKey, 0, endKey.length));
        }
        
        @Deprecated
        public Scanner createScanner(final RawComparable beginKey, final RawComparable endKey) throws IOException {
            return this.createScannerByKey(beginKey, endKey);
        }
        
        public Scanner createScannerByKey(final RawComparable beginKey, final RawComparable endKey) throws IOException {
            if (beginKey != null && endKey != null && this.compareKeys(beginKey, endKey) >= 0) {
                return new Scanner(this, beginKey, beginKey);
            }
            return new Scanner(this, beginKey, endKey);
        }
        
        public Scanner createScannerByRecordNum(long beginRecNum, long endRecNum) throws IOException {
            if (beginRecNum < 0L) {
                beginRecNum = 0L;
            }
            if (endRecNum < 0L || endRecNum > this.getEntryCount()) {
                endRecNum = this.getEntryCount();
            }
            return new Scanner(this, this.getLocationByRecordNum(beginRecNum), this.getLocationByRecordNum(endRecNum));
        }
        
        long getBlockEntryCount(final int curBid) {
            return this.tfileIndex.getEntry(curBid).entries();
        }
        
        BCFile.Reader.BlockReader getBlockReader(final int blockIndex) throws IOException {
            return this.readerBCF.getDataBlock(blockIndex);
        }
        
        static final class Location implements Comparable<Location>, Cloneable
        {
            private int blockIndex;
            private long recordIndex;
            
            Location(final int blockIndex, final long recordIndex) {
                this.set(blockIndex, recordIndex);
            }
            
            void incRecordIndex() {
                ++this.recordIndex;
            }
            
            Location(final Location other) {
                this.set(other);
            }
            
            int getBlockIndex() {
                return this.blockIndex;
            }
            
            long getRecordIndex() {
                return this.recordIndex;
            }
            
            void set(final int blockIndex, final long recordIndex) {
                if (((long)blockIndex | recordIndex) < 0L) {
                    throw new IllegalArgumentException("Illegal parameter for BlockLocation.");
                }
                this.blockIndex = blockIndex;
                this.recordIndex = recordIndex;
            }
            
            void set(final Location other) {
                this.set(other.blockIndex, other.recordIndex);
            }
            
            @Override
            public int compareTo(final Location other) {
                return this.compareTo(other.blockIndex, other.recordIndex);
            }
            
            int compareTo(final int bid, final long rid) {
                if (this.blockIndex != bid) {
                    return this.blockIndex - bid;
                }
                final long ret = this.recordIndex - rid;
                if (ret > 0L) {
                    return 1;
                }
                if (ret < 0L) {
                    return -1;
                }
                return 0;
            }
            
            @Override
            protected Location clone() {
                return new Location(this.blockIndex, this.recordIndex);
            }
            
            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 31 + this.blockIndex;
                result = (int)(31 * result + this.recordIndex);
                return result;
            }
            
            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (this.getClass() != obj.getClass()) {
                    return false;
                }
                final Location other = (Location)obj;
                return this.blockIndex == other.blockIndex && this.recordIndex == other.recordIndex;
            }
        }
        
        public static class Scanner implements Closeable
        {
            final Reader reader;
            private BCFile.Reader.BlockReader blkReader;
            Location beginLocation;
            Location endLocation;
            Location currentLocation;
            boolean valueChecked;
            final byte[] keyBuffer;
            int klen;
            static final int MAX_VAL_TRANSFER_BUF_SIZE = 131072;
            BytesWritable valTransferBuffer;
            DataInputBuffer keyDataInputStream;
            Chunk.ChunkDecoder valueBufferInputStream;
            DataInputStream valueDataInputStream;
            int vlen;
            
            protected Scanner(final Reader reader, final long offBegin, final long offEnd) throws IOException {
                this(reader, reader.getLocationNear(offBegin), reader.getLocationNear(offEnd));
            }
            
            Scanner(final Reader reader, final Location begin, final Location end) throws IOException {
                this.valueChecked = false;
                this.klen = -1;
                (this.reader = reader).checkTFileDataIndex();
                this.beginLocation = begin;
                this.endLocation = end;
                this.valTransferBuffer = new BytesWritable();
                this.keyBuffer = new byte[65536];
                this.keyDataInputStream = new DataInputBuffer();
                this.valueBufferInputStream = new Chunk.ChunkDecoder();
                this.valueDataInputStream = new DataInputStream(this.valueBufferInputStream);
                if (this.beginLocation.compareTo(this.endLocation) >= 0) {
                    this.currentLocation = new Location(this.endLocation);
                }
                else {
                    this.currentLocation = new Location(0, 0L);
                    this.initBlock(this.beginLocation.getBlockIndex());
                    this.inBlockAdvance(this.beginLocation.getRecordIndex());
                }
            }
            
            protected Scanner(final Reader reader, final RawComparable beginKey, final RawComparable endKey) throws IOException {
                this(reader, (beginKey == null) ? reader.begin() : reader.getBlockContainsKey(beginKey, false), reader.end());
                if (beginKey != null) {
                    this.inBlockAdvance(beginKey, false);
                    this.beginLocation.set(this.currentLocation);
                }
                if (endKey != null) {
                    this.seekTo(endKey, false);
                    this.endLocation.set(this.currentLocation);
                    this.seekTo(this.beginLocation);
                }
            }
            
            public boolean seekTo(final byte[] key) throws IOException {
                return this.seekTo(key, 0, key.length);
            }
            
            public boolean seekTo(final byte[] key, final int keyOffset, final int keyLen) throws IOException {
                return this.seekTo(new ByteArray(key, keyOffset, keyLen), false);
            }
            
            private boolean seekTo(final RawComparable key, final boolean beyond) throws IOException {
                Location l = this.reader.getBlockContainsKey(key, beyond);
                if (l.compareTo(this.beginLocation) < 0) {
                    l = this.beginLocation;
                }
                else if (l.compareTo(this.endLocation) >= 0) {
                    this.seekTo(this.endLocation);
                    return false;
                }
                if (this.atEnd() || l.getBlockIndex() != this.currentLocation.getBlockIndex() || this.compareCursorKeyTo(key) >= 0) {
                    this.seekTo(l);
                }
                return this.inBlockAdvance(key, beyond);
            }
            
            private void seekTo(final Location l) throws IOException {
                if (l.compareTo(this.beginLocation) < 0) {
                    throw new IllegalArgumentException("Attempt to seek before the begin location.");
                }
                if (l.compareTo(this.endLocation) > 0) {
                    throw new IllegalArgumentException("Attempt to seek after the end location.");
                }
                if (l.compareTo(this.endLocation) == 0) {
                    this.parkCursorAtEnd();
                    return;
                }
                if (l.getBlockIndex() != this.currentLocation.getBlockIndex()) {
                    this.initBlock(l.getBlockIndex());
                }
                else {
                    if (this.valueChecked) {
                        this.inBlockAdvance(1L);
                    }
                    if (l.getRecordIndex() < this.currentLocation.getRecordIndex()) {
                        this.initBlock(l.getBlockIndex());
                    }
                }
                this.inBlockAdvance(l.getRecordIndex() - this.currentLocation.getRecordIndex());
            }
            
            public void rewind() throws IOException {
                this.seekTo(this.beginLocation);
            }
            
            public void seekToEnd() throws IOException {
                this.parkCursorAtEnd();
            }
            
            public void lowerBound(final byte[] key) throws IOException {
                this.lowerBound(key, 0, key.length);
            }
            
            public void lowerBound(final byte[] key, final int keyOffset, final int keyLen) throws IOException {
                this.seekTo(new ByteArray(key, keyOffset, keyLen), false);
            }
            
            public void upperBound(final byte[] key) throws IOException {
                this.upperBound(key, 0, key.length);
            }
            
            public void upperBound(final byte[] key, final int keyOffset, final int keyLen) throws IOException {
                this.seekTo(new ByteArray(key, keyOffset, keyLen), true);
            }
            
            public boolean advance() throws IOException {
                if (this.atEnd()) {
                    return false;
                }
                final int curBid = this.currentLocation.getBlockIndex();
                final long curRid = this.currentLocation.getRecordIndex();
                final long entriesInBlock = this.reader.getBlockEntryCount(curBid);
                if (curRid + 1L >= entriesInBlock) {
                    if (this.endLocation.compareTo(curBid + 1, 0L) <= 0) {
                        this.parkCursorAtEnd();
                    }
                    else {
                        this.initBlock(curBid + 1);
                    }
                }
                else {
                    this.inBlockAdvance(1L);
                }
                return true;
            }
            
            private void initBlock(final int blockIndex) throws IOException {
                this.klen = -1;
                if (this.blkReader != null) {
                    try {
                        this.blkReader.close();
                    }
                    finally {
                        this.blkReader = null;
                    }
                }
                this.blkReader = this.reader.getBlockReader(blockIndex);
                this.currentLocation.set(blockIndex, 0L);
            }
            
            private void parkCursorAtEnd() throws IOException {
                this.klen = -1;
                this.currentLocation.set(this.endLocation);
                if (this.blkReader != null) {
                    try {
                        this.blkReader.close();
                    }
                    finally {
                        this.blkReader = null;
                    }
                }
            }
            
            @Override
            public void close() throws IOException {
                this.parkCursorAtEnd();
            }
            
            public boolean atEnd() {
                return this.currentLocation.compareTo(this.endLocation) >= 0;
            }
            
            void checkKey() throws IOException {
                if (this.klen >= 0) {
                    return;
                }
                if (this.atEnd()) {
                    throw new EOFException("No key-value to read");
                }
                this.klen = -1;
                this.vlen = -1;
                this.valueChecked = false;
                this.klen = Utils.readVInt(this.blkReader);
                this.blkReader.readFully(this.keyBuffer, 0, this.klen);
                this.valueBufferInputStream.reset(this.blkReader);
                if (this.valueBufferInputStream.isLastChunk()) {
                    this.vlen = this.valueBufferInputStream.getRemain();
                }
            }
            
            public Entry entry() throws IOException {
                this.checkKey();
                return new Entry();
            }
            
            public long getRecordNum() throws IOException {
                return this.reader.getRecordNumByLocation(this.currentLocation);
            }
            
            int compareCursorKeyTo(final RawComparable other) throws IOException {
                this.checkKey();
                return this.reader.compareKeys(this.keyBuffer, 0, this.klen, other.buffer(), other.offset(), other.size());
            }
            
            private void inBlockAdvance(final long n) throws IOException {
                for (long i = 0L; i < n; ++i) {
                    this.checkKey();
                    if (!this.valueBufferInputStream.isClosed()) {
                        this.valueBufferInputStream.close();
                    }
                    this.klen = -1;
                    this.currentLocation.incRecordIndex();
                }
            }
            
            private boolean inBlockAdvance(final RawComparable key, final boolean greater) throws IOException {
                final int curBid = this.currentLocation.getBlockIndex();
                long entryInBlock = this.reader.getBlockEntryCount(curBid);
                if (curBid == this.endLocation.getBlockIndex()) {
                    entryInBlock = this.endLocation.getRecordIndex();
                }
                while (this.currentLocation.getRecordIndex() < entryInBlock) {
                    final int cmp = this.compareCursorKeyTo(key);
                    if (cmp > 0) {
                        return false;
                    }
                    if (cmp == 0 && !greater) {
                        return true;
                    }
                    if (!this.valueBufferInputStream.isClosed()) {
                        this.valueBufferInputStream.close();
                    }
                    this.klen = -1;
                    this.currentLocation.incRecordIndex();
                }
                throw new RuntimeException("Cannot find matching key in block.");
            }
            
            public class Entry implements Comparable<RawComparable>
            {
                public int getKeyLength() {
                    return Scanner.this.klen;
                }
                
                byte[] getKeyBuffer() {
                    return Scanner.this.keyBuffer;
                }
                
                public void get(final BytesWritable key, final BytesWritable value) throws IOException {
                    this.getKey(key);
                    this.getValue(value);
                }
                
                public int getKey(final BytesWritable key) throws IOException {
                    key.setSize(this.getKeyLength());
                    this.getKey(key.getBytes());
                    return key.getLength();
                }
                
                public long getValue(final BytesWritable value) throws IOException {
                    final DataInputStream dis = this.getValueStream();
                    int size = 0;
                    try {
                        int remain;
                        while ((remain = Scanner.this.valueBufferInputStream.getRemain()) > 0) {
                            value.setSize(size + remain);
                            dis.readFully(value.getBytes(), size, remain);
                            size += remain;
                        }
                        return value.getLength();
                    }
                    finally {
                        dis.close();
                    }
                }
                
                public int writeKey(final OutputStream out) throws IOException {
                    out.write(Scanner.this.keyBuffer, 0, Scanner.this.klen);
                    return Scanner.this.klen;
                }
                
                public long writeValue(final OutputStream out) throws IOException {
                    final DataInputStream dis = this.getValueStream();
                    long size = 0L;
                    try {
                        int chunkSize;
                        while ((chunkSize = Scanner.this.valueBufferInputStream.getRemain()) > 0) {
                            chunkSize = Math.min(chunkSize, 131072);
                            Scanner.this.valTransferBuffer.setSize(chunkSize);
                            dis.readFully(Scanner.this.valTransferBuffer.getBytes(), 0, chunkSize);
                            out.write(Scanner.this.valTransferBuffer.getBytes(), 0, chunkSize);
                            size += chunkSize;
                        }
                        return size;
                    }
                    finally {
                        dis.close();
                    }
                }
                
                public int getKey(final byte[] buf) throws IOException {
                    return this.getKey(buf, 0);
                }
                
                public int getKey(final byte[] buf, final int offset) throws IOException {
                    if ((offset | buf.length - offset - Scanner.this.klen) < 0) {
                        throw new IndexOutOfBoundsException("Buffer not enough to store the key");
                    }
                    System.arraycopy(Scanner.this.keyBuffer, 0, buf, offset, Scanner.this.klen);
                    return Scanner.this.klen;
                }
                
                public DataInputStream getKeyStream() {
                    Scanner.this.keyDataInputStream.reset(Scanner.this.keyBuffer, Scanner.this.klen);
                    return Scanner.this.keyDataInputStream;
                }
                
                public int getValueLength() {
                    if (Scanner.this.vlen >= 0) {
                        return Scanner.this.vlen;
                    }
                    throw new RuntimeException("Value length unknown.");
                }
                
                public int getValue(final byte[] buf) throws IOException {
                    return this.getValue(buf, 0);
                }
                
                public int getValue(final byte[] buf, final int offset) throws IOException {
                    final DataInputStream dis = this.getValueStream();
                    try {
                        if (this.isValueLengthKnown()) {
                            if ((offset | buf.length - offset - Scanner.this.vlen) < 0) {
                                throw new IndexOutOfBoundsException("Buffer too small to hold value");
                            }
                            dis.readFully(buf, offset, Scanner.this.vlen);
                            return Scanner.this.vlen;
                        }
                        else {
                            int nextOffset;
                            int n;
                            for (nextOffset = offset; nextOffset < buf.length; nextOffset += n) {
                                n = dis.read(buf, nextOffset, buf.length - nextOffset);
                                if (n < 0) {
                                    break;
                                }
                            }
                            if (dis.read() >= 0) {
                                throw new IndexOutOfBoundsException("Buffer too small to hold value");
                            }
                            return nextOffset - offset;
                        }
                    }
                    finally {
                        dis.close();
                    }
                }
                
                public DataInputStream getValueStream() throws IOException {
                    if (Scanner.this.valueChecked) {
                        throw new IllegalStateException("Attempt to examine value multiple times.");
                    }
                    Scanner.this.valueChecked = true;
                    return Scanner.this.valueDataInputStream;
                }
                
                public boolean isValueLengthKnown() {
                    return Scanner.this.vlen >= 0;
                }
                
                public int compareTo(final byte[] buf) {
                    return this.compareTo(buf, 0, buf.length);
                }
                
                public int compareTo(final byte[] buf, final int offset, final int length) {
                    return this.compareTo((RawComparable)new ByteArray(buf, offset, length));
                }
                
                @Override
                public int compareTo(final RawComparable key) {
                    return Scanner.this.reader.compareKeys(Scanner.this.keyBuffer, 0, this.getKeyLength(), key.buffer(), key.offset(), key.size());
                }
                
                @Override
                public boolean equals(final Object other) {
                    return this == other || (other instanceof Entry && ((Entry)other).compareTo(Scanner.this.keyBuffer, 0, this.getKeyLength()) == 0);
                }
                
                @Override
                public int hashCode() {
                    return WritableComparator.hashBytes(Scanner.this.keyBuffer, 0, this.getKeyLength());
                }
            }
        }
    }
    
    static final class TFileMeta
    {
        static final String BLOCK_NAME = "TFile.meta";
        final Utils.Version version;
        private long recordCount;
        private final String strComparator;
        private final CompareUtils.BytesComparator comparator;
        
        public TFileMeta(final String comparator) {
            this.version = TFile.API_VERSION;
            this.recordCount = 0L;
            this.strComparator = ((comparator == null) ? "" : comparator);
            this.comparator = makeComparator(this.strComparator);
        }
        
        public TFileMeta(final DataInput in) throws IOException {
            this.version = new Utils.Version(in);
            if (!this.version.compatibleWith(TFile.API_VERSION)) {
                throw new RuntimeException("Incompatible TFile fileVersion.");
            }
            this.recordCount = Utils.readVLong(in);
            this.strComparator = Utils.readString(in);
            this.comparator = makeComparator(this.strComparator);
        }
        
        static CompareUtils.BytesComparator makeComparator(final String comparator) {
            if (comparator.length() == 0) {
                return null;
            }
            if (comparator.equals("memcmp")) {
                return new CompareUtils.BytesComparator(new CompareUtils.MemcmpRawComparator());
            }
            if (comparator.startsWith("jclass:")) {
                final String compClassName = comparator.substring("jclass:".length()).trim();
                try {
                    final Class compClass = Class.forName(compClassName);
                    return new CompareUtils.BytesComparator(compClass.newInstance());
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Failed to instantiate comparator: " + comparator + "(" + e.toString() + ")");
                }
            }
            throw new IllegalArgumentException("Unsupported comparator: " + comparator);
        }
        
        public void write(final DataOutput out) throws IOException {
            TFile.API_VERSION.write(out);
            Utils.writeVLong(out, this.recordCount);
            Utils.writeString(out, this.strComparator);
        }
        
        public long getRecordCount() {
            return this.recordCount;
        }
        
        public void incRecordCount() {
            ++this.recordCount;
        }
        
        public boolean isSorted() {
            return !this.strComparator.isEmpty();
        }
        
        public String getComparatorString() {
            return this.strComparator;
        }
        
        public CompareUtils.BytesComparator getComparator() {
            return this.comparator;
        }
        
        public Utils.Version getVersion() {
            return this.version;
        }
    }
    
    static class TFileIndex
    {
        static final String BLOCK_NAME = "TFile.index";
        private ByteArray firstKey;
        private final ArrayList<TFileIndexEntry> index;
        private final ArrayList<Long> recordNumIndex;
        private final CompareUtils.BytesComparator comparator;
        private long sum;
        
        public TFileIndex(final int entryCount, final DataInput in, final CompareUtils.BytesComparator comparator) throws IOException {
            this.sum = 0L;
            this.index = new ArrayList<TFileIndexEntry>(entryCount);
            this.recordNumIndex = new ArrayList<Long>(entryCount);
            int size = Utils.readVInt(in);
            if (size > 0) {
                byte[] buffer = new byte[size];
                in.readFully(buffer);
                final DataInputStream firstKeyInputStream = new DataInputStream(new ByteArrayInputStream(buffer, 0, size));
                final int firstKeyLength = Utils.readVInt(firstKeyInputStream);
                this.firstKey = new ByteArray(new byte[firstKeyLength]);
                firstKeyInputStream.readFully(this.firstKey.buffer());
                for (int i = 0; i < entryCount; ++i) {
                    size = Utils.readVInt(in);
                    if (buffer.length < size) {
                        buffer = new byte[size];
                    }
                    in.readFully(buffer, 0, size);
                    final TFileIndexEntry idx = new TFileIndexEntry(new DataInputStream(new ByteArrayInputStream(buffer, 0, size)));
                    this.index.add(idx);
                    this.sum += idx.entries();
                    this.recordNumIndex.add(this.sum);
                }
            }
            else if (entryCount != 0) {
                throw new RuntimeException("Internal error");
            }
            this.comparator = comparator;
        }
        
        public int lowerBound(final RawComparable key) {
            if (this.comparator == null) {
                throw new RuntimeException("Cannot search in unsorted TFile");
            }
            if (this.firstKey == null) {
                return -1;
            }
            final int ret = Utils.lowerBound(this.index, key, this.comparator);
            if (ret == this.index.size()) {
                return -1;
            }
            return ret;
        }
        
        public int upperBound(final RawComparable key) {
            if (this.comparator == null) {
                throw new RuntimeException("Cannot search in unsorted TFile");
            }
            if (this.firstKey == null) {
                return -1;
            }
            final int ret = Utils.upperBound(this.index, key, this.comparator);
            if (ret == this.index.size()) {
                return -1;
            }
            return ret;
        }
        
        public TFileIndex(final CompareUtils.BytesComparator comparator) {
            this.sum = 0L;
            this.index = new ArrayList<TFileIndexEntry>();
            this.recordNumIndex = new ArrayList<Long>();
            this.comparator = comparator;
        }
        
        public RawComparable getFirstKey() {
            return this.firstKey;
        }
        
        public Reader.Location getLocationByRecordNum(final long recNum) {
            final int idx = Utils.upperBound(this.recordNumIndex, recNum);
            final long lastRecNum = (idx == 0) ? 0L : this.recordNumIndex.get(idx - 1);
            return new Reader.Location(idx, recNum - lastRecNum);
        }
        
        public long getRecordNumByLocation(final Reader.Location location) {
            final int blkIndex = location.getBlockIndex();
            final long lastRecNum = (blkIndex == 0) ? 0L : this.recordNumIndex.get(blkIndex - 1);
            return lastRecNum + location.getRecordIndex();
        }
        
        public void setFirstKey(final byte[] key, final int offset, final int length) {
            this.firstKey = new ByteArray(new byte[length]);
            System.arraycopy(key, offset, this.firstKey.buffer(), 0, length);
        }
        
        public RawComparable getLastKey() {
            if (this.index.size() == 0) {
                return null;
            }
            return new ByteArray(this.index.get(this.index.size() - 1).buffer());
        }
        
        public void addEntry(final TFileIndexEntry keyEntry) {
            this.index.add(keyEntry);
            this.sum += keyEntry.entries();
            this.recordNumIndex.add(this.sum);
        }
        
        public TFileIndexEntry getEntry(final int bid) {
            return this.index.get(bid);
        }
        
        public void write(final DataOutput out) throws IOException {
            if (this.firstKey == null) {
                Utils.writeVInt(out, 0);
                return;
            }
            final DataOutputBuffer dob = new DataOutputBuffer();
            Utils.writeVInt(dob, this.firstKey.size());
            dob.write(this.firstKey.buffer());
            Utils.writeVInt(out, dob.size());
            out.write(dob.getData(), 0, dob.getLength());
            for (final TFileIndexEntry entry : this.index) {
                dob.reset();
                entry.write(dob);
                Utils.writeVInt(out, dob.getLength());
                out.write(dob.getData(), 0, dob.getLength());
            }
        }
    }
    
    static final class TFileIndexEntry implements RawComparable
    {
        final byte[] key;
        final long kvEntries;
        
        public TFileIndexEntry(final DataInput in) throws IOException {
            final int len = Utils.readVInt(in);
            in.readFully(this.key = new byte[len], 0, len);
            this.kvEntries = Utils.readVLong(in);
        }
        
        public TFileIndexEntry(final byte[] newkey, final int offset, final int len, final long entries) {
            System.arraycopy(newkey, offset, this.key = new byte[len], 0, len);
            this.kvEntries = entries;
        }
        
        @Override
        public byte[] buffer() {
            return this.key;
        }
        
        @Override
        public int offset() {
            return 0;
        }
        
        @Override
        public int size() {
            return this.key.length;
        }
        
        long entries() {
            return this.kvEntries;
        }
        
        public void write(final DataOutput out) throws IOException {
            Utils.writeVInt(out, this.key.length);
            out.write(this.key, 0, this.key.length);
            Utils.writeVLong(out, this.kvEntries);
        }
    }
}
