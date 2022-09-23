// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Map;
import java.util.Comparator;
import java.util.Arrays;

public class FullMapEqualComparer implements MapEqualComparer
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
        final Object[] sortedMapKeys1 = map1.keySet().toArray();
        Arrays.sort(sortedMapKeys1, new MapKeyComparator(mkoi1));
        final Object[] sortedMapKeys2 = map2.keySet().toArray();
        Arrays.sort(sortedMapKeys2, new MapKeyComparator(mkoi2));
        for (int i = 0; i < mapsize1; ++i) {
            final Object mk1 = sortedMapKeys1[i];
            final Object mk2 = sortedMapKeys2[i];
            int rc = ObjectInspectorUtils.compare(mk1, mkoi1, mk2, mkoi2, this);
            if (rc != 0) {
                return rc;
            }
            final Object mv1 = map1.get(mk1);
            final Object mv2 = map2.get(mk2);
            rc = ObjectInspectorUtils.compare(mv1, mvoi1, mv2, mvoi2, this);
            if (rc != 0) {
                return rc;
            }
        }
        return 0;
    }
    
    private static class MapKeyComparator implements Comparator<Object>
    {
        private ObjectInspector oi;
        
        MapKeyComparator(final ObjectInspector oi) {
            this.oi = oi;
        }
        
        @Override
        public int compare(final Object o1, final Object o2) {
            return ObjectInspectorUtils.compare(o1, this.oi, o2, this.oi);
        }
    }
}
