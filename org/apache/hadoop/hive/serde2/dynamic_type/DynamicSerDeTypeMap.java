// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.HashMap;
import org.apache.thrift.protocol.TProtocol;
import java.util.Map;
import java.util.Collections;
import org.apache.thrift.protocol.TMap;

public class DynamicSerDeTypeMap extends DynamicSerDeTypeBase
{
    private final byte FD_KEYTYPE = 0;
    private final byte FD_VALUETYPE = 1;
    TMap serializeMap;
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public boolean isMap() {
        return true;
    }
    
    @Override
    public Class getRealType() {
        try {
            final Class c = this.getKeyType().getRealType();
            final Class c2 = this.getValueType().getRealType();
            final Object o = c.newInstance();
            final Object o2 = c2.newInstance();
            final Map<?, ?> l = Collections.singletonMap(o, o2);
            return l.getClass();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public DynamicSerDeTypeMap(final int i) {
        super(i);
        this.serializeMap = null;
    }
    
    public DynamicSerDeTypeMap(final thrift_grammar p, final int i) {
        super(p, i);
        this.serializeMap = null;
    }
    
    public DynamicSerDeTypeBase getKeyType() {
        return ((DynamicSerDeFieldType)this.jjtGetChild(0)).getMyType();
    }
    
    public DynamicSerDeTypeBase getValueType() {
        return ((DynamicSerDeFieldType)this.jjtGetChild(1)).getMyType();
    }
    
    @Override
    public String toString() {
        return "map<" + this.getKeyType().toString() + "," + this.getValueType().toString() + ">";
    }
    
    @Override
    public Map<Object, Object> deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        HashMap<Object, Object> deserializeReuse;
        if (reuse != null) {
            deserializeReuse = (HashMap<Object, Object>)reuse;
            deserializeReuse.clear();
        }
        else {
            deserializeReuse = new HashMap<Object, Object>();
        }
        final TMap themap = iprot.readMapBegin();
        if (themap == null) {
            return null;
        }
        for (int mapSize = themap.size, i = 0; i < mapSize; ++i) {
            final Object key = this.getKeyType().deserialize(null, iprot);
            final Object value = this.getValueType().deserialize(null, iprot);
            deserializeReuse.put(key, value);
        }
        iprot.readMapEnd();
        return deserializeReuse;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final DynamicSerDeTypeBase keyType = this.getKeyType();
        final DynamicSerDeTypeBase valueType = this.getValueType();
        final WriteNullsProtocol nullProtocol = (oprot instanceof WriteNullsProtocol) ? oprot : null;
        assert oi.getCategory() == ObjectInspector.Category.MAP;
        final MapObjectInspector moi = (MapObjectInspector)oi;
        final ObjectInspector koi = moi.getMapKeyObjectInspector();
        final ObjectInspector voi = moi.getMapValueObjectInspector();
        final Map<?, ?> map = moi.getMap(o);
        oprot.writeMapBegin(this.serializeMap = new TMap(keyType.getType(), valueType.getType(), map.size()));
        for (final Object element : map.entrySet()) {
            final Map.Entry it = (Map.Entry)element;
            final Object key = it.getKey();
            final Object value = it.getValue();
            keyType.serialize(key, koi, oprot);
            if (value == null) {
                assert nullProtocol != null;
                nullProtocol.writeNull();
            }
            else {
                valueType.serialize(value, voi, oprot);
            }
        }
        oprot.writeMapEnd();
    }
    
    @Override
    public byte getType() {
        return 13;
    }
}
