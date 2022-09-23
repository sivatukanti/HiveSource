// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import java.util.ArrayList;

public class DynamicSerDeTypeList extends DynamicSerDeTypeBase
{
    private static final int FD_TYPE = 0;
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public boolean isList() {
        return true;
    }
    
    @Override
    public Class getRealType() {
        return ArrayList.class;
    }
    
    public DynamicSerDeTypeList(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeList(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    public DynamicSerDeTypeBase getElementType() {
        return ((DynamicSerDeFieldType)this.jjtGetChild(0)).getMyType();
    }
    
    @Override
    public String toString() {
        return "array<" + this.getElementType().toString() + ">";
    }
    
    @Override
    public ArrayList<Object> deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final TList thelist = iprot.readListBegin();
        if (thelist == null) {
            return null;
        }
        ArrayList<Object> deserializeReuse;
        if (reuse != null) {
            deserializeReuse = (ArrayList<Object>)reuse;
            while (deserializeReuse.size() > thelist.size) {
                deserializeReuse.remove(deserializeReuse.size() - 1);
            }
        }
        else {
            deserializeReuse = new ArrayList<Object>();
        }
        deserializeReuse.ensureCapacity(thelist.size);
        for (int i = 0; i < thelist.size; ++i) {
            if (i + 1 > deserializeReuse.size()) {
                deserializeReuse.add(this.getElementType().deserialize(null, iprot));
            }
            else {
                deserializeReuse.set(i, this.getElementType().deserialize(deserializeReuse.get(i), iprot));
            }
        }
        iprot.readListEnd();
        return deserializeReuse;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final ListObjectInspector loi = (ListObjectInspector)oi;
        final ObjectInspector elementObjectInspector = loi.getListElementObjectInspector();
        final DynamicSerDeTypeBase mt = this.getElementType();
        final WriteNullsProtocol nullProtocol = (oprot instanceof WriteNullsProtocol) ? oprot : null;
        if (o instanceof List) {
            final List<?> list = (List<?>)o;
            oprot.writeListBegin(new TList(mt.getType(), list.size()));
            for (final Object element : list) {
                if (element == null) {
                    assert nullProtocol != null;
                    nullProtocol.writeNull();
                }
                else {
                    mt.serialize(element, elementObjectInspector, oprot);
                }
            }
        }
        else {
            final Object[] list2 = (Object[])o;
            oprot.writeListBegin(new TList(mt.getType(), list2.length));
            for (final Object element2 : list2) {
                if (element2 == null && nullProtocol != null) {
                    assert nullProtocol != null;
                    nullProtocol.writeNull();
                }
                else {
                    mt.serialize(element2, elementObjectInspector, oprot);
                }
            }
        }
        oprot.writeListEnd();
    }
    
    @Override
    public byte getType() {
        return 15;
    }
}
