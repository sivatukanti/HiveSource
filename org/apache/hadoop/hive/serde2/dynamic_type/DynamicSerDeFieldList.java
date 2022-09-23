// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.thrift.SkippableTProtocol;
import java.util.ArrayList;
import org.apache.thrift.protocol.TProtocol;
import java.util.HashMap;
import org.apache.thrift.protocol.TField;
import java.util.Map;
import java.io.Serializable;

public class DynamicSerDeFieldList extends DynamicSerDeSimpleNode implements Serializable
{
    private Map<Integer, DynamicSerDeTypeBase> types_by_id;
    private Map<String, DynamicSerDeTypeBase> types_by_column_name;
    private DynamicSerDeTypeBase[] ordered_types;
    private Map<String, Integer> ordered_column_id_by_name;
    protected boolean isRealThrift;
    protected boolean[] fieldsPresent;
    TField field;
    
    public DynamicSerDeFieldList(final int i) {
        super(i);
        this.types_by_id = null;
        this.types_by_column_name = null;
        this.ordered_types = null;
        this.ordered_column_id_by_name = null;
        this.isRealThrift = false;
        this.field = new TField();
    }
    
    public DynamicSerDeFieldList(final thrift_grammar p, final int i) {
        super(p, i);
        this.types_by_id = null;
        this.types_by_column_name = null;
        this.ordered_types = null;
        this.ordered_column_id_by_name = null;
        this.isRealThrift = false;
        this.field = new TField();
    }
    
    private DynamicSerDeField getField(final int i) {
        return (DynamicSerDeField)this.jjtGetChild(i);
    }
    
    public final DynamicSerDeField[] getChildren() {
        final int size = this.jjtGetNumChildren();
        final DynamicSerDeField[] result = new DynamicSerDeField[size];
        for (int i = 0; i < size; ++i) {
            result[i] = (DynamicSerDeField)this.jjtGetChild(i);
        }
        return result;
    }
    
    private int getNumFields() {
        return this.jjtGetNumChildren();
    }
    
    public void initialize() {
        if (this.types_by_id == null) {
            this.types_by_id = new HashMap<Integer, DynamicSerDeTypeBase>();
            this.types_by_column_name = new HashMap<String, DynamicSerDeTypeBase>();
            this.ordered_types = new DynamicSerDeTypeBase[this.jjtGetNumChildren()];
            this.ordered_column_id_by_name = new HashMap<String, Integer>();
            for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
                final DynamicSerDeField mt = this.getField(i);
                final DynamicSerDeTypeBase type = mt.getFieldType().getMyType();
                type.initialize();
                type.fieldid = mt.fieldid;
                type.name = mt.name;
                this.types_by_id.put(mt.fieldid, type);
                this.types_by_column_name.put(mt.name, type);
                this.ordered_types[i] = type;
                this.ordered_column_id_by_name.put(mt.name, i);
            }
        }
    }
    
    private DynamicSerDeTypeBase getFieldByFieldId(final int i) {
        return this.types_by_id.get(i);
    }
    
    protected DynamicSerDeTypeBase getFieldByName(final String fieldname) {
        return this.types_by_column_name.get(fieldname);
    }
    
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        ArrayList<Object> struct = null;
        if (reuse == null) {
            struct = new ArrayList<Object>(this.getNumFields());
            for (final DynamicSerDeTypeBase orderedType : this.ordered_types) {
                struct.add(null);
            }
        }
        else {
            struct = (ArrayList<Object>)reuse;
            assert struct.size() == this.ordered_types.length;
        }
        final boolean fastSkips = iprot instanceof SkippableTProtocol;
        boolean stopSeen = false;
        if (this.fieldsPresent == null) {
            this.fieldsPresent = new boolean[this.ordered_types.length];
        }
        Arrays.fill(this.fieldsPresent, false);
        for (int i = 0; i < this.getNumFields(); ++i) {
            DynamicSerDeTypeBase mt = null;
            TField field = null;
            if (!this.isRealThrift && this.getField(i).isSkippable()) {
                mt = this.ordered_types[i];
                if (fastSkips) {
                    ((SkippableTProtocol)iprot).skip(mt.getType());
                }
                else {
                    TProtocolUtil.skip(iprot, mt.getType());
                }
                struct.set(i, null);
            }
            else {
                field = iprot.readFieldBegin();
                if (field.type >= 0) {
                    if (field.type == 0) {
                        stopSeen = true;
                        break;
                    }
                    mt = this.getFieldByFieldId(field.id);
                    if (mt == null) {
                        System.err.println("ERROR for fieldid: " + field.id + " system has no knowledge of this field which is of type : " + field.type);
                        TProtocolUtil.skip(iprot, field.type);
                        continue;
                    }
                }
                int orderedId = -1;
                if (field.type < 0) {
                    mt = this.ordered_types[i];
                    orderedId = i;
                }
                else {
                    orderedId = this.ordered_column_id_by_name.get(mt.name);
                }
                struct.set(orderedId, mt.deserialize(struct.get(orderedId), iprot));
                iprot.readFieldEnd();
                this.fieldsPresent[orderedId] = true;
            }
        }
        for (int i = 0; i < this.ordered_types.length; ++i) {
            if (!this.fieldsPresent[i]) {
                struct.set(i, null);
            }
        }
        if (!stopSeen) {
            iprot.readFieldBegin();
        }
        return struct;
    }
    
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        assert oi instanceof StructObjectInspector;
        final StructObjectInspector soi = (StructObjectInspector)oi;
        final boolean writeNulls = oprot instanceof WriteNullsProtocol;
        final List<? extends StructField> fields = soi.getAllStructFieldRefs();
        if (fields.size() != this.ordered_types.length) {
            throw new SerDeException("Trying to serialize " + fields.size() + " fields into a struct with " + this.ordered_types.length + " object=" + o + " objectinspector=" + oi.getTypeName());
        }
        for (int i = 0; i < fields.size(); ++i) {
            final Object f = soi.getStructFieldData(o, (StructField)fields.get(i));
            final DynamicSerDeTypeBase mt = this.ordered_types[i];
            if (f != null || writeNulls) {
                oprot.writeFieldBegin(this.field = new TField(mt.name, mt.getType(), (short)mt.fieldid));
                if (f == null) {
                    ((WriteNullsProtocol)oprot).writeNull();
                }
                else {
                    mt.serialize(f, ((StructField)fields.get(i)).getFieldObjectInspector(), oprot);
                }
                oprot.writeFieldEnd();
            }
        }
        oprot.writeFieldStop();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        String prefix = "";
        for (final DynamicSerDeField t : this.getChildren()) {
            result.append(prefix + t.fieldid + ":" + t.getFieldType().getMyType().toString() + " " + t.name);
            prefix = ",";
        }
        return result.toString();
    }
}
