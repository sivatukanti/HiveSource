// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.service;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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

public class HiveClusterStatus implements TBase<HiveClusterStatus, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TASK_TRACKERS_FIELD_DESC;
    private static final TField MAP_TASKS_FIELD_DESC;
    private static final TField REDUCE_TASKS_FIELD_DESC;
    private static final TField MAX_MAP_TASKS_FIELD_DESC;
    private static final TField MAX_REDUCE_TASKS_FIELD_DESC;
    private static final TField STATE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int taskTrackers;
    private int mapTasks;
    private int reduceTasks;
    private int maxMapTasks;
    private int maxReduceTasks;
    private JobTrackerState state;
    private static final int __TASKTRACKERS_ISSET_ID = 0;
    private static final int __MAPTASKS_ISSET_ID = 1;
    private static final int __REDUCETASKS_ISSET_ID = 2;
    private static final int __MAXMAPTASKS_ISSET_ID = 3;
    private static final int __MAXREDUCETASKS_ISSET_ID = 4;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HiveClusterStatus() {
        this.__isset_bitfield = 0;
    }
    
    public HiveClusterStatus(final int taskTrackers, final int mapTasks, final int reduceTasks, final int maxMapTasks, final int maxReduceTasks, final JobTrackerState state) {
        this();
        this.taskTrackers = taskTrackers;
        this.setTaskTrackersIsSet(true);
        this.mapTasks = mapTasks;
        this.setMapTasksIsSet(true);
        this.reduceTasks = reduceTasks;
        this.setReduceTasksIsSet(true);
        this.maxMapTasks = maxMapTasks;
        this.setMaxMapTasksIsSet(true);
        this.maxReduceTasks = maxReduceTasks;
        this.setMaxReduceTasksIsSet(true);
        this.state = state;
    }
    
    public HiveClusterStatus(final HiveClusterStatus other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.taskTrackers = other.taskTrackers;
        this.mapTasks = other.mapTasks;
        this.reduceTasks = other.reduceTasks;
        this.maxMapTasks = other.maxMapTasks;
        this.maxReduceTasks = other.maxReduceTasks;
        if (other.isSetState()) {
            this.state = other.state;
        }
    }
    
    @Override
    public HiveClusterStatus deepCopy() {
        return new HiveClusterStatus(this);
    }
    
    @Override
    public void clear() {
        this.setTaskTrackersIsSet(false);
        this.taskTrackers = 0;
        this.setMapTasksIsSet(false);
        this.mapTasks = 0;
        this.setReduceTasksIsSet(false);
        this.reduceTasks = 0;
        this.setMaxMapTasksIsSet(false);
        this.maxMapTasks = 0;
        this.setMaxReduceTasksIsSet(false);
        this.maxReduceTasks = 0;
        this.state = null;
    }
    
    public int getTaskTrackers() {
        return this.taskTrackers;
    }
    
    public void setTaskTrackers(final int taskTrackers) {
        this.taskTrackers = taskTrackers;
        this.setTaskTrackersIsSet(true);
    }
    
    public void unsetTaskTrackers() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetTaskTrackers() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setTaskTrackersIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getMapTasks() {
        return this.mapTasks;
    }
    
    public void setMapTasks(final int mapTasks) {
        this.mapTasks = mapTasks;
        this.setMapTasksIsSet(true);
    }
    
    public void unsetMapTasks() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetMapTasks() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setMapTasksIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public int getReduceTasks() {
        return this.reduceTasks;
    }
    
    public void setReduceTasks(final int reduceTasks) {
        this.reduceTasks = reduceTasks;
        this.setReduceTasksIsSet(true);
    }
    
    public void unsetReduceTasks() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetReduceTasks() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setReduceTasksIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    public int getMaxMapTasks() {
        return this.maxMapTasks;
    }
    
    public void setMaxMapTasks(final int maxMapTasks) {
        this.maxMapTasks = maxMapTasks;
        this.setMaxMapTasksIsSet(true);
    }
    
    public void unsetMaxMapTasks() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetMaxMapTasks() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setMaxMapTasksIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
    }
    
    public int getMaxReduceTasks() {
        return this.maxReduceTasks;
    }
    
    public void setMaxReduceTasks(final int maxReduceTasks) {
        this.maxReduceTasks = maxReduceTasks;
        this.setMaxReduceTasksIsSet(true);
    }
    
    public void unsetMaxReduceTasks() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 4);
    }
    
    public boolean isSetMaxReduceTasks() {
        return EncodingUtils.testBit(this.__isset_bitfield, 4);
    }
    
    public void setMaxReduceTasksIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 4, value);
    }
    
    public JobTrackerState getState() {
        return this.state;
    }
    
    public void setState(final JobTrackerState state) {
        this.state = state;
    }
    
    public void unsetState() {
        this.state = null;
    }
    
    public boolean isSetState() {
        return this.state != null;
    }
    
    public void setStateIsSet(final boolean value) {
        if (!value) {
            this.state = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TASK_TRACKERS: {
                if (value == null) {
                    this.unsetTaskTrackers();
                    break;
                }
                this.setTaskTrackers((int)value);
                break;
            }
            case MAP_TASKS: {
                if (value == null) {
                    this.unsetMapTasks();
                    break;
                }
                this.setMapTasks((int)value);
                break;
            }
            case REDUCE_TASKS: {
                if (value == null) {
                    this.unsetReduceTasks();
                    break;
                }
                this.setReduceTasks((int)value);
                break;
            }
            case MAX_MAP_TASKS: {
                if (value == null) {
                    this.unsetMaxMapTasks();
                    break;
                }
                this.setMaxMapTasks((int)value);
                break;
            }
            case MAX_REDUCE_TASKS: {
                if (value == null) {
                    this.unsetMaxReduceTasks();
                    break;
                }
                this.setMaxReduceTasks((int)value);
                break;
            }
            case STATE: {
                if (value == null) {
                    this.unsetState();
                    break;
                }
                this.setState((JobTrackerState)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TASK_TRACKERS: {
                return this.getTaskTrackers();
            }
            case MAP_TASKS: {
                return this.getMapTasks();
            }
            case REDUCE_TASKS: {
                return this.getReduceTasks();
            }
            case MAX_MAP_TASKS: {
                return this.getMaxMapTasks();
            }
            case MAX_REDUCE_TASKS: {
                return this.getMaxReduceTasks();
            }
            case STATE: {
                return this.getState();
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
            case TASK_TRACKERS: {
                return this.isSetTaskTrackers();
            }
            case MAP_TASKS: {
                return this.isSetMapTasks();
            }
            case REDUCE_TASKS: {
                return this.isSetReduceTasks();
            }
            case MAX_MAP_TASKS: {
                return this.isSetMaxMapTasks();
            }
            case MAX_REDUCE_TASKS: {
                return this.isSetMaxReduceTasks();
            }
            case STATE: {
                return this.isSetState();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HiveClusterStatus && this.equals((HiveClusterStatus)that);
    }
    
    public boolean equals(final HiveClusterStatus that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_taskTrackers = true;
        final boolean that_present_taskTrackers = true;
        if (this_present_taskTrackers || that_present_taskTrackers) {
            if (!this_present_taskTrackers || !that_present_taskTrackers) {
                return false;
            }
            if (this.taskTrackers != that.taskTrackers) {
                return false;
            }
        }
        final boolean this_present_mapTasks = true;
        final boolean that_present_mapTasks = true;
        if (this_present_mapTasks || that_present_mapTasks) {
            if (!this_present_mapTasks || !that_present_mapTasks) {
                return false;
            }
            if (this.mapTasks != that.mapTasks) {
                return false;
            }
        }
        final boolean this_present_reduceTasks = true;
        final boolean that_present_reduceTasks = true;
        if (this_present_reduceTasks || that_present_reduceTasks) {
            if (!this_present_reduceTasks || !that_present_reduceTasks) {
                return false;
            }
            if (this.reduceTasks != that.reduceTasks) {
                return false;
            }
        }
        final boolean this_present_maxMapTasks = true;
        final boolean that_present_maxMapTasks = true;
        if (this_present_maxMapTasks || that_present_maxMapTasks) {
            if (!this_present_maxMapTasks || !that_present_maxMapTasks) {
                return false;
            }
            if (this.maxMapTasks != that.maxMapTasks) {
                return false;
            }
        }
        final boolean this_present_maxReduceTasks = true;
        final boolean that_present_maxReduceTasks = true;
        if (this_present_maxReduceTasks || that_present_maxReduceTasks) {
            if (!this_present_maxReduceTasks || !that_present_maxReduceTasks) {
                return false;
            }
            if (this.maxReduceTasks != that.maxReduceTasks) {
                return false;
            }
        }
        final boolean this_present_state = this.isSetState();
        final boolean that_present_state = that.isSetState();
        if (this_present_state || that_present_state) {
            if (!this_present_state || !that_present_state) {
                return false;
            }
            if (!this.state.equals(that.state)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_taskTrackers = true;
        builder.append(present_taskTrackers);
        if (present_taskTrackers) {
            builder.append(this.taskTrackers);
        }
        final boolean present_mapTasks = true;
        builder.append(present_mapTasks);
        if (present_mapTasks) {
            builder.append(this.mapTasks);
        }
        final boolean present_reduceTasks = true;
        builder.append(present_reduceTasks);
        if (present_reduceTasks) {
            builder.append(this.reduceTasks);
        }
        final boolean present_maxMapTasks = true;
        builder.append(present_maxMapTasks);
        if (present_maxMapTasks) {
            builder.append(this.maxMapTasks);
        }
        final boolean present_maxReduceTasks = true;
        builder.append(present_maxReduceTasks);
        if (present_maxReduceTasks) {
            builder.append(this.maxReduceTasks);
        }
        final boolean present_state = this.isSetState();
        builder.append(present_state);
        if (present_state) {
            builder.append(this.state.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HiveClusterStatus other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HiveClusterStatus typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTaskTrackers()).compareTo(Boolean.valueOf(typedOther.isSetTaskTrackers()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTaskTrackers()) {
            lastComparison = TBaseHelper.compareTo(this.taskTrackers, typedOther.taskTrackers);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMapTasks()).compareTo(Boolean.valueOf(typedOther.isSetMapTasks()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMapTasks()) {
            lastComparison = TBaseHelper.compareTo(this.mapTasks, typedOther.mapTasks);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetReduceTasks()).compareTo(Boolean.valueOf(typedOther.isSetReduceTasks()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetReduceTasks()) {
            lastComparison = TBaseHelper.compareTo(this.reduceTasks, typedOther.reduceTasks);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMaxMapTasks()).compareTo(Boolean.valueOf(typedOther.isSetMaxMapTasks()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxMapTasks()) {
            lastComparison = TBaseHelper.compareTo(this.maxMapTasks, typedOther.maxMapTasks);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMaxReduceTasks()).compareTo(Boolean.valueOf(typedOther.isSetMaxReduceTasks()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxReduceTasks()) {
            lastComparison = TBaseHelper.compareTo(this.maxReduceTasks, typedOther.maxReduceTasks);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetState()).compareTo(Boolean.valueOf(typedOther.isSetState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetState()) {
            lastComparison = TBaseHelper.compareTo(this.state, typedOther.state);
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
        HiveClusterStatus.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HiveClusterStatus.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HiveClusterStatus(");
        boolean first = true;
        sb.append("taskTrackers:");
        sb.append(this.taskTrackers);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("mapTasks:");
        sb.append(this.mapTasks);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("reduceTasks:");
        sb.append(this.reduceTasks);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("maxMapTasks:");
        sb.append(this.maxMapTasks);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("maxReduceTasks:");
        sb.append(this.maxReduceTasks);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("state:");
        if (this.state == null) {
            sb.append("null");
        }
        else {
            sb.append(this.state);
        }
        first = false;
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("HiveClusterStatus");
        TASK_TRACKERS_FIELD_DESC = new TField("taskTrackers", (byte)8, (short)1);
        MAP_TASKS_FIELD_DESC = new TField("mapTasks", (byte)8, (short)2);
        REDUCE_TASKS_FIELD_DESC = new TField("reduceTasks", (byte)8, (short)3);
        MAX_MAP_TASKS_FIELD_DESC = new TField("maxMapTasks", (byte)8, (short)4);
        MAX_REDUCE_TASKS_FIELD_DESC = new TField("maxReduceTasks", (byte)8, (short)5);
        STATE_FIELD_DESC = new TField("state", (byte)8, (short)6);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HiveClusterStatusStandardSchemeFactory());
        HiveClusterStatus.schemes.put(TupleScheme.class, new HiveClusterStatusTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TASK_TRACKERS, new FieldMetaData("taskTrackers", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.MAP_TASKS, new FieldMetaData("mapTasks", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.REDUCE_TASKS, new FieldMetaData("reduceTasks", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.MAX_MAP_TASKS, new FieldMetaData("maxMapTasks", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.MAX_REDUCE_TASKS, new FieldMetaData("maxReduceTasks", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.STATE, new FieldMetaData("state", (byte)3, new EnumMetaData((byte)16, JobTrackerState.class)));
        FieldMetaData.addStructMetaDataMap(HiveClusterStatus.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TASK_TRACKERS((short)1, "taskTrackers"), 
        MAP_TASKS((short)2, "mapTasks"), 
        REDUCE_TASKS((short)3, "reduceTasks"), 
        MAX_MAP_TASKS((short)4, "maxMapTasks"), 
        MAX_REDUCE_TASKS((short)5, "maxReduceTasks"), 
        STATE((short)6, "state");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TASK_TRACKERS;
                }
                case 2: {
                    return _Fields.MAP_TASKS;
                }
                case 3: {
                    return _Fields.REDUCE_TASKS;
                }
                case 4: {
                    return _Fields.MAX_MAP_TASKS;
                }
                case 5: {
                    return _Fields.MAX_REDUCE_TASKS;
                }
                case 6: {
                    return _Fields.STATE;
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
    
    private static class HiveClusterStatusStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveClusterStatusStandardScheme getScheme() {
            return new HiveClusterStatusStandardScheme();
        }
    }
    
    private static class HiveClusterStatusStandardScheme extends StandardScheme<HiveClusterStatus>
    {
        @Override
        public void read(final TProtocol iprot, final HiveClusterStatus struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.taskTrackers = iprot.readI32();
                            struct.setTaskTrackersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.mapTasks = iprot.readI32();
                            struct.setMapTasksIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 8) {
                            struct.reduceTasks = iprot.readI32();
                            struct.setReduceTasksIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.maxMapTasks = iprot.readI32();
                            struct.setMaxMapTasksIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.maxReduceTasks = iprot.readI32();
                            struct.setMaxReduceTasksIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.state = JobTrackerState.findByValue(iprot.readI32());
                            struct.setStateIsSet(true);
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
        public void write(final TProtocol oprot, final HiveClusterStatus struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HiveClusterStatus.STRUCT_DESC);
            oprot.writeFieldBegin(HiveClusterStatus.TASK_TRACKERS_FIELD_DESC);
            oprot.writeI32(struct.taskTrackers);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HiveClusterStatus.MAP_TASKS_FIELD_DESC);
            oprot.writeI32(struct.mapTasks);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HiveClusterStatus.REDUCE_TASKS_FIELD_DESC);
            oprot.writeI32(struct.reduceTasks);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HiveClusterStatus.MAX_MAP_TASKS_FIELD_DESC);
            oprot.writeI32(struct.maxMapTasks);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HiveClusterStatus.MAX_REDUCE_TASKS_FIELD_DESC);
            oprot.writeI32(struct.maxReduceTasks);
            oprot.writeFieldEnd();
            if (struct.state != null) {
                oprot.writeFieldBegin(HiveClusterStatus.STATE_FIELD_DESC);
                oprot.writeI32(struct.state.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HiveClusterStatusTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveClusterStatusTupleScheme getScheme() {
            return new HiveClusterStatusTupleScheme();
        }
    }
    
    private static class HiveClusterStatusTupleScheme extends TupleScheme<HiveClusterStatus>
    {
        @Override
        public void write(final TProtocol prot, final HiveClusterStatus struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetTaskTrackers()) {
                optionals.set(0);
            }
            if (struct.isSetMapTasks()) {
                optionals.set(1);
            }
            if (struct.isSetReduceTasks()) {
                optionals.set(2);
            }
            if (struct.isSetMaxMapTasks()) {
                optionals.set(3);
            }
            if (struct.isSetMaxReduceTasks()) {
                optionals.set(4);
            }
            if (struct.isSetState()) {
                optionals.set(5);
            }
            oprot.writeBitSet(optionals, 6);
            if (struct.isSetTaskTrackers()) {
                oprot.writeI32(struct.taskTrackers);
            }
            if (struct.isSetMapTasks()) {
                oprot.writeI32(struct.mapTasks);
            }
            if (struct.isSetReduceTasks()) {
                oprot.writeI32(struct.reduceTasks);
            }
            if (struct.isSetMaxMapTasks()) {
                oprot.writeI32(struct.maxMapTasks);
            }
            if (struct.isSetMaxReduceTasks()) {
                oprot.writeI32(struct.maxReduceTasks);
            }
            if (struct.isSetState()) {
                oprot.writeI32(struct.state.getValue());
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HiveClusterStatus struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(6);
            if (incoming.get(0)) {
                struct.taskTrackers = iprot.readI32();
                struct.setTaskTrackersIsSet(true);
            }
            if (incoming.get(1)) {
                struct.mapTasks = iprot.readI32();
                struct.setMapTasksIsSet(true);
            }
            if (incoming.get(2)) {
                struct.reduceTasks = iprot.readI32();
                struct.setReduceTasksIsSet(true);
            }
            if (incoming.get(3)) {
                struct.maxMapTasks = iprot.readI32();
                struct.setMaxMapTasksIsSet(true);
            }
            if (incoming.get(4)) {
                struct.maxReduceTasks = iprot.readI32();
                struct.setMaxReduceTasksIsSet(true);
            }
            if (incoming.get(5)) {
                struct.state = JobTrackerState.findByValue(iprot.readI32());
                struct.setStateIsSet(true);
            }
        }
    }
}
