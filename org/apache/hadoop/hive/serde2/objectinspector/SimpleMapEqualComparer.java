// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Iterator;
import java.util.Map;

public class SimpleMapEqualComparer implements MapEqualComparer
{
    @Override
    public int compare(final Object o1, final MapObjectInspector moi1, final Object o2, final MapObjectInspector moi2) {
        final int mapsize1 = moi1.getMapSize(o1);
        final int mapsize2 = moi2.getMapSize(o2);
        if (mapsize1 != mapsize2) {
            return mapsize1 - mapsize2;
        }
        final ObjectInspector mvoi1 = moi1.getMapValueObjectInspector();
        final ObjectInspector mvoi2 = moi2.getMapValueObjectInspector();
        final Map<?, ?> map1 = moi1.getMap(o1);
        for (final Object mk1 : map1.keySet()) {
            final int rc = ObjectInspectorUtils.compare(moi1.getMapValueElement(o1, mk1), mvoi1, moi2.getMapValueElement(o2, mk1), mvoi2, this);
            if (rc != 0) {
                return rc;
            }
        }
        return 0;
    }
}
