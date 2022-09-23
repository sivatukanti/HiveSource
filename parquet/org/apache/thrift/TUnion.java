// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import parquet.org.apache.thrift.protocol.TStruct;
import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TProtocol;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.nio.ByteBuffer;

public abstract class TUnion<T extends TUnion<?, ?>, F extends TFieldIdEnum> implements TBase<T, F>
{
    protected Object value_;
    protected F setField_;
    
    protected TUnion() {
        this.setField_ = null;
        this.value_ = null;
    }
    
    protected TUnion(final F setField, final Object value) {
        this.setFieldValue(setField, value);
    }
    
    protected TUnion(final TUnion<T, F> other) {
        if (!other.getClass().equals(this.getClass())) {
            throw new ClassCastException();
        }
        this.setField_ = other.setField_;
        this.value_ = deepCopyObject(other.value_);
    }
    
    private static Object deepCopyObject(final Object o) {
        if (o instanceof TBase) {
            return ((TBase)o).deepCopy();
        }
        if (o instanceof ByteBuffer) {
            return TBaseHelper.copyBinary((ByteBuffer)o);
        }
        if (o instanceof List) {
            return deepCopyList((List)o);
        }
        if (o instanceof Set) {
            return deepCopySet((Set)o);
        }
        if (o instanceof Map) {
            return deepCopyMap((Map<Object, Object>)o);
        }
        return o;
    }
    
    private static Map deepCopyMap(final Map<Object, Object> map) {
        final Map copy = new HashMap();
        for (final Map.Entry<Object, Object> entry : map.entrySet()) {
            copy.put(deepCopyObject(entry.getKey()), deepCopyObject(entry.getValue()));
        }
        return copy;
    }
    
    private static Set deepCopySet(final Set set) {
        final Set copy = new HashSet();
        for (final Object o : set) {
            copy.add(deepCopyObject(o));
        }
        return copy;
    }
    
    private static List deepCopyList(final List list) {
        final List copy = new ArrayList(list.size());
        for (final Object o : list) {
            copy.add(deepCopyObject(o));
        }
        return copy;
    }
    
    public F getSetField() {
        return this.setField_;
    }
    
    public Object getFieldValue() {
        return this.value_;
    }
    
    public Object getFieldValue(final F fieldId) {
        if (fieldId != this.setField_) {
            throw new IllegalArgumentException("Cannot get the value of field " + fieldId + " because union's set field is " + this.setField_);
        }
        return this.getFieldValue();
    }
    
    public Object getFieldValue(final int fieldId) {
        return this.getFieldValue(this.enumForId((short)fieldId));
    }
    
    public boolean isSet() {
        return this.setField_ != null;
    }
    
    public boolean isSet(final F fieldId) {
        return this.setField_ == fieldId;
    }
    
    public boolean isSet(final int fieldId) {
        return this.isSet(this.enumForId((short)fieldId));
    }
    
    public void read(final TProtocol iprot) throws TException {
        this.setField_ = null;
        this.value_ = null;
        iprot.readStructBegin();
        final TField field = iprot.readFieldBegin();
        this.value_ = this.readValue(iprot, field);
        if (this.value_ != null) {
            this.setField_ = this.enumForId(field.id);
        }
        iprot.readFieldEnd();
        iprot.readFieldBegin();
        iprot.readStructEnd();
    }
    
    public void setFieldValue(final F fieldId, final Object value) {
        this.checkType(fieldId, value);
        this.setField_ = fieldId;
        this.value_ = value;
    }
    
    public void setFieldValue(final int fieldId, final Object value) {
        this.setFieldValue(this.enumForId((short)fieldId), value);
    }
    
    public void write(final TProtocol oprot) throws TException {
        if (this.getSetField() == null || this.getFieldValue() == null) {
            throw new TProtocolException("Cannot write a TUnion with no set value!");
        }
        oprot.writeStructBegin(this.getStructDesc());
        oprot.writeFieldBegin(this.getFieldDesc(this.setField_));
        this.writeValue(oprot);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    protected abstract void checkType(final F p0, final Object p1) throws ClassCastException;
    
    protected abstract Object readValue(final TProtocol p0, final TField p1) throws TException;
    
    protected abstract void writeValue(final TProtocol p0) throws TException;
    
    protected abstract TStruct getStructDesc();
    
    protected abstract TField getFieldDesc(final F p0);
    
    protected abstract F enumForId(final short p0);
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(this.getClass().getSimpleName());
        sb.append(" ");
        if (this.getSetField() != null) {
            final Object v = this.getFieldValue();
            sb.append(this.getFieldDesc(this.getSetField()).name);
            sb.append(":");
            if (v instanceof ByteBuffer) {
                TBaseHelper.toString((ByteBuffer)v, sb);
            }
            else {
                sb.append(v.toString());
            }
        }
        sb.append(">");
        return sb.toString();
    }
    
    public final void clear() {
        this.setField_ = null;
        this.value_ = null;
    }
}
