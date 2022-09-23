// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.io.api.Binary;
import java.util.HashMap;
import parquet.hadoop.metadata.ColumnPath;
import parquet.schema.OriginalType;
import parquet.schema.PrimitiveType;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class ValidTypeMap
{
    private static final Map<Class<?>, Set<FullTypeDescriptor>> classToParquetType;
    private static final Map<FullTypeDescriptor, Set<Class<?>>> parquetTypeToClass;
    
    private ValidTypeMap() {
    }
    
    private static void add(final Class<?> c, final FullTypeDescriptor f) {
        Set<FullTypeDescriptor> descriptors = ValidTypeMap.classToParquetType.get(c);
        if (descriptors == null) {
            descriptors = new HashSet<FullTypeDescriptor>();
            ValidTypeMap.classToParquetType.put(c, descriptors);
        }
        descriptors.add(f);
        Set<Class<?>> classes = ValidTypeMap.parquetTypeToClass.get(f);
        if (classes == null) {
            classes = new HashSet<Class<?>>();
            ValidTypeMap.parquetTypeToClass.put(f, classes);
        }
        classes.add(c);
    }
    
    public static <T extends Comparable<T>> void assertTypeValid(final Operators.Column<T> foundColumn, final PrimitiveType.PrimitiveTypeName primitiveType, final OriginalType originalType) {
        final Class<T> foundColumnType = foundColumn.getColumnType();
        final ColumnPath columnPath = foundColumn.getColumnPath();
        final Set<FullTypeDescriptor> validTypeDescriptors = ValidTypeMap.classToParquetType.get(foundColumnType);
        final FullTypeDescriptor typeInFileMetaData = new FullTypeDescriptor(primitiveType, originalType);
        if (validTypeDescriptors == null) {
            final StringBuilder message = new StringBuilder();
            message.append("Column ").append(columnPath.toDotString()).append(" was declared as type: ").append(foundColumnType.getName()).append(" which is not supported in FilterPredicates.");
            final Set<Class<?>> supportedTypes = ValidTypeMap.parquetTypeToClass.get(typeInFileMetaData);
            if (supportedTypes != null) {
                message.append(" Supported types for this column are: ").append(supportedTypes);
            }
            else {
                message.append(" There are no supported types for columns of " + typeInFileMetaData);
            }
            throw new IllegalArgumentException(message.toString());
        }
        if (!validTypeDescriptors.contains(typeInFileMetaData)) {
            final StringBuilder message = new StringBuilder();
            message.append("FilterPredicate column: ").append(columnPath.toDotString()).append("'s declared type (").append(foundColumnType.getName()).append(") does not match the schema found in file metadata. Column ").append(columnPath.toDotString()).append(" is of type: ").append(typeInFileMetaData).append("\nValid types for this column are: ").append(ValidTypeMap.parquetTypeToClass.get(typeInFileMetaData));
            throw new IllegalArgumentException(message.toString());
        }
    }
    
    static {
        classToParquetType = new HashMap<Class<?>, Set<FullTypeDescriptor>>();
        parquetTypeToClass = new HashMap<FullTypeDescriptor, Set<Class<?>>>();
        add(Integer.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.INT32, (OriginalType)null));
        add(Long.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.INT64, (OriginalType)null));
        add(Float.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.FLOAT, (OriginalType)null));
        add(Double.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.DOUBLE, (OriginalType)null));
        add(Boolean.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.BOOLEAN, (OriginalType)null));
        add(Binary.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.BINARY, (OriginalType)null));
        add(Binary.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY, (OriginalType)null));
        add(Binary.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.BINARY, OriginalType.UTF8));
        add(Binary.class, new FullTypeDescriptor(PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY, OriginalType.UTF8));
    }
    
    private static final class FullTypeDescriptor
    {
        private final PrimitiveType.PrimitiveTypeName primitiveType;
        private final OriginalType originalType;
        
        private FullTypeDescriptor(final PrimitiveType.PrimitiveTypeName primitiveType, final OriginalType originalType) {
            this.primitiveType = primitiveType;
            this.originalType = originalType;
        }
        
        public PrimitiveType.PrimitiveTypeName getPrimitiveType() {
            return this.primitiveType;
        }
        
        public OriginalType getOriginalType() {
            return this.originalType;
        }
        
        @Override
        public String toString() {
            return "FullTypeDescriptor(PrimitiveType: " + this.primitiveType + ", OriginalType: " + this.originalType + ')';
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final FullTypeDescriptor that = (FullTypeDescriptor)o;
            return this.originalType == that.originalType && this.primitiveType == that.primitiveType;
        }
        
        @Override
        public int hashCode() {
            int result = (this.primitiveType != null) ? this.primitiveType.hashCode() : 0;
            result = 31 * result + ((this.originalType != null) ? this.originalType.hashCode() : 0);
            return result;
        }
    }
}
