// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.StructMetaData;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import parquet.org.apache.thrift.TEnum;
import parquet.org.apache.thrift.meta_data.EnumMetaData;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import java.util.BitSet;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class PageHeader implements TBase<PageHeader, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField UNCOMPRESSED_PAGE_SIZE_FIELD_DESC;
    private static final TField COMPRESSED_PAGE_SIZE_FIELD_DESC;
    private static final TField CRC_FIELD_DESC;
    private static final TField DATA_PAGE_HEADER_FIELD_DESC;
    private static final TField INDEX_PAGE_HEADER_FIELD_DESC;
    private static final TField DICTIONARY_PAGE_HEADER_FIELD_DESC;
    private static final TField DATA_PAGE_HEADER_V2_FIELD_DESC;
    public PageType type;
    public int uncompressed_page_size;
    public int compressed_page_size;
    public int crc;
    public DataPageHeader data_page_header;
    public IndexPageHeader index_page_header;
    public DictionaryPageHeader dictionary_page_header;
    public DataPageHeaderV2 data_page_header_v2;
    private static final int __UNCOMPRESSED_PAGE_SIZE_ISSET_ID = 0;
    private static final int __COMPRESSED_PAGE_SIZE_ISSET_ID = 1;
    private static final int __CRC_ISSET_ID = 2;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PageHeader() {
        this.__isset_bit_vector = new BitSet(3);
    }
    
    public PageHeader(final PageType type, final int uncompressed_page_size, final int compressed_page_size) {
        this();
        this.type = type;
        this.uncompressed_page_size = uncompressed_page_size;
        this.setUncompressed_page_sizeIsSet(true);
        this.compressed_page_size = compressed_page_size;
        this.setCompressed_page_sizeIsSet(true);
    }
    
    public PageHeader(final PageHeader other) {
        (this.__isset_bit_vector = new BitSet(3)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetType()) {
            this.type = other.type;
        }
        this.uncompressed_page_size = other.uncompressed_page_size;
        this.compressed_page_size = other.compressed_page_size;
        this.crc = other.crc;
        if (other.isSetData_page_header()) {
            this.data_page_header = new DataPageHeader(other.data_page_header);
        }
        if (other.isSetIndex_page_header()) {
            this.index_page_header = new IndexPageHeader(other.index_page_header);
        }
        if (other.isSetDictionary_page_header()) {
            this.dictionary_page_header = new DictionaryPageHeader(other.dictionary_page_header);
        }
        if (other.isSetData_page_header_v2()) {
            this.data_page_header_v2 = new DataPageHeaderV2(other.data_page_header_v2);
        }
    }
    
    @Override
    public PageHeader deepCopy() {
        return new PageHeader(this);
    }
    
    @Override
    public void clear() {
        this.type = null;
        this.setUncompressed_page_sizeIsSet(false);
        this.uncompressed_page_size = 0;
        this.setCompressed_page_sizeIsSet(false);
        this.compressed_page_size = 0;
        this.setCrcIsSet(false);
        this.crc = 0;
        this.data_page_header = null;
        this.index_page_header = null;
        this.dictionary_page_header = null;
        this.data_page_header_v2 = null;
    }
    
    public PageType getType() {
        return this.type;
    }
    
    public PageHeader setType(final PageType type) {
        this.type = type;
        return this;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
    }
    
    public int getUncompressed_page_size() {
        return this.uncompressed_page_size;
    }
    
    public PageHeader setUncompressed_page_size(final int uncompressed_page_size) {
        this.uncompressed_page_size = uncompressed_page_size;
        this.setUncompressed_page_sizeIsSet(true);
        return this;
    }
    
    public void unsetUncompressed_page_size() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetUncompressed_page_size() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setUncompressed_page_sizeIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public int getCompressed_page_size() {
        return this.compressed_page_size;
    }
    
    public PageHeader setCompressed_page_size(final int compressed_page_size) {
        this.compressed_page_size = compressed_page_size;
        this.setCompressed_page_sizeIsSet(true);
        return this;
    }
    
    public void unsetCompressed_page_size() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetCompressed_page_size() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setCompressed_page_sizeIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public int getCrc() {
        return this.crc;
    }
    
    public PageHeader setCrc(final int crc) {
        this.crc = crc;
        this.setCrcIsSet(true);
        return this;
    }
    
    public void unsetCrc() {
        this.__isset_bit_vector.clear(2);
    }
    
    public boolean isSetCrc() {
        return this.__isset_bit_vector.get(2);
    }
    
    public void setCrcIsSet(final boolean value) {
        this.__isset_bit_vector.set(2, value);
    }
    
    public DataPageHeader getData_page_header() {
        return this.data_page_header;
    }
    
    public PageHeader setData_page_header(final DataPageHeader data_page_header) {
        this.data_page_header = data_page_header;
        return this;
    }
    
    public void unsetData_page_header() {
        this.data_page_header = null;
    }
    
    public boolean isSetData_page_header() {
        return this.data_page_header != null;
    }
    
    public void setData_page_headerIsSet(final boolean value) {
        if (!value) {
            this.data_page_header = null;
        }
    }
    
    public IndexPageHeader getIndex_page_header() {
        return this.index_page_header;
    }
    
    public PageHeader setIndex_page_header(final IndexPageHeader index_page_header) {
        this.index_page_header = index_page_header;
        return this;
    }
    
    public void unsetIndex_page_header() {
        this.index_page_header = null;
    }
    
    public boolean isSetIndex_page_header() {
        return this.index_page_header != null;
    }
    
    public void setIndex_page_headerIsSet(final boolean value) {
        if (!value) {
            this.index_page_header = null;
        }
    }
    
    public DictionaryPageHeader getDictionary_page_header() {
        return this.dictionary_page_header;
    }
    
    public PageHeader setDictionary_page_header(final DictionaryPageHeader dictionary_page_header) {
        this.dictionary_page_header = dictionary_page_header;
        return this;
    }
    
    public void unsetDictionary_page_header() {
        this.dictionary_page_header = null;
    }
    
    public boolean isSetDictionary_page_header() {
        return this.dictionary_page_header != null;
    }
    
    public void setDictionary_page_headerIsSet(final boolean value) {
        if (!value) {
            this.dictionary_page_header = null;
        }
    }
    
    public DataPageHeaderV2 getData_page_header_v2() {
        return this.data_page_header_v2;
    }
    
    public PageHeader setData_page_header_v2(final DataPageHeaderV2 data_page_header_v2) {
        this.data_page_header_v2 = data_page_header_v2;
        return this;
    }
    
    public void unsetData_page_header_v2() {
        this.data_page_header_v2 = null;
    }
    
    public boolean isSetData_page_header_v2() {
        return this.data_page_header_v2 != null;
    }
    
    public void setData_page_header_v2IsSet(final boolean value) {
        if (!value) {
            this.data_page_header_v2 = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((PageType)value);
                break;
            }
            case UNCOMPRESSED_PAGE_SIZE: {
                if (value == null) {
                    this.unsetUncompressed_page_size();
                    break;
                }
                this.setUncompressed_page_size((int)value);
                break;
            }
            case COMPRESSED_PAGE_SIZE: {
                if (value == null) {
                    this.unsetCompressed_page_size();
                    break;
                }
                this.setCompressed_page_size((int)value);
                break;
            }
            case CRC: {
                if (value == null) {
                    this.unsetCrc();
                    break;
                }
                this.setCrc((int)value);
                break;
            }
            case DATA_PAGE_HEADER: {
                if (value == null) {
                    this.unsetData_page_header();
                    break;
                }
                this.setData_page_header((DataPageHeader)value);
                break;
            }
            case INDEX_PAGE_HEADER: {
                if (value == null) {
                    this.unsetIndex_page_header();
                    break;
                }
                this.setIndex_page_header((IndexPageHeader)value);
                break;
            }
            case DICTIONARY_PAGE_HEADER: {
                if (value == null) {
                    this.unsetDictionary_page_header();
                    break;
                }
                this.setDictionary_page_header((DictionaryPageHeader)value);
                break;
            }
            case DATA_PAGE_HEADER_V2: {
                if (value == null) {
                    this.unsetData_page_header_v2();
                    break;
                }
                this.setData_page_header_v2((DataPageHeaderV2)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE: {
                return this.getType();
            }
            case UNCOMPRESSED_PAGE_SIZE: {
                return new Integer(this.getUncompressed_page_size());
            }
            case COMPRESSED_PAGE_SIZE: {
                return new Integer(this.getCompressed_page_size());
            }
            case CRC: {
                return new Integer(this.getCrc());
            }
            case DATA_PAGE_HEADER: {
                return this.getData_page_header();
            }
            case INDEX_PAGE_HEADER: {
                return this.getIndex_page_header();
            }
            case DICTIONARY_PAGE_HEADER: {
                return this.getDictionary_page_header();
            }
            case DATA_PAGE_HEADER_V2: {
                return this.getData_page_header_v2();
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
            case TYPE: {
                return this.isSetType();
            }
            case UNCOMPRESSED_PAGE_SIZE: {
                return this.isSetUncompressed_page_size();
            }
            case COMPRESSED_PAGE_SIZE: {
                return this.isSetCompressed_page_size();
            }
            case CRC: {
                return this.isSetCrc();
            }
            case DATA_PAGE_HEADER: {
                return this.isSetData_page_header();
            }
            case INDEX_PAGE_HEADER: {
                return this.isSetIndex_page_header();
            }
            case DICTIONARY_PAGE_HEADER: {
                return this.isSetDictionary_page_header();
            }
            case DATA_PAGE_HEADER_V2: {
                return this.isSetData_page_header_v2();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PageHeader && this.equals((PageHeader)that);
    }
    
    public boolean equals(final PageHeader that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
                return false;
            }
        }
        final boolean this_present_uncompressed_page_size = true;
        final boolean that_present_uncompressed_page_size = true;
        if (this_present_uncompressed_page_size || that_present_uncompressed_page_size) {
            if (!this_present_uncompressed_page_size || !that_present_uncompressed_page_size) {
                return false;
            }
            if (this.uncompressed_page_size != that.uncompressed_page_size) {
                return false;
            }
        }
        final boolean this_present_compressed_page_size = true;
        final boolean that_present_compressed_page_size = true;
        if (this_present_compressed_page_size || that_present_compressed_page_size) {
            if (!this_present_compressed_page_size || !that_present_compressed_page_size) {
                return false;
            }
            if (this.compressed_page_size != that.compressed_page_size) {
                return false;
            }
        }
        final boolean this_present_crc = this.isSetCrc();
        final boolean that_present_crc = that.isSetCrc();
        if (this_present_crc || that_present_crc) {
            if (!this_present_crc || !that_present_crc) {
                return false;
            }
            if (this.crc != that.crc) {
                return false;
            }
        }
        final boolean this_present_data_page_header = this.isSetData_page_header();
        final boolean that_present_data_page_header = that.isSetData_page_header();
        if (this_present_data_page_header || that_present_data_page_header) {
            if (!this_present_data_page_header || !that_present_data_page_header) {
                return false;
            }
            if (!this.data_page_header.equals(that.data_page_header)) {
                return false;
            }
        }
        final boolean this_present_index_page_header = this.isSetIndex_page_header();
        final boolean that_present_index_page_header = that.isSetIndex_page_header();
        if (this_present_index_page_header || that_present_index_page_header) {
            if (!this_present_index_page_header || !that_present_index_page_header) {
                return false;
            }
            if (!this.index_page_header.equals(that.index_page_header)) {
                return false;
            }
        }
        final boolean this_present_dictionary_page_header = this.isSetDictionary_page_header();
        final boolean that_present_dictionary_page_header = that.isSetDictionary_page_header();
        if (this_present_dictionary_page_header || that_present_dictionary_page_header) {
            if (!this_present_dictionary_page_header || !that_present_dictionary_page_header) {
                return false;
            }
            if (!this.dictionary_page_header.equals(that.dictionary_page_header)) {
                return false;
            }
        }
        final boolean this_present_data_page_header_v2 = this.isSetData_page_header_v2();
        final boolean that_present_data_page_header_v2 = that.isSetData_page_header_v2();
        if (this_present_data_page_header_v2 || that_present_data_page_header_v2) {
            if (!this_present_data_page_header_v2 || !that_present_data_page_header_v2) {
                return false;
            }
            if (!this.data_page_header_v2.equals(that.data_page_header_v2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_uncompressed_page_size = true;
        builder.append(present_uncompressed_page_size);
        if (present_uncompressed_page_size) {
            builder.append(this.uncompressed_page_size);
        }
        final boolean present_compressed_page_size = true;
        builder.append(present_compressed_page_size);
        if (present_compressed_page_size) {
            builder.append(this.compressed_page_size);
        }
        final boolean present_crc = this.isSetCrc();
        builder.append(present_crc);
        if (present_crc) {
            builder.append(this.crc);
        }
        final boolean present_data_page_header = this.isSetData_page_header();
        builder.append(present_data_page_header);
        if (present_data_page_header) {
            builder.append(this.data_page_header);
        }
        final boolean present_index_page_header = this.isSetIndex_page_header();
        builder.append(present_index_page_header);
        if (present_index_page_header) {
            builder.append(this.index_page_header);
        }
        final boolean present_dictionary_page_header = this.isSetDictionary_page_header();
        builder.append(present_dictionary_page_header);
        if (present_dictionary_page_header) {
            builder.append(this.dictionary_page_header);
        }
        final boolean present_data_page_header_v2 = this.isSetData_page_header_v2();
        builder.append(present_data_page_header_v2);
        if (present_data_page_header_v2) {
            builder.append(this.data_page_header_v2);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PageHeader other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PageHeader typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUncompressed_page_size()).compareTo(Boolean.valueOf(typedOther.isSetUncompressed_page_size()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUncompressed_page_size()) {
            lastComparison = TBaseHelper.compareTo(this.uncompressed_page_size, typedOther.uncompressed_page_size);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCompressed_page_size()).compareTo(Boolean.valueOf(typedOther.isSetCompressed_page_size()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCompressed_page_size()) {
            lastComparison = TBaseHelper.compareTo(this.compressed_page_size, typedOther.compressed_page_size);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCrc()).compareTo(Boolean.valueOf(typedOther.isSetCrc()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCrc()) {
            lastComparison = TBaseHelper.compareTo(this.crc, typedOther.crc);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetData_page_header()).compareTo(Boolean.valueOf(typedOther.isSetData_page_header()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetData_page_header()) {
            lastComparison = TBaseHelper.compareTo(this.data_page_header, typedOther.data_page_header);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIndex_page_header()).compareTo(Boolean.valueOf(typedOther.isSetIndex_page_header()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIndex_page_header()) {
            lastComparison = TBaseHelper.compareTo(this.index_page_header, typedOther.index_page_header);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDictionary_page_header()).compareTo(Boolean.valueOf(typedOther.isSetDictionary_page_header()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDictionary_page_header()) {
            lastComparison = TBaseHelper.compareTo(this.dictionary_page_header, typedOther.dictionary_page_header);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetData_page_header_v2()).compareTo(Boolean.valueOf(typedOther.isSetData_page_header_v2()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetData_page_header_v2()) {
            lastComparison = TBaseHelper.compareTo(this.data_page_header_v2, typedOther.data_page_header_v2);
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
        iprot.readStructBegin();
        while (true) {
            final TField field = iprot.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            switch (field.id) {
                case 1: {
                    if (field.type == 8) {
                        this.type = PageType.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 8) {
                        this.uncompressed_page_size = iprot.readI32();
                        this.setUncompressed_page_sizeIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 8) {
                        this.compressed_page_size = iprot.readI32();
                        this.setCompressed_page_sizeIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 8) {
                        this.crc = iprot.readI32();
                        this.setCrcIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
                    if (field.type == 12) {
                        (this.data_page_header = new DataPageHeader()).read(iprot);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 6: {
                    if (field.type == 12) {
                        (this.index_page_header = new IndexPageHeader()).read(iprot);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 7: {
                    if (field.type == 12) {
                        (this.dictionary_page_header = new DictionaryPageHeader()).read(iprot);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 8: {
                    if (field.type == 12) {
                        (this.data_page_header_v2 = new DataPageHeaderV2()).read(iprot);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                default: {
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();
        if (!this.isSetUncompressed_page_size()) {
            throw new TProtocolException("Required field 'uncompressed_page_size' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetCompressed_page_size()) {
            throw new TProtocolException("Required field 'compressed_page_size' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(PageHeader.STRUCT_DESC);
        if (this.type != null) {
            oprot.writeFieldBegin(PageHeader.TYPE_FIELD_DESC);
            oprot.writeI32(this.type.getValue());
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(PageHeader.UNCOMPRESSED_PAGE_SIZE_FIELD_DESC);
        oprot.writeI32(this.uncompressed_page_size);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(PageHeader.COMPRESSED_PAGE_SIZE_FIELD_DESC);
        oprot.writeI32(this.compressed_page_size);
        oprot.writeFieldEnd();
        if (this.isSetCrc()) {
            oprot.writeFieldBegin(PageHeader.CRC_FIELD_DESC);
            oprot.writeI32(this.crc);
            oprot.writeFieldEnd();
        }
        if (this.data_page_header != null && this.isSetData_page_header()) {
            oprot.writeFieldBegin(PageHeader.DATA_PAGE_HEADER_FIELD_DESC);
            this.data_page_header.write(oprot);
            oprot.writeFieldEnd();
        }
        if (this.index_page_header != null && this.isSetIndex_page_header()) {
            oprot.writeFieldBegin(PageHeader.INDEX_PAGE_HEADER_FIELD_DESC);
            this.index_page_header.write(oprot);
            oprot.writeFieldEnd();
        }
        if (this.dictionary_page_header != null && this.isSetDictionary_page_header()) {
            oprot.writeFieldBegin(PageHeader.DICTIONARY_PAGE_HEADER_FIELD_DESC);
            this.dictionary_page_header.write(oprot);
            oprot.writeFieldEnd();
        }
        if (this.data_page_header_v2 != null && this.isSetData_page_header_v2()) {
            oprot.writeFieldBegin(PageHeader.DATA_PAGE_HEADER_V2_FIELD_DESC);
            this.data_page_header_v2.write(oprot);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageHeader(");
        boolean first = true;
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("uncompressed_page_size:");
        sb.append(this.uncompressed_page_size);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("compressed_page_size:");
        sb.append(this.compressed_page_size);
        first = false;
        if (this.isSetCrc()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("crc:");
            sb.append(this.crc);
            first = false;
        }
        if (this.isSetData_page_header()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("data_page_header:");
            if (this.data_page_header == null) {
                sb.append("null");
            }
            else {
                sb.append(this.data_page_header);
            }
            first = false;
        }
        if (this.isSetIndex_page_header()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("index_page_header:");
            if (this.index_page_header == null) {
                sb.append("null");
            }
            else {
                sb.append(this.index_page_header);
            }
            first = false;
        }
        if (this.isSetDictionary_page_header()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("dictionary_page_header:");
            if (this.dictionary_page_header == null) {
                sb.append("null");
            }
            else {
                sb.append(this.dictionary_page_header);
            }
            first = false;
        }
        if (this.isSetData_page_header_v2()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("data_page_header_v2:");
            if (this.data_page_header_v2 == null) {
                sb.append("null");
            }
            else {
                sb.append(this.data_page_header_v2);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.type == null) {
            throw new TProtocolException("Required field 'type' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("PageHeader");
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
        UNCOMPRESSED_PAGE_SIZE_FIELD_DESC = new TField("uncompressed_page_size", (byte)8, (short)2);
        COMPRESSED_PAGE_SIZE_FIELD_DESC = new TField("compressed_page_size", (byte)8, (short)3);
        CRC_FIELD_DESC = new TField("crc", (byte)8, (short)4);
        DATA_PAGE_HEADER_FIELD_DESC = new TField("data_page_header", (byte)12, (short)5);
        INDEX_PAGE_HEADER_FIELD_DESC = new TField("index_page_header", (byte)12, (short)6);
        DICTIONARY_PAGE_HEADER_FIELD_DESC = new TField("dictionary_page_header", (byte)12, (short)7);
        DATA_PAGE_HEADER_V2_FIELD_DESC = new TField("data_page_header_v2", (byte)12, (short)8);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, PageType.class)));
        tmpMap.put(_Fields.UNCOMPRESSED_PAGE_SIZE, new FieldMetaData("uncompressed_page_size", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.COMPRESSED_PAGE_SIZE, new FieldMetaData("compressed_page_size", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.CRC, new FieldMetaData("crc", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.DATA_PAGE_HEADER, new FieldMetaData("data_page_header", (byte)2, new StructMetaData((byte)12, DataPageHeader.class)));
        tmpMap.put(_Fields.INDEX_PAGE_HEADER, new FieldMetaData("index_page_header", (byte)2, new StructMetaData((byte)12, IndexPageHeader.class)));
        tmpMap.put(_Fields.DICTIONARY_PAGE_HEADER, new FieldMetaData("dictionary_page_header", (byte)2, new StructMetaData((byte)12, DictionaryPageHeader.class)));
        tmpMap.put(_Fields.DATA_PAGE_HEADER_V2, new FieldMetaData("data_page_header_v2", (byte)2, new StructMetaData((byte)12, DataPageHeaderV2.class)));
        FieldMetaData.addStructMetaDataMap(PageHeader.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE((short)1, "type"), 
        UNCOMPRESSED_PAGE_SIZE((short)2, "uncompressed_page_size"), 
        COMPRESSED_PAGE_SIZE((short)3, "compressed_page_size"), 
        CRC((short)4, "crc"), 
        DATA_PAGE_HEADER((short)5, "data_page_header"), 
        INDEX_PAGE_HEADER((short)6, "index_page_header"), 
        DICTIONARY_PAGE_HEADER((short)7, "dictionary_page_header"), 
        DATA_PAGE_HEADER_V2((short)8, "data_page_header_v2");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE;
                }
                case 2: {
                    return _Fields.UNCOMPRESSED_PAGE_SIZE;
                }
                case 3: {
                    return _Fields.COMPRESSED_PAGE_SIZE;
                }
                case 4: {
                    return _Fields.CRC;
                }
                case 5: {
                    return _Fields.DATA_PAGE_HEADER;
                }
                case 6: {
                    return _Fields.INDEX_PAGE_HEADER;
                }
                case 7: {
                    return _Fields.DICTIONARY_PAGE_HEADER;
                }
                case 8: {
                    return _Fields.DATA_PAGE_HEADER_V2;
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
}
