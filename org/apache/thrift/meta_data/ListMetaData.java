// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.meta_data;

public class ListMetaData extends FieldValueMetaData
{
    public final FieldValueMetaData elemMetaData;
    
    public ListMetaData(final byte type, final FieldValueMetaData eMetaData) {
        super(type);
        this.elemMetaData = eMetaData;
    }
}
