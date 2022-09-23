// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazyUnionObjectInspector;

public class LazyUnion extends LazyNonPrimitive<LazyUnionObjectInspector>
{
    private boolean parsed;
    private int startPosition;
    private Object field;
    private byte tag;
    private boolean fieldInited;
    private boolean fieldSet;
    
    public LazyUnion(final LazyUnionObjectInspector oi) {
        super(oi);
        this.fieldInited = false;
        this.fieldSet = false;
    }
    
    public LazyUnion(final LazyUnionObjectInspector oi, final byte tag, final Object field) {
        super(oi);
        this.fieldInited = false;
        this.fieldSet = false;
        this.field = field;
        this.tag = tag;
        this.fieldSet = true;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.parsed = false;
    }
    
    private void parse() {
        final byte separator = ((LazyUnionObjectInspector)this.oi).getSeparator();
        final boolean isEscaped = ((LazyUnionObjectInspector)this.oi).isEscaped();
        final byte escapeChar = ((LazyUnionObjectInspector)this.oi).getEscapeChar();
        boolean tagStarted = false;
        boolean tagParsed = false;
        int tagStart = -1;
        int tagEnd = -1;
        final int unionByteEnd = this.start + this.length;
        int fieldByteEnd = this.start;
        final byte[] bytes = this.bytes.getData();
        while (fieldByteEnd < unionByteEnd) {
            if (bytes[fieldByteEnd] != separator) {
                if (isEscaped && bytes[fieldByteEnd] == escapeChar && fieldByteEnd + 1 < unionByteEnd) {
                    ++fieldByteEnd;
                }
                else if (!tagStarted) {
                    tagStart = fieldByteEnd;
                    tagStarted = true;
                }
            }
            else if (!tagParsed) {
                tagEnd = fieldByteEnd - 1;
                this.startPosition = fieldByteEnd + 1;
                tagParsed = true;
            }
            ++fieldByteEnd;
        }
        this.tag = LazyByte.parseByte(bytes, tagStart, tagEnd - tagStart + 1);
        this.field = LazyFactory.createLazyObject(((LazyUnionObjectInspector)this.oi).getObjectInspectors().get(this.tag));
        this.fieldInited = false;
        this.parsed = true;
    }
    
    private Object uncheckedGetField() {
        final LazyObject field = (LazyObject)this.field;
        if (this.fieldInited) {
            return field.getObject();
        }
        this.fieldInited = true;
        final int fieldStart = this.startPosition;
        final int fieldLength = this.start + this.length - this.startPosition;
        if (this.isNull(((LazyUnionObjectInspector)this.oi).getNullSequence(), this.bytes, fieldStart, fieldLength)) {
            field.setNull();
        }
        else {
            field.init(this.bytes, fieldStart, fieldLength);
        }
        return field.getObject();
    }
    
    public Object getField() {
        if (this.fieldSet) {
            return this.field;
        }
        if (!this.parsed) {
            this.parse();
        }
        return this.uncheckedGetField();
    }
    
    public byte getTag() {
        if (this.fieldSet) {
            return this.tag;
        }
        if (!this.parsed) {
            this.parse();
        }
        return this.tag;
    }
}
