// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import java.util.BitSet;
import parquet.io.api.Binary;
import java.util.Iterator;
import parquet.column.ColumnWriter;
import parquet.schema.Type;
import java.util.Arrays;
import parquet.io.api.RecordConsumer;
import parquet.column.ColumnWriteStore;
import parquet.filter2.recordlevel.IncrementallyUpdatedFilterPredicate;
import parquet.filter2.predicate.FilterPredicate;
import parquet.column.impl.ColumnReadStoreImpl;
import parquet.filter2.recordlevel.FilteringRecordMaterializer;
import parquet.filter2.recordlevel.IncrementallyUpdatedFilterPredicateBuilder;
import parquet.Preconditions;
import parquet.filter.UnboundRecordFilter;
import parquet.filter2.compat.FilterCompat;
import parquet.io.api.RecordMaterializer;
import parquet.column.page.PageReadStore;
import parquet.schema.GroupType;
import parquet.schema.MessageType;
import java.util.List;
import parquet.Log;

public class MessageColumnIO extends GroupColumnIO
{
    private static final Log logger;
    private static final boolean DEBUG;
    private List<PrimitiveColumnIO> leaves;
    private final boolean validating;
    
    MessageColumnIO(final MessageType messageType, final boolean validating) {
        super(messageType, null, 0);
        this.validating = validating;
    }
    
    public List<String[]> getColumnNames() {
        return super.getColumnNames();
    }
    
    public <T> RecordReader<T> getRecordReader(final PageReadStore columns, final RecordMaterializer<T> recordMaterializer) {
        return this.getRecordReader(columns, recordMaterializer, FilterCompat.NOOP);
    }
    
    @Deprecated
    public <T> RecordReader<T> getRecordReader(final PageReadStore columns, final RecordMaterializer<T> recordMaterializer, final UnboundRecordFilter filter) {
        return this.getRecordReader(columns, recordMaterializer, FilterCompat.get(filter));
    }
    
    public <T> RecordReader<T> getRecordReader(final PageReadStore columns, final RecordMaterializer<T> recordMaterializer, final FilterCompat.Filter filter) {
        Preconditions.checkNotNull(columns, "columns");
        Preconditions.checkNotNull(recordMaterializer, "recordMaterializer");
        Preconditions.checkNotNull(filter, "filter");
        if (this.leaves.isEmpty()) {
            return new EmptyRecordReader<T>(recordMaterializer);
        }
        return filter.accept((FilterCompat.Visitor<RecordReader<T>>)new FilterCompat.Visitor<RecordReader<T>>() {
            @Override
            public RecordReader<T> visit(final FilterCompat.FilterPredicateCompat filterPredicateCompat) {
                final FilterPredicate predicate = filterPredicateCompat.getFilterPredicate();
                final IncrementallyUpdatedFilterPredicateBuilder builder = new IncrementallyUpdatedFilterPredicateBuilder();
                final IncrementallyUpdatedFilterPredicate streamingPredicate = builder.build(predicate);
                final RecordMaterializer<T> filteringRecordMaterializer = new FilteringRecordMaterializer<T>(recordMaterializer, MessageColumnIO.this.leaves, builder.getValueInspectorsByColumn(), streamingPredicate);
                return new RecordReaderImplementation<T>(MessageColumnIO.this, filteringRecordMaterializer, MessageColumnIO.this.validating, new ColumnReadStoreImpl(columns, filteringRecordMaterializer.getRootConverter(), MessageColumnIO.this.getType()));
            }
            
            @Override
            public RecordReader<T> visit(final FilterCompat.UnboundRecordFilterCompat unboundRecordFilterCompat) {
                return new FilteredRecordReader<T>(MessageColumnIO.this, recordMaterializer, MessageColumnIO.this.validating, new ColumnReadStoreImpl(columns, recordMaterializer.getRootConverter(), MessageColumnIO.this.getType()), unboundRecordFilterCompat.getUnboundRecordFilter(), columns.getRowCount());
            }
            
            @Override
            public RecordReader<T> visit(final FilterCompat.NoOpFilter noOpFilter) {
                return new RecordReaderImplementation<T>(MessageColumnIO.this, recordMaterializer, MessageColumnIO.this.validating, new ColumnReadStoreImpl(columns, recordMaterializer.getRootConverter(), MessageColumnIO.this.getType()));
            }
        });
    }
    
    public RecordConsumer getRecordWriter(final ColumnWriteStore columns) {
        RecordConsumer recordWriter = new MessageColumnIORecordConsumer(columns);
        if (MessageColumnIO.DEBUG) {
            recordWriter = new RecordConsumerLoggingWrapper(recordWriter);
        }
        return this.validating ? new ValidatingRecordConsumer(recordWriter, this.getType()) : recordWriter;
    }
    
    void setLevels() {
        this.setLevels(0, 0, new String[0], new int[0], Arrays.asList(this), Arrays.asList(this));
    }
    
    void setLeaves(final List<PrimitiveColumnIO> leaves) {
        this.leaves = leaves;
    }
    
    public List<PrimitiveColumnIO> getLeaves() {
        return this.leaves;
    }
    
    @Override
    public MessageType getType() {
        return (MessageType)super.getType();
    }
    
    static {
        logger = Log.getLog(MessageColumnIO.class);
        DEBUG = Log.DEBUG;
    }
    
    private class MessageColumnIORecordConsumer extends RecordConsumer
    {
        private ColumnIO currentColumnIO;
        private int currentLevel;
        private final FieldsMarker[] fieldsWritten;
        private final int[] r;
        private final ColumnWriter[] columnWriter;
        private final ColumnWriteStore columns;
        private boolean emptyField;
        
        public MessageColumnIORecordConsumer(final ColumnWriteStore columns) {
            this.currentLevel = 0;
            this.emptyField = true;
            this.columns = columns;
            int maxDepth = 0;
            this.columnWriter = new ColumnWriter[MessageColumnIO.this.getLeaves().size()];
            for (final PrimitiveColumnIO primitiveColumnIO : MessageColumnIO.this.getLeaves()) {
                maxDepth = Math.max(maxDepth, primitiveColumnIO.getFieldPath().length);
                this.columnWriter[primitiveColumnIO.getId()] = columns.getColumnWriter(primitiveColumnIO.getColumnDescriptor());
            }
            this.fieldsWritten = new FieldsMarker[maxDepth];
            for (int i = 0; i < maxDepth; ++i) {
                this.fieldsWritten[i] = new FieldsMarker();
            }
            this.r = new int[maxDepth];
        }
        
        public void printState() {
            this.log(this.currentLevel + ", " + this.fieldsWritten[this.currentLevel] + ": " + Arrays.toString(this.currentColumnIO.getFieldPath()) + " r:" + this.r[this.currentLevel]);
            if (this.r[this.currentLevel] > this.currentColumnIO.getRepetitionLevel()) {
                throw new InvalidRecordException(this.r[this.currentLevel] + "(r) > " + this.currentColumnIO.getRepetitionLevel() + " ( schema r)");
            }
        }
        
        private void log(final Object m) {
            String indent = "";
            for (int i = 0; i < this.currentLevel; ++i) {
                indent += "  ";
            }
            MessageColumnIO.logger.debug(indent + m);
        }
        
        @Override
        public void startMessage() {
            if (MessageColumnIO.DEBUG) {
                this.log("< MESSAGE START >");
            }
            this.currentColumnIO = MessageColumnIO.this;
            this.r[0] = 0;
            final int numberOfFieldsToVisit = ((GroupColumnIO)this.currentColumnIO).getChildrenCount();
            this.fieldsWritten[0].reset(numberOfFieldsToVisit);
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void endMessage() {
            this.writeNullForMissingFieldsAtCurrentLevel();
            this.columns.endRecord();
            if (MessageColumnIO.DEBUG) {
                this.log("< MESSAGE END >");
            }
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void startField(final String field, final int index) {
            try {
                if (MessageColumnIO.DEBUG) {
                    this.log("startField(" + field + ", " + index + ")");
                }
                this.currentColumnIO = ((GroupColumnIO)this.currentColumnIO).getChild(index);
                this.emptyField = true;
                if (MessageColumnIO.DEBUG) {
                    this.printState();
                }
            }
            catch (RuntimeException e) {
                throw new ParquetEncodingException("error starting field " + field + " at " + index, e);
            }
        }
        
        @Override
        public void endField(final String field, final int index) {
            if (MessageColumnIO.DEBUG) {
                this.log("endField(" + field + ", " + index + ")");
            }
            this.currentColumnIO = this.currentColumnIO.getParent();
            if (this.emptyField) {
                throw new ParquetEncodingException("empty fields are illegal, the field should be ommited completely instead");
            }
            this.fieldsWritten[this.currentLevel].markWritten(index);
            this.r[this.currentLevel] = ((this.currentLevel == 0) ? 0 : this.r[this.currentLevel - 1]);
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        private void writeNullForMissingFieldsAtCurrentLevel() {
            for (int currentFieldsCount = ((GroupColumnIO)this.currentColumnIO).getChildrenCount(), i = 0; i < currentFieldsCount; ++i) {
                if (!this.fieldsWritten[this.currentLevel].isWritten(i)) {
                    try {
                        final ColumnIO undefinedField = ((GroupColumnIO)this.currentColumnIO).getChild(i);
                        final int d = this.currentColumnIO.getDefinitionLevel();
                        if (MessageColumnIO.DEBUG) {
                            this.log(Arrays.toString(undefinedField.getFieldPath()) + ".writeNull(" + this.r[this.currentLevel] + "," + d + ")");
                        }
                        this.writeNull(undefinedField, this.r[this.currentLevel], d);
                    }
                    catch (RuntimeException e) {
                        throw new ParquetEncodingException("error while writing nulls for fields of indexes " + i + " . current index: " + this.fieldsWritten[this.currentLevel], e);
                    }
                }
            }
        }
        
        private void writeNull(final ColumnIO undefinedField, final int r, final int d) {
            if (undefinedField.getType().isPrimitive()) {
                this.columnWriter[((PrimitiveColumnIO)undefinedField).getId()].writeNull(r, d);
            }
            else {
                final GroupColumnIO groupColumnIO = (GroupColumnIO)undefinedField;
                for (int childrenCount = groupColumnIO.getChildrenCount(), i = 0; i < childrenCount; ++i) {
                    this.writeNull(groupColumnIO.getChild(i), r, d);
                }
            }
        }
        
        private void setRepetitionLevel() {
            this.r[this.currentLevel] = this.currentColumnIO.getRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.log("r: " + this.r[this.currentLevel]);
            }
        }
        
        @Override
        public void startGroup() {
            if (MessageColumnIO.DEBUG) {
                this.log("startGroup()");
            }
            ++this.currentLevel;
            this.r[this.currentLevel] = this.r[this.currentLevel - 1];
            final int fieldsCount = ((GroupColumnIO)this.currentColumnIO).getChildrenCount();
            this.fieldsWritten[this.currentLevel].reset(fieldsCount);
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void endGroup() {
            if (MessageColumnIO.DEBUG) {
                this.log("endGroup()");
            }
            this.emptyField = false;
            this.writeNullForMissingFieldsAtCurrentLevel();
            --this.currentLevel;
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        private ColumnWriter getColumnWriter() {
            return this.columnWriter[((PrimitiveColumnIO)this.currentColumnIO).getId()];
        }
        
        @Override
        public void addInteger(final int value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addInt(" + value + ")");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void addLong(final long value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addLong(" + value + ")");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void addBoolean(final boolean value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addBoolean(" + value + ")");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void addBinary(final Binary value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addBinary(" + value.length() + " bytes)");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void addFloat(final float value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addFloat(" + value + ")");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        @Override
        public void addDouble(final double value) {
            if (MessageColumnIO.DEBUG) {
                this.log("addDouble(" + value + ")");
            }
            this.emptyField = false;
            this.getColumnWriter().write(value, this.r[this.currentLevel], this.currentColumnIO.getDefinitionLevel());
            this.setRepetitionLevel();
            if (MessageColumnIO.DEBUG) {
                this.printState();
            }
        }
        
        private class FieldsMarker
        {
            private BitSet vistedIndexes;
            
            private FieldsMarker() {
                this.vistedIndexes = new BitSet();
            }
            
            @Override
            public String toString() {
                return "VistedIndex{vistedIndexes=" + this.vistedIndexes + '}';
            }
            
            public void reset(final int fieldsCount) {
                this.vistedIndexes.clear(0, fieldsCount);
            }
            
            public void markWritten(final int i) {
                this.vistedIndexes.set(i);
            }
            
            public boolean isWritten(final int i) {
                return this.vistedIndexes.get(i);
            }
        }
    }
}
