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

public class PartitionSpecWithSharedSD implements TBase<PartitionSpecWithSharedSD, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PARTITIONS_FIELD_DESC;
    private static final TField SD_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<PartitionWithoutSD> partitions;
    private StorageDescriptor sd;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionSpecWithSharedSD() {
    }
    
    public PartitionSpecWithSharedSD(final List<PartitionWithoutSD> partitions, final StorageDescriptor sd) {
        this();
        this.partitions = partitions;
        this.sd = sd;
    }
    
    public PartitionSpecWithSharedSD(final PartitionSpecWithSharedSD other) {
        if (other.isSetPartitions()) {
            final List<PartitionWithoutSD> __this__partitions = new ArrayList<PartitionWithoutSD>();
            for (final PartitionWithoutSD other_element : other.partitions) {
                __this__partitions.add(new PartitionWithoutSD(other_element));
            }
            this.partitions = __this__partitions;
        }
        if (other.isSetSd()) {
            this.sd = new StorageDescriptor(other.sd);
        }
    }
    
    @Override
    public PartitionSpecWithSharedSD deepCopy() {
        return new PartitionSpecWithSharedSD(this);
    }
    
    @Override
    public void clear() {
        this.partitions = null;
        this.sd = null;
    }
    
    public int getPartitionsSize() {
        return (this.partitions == null) ? 0 : this.partitions.size();
    }
    
    public Iterator<PartitionWithoutSD> getPartitionsIterator() {
        return (this.partitions == null) ? null : this.partitions.iterator();
    }
    
    public void addToPartitions(final PartitionWithoutSD elem) {
        if (this.partitions == null) {
            this.partitions = new ArrayList<PartitionWithoutSD>();
        }
        this.partitions.add(elem);
    }
    
    public List<PartitionWithoutSD> getPartitions() {
        return this.partitions;
    }
    
    public void setPartitions(final List<PartitionWithoutSD> partitions) {
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
    
    public StorageDescriptor getSd() {
        return this.sd;
    }
    
    public void setSd(final StorageDescriptor sd) {
        this.sd = sd;
    }
    
    public void unsetSd() {
        this.sd = null;
    }
    
    public boolean isSetSd() {
        return this.sd != null;
    }
    
    public void setSdIsSet(final boolean value) {
        if (!value) {
            this.sd = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PARTITIONS: {
                if (value == null) {
                    this.unsetPartitions();
                    break;
                }
                this.setPartitions((List<PartitionWithoutSD>)value);
                break;
            }
            case SD: {
                if (value == null) {
                    this.unsetSd();
                    break;
                }
                this.setSd((StorageDescriptor)value);
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
            case SD: {
                return this.getSd();
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
            case SD: {
                return this.isSetSd();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionSpecWithSharedSD && this.equals((PartitionSpecWithSharedSD)that);
    }
    
    public boolean equals(final PartitionSpecWithSharedSD that) {
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
        final boolean this_present_sd = this.isSetSd();
        final boolean that_present_sd = that.isSetSd();
        if (this_present_sd || that_present_sd) {
            if (!this_present_sd || !that_present_sd) {
                return false;
            }
            if (!this.sd.equals(that.sd)) {
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
        final boolean present_sd = this.isSetSd();
        builder.append(present_sd);
        if (present_sd) {
            builder.append(this.sd);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionSpecWithSharedSD other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionSpecWithSharedSD typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetSd()).compareTo(Boolean.valueOf(typedOther.isSetSd()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSd()) {
            lastComparison = TBaseHelper.compareTo(this.sd, typedOther.sd);
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
        PartitionSpecWithSharedSD.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionSpecWithSharedSD.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionSpecWithSharedSD(");
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
        sb.append("sd:");
        if (this.sd == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sd);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.sd != null) {
            this.sd.validate();
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
        STRUCT_DESC = new TStruct("PartitionSpecWithSharedSD");
        PARTITIONS_FIELD_DESC = new TField("partitions", (byte)15, (short)1);
        SD_FIELD_DESC = new TField("sd", (byte)12, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionSpecWithSharedSDStandardSchemeFactory());
        PartitionSpecWithSharedSD.schemes.put(TupleScheme.class, new PartitionSpecWithSharedSDTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PARTITIONS, new FieldMetaData("partitions", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, PartitionWithoutSD.class))));
        tmpMap.put(_Fields.SD, new FieldMetaData("sd", (byte)3, new StructMetaData((byte)12, StorageDescriptor.class)));
        FieldMetaData.addStructMetaDataMap(PartitionSpecWithSharedSD.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PARTITIONS((short)1, "partitions"), 
        SD((short)2, "sd");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PARTITIONS;
                }
                case 2: {
                    return _Fields.SD;
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
    
    private static class PartitionSpecWithSharedSDStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionSpecWithSharedSDStandardScheme getScheme() {
            return new PartitionSpecWithSharedSDStandardScheme();
        }
    }
    
    private static class PartitionSpecWithSharedSDStandardScheme extends StandardScheme<PartitionSpecWithSharedSD>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionSpecWithSharedSD struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list244 = iprot.readListBegin();
                            struct.partitions = (List<PartitionWithoutSD>)new ArrayList(_list244.size);
                            for (int _i245 = 0; _i245 < _list244.size; ++_i245) {
                                final PartitionWithoutSD _elem246 = new PartitionWithoutSD();
                                _elem246.read(iprot);
                                struct.partitions.add(_elem246);
                            }
                            iprot.readListEnd();
                            struct.setPartitionsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.sd = new StorageDescriptor();
                            struct.sd.read(iprot);
                            struct.setSdIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionSpecWithSharedSD struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionSpecWithSharedSD.STRUCT_DESC);
            if (struct.partitions != null) {
                oprot.writeFieldBegin(PartitionSpecWithSharedSD.PARTITIONS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.partitions.size()));
                for (final PartitionWithoutSD _iter247 : struct.partitions) {
                    _iter247.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.sd != null) {
                oprot.writeFieldBegin(PartitionSpecWithSharedSD.SD_FIELD_DESC);
                struct.sd.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionSpecWithSharedSDTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionSpecWithSharedSDTupleScheme getScheme() {
            return new PartitionSpecWithSharedSDTupleScheme();
        }
    }
    
    private static class PartitionSpecWithSharedSDTupleScheme extends TupleScheme<PartitionSpecWithSharedSD>
    {
        @Override
        public void write(final TProtocol prot, final PartitionSpecWithSharedSD struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetPartitions()) {
                optionals.set(0);
            }
            if (struct.isSetSd()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetPartitions()) {
                oprot.writeI32(struct.partitions.size());
                for (final PartitionWithoutSD _iter248 : struct.partitions) {
                    _iter248.write(oprot);
                }
            }
            if (struct.isSetSd()) {
                struct.sd.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionSpecWithSharedSD struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                final TList _list249 = new TList((byte)12, iprot.readI32());
                struct.partitions = (List<PartitionWithoutSD>)new ArrayList(_list249.size);
                for (int _i250 = 0; _i250 < _list249.size; ++_i250) {
                    final PartitionWithoutSD _elem251 = new PartitionWithoutSD();
                    _elem251.read(iprot);
                    struct.partitions.add(_elem251);
                }
                struct.setPartitionsIsSet(true);
            }
            if (incoming.get(1)) {
                struct.sd = new StorageDescriptor();
                struct.sd.read(iprot);
                struct.setSdIsSet(true);
            }
        }
    }
}
