// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import com.google.common.primitives.Bytes;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeStatsStruct;
import org.apache.hadoop.hive.serde2.StructObject;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;

public class LazyStruct extends LazyNonPrimitive<LazySimpleStructObjectInspector> implements StructObject, SerDeStatsStruct
{
    private static Log LOG;
    boolean parsed;
    long serializedSize;
    int[] startPosition;
    LazyObjectBase[] fields;
    boolean[] fieldInited;
    boolean missingFieldWarned;
    boolean extraFieldWarned;
    private transient List<Object> cachedList;
    
    public LazyStruct(final LazySimpleStructObjectInspector oi) {
        super(oi);
        this.missingFieldWarned = false;
        this.extraFieldWarned = false;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
        this.serializedSize = length;
    }
    
    private void parse() {
        final byte separator = ((LazySimpleStructObjectInspector)this.oi).getSeparator();
        final boolean lastColumnTakesRest = ((LazySimpleStructObjectInspector)this.oi).getLastColumnTakesRest();
        final boolean isEscaped = ((LazySimpleStructObjectInspector)this.oi).isEscaped();
        final byte escapeChar = ((LazySimpleStructObjectInspector)this.oi).getEscapeChar();
        if (this.fields == null) {
            this.initLazyFields(((LazySimpleStructObjectInspector)this.oi).getAllStructFieldRefs());
        }
        final int structByteEnd = this.start + this.length;
        int fieldId = 0;
        int fieldByteBegin = this.start;
        int fieldByteEnd = this.start;
        final byte[] bytes = this.bytes.getData();
        while (fieldByteEnd <= structByteEnd) {
            if (fieldByteEnd == structByteEnd || bytes[fieldByteEnd] == separator) {
                if (lastColumnTakesRest && fieldId == this.fields.length - 1) {
                    fieldByteEnd = structByteEnd;
                }
                this.startPosition[fieldId] = fieldByteBegin;
                if (++fieldId == this.fields.length || fieldByteEnd == structByteEnd) {
                    for (int i = fieldId; i <= this.fields.length; ++i) {
                        this.startPosition[i] = fieldByteEnd + 1;
                    }
                    break;
                }
                fieldByteBegin = fieldByteEnd + 1;
                ++fieldByteEnd;
            }
            else if (isEscaped && bytes[fieldByteEnd] == escapeChar && fieldByteEnd + 1 < structByteEnd) {
                fieldByteEnd += 2;
            }
            else {
                ++fieldByteEnd;
            }
        }
        if (!this.extraFieldWarned && fieldByteEnd < structByteEnd) {
            this.extraFieldWarned = true;
            LazyStruct.LOG.warn("Extra bytes detected at the end of the row! Ignoring similar problems.");
        }
        if (!this.missingFieldWarned && fieldId < this.fields.length) {
            this.missingFieldWarned = true;
            LazyStruct.LOG.info("Missing fields! Expected " + this.fields.length + " fields but " + "only got " + fieldId + "! Ignoring similar problems.");
        }
        Arrays.fill(this.fieldInited, false);
        this.parsed = true;
    }
    
    protected final void initLazyFields(final List<? extends StructField> fieldRefs) {
        this.fields = new LazyObjectBase[fieldRefs.size()];
        for (int i = 0; i < this.fields.length; ++i) {
            try {
                this.fields[i] = this.createLazyField(i, (StructField)fieldRefs.get(i));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.fieldInited = new boolean[this.fields.length];
        this.startPosition = new int[this.fields.length + 1];
    }
    
    protected LazyObjectBase createLazyField(final int fieldID, final StructField fieldRef) throws SerDeException {
        return LazyFactory.createLazyObject(fieldRef.getFieldObjectInspector());
    }
    
    @Override
    public Object getField(final int fieldID) {
        if (!this.parsed) {
            this.parse();
        }
        return this.uncheckedGetField(fieldID);
    }
    
    private Object uncheckedGetField(final int fieldID) {
        if (this.fieldInited[fieldID]) {
            return this.fields[fieldID].getObject();
        }
        this.fieldInited[fieldID] = true;
        final int fieldByteBegin = this.startPosition[fieldID];
        final int fieldLength = this.startPosition[fieldID + 1] - this.startPosition[fieldID] - 1;
        if (this.isNull(((LazySimpleStructObjectInspector)this.oi).getNullSequence(), this.bytes, fieldByteBegin, fieldLength)) {
            this.fields[fieldID].setNull();
        }
        else {
            this.fields[fieldID].init(this.bytes, fieldByteBegin, fieldLength);
        }
        return this.fields[fieldID].getObject();
    }
    
    @Override
    public List<Object> getFieldsAsList() {
        if (!this.parsed) {
            this.parse();
        }
        if (this.cachedList == null) {
            this.cachedList = new ArrayList<Object>();
        }
        else {
            this.cachedList.clear();
        }
        for (int i = 0; i < this.fields.length; ++i) {
            this.cachedList.add(this.uncheckedGetField(i));
        }
        return this.cachedList;
    }
    
    protected boolean getParsed() {
        return this.parsed;
    }
    
    protected void setParsed(final boolean parsed) {
        this.parsed = parsed;
    }
    
    protected LazyObjectBase[] getFields() {
        return this.fields;
    }
    
    protected void setFields(final LazyObject[] fields) {
        this.fields = fields;
    }
    
    protected boolean[] getFieldInited() {
        return this.fieldInited;
    }
    
    protected void setFieldInited(final boolean[] fieldInited) {
        this.fieldInited = fieldInited;
    }
    
    @Override
    public long getRawDataSerializedSize() {
        return this.serializedSize;
    }
    
    public void parseMultiDelimit(final byte[] rawRow, final byte[] fieldDelimit) {
        if (rawRow == null || fieldDelimit == null) {
            return;
        }
        if (this.fields == null) {
            final List<? extends StructField> fieldRefs = ((LazySimpleStructObjectInspector)this.oi).getAllStructFieldRefs();
            this.fields = new LazyObject[fieldRefs.size()];
            for (int i = 0; i < this.fields.length; ++i) {
                this.fields[i] = LazyFactory.createLazyObject(((StructField)fieldRefs.get(i)).getFieldObjectInspector());
            }
            this.fieldInited = new boolean[this.fields.length];
            this.startPosition = new int[this.fields.length + 1];
        }
        final int[] delimitIndexes = this.findIndexes(rawRow, fieldDelimit);
        final int diff = fieldDelimit.length - 1;
        this.startPosition[0] = 0;
        for (int j = 1; j < this.fields.length; ++j) {
            if (delimitIndexes[j - 1] != -1) {
                final int start = delimitIndexes[j - 1] + fieldDelimit.length;
                this.startPosition[j] = start - j * diff;
            }
            else {
                this.startPosition[j] = this.length + 1;
            }
        }
        this.startPosition[this.fields.length] = this.length + 1;
        Arrays.fill(this.fieldInited, false);
        this.parsed = true;
    }
    
    private int[] findIndexes(byte[] array, final byte[] target) {
        if (this.fields.length <= 1) {
            return new int[0];
        }
        final int[] indexes = new int[this.fields.length - 1];
        Arrays.fill(indexes, -1);
        indexes[0] = Bytes.indexOf(array, target);
        if (indexes[0] == -1) {
            return indexes;
        }
        int indexInNewArray = indexes[0];
        for (int i = 1; i < indexes.length; ++i) {
            array = Arrays.copyOfRange(array, indexInNewArray + target.length, array.length);
            indexInNewArray = Bytes.indexOf(array, target);
            if (indexInNewArray == -1) {
                break;
            }
            indexes[i] = indexInNewArray + indexes[i - 1] + target.length;
        }
        return indexes;
    }
    
    public byte[] getBytes() {
        return this.bytes.getData();
    }
    
    static {
        LazyStruct.LOG = LogFactory.getLog(LazyStruct.class.getName());
    }
}
