// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.MetaException;

public interface PartitionExpressionProxy
{
    String convertExprToFilter(final byte[] p0) throws MetaException;
    
    boolean filterPartitionsByExpr(final List<String> p0, final List<PrimitiveTypeInfo> p1, final byte[] p2, final String p3, final List<String> p4) throws MetaException;
}
