// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
import java.util.HashMap;
import org.apache.thrift.TFieldIdEnum;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class ShowCompactRequest implements TBase<ShowCompactRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ShowCompactRequest() {
    }
    
    public ShowCompactRequest(final ShowCompactRequest other) {
    }
    
    @Override
    public ShowCompactRequest deepCopy() {
        return new ShowCompactRequest(this);
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        final int n = ShowCompactRequest$1.$SwitchMap$org$apache$hadoop$hive$metastore$api$ShowCompactRequest$_Fields[field.ordinal()];
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        final int n = ShowCompactRequest$1.$SwitchMap$org$apache$hadoop$hive$metastore$api$ShowCompactRequest$_Fields[field.ordinal()];
        throw new IllegalStateException();
    }
    
    @Override
    public boolean isSet(final _Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        final int n = ShowCompactRequest$1.$SwitchMap$org$apache$hadoop$hive$metastore$api$ShowCompactRequest$_Fields[field.ordinal()];
        throw new IllegalStateException();
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ShowCompactRequest && this.equals((ShowCompactRequest)that);
    }
    
    public boolean equals(final ShowCompactRequest that) {
        return that != null;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ShowCompactRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        final int lastComparison = 0;
        final ShowCompactRequest typedOther = other;
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        ShowCompactRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ShowCompactRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShowCompactRequest(");
        final boolean first = true;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
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
        STRUCT_DESC = new TStruct("ShowCompactRequest");
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ShowCompactRequestStandardSchemeFactory());
        ShowCompactRequest.schemes.put(TupleScheme.class, new ShowCompactRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        FieldMetaData.addStructMetaDataMap(ShowCompactRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            return null;
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
    
    private static class ShowCompactRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactRequestStandardScheme getScheme() {
            return new ShowCompactRequestStandardScheme();
        }
    }
    
    private static class ShowCompactRequestStandardScheme extends StandardScheme<ShowCompactRequest>
    {
        @Override
        public void read(final TProtocol iprot, final ShowCompactRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                final short id = schemeField.id;
                TProtocolUtil.skip(iprot, schemeField.type);
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();
            struct.validate();
        }
        
        @Override
        public void write(final TProtocol oprot, final ShowCompactRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ShowCompactRequest.STRUCT_DESC);
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ShowCompactRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactRequestTupleScheme getScheme() {
            return new ShowCompactRequestTupleScheme();
        }
    }
    
    private static class ShowCompactRequestTupleScheme extends TupleScheme<ShowCompactRequest>
    {
        @Override
        public void write(final TProtocol prot, final ShowCompactRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
        }
        
        @Override
        public void read(final TProtocol prot, final ShowCompactRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
        }
    }
}
