// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.meta_data;

public class MapMetaData extends FieldValueMetaData
{
    public final FieldValueMetaData keyMetaData;
    public final FieldValueMetaData valueMetaData;
    
    public MapMetaData(final byte type, final FieldValueMetaData kMetaData, final FieldValueMetaData vMetaData) {
        super(type);
        this.keyMetaData = kMetaData;
        this.valueMetaData = vMetaData;
    }
}
