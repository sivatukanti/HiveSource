// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import org.apache.thrift.EncodingUtils;
import java.util.Iterator;
import java.util.HashMap;
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

public class StorageDescriptor implements TBase<StorageDescriptor, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COLS_FIELD_DESC;
    private static final TField LOCATION_FIELD_DESC;
    private static final TField INPUT_FORMAT_FIELD_DESC;
    private static final TField OUTPUT_FORMAT_FIELD_DESC;
    private static final TField COMPRESSED_FIELD_DESC;
    private static final TField NUM_BUCKETS_FIELD_DESC;
    private static final TField SERDE_INFO_FIELD_DESC;
    private static final TField BUCKET_COLS_FIELD_DESC;
    private static final TField SORT_COLS_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField SKEWED_INFO_FIELD_DESC;
    private static final TField STORED_AS_SUB_DIRECTORIES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<FieldSchema> cols;
    private String location;
    private String inputFormat;
    private String outputFormat;
    private boolean compressed;
    private int numBuckets;
    private SerDeInfo serdeInfo;
    private List<String> bucketCols;
    private List<Order> sortCols;
    private Map<String, String> parameters;
    private SkewedInfo skewedInfo;
    private boolean storedAsSubDirectories;
    private static final int __COMPRESSED_ISSET_ID = 0;
    private static final int __NUMBUCKETS_ISSET_ID = 1;
    private static final int __STOREDASSUBDIRECTORIES_ISSET_ID = 2;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public StorageDescriptor() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.SKEWED_INFO, _Fields.STORED_AS_SUB_DIRECTORIES };
    }
    
    public StorageDescriptor(final List<FieldSchema> cols, final String location, final String inputFormat, final String outputFormat, final boolean compressed, final int numBuckets, final SerDeInfo serdeInfo, final List<String> bucketCols, final List<Order> sortCols, final Map<String, String> parameters) {
        this();
        this.cols = cols;
        this.location = location;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.compressed = compressed;
        this.setCompressedIsSet(true);
        this.numBuckets = numBuckets;
        this.setNumBucketsIsSet(true);
        this.serdeInfo = serdeInfo;
        this.bucketCols = bucketCols;
        this.sortCols = sortCols;
        this.parameters = parameters;
    }
    
    public StorageDescriptor(final StorageDescriptor other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.SKEWED_INFO, _Fields.STORED_AS_SUB_DIRECTORIES };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetCols()) {
            final List<FieldSchema> __this__cols = new ArrayList<FieldSchema>();
            for (final FieldSchema other_element : other.cols) {
                __this__cols.add(new FieldSchema(other_element));
            }
            this.cols = __this__cols;
        }
        if (other.isSetLocation()) {
            this.location = other.location;
        }
        if (other.isSetInputFormat()) {
            this.inputFormat = other.inputFormat;
        }
        if (other.isSetOutputFormat()) {
            this.outputFormat = other.outputFormat;
        }
        this.compressed = other.compressed;
        this.numBuckets = other.numBuckets;
        if (other.isSetSerdeInfo()) {
            this.serdeInfo = new SerDeInfo(other.serdeInfo);
        }
        if (other.isSetBucketCols()) {
            final List<String> __this__bucketCols = new ArrayList<String>();
            for (final String other_element2 : other.bucketCols) {
                __this__bucketCols.add(other_element2);
            }
            this.bucketCols = __this__bucketCols;
        }
        if (other.isSetSortCols()) {
            final List<Order> __this__sortCols = new ArrayList<Order>();
            for (final Order other_element3 : other.sortCols) {
                __this__sortCols.add(new Order(other_element3));
            }
            this.sortCols = __this__sortCols;
        }
        if (other.isSetParameters()) {
            final Map<String, String> __this__parameters = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element4 : other.parameters.entrySet()) {
                final String other_element_key = other_element4.getKey();
                final String other_element_value = other_element4.getValue();
                final String __this__parameters_copy_key = other_element_key;
                final String __this__parameters_copy_value = other_element_value;
                __this__parameters.put(__this__parameters_copy_key, __this__parameters_copy_value);
            }
            this.parameters = __this__parameters;
        }
        if (other.isSetSkewedInfo()) {
            this.skewedInfo = new SkewedInfo(other.skewedInfo);
        }
        this.storedAsSubDirectories = other.storedAsSubDirectories;
    }
    
    @Override
    public StorageDescriptor deepCopy() {
        return new StorageDescriptor(this);
    }
    
    @Override
    public void clear() {
        this.cols = null;
        this.location = null;
        this.inputFormat = null;
        this.outputFormat = null;
        this.setCompressedIsSet(false);
        this.setNumBucketsIsSet(this.compressed = false);
        this.numBuckets = 0;
        this.serdeInfo = null;
        this.bucketCols = null;
        this.sortCols = null;
        this.parameters = null;
        this.skewedInfo = null;
        this.setStoredAsSubDirectoriesIsSet(false);
        this.storedAsSubDirectories = false;
    }
    
    public int getColsSize() {
        return (this.cols == null) ? 0 : this.cols.size();
    }
    
    public Iterator<FieldSchema> getColsIterator() {
        return (this.cols == null) ? null : this.cols.iterator();
    }
    
    public void addToCols(final FieldSchema elem) {
        if (this.cols == null) {
            this.cols = new ArrayList<FieldSchema>();
        }
        this.cols.add(elem);
    }
    
    public List<FieldSchema> getCols() {
        return this.cols;
    }
    
    public void setCols(final List<FieldSchema> cols) {
        this.cols = cols;
    }
    
    public void unsetCols() {
        this.cols = null;
    }
    
    public boolean isSetCols() {
        return this.cols != null;
    }
    
    public void setColsIsSet(final boolean value) {
        if (!value) {
            this.cols = null;
        }
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public void unsetLocation() {
        this.location = null;
    }
    
    public boolean isSetLocation() {
        return this.location != null;
    }
    
    public void setLocationIsSet(final boolean value) {
        if (!value) {
            this.location = null;
        }
    }
    
    public String getInputFormat() {
        return this.inputFormat;
    }
    
    public void setInputFormat(final String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    public void unsetInputFormat() {
        this.inputFormat = null;
    }
    
    public boolean isSetInputFormat() {
        return this.inputFormat != null;
    }
    
    public void setInputFormatIsSet(final boolean value) {
        if (!value) {
            this.inputFormat = null;
        }
    }
    
    public String getOutputFormat() {
        return this.outputFormat;
    }
    
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public void unsetOutputFormat() {
        this.outputFormat = null;
    }
    
    public boolean isSetOutputFormat() {
        return this.outputFormat != null;
    }
    
    public void setOutputFormatIsSet(final boolean value) {
        if (!value) {
            this.outputFormat = null;
        }
    }
    
    public boolean isCompressed() {
        return this.compressed;
    }
    
    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
        this.setCompressedIsSet(true);
    }
    
    public void unsetCompressed() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetCompressed() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setCompressedIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getNumBuckets() {
        return this.numBuckets;
    }
    
    public void setNumBuckets(final int numBuckets) {
        this.numBuckets = numBuckets;
        this.setNumBucketsIsSet(true);
    }
    
    public void unsetNumBuckets() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetNumBuckets() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setNumBucketsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public SerDeInfo getSerdeInfo() {
        return this.serdeInfo;
    }
    
    public void setSerdeInfo(final SerDeInfo serdeInfo) {
        this.serdeInfo = serdeInfo;
    }
    
    public void unsetSerdeInfo() {
        this.serdeInfo = null;
    }
    
    public boolean isSetSerdeInfo() {
        return this.serdeInfo != null;
    }
    
    public void setSerdeInfoIsSet(final boolean value) {
        if (!value) {
            this.serdeInfo = null;
        }
    }
    
    public int getBucketColsSize() {
        return (this.bucketCols == null) ? 0 : this.bucketCols.size();
    }
    
    public Iterator<String> getBucketColsIterator() {
        return (this.bucketCols == null) ? null : this.bucketCols.iterator();
    }
    
    public void addToBucketCols(final String elem) {
        if (this.bucketCols == null) {
            this.bucketCols = new ArrayList<String>();
        }
        this.bucketCols.add(elem);
    }
    
    public List<String> getBucketCols() {
        return this.bucketCols;
    }
    
    public void setBucketCols(final List<String> bucketCols) {
        this.bucketCols = bucketCols;
    }
    
    public void unsetBucketCols() {
        this.bucketCols = null;
    }
    
    public boolean isSetBucketCols() {
        return this.bucketCols != null;
    }
    
    public void setBucketColsIsSet(final boolean value) {
        if (!value) {
            this.bucketCols = null;
        }
    }
    
    public int getSortColsSize() {
        return (this.sortCols == null) ? 0 : this.sortCols.size();
    }
    
    public Iterator<Order> getSortColsIterator() {
        return (this.sortCols == null) ? null : this.sortCols.iterator();
    }
    
    public void addToSortCols(final Order elem) {
        if (this.sortCols == null) {
            this.sortCols = new ArrayList<Order>();
        }
        this.sortCols.add(elem);
    }
    
    public List<Order> getSortCols() {
        return this.sortCols;
    }
    
    public void setSortCols(final List<Order> sortCols) {
        this.sortCols = sortCols;
    }
    
    public void unsetSortCols() {
        this.sortCols = null;
    }
    
    public boolean isSetSortCols() {
        return this.sortCols != null;
    }
    
    public void setSortColsIsSet(final boolean value) {
        if (!value) {
            this.sortCols = null;
        }
    }
    
    public int getParametersSize() {
        return (this.parameters == null) ? 0 : this.parameters.size();
    }
    
    public void putToParameters(final String key, final String val) {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, String>();
        }
        this.parameters.put(key, val);
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public void unsetParameters() {
        this.parameters = null;
    }
    
    public boolean isSetParameters() {
        return this.parameters != null;
    }
    
    public void setParametersIsSet(final boolean value) {
        if (!value) {
            this.parameters = null;
        }
    }
    
    public SkewedInfo getSkewedInfo() {
        return this.skewedInfo;
    }
    
    public void setSkewedInfo(final SkewedInfo skewedInfo) {
        this.skewedInfo = skewedInfo;
    }
    
    public void unsetSkewedInfo() {
        this.skewedInfo = null;
    }
    
    public boolean isSetSkewedInfo() {
        return this.skewedInfo != null;
    }
    
    public void setSkewedInfoIsSet(final boolean value) {
        if (!value) {
            this.skewedInfo = null;
        }
    }
    
    public boolean isStoredAsSubDirectories() {
        return this.storedAsSubDirectories;
    }
    
    public void setStoredAsSubDirectories(final boolean storedAsSubDirectories) {
        this.storedAsSubDirectories = storedAsSubDirectories;
        this.setStoredAsSubDirectoriesIsSet(true);
    }
    
    public void unsetStoredAsSubDirectories() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetStoredAsSubDirectories() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setStoredAsSubDirectoriesIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COLS: {
                if (value == null) {
                    this.unsetCols();
                    break;
                }
                this.setCols((List<FieldSchema>)value);
                break;
            }
            case LOCATION: {
                if (value == null) {
                    this.unsetLocation();
                    break;
                }
                this.setLocation((String)value);
                break;
            }
            case INPUT_FORMAT: {
                if (value == null) {
                    this.unsetInputFormat();
                    break;
                }
                this.setInputFormat((String)value);
                break;
            }
            case OUTPUT_FORMAT: {
                if (value == null) {
                    this.unsetOutputFormat();
                    break;
                }
                this.setOutputFormat((String)value);
                break;
            }
            case COMPRESSED: {
                if (value == null) {
                    this.unsetCompressed();
                    break;
                }
                this.setCompressed((boolean)value);
                break;
            }
            case NUM_BUCKETS: {
                if (value == null) {
                    this.unsetNumBuckets();
                    break;
                }
                this.setNumBuckets((int)value);
                break;
            }
            case SERDE_INFO: {
                if (value == null) {
                    this.unsetSerdeInfo();
                    break;
                }
                this.setSerdeInfo((SerDeInfo)value);
                break;
            }
            case BUCKET_COLS: {
                if (value == null) {
                    this.unsetBucketCols();
                    break;
                }
                this.setBucketCols((List<String>)value);
                break;
            }
            case SORT_COLS: {
                if (value == null) {
                    this.unsetSortCols();
                    break;
                }
                this.setSortCols((List<Order>)value);
                break;
            }
            case PARAMETERS: {
                if (value == null) {
                    this.unsetParameters();
                    break;
                }
                this.setParameters((Map<String, String>)value);
                break;
            }
            case SKEWED_INFO: {
                if (value == null) {
                    this.unsetSkewedInfo();
                    break;
                }
                this.setSkewedInfo((SkewedInfo)value);
                break;
            }
            case STORED_AS_SUB_DIRECTORIES: {
                if (value == null) {
                    this.unsetStoredAsSubDirectories();
                    break;
                }
                this.setStoredAsSubDirectories((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COLS: {
                return this.getCols();
            }
            case LOCATION: {
                return this.getLocation();
            }
            case INPUT_FORMAT: {
                return this.getInputFormat();
            }
            case OUTPUT_FORMAT: {
                return this.getOutputFormat();
            }
            case COMPRESSED: {
                return this.isCompressed();
            }
            case NUM_BUCKETS: {
                return this.getNumBuckets();
            }
            case SERDE_INFO: {
                return this.getSerdeInfo();
            }
            case BUCKET_COLS: {
                return this.getBucketCols();
            }
            case SORT_COLS: {
                return this.getSortCols();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case SKEWED_INFO: {
                return this.getSkewedInfo();
            }
            case STORED_AS_SUB_DIRECTORIES: {
                return this.isStoredAsSubDirectories();
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
            case COLS: {
                return this.isSetCols();
            }
            case LOCATION: {
                return this.isSetLocation();
            }
            case INPUT_FORMAT: {
                return this.isSetInputFormat();
            }
            case OUTPUT_FORMAT: {
                return this.isSetOutputFormat();
            }
            case COMPRESSED: {
                return this.isSetCompressed();
            }
            case NUM_BUCKETS: {
                return this.isSetNumBuckets();
            }
            case SERDE_INFO: {
                return this.isSetSerdeInfo();
            }
            case BUCKET_COLS: {
                return this.isSetBucketCols();
            }
            case SORT_COLS: {
                return this.isSetSortCols();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case SKEWED_INFO: {
                return this.isSetSkewedInfo();
            }
            case STORED_AS_SUB_DIRECTORIES: {
                return this.isSetStoredAsSubDirectories();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof StorageDescriptor && this.equals((StorageDescriptor)that);
    }
    
    public boolean equals(final StorageDescriptor that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_cols = this.isSetCols();
        final boolean that_present_cols = that.isSetCols();
        if (this_present_cols || that_present_cols) {
            if (!this_present_cols || !that_present_cols) {
                return false;
            }
            if (!this.cols.equals(that.cols)) {
                return false;
            }
        }
        final boolean this_present_location = this.isSetLocation();
        final boolean that_present_location = that.isSetLocation();
        if (this_present_location || that_present_location) {
            if (!this_present_location || !that_present_location) {
                return false;
            }
            if (!this.location.equals(that.location)) {
                return false;
            }
        }
        final boolean this_present_inputFormat = this.isSetInputFormat();
        final boolean that_present_inputFormat = that.isSetInputFormat();
        if (this_present_inputFormat || that_present_inputFormat) {
            if (!this_present_inputFormat || !that_present_inputFormat) {
                return false;
            }
            if (!this.inputFormat.equals(that.inputFormat)) {
                return false;
            }
        }
        final boolean this_present_outputFormat = this.isSetOutputFormat();
        final boolean that_present_outputFormat = that.isSetOutputFormat();
        if (this_present_outputFormat || that_present_outputFormat) {
            if (!this_present_outputFormat || !that_present_outputFormat) {
                return false;
            }
            if (!this.outputFormat.equals(that.outputFormat)) {
                return false;
            }
        }
        final boolean this_present_compressed = true;
        final boolean that_present_compressed = true;
        if (this_present_compressed || that_present_compressed) {
            if (!this_present_compressed || !that_present_compressed) {
                return false;
            }
            if (this.compressed != that.compressed) {
                return false;
            }
        }
        final boolean this_present_numBuckets = true;
        final boolean that_present_numBuckets = true;
        if (this_present_numBuckets || that_present_numBuckets) {
            if (!this_present_numBuckets || !that_present_numBuckets) {
                return false;
            }
            if (this.numBuckets != that.numBuckets) {
                return false;
            }
        }
        final boolean this_present_serdeInfo = this.isSetSerdeInfo();
        final boolean that_present_serdeInfo = that.isSetSerdeInfo();
        if (this_present_serdeInfo || that_present_serdeInfo) {
            if (!this_present_serdeInfo || !that_present_serdeInfo) {
                return false;
            }
            if (!this.serdeInfo.equals(that.serdeInfo)) {
                return false;
            }
        }
        final boolean this_present_bucketCols = this.isSetBucketCols();
        final boolean that_present_bucketCols = that.isSetBucketCols();
        if (this_present_bucketCols || that_present_bucketCols) {
            if (!this_present_bucketCols || !that_present_bucketCols) {
                return false;
            }
            if (!this.bucketCols.equals(that.bucketCols)) {
                return false;
            }
        }
        final boolean this_present_sortCols = this.isSetSortCols();
        final boolean that_present_sortCols = that.isSetSortCols();
        if (this_present_sortCols || that_present_sortCols) {
            if (!this_present_sortCols || !that_present_sortCols) {
                return false;
            }
            if (!this.sortCols.equals(that.sortCols)) {
                return false;
            }
        }
        final boolean this_present_parameters = this.isSetParameters();
        final boolean that_present_parameters = that.isSetParameters();
        if (this_present_parameters || that_present_parameters) {
            if (!this_present_parameters || !that_present_parameters) {
                return false;
            }
            if (!this.parameters.equals(that.parameters)) {
                return false;
            }
        }
        final boolean this_present_skewedInfo = this.isSetSkewedInfo();
        final boolean that_present_skewedInfo = that.isSetSkewedInfo();
        if (this_present_skewedInfo || that_present_skewedInfo) {
            if (!this_present_skewedInfo || !that_present_skewedInfo) {
                return false;
            }
            if (!this.skewedInfo.equals(that.skewedInfo)) {
                return false;
            }
        }
        final boolean this_present_storedAsSubDirectories = this.isSetStoredAsSubDirectories();
        final boolean that_present_storedAsSubDirectories = that.isSetStoredAsSubDirectories();
        if (this_present_storedAsSubDirectories || that_present_storedAsSubDirectories) {
            if (!this_present_storedAsSubDirectories || !that_present_storedAsSubDirectories) {
                return false;
            }
            if (this.storedAsSubDirectories != that.storedAsSubDirectories) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_cols = this.isSetCols();
        builder.append(present_cols);
        if (present_cols) {
            builder.append(this.cols);
        }
        final boolean present_location = this.isSetLocation();
        builder.append(present_location);
        if (present_location) {
            builder.append(this.location);
        }
        final boolean present_inputFormat = this.isSetInputFormat();
        builder.append(present_inputFormat);
        if (present_inputFormat) {
            builder.append(this.inputFormat);
        }
        final boolean present_outputFormat = this.isSetOutputFormat();
        builder.append(present_outputFormat);
        if (present_outputFormat) {
            builder.append(this.outputFormat);
        }
        final boolean present_compressed = true;
        builder.append(present_compressed);
        if (present_compressed) {
            builder.append(this.compressed);
        }
        final boolean present_numBuckets = true;
        builder.append(present_numBuckets);
        if (present_numBuckets) {
            builder.append(this.numBuckets);
        }
        final boolean present_serdeInfo = this.isSetSerdeInfo();
        builder.append(present_serdeInfo);
        if (present_serdeInfo) {
            builder.append(this.serdeInfo);
        }
        final boolean present_bucketCols = this.isSetBucketCols();
        builder.append(present_bucketCols);
        if (present_bucketCols) {
            builder.append(this.bucketCols);
        }
        final boolean present_sortCols = this.isSetSortCols();
        builder.append(present_sortCols);
        if (present_sortCols) {
            builder.append(this.sortCols);
        }
        final boolean present_parameters = this.isSetParameters();
        builder.append(present_parameters);
        if (present_parameters) {
            builder.append(this.parameters);
        }
        final boolean present_skewedInfo = this.isSetSkewedInfo();
        builder.append(present_skewedInfo);
        if (present_skewedInfo) {
            builder.append(this.skewedInfo);
        }
        final boolean present_storedAsSubDirectories = this.isSetStoredAsSubDirectories();
        builder.append(present_storedAsSubDirectories);
        if (present_storedAsSubDirectories) {
            builder.append(this.storedAsSubDirectories);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final StorageDescriptor other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final StorageDescriptor typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetCols()).compareTo(Boolean.valueOf(typedOther.isSetCols()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCols()) {
            lastComparison = TBaseHelper.compareTo(this.cols, typedOther.cols);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLocation()).compareTo(Boolean.valueOf(typedOther.isSetLocation()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLocation()) {
            lastComparison = TBaseHelper.compareTo(this.location, typedOther.location);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetInputFormat()).compareTo(Boolean.valueOf(typedOther.isSetInputFormat()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetInputFormat()) {
            lastComparison = TBaseHelper.compareTo(this.inputFormat, typedOther.inputFormat);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOutputFormat()).compareTo(Boolean.valueOf(typedOther.isSetOutputFormat()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOutputFormat()) {
            lastComparison = TBaseHelper.compareTo(this.outputFormat, typedOther.outputFormat);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCompressed()).compareTo(Boolean.valueOf(typedOther.isSetCompressed()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCompressed()) {
            lastComparison = TBaseHelper.compareTo(this.compressed, typedOther.compressed);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNumBuckets()).compareTo(Boolean.valueOf(typedOther.isSetNumBuckets()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNumBuckets()) {
            lastComparison = TBaseHelper.compareTo(this.numBuckets, typedOther.numBuckets);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSerdeInfo()).compareTo(Boolean.valueOf(typedOther.isSetSerdeInfo()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSerdeInfo()) {
            lastComparison = TBaseHelper.compareTo(this.serdeInfo, typedOther.serdeInfo);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetBucketCols()).compareTo(Boolean.valueOf(typedOther.isSetBucketCols()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetBucketCols()) {
            lastComparison = TBaseHelper.compareTo(this.bucketCols, typedOther.bucketCols);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSortCols()).compareTo(Boolean.valueOf(typedOther.isSetSortCols()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSortCols()) {
            lastComparison = TBaseHelper.compareTo(this.sortCols, typedOther.sortCols);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetParameters()).compareTo(Boolean.valueOf(typedOther.isSetParameters()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetParameters()) {
            lastComparison = TBaseHelper.compareTo(this.parameters, typedOther.parameters);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSkewedInfo()).compareTo(Boolean.valueOf(typedOther.isSetSkewedInfo()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSkewedInfo()) {
            lastComparison = TBaseHelper.compareTo(this.skewedInfo, typedOther.skewedInfo);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStoredAsSubDirectories()).compareTo(Boolean.valueOf(typedOther.isSetStoredAsSubDirectories()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStoredAsSubDirectories()) {
            lastComparison = TBaseHelper.compareTo(this.storedAsSubDirectories, typedOther.storedAsSubDirectories);
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
        StorageDescriptor.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        StorageDescriptor.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StorageDescriptor(");
        boolean first = true;
        sb.append("cols:");
        if (this.cols == null) {
            sb.append("null");
        }
        else {
            sb.append(this.cols);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("location:");
        if (this.location == null) {
            sb.append("null");
        }
        else {
            sb.append(this.location);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("inputFormat:");
        if (this.inputFormat == null) {
            sb.append("null");
        }
        else {
            sb.append(this.inputFormat);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("outputFormat:");
        if (this.outputFormat == null) {
            sb.append("null");
        }
        else {
            sb.append(this.outputFormat);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("compressed:");
        sb.append(this.compressed);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("numBuckets:");
        sb.append(this.numBuckets);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("serdeInfo:");
        if (this.serdeInfo == null) {
            sb.append("null");
        }
        else {
            sb.append(this.serdeInfo);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("bucketCols:");
        if (this.bucketCols == null) {
            sb.append("null");
        }
        else {
            sb.append(this.bucketCols);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("sortCols:");
        if (this.sortCols == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sortCols);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("parameters:");
        if (this.parameters == null) {
            sb.append("null");
        }
        else {
            sb.append(this.parameters);
        }
        first = false;
        if (this.isSetSkewedInfo()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("skewedInfo:");
            if (this.skewedInfo == null) {
                sb.append("null");
            }
            else {
                sb.append(this.skewedInfo);
            }
            first = false;
        }
        if (this.isSetStoredAsSubDirectories()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("storedAsSubDirectories:");
            sb.append(this.storedAsSubDirectories);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.serdeInfo != null) {
            this.serdeInfo.validate();
        }
        if (this.skewedInfo != null) {
            this.skewedInfo.validate();
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
        STRUCT_DESC = new TStruct("StorageDescriptor");
        COLS_FIELD_DESC = new TField("cols", (byte)15, (short)1);
        LOCATION_FIELD_DESC = new TField("location", (byte)11, (short)2);
        INPUT_FORMAT_FIELD_DESC = new TField("inputFormat", (byte)11, (short)3);
        OUTPUT_FORMAT_FIELD_DESC = new TField("outputFormat", (byte)11, (short)4);
        COMPRESSED_FIELD_DESC = new TField("compressed", (byte)2, (short)5);
        NUM_BUCKETS_FIELD_DESC = new TField("numBuckets", (byte)8, (short)6);
        SERDE_INFO_FIELD_DESC = new TField("serdeInfo", (byte)12, (short)7);
        BUCKET_COLS_FIELD_DESC = new TField("bucketCols", (byte)15, (short)8);
        SORT_COLS_FIELD_DESC = new TField("sortCols", (byte)15, (short)9);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)10);
        SKEWED_INFO_FIELD_DESC = new TField("skewedInfo", (byte)12, (short)11);
        STORED_AS_SUB_DIRECTORIES_FIELD_DESC = new TField("storedAsSubDirectories", (byte)2, (short)12);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new StorageDescriptorStandardSchemeFactory());
        StorageDescriptor.schemes.put(TupleScheme.class, new StorageDescriptorTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COLS, new FieldMetaData("cols", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, FieldSchema.class))));
        tmpMap.put(_Fields.LOCATION, new FieldMetaData("location", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.INPUT_FORMAT, new FieldMetaData("inputFormat", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OUTPUT_FORMAT, new FieldMetaData("outputFormat", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COMPRESSED, new FieldMetaData("compressed", (byte)3, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.NUM_BUCKETS, new FieldMetaData("numBuckets", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.SERDE_INFO, new FieldMetaData("serdeInfo", (byte)3, new StructMetaData((byte)12, SerDeInfo.class)));
        tmpMap.put(_Fields.BUCKET_COLS, new FieldMetaData("bucketCols", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.SORT_COLS, new FieldMetaData("sortCols", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, Order.class))));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.SKEWED_INFO, new FieldMetaData("skewedInfo", (byte)2, new StructMetaData((byte)12, SkewedInfo.class)));
        tmpMap.put(_Fields.STORED_AS_SUB_DIRECTORIES, new FieldMetaData("storedAsSubDirectories", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(StorageDescriptor.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COLS((short)1, "cols"), 
        LOCATION((short)2, "location"), 
        INPUT_FORMAT((short)3, "inputFormat"), 
        OUTPUT_FORMAT((short)4, "outputFormat"), 
        COMPRESSED((short)5, "compressed"), 
        NUM_BUCKETS((short)6, "numBuckets"), 
        SERDE_INFO((short)7, "serdeInfo"), 
        BUCKET_COLS((short)8, "bucketCols"), 
        SORT_COLS((short)9, "sortCols"), 
        PARAMETERS((short)10, "parameters"), 
        SKEWED_INFO((short)11, "skewedInfo"), 
        STORED_AS_SUB_DIRECTORIES((short)12, "storedAsSubDirectories");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COLS;
                }
                case 2: {
                    return _Fields.LOCATION;
                }
                case 3: {
                    return _Fields.INPUT_FORMAT;
                }
                case 4: {
                    return _Fields.OUTPUT_FORMAT;
                }
                case 5: {
                    return _Fields.COMPRESSED;
                }
                case 6: {
                    return _Fields.NUM_BUCKETS;
                }
                case 7: {
                    return _Fields.SERDE_INFO;
                }
                case 8: {
                    return _Fields.BUCKET_COLS;
                }
                case 9: {
                    return _Fields.SORT_COLS;
                }
                case 10: {
                    return _Fields.PARAMETERS;
                }
                case 11: {
                    return _Fields.SKEWED_INFO;
                }
                case 12: {
                    return _Fields.STORED_AS_SUB_DIRECTORIES;
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
    
    private static class StorageDescriptorStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public StorageDescriptorStandardScheme getScheme() {
            return new StorageDescriptorStandardScheme();
        }
    }
    
    private static class StorageDescriptorStandardScheme extends StandardScheme<StorageDescriptor>
    {
        @Override
        public void read(final TProtocol iprot, final StorageDescriptor struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list156 = iprot.readListBegin();
                            struct.cols = (List<FieldSchema>)new ArrayList(_list156.size);
                            for (int _i157 = 0; _i157 < _list156.size; ++_i157) {
                                final FieldSchema _elem158 = new FieldSchema();
                                _elem158.read(iprot);
                                struct.cols.add(_elem158);
                            }
                            iprot.readListEnd();
                            struct.setColsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.location = iprot.readString();
                            struct.setLocationIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.inputFormat = iprot.readString();
                            struct.setInputFormatIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.outputFormat = iprot.readString();
                            struct.setOutputFormatIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 2) {
                            struct.compressed = iprot.readBool();
                            struct.setCompressedIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.numBuckets = iprot.readI32();
                            struct.setNumBucketsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 12) {
                            struct.serdeInfo = new SerDeInfo();
                            struct.serdeInfo.read(iprot);
                            struct.setSerdeInfoIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 15) {
                            final TList _list157 = iprot.readListBegin();
                            struct.bucketCols = (List<String>)new ArrayList(_list157.size);
                            for (int _i158 = 0; _i158 < _list157.size; ++_i158) {
                                final String _elem159 = iprot.readString();
                                struct.bucketCols.add(_elem159);
                            }
                            iprot.readListEnd();
                            struct.setBucketColsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 15) {
                            final TList _list158 = iprot.readListBegin();
                            struct.sortCols = (List<Order>)new ArrayList(_list158.size);
                            for (int _i159 = 0; _i159 < _list158.size; ++_i159) {
                                final Order _elem160 = new Order();
                                _elem160.read(iprot);
                                struct.sortCols.add(_elem160);
                            }
                            iprot.readListEnd();
                            struct.setSortColsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 13) {
                            final TMap _map165 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map165.size);
                            for (int _i160 = 0; _i160 < _map165.size; ++_i160) {
                                final String _key167 = iprot.readString();
                                final String _val168 = iprot.readString();
                                struct.parameters.put(_key167, _val168);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 11: {
                        if (schemeField.type == 12) {
                            struct.skewedInfo = new SkewedInfo();
                            struct.skewedInfo.read(iprot);
                            struct.setSkewedInfoIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 12: {
                        if (schemeField.type == 2) {
                            struct.storedAsSubDirectories = iprot.readBool();
                            struct.setStoredAsSubDirectoriesIsSet(true);
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
        public void write(final TProtocol oprot, final StorageDescriptor struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(StorageDescriptor.STRUCT_DESC);
            if (struct.cols != null) {
                oprot.writeFieldBegin(StorageDescriptor.COLS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.cols.size()));
                for (final FieldSchema _iter169 : struct.cols) {
                    _iter169.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.location != null) {
                oprot.writeFieldBegin(StorageDescriptor.LOCATION_FIELD_DESC);
                oprot.writeString(struct.location);
                oprot.writeFieldEnd();
            }
            if (struct.inputFormat != null) {
                oprot.writeFieldBegin(StorageDescriptor.INPUT_FORMAT_FIELD_DESC);
                oprot.writeString(struct.inputFormat);
                oprot.writeFieldEnd();
            }
            if (struct.outputFormat != null) {
                oprot.writeFieldBegin(StorageDescriptor.OUTPUT_FORMAT_FIELD_DESC);
                oprot.writeString(struct.outputFormat);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(StorageDescriptor.COMPRESSED_FIELD_DESC);
            oprot.writeBool(struct.compressed);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(StorageDescriptor.NUM_BUCKETS_FIELD_DESC);
            oprot.writeI32(struct.numBuckets);
            oprot.writeFieldEnd();
            if (struct.serdeInfo != null) {
                oprot.writeFieldBegin(StorageDescriptor.SERDE_INFO_FIELD_DESC);
                struct.serdeInfo.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.bucketCols != null) {
                oprot.writeFieldBegin(StorageDescriptor.BUCKET_COLS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.bucketCols.size()));
                for (final String _iter170 : struct.bucketCols) {
                    oprot.writeString(_iter170);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.sortCols != null) {
                oprot.writeFieldBegin(StorageDescriptor.SORT_COLS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.sortCols.size()));
                for (final Order _iter171 : struct.sortCols) {
                    _iter171.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(StorageDescriptor.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter172 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter172.getKey());
                    oprot.writeString(_iter172.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.skewedInfo != null && struct.isSetSkewedInfo()) {
                oprot.writeFieldBegin(StorageDescriptor.SKEWED_INFO_FIELD_DESC);
                struct.skewedInfo.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetStoredAsSubDirectories()) {
                oprot.writeFieldBegin(StorageDescriptor.STORED_AS_SUB_DIRECTORIES_FIELD_DESC);
                oprot.writeBool(struct.storedAsSubDirectories);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class StorageDescriptorTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public StorageDescriptorTupleScheme getScheme() {
            return new StorageDescriptorTupleScheme();
        }
    }
    
    private static class StorageDescriptorTupleScheme extends TupleScheme<StorageDescriptor>
    {
        @Override
        public void write(final TProtocol prot, final StorageDescriptor struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetCols()) {
                optionals.set(0);
            }
            if (struct.isSetLocation()) {
                optionals.set(1);
            }
            if (struct.isSetInputFormat()) {
                optionals.set(2);
            }
            if (struct.isSetOutputFormat()) {
                optionals.set(3);
            }
            if (struct.isSetCompressed()) {
                optionals.set(4);
            }
            if (struct.isSetNumBuckets()) {
                optionals.set(5);
            }
            if (struct.isSetSerdeInfo()) {
                optionals.set(6);
            }
            if (struct.isSetBucketCols()) {
                optionals.set(7);
            }
            if (struct.isSetSortCols()) {
                optionals.set(8);
            }
            if (struct.isSetParameters()) {
                optionals.set(9);
            }
            if (struct.isSetSkewedInfo()) {
                optionals.set(10);
            }
            if (struct.isSetStoredAsSubDirectories()) {
                optionals.set(11);
            }
            oprot.writeBitSet(optionals, 12);
            if (struct.isSetCols()) {
                oprot.writeI32(struct.cols.size());
                for (final FieldSchema _iter173 : struct.cols) {
                    _iter173.write(oprot);
                }
            }
            if (struct.isSetLocation()) {
                oprot.writeString(struct.location);
            }
            if (struct.isSetInputFormat()) {
                oprot.writeString(struct.inputFormat);
            }
            if (struct.isSetOutputFormat()) {
                oprot.writeString(struct.outputFormat);
            }
            if (struct.isSetCompressed()) {
                oprot.writeBool(struct.compressed);
            }
            if (struct.isSetNumBuckets()) {
                oprot.writeI32(struct.numBuckets);
            }
            if (struct.isSetSerdeInfo()) {
                struct.serdeInfo.write(oprot);
            }
            if (struct.isSetBucketCols()) {
                oprot.writeI32(struct.bucketCols.size());
                for (final String _iter174 : struct.bucketCols) {
                    oprot.writeString(_iter174);
                }
            }
            if (struct.isSetSortCols()) {
                oprot.writeI32(struct.sortCols.size());
                for (final Order _iter175 : struct.sortCols) {
                    _iter175.write(oprot);
                }
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter176 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter176.getKey());
                    oprot.writeString(_iter176.getValue());
                }
            }
            if (struct.isSetSkewedInfo()) {
                struct.skewedInfo.write(oprot);
            }
            if (struct.isSetStoredAsSubDirectories()) {
                oprot.writeBool(struct.storedAsSubDirectories);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final StorageDescriptor struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(12);
            if (incoming.get(0)) {
                final TList _list177 = new TList((byte)12, iprot.readI32());
                struct.cols = (List<FieldSchema>)new ArrayList(_list177.size);
                for (int _i178 = 0; _i178 < _list177.size; ++_i178) {
                    final FieldSchema _elem179 = new FieldSchema();
                    _elem179.read(iprot);
                    struct.cols.add(_elem179);
                }
                struct.setColsIsSet(true);
            }
            if (incoming.get(1)) {
                struct.location = iprot.readString();
                struct.setLocationIsSet(true);
            }
            if (incoming.get(2)) {
                struct.inputFormat = iprot.readString();
                struct.setInputFormatIsSet(true);
            }
            if (incoming.get(3)) {
                struct.outputFormat = iprot.readString();
                struct.setOutputFormatIsSet(true);
            }
            if (incoming.get(4)) {
                struct.compressed = iprot.readBool();
                struct.setCompressedIsSet(true);
            }
            if (incoming.get(5)) {
                struct.numBuckets = iprot.readI32();
                struct.setNumBucketsIsSet(true);
            }
            if (incoming.get(6)) {
                struct.serdeInfo = new SerDeInfo();
                struct.serdeInfo.read(iprot);
                struct.setSerdeInfoIsSet(true);
            }
            if (incoming.get(7)) {
                final TList _list178 = new TList((byte)11, iprot.readI32());
                struct.bucketCols = (List<String>)new ArrayList(_list178.size);
                for (int _i179 = 0; _i179 < _list178.size; ++_i179) {
                    final String _elem180 = iprot.readString();
                    struct.bucketCols.add(_elem180);
                }
                struct.setBucketColsIsSet(true);
            }
            if (incoming.get(8)) {
                final TList _list179 = new TList((byte)12, iprot.readI32());
                struct.sortCols = (List<Order>)new ArrayList(_list179.size);
                for (int _i180 = 0; _i180 < _list179.size; ++_i180) {
                    final Order _elem181 = new Order();
                    _elem181.read(iprot);
                    struct.sortCols.add(_elem181);
                }
                struct.setSortColsIsSet(true);
            }
            if (incoming.get(9)) {
                final TMap _map186 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map186.size);
                for (int _i181 = 0; _i181 < _map186.size; ++_i181) {
                    final String _key188 = iprot.readString();
                    final String _val189 = iprot.readString();
                    struct.parameters.put(_key188, _val189);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(10)) {
                struct.skewedInfo = new SkewedInfo();
                struct.skewedInfo.read(iprot);
                struct.setSkewedInfoIsSet(true);
            }
            if (incoming.get(11)) {
                struct.storedAsSubDirectories = iprot.readBool();
                struct.setStoredAsSubDirectoriesIsSet(true);
            }
        }
    }
}
