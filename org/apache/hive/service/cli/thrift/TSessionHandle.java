// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TSessionHandle implements TBase<TSessionHandle, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_ID_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private THandleIdentifier sessionId;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TSessionHandle() {
    }
    
    public TSessionHandle(final THandleIdentifier sessionId) {
        this();
        this.sessionId = sessionId;
    }
    
    public TSessionHandle(final TSessionHandle other) {
        if (other.isSetSessionId()) {
            this.sessionId = new THandleIdentifier(other.sessionId);
        }
    }
    
    @Override
    public TSessionHandle deepCopy() {
        return new TSessionHandle(this);
    }
    
    @Override
    public void clear() {
        this.sessionId = null;
    }
    
    public THandleIdentifier getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final THandleIdentifier sessionId) {
        this.sessionId = sessionId;
    }
    
    public void unsetSessionId() {
        this.sessionId = null;
    }
    
    public boolean isSetSessionId() {
        return this.sessionId != null;
    }
    
    public void setSessionIdIsSet(final boolean value) {
        if (!value) {
            this.sessionId = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SESSION_ID: {
                if (value == null) {
                    this.unsetSessionId();
                    break;
                }
                this.setSessionId((THandleIdentifier)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SESSION_ID: {
                return this.getSessionId();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean isSet(final _Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        switch (field) {
            case SESSION_ID: {
                return this.isSetSessionId();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TSessionHandle && this.equals((TSessionHandle)that);
    }
    
    public boolean equals(final TSessionHandle that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_sessionId = this.isSetSessionId();
        final boolean that_present_sessionId = that.isSetSessionId();
        if (this_present_sessionId || that_present_sessionId) {
            if (!this_present_sessionId || !that_present_sessionId) {
                return false;
            }
            if (!this.sessionId.equals(that.sessionId)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_sessionId = this.isSetSessionId();
        builder.append(present_sessionId);
        if (present_sessionId) {
            builder.append(this.sessionId);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TSessionHandle other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TSessionHandle typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSessionId()).compareTo(Boolean.valueOf(typedOther.isSetSessionId()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSessionId()) {
            lastComparison = TBaseHelper.compareTo(this.sessionId, typedOther.sessionId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        TSessionHandle.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TSessionHandle.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TSessionHandle(");
        boolean first = true;
        sb.append("sessionId:");
        if (this.sessionId == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sessionId);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSessionId()) {
            throw new TProtocolException("Required field 'sessionId' is unset! Struct:" + this.toString());
        }
        if (this.sessionId != null) {
            this.sessionId.validate();
        }
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
        STRUCT_DESC = new TStruct("TSessionHandle");
        SESSION_ID_FIELD_DESC = new TField("sessionId", (byte)12, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TSessionHandleStandardSchemeFactory());
        TSessionHandle.schemes.put(TupleScheme.class, new TSessionHandleTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_ID, new FieldMetaData("sessionId", (byte)1, new StructMetaData((byte)12, THandleIdentifier.class)));
        FieldMetaData.addStructMetaDataMap(TSessionHandle.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_ID((short)1, "sessionId");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SESSION_ID;
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
    
    private static class TSessionHandleStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TSessionHandleStandardScheme getScheme() {
            return new TSessionHandleStandardScheme();
        }
    }
    
    private static class TSessionHandleStandardScheme extends StandardScheme<TSessionHandle>
    {
        @Override
        public void read(final TProtocol iprot, final TSessionHandle struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.sessionId = new THandleIdentifier();
                            struct.sessionId.read(iprot);
                            struct.setSessionIdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    default: {
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();
            struct.validate();
        }
        
        @Override
        public void write(final TProtocol oprot, final TSessionHandle struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TSessionHandle.STRUCT_DESC);
            if (struct.sessionId != null) {
                oprot.writeFieldBegin(TSessionHandle.SESSION_ID_FIELD_DESC);
                struct.sessionId.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TSessionHandleTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TSessionHandleTupleScheme getScheme() {
            return new TSessionHandleTupleScheme();
        }
    }
    
    private static class TSessionHandleTupleScheme extends TupleScheme<TSessionHandle>
    {
        @Override
        public void write(final TProtocol prot, final TSessionHandle struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionId.write(oprot);
        }
        
        @Override
        public void read(final TProtocol prot, final TSessionHandle struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionId = new THandleIdentifier();
            struct.sessionId.read(iprot);
            struct.setSessionIdIsSet(true);
        }
    }
}
