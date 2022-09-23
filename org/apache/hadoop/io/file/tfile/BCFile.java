// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import java.io.DataInputStream;
import java.io.InputStream;
import org.apache.hadoop.io.compress.Decompressor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.DataInput;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.Compressor;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.Closeable;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

final class BCFile
{
    static final Utils.Version API_VERSION;
    static final Logger LOG;
    
    private BCFile() {
    }
    
    static {
        API_VERSION = new Utils.Version((short)1, (short)0);
        LOG = LoggerFactory.getLogger(BCFile.class);
    }
    
    public static class Writer implements Closeable
    {
        private final FSDataOutputStream out;
        private final Configuration conf;
        final DataIndex dataIndex;
        final MetaIndex metaIndex;
        boolean blkInProgress;
        private boolean metaBlkSeen;
        private boolean closed;
        long errorCount;
        private BytesWritable fsOutputBuffer;
        
        public Writer(final FSDataOutputStream fout, final String compressionName, final Configuration conf) throws IOException {
            this.blkInProgress = false;
            this.metaBlkSeen = false;
            this.closed = false;
            this.errorCount = 0L;
            if (fout.getPos() != 0L) {
                throw new IOException("Output file not at zero offset.");
            }
            this.out = fout;
            this.conf = conf;
            this.dataIndex = new DataIndex(compressionName);
            this.metaIndex = new MetaIndex();
            this.fsOutputBuffer = new BytesWritable();
            Magic.write(fout);
        }
        
        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            try {
                if (this.errorCount == 0L) {
                    if (this.blkInProgress) {
                        throw new IllegalStateException("Close() called with active block appender.");
                    }
                    final BlockAppender appender = this.prepareMetaBlock("BCFile.index", this.getDefaultCompressionAlgorithm());
                    try {
                        this.dataIndex.write(appender);
                    }
                    finally {
                        appender.close();
                    }
                    final long offsetIndexMeta = this.out.getPos();
                    this.metaIndex.write(this.out);
                    this.out.writeLong(offsetIndexMeta);
                    BCFile.API_VERSION.write(this.out);
                    Magic.write(this.out);
                    this.out.flush();
                }
            }
            finally {
                this.closed = true;
            }
        }
        
        private Compression.Algorithm getDefaultCompressionAlgorithm() {
            return this.dataIndex.getDefaultCompressionAlgorithm();
        }
        
        private BlockAppender prepareMetaBlock(final String name, final Compression.Algorithm compressAlgo) throws IOException, MetaBlockAlreadyExists {
            if (this.blkInProgress) {
                throw new IllegalStateException("Cannot create Meta Block until previous block is closed.");
            }
            if (this.metaIndex.getMetaByName(name) != null) {
                throw new MetaBlockAlreadyExists("name=" + name);
            }
            final MetaBlockRegister mbr = new MetaBlockRegister(name, compressAlgo);
            final WBlockState wbs = new WBlockState(compressAlgo, this.out, this.fsOutputBuffer, this.conf);
            final BlockAppender ba = new BlockAppender(mbr, wbs);
            this.blkInProgress = true;
            this.metaBlkSeen = true;
            return ba;
        }
        
        public BlockAppender prepareMetaBlock(final String name, final String compressionName) throws IOException, MetaBlockAlreadyExists {
            return this.prepareMetaBlock(name, Compression.getCompressionAlgorithmByName(compressionName));
        }
        
        public BlockAppender prepareMetaBlock(final String name) throws IOException, MetaBlockAlreadyExists {
            return this.prepareMetaBlock(name, this.getDefaultCompressionAlgorithm());
        }
        
        public BlockAppender prepareDataBlock() throws IOException {
            if (this.blkInProgress) {
                throw new IllegalStateException("Cannot create Data Block until previous block is closed.");
            }
            if (this.metaBlkSeen) {
                throw new IllegalStateException("Cannot create Data Block after Meta Blocks.");
            }
            final DataBlockRegister dbr = new DataBlockRegister();
            final WBlockState wbs = new WBlockState(this.getDefaultCompressionAlgorithm(), this.out, this.fsOutputBuffer, this.conf);
            final BlockAppender ba = new BlockAppender(dbr, wbs);
            this.blkInProgress = true;
            return ba;
        }
        
        private static final class WBlockState
        {
            private final Compression.Algorithm compressAlgo;
            private Compressor compressor;
            private final FSDataOutputStream fsOut;
            private final long posStart;
            private final SimpleBufferedOutputStream fsBufferedOutput;
            private OutputStream out;
            
            public WBlockState(final Compression.Algorithm compressionAlgo, final FSDataOutputStream fsOut, final BytesWritable fsOutputBuffer, final Configuration conf) throws IOException {
                this.compressAlgo = compressionAlgo;
                this.fsOut = fsOut;
                this.posStart = fsOut.getPos();
                fsOutputBuffer.setCapacity(TFile.getFSOutputBufferSize(conf));
                this.fsBufferedOutput = new SimpleBufferedOutputStream(this.fsOut, fsOutputBuffer.getBytes());
                this.compressor = this.compressAlgo.getCompressor();
                try {
                    this.out = compressionAlgo.createCompressionStream(this.fsBufferedOutput, this.compressor, 0);
                }
                catch (IOException e) {
                    this.compressAlgo.returnCompressor(this.compressor);
                    throw e;
                }
            }
            
            OutputStream getOutputStream() {
                return this.out;
            }
            
            long getCurrentPos() throws IOException {
                return this.fsOut.getPos() + this.fsBufferedOutput.size();
            }
            
            long getStartPos() {
                return this.posStart;
            }
            
            long getCompressedSize() throws IOException {
                final long ret = this.getCurrentPos() - this.posStart;
                return ret;
            }
            
            public void finish() throws IOException {
                try {
                    if (this.out != null) {
                        this.out.flush();
                        this.out = null;
                    }
                }
                finally {
                    this.compressAlgo.returnCompressor(this.compressor);
                    this.compressor = null;
                }
            }
        }
        
        public class BlockAppender extends DataOutputStream
        {
            private final BlockRegister blockRegister;
            private final WBlockState wBlkState;
            private boolean closed;
            
            BlockAppender(final BlockRegister register, final WBlockState wbs) {
                super(wbs.getOutputStream());
                this.closed = false;
                this.blockRegister = register;
                this.wBlkState = wbs;
            }
            
            public long getRawSize() throws IOException {
                return (long)this.size() & 0xFFFFFFFFL;
            }
            
            public long getCompressedSize() throws IOException {
                return this.wBlkState.getCompressedSize();
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
                    this.wBlkState.finish();
                    this.blockRegister.register(this.getRawSize(), this.wBlkState.getStartPos(), this.wBlkState.getCurrentPos());
                    final Writer this$2 = Writer.this;
                    --this$2.errorCount;
                }
                finally {
                    this.closed = true;
                    Writer.this.blkInProgress = false;
                }
            }
        }
        
        private class MetaBlockRegister implements BlockRegister
        {
            private final String name;
            private final Compression.Algorithm compressAlgo;
            
            MetaBlockRegister(final String name, final Compression.Algorithm compressAlgo) {
                this.name = name;
                this.compressAlgo = compressAlgo;
            }
            
            @Override
            public void register(final long raw, final long begin, final long end) {
                Writer.this.metaIndex.addEntry(new MetaIndexEntry(this.name, this.compressAlgo, new BlockRegion(begin, end - begin, raw)));
            }
        }
        
        private class DataBlockRegister implements BlockRegister
        {
            DataBlockRegister() {
            }
            
            @Override
            public void register(final long raw, final long begin, final long end) {
                Writer.this.dataIndex.addBlockRegion(new BlockRegion(begin, end - begin, raw));
            }
        }
        
        private interface BlockRegister
        {
            void register(final long p0, final long p1, final long p2);
        }
    }
    
    public static class Reader implements Closeable
    {
        private final FSDataInputStream in;
        private final Configuration conf;
        final DataIndex dataIndex;
        final MetaIndex metaIndex;
        final Utils.Version version;
        
        public Reader(final FSDataInputStream fin, final long fileLength, final Configuration conf) throws IOException {
            this.in = fin;
            this.conf = conf;
            fin.seek(fileLength - Magic.size() - Utils.Version.size() - 8L);
            final long offsetIndexMeta = fin.readLong();
            this.version = new Utils.Version(fin);
            Magic.readAndVerify(fin);
            if (!this.version.compatibleWith(BCFile.API_VERSION)) {
                throw new RuntimeException("Incompatible BCFile fileBCFileVersion.");
            }
            fin.seek(offsetIndexMeta);
            this.metaIndex = new MetaIndex(fin);
            final BlockReader blockR = this.getMetaBlock("BCFile.index");
            try {
                this.dataIndex = new DataIndex(blockR);
            }
            finally {
                blockR.close();
            }
        }
        
        public String getDefaultCompressionName() {
            return this.dataIndex.getDefaultCompressionAlgorithm().getName();
        }
        
        public Utils.Version getBCFileVersion() {
            return this.version;
        }
        
        public Utils.Version getAPIVersion() {
            return BCFile.API_VERSION;
        }
        
        @Override
        public void close() {
        }
        
        public int getBlockCount() {
            return this.dataIndex.getBlockRegionList().size();
        }
        
        public BlockReader getMetaBlock(final String name) throws IOException, MetaBlockDoesNotExist {
            final MetaIndexEntry imeBCIndex = this.metaIndex.getMetaByName(name);
            if (imeBCIndex == null) {
                throw new MetaBlockDoesNotExist("name=" + name);
            }
            final BlockRegion region = imeBCIndex.getRegion();
            return this.createReader(imeBCIndex.getCompressionAlgorithm(), region);
        }
        
        public BlockReader getDataBlock(final int blockIndex) throws IOException {
            if (blockIndex < 0 || blockIndex >= this.getBlockCount()) {
                throw new IndexOutOfBoundsException(String.format("blockIndex=%d, numBlocks=%d", blockIndex, this.getBlockCount()));
            }
            final BlockRegion region = this.dataIndex.getBlockRegionList().get(blockIndex);
            return this.createReader(this.dataIndex.getDefaultCompressionAlgorithm(), region);
        }
        
        private BlockReader createReader(final Compression.Algorithm compressAlgo, final BlockRegion region) throws IOException {
            final RBlockState rbs = new RBlockState(compressAlgo, this.in, region, this.conf);
            return new BlockReader(rbs);
        }
        
        public int getBlockIndexNear(final long offset) {
            final ArrayList<BlockRegion> list = this.dataIndex.getBlockRegionList();
            final int idx = Utils.lowerBound((List<? extends CompareUtils.ScalarLong>)list, new CompareUtils.ScalarLong(offset), new CompareUtils.ScalarComparator());
            if (idx == list.size()) {
                return -1;
            }
            return idx;
        }
        
        private static final class RBlockState
        {
            private final Compression.Algorithm compressAlgo;
            private Decompressor decompressor;
            private final BlockRegion region;
            private final InputStream in;
            
            public RBlockState(final Compression.Algorithm compressionAlgo, final FSDataInputStream fsin, final BlockRegion region, final Configuration conf) throws IOException {
                this.compressAlgo = compressionAlgo;
                this.region = region;
                this.decompressor = compressionAlgo.getDecompressor();
                try {
                    this.in = this.compressAlgo.createDecompressionStream(new BoundedRangeFileInputStream(fsin, this.region.getOffset(), this.region.getCompressedSize()), this.decompressor, TFile.getFSInputBufferSize(conf));
                }
                catch (IOException e) {
                    this.compressAlgo.returnDecompressor(this.decompressor);
                    throw e;
                }
            }
            
            public InputStream getInputStream() {
                return this.in;
            }
            
            public String getCompressionName() {
                return this.compressAlgo.getName();
            }
            
            public BlockRegion getBlockRegion() {
                return this.region;
            }
            
            public void finish() throws IOException {
                try {
                    this.in.close();
                }
                finally {
                    this.compressAlgo.returnDecompressor(this.decompressor);
                    this.decompressor = null;
                }
            }
        }
        
        public static class BlockReader extends DataInputStream
        {
            private final RBlockState rBlkState;
            private boolean closed;
            
            BlockReader(final RBlockState rbs) {
                super(rbs.getInputStream());
                this.closed = false;
                this.rBlkState = rbs;
            }
            
            @Override
            public void close() throws IOException {
                if (this.closed) {
                    return;
                }
                try {
                    this.rBlkState.finish();
                }
                finally {
                    this.closed = true;
                }
            }
            
            public String getCompressionName() {
                return this.rBlkState.getCompressionName();
            }
            
            public long getRawSize() {
                return this.rBlkState.getBlockRegion().getRawSize();
            }
            
            public long getCompressedSize() {
                return this.rBlkState.getBlockRegion().getCompressedSize();
            }
            
            public long getStartPos() {
                return this.rBlkState.getBlockRegion().getOffset();
            }
        }
    }
    
    static class MetaIndex
    {
        final Map<String, MetaIndexEntry> index;
        
        public MetaIndex() {
            this.index = new TreeMap<String, MetaIndexEntry>();
        }
        
        public MetaIndex(final DataInput in) throws IOException {
            final int count = Utils.readVInt(in);
            this.index = new TreeMap<String, MetaIndexEntry>();
            for (int nx = 0; nx < count; ++nx) {
                final MetaIndexEntry indexEntry = new MetaIndexEntry(in);
                this.index.put(indexEntry.getMetaName(), indexEntry);
            }
        }
        
        public void addEntry(final MetaIndexEntry indexEntry) {
            this.index.put(indexEntry.getMetaName(), indexEntry);
        }
        
        public MetaIndexEntry getMetaByName(final String name) {
            return this.index.get(name);
        }
        
        public void write(final DataOutput out) throws IOException {
            Utils.writeVInt(out, this.index.size());
            for (final MetaIndexEntry indexEntry : this.index.values()) {
                indexEntry.write(out);
            }
        }
    }
    
    static final class MetaIndexEntry
    {
        private final String metaName;
        private final Compression.Algorithm compressionAlgorithm;
        private static final String defaultPrefix = "data:";
        private final BlockRegion region;
        
        public MetaIndexEntry(final DataInput in) throws IOException {
            final String fullMetaName = Utils.readString(in);
            if (fullMetaName.startsWith("data:")) {
                this.metaName = fullMetaName.substring("data:".length(), fullMetaName.length());
                this.compressionAlgorithm = Compression.getCompressionAlgorithmByName(Utils.readString(in));
                this.region = new BlockRegion(in);
                return;
            }
            throw new IOException("Corrupted Meta region Index");
        }
        
        public MetaIndexEntry(final String metaName, final Compression.Algorithm compressionAlgorithm, final BlockRegion region) {
            this.metaName = metaName;
            this.compressionAlgorithm = compressionAlgorithm;
            this.region = region;
        }
        
        public String getMetaName() {
            return this.metaName;
        }
        
        public Compression.Algorithm getCompressionAlgorithm() {
            return this.compressionAlgorithm;
        }
        
        public BlockRegion getRegion() {
            return this.region;
        }
        
        public void write(final DataOutput out) throws IOException {
            Utils.writeString(out, "data:" + this.metaName);
            Utils.writeString(out, this.compressionAlgorithm.getName());
            this.region.write(out);
        }
    }
    
    static class DataIndex
    {
        static final String BLOCK_NAME = "BCFile.index";
        private final Compression.Algorithm defaultCompressionAlgorithm;
        private final ArrayList<BlockRegion> listRegions;
        
        public DataIndex(final DataInput in) throws IOException {
            this.defaultCompressionAlgorithm = Compression.getCompressionAlgorithmByName(Utils.readString(in));
            final int n = Utils.readVInt(in);
            this.listRegions = new ArrayList<BlockRegion>(n);
            for (int i = 0; i < n; ++i) {
                final BlockRegion region = new BlockRegion(in);
                this.listRegions.add(region);
            }
        }
        
        public DataIndex(final String defaultCompressionAlgorithmName) {
            this.defaultCompressionAlgorithm = Compression.getCompressionAlgorithmByName(defaultCompressionAlgorithmName);
            this.listRegions = new ArrayList<BlockRegion>();
        }
        
        public Compression.Algorithm getDefaultCompressionAlgorithm() {
            return this.defaultCompressionAlgorithm;
        }
        
        public ArrayList<BlockRegion> getBlockRegionList() {
            return this.listRegions;
        }
        
        public void addBlockRegion(final BlockRegion region) {
            this.listRegions.add(region);
        }
        
        public void write(final DataOutput out) throws IOException {
            Utils.writeString(out, this.defaultCompressionAlgorithm.getName());
            Utils.writeVInt(out, this.listRegions.size());
            for (final BlockRegion region : this.listRegions) {
                region.write(out);
            }
        }
    }
    
    static final class Magic
    {
        private static final byte[] AB_MAGIC_BCFILE;
        
        public static void readAndVerify(final DataInput in) throws IOException {
            final byte[] abMagic = new byte[size()];
            in.readFully(abMagic);
            if (!Arrays.equals(abMagic, Magic.AB_MAGIC_BCFILE)) {
                throw new IOException("Not a valid BCFile.");
            }
        }
        
        public static void write(final DataOutput out) throws IOException {
            out.write(Magic.AB_MAGIC_BCFILE);
        }
        
        public static int size() {
            return Magic.AB_MAGIC_BCFILE.length;
        }
        
        static {
            AB_MAGIC_BCFILE = new byte[] { -47, 17, -45, 104, -111, -75, -41, -74, 57, -33, 65, 64, -110, -70, -31, 80 };
        }
    }
    
    static final class BlockRegion implements CompareUtils.Scalar
    {
        private final long offset;
        private final long compressedSize;
        private final long rawSize;
        
        public BlockRegion(final DataInput in) throws IOException {
            this.offset = Utils.readVLong(in);
            this.compressedSize = Utils.readVLong(in);
            this.rawSize = Utils.readVLong(in);
        }
        
        public BlockRegion(final long offset, final long compressedSize, final long rawSize) {
            this.offset = offset;
            this.compressedSize = compressedSize;
            this.rawSize = rawSize;
        }
        
        public void write(final DataOutput out) throws IOException {
            Utils.writeVLong(out, this.offset);
            Utils.writeVLong(out, this.compressedSize);
            Utils.writeVLong(out, this.rawSize);
        }
        
        public long getOffset() {
            return this.offset;
        }
        
        public long getCompressedSize() {
            return this.compressedSize;
        }
        
        public long getRawSize() {
            return this.rawSize;
        }
        
        @Override
        public long magnitude() {
            return this.offset;
        }
    }
}
