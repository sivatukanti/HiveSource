// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.Ints;
import parquet.column.page.DataPageV2;
import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.column.page.DataPageV1;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import parquet.column.page.DictionaryPage;
import parquet.column.page.DataPage;
import java.util.List;
import parquet.column.page.PageReader;
import java.util.HashMap;
import parquet.column.ColumnDescriptor;
import java.util.Map;
import parquet.Log;
import parquet.column.page.PageReadStore;

class ColumnChunkPageReadStore implements PageReadStore
{
    private static final Log LOG;
    private final Map<ColumnDescriptor, ColumnChunkPageReader> readers;
    private final long rowCount;
    
    public ColumnChunkPageReadStore(final long rowCount) {
        this.readers = new HashMap<ColumnDescriptor, ColumnChunkPageReader>();
        this.rowCount = rowCount;
    }
    
    @Override
    public long getRowCount() {
        return this.rowCount;
    }
    
    @Override
    public PageReader getPageReader(final ColumnDescriptor path) {
        if (!this.readers.containsKey(path)) {
            throw new IllegalArgumentException(path + " is not in the store: " + this.readers.keySet() + " " + this.rowCount);
        }
        return this.readers.get(path);
    }
    
    void addColumn(final ColumnDescriptor path, final ColumnChunkPageReader reader) {
        if (this.readers.put(path, reader) != null) {
            throw new RuntimeException(path + " was added twice");
        }
    }
    
    static {
        LOG = Log.getLog(ColumnChunkPageReadStore.class);
    }
    
    static final class ColumnChunkPageReader implements PageReader
    {
        private final CodecFactory.BytesDecompressor decompressor;
        private final long valueCount;
        private final List<DataPage> compressedPages;
        private final DictionaryPage compressedDictionaryPage;
        
        ColumnChunkPageReader(final CodecFactory.BytesDecompressor decompressor, final List<DataPage> compressedPages, final DictionaryPage compressedDictionaryPage) {
            this.decompressor = decompressor;
            this.compressedPages = new LinkedList<DataPage>(compressedPages);
            this.compressedDictionaryPage = compressedDictionaryPage;
            int count = 0;
            for (final DataPage p : compressedPages) {
                count += p.getValueCount();
            }
            this.valueCount = count;
        }
        
        @Override
        public long getTotalValueCount() {
            return this.valueCount;
        }
        
        @Override
        public DataPage readPage() {
            if (this.compressedPages.isEmpty()) {
                return null;
            }
            final DataPage compressedPage = this.compressedPages.remove(0);
            return compressedPage.accept((DataPage.Visitor<DataPage>)new DataPage.Visitor<DataPage>() {
                @Override
                public DataPage visit(final DataPageV1 dataPageV1) {
                    try {
                        return new DataPageV1(ColumnChunkPageReader.this.decompressor.decompress(dataPageV1.getBytes(), dataPageV1.getUncompressedSize()), dataPageV1.getValueCount(), dataPageV1.getUncompressedSize(), dataPageV1.getStatistics(), dataPageV1.getRlEncoding(), dataPageV1.getDlEncoding(), dataPageV1.getValueEncoding());
                    }
                    catch (IOException e) {
                        throw new ParquetDecodingException("could not decompress page", e);
                    }
                }
                
                @Override
                public DataPage visit(final DataPageV2 dataPageV2) {
                    if (!dataPageV2.isCompressed()) {
                        return dataPageV2;
                    }
                    try {
                        final int uncompressedSize = Ints.checkedCast(dataPageV2.getUncompressedSize() - dataPageV2.getDefinitionLevels().size() - dataPageV2.getRepetitionLevels().size());
                        return DataPageV2.uncompressed(dataPageV2.getRowCount(), dataPageV2.getNullCount(), dataPageV2.getValueCount(), dataPageV2.getRepetitionLevels(), dataPageV2.getDefinitionLevels(), dataPageV2.getDataEncoding(), ColumnChunkPageReader.this.decompressor.decompress(dataPageV2.getData(), uncompressedSize), dataPageV2.getStatistics());
                    }
                    catch (IOException e) {
                        throw new ParquetDecodingException("could not decompress page", e);
                    }
                }
            });
        }
        
        @Override
        public DictionaryPage readDictionaryPage() {
            if (this.compressedDictionaryPage == null) {
                return null;
            }
            try {
                return new DictionaryPage(this.decompressor.decompress(this.compressedDictionaryPage.getBytes(), this.compressedDictionaryPage.getUncompressedSize()), this.compressedDictionaryPage.getDictionarySize(), this.compressedDictionaryPage.getEncoding());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
