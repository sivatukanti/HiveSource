// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import org.apache.commons.lang.builder.HashCodeBuilder;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class IndexPageHeader implements TBase<IndexPageHeader, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public IndexPageHeader() {
    }
    
    public IndexPageHeader(final IndexPageHeader other) {
    }
    
    @Override
    public IndexPageHeader deepCopy() {
        return new IndexPageHeader(this);
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        final int n = IndexPageHeader$1.$SwitchMap$parquet$format$IndexPageHeader$_Fields[field.ordinal()];
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        final int n = IndexPageHeader$1.$SwitchMap$parquet$format$IndexPageHeader$_Fields[field.ordinal()];
        throw new IllegalStateException();
    }
    
    @Override
    public boolean isSet(final _Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        final int n = IndexPageHeader$1.$SwitchMap$parquet$format$IndexPageHeader$_Fields[field.ordinal()];
        throw new IllegalStateException();
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof IndexPageHeader && this.equals((IndexPageHeader)that);
    }
    
    public boolean equals(final IndexPageHeader that) {
        return that != null;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final IndexPageHeader other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        final int lastComparison = 0;
        final IndexPageHeader typedOther = other;
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        iprot.readStructBegin();
        while (true) {
            final TField field = iprot.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            final short id = field.id;
            TProtocolUtil.skip(iprot, field.type);
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(IndexPageHeader.STRUCT_DESC);
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndexPageHeader(");
        final boolean first = true;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
    }
    
    static {
        STRUCT_DESC = new TStruct("IndexPageHeader");
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        FieldMetaData.addStructMetaDataMap(IndexPageHeader.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            return null;
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
}
