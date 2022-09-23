// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import org.apache.thrift.EncodingUtils;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TExecuteStatementReq implements TBase<TExecuteStatementReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField STATEMENT_FIELD_DESC;
    private static final TField CONF_OVERLAY_FIELD_DESC;
    private static final TField RUN_ASYNC_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TSessionHandle sessionHandle;
    private String statement;
    private Map<String, String> confOverlay;
    private boolean runAsync;
    private static final int __RUNASYNC_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TExecuteStatementReq() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.CONF_OVERLAY, _Fields.RUN_ASYNC };
        this.runAsync = false;
    }
    
    public TExecuteStatementReq(final TSessionHandle sessionHandle, final String statement) {
        this();
        this.sessionHandle = sessionHandle;
        this.statement = statement;
    }
    
    public TExecuteStatementReq(final TExecuteStatementReq other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.CONF_OVERLAY, _Fields.RUN_ASYNC };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
        }
        if (other.isSetStatement()) {
            this.statement = other.statement;
        }
        if (other.isSetConfOverlay()) {
            final Map<String, String> __this__confOverlay = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.confOverlay.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__confOverlay_copy_key = other_element_key;
                final String __this__confOverlay_copy_value = other_element_value;
                __this__confOverlay.put(__this__confOverlay_copy_key, __this__confOverlay_copy_value);
            }
            this.confOverlay = __this__confOverlay;
        }
        this.runAsync = other.runAsync;
    }
    
    @Override
    public TExecuteStatementReq deepCopy() {
        return new TExecuteStatementReq(this);
    }
    
    @Override
    public void clear() {
        this.sessionHandle = null;
        this.statement = null;
        this.confOverlay = null;
        this.runAsync = false;
    }
    
    public TSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }
    
    public void setSessionHandle(final TSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }
    
    public void unsetSessionHandle() {
        this.sessionHandle = null;
    }
    
    public boolean isSetSessionHandle() {
        return this.sessionHandle != null;
    }
    
    public void setSessionHandleIsSet(final boolean value) {
        if (!value) {
            this.sessionHandle = null;
        }
    }
    
    public String getStatement() {
        return this.statement;
    }
    
    public void setStatement(final String statement) {
        this.statement = statement;
    }
    
    public void unsetStatement() {
        this.statement = null;
    }
    
    public boolean isSetStatement() {
        return this.statement != null;
    }
    
    public void setStatementIsSet(final boolean value) {
        if (!value) {
            this.statement = null;
        }
    }
    
    public int getConfOverlaySize() {
        return (this.confOverlay == null) ? 0 : this.confOverlay.size();
    }
    
    public void putToConfOverlay(final String key, final String val) {
        if (this.confOverlay == null) {
            this.confOverlay = new HashMap<String, String>();
        }
        this.confOverlay.put(key, val);
    }
    
    public Map<String, String> getConfOverlay() {
        return this.confOverlay;
    }
    
    public void setConfOverlay(final Map<String, String> confOverlay) {
        this.confOverlay = confOverlay;
    }
    
    public void unsetConfOverlay() {
        this.confOverlay = null;
    }
    
    public boolean isSetConfOverlay() {
        return this.confOverlay != null;
    }
    
    public void setConfOverlayIsSet(final boolean value) {
        if (!value) {
            this.confOverlay = null;
        }
    }
    
    public boolean isRunAsync() {
        return this.runAsync;
    }
    
    public void setRunAsync(final boolean runAsync) {
        this.runAsync = runAsync;
        this.setRunAsyncIsSet(true);
    }
    
    public void unsetRunAsync() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetRunAsync() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setRunAsyncIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SESSION_HANDLE: {
                if (value == null) {
                    this.unsetSessionHandle();
                    break;
                }
                this.setSessionHandle((TSessionHandle)value);
                break;
            }
            case STATEMENT: {
                if (value == null) {
                    this.unsetStatement();
                    break;
                }
                this.setStatement((String)value);
                break;
            }
            case CONF_OVERLAY: {
                if (value == null) {
                    this.unsetConfOverlay();
                    break;
                }
                this.setConfOverlay((Map<String, String>)value);
                break;
            }
            case RUN_ASYNC: {
                if (value == null) {
                    this.unsetRunAsync();
                    break;
                }
                this.setRunAsync((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SESSION_HANDLE: {
                return this.getSessionHandle();
            }
            case STATEMENT: {
                return this.getStatement();
            }
            case CONF_OVERLAY: {
                return this.getConfOverlay();
            }
            case RUN_ASYNC: {
                return this.isRunAsync();
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
            case SESSION_HANDLE: {
                return this.isSetSessionHandle();
            }
            case STATEMENT: {
                return this.isSetStatement();
            }
            case CONF_OVERLAY: {
                return this.isSetConfOverlay();
            }
            case RUN_ASYNC: {
                return this.isSetRunAsync();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TExecuteStatementReq && this.equals((TExecuteStatementReq)that);
    }
    
    public boolean equals(final TExecuteStatementReq that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_sessionHandle = this.isSetSessionHandle();
        final boolean that_present_sessionHandle = that.isSetSessionHandle();
        if (this_present_sessionHandle || that_present_sessionHandle) {
            if (!this_present_sessionHandle || !that_present_sessionHandle) {
                return false;
            }
            if (!this.sessionHandle.equals(that.sessionHandle)) {
                return false;
            }
        }
        final boolean this_present_statement = this.isSetStatement();
        final boolean that_present_statement = that.isSetStatement();
        if (this_present_statement || that_present_statement) {
            if (!this_present_statement || !that_present_statement) {
                return false;
            }
            if (!this.statement.equals(that.statement)) {
                return false;
            }
        }
        final boolean this_present_confOverlay = this.isSetConfOverlay();
        final boolean that_present_confOverlay = that.isSetConfOverlay();
        if (this_present_confOverlay || that_present_confOverlay) {
            if (!this_present_confOverlay || !that_present_confOverlay) {
                return false;
            }
            if (!this.confOverlay.equals(that.confOverlay)) {
                return false;
            }
        }
        final boolean this_present_runAsync = this.isSetRunAsync();
        final boolean that_present_runAsync = that.isSetRunAsync();
        if (this_present_runAsync || that_present_runAsync) {
            if (!this_present_runAsync || !that_present_runAsync) {
                return false;
            }
            if (this.runAsync != that.runAsync) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_sessionHandle = this.isSetSessionHandle();
        builder.append(present_sessionHandle);
        if (present_sessionHandle) {
            builder.append(this.sessionHandle);
        }
        final boolean present_statement = this.isSetStatement();
        builder.append(present_statement);
        if (present_statement) {
            builder.append(this.statement);
        }
        final boolean present_confOverlay = this.isSetConfOverlay();
        builder.append(present_confOverlay);
        if (present_confOverlay) {
            builder.append(this.confOverlay);
        }
        final boolean present_runAsync = this.isSetRunAsync();
        builder.append(present_runAsync);
        if (present_runAsync) {
            builder.append(this.runAsync);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TExecuteStatementReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TExecuteStatementReq typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSessionHandle()).compareTo(Boolean.valueOf(typedOther.isSetSessionHandle()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSessionHandle()) {
            lastComparison = TBaseHelper.compareTo(this.sessionHandle, typedOther.sessionHandle);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStatement()).compareTo(Boolean.valueOf(typedOther.isSetStatement()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatement()) {
            lastComparison = TBaseHelper.compareTo(this.statement, typedOther.statement);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetConfOverlay()).compareTo(Boolean.valueOf(typedOther.isSetConfOverlay()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetConfOverlay()) {
            lastComparison = TBaseHelper.compareTo(this.confOverlay, typedOther.confOverlay);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRunAsync()).compareTo(Boolean.valueOf(typedOther.isSetRunAsync()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRunAsync()) {
            lastComparison = TBaseHelper.compareTo(this.runAsync, typedOther.runAsync);
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
        TExecuteStatementReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TExecuteStatementReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TExecuteStatementReq(");
        boolean first = true;
        sb.append("sessionHandle:");
        if (this.sessionHandle == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sessionHandle);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("statement:");
        if (this.statement == null) {
            sb.append("null");
        }
        else {
            sb.append(this.statement);
        }
        first = false;
        if (this.isSetConfOverlay()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("confOverlay:");
            if (this.confOverlay == null) {
                sb.append("null");
            }
            else {
                sb.append(this.confOverlay);
            }
            first = false;
        }
        if (this.isSetRunAsync()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("runAsync:");
            sb.append(this.runAsync);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSessionHandle()) {
            throw new TProtocolException("Required field 'sessionHandle' is unset! Struct:" + this.toString());
        }
        if (!this.isSetStatement()) {
            throw new TProtocolException("Required field 'statement' is unset! Struct:" + this.toString());
        }
        if (this.sessionHandle != null) {
            this.sessionHandle.validate();
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TExecuteStatementReq");
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)1);
        STATEMENT_FIELD_DESC = new TField("statement", (byte)11, (short)2);
        CONF_OVERLAY_FIELD_DESC = new TField("confOverlay", (byte)13, (short)3);
        RUN_ASYNC_FIELD_DESC = new TField("runAsync", (byte)2, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TExecuteStatementReqStandardSchemeFactory());
        TExecuteStatementReq.schemes.put(TupleScheme.class, new TExecuteStatementReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)1, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.STATEMENT, new FieldMetaData("statement", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CONF_OVERLAY, new FieldMetaData("confOverlay", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.RUN_ASYNC, new FieldMetaData("runAsync", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(TExecuteStatementReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_HANDLE((short)1, "sessionHandle"), 
        STATEMENT((short)2, "statement"), 
        CONF_OVERLAY((short)3, "confOverlay"), 
        RUN_ASYNC((short)4, "runAsync");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SESSION_HANDLE;
                }
                case 2: {
                    return _Fields.STATEMENT;
                }
                case 3: {
                    return _Fields.CONF_OVERLAY;
                }
                case 4: {
                    return _Fields.RUN_ASYNC;
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
    
    private static class TExecuteStatementReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TExecuteStatementReqStandardScheme getScheme() {
            return new TExecuteStatementReqStandardScheme();
        }
    }
    
    private static class TExecuteStatementReqStandardScheme extends StandardScheme<TExecuteStatementReq>
    {
        @Override
        public void read(final TProtocol iprot, final TExecuteStatementReq struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.sessionHandle = new TSessionHandle();
                            struct.sessionHandle.read(iprot);
                            struct.setSessionHandleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.statement = iprot.readString();
                            struct.setStatementIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 13) {
                            final TMap _map162 = iprot.readMapBegin();
                            struct.confOverlay = (Map<String, String>)new HashMap(2 * _map162.size);
                            for (int _i163 = 0; _i163 < _map162.size; ++_i163) {
                                final String _key164 = iprot.readString();
                                final String _val165 = iprot.readString();
                                struct.confOverlay.put(_key164, _val165);
                            }
                            iprot.readMapEnd();
                            struct.setConfOverlayIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 2) {
                            struct.runAsync = iprot.readBool();
                            struct.setRunAsyncIsSet(true);
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
        public void write(final TProtocol oprot, final TExecuteStatementReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TExecuteStatementReq.STRUCT_DESC);
            if (struct.sessionHandle != null) {
                oprot.writeFieldBegin(TExecuteStatementReq.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.statement != null) {
                oprot.writeFieldBegin(TExecuteStatementReq.STATEMENT_FIELD_DESC);
                oprot.writeString(struct.statement);
                oprot.writeFieldEnd();
            }
            if (struct.confOverlay != null && struct.isSetConfOverlay()) {
                oprot.writeFieldBegin(TExecuteStatementReq.CONF_OVERLAY_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.confOverlay.size()));
                for (final Map.Entry<String, String> _iter166 : struct.confOverlay.entrySet()) {
                    oprot.writeString(_iter166.getKey());
                    oprot.writeString(_iter166.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.isSetRunAsync()) {
                oprot.writeFieldBegin(TExecuteStatementReq.RUN_ASYNC_FIELD_DESC);
                oprot.writeBool(struct.runAsync);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TExecuteStatementReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TExecuteStatementReqTupleScheme getScheme() {
            return new TExecuteStatementReqTupleScheme();
        }
    }
    
    private static class TExecuteStatementReqTupleScheme extends TupleScheme<TExecuteStatementReq>
    {
        @Override
        public void write(final TProtocol prot, final TExecuteStatementReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionHandle.write(oprot);
            oprot.writeString(struct.statement);
            final BitSet optionals = new BitSet();
            if (struct.isSetConfOverlay()) {
                optionals.set(0);
            }
            if (struct.isSetRunAsync()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetConfOverlay()) {
                oprot.writeI32(struct.confOverlay.size());
                for (final Map.Entry<String, String> _iter167 : struct.confOverlay.entrySet()) {
                    oprot.writeString(_iter167.getKey());
                    oprot.writeString(_iter167.getValue());
                }
            }
            if (struct.isSetRunAsync()) {
                oprot.writeBool(struct.runAsync);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TExecuteStatementReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionHandle = new TSessionHandle();
            struct.sessionHandle.read(iprot);
            struct.setSessionHandleIsSet(true);
            struct.statement = iprot.readString();
            struct.setStatementIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                final TMap _map168 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.confOverlay = (Map<String, String>)new HashMap(2 * _map168.size);
                for (int _i169 = 0; _i169 < _map168.size; ++_i169) {
                    final String _key170 = iprot.readString();
                    final String _val171 = iprot.readString();
                    struct.confOverlay.put(_key170, _val171);
                }
                struct.setConfOverlayIsSet(true);
            }
            if (incoming.get(1)) {
                struct.runAsync = iprot.readBool();
                struct.setRunAsyncIsSet(true);
            }
        }
    }
}
