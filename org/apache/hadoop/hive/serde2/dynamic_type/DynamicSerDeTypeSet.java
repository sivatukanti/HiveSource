// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.HashSet;
import org.apache.thrift.protocol.TProtocol;
import java.util.Set;
import java.util.Collections;
import org.apache.thrift.protocol.TSet;

public class DynamicSerDeTypeSet extends DynamicSerDeTypeBase
{
    private static final int FD_TYPE = 0;
    TSet tset;
    
    public DynamicSerDeTypeSet(final int i) {
        super(i);
        this.tset = null;
    }
    
    public DynamicSerDeTypeSet(final thrift_grammar p, final int i) {
        super(p, i);
        this.tset = null;
    }
    
    @Override
    public Class getRealType() {
        try {
            final Class c = this.getElementType().getRealType();
            final Object o = c.newInstance();
            final Set<?> l = Collections.singleton(o);
            return l.getClass();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public DynamicSerDeTypeBase getElementType() {
        return ((DynamicSerDeFieldType)this.jjtGetChild(0)).getMyType();
    }
    
    @Override
    public String toString() {
        return "set<" + this.getElementType().toString() + ">";
    }
    
    @Override
    public byte getType() {
        return 14;
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final TSet theset = iprot.readSetBegin();
        if (theset == null) {
            return null;
        }
        Set<Object> result;
        if (reuse != null) {
            result = (Set<Object>)reuse;
            result.clear();
        }
        else {
            result = new HashSet<Object>();
        }
        for (int i = 0; i < theset.size; ++i) {
            final Object elem = this.getElementType().deserialize(null, iprot);
            result.add(elem);
        }
        iprot.readSetEnd();
        return result;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final ListObjectInspector loi = (ListObjectInspector)oi;
        final Set<Object> set = (Set<Object>)o;
        final DynamicSerDeTypeBase mt = this.getElementType();
        oprot.writeSetBegin(this.tset = new TSet(mt.getType(), set.size()));
        for (final Object element : set) {
            mt.serialize(element, loi.getListElementObjectInspector(), oprot);
        }
        oprot.writeSetEnd();
    }
}
