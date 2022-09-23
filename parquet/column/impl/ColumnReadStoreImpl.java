// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import parquet.schema.GroupType;
import parquet.io.api.Converter;
import parquet.schema.Type;
import parquet.io.api.PrimitiveConverter;
import parquet.column.page.PageReader;
import parquet.column.ColumnReader;
import parquet.column.ColumnDescriptor;
import parquet.schema.MessageType;
import parquet.io.api.GroupConverter;
import parquet.column.page.PageReadStore;
import parquet.column.ColumnReadStore;

public class ColumnReadStoreImpl implements ColumnReadStore
{
    private final PageReadStore pageReadStore;
    private final GroupConverter recordConverter;
    private final MessageType schema;
    
    public ColumnReadStoreImpl(final PageReadStore pageReadStore, final GroupConverter recordConverter, final MessageType schema) {
        this.pageReadStore = pageReadStore;
        this.recordConverter = recordConverter;
        this.schema = schema;
    }
    
    @Override
    public ColumnReader getColumnReader(final ColumnDescriptor path) {
        return this.newMemColumnReader(path, this.pageReadStore.getPageReader(path));
    }
    
    private ColumnReaderImpl newMemColumnReader(final ColumnDescriptor path, final PageReader pageReader) {
        final PrimitiveConverter converter = this.getPrimitiveConverter(path);
        return new ColumnReaderImpl(path, pageReader, converter);
    }
    
    private PrimitiveConverter getPrimitiveConverter(final ColumnDescriptor path) {
        Type currentType = this.schema;
        Converter currentConverter = this.recordConverter;
        for (final String fieldName : path.getPath()) {
            final GroupType groupType = currentType.asGroupType();
            final int fieldIndex = groupType.getFieldIndex(fieldName);
            currentType = groupType.getType(fieldName);
            currentConverter = currentConverter.asGroupConverter().getConverter(fieldIndex);
        }
        final PrimitiveConverter converter = currentConverter.asPrimitiveConverter();
        return converter;
    }
}
