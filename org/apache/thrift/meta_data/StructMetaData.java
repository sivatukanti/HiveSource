// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.meta_data;

import org.apache.thrift.TBase;

public class StructMetaData extends FieldValueMetaData
{
    public final Class<? extends TBase> structClass;
    
    public StructMetaData(final byte type, final Class<? extends TBase> sClass) {
        super(type);
        this.structClass = sClass;
    }
}
