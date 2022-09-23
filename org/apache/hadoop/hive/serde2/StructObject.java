// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.util.List;

public interface StructObject
{
    Object getField(final int p0);
    
    List<Object> getFieldsAsList();
}
