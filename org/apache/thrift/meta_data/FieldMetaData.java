// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.meta_data;

import java.util.HashMap;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TBase;
import java.util.Map;
import java.io.Serializable;

public class FieldMetaData implements Serializable
{
    public final String fieldName;
    public final byte requirementType;
    public final FieldValueMetaData valueMetaData;
    private static Map<Class<? extends TBase>, Map<? extends TFieldIdEnum, FieldMetaData>> structMap;
    
    public FieldMetaData(final String name, final byte req, final FieldValueMetaData vMetaData) {
        this.fieldName = name;
        this.requirementType = req;
        this.valueMetaData = vMetaData;
    }
    
    public static void addStructMetaDataMap(final Class<? extends TBase> sClass, final Map<? extends TFieldIdEnum, FieldMetaData> map) {
        FieldMetaData.structMap.put(sClass, map);
    }
    
    public static Map<? extends TFieldIdEnum, FieldMetaData> getStructMetaDataMap(final Class<? extends TBase> sClass) {
        if (!FieldMetaData.structMap.containsKey(sClass)) {
            try {
                sClass.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("InstantiationException for TBase class: " + sClass.getName() + ", message: " + e.getMessage());
            }
            catch (IllegalAccessException e2) {
                throw new RuntimeException("IllegalAccessException for TBase class: " + sClass.getName() + ", message: " + e2.getMessage());
            }
        }
        return FieldMetaData.structMap.get(sClass);
    }
    
    static {
        FieldMetaData.structMap = new HashMap<Class<? extends TBase>, Map<? extends TFieldIdEnum, FieldMetaData>>();
    }
}
