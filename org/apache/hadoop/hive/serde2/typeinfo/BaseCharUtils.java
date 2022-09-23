// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.serde2.io.HiveBaseCharWritable;

public class BaseCharUtils
{
    public static void validateVarcharParameter(final int length) {
        if (length > 65535 || length < 1) {
            throw new RuntimeException("Varchar length " + length + " out of allowed range [1, " + 65535 + "]");
        }
    }
    
    public static void validateCharParameter(final int length) {
        if (length > 255 || length < 1) {
            throw new RuntimeException("Char length " + length + " out of allowed range [1, " + 255 + "]");
        }
    }
    
    public static boolean doesWritableMatchTypeParams(final HiveBaseCharWritable writable, final BaseCharTypeInfo typeInfo) {
        return typeInfo.getLength() >= writable.getCharacterLength();
    }
    
    public static boolean doesPrimitiveMatchTypeParams(final HiveBaseChar value, final BaseCharTypeInfo typeInfo) {
        return typeInfo.getLength() == value.getCharacterLength();
    }
}
