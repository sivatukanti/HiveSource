// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import java.util.HashMap;

@Deprecated
public final class EnumResolver<T extends Enum<T>> extends parquet.org.codehaus.jackson.map.util.EnumResolver<T>
{
    private EnumResolver(final Class<T> enumClass, final T[] enums, final HashMap<String, T> map) {
        super(enumClass, enums, map);
    }
}
