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

public class InsertEventRequestData implements TBase<InsertEventRequestData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FILES_ADDED_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<String> filesAdded;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public InsertEventRequestData() {
    }
    
    public InsertEventRequestData(final List<String> filesAdded) {
        this();
        this.filesAdded = filesAdded;
    }
    
    public InsertEventRequestData(final InsertEventRequestData other) {
        if (other.isSetFilesAdded()) {
            final List<String> __this__filesAdded = new ArrayList<String>();
            for (final String other_element : other.filesAdded) {
                __this__filesAdded.add(other_element);
            }
            this.filesAdded = __this__filesAdded;
        }
    }
    
    @Override
    public InsertEventRequestData deepCopy() {
        return new InsertEventRequestData(this);
    }
    
    @Override
    public void clear() {
        this.filesAdded = null;
    }
    
    public int getFilesAddedSize() {
        return (this.filesAdded == null) ? 0 : this.filesAdded.size();
    }
    
    public Iterator<String> getFilesAddedIterator() {
        return (this.filesAdded == null) ? null : this.filesAdded.iterator();
    }
    
    public void addToFilesAdded(final String elem) {
        if (this.filesAdded == null) {
            this.filesAdded = new ArrayList<String>();
        }
        this.filesAdded.add(elem);
    }
    
    public List<String> getFilesAdded() {
        return this.filesAdded;
    }
    
    public void setFilesAdded(final List<String> filesAdded) {
        this.filesAdded = filesAdded;
    }
    
    public void unsetFilesAdded() {
        this.filesAdded = null;
    }
    
    public boolean isSetFilesAdded() {
        return this.filesAdded != null;
    }
    
    public void setFilesAddedIsSet(final boolean value) {
        if (!value) {
            this.filesAdded = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FILES_ADDED: {
                if (value == null) {
                    this.unsetFilesAdded();
                    break;
                }
                this.setFilesAdded((List<String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FILES_ADDED: {
                return this.getFilesAdded();
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
            case FILES_ADDED: {
                return this.isSetFilesAdded();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof InsertEventRequestData && this.equals((InsertEventRequestData)that);
    }
    
    public boolean equals(final InsertEventRequestData that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_filesAdded = this.isSetFilesAdded();
        final boolean that_present_filesAdded = that.isSetFilesAdded();
        if (this_present_filesAdded || that_present_filesAdded) {
            if (!this_present_filesAdded || !that_present_filesAdded) {
                return false;
            }
            if (!this.filesAdded.equals(that.filesAdded)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_filesAdded = this.isSetFilesAdded();
        builder.append(present_filesAdded);
        if (present_filesAdded) {
            builder.append(this.filesAdded);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final InsertEventRequestData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final InsertEventRequestData typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetFilesAdded()).compareTo(Boolean.valueOf(typedOther.isSetFilesAdded()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFilesAdded()) {
            lastComparison = TBaseHelper.compareTo(this.filesAdded, typedOther.filesAdded);
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
        InsertEventRequestData.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        InsertEventRequestData.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InsertEventRequestData(");
        boolean first = true;
        sb.append("filesAdded:");
        if (this.filesAdded == null) {
            sb.append("null");
        }
        else {
            sb.append(this.filesAdded);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetFilesAdded()) {
            throw new TProtocolException("Required field 'filesAdded' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("InsertEventRequestData");
        FILES_ADDED_FIELD_DESC = new TField("filesAdded", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new InsertEventRequestDataStandardSchemeFactory());
        InsertEventRequestData.schemes.put(TupleScheme.class, new InsertEventRequestDataTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FILES_ADDED, new FieldMetaData("filesAdded", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(InsertEventRequestData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FILES_ADDED((short)1, "filesAdded");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FILES_ADDED;
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
    
    private static class InsertEventRequestDataStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public InsertEventRequestDataStandardScheme getScheme() {
            return new InsertEventRequestDataStandardScheme();
        }
    }
    
    private static class InsertEventRequestDataStandardScheme extends StandardScheme<InsertEventRequestData>
    {
        @Override
        public void read(final TProtocol iprot, final InsertEventRequestData struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list508 = iprot.readListBegin();
                            struct.filesAdded = (List<String>)new ArrayList(_list508.size);
                            for (int _i509 = 0; _i509 < _list508.size; ++_i509) {
                                final String _elem510 = iprot.readString();
                                struct.filesAdded.add(_elem510);
                            }
                            iprot.readListEnd();
                            struct.setFilesAddedIsSet(true);
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
        public void write(final TProtocol oprot, final InsertEventRequestData struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(InsertEventRequestData.STRUCT_DESC);
            if (struct.filesAdded != null) {
                oprot.writeFieldBegin(InsertEventRequestData.FILES_ADDED_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.filesAdded.size()));
                for (final String _iter511 : struct.filesAdded) {
                    oprot.writeString(_iter511);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class InsertEventRequestDataTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public InsertEventRequestDataTupleScheme getScheme() {
            return new InsertEventRequestDataTupleScheme();
        }
    }
    
    private static class InsertEventRequestDataTupleScheme extends TupleScheme<InsertEventRequestData>
    {
        @Override
        public void write(final TProtocol prot, final InsertEventRequestData struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.filesAdded.size());
            for (final String _iter512 : struct.filesAdded) {
                oprot.writeString(_iter512);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final InsertEventRequestData struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list513 = new TList((byte)11, iprot.readI32());
            struct.filesAdded = (List<String>)new ArrayList(_list513.size);
            for (int _i514 = 0; _i514 < _list513.size; ++_i514) {
                final String _elem515 = iprot.readString();
                struct.filesAdded.add(_elem515);
            }
            struct.setFilesAddedIsSet(true);
        }
    }
}
