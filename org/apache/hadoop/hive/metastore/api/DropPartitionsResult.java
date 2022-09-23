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

public class DropPartitionsResult implements TBase<DropPartitionsResult, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PARTITIONS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<Partition> partitions;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DropPartitionsResult() {
        this.optionals = new _Fields[] { _Fields.PARTITIONS };
    }
    
    public DropPartitionsResult(final DropPartitionsResult other) {
        this.optionals = new _Fields[] { _Fields.PARTITIONS };
        if (other.isSetPartitions()) {
            final List<Partition> __this__partitions = new ArrayList<Partition>();
            for (final Partition other_element : other.partitions) {
                __this__partitions.add(new Partition(other_element));
            }
            this.partitions = __this__partitions;
        }
    }
    
    @Override
    public DropPartitionsResult deepCopy() {
        return new DropPartitionsResult(this);
    }
    
    @Override
    public void clear() {
        this.partitions = null;
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
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PARTITIONS: {
                return this.getPartitions();
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
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof DropPartitionsResult && this.equals((DropPartitionsResult)that);
    }
    
    public boolean equals(final DropPartitionsResult that) {
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
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DropPartitionsResult other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DropPartitionsResult typedOther = other;
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        DropPartitionsResult.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        DropPartitionsResult.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DropPartitionsResult(");
        boolean first = true;
        if (this.isSetPartitions()) {
            sb.append("partitions:");
            if (this.partitions == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partitions);
            }
            first = false;
        }
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
        STRUCT_DESC = new TStruct("DropPartitionsResult");
        PARTITIONS_FIELD_DESC = new TField("partitions", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DropPartitionsResultStandardSchemeFactory());
        DropPartitionsResult.schemes.put(TupleScheme.class, new DropPartitionsResultTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PARTITIONS, new FieldMetaData("partitions", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, Partition.class))));
        FieldMetaData.addStructMetaDataMap(DropPartitionsResult.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PARTITIONS((short)1, "partitions");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PARTITIONS;
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
    
    private static class DropPartitionsResultStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsResultStandardScheme getScheme() {
            return new DropPartitionsResultStandardScheme();
        }
    }
    
    private static class DropPartitionsResultStandardScheme extends StandardScheme<DropPartitionsResult>
    {
        @Override
        public void read(final TProtocol iprot, final DropPartitionsResult struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list396 = iprot.readListBegin();
                            struct.partitions = (List<Partition>)new ArrayList(_list396.size);
                            for (int _i397 = 0; _i397 < _list396.size; ++_i397) {
                                final Partition _elem398 = new Partition();
                                _elem398.read(iprot);
                                struct.partitions.add(_elem398);
                            }
                            iprot.readListEnd();
                            struct.setPartitionsIsSet(true);
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
        public void write(final TProtocol oprot, final DropPartitionsResult struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(DropPartitionsResult.STRUCT_DESC);
            if (struct.partitions != null && struct.isSetPartitions()) {
                oprot.writeFieldBegin(DropPartitionsResult.PARTITIONS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.partitions.size()));
                for (final Partition _iter399 : struct.partitions) {
                    _iter399.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DropPartitionsResultTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsResultTupleScheme getScheme() {
            return new DropPartitionsResultTupleScheme();
        }
    }
    
    private static class DropPartitionsResultTupleScheme extends TupleScheme<DropPartitionsResult>
    {
        @Override
        public void write(final TProtocol prot, final DropPartitionsResult struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetPartitions()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetPartitions()) {
                oprot.writeI32(struct.partitions.size());
                for (final Partition _iter400 : struct.partitions) {
                    _iter400.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final DropPartitionsResult struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                final TList _list401 = new TList((byte)12, iprot.readI32());
                struct.partitions = (List<Partition>)new ArrayList(_list401.size);
                for (int _i402 = 0; _i402 < _list401.size; ++_i402) {
                    final Partition _elem403 = new Partition();
                    _elem403.read(iprot);
                    struct.partitions.add(_elem403);
                }
                struct.setPartitionsIsSet(true);
            }
        }
    }
}
