// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.schema.MessageType;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.schema.MessageTypeParser;
import java.util.Arrays;
import java.util.Comparator;
import parquet.io.ParquetDecodingException;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.fs.BlockLocation;
import parquet.hadoop.metadata.BlockMetaData;
import parquet.hadoop.metadata.ParquetMetadata;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.util.Iterator;
import parquet.filter2.compat.FilterCompat;
import java.util.Collection;
import parquet.filter2.compat.RowGroupFilter;
import java.util.ArrayList;
import parquet.hadoop.api.ReadSupport;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import parquet.Log;

class ClientSideMetadataSplitStrategy
{
    private static final Log LOG;
    
    List<ParquetInputSplit> getSplits(final Configuration configuration, final List<Footer> footers, final long maxSplitSize, final long minSplitSize, final ReadSupport.ReadContext readContext) throws IOException {
        final List<ParquetInputSplit> splits = new ArrayList<ParquetInputSplit>();
        final FilterCompat.Filter filter = ParquetInputFormat.getFilter(configuration);
        long rowGroupsDropped = 0L;
        long totalRowGroups = 0L;
        for (final Footer footer : footers) {
            final Path file = footer.getFile();
            ClientSideMetadataSplitStrategy.LOG.debug(file);
            final FileSystem fs = file.getFileSystem(configuration);
            final FileStatus fileStatus = fs.getFileStatus(file);
            final ParquetMetadata parquetMetaData = footer.getParquetMetadata();
            final List<BlockMetaData> blocks = parquetMetaData.getBlocks();
            totalRowGroups += blocks.size();
            final List<BlockMetaData> filteredBlocks = RowGroupFilter.filterRowGroups(filter, blocks, parquetMetaData.getFileMetaData().getSchema());
            rowGroupsDropped += blocks.size() - filteredBlocks.size();
            if (filteredBlocks.isEmpty()) {
                continue;
            }
            final BlockLocation[] fileBlockLocations = fs.getFileBlockLocations(fileStatus, 0L, fileStatus.getLen());
            splits.addAll(generateSplits(filteredBlocks, fileBlockLocations, fileStatus, readContext.getRequestedSchema().toString(), readContext.getReadSupportMetadata(), minSplitSize, maxSplitSize));
        }
        if (rowGroupsDropped > 0L && totalRowGroups > 0L) {
            final int percentDropped = (int)(rowGroupsDropped / (double)totalRowGroups * 100.0);
            ClientSideMetadataSplitStrategy.LOG.info("Dropping " + rowGroupsDropped + " row groups that do not pass filter predicate! (" + percentDropped + "%)");
        }
        else {
            ClientSideMetadataSplitStrategy.LOG.info("There were no row groups that could be dropped due to filter predicates");
        }
        return splits;
    }
    
    static <T> List<ParquetInputSplit> generateSplits(final List<BlockMetaData> rowGroupBlocks, final BlockLocation[] hdfsBlocksArray, final FileStatus fileStatus, final String requestedSchema, final Map<String, String> readSupportMetadata, final long minSplitSize, final long maxSplitSize) throws IOException {
        final List<SplitInfo> splitRowGroups = generateSplitInfo(rowGroupBlocks, hdfsBlocksArray, minSplitSize, maxSplitSize);
        final List<ParquetInputSplit> resultSplits = new ArrayList<ParquetInputSplit>();
        for (final SplitInfo splitInfo : splitRowGroups) {
            final ParquetInputSplit split = splitInfo.getParquetInputSplit(fileStatus, requestedSchema, readSupportMetadata);
            resultSplits.add(split);
        }
        return resultSplits;
    }
    
    static List<SplitInfo> generateSplitInfo(final List<BlockMetaData> rowGroupBlocks, final BlockLocation[] hdfsBlocksArray, final long minSplitSize, final long maxSplitSize) {
        if (maxSplitSize < minSplitSize || maxSplitSize < 0L || minSplitSize < 0L) {
            throw new ParquetDecodingException("maxSplitSize and minSplitSize should be positive and max should be greater or equal to the minSplitSize: maxSplitSize = " + maxSplitSize + "; minSplitSize is " + minSplitSize);
        }
        final HDFSBlocks hdfsBlocks = new HDFSBlocks(hdfsBlocksArray);
        hdfsBlocks.checkBelongingToANewHDFSBlock(rowGroupBlocks.get(0));
        SplitInfo currentSplit = new SplitInfo(hdfsBlocks.getCurrentBlock());
        final List<SplitInfo> splitRowGroups = new ArrayList<SplitInfo>();
        checkSorted(rowGroupBlocks);
        for (final BlockMetaData rowGroupMetadata : rowGroupBlocks) {
            if ((hdfsBlocks.checkBelongingToANewHDFSBlock(rowGroupMetadata) && currentSplit.getCompressedByteSize() >= minSplitSize && currentSplit.getCompressedByteSize() > 0L) || currentSplit.getCompressedByteSize() >= maxSplitSize) {
                splitRowGroups.add(currentSplit);
                currentSplit = new SplitInfo(hdfsBlocks.getCurrentBlock());
            }
            currentSplit.addRowGroup(rowGroupMetadata);
        }
        if (currentSplit.getRowGroupCount() > 0) {
            splitRowGroups.add(currentSplit);
        }
        return splitRowGroups;
    }
    
    private static void checkSorted(final List<BlockMetaData> rowGroupBlocks) {
        final long previousOffset = 0L;
        for (final BlockMetaData rowGroup : rowGroupBlocks) {
            final long currentOffset = rowGroup.getStartingPos();
            if (currentOffset < previousOffset) {
                throw new ParquetDecodingException("row groups are not sorted: previous row groups starts at " + previousOffset + ", current row group starts at " + currentOffset);
            }
        }
    }
    
    static {
        LOG = Log.getLog(ClientSideMetadataSplitStrategy.class);
    }
    
    private static class HDFSBlocks
    {
        BlockLocation[] hdfsBlocks;
        int currentStartHdfsBlockIndex;
        int currentMidPointHDFSBlockIndex;
        
        private HDFSBlocks(final BlockLocation[] hdfsBlocks) {
            this.currentStartHdfsBlockIndex = 0;
            this.currentMidPointHDFSBlockIndex = 0;
            this.hdfsBlocks = hdfsBlocks;
            final Comparator<BlockLocation> comparator = new Comparator<BlockLocation>() {
                @Override
                public int compare(final BlockLocation b1, final BlockLocation b2) {
                    return Long.signum(b1.getOffset() - b2.getOffset());
                }
            };
            Arrays.sort(hdfsBlocks, comparator);
        }
        
        private long getHDFSBlockEndingPosition(final int hdfsBlockIndex) {
            final BlockLocation hdfsBlock = this.hdfsBlocks[hdfsBlockIndex];
            return hdfsBlock.getOffset() + hdfsBlock.getLength() - 1L;
        }
        
        private boolean checkBelongingToANewHDFSBlock(final BlockMetaData rowGroupMetadata) {
            boolean isNewHdfsBlock = false;
            final long rowGroupMidPoint = rowGroupMetadata.getStartingPos() + rowGroupMetadata.getCompressedSize() / 2L;
            while (rowGroupMidPoint > this.getHDFSBlockEndingPosition(this.currentMidPointHDFSBlockIndex)) {
                isNewHdfsBlock = true;
                ++this.currentMidPointHDFSBlockIndex;
                if (this.currentMidPointHDFSBlockIndex >= this.hdfsBlocks.length) {
                    throw new ParquetDecodingException("the row group is not in hdfs blocks in the file: midpoint of row groups is " + rowGroupMidPoint + ", the end of the hdfs block is " + this.getHDFSBlockEndingPosition(this.currentMidPointHDFSBlockIndex - 1));
                }
            }
            while (rowGroupMetadata.getStartingPos() > this.getHDFSBlockEndingPosition(this.currentStartHdfsBlockIndex)) {
                ++this.currentStartHdfsBlockIndex;
                if (this.currentStartHdfsBlockIndex >= this.hdfsBlocks.length) {
                    throw new ParquetDecodingException("The row group does not start in this file: row group offset is " + rowGroupMetadata.getStartingPos() + " but the end of hdfs blocks of file is " + this.getHDFSBlockEndingPosition(this.currentStartHdfsBlockIndex));
                }
            }
            return isNewHdfsBlock;
        }
        
        public BlockLocation getCurrentBlock() {
            return this.hdfsBlocks[this.currentStartHdfsBlockIndex];
        }
    }
    
    static class SplitInfo
    {
        List<BlockMetaData> rowGroups;
        BlockLocation hdfsBlock;
        long compressedByteSize;
        
        public SplitInfo(final BlockLocation currentBlock) {
            this.rowGroups = new ArrayList<BlockMetaData>();
            this.compressedByteSize = 0L;
            this.hdfsBlock = currentBlock;
        }
        
        private void addRowGroup(final BlockMetaData rowGroup) {
            this.rowGroups.add(rowGroup);
            this.compressedByteSize += rowGroup.getCompressedSize();
        }
        
        public long getCompressedByteSize() {
            return this.compressedByteSize;
        }
        
        public List<BlockMetaData> getRowGroups() {
            return this.rowGroups;
        }
        
        int getRowGroupCount() {
            return this.rowGroups.size();
        }
        
        public ParquetInputSplit getParquetInputSplit(final FileStatus fileStatus, final String requestedSchema, final Map<String, String> readSupportMetadata) throws IOException {
            final MessageType requested = MessageTypeParser.parseMessageType(requestedSchema);
            long length = 0L;
            for (final BlockMetaData block : this.getRowGroups()) {
                final List<ColumnChunkMetaData> columns = block.getColumns();
                for (final ColumnChunkMetaData column : columns) {
                    if (requested.containsPath(column.getPath().toArray())) {
                        length += column.getTotalSize();
                    }
                }
            }
            final BlockMetaData lastRowGroup = this.getRowGroups().get(this.getRowGroupCount() - 1);
            final long end = lastRowGroup.getStartingPos() + lastRowGroup.getTotalByteSize();
            final long[] rowGroupOffsets = new long[this.getRowGroupCount()];
            for (int i = 0; i < rowGroupOffsets.length; ++i) {
                rowGroupOffsets[i] = this.getRowGroups().get(i).getStartingPos();
            }
            return new ParquetInputSplit(fileStatus.getPath(), this.hdfsBlock.getOffset(), end, length, this.hdfsBlock.getHosts(), rowGroupOffsets);
        }
    }
}
