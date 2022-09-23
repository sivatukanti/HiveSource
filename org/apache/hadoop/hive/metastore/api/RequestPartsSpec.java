// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.TBase;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TEnum;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.protocol.TProtocolException;
import java.util.Iterator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.ArrayList;
import org.apache.thrift.protocol.TProtocol;
import java.util.List;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.TUnion;

public class RequestPartsSpec extends TUnion<RequestPartsSpec, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAMES_FIELD_DESC;
    private static final TField EXPRS_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public RequestPartsSpec() {
    }
    
    public RequestPartsSpec(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public RequestPartsSpec(final RequestPartsSpec other) {
        super(other);
    }
    
    @Override
    public RequestPartsSpec deepCopy() {
        return new RequestPartsSpec(this);
    }
    
    public static RequestPartsSpec names(final List<String> value) {
        final RequestPartsSpec x = new RequestPartsSpec();
        x.setNames(value);
        return x;
    }
    
    public static RequestPartsSpec exprs(final List<DropPartitionsExpr> value) {
        final RequestPartsSpec x = new RequestPartsSpec();
        x.setExprs(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case NAMES: {
                if (value instanceof List) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type List<String> for field 'names', but got " + value.getClass().getSimpleName());
            }
            case EXPRS: {
                if (value instanceof List) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type List<DropPartitionsExpr> for field 'exprs', but got " + value.getClass().getSimpleName());
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected Object standardSchemeReadValue(final TProtocol iprot, final TField field) throws TException {
        final _Fields setField = _Fields.findByThriftId(field.id);
        if (setField == null) {
            return null;
        }
        switch (setField) {
            case NAMES: {
                if (field.type == RequestPartsSpec.NAMES_FIELD_DESC.type) {
                    final TList _list404 = iprot.readListBegin();
                    final List<String> names = new ArrayList<String>(_list404.size);
                    for (int _i405 = 0; _i405 < _list404.size; ++_i405) {
                        final String _elem406 = iprot.readString();
                        names.add(_elem406);
                    }
                    iprot.readListEnd();
                    return names;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case EXPRS: {
                if (field.type == RequestPartsSpec.EXPRS_FIELD_DESC.type) {
                    final TList _list405 = iprot.readListBegin();
                    final List<DropPartitionsExpr> exprs = new ArrayList<DropPartitionsExpr>(_list405.size);
                    for (int _i406 = 0; _i406 < _list405.size; ++_i406) {
                        final DropPartitionsExpr _elem407 = new DropPartitionsExpr();
                        _elem407.read(iprot);
                        exprs.add(_elem407);
                    }
                    iprot.readListEnd();
                    return exprs;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void standardSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case NAMES: {
                final List<String> names = (List<String>)this.value_;
                oprot.writeListBegin(new TList((byte)11, names.size()));
                for (final String _iter410 : names) {
                    oprot.writeString(_iter410);
                }
                oprot.writeListEnd();
            }
            case EXPRS: {
                final List<DropPartitionsExpr> exprs = (List<DropPartitionsExpr>)this.value_;
                oprot.writeListBegin(new TList((byte)12, exprs.size()));
                for (final DropPartitionsExpr _iter411 : exprs) {
                    _iter411.write(oprot);
                }
                oprot.writeListEnd();
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected Object tupleSchemeReadValue(final TProtocol iprot, final short fieldID) throws TException {
        final _Fields setField = _Fields.findByThriftId(fieldID);
        if (setField == null) {
            throw new TProtocolException("Couldn't find a field with field id " + fieldID);
        }
        switch (setField) {
            case NAMES: {
                final TList _list412 = iprot.readListBegin();
                final List<String> names = new ArrayList<String>(_list412.size);
                for (int _i413 = 0; _i413 < _list412.size; ++_i413) {
                    final String _elem414 = iprot.readString();
                    names.add(_elem414);
                }
                iprot.readListEnd();
                return names;
            }
            case EXPRS: {
                final TList _list413 = iprot.readListBegin();
                final List<DropPartitionsExpr> exprs = new ArrayList<DropPartitionsExpr>(_list413.size);
                for (int _i414 = 0; _i414 < _list413.size; ++_i414) {
                    final DropPartitionsExpr _elem415 = new DropPartitionsExpr();
                    _elem415.read(iprot);
                    exprs.add(_elem415);
                }
                iprot.readListEnd();
                return exprs;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case NAMES: {
                final List<String> names = (List<String>)this.value_;
                oprot.writeListBegin(new TList((byte)11, names.size()));
                for (final String _iter418 : names) {
                    oprot.writeString(_iter418);
                }
                oprot.writeListEnd();
            }
            case EXPRS: {
                final List<DropPartitionsExpr> exprs = (List<DropPartitionsExpr>)this.value_;
                oprot.writeListBegin(new TList((byte)12, exprs.size()));
                for (final DropPartitionsExpr _iter419 : exprs) {
                    _iter419.write(oprot);
                }
                oprot.writeListEnd();
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case NAMES: {
                return RequestPartsSpec.NAMES_FIELD_DESC;
            }
            case EXPRS: {
                return RequestPartsSpec.EXPRS_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return RequestPartsSpec.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public List<String> getNames() {
        if (this.getSetField() == _Fields.NAMES) {
            return (List<String>)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'names' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setNames(final List<String> value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.NAMES;
        this.value_ = value;
    }
    
    public List<DropPartitionsExpr> getExprs() {
        if (this.getSetField() == _Fields.EXPRS) {
            return (List<DropPartitionsExpr>)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'exprs' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setExprs(final List<DropPartitionsExpr> value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.EXPRS;
        this.value_ = value;
    }
    
    public boolean isSetNames() {
        return this.setField_ == _Fields.NAMES;
    }
    
    public boolean isSetExprs() {
        return this.setField_ == _Fields.EXPRS;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof RequestPartsSpec && this.equals((RequestPartsSpec)other);
    }
    
    public boolean equals(final RequestPartsSpec other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final RequestPartsSpec other) {
        final int lastComparison = TBaseHelper.compareTo(((TUnion<T, Comparable>)this).getSetField(), ((TUnion<T, Comparable>)other).getSetField());
        if (lastComparison == 0) {
            return TBaseHelper.compareTo(this.getFieldValue(), other.getFieldValue());
        }
        return lastComparison;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getClass().getName());
        final TFieldIdEnum setField = ((TUnion<T, TFieldIdEnum>)this).getSetField();
        if (setField != null) {
            hcb.append(setField.getThriftFieldId());
            final Object value = this.getFieldValue();
            if (value instanceof TEnum) {
                hcb.append(((TEnum)this.getFieldValue()).getValue());
            }
            else {
                hcb.append(value);
            }
        }
        return hcb.toHashCode();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            this.write(new TCompactProtocol(new TIOStreamTransport(out)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("RequestPartsSpec");
        NAMES_FIELD_DESC = new TField("names", (byte)15, (short)1);
        EXPRS_FIELD_DESC = new TField("exprs", (byte)15, (short)2);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAMES, new FieldMetaData("names", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.EXPRS, new FieldMetaData("exprs", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, DropPartitionsExpr.class))));
        FieldMetaData.addStructMetaDataMap(RequestPartsSpec.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAMES((short)1, "names"), 
        EXPRS((short)2, "exprs");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAMES;
                }
                case 2: {
                    return _Fields.EXPRS;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static _Fields findByThriftIdOrThrow(final int fieldId) {
            final _Fields fields = findByThriftId(fieldId);
            if (fields == null) {
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            }
            return fields;
        }
        
        public static _Fields findByName(final String name) {
            return _Fields.byName.get(name);
        }
        
        private _Fields(final short thriftId, final String fieldName) {
            this._thriftId = thriftId;
            this._fieldName = fieldName;
        }
        
        @Override
        public short getThriftFieldId() {
            return this._thriftId;
        }
        
        @Override
        public String getFieldName() {
            return this._fieldName;
        }
        
        static {
            byName = new HashMap<String, _Fields>();
            for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                _Fields.byName.put(field.getFieldName(), field);
            }
        }
    }
}
