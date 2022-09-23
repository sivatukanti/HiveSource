// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

public abstract class ConversionPatterns
{
    private static GroupType listWrapper(final Type.Repetition repetition, final String alias, final OriginalType originalType, final Type nested) {
        if (!nested.isRepetition(Type.Repetition.REPEATED)) {
            throw new IllegalArgumentException("Nested type should be repeated: " + nested);
        }
        return new GroupType(repetition, alias, originalType, new Type[] { nested });
    }
    
    public static GroupType mapType(final Type.Repetition repetition, final String alias, final Type keyType, final Type valueType) {
        return mapType(repetition, alias, "map", keyType, valueType);
    }
    
    public static GroupType stringKeyMapType(final Type.Repetition repetition, final String alias, final String mapAlias, final Type valueType) {
        return mapType(repetition, alias, mapAlias, new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.BINARY, "key", OriginalType.UTF8), valueType);
    }
    
    public static GroupType stringKeyMapType(final Type.Repetition repetition, final String alias, final Type valueType) {
        return stringKeyMapType(repetition, alias, "map", valueType);
    }
    
    public static GroupType mapType(final Type.Repetition repetition, final String alias, final String mapAlias, final Type keyType, final Type valueType) {
        if (valueType == null) {
            return listWrapper(repetition, alias, OriginalType.MAP, new GroupType(Type.Repetition.REPEATED, mapAlias, OriginalType.MAP_KEY_VALUE, new Type[] { keyType }));
        }
        if (!valueType.getName().equals("value")) {
            throw new RuntimeException(valueType.getName() + " should be value");
        }
        return listWrapper(repetition, alias, OriginalType.MAP, new GroupType(Type.Repetition.REPEATED, mapAlias, OriginalType.MAP_KEY_VALUE, new Type[] { keyType, valueType }));
    }
    
    public static GroupType listType(final Type.Repetition repetition, final String alias, final Type nestedType) {
        return listWrapper(repetition, alias, OriginalType.LIST, nestedType);
    }
}
