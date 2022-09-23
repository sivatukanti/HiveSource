// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import parquet.io.api.Binary;
import parquet.io.api.PrimitiveConverter;
import parquet.io.api.RecordConsumer;
import parquet.column.ColumnReader;
import java.util.List;
import java.util.Arrays;
import parquet.io.InvalidRecordException;

public final class PrimitiveType extends Type
{
    private final PrimitiveTypeName primitive;
    private final int length;
    private final DecimalMetadata decimalMeta;
    
    public PrimitiveType(final Repetition repetition, final PrimitiveTypeName primitive, final String name) {
        this(repetition, primitive, 0, name, null, null, null);
    }
    
    public PrimitiveType(final Repetition repetition, final PrimitiveTypeName primitive, final int length, final String name) {
        this(repetition, primitive, length, name, null, null, null);
    }
    
    public PrimitiveType(final Repetition repetition, final PrimitiveTypeName primitive, final String name, final OriginalType originalType) {
        this(repetition, primitive, 0, name, originalType, null, null);
    }
    
    @Deprecated
    public PrimitiveType(final Repetition repetition, final PrimitiveTypeName primitive, final int length, final String name, final OriginalType originalType) {
        this(repetition, primitive, length, name, originalType, null, null);
    }
    
    PrimitiveType(final Repetition repetition, final PrimitiveTypeName primitive, final int length, final String name, final OriginalType originalType, final DecimalMetadata decimalMeta, final ID id) {
        super(name, repetition, originalType, id);
        this.primitive = primitive;
        this.length = length;
        this.decimalMeta = decimalMeta;
    }
    
    @Override
    public PrimitiveType withId(final int id) {
        return new PrimitiveType(this.getRepetition(), this.primitive, this.length, this.getName(), this.getOriginalType(), this.decimalMeta, new ID(id));
    }
    
    public PrimitiveTypeName getPrimitiveTypeName() {
        return this.primitive;
    }
    
    public int getTypeLength() {
        return this.length;
    }
    
    public DecimalMetadata getDecimalMetadata() {
        return this.decimalMeta;
    }
    
    @Override
    public boolean isPrimitive() {
        return true;
    }
    
    @Override
    public void accept(final TypeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void writeToStringBuilder(final StringBuilder sb, final String indent) {
        sb.append(indent).append(this.getRepetition().name().toLowerCase()).append(" ").append(this.primitive.name().toLowerCase());
        if (this.primitive == PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY) {
            sb.append("(" + this.length + ")");
        }
        sb.append(" ").append(this.getName());
        if (this.getOriginalType() != null) {
            sb.append(" (").append(this.getOriginalType());
            final DecimalMetadata meta = this.getDecimalMetadata();
            if (meta != null) {
                sb.append("(").append(meta.getPrecision()).append(",").append(meta.getScale()).append(")");
            }
            sb.append(")");
        }
        if (this.getId() != null) {
            sb.append(" = ").append(this.getId());
        }
    }
    
    @Deprecated
    @Override
    protected int typeHashCode() {
        return this.hashCode();
    }
    
    @Deprecated
    @Override
    protected boolean typeEquals(final Type other) {
        return this.equals(other);
    }
    
    @Override
    protected boolean equals(final Type other) {
        if (!other.isPrimitive()) {
            return false;
        }
        final PrimitiveType otherPrimitive = other.asPrimitiveType();
        return super.equals(other) && this.primitive == otherPrimitive.getPrimitiveTypeName() && this.length == otherPrimitive.length && this.eqOrBothNull(this.decimalMeta, otherPrimitive.decimalMeta);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.primitive.hashCode();
        hash = hash * 31 + this.length;
        if (this.decimalMeta != null) {
            hash = hash * 31 + this.decimalMeta.hashCode();
        }
        return hash;
    }
    
    public int getMaxRepetitionLevel(final String[] path, final int i) {
        if (path.length != i) {
            throw new InvalidRecordException("Arrived at primitive node, path invalid");
        }
        return this.isRepetition(Repetition.REPEATED) ? 1 : 0;
    }
    
    public int getMaxDefinitionLevel(final String[] path, final int i) {
        if (path.length != i) {
            throw new InvalidRecordException("Arrived at primitive node, path invalid");
        }
        return this.isRepetition(Repetition.REQUIRED) ? 0 : 1;
    }
    
    public Type getType(final String[] path, final int i) {
        if (path.length != i) {
            throw new InvalidRecordException("Arrived at primitive node at index " + i + " , path invalid: " + Arrays.toString(path));
        }
        return this;
    }
    
    @Override
    protected List<String[]> getPaths(final int depth) {
        return Arrays.asList(new String[][] { new String[depth] });
    }
    
    @Override
    void checkContains(final Type subType) {
        super.checkContains(subType);
        if (!subType.isPrimitive()) {
            throw new InvalidRecordException(subType + " found: expected " + this);
        }
        final PrimitiveType primitiveType = subType.asPrimitiveType();
        if (this.primitive != primitiveType.primitive) {
            throw new InvalidRecordException(subType + " found: expected " + this);
        }
    }
    
    public <T> T convert(final List<GroupType> path, final TypeConverter<T> converter) {
        return converter.convertPrimitiveType(path, this);
    }
    
    @Override
    protected boolean containsPath(final String[] path, final int depth) {
        return path.length == depth;
    }
    
    @Override
    protected Type union(final Type toMerge) {
        return this.union(toMerge, true);
    }
    
    @Override
    protected Type union(final Type toMerge, final boolean strict) {
        if (!toMerge.isPrimitive() || (strict && !this.primitive.equals(toMerge.asPrimitiveType().getPrimitiveTypeName()))) {
            throw new IncompatibleSchemaModificationException("can not merge type " + toMerge + " into " + this);
        }
        final Types.PrimitiveBuilder<PrimitiveType> builder = Types.primitive(this.primitive, toMerge.getRepetition());
        if (PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY == this.primitive) {
            builder.length(this.length);
        }
        return builder.named(this.getName());
    }
    
    public enum PrimitiveTypeName
    {
        INT64("getLong", (Class)Long.TYPE) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getLong());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addLong(columnReader.getLong());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addLong(columnReader.getLong());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertINT64(this);
            }
        }, 
        INT32("getInteger", (Class)Integer.TYPE) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getInteger());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addInteger(columnReader.getInteger());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addInt(columnReader.getInteger());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertINT32(this);
            }
        }, 
        BOOLEAN("getBoolean", (Class)Boolean.TYPE) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getBoolean());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addBoolean(columnReader.getBoolean());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addBoolean(columnReader.getBoolean());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertBOOLEAN(this);
            }
        }, 
        BINARY("getBinary", (Class)Binary.class) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getBinary());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addBinary(columnReader.getBinary());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addBinary(columnReader.getBinary());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertBINARY(this);
            }
        }, 
        FLOAT("getFloat", (Class)Float.TYPE) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getFloat());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addFloat(columnReader.getFloat());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addFloat(columnReader.getFloat());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertFLOAT(this);
            }
        }, 
        DOUBLE("getDouble", (Class)Double.TYPE) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getDouble());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addDouble(columnReader.getDouble());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addDouble(columnReader.getDouble());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertDOUBLE(this);
            }
        }, 
        INT96("getBinary", (Class)Binary.class) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return Arrays.toString(columnReader.getBinary().getBytes());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addBinary(columnReader.getBinary());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addBinary(columnReader.getBinary());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertINT96(this);
            }
        }, 
        FIXED_LEN_BYTE_ARRAY("getBinary", (Class)Binary.class) {
            @Override
            public String toString(final ColumnReader columnReader) {
                return String.valueOf(columnReader.getBinary());
            }
            
            @Override
            public void addValueToRecordConsumer(final RecordConsumer recordConsumer, final ColumnReader columnReader) {
                recordConsumer.addBinary(columnReader.getBinary());
            }
            
            @Override
            public void addValueToPrimitiveConverter(final PrimitiveConverter primitiveConverter, final ColumnReader columnReader) {
                primitiveConverter.addBinary(columnReader.getBinary());
            }
            
            @Override
            public <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> converter) throws E, Exception {
                return converter.convertFIXED_LEN_BYTE_ARRAY(this);
            }
        };
        
        public final String getMethod;
        public final Class<?> javaType;
        
        private PrimitiveTypeName(final String getMethod, final Class<?> javaType) {
            this.getMethod = getMethod;
            this.javaType = javaType;
        }
        
        public abstract String toString(final ColumnReader p0);
        
        public abstract void addValueToRecordConsumer(final RecordConsumer p0, final ColumnReader p1);
        
        public abstract void addValueToPrimitiveConverter(final PrimitiveConverter p0, final ColumnReader p1);
        
        public abstract <T, E extends Exception> T convert(final PrimitiveTypeNameConverter<T, E> p0) throws E, Exception;
    }
    
    public interface PrimitiveTypeNameConverter<T, E extends Exception>
    {
        T convertFLOAT(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertDOUBLE(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertINT32(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertINT64(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertINT96(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertFIXED_LEN_BYTE_ARRAY(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertBOOLEAN(final PrimitiveTypeName p0) throws E, Exception;
        
        T convertBINARY(final PrimitiveTypeName p0) throws E, Exception;
    }
}
