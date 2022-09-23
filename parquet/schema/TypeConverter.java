// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import java.util.List;

public interface TypeConverter<T>
{
    T convertPrimitiveType(final List<GroupType> p0, final PrimitiveType p1);
    
    T convertGroupType(final List<GroupType> p0, final GroupType p1, final List<T> p2);
    
    T convertMessageType(final MessageType p0, final List<T> p1);
}
