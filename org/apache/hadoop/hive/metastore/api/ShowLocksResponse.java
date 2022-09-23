// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class ShowLocksResponse implements TBase<ShowLocksResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOCKS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<ShowLocksResponseElement> locks;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ShowLocksResponse() {
    }
    
    public ShowLocksResponse(final List<ShowLocksResponseElement> locks) {
        this();
        this.locks = locks;
    }
    
    public ShowLocksResponse(final ShowLocksResponse other) {
        if (other.isSetLocks()) {
            final List<ShowLocksResponseElement> __this__locks = new ArrayList<ShowLocksResponseElement>();
            for (final ShowLocksResponseElement other_element : other.locks) {
                __this__locks.add(new ShowLocksResponseElement(other_element));
            }
            this.locks = __this__locks;
        }
    }
    
    @Override
    public ShowLocksResponse deepCopy() {
        return new ShowLocksResponse(this);
    }
    
    @Override
    public void clear() {
        this.locks = null;
    }
    
    public int getLocksSize() {
        return (this.locks == null) ? 0 : this.locks.size();
    }
    
    public Iterator<ShowLocksResponseElement> getLocksIterator() {
        return (this.locks == null) ? null : this.locks.iterator();
    }
    
    public void addToLocks(final ShowLocksResponseElement elem) {
        if (this.locks == null) {
            this.locks = new ArrayList<ShowLocksResponseElement>();
        }
        this.locks.add(elem);
    }
    
    public List<ShowLocksResponseElement> getLocks() {
        return this.locks;
    }
    
    public void setLocks(final List<ShowLocksResponseElement> locks) {
        this.locks = locks;
    }
    
    public void unsetLocks() {
        this.locks = null;
    }
    
    public boolean isSetLocks() {
        return this.locks != null;
    }
    
    public void setLocksIsSet(final boolean value) {
        if (!value) {
            this.locks = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case LOCKS: {
                if (value == null) {
                    this.unsetLocks();
                    break;
                }
                this.setLocks((List<ShowLocksResponseElement>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LOCKS: {
                return this.getLocks();
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
            case LOCKS: {
                return this.isSetLocks();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ShowLocksResponse && this.equals((ShowLocksResponse)that);
    }
    
    public boolean equals(final ShowLocksResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_locks = this.isSetLocks();
        final boolean that_present_locks = that.isSetLocks();
        if (this_present_locks || that_present_locks) {
            if (!this_present_locks || !that_present_locks) {
                return false;
            }
            if (!this.locks.equals(that.locks)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_locks = this.isSetLocks();
        builder.append(present_locks);
        if (present_locks) {
            builder.append(this.locks);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ShowLocksResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ShowLocksResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetLocks()).compareTo(Boolean.valueOf(typedOther.isSetLocks()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLocks()) {
            lastComparison = TBaseHelper.compareTo(this.locks, typedOther.locks);
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
        ShowLocksResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ShowLocksResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShowLocksResponse(");
        boolean first = true;
        sb.append("locks:");
        if (this.locks == null) {
            sb.append("null");
        }
        else {
            sb.append(this.locks);
        }
        first = false;
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
        STRUCT_DESC = new TStruct("ShowLocksResponse");
        LOCKS_FIELD_DESC = new TField("locks", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ShowLocksResponseStandardSchemeFactory());
        ShowLocksResponse.schemes.put(TupleScheme.class, new ShowLocksResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOCKS, new FieldMetaData("locks", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, ShowLocksResponseElement.class))));
        FieldMetaData.addStructMetaDataMap(ShowLocksResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOCKS((short)1, "locks");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOCKS;
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
    
    private static class ShowLocksResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowLocksResponseStandardScheme getScheme() {
            return new ShowLocksResponseStandardScheme();
        }
    }
    
    private static class ShowLocksResponseStandardScheme extends StandardScheme<ShowLocksResponse>
    {
        @Override
        public void read(final TProtocol iprot, final ShowLocksResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list460 = iprot.readListBegin();
                            struct.locks = (List<ShowLocksResponseElement>)new ArrayList(_list460.size);
                            for (int _i461 = 0; _i461 < _list460.size; ++_i461) {
                                final ShowLocksResponseElement _elem462 = new ShowLocksResponseElement();
                                _elem462.read(iprot);
                                struct.locks.add(_elem462);
                            }
                            iprot.readListEnd();
                            struct.setLocksIsSet(true);
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
        public void write(final TProtocol oprot, final ShowLocksResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ShowLocksResponse.STRUCT_DESC);
            if (struct.locks != null) {
                oprot.writeFieldBegin(ShowLocksResponse.LOCKS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.locks.size()));
                for (final ShowLocksResponseElement _iter463 : struct.locks) {
                    _iter463.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ShowLocksResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowLocksResponseTupleScheme getScheme() {
            return new ShowLocksResponseTupleScheme();
        }
    }
    
    private static class ShowLocksResponseTupleScheme extends TupleScheme<ShowLocksResponse>
    {
        @Override
        public void write(final TProtocol prot, final ShowLocksResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetLocks()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetLocks()) {
                oprot.writeI32(struct.locks.size());
                for (final ShowLocksResponseElement _iter464 : struct.locks) {
                    _iter464.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ShowLocksResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                final TList _list465 = new TList((byte)12, iprot.readI32());
                struct.locks = (List<ShowLocksResponseElement>)new ArrayList(_list465.size);
                for (int _i466 = 0; _i466 < _list465.size; ++_i466) {
                    final ShowLocksResponseElement _elem467 = new ShowLocksResponseElement();
                    _elem467.read(iprot);
                    struct.locks.add(_elem467);
                }
                struct.setLocksIsSet(true);
            }
        }
    }
}
