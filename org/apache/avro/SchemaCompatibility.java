// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.Arrays;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;

public class SchemaCompatibility
{
    private static final Logger LOG;
    public static final String READER_WRITER_COMPATIBLE_MESSAGE = "Reader schema can always successfully decode data written using the writer schema.";
    
    private SchemaCompatibility() {
    }
    
    public static SchemaPairCompatibility checkReaderWriterCompatibility(final Schema reader, final Schema writer) {
        final SchemaCompatibilityType compatibility = new ReaderWriterCompatiblityChecker().getCompatibility(reader, writer);
        String message = null;
        switch (compatibility) {
            case INCOMPATIBLE: {
                message = String.format("Data encoded using writer schema:%n%s%nwill or may fail to decode using reader schema:%n%s%n", writer.toString(true), reader.toString(true));
                break;
            }
            case COMPATIBLE: {
                message = "Reader schema can always successfully decode data written using the writer schema.";
                break;
            }
            default: {
                throw new AvroRuntimeException("Unknown compatibility: " + compatibility);
            }
        }
        return new SchemaPairCompatibility(compatibility, reader, writer, message);
    }
    
    public static boolean schemaNameEquals(final Schema reader, final Schema writer) {
        final String writerFullName = writer.getFullName();
        return objectsEqual(reader.getFullName(), writerFullName) || reader.getAliases().contains(writerFullName);
    }
    
    public static Schema.Field lookupWriterField(final Schema writerSchema, final Schema.Field readerField) {
        assert writerSchema.getType() == Schema.Type.RECORD;
        final List<Schema.Field> writerFields = new ArrayList<Schema.Field>();
        final Schema.Field direct = writerSchema.getField(readerField.name());
        if (direct != null) {
            writerFields.add(direct);
        }
        for (final String readerFieldAliasName : readerField.aliases()) {
            final Schema.Field writerField = writerSchema.getField(readerFieldAliasName);
            if (writerField != null) {
                writerFields.add(writerField);
            }
        }
        switch (writerFields.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return writerFields.get(0);
            }
            default: {
                throw new AvroRuntimeException(String.format("Reader record field %s matches multiple fields in writer record schema %s", readerField, writerSchema));
            }
        }
    }
    
    private static boolean objectsEqual(final Object obj1, final Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }
    
    static {
        LOG = LoggerFactory.getLogger(SchemaCompatibility.class);
    }
    
    private static final class ReaderWriter
    {
        private final Schema mReader;
        private final Schema mWriter;
        
        public ReaderWriter(final Schema reader, final Schema writer) {
            this.mReader = reader;
            this.mWriter = writer;
        }
        
        public Schema getReader() {
            return this.mReader;
        }
        
        public Schema getWriter() {
            return this.mWriter;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.mReader) ^ System.identityHashCode(this.mWriter);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ReaderWriter)) {
                return false;
            }
            final ReaderWriter that = (ReaderWriter)obj;
            return this.mReader == that.mReader && this.mWriter == that.mWriter;
        }
        
        @Override
        public String toString() {
            return String.format("ReaderWriter{reader:%s, writer:%s}", this.mReader, this.mWriter);
        }
    }
    
    private static final class ReaderWriterCompatiblityChecker
    {
        private final Map<ReaderWriter, SchemaCompatibilityType> mMemoizeMap;
        
        private ReaderWriterCompatiblityChecker() {
            this.mMemoizeMap = new HashMap<ReaderWriter, SchemaCompatibilityType>();
        }
        
        public SchemaCompatibilityType getCompatibility(final Schema reader, final Schema writer) {
            SchemaCompatibility.LOG.debug("Checking compatibility of reader {} with writer {}", reader, writer);
            final ReaderWriter pair = new ReaderWriter(reader, writer);
            final SchemaCompatibilityType existing = this.mMemoizeMap.get(pair);
            if (existing == null) {
                this.mMemoizeMap.put(pair, SchemaCompatibilityType.RECURSION_IN_PROGRESS);
                final SchemaCompatibilityType calculated = this.calculateCompatibility(reader, writer);
                this.mMemoizeMap.put(pair, calculated);
                return calculated;
            }
            if (existing == SchemaCompatibilityType.RECURSION_IN_PROGRESS) {
                return SchemaCompatibilityType.COMPATIBLE;
            }
            return existing;
        }
        
        private SchemaCompatibilityType calculateCompatibility(final Schema reader, final Schema writer) {
            assert reader != null;
            assert writer != null;
            if (reader.getType() == writer.getType()) {
                switch (reader.getType()) {
                    case NULL:
                    case BOOLEAN:
                    case INT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                    case BYTES:
                    case STRING: {
                        return SchemaCompatibilityType.COMPATIBLE;
                    }
                    case ARRAY: {
                        return this.getCompatibility(reader.getElementType(), writer.getElementType());
                    }
                    case MAP: {
                        return this.getCompatibility(reader.getValueType(), writer.getValueType());
                    }
                    case FIXED: {
                        if (!SchemaCompatibility.schemaNameEquals(reader, writer)) {
                            return SchemaCompatibilityType.INCOMPATIBLE;
                        }
                        if (reader.getFixedSize() != writer.getFixedSize()) {
                            return SchemaCompatibilityType.INCOMPATIBLE;
                        }
                        return SchemaCompatibilityType.COMPATIBLE;
                    }
                    case ENUM: {
                        if (!SchemaCompatibility.schemaNameEquals(reader, writer)) {
                            return SchemaCompatibilityType.INCOMPATIBLE;
                        }
                        final Set<String> symbols = new HashSet<String>(writer.getEnumSymbols());
                        symbols.removeAll(reader.getEnumSymbols());
                        return symbols.isEmpty() ? SchemaCompatibilityType.COMPATIBLE : SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case RECORD: {
                        if (!SchemaCompatibility.schemaNameEquals(reader, writer)) {
                            return SchemaCompatibilityType.INCOMPATIBLE;
                        }
                        for (final Schema.Field readerField : reader.getFields()) {
                            final Schema.Field writerField = SchemaCompatibility.lookupWriterField(writer, readerField);
                            if (writerField == null) {
                                if (readerField.defaultValue() == null) {
                                    return SchemaCompatibilityType.INCOMPATIBLE;
                                }
                                continue;
                            }
                            else {
                                if (this.getCompatibility(readerField.schema(), writerField.schema()) == SchemaCompatibilityType.INCOMPATIBLE) {
                                    return SchemaCompatibilityType.INCOMPATIBLE;
                                }
                                continue;
                            }
                        }
                        return SchemaCompatibilityType.COMPATIBLE;
                    }
                    case UNION: {
                        for (final Schema writerBranch : writer.getTypes()) {
                            if (this.getCompatibility(reader, writerBranch) == SchemaCompatibilityType.INCOMPATIBLE) {
                                return SchemaCompatibilityType.INCOMPATIBLE;
                            }
                        }
                        return SchemaCompatibilityType.COMPATIBLE;
                    }
                    default: {
                        throw new AvroRuntimeException("Unknown schema type: " + reader.getType());
                    }
                }
            }
            else {
                if (writer.getType() == Schema.Type.UNION && writer.getTypes().size() == 1) {
                    return this.getCompatibility(reader, writer.getTypes().get(0));
                }
                switch (reader.getType()) {
                    case NULL: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case BOOLEAN: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case INT: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case LONG: {
                        return (writer.getType() == Schema.Type.INT) ? SchemaCompatibilityType.COMPATIBLE : SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case FLOAT: {
                        return (writer.getType() == Schema.Type.INT || writer.getType() == Schema.Type.LONG) ? SchemaCompatibilityType.COMPATIBLE : SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case DOUBLE: {
                        return (writer.getType() == Schema.Type.INT || writer.getType() == Schema.Type.LONG || writer.getType() == Schema.Type.FLOAT) ? SchemaCompatibilityType.COMPATIBLE : SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case BYTES: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case STRING: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case ARRAY: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case MAP: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case FIXED: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case ENUM: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case RECORD: {
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    case UNION: {
                        for (final Schema readerBranch : reader.getTypes()) {
                            if (this.getCompatibility(readerBranch, writer) == SchemaCompatibilityType.COMPATIBLE) {
                                return SchemaCompatibilityType.COMPATIBLE;
                            }
                        }
                        return SchemaCompatibilityType.INCOMPATIBLE;
                    }
                    default: {
                        throw new AvroRuntimeException("Unknown schema type: " + reader.getType());
                    }
                }
            }
        }
    }
    
    public enum SchemaCompatibilityType
    {
        COMPATIBLE, 
        INCOMPATIBLE, 
        RECURSION_IN_PROGRESS;
    }
    
    public static final class SchemaPairCompatibility
    {
        private final SchemaCompatibilityType mType;
        private final Schema mReader;
        private final Schema mWriter;
        private final String mDescription;
        
        public SchemaPairCompatibility(final SchemaCompatibilityType type, final Schema reader, final Schema writer, final String description) {
            this.mType = type;
            this.mReader = reader;
            this.mWriter = writer;
            this.mDescription = description;
        }
        
        public SchemaCompatibilityType getType() {
            return this.mType;
        }
        
        public Schema getReader() {
            return this.mReader;
        }
        
        public Schema getWriter() {
            return this.mWriter;
        }
        
        public String getDescription() {
            return this.mDescription;
        }
        
        @Override
        public String toString() {
            return String.format("SchemaPairCompatibility{type:%s, readerSchema:%s, writerSchema:%s, description:%s}", this.mType, this.mReader, this.mWriter, this.mDescription);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (null != other && other instanceof SchemaPairCompatibility) {
                final SchemaPairCompatibility result = (SchemaPairCompatibility)other;
                return objectsEqual(result.mType, this.mType) && objectsEqual(result.mReader, this.mReader) && objectsEqual(result.mWriter, this.mWriter) && objectsEqual(result.mDescription, this.mDescription);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] { this.mType, this.mReader, this.mWriter, this.mDescription });
        }
    }
}
