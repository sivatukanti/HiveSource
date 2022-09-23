// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
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

public class OpenTxnsResponse implements TBase<OpenTxnsResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TXN_IDS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<Long> txn_ids;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public OpenTxnsResponse() {
    }
    
    public OpenTxnsResponse(final List<Long> txn_ids) {
        this();
        this.txn_ids = txn_ids;
    }
    
    public OpenTxnsResponse(final OpenTxnsResponse other) {
        if (other.isSetTxn_ids()) {
            final List<Long> __this__txn_ids = new ArrayList<Long>();
            for (final Long other_element : other.txn_ids) {
                __this__txn_ids.add(other_element);
            }
            this.txn_ids = __this__txn_ids;
        }
    }
    
    @Override
    public OpenTxnsResponse deepCopy() {
        return new OpenTxnsResponse(this);
    }
    
    @Override
    public void clear() {
        this.txn_ids = null;
    }
    
    public int getTxn_idsSize() {
        return (this.txn_ids == null) ? 0 : this.txn_ids.size();
    }
    
    public Iterator<Long> getTxn_idsIterator() {
        return (this.txn_ids == null) ? null : this.txn_ids.iterator();
    }
    
    public void addToTxn_ids(final long elem) {
        if (this.txn_ids == null) {
            this.txn_ids = new ArrayList<Long>();
        }
        this.txn_ids.add(elem);
    }
    
    public List<Long> getTxn_ids() {
        return this.txn_ids;
    }
    
    public void setTxn_ids(final List<Long> txn_ids) {
        this.txn_ids = txn_ids;
    }
    
    public void unsetTxn_ids() {
        this.txn_ids = null;
    }
    
    public boolean isSetTxn_ids() {
        return this.txn_ids != null;
    }
    
    public void setTxn_idsIsSet(final boolean value) {
        if (!value) {
            this.txn_ids = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TXN_IDS: {
                if (value == null) {
                    this.unsetTxn_ids();
                    break;
                }
                this.setTxn_ids((List<Long>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TXN_IDS: {
                return this.getTxn_ids();
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
            case TXN_IDS: {
                return this.isSetTxn_ids();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof OpenTxnsResponse && this.equals((OpenTxnsResponse)that);
    }
    
    public boolean equals(final OpenTxnsResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_txn_ids = this.isSetTxn_ids();
        final boolean that_present_txn_ids = that.isSetTxn_ids();
        if (this_present_txn_ids || that_present_txn_ids) {
            if (!this_present_txn_ids || !that_present_txn_ids) {
                return false;
            }
            if (!this.txn_ids.equals(that.txn_ids)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_txn_ids = this.isSetTxn_ids();
        builder.append(present_txn_ids);
        if (present_txn_ids) {
            builder.append(this.txn_ids);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final OpenTxnsResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final OpenTxnsResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTxn_ids()).compareTo(Boolean.valueOf(typedOther.isSetTxn_ids()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxn_ids()) {
            lastComparison = TBaseHelper.compareTo(this.txn_ids, typedOther.txn_ids);
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
        OpenTxnsResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        OpenTxnsResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OpenTxnsResponse(");
        boolean first = true;
        sb.append("txn_ids:");
        if (this.txn_ids == null) {
            sb.append("null");
        }
        else {
            sb.append(this.txn_ids);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTxn_ids()) {
            throw new TProtocolException("Required field 'txn_ids' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("OpenTxnsResponse");
        TXN_IDS_FIELD_DESC = new TField("txn_ids", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new OpenTxnsResponseStandardSchemeFactory());
        OpenTxnsResponse.schemes.put(TupleScheme.class, new OpenTxnsResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TXN_IDS, new FieldMetaData("txn_ids", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)10))));
        FieldMetaData.addStructMetaDataMap(OpenTxnsResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TXN_IDS((short)1, "txn_ids");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TXN_IDS;
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
    
    private static class OpenTxnsResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public OpenTxnsResponseStandardScheme getScheme() {
            return new OpenTxnsResponseStandardScheme();
        }
    }
    
    private static class OpenTxnsResponseStandardScheme extends StandardScheme<OpenTxnsResponse>
    {
        @Override
        public void read(final TProtocol iprot, final OpenTxnsResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list444 = iprot.readListBegin();
                            struct.txn_ids = (List<Long>)new ArrayList(_list444.size);
                            for (int _i445 = 0; _i445 < _list444.size; ++_i445) {
                                final long _elem446 = iprot.readI64();
                                struct.txn_ids.add(_elem446);
                            }
                            iprot.readListEnd();
                            struct.setTxn_idsIsSet(true);
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
        public void write(final TProtocol oprot, final OpenTxnsResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(OpenTxnsResponse.STRUCT_DESC);
            if (struct.txn_ids != null) {
                oprot.writeFieldBegin(OpenTxnsResponse.TXN_IDS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)10, struct.txn_ids.size()));
                for (final long _iter447 : struct.txn_ids) {
                    oprot.writeI64(_iter447);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class OpenTxnsResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public OpenTxnsResponseTupleScheme getScheme() {
            return new OpenTxnsResponseTupleScheme();
        }
    }
    
    private static class OpenTxnsResponseTupleScheme extends TupleScheme<OpenTxnsResponse>
    {
        @Override
        public void write(final TProtocol prot, final OpenTxnsResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.txn_ids.size());
            for (final long _iter448 : struct.txn_ids) {
                oprot.writeI64(_iter448);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final OpenTxnsResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list449 = new TList((byte)10, iprot.readI32());
            struct.txn_ids = (List<Long>)new ArrayList(_list449.size);
            for (int _i450 = 0; _i450 < _list449.size; ++_i450) {
                final long _elem451 = iprot.readI64();
                struct.txn_ids.add(_elem451);
            }
            struct.setTxn_idsIsSet(true);
        }
    }
}
