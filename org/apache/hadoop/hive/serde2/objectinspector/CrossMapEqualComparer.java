// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Iterator;
import java.util.Map;

public class CrossMapEqualComparer implements MapEqualComparer
{
    @Override
    public int compare(final Object o1, final MapObjectInspector moi1, final Object o2, final MapObjectInspector moi2) {
        final int mapsize1 = moi1.getMapSize(o1);
        final int mapsize2 = moi2.getMapSize(o2);
        if (mapsize1 != mapsize2) {
            return mapsize1 - mapsize2;
        }
        final ObjectInspector mkoi1 = moi1.getMapKeyObjectInspector();
        final ObjectInspector mkoi2 = moi2.getMapKeyObjectInspector();
        final ObjectInspector mvoi1 = moi1.getMapValueObjectInspector();
        final ObjectInspector mvoi2 = moi2.getMapValueObjectInspector();
        final Map<?, ?> map1 = moi1.getMap(o1);
        final Map<?, ?> map2 = moi2.getMap(o2);
        for (final Object mk1 : map1.keySet()) {
            boolean notFound = true;
            for (final Object mk2 : map2.keySet()) {
                int rc = ObjectInspectorUtils.compare(mk1, mkoi1, mk2, mkoi2, this);
                if (rc != 0) {
                    continue;
                }
                notFound = false;
                final Object mv1 = map1.get(mk1);
                final Object mv2 = map2.get(mk2);
                rc = ObjectInspectorUtils.compare(mv1, mvoi1, mv2, mvoi2, this);
                if (rc != 0) {
                    return rc;
                }
                break;
            }
            if (notFound) {
                return 1;
            }
        }
        return 0;
    }
}
