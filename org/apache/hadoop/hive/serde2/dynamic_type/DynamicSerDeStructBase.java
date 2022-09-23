// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.thrift.protocol.TStruct;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.thrift.protocol.TProtocol;
import java.util.List;
import java.io.Serializable;

public abstract class DynamicSerDeStructBase extends DynamicSerDeTypeBase implements Serializable
{
    DynamicSerDeFieldList fieldList;
    
    public DynamicSerDeStructBase(final int i) {
        super(i);
    }
    
    public DynamicSerDeStructBase(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    public abstract DynamicSerDeFieldList getFieldList();
    
    @Override
    public void initialize() {
        (this.fieldList = this.getFieldList()).initialize();
    }
    
    @Override
    public boolean isPrimitive() {
        return false;
    }
    
    @Override
    public Class getRealType() {
        return List.class;
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        iprot.readStructBegin();
        final Object o = this.fieldList.deserialize(reuse, iprot);
        iprot.readStructEnd();
        return o;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        oprot.writeStructBegin(new TStruct(this.name));
        this.fieldList.serialize(o, oi, oprot);
        oprot.writeStructEnd();
    }
}
