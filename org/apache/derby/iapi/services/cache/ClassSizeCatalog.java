// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.cache;

import java.util.Hashtable;

class ClassSizeCatalog extends Hashtable
{
    ClassSizeCatalog() {
        this.put("org.apache.derby.iapi.types.SQLBit", new int[] { 4, 5 });
        this.put("org.apache.derby.iapi.types.SQLBlob", new int[] { 4, 5 });
        this.put("java.util.Vector", new int[] { 12, 3 });
        this.put("org.apache.derby.iapi.types.SQLLongvarchar", new int[] { 4, 9 });
        this.put("org.apache.derby.iapi.types.SQLLongVarbit", new int[] { 4, 5 });
        this.put("org.apache.derby.impl.store.access.heap.HeapRowLocation", new int[] { 12, 3 });
        this.put("java.util.ArrayList", new int[] { 8, 3 });
        this.put("org.apache.derby.iapi.types.SQLTimestamp", new int[] { 12, 2 });
        this.put("org.apache.derby.impl.store.raw.data.RecordId", new int[] { 8, 3 });
        this.put("org.apache.derby.iapi.types.UserType", new int[] { 0, 3 });
        this.put("org.apache.derby.iapi.types.CollatorSQLLongvarchar", new int[] { 4, 10 });
        this.put("org.apache.derby.iapi.types.DataType", new int[] { 0, 2 });
        this.put("org.apache.derby.iapi.types.SQLInteger", new int[] { 8, 2 });
        this.put("org.apache.derby.impl.store.access.btree.index.B2I", new int[] { 40, 6 });
        this.put("org.apache.derby.iapi.types.BinaryDecimal", new int[] { 4, 3 });
        this.put("org.apache.derby.impl.store.access.btree.BTree", new int[] { 28, 6 });
        this.put("org.apache.derby.iapi.types.SQLChar", new int[] { 4, 9 });
        this.put("org.apache.derby.iapi.types.SQLTinyint", new int[] { 5, 2 });
        this.put("org.apache.derby.iapi.types.CollatorSQLChar", new int[] { 4, 10 });
        this.put("org.apache.derby.iapi.types.SQLTime", new int[] { 8, 2 });
        this.put("org.apache.derby.iapi.types.SQLClob", new int[] { 4, 11 });
        this.put("org.apache.derby.iapi.types.SQLBinary", new int[] { 4, 5 });
        this.put("org.apache.derby.iapi.types.SQLVarchar", new int[] { 4, 9 });
        this.put("org.apache.derby.iapi.types.SQLVarbit", new int[] { 4, 5 });
        this.put("org.apache.derby.iapi.types.SQLDate", new int[] { 4, 2 });
        this.put("org.apache.derby.impl.store.access.StorableFormatId", new int[] { 4, 2 });
        this.put("org.apache.derby.iapi.types.NumberDataType", new int[] { 0, 2 });
        this.put("org.apache.derby.iapi.types.CollatorSQLClob", new int[] { 4, 12 });
        this.put("org.apache.derby.iapi.types.XML", new int[] { 8, 4 });
        this.put("org.apache.derby.impl.store.access.conglomerate.GenericConglomerate", new int[] { 0, 2 });
        this.put("org.apache.derby.iapi.types.SQLDecimal", new int[] { 4, 4 });
        this.put("org.apache.derby.iapi.types.SQLBoolean", new int[] { 12, 2 });
        this.put("org.apache.derby.iapi.types.CollatorSQLVarchar", new int[] { 4, 10 });
        this.put("org.apache.derby.iapi.types.SQLRef", new int[] { 0, 3 });
        this.put("org.apache.derby.iapi.types.SQLDouble", new int[] { 12, 2 });
        this.put("java.lang.ref.WeakReference", new int[] { 0, 6 });
        this.put("org.apache.derby.impl.store.access.heap.Heap", new int[] { 8, 5 });
        this.put("java.math.BigDecimal", new int[] { 16, 4 });
        this.put("org.apache.derby.impl.store.access.btree.index.B2I_v10_2", new int[] { 40, 6 });
        this.put("org.apache.derby.iapi.types.SQLSmallint", new int[] { 8, 2 });
        this.put("org.apache.derby.impl.store.access.UTF", new int[] { 0, 3 });
        this.put("java.util.GregorianCalendar", new int[] { 76, 11 });
        this.put("org.apache.derby.iapi.store.raw.ContainerKey", new int[] { 16, 2 });
        this.put("org.apache.derby.impl.store.access.btree.index.B2I_10_3", new int[] { 40, 6 });
        this.put("org.apache.derby.iapi.types.SQLReal", new int[] { 8, 2 });
        this.put("org.apache.derby.iapi.types.BigIntegerDecimal", new int[] { 4, 3 });
        this.put("org.apache.derby.impl.services.cache.CachedItem", new int[] { 24, 3 });
        this.put("org.apache.derby.impl.store.access.heap.Heap_v10_2", new int[] { 8, 5 });
        this.put("org.apache.derby.iapi.types.SQLLongint", new int[] { 12, 2 });
    }
}
