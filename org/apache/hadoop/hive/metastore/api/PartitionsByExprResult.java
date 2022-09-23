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
import org.apache.thrift.EncodingUtils;
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

public class PartitionsByExprResult implements TBase<PartitionsByExprResult, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PARTITIONS_FIELD_DESC;
    private static final TField HAS_UNKNOWN_PARTITIONS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<Partition> partitions;
    private boolean hasUnknownPartitions;
    private static final int __HASUNKNOWNPARTITIONS_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionsByExprResult() {
        this.__isset_bitfield = 0;
    }
    
    public PartitionsByExprResult(final List<Partition> partitions, final boolean hasUnknownPartitions) {
        this();
        this.partitions = partitions;
        this.hasUnknownPartitions = hasUnknownPartitions;
        this.setHasUnknownPartitionsIsSet(true);
    }
    
    public PartitionsByExprResult(final PartitionsByExprResult other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetPartitions()) {
            final List<Partition> __this__partitions = new ArrayList<Partition>();
            for (final Partition other_element : other.partitions) {
                __this__partitions.add(new Partition(other_element));
            }
            this.partitions = __this__partitions;
        }
        this.hasUnknownPartitions = other.hasUnknownPartitions;
    }
    
    @Override
    public PartitionsByExprResult deepCopy() {
        return new PartitionsByExprResult(this);
    }
    
    @Override
    public void clear() {
        this.partitions = null;
        this.setHasUnknownPartitionsIsSet(false);
        this.hasUnknownPartitions = false;
    }
    
    public int getPartitionsSize() {
        return (this.partitions == null) ? 0 : this.partitions.size();
    }
    
    public Iterator<Partition> getPartitionsIterator() {
        return (this.partitions == null) ? null : this.partitions.iterator();
    }
    
    public void addToPartitions(final Partition elem) {
        if (this.partitions == null) {
            this.partitions = new ArrayList<Partition>();
        }
        this.partitions.add(elem);
    }
    
    public List<Partition> getPartitions() {
        return this.partitions;
    }
    
    public void setPartitions(final List<Partition> partitions) {
        this.partitions = partitions;
    }
    
    public void unsetPartitions() {
        this.partitions = null;
    }
    
    public boolean isSetPartitions() {
        return this.partitions != null;
    }
    
    public void setPartitionsIsSet(final boolean value) {
        if (!value) {
            this.partitions = null;
        }
    }
    
    public boolean isHasUnknownPartitions() {
        return this.hasUnknownPartitions;
    }
    
    public void setHasUnknownPartitions(final boolean hasUnknownPartitions) {
        this.hasUnknownPartitions = hasUnknownPartitions;
        this.setHasUnknownPartitionsIsSet(true);
    }
    
    public void unsetHasUnknownPartitions() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetHasUnknownPartitions() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setHasUnknownPartitionsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PARTITIONS: {
                if (value == null) {
                    this.unsetPartitions();
                    break;
                }
                this.setPartitions((List<Partition>)value);
                break;
            }
            case HAS_UNKNOWN_PARTITIONS: {
                if (value == null) {
                    this.unsetHasUnknownPartitions();
                    break;
                }
                this.setHasUnknownPartitions((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PARTITIONS: {
                return this.getPartitions();
            }
            case HAS_UNKNOWN_PARTITIONS: {
                return this.isHasUnknownPartitions();
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
            case PARTITIONS: {
                return this.isSetPartitions();
            }
            case HAS_UNKNOWN_PARTITIONS: {
                return this.isSetHasUnknownPartitions();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionsByExprResult && this.equals((PartitionsByExprResult)that);
    }
    
    public boolean equals(final PartitionsByExprResult that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_partitions = this.isSetPartitions();
        final boolean that_present_partitions = that.isSetPartitions();
        if (this_present_partitions || that_present_partitions) {
            if (!this_present_partitions || !that_present_partitions) {
                return false;
            }
            if (!this.partitions.equals(that.partitions)) {
                return false;
            }
        }
        final boolean this_present_hasUnknownPartitions = true;
        final boolean that_present_hasUnknownPartitions = true;
        if (this_present_hasUnknownPartitions || that_present_hasUnknownPartitions) {
            if (!this_present_hasUnknownPartitions || !that_present_hasUnknownPartitions) {
                return false;
            }
            if (this.hasUnknownPartitions != that.hasUnknownPartitions) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_partitions = this.isSetPartitions();
        builder.append(present_partitions);
        if (present_partitions) {
            builder.append(this.partitions);
        }
        final boolean present_hasUnknownPartitions = true;
        builder.append(present_hasUnknownPartitions);
        if (present_hasUnknownPartitions) {
            builder.append(this.hasUnknownPartitions);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionsByExprResult other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionsByExprResult typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPartitions()).compareTo(Boolean.valueOf(typedOther.isSetPartitions()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitions()) {
            lastComparison = TBaseHelper.compareTo(this.partitions, typedOther.partitions);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHasUnknownPartitions()).compareTo(Boolean.valueOf(typedOther.isSetHasUnknownPartitions()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHasUnknownPartitions()) {
            lastComparison = TBaseHelper.compareTo(this.hasUnknownPartitions, typedOther.hasUnknownPartitions);
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
        PartitionsByExprResult.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionsByExprResult.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionsByExprResult(");
        boolean first = true;
        sb.append("partitions:");
        if (this.partitions == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partitions);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("hasUnknownPartitions:");
        sb.append(this.hasUnknownPartitions);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetPartitions()) {
            throw new TProtocolException("Required field 'partitions' is unset! Struct:" + this.toString());
        }
        if (!this.isSetHasUnknownPartitions()) {
            throw new TProtocolException("Required field 'hasUnknownPartitions' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("PartitionsByExprResult");
        PARTITIONS_FIELD_DESC = new TField("partitions", (byte)15, (short)1);
        HAS_UNKNOWN_PARTITIONS_FIELD_DESC = new TField("hasUnknownPartitions", (byte)2, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionsByExprResultStandardSchemeFactory());
        PartitionsByExprResult.schemes.put(TupleScheme.class, new PartitionsByExprResultTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PARTITIONS, new FieldMetaData("partitions", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, Partition.class))));
        tmpMap.put(_Fields.HAS_UNKNOWN_PARTITIONS, new FieldMetaData("hasUnknownPartitions", (byte)1, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(PartitionsByExprResult.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PARTITIONS((short)1, "partitions"), 
        HAS_UNKNOWN_PARTITIONS((short)2, "hasUnknownPartitions");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PARTITIONS;
                }
                case 2: {
                    return _Fields.HAS_UNKNOWN_PARTITIONS;
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
    
    private static class PartitionsByExprResultStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsByExprResultStandardScheme getScheme() {
            return new PartitionsByExprResultStandardScheme();
        }
    }
    
    private static class PartitionsByExprResultStandardScheme extends StandardScheme<PartitionsByExprResult>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionsByExprResult struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list322 = iprot.readListBegin();
                            struct.partitions = (List<Partition>)new ArrayList(_list322.size);
                            for (int _i323 = 0; _i323 < _list322.size; ++_i323) {
                                final Partition _elem324 = new Partition();
                                _elem324.read(iprot);
                                struct.partitions.add(_elem324);
                            }
                            iprot.readListEnd();
                            struct.setPartitionsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 2) {
                            struct.hasUnknownPartitions = iprot.readBool();
                            struct.setHasUnknownPartitionsIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionsByExprResult struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionsByExprResult.STRUCT_DESC);
            if (struct.partitions != null) {
                oprot.writeFieldBegin(PartitionsByExprResult.PARTITIONS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.partitions.size()));
                for (final Partition _iter325 : struct.partitions) {
                    _iter325.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(PartitionsByExprResult.HAS_UNKNOWN_PARTITIONS_FIELD_DESC);
            oprot.writeBool(struct.hasUnknownPartitions);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionsByExprResultTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsByExprResultTupleScheme getScheme() {
            return new PartitionsByExprResultTupleScheme();
        }
    }
    
    private static class PartitionsByExprResultTupleScheme extends TupleScheme<PartitionsByExprResult>
    {
        @Override
        public void write(final TProtocol prot, final PartitionsByExprResult struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.partitions.size());
            for (final Partition _iter326 : struct.partitions) {
                _iter326.write(oprot);
            }
            oprot.writeBool(struct.hasUnknownPartitions);
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionsByExprResult struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list327 = new TList((byte)12, iprot.readI32());
            struct.partitions = (List<Partition>)new ArrayList(_list327.size);
            for (int _i328 = 0; _i328 < _list327.size; ++_i328) {
                final Partition _elem329 = new Partition();
                _elem329.read(iprot);
                struct.partitions.add(_elem329);
            }
            struct.setPartitionsIsSet(true);
            struct.hasUnknownPartitions = iprot.readBool();
            struct.setHasUnknownPartitionsIsSet(true);
        }
    }
}
