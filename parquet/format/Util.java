// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.protocol.TCompactProtocol;
import parquet.org.apache.thrift.transport.TIOStreamTransport;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TException;
import parquet.format.event.FieldConsumer;
import parquet.format.event.EventBasedThriftReader;
import java.util.List;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.format.event.TypedConsumer;
import parquet.format.event.Consumers;
import java.io.InputStream;
import java.io.IOException;
import parquet.org.apache.thrift.TBase;
import java.io.OutputStream;

public class Util
{
    public static void writePageHeader(final PageHeader pageHeader, final OutputStream to) throws IOException {
        write(pageHeader, to);
    }
    
    public static PageHeader readPageHeader(final InputStream from) throws IOException {
        return read(from, new PageHeader());
    }
    
    public static void writeFileMetaData(final FileMetaData fileMetadata, final OutputStream to) throws IOException {
        write(fileMetadata, to);
    }
    
    public static FileMetaData readFileMetaData(final InputStream from) throws IOException {
        return read(from, new FileMetaData());
    }
    
    public static FileMetaData readFileMetaData(final InputStream from, final boolean skipRowGroups) throws IOException {
        final FileMetaData md = new FileMetaData();
        if (skipRowGroups) {
            readFileMetaData(from, new DefaultFileMetaDataConsumer(md), skipRowGroups);
        }
        else {
            read(from, md);
        }
        return md;
    }
    
    public static void readFileMetaData(final InputStream from, final FileMetaDataConsumer consumer) throws IOException {
        readFileMetaData(from, consumer, false);
    }
    
    public static void readFileMetaData(final InputStream from, final FileMetaDataConsumer consumer, final boolean skipRowGroups) throws IOException {
        try {
            Consumers.DelegatingFieldConsumer eventConsumer = Consumers.fieldConsumer().onField(FileMetaData._Fields.VERSION, new TypedConsumer.I32Consumer() {
                @Override
                public void consume(final int value) {
                    consumer.setVersion(value);
                }
            }).onField(FileMetaData._Fields.SCHEMA, Consumers.listOf(SchemaElement.class, new Consumers.Consumer<List<SchemaElement>>() {
                @Override
                public void consume(final List<SchemaElement> schema) {
                    consumer.setSchema(schema);
                }
            })).onField(FileMetaData._Fields.NUM_ROWS, new TypedConsumer.I64Consumer() {
                @Override
                public void consume(final long value) {
                    consumer.setNumRows(value);
                }
            }).onField(FileMetaData._Fields.KEY_VALUE_METADATA, Consumers.listElementsOf(Consumers.struct(KeyValue.class, new Consumers.Consumer<KeyValue>() {
                @Override
                public void consume(final KeyValue kv) {
                    consumer.addKeyValueMetaData(kv);
                }
            }))).onField(FileMetaData._Fields.CREATED_BY, new TypedConsumer.StringConsumer() {
                @Override
                public void consume(final String value) {
                    consumer.setCreatedBy(value);
                }
            });
            if (!skipRowGroups) {
                eventConsumer = eventConsumer.onField(FileMetaData._Fields.ROW_GROUPS, Consumers.listElementsOf(Consumers.struct(RowGroup.class, new Consumers.Consumer<RowGroup>() {
                    @Override
                    public void consume(final RowGroup rowGroup) {
                        consumer.addRowGroup(rowGroup);
                    }
                })));
            }
            new EventBasedThriftReader(protocol(from)).readStruct(eventConsumer);
        }
        catch (TException e) {
            throw new IOException("can not read FileMetaData: " + e.getMessage(), e);
        }
    }
    
    private static TProtocol protocol(final OutputStream to) {
        return protocol(new TIOStreamTransport(to));
    }
    
    private static TProtocol protocol(final InputStream from) {
        return protocol(new TIOStreamTransport(from));
    }
    
    private static InterningProtocol protocol(final TIOStreamTransport t) {
        return new InterningProtocol(new TCompactProtocol(t));
    }
    
    private static <T extends TBase<?, ?>> T read(final InputStream from, final T tbase) throws IOException {
        try {
            tbase.read(protocol(from));
            return tbase;
        }
        catch (TException e) {
            throw new IOException("can not read " + tbase.getClass() + ": " + e.getMessage(), e);
        }
    }
    
    private static void write(final TBase<?, ?> tbase, final OutputStream to) throws IOException {
        try {
            tbase.write(protocol(to));
        }
        catch (TException e) {
            throw new IOException("can not write " + tbase, e);
        }
    }
    
    public abstract static class FileMetaDataConsumer
    {
        public abstract void setVersion(final int p0);
        
        public abstract void setSchema(final List<SchemaElement> p0);
        
        public abstract void setNumRows(final long p0);
        
        public abstract void addRowGroup(final RowGroup p0);
        
        public abstract void addKeyValueMetaData(final KeyValue p0);
        
        public abstract void setCreatedBy(final String p0);
    }
    
    public static final class DefaultFileMetaDataConsumer extends FileMetaDataConsumer
    {
        private final FileMetaData md;
        
        public DefaultFileMetaDataConsumer(final FileMetaData md) {
            this.md = md;
        }
        
        @Override
        public void setVersion(final int version) {
            this.md.setVersion(version);
        }
        
        @Override
        public void setSchema(final List<SchemaElement> schema) {
            this.md.setSchema(schema);
        }
        
        @Override
        public void setNumRows(final long numRows) {
            this.md.setNum_rows(numRows);
        }
        
        @Override
        public void setCreatedBy(final String createdBy) {
            this.md.setCreated_by(createdBy);
        }
        
        @Override
        public void addRowGroup(final RowGroup rowGroup) {
            this.md.addToRow_groups(rowGroup);
        }
        
        @Override
        public void addKeyValueMetaData(final KeyValue kv) {
            this.md.addToKey_value_metadata(kv);
        }
    }
}
