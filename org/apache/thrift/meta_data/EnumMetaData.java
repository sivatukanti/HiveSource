// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.meta_data;

import org.apache.thrift.TEnum;

public class EnumMetaData extends FieldValueMetaData
{
    public final Class<? extends TEnum> enumClass;
    
    public EnumMetaData(final byte type, final Class<? extends TEnum> sClass) {
        super(type);
        this.enumClass = sClass;
    }
}
