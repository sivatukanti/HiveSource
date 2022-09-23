// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class ShowCompactResponse implements TBase<ShowCompactResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COMPACTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<ShowCompactResponseElement> compacts;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ShowCompactResponse() {
    }
    
    public ShowCompactResponse(final List<ShowCompactResponseElement> compacts) {
        this();
        this.compacts = compacts;
    }
    
    public ShowCompactResponse(final ShowCompactResponse other) {
        if (other.isSetCompacts()) {
            final List<ShowCompactResponseElement> __this__compacts = new ArrayList<ShowCompactResponseElement>();
            for (final ShowCompactResponseElement other_element : other.compacts) {
                __this__compacts.add(new ShowCompactResponseElement(other_element));
            }
            this.compacts = __this__compacts;
        }
    }
    
    @Override
    public ShowCompactResponse deepCopy() {
        return new ShowCompactResponse(this);
    }
    
    @Override
    public void clear() {
        this.compacts = null;
    }
    
    public int getCompactsSize() {
        return (this.compacts == null) ? 0 : this.compacts.size();
    }
    
    public Iterator<ShowCompactResponseElement> getCompactsIterator() {
        return (this.compacts == null) ? null : this.compacts.iterator();
    }
    
    public void addToCompacts(final ShowCompactResponseElement elem) {
        if (this.compacts == null) {
            this.compacts = new ArrayList<ShowCompactResponseElement>();
        }
        this.compacts.add(elem);
    }
    
    public List<ShowCompactResponseElement> getCompacts() {
        return this.compacts;
    }
    
    public void setCompacts(final List<ShowCompactResponseElement> compacts) {
        this.compacts = compacts;
    }
    
    public void unsetCompacts() {
        this.compacts = null;
    }
    
    public boolean isSetCompacts() {
        return this.compacts != null;
    }
    
    public void setCompactsIsSet(final boolean value) {
        if (!value) {
            this.compacts = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COMPACTS: {
                if (value == null) {
                    this.unsetCompacts();
                    break;
                }
                this.setCompacts((List<ShowCompactResponseElement>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COMPACTS: {
                return this.getCompacts();
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
            case COMPACTS: {
                return this.isSetCompacts();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ShowCompactResponse && this.equals((ShowCompactResponse)that);
    }
    
    public boolean equals(final ShowCompactResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_compacts = this.isSetCompacts();
        final boolean that_present_compacts = that.isSetCompacts();
        if (this_present_compacts || that_present_compacts) {
            if (!this_present_compacts || !that_present_compacts) {
                return false;
            }
            if (!this.compacts.equals(that.compacts)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_compacts = this.isSetCompacts();
        builder.append(present_compacts);
        if (present_compacts) {
            builder.append(this.compacts);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ShowCompactResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ShowCompactResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetCompacts()).compareTo(Boolean.valueOf(typedOther.isSetCompacts()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCompacts()) {
            lastComparison = TBaseHelper.compareTo(this.compacts, typedOther.compacts);
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
        ShowCompactResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ShowCompactResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShowCompactResponse(");
        boolean first = true;
        sb.append("compacts:");
        if (this.compacts == null) {
            sb.append("null");
        }
        else {
            sb.append(this.compacts);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetCompacts()) {
            throw new TProtocolException("Required field 'compacts' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("ShowCompactResponse");
        COMPACTS_FIELD_DESC = new TField("compacts", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ShowCompactResponseStandardSchemeFactory());
        ShowCompactResponse.schemes.put(TupleScheme.class, new ShowCompactResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COMPACTS, new FieldMetaData("compacts", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ShowCompactResponseElement.class))));
        FieldMetaData.addStructMetaDataMap(ShowCompactResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COMPACTS((short)1, "compacts");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COMPACTS;
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
    
    private static class ShowCompactResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactResponseStandardScheme getScheme() {
            return new ShowCompactResponseStandardScheme();
        }
    }
    
    private static class ShowCompactResponseStandardScheme extends StandardScheme<ShowCompactResponse>
    {
        @Override
        public void read(final TProtocol iprot, final ShowCompactResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list484 = iprot.readListBegin();
                            struct.compacts = (List<ShowCompactResponseElement>)new ArrayList(_list484.size);
                            for (int _i485 = 0; _i485 < _list484.size; ++_i485) {
                                final ShowCompactResponseElement _elem486 = new ShowCompactResponseElement();
                                _elem486.read(iprot);
                                struct.compacts.add(_elem486);
                            }
                            iprot.readListEnd();
                            struct.setCompactsIsSet(true);
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
        public void write(final TProtocol oprot, final ShowCompactResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ShowCompactResponse.STRUCT_DESC);
            if (struct.compacts != null) {
                oprot.writeFieldBegin(ShowCompactResponse.COMPACTS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.compacts.size()));
                for (final ShowCompactResponseElement _iter487 : struct.compacts) {
                    _iter487.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ShowCompactResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactResponseTupleScheme getScheme() {
            return new ShowCompactResponseTupleScheme();
        }
    }
    
    private static class ShowCompactResponseTupleScheme extends TupleScheme<ShowCompactResponse>
    {
        @Override
        public void write(final TProtocol prot, final ShowCompactResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.compacts.size());
            for (final ShowCompactResponseElement _iter488 : struct.compacts) {
                _iter488.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ShowCompactResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list489 = new TList((byte)12, iprot.readI32());
            struct.compacts = (List<ShowCompactResponseElement>)new ArrayList(_list489.size);
            for (int _i490 = 0; _i490 < _list489.size; ++_i490) {
                final ShowCompactResponseElement _elem491 = new ShowCompactResponseElement();
                _elem491.read(iprot);
                struct.compacts.add(_elem491);
            }
            struct.setCompactsIsSet(true);
        }
    }
}
