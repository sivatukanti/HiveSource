// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TFetchResultsResp implements TBase<TFetchResultsResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField HAS_MORE_ROWS_FIELD_DESC;
    private static final TField RESULTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private boolean hasMoreRows;
    private TRowSet results;
    private static final int __HASMOREROWS_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TFetchResultsResp() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.HAS_MORE_ROWS, _Fields.RESULTS };
    }
    
    public TFetchResultsResp(final TStatus status) {
        this();
        this.status = status;
    }
    
    public TFetchResultsResp(final TFetchResultsResp other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.HAS_MORE_ROWS, _Fields.RESULTS };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        this.hasMoreRows = other.hasMoreRows;
        if (other.isSetResults()) {
            this.results = new TRowSet(other.results);
        }
    }
    
    @Override
    public TFetchResultsResp deepCopy() {
        return new TFetchResultsResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.setHasMoreRowsIsSet(false);
        this.hasMoreRows = false;
        this.results = null;
    }
    
    public TStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final TStatus status) {
        this.status = status;
    }
    
    public void unsetStatus() {
        this.status = null;
    }
    
    public boolean isSetStatus() {
        return this.status != null;
    }
    
    public void setStatusIsSet(final boolean value) {
        if (!value) {
            this.status = null;
        }
    }
    
    public boolean isHasMoreRows() {
        return this.hasMoreRows;
    }
    
    public void setHasMoreRows(final boolean hasMoreRows) {
        this.hasMoreRows = hasMoreRows;
        this.setHasMoreRowsIsSet(true);
    }
    
    public void unsetHasMoreRows() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetHasMoreRows() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setHasMoreRowsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public TRowSet getResults() {
        return this.results;
    }
    
    public void setResults(final TRowSet results) {
        this.results = results;
    }
    
    public void unsetResults() {
        this.results = null;
    }
    
    public boolean isSetResults() {
        return this.results != null;
    }
    
    public void setResultsIsSet(final boolean value) {
        if (!value) {
            this.results = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case STATUS: {
                if (value == null) {
                    this.unsetStatus();
                    break;
                }
                this.setStatus((TStatus)value);
                break;
            }
            case HAS_MORE_ROWS: {
                if (value == null) {
                    this.unsetHasMoreRows();
                    break;
                }
                this.setHasMoreRows((boolean)value);
                break;
            }
            case RESULTS: {
                if (value == null) {
                    this.unsetResults();
                    break;
                }
                this.setResults((TRowSet)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case STATUS: {
                return this.getStatus();
            }
            case HAS_MORE_ROWS: {
                return this.isHasMoreRows();
            }
            case RESULTS: {
                return this.getResults();
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
            case STATUS: {
                return this.isSetStatus();
            }
            case HAS_MORE_ROWS: {
                return this.isSetHasMoreRows();
            }
            case RESULTS: {
                return this.isSetResults();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TFetchResultsResp && this.equals((TFetchResultsResp)that);
    }
    
    public boolean equals(final TFetchResultsResp that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_status = this.isSetStatus();
        final boolean that_present_status = that.isSetStatus();
        if (this_present_status || that_present_status) {
            if (!this_present_status || !that_present_status) {
                return false;
            }
            if (!this.status.equals(that.status)) {
                return false;
            }
        }
        final boolean this_present_hasMoreRows = this.isSetHasMoreRows();
        final boolean that_present_hasMoreRows = that.isSetHasMoreRows();
        if (this_present_hasMoreRows || that_present_hasMoreRows) {
            if (!this_present_hasMoreRows || !that_present_hasMoreRows) {
                return false;
            }
            if (this.hasMoreRows != that.hasMoreRows) {
                return false;
            }
        }
        final boolean this_present_results = this.isSetResults();
        final boolean that_present_results = that.isSetResults();
        if (this_present_results || that_present_results) {
            if (!this_present_results || !that_present_results) {
                return false;
            }
            if (!this.results.equals(that.results)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_status = this.isSetStatus();
        builder.append(present_status);
        if (present_status) {
            builder.append(this.status);
        }
        final boolean present_hasMoreRows = this.isSetHasMoreRows();
        builder.append(present_hasMoreRows);
        if (present_hasMoreRows) {
            builder.append(this.hasMoreRows);
        }
        final boolean present_results = this.isSetResults();
        builder.append(present_results);
        if (present_results) {
            builder.append(this.results);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TFetchResultsResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TFetchResultsResp typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatus()).compareTo(Boolean.valueOf(typedOther.isSetStatus()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatus()) {
            lastComparison = TBaseHelper.compareTo(this.status, typedOther.status);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHasMoreRows()).compareTo(Boolean.valueOf(typedOther.isSetHasMoreRows()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHasMoreRows()) {
            lastComparison = TBaseHelper.compareTo(this.hasMoreRows, typedOther.hasMoreRows);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetResults()).compareTo(Boolean.valueOf(typedOther.isSetResults()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetResults()) {
            lastComparison = TBaseHelper.compareTo(this.results, typedOther.results);
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
        TFetchResultsResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TFetchResultsResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TFetchResultsResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (this.isSetHasMoreRows()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("hasMoreRows:");
            sb.append(this.hasMoreRows);
            first = false;
        }
        if (this.isSetResults()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("results:");
            if (this.results == null) {
                sb.append("null");
            }
            else {
                sb.append(this.results);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStatus()) {
            throw new TProtocolException("Required field 'status' is unset! Struct:" + this.toString());
        }
        if (this.status != null) {
            this.status.validate();
        }
        if (this.results != null) {
            this.results.validate();
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
        STRUCT_DESC = new TStruct("TFetchResultsResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        HAS_MORE_ROWS_FIELD_DESC = new TField("hasMoreRows", (byte)2, (short)2);
        RESULTS_FIELD_DESC = new TField("results", (byte)12, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TFetchResultsRespStandardSchemeFactory());
        TFetchResultsResp.schemes.put(TupleScheme.class, new TFetchResultsRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.HAS_MORE_ROWS, new FieldMetaData("hasMoreRows", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.RESULTS, new FieldMetaData("results", (byte)2, new StructMetaData((byte)12, TRowSet.class)));
        FieldMetaData.addStructMetaDataMap(TFetchResultsResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        HAS_MORE_ROWS((short)2, "hasMoreRows"), 
        RESULTS((short)3, "results");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.HAS_MORE_ROWS;
                }
                case 3: {
                    return _Fields.RESULTS;
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
    
    private static class TFetchResultsRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TFetchResultsRespStandardScheme getScheme() {
            return new TFetchResultsRespStandardScheme();
        }
    }
    
    private static class TFetchResultsRespStandardScheme extends StandardScheme<TFetchResultsResp>
    {
        @Override
        public void read(final TProtocol iprot, final TFetchResultsResp struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.status = new TStatus();
                            struct.status.read(iprot);
                            struct.setStatusIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 2) {
                            struct.hasMoreRows = iprot.readBool();
                            struct.setHasMoreRowsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 12) {
                            struct.results = new TRowSet();
                            struct.results.read(iprot);
                            struct.setResultsIsSet(true);
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
        public void write(final TProtocol oprot, final TFetchResultsResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TFetchResultsResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TFetchResultsResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetHasMoreRows()) {
                oprot.writeFieldBegin(TFetchResultsResp.HAS_MORE_ROWS_FIELD_DESC);
                oprot.writeBool(struct.hasMoreRows);
                oprot.writeFieldEnd();
            }
            if (struct.results != null && struct.isSetResults()) {
                oprot.writeFieldBegin(TFetchResultsResp.RESULTS_FIELD_DESC);
                struct.results.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TFetchResultsRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TFetchResultsRespTupleScheme getScheme() {
            return new TFetchResultsRespTupleScheme();
        }
    }
    
    private static class TFetchResultsRespTupleScheme extends TupleScheme<TFetchResultsResp>
    {
        @Override
        public void write(final TProtocol prot, final TFetchResultsResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetHasMoreRows()) {
                optionals.set(0);
            }
            if (struct.isSetResults()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetHasMoreRows()) {
                oprot.writeBool(struct.hasMoreRows);
            }
            if (struct.isSetResults()) {
                struct.results.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TFetchResultsResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.hasMoreRows = iprot.readBool();
                struct.setHasMoreRowsIsSet(true);
            }
            if (incoming.get(1)) {
                struct.results = new TRowSet();
                struct.results.read(iprot);
                struct.setResultsIsSet(true);
            }
        }
    }
}
