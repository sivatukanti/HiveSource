// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
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
import java.util.Iterator;
import java.util.HashSet;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Set;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class HeartbeatTxnRangeResponse implements TBase<HeartbeatTxnRangeResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField ABORTED_FIELD_DESC;
    private static final TField NOSUCH_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Set<Long> aborted;
    private Set<Long> nosuch;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HeartbeatTxnRangeResponse() {
    }
    
    public HeartbeatTxnRangeResponse(final Set<Long> aborted, final Set<Long> nosuch) {
        this();
        this.aborted = aborted;
        this.nosuch = nosuch;
    }
    
    public HeartbeatTxnRangeResponse(final HeartbeatTxnRangeResponse other) {
        if (other.isSetAborted()) {
            final Set<Long> __this__aborted = new HashSet<Long>();
            for (final Long other_element : other.aborted) {
                __this__aborted.add(other_element);
            }
            this.aborted = __this__aborted;
        }
        if (other.isSetNosuch()) {
            final Set<Long> __this__nosuch = new HashSet<Long>();
            for (final Long other_element : other.nosuch) {
                __this__nosuch.add(other_element);
            }
            this.nosuch = __this__nosuch;
        }
    }
    
    @Override
    public HeartbeatTxnRangeResponse deepCopy() {
        return new HeartbeatTxnRangeResponse(this);
    }
    
    @Override
    public void clear() {
        this.aborted = null;
        this.nosuch = null;
    }
    
    public int getAbortedSize() {
        return (this.aborted == null) ? 0 : this.aborted.size();
    }
    
    public Iterator<Long> getAbortedIterator() {
        return (this.aborted == null) ? null : this.aborted.iterator();
    }
    
    public void addToAborted(final long elem) {
        if (this.aborted == null) {
            this.aborted = new HashSet<Long>();
        }
        this.aborted.add(elem);
    }
    
    public Set<Long> getAborted() {
        return this.aborted;
    }
    
    public void setAborted(final Set<Long> aborted) {
        this.aborted = aborted;
    }
    
    public void unsetAborted() {
        this.aborted = null;
    }
    
    public boolean isSetAborted() {
        return this.aborted != null;
    }
    
    public void setAbortedIsSet(final boolean value) {
        if (!value) {
            this.aborted = null;
        }
    }
    
    public int getNosuchSize() {
        return (this.nosuch == null) ? 0 : this.nosuch.size();
    }
    
    public Iterator<Long> getNosuchIterator() {
        return (this.nosuch == null) ? null : this.nosuch.iterator();
    }
    
    public void addToNosuch(final long elem) {
        if (this.nosuch == null) {
            this.nosuch = new HashSet<Long>();
        }
        this.nosuch.add(elem);
    }
    
    public Set<Long> getNosuch() {
        return this.nosuch;
    }
    
    public void setNosuch(final Set<Long> nosuch) {
        this.nosuch = nosuch;
    }
    
    public void unsetNosuch() {
        this.nosuch = null;
    }
    
    public boolean isSetNosuch() {
        return this.nosuch != null;
    }
    
    public void setNosuchIsSet(final boolean value) {
        if (!value) {
            this.nosuch = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case ABORTED: {
                if (value == null) {
                    this.unsetAborted();
                    break;
                }
                this.setAborted((Set<Long>)value);
                break;
            }
            case NOSUCH: {
                if (value == null) {
                    this.unsetNosuch();
                    break;
                }
                this.setNosuch((Set<Long>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case ABORTED: {
                return this.getAborted();
            }
            case NOSUCH: {
                return this.getNosuch();
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
            case ABORTED: {
                return this.isSetAborted();
            }
            case NOSUCH: {
                return this.isSetNosuch();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HeartbeatTxnRangeResponse && this.equals((HeartbeatTxnRangeResponse)that);
    }
    
    public boolean equals(final HeartbeatTxnRangeResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_aborted = this.isSetAborted();
        final boolean that_present_aborted = that.isSetAborted();
        if (this_present_aborted || that_present_aborted) {
            if (!this_present_aborted || !that_present_aborted) {
                return false;
            }
            if (!this.aborted.equals(that.aborted)) {
                return false;
            }
        }
        final boolean this_present_nosuch = this.isSetNosuch();
        final boolean that_present_nosuch = that.isSetNosuch();
        if (this_present_nosuch || that_present_nosuch) {
            if (!this_present_nosuch || !that_present_nosuch) {
                return false;
            }
            if (!this.nosuch.equals(that.nosuch)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_aborted = this.isSetAborted();
        builder.append(present_aborted);
        if (present_aborted) {
            builder.append(this.aborted);
        }
        final boolean present_nosuch = this.isSetNosuch();
        builder.append(present_nosuch);
        if (present_nosuch) {
            builder.append(this.nosuch);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HeartbeatTxnRangeResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HeartbeatTxnRangeResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetAborted()).compareTo(Boolean.valueOf(typedOther.isSetAborted()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAborted()) {
            lastComparison = TBaseHelper.compareTo(this.aborted, typedOther.aborted);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNosuch()).compareTo(Boolean.valueOf(typedOther.isSetNosuch()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNosuch()) {
            lastComparison = TBaseHelper.compareTo(this.nosuch, typedOther.nosuch);
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
        HeartbeatTxnRangeResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HeartbeatTxnRangeResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HeartbeatTxnRangeResponse(");
        boolean first = true;
        sb.append("aborted:");
        if (this.aborted == null) {
            sb.append("null");
        }
        else {
            sb.append(this.aborted);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("nosuch:");
        if (this.nosuch == null) {
            sb.append("null");
        }
        else {
            sb.append(this.nosuch);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetAborted()) {
            throw new TProtocolException("Required field 'aborted' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNosuch()) {
            throw new TProtocolException("Required field 'nosuch' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("HeartbeatTxnRangeResponse");
        ABORTED_FIELD_DESC = new TField("aborted", (byte)14, (short)1);
        NOSUCH_FIELD_DESC = new TField("nosuch", (byte)14, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HeartbeatTxnRangeResponseStandardSchemeFactory());
        HeartbeatTxnRangeResponse.schemes.put(TupleScheme.class, new HeartbeatTxnRangeResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.ABORTED, new FieldMetaData("aborted", (byte)1, new SetMetaData((byte)14, new FieldValueMetaData((byte)10))));
        tmpMap.put(_Fields.NOSUCH, new FieldMetaData("nosuch", (byte)1, new SetMetaData((byte)14, new FieldValueMetaData((byte)10))));
        FieldMetaData.addStructMetaDataMap(HeartbeatTxnRangeResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        ABORTED((short)1, "aborted"), 
        NOSUCH((short)2, "nosuch");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.ABORTED;
                }
                case 2: {
                    return _Fields.NOSUCH;
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
    
    private static class HeartbeatTxnRangeResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatTxnRangeResponseStandardScheme getScheme() {
            return new HeartbeatTxnRangeResponseStandardScheme();
        }
    }
    
    private static class HeartbeatTxnRangeResponseStandardScheme extends StandardScheme<HeartbeatTxnRangeResponse>
    {
        @Override
        public void read(final TProtocol iprot, final HeartbeatTxnRangeResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 14) {
                            final TSet _set468 = iprot.readSetBegin();
                            struct.aborted = (Set<Long>)new HashSet(2 * _set468.size);
                            for (int _i469 = 0; _i469 < _set468.size; ++_i469) {
                                final long _elem470 = iprot.readI64();
                                struct.aborted.add(_elem470);
                            }
                            iprot.readSetEnd();
                            struct.setAbortedIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 14) {
                            final TSet _set469 = iprot.readSetBegin();
                            struct.nosuch = (Set<Long>)new HashSet(2 * _set469.size);
                            for (int _i470 = 0; _i470 < _set469.size; ++_i470) {
                                final long _elem471 = iprot.readI64();
                                struct.nosuch.add(_elem471);
                            }
                            iprot.readSetEnd();
                            struct.setNosuchIsSet(true);
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
        public void write(final TProtocol oprot, final HeartbeatTxnRangeResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HeartbeatTxnRangeResponse.STRUCT_DESC);
            if (struct.aborted != null) {
                oprot.writeFieldBegin(HeartbeatTxnRangeResponse.ABORTED_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)10, struct.aborted.size()));
                for (final long _iter474 : struct.aborted) {
                    oprot.writeI64(_iter474);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            if (struct.nosuch != null) {
                oprot.writeFieldBegin(HeartbeatTxnRangeResponse.NOSUCH_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)10, struct.nosuch.size()));
                for (final long _iter475 : struct.nosuch) {
                    oprot.writeI64(_iter475);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HeartbeatTxnRangeResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatTxnRangeResponseTupleScheme getScheme() {
            return new HeartbeatTxnRangeResponseTupleScheme();
        }
    }
    
    private static class HeartbeatTxnRangeResponseTupleScheme extends TupleScheme<HeartbeatTxnRangeResponse>
    {
        @Override
        public void write(final TProtocol prot, final HeartbeatTxnRangeResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.aborted.size());
            for (final long _iter476 : struct.aborted) {
                oprot.writeI64(_iter476);
            }
            oprot.writeI32(struct.nosuch.size());
            for (final long _iter477 : struct.nosuch) {
                oprot.writeI64(_iter477);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HeartbeatTxnRangeResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TSet _set478 = new TSet((byte)10, iprot.readI32());
            struct.aborted = (Set<Long>)new HashSet(2 * _set478.size);
            for (int _i479 = 0; _i479 < _set478.size; ++_i479) {
                final long _elem480 = iprot.readI64();
                struct.aborted.add(_elem480);
            }
            struct.setAbortedIsSet(true);
            final TSet _set479 = new TSet((byte)10, iprot.readI32());
            struct.nosuch = (Set<Long>)new HashSet(2 * _set479.size);
            for (int _i480 = 0; _i480 < _set479.size; ++_i480) {
                final long _elem481 = iprot.readI64();
                struct.nosuch.add(_elem481);
            }
            struct.setNosuchIsSet(true);
        }
    }
}
