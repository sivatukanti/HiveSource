// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

final class FieldSet<FieldDescriptorType extends FieldDescriptorLite<FieldDescriptorType>>
{
    private final SmallSortedMap<FieldDescriptorType, Object> fields;
    private boolean isImmutable;
    private boolean hasLazyField;
    private static final FieldSet DEFAULT_INSTANCE;
    
    private FieldSet() {
        this.hasLazyField = false;
        this.fields = SmallSortedMap.newFieldMap(16);
    }
    
    private FieldSet(final boolean dummy) {
        this.hasLazyField = false;
        this.fields = SmallSortedMap.newFieldMap(0);
        this.makeImmutable();
    }
    
    public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
        return new FieldSet<T>();
    }
    
    public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
        return (FieldSet<T>)FieldSet.DEFAULT_INSTANCE;
    }
    
    public void makeImmutable() {
        if (this.isImmutable) {
            return;
        }
        this.fields.makeImmutable();
        this.isImmutable = true;
    }
    
    public boolean isImmutable() {
        return this.isImmutable;
    }
    
    public FieldSet<FieldDescriptorType> clone() {
        final FieldSet<FieldDescriptorType> clone = newFieldSet();
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            final Map.Entry<FieldDescriptorType, Object> entry = this.fields.getArrayEntryAt(i);
            final FieldDescriptorType descriptor = entry.getKey();
            clone.setField(descriptor, entry.getValue());
        }
        final Iterator i$ = this.fields.getOverflowEntries().iterator();
        while (i$.hasNext()) {
            final Map.Entry<FieldDescriptorType, Object> entry = i$.next();
            final FieldDescriptorType descriptor = entry.getKey();
            clone.setField(descriptor, entry.getValue());
        }
        clone.hasLazyField = this.hasLazyField;
        return clone;
    }
    
    public void clear() {
        this.fields.clear();
        this.hasLazyField = false;
    }
    
    public Map<FieldDescriptorType, Object> getAllFields() {
        if (this.hasLazyField) {
            final SmallSortedMap<FieldDescriptorType, Object> result = SmallSortedMap.newFieldMap(16);
            for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
                this.cloneFieldEntry(result, this.fields.getArrayEntryAt(i));
            }
            for (final Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
                this.cloneFieldEntry(result, entry);
            }
            if (this.fields.isImmutable()) {
                result.makeImmutable();
            }
            return result;
        }
        return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap((Map<? extends FieldDescriptorType, ?>)this.fields);
    }
    
    private void cloneFieldEntry(final Map<FieldDescriptorType, Object> map, final Map.Entry<FieldDescriptorType, Object> entry) {
        final FieldDescriptorType key = entry.getKey();
        final Object value = entry.getValue();
        if (value instanceof LazyField) {
            map.put(key, ((LazyField)value).getValue());
        }
        else {
            map.put(key, value);
        }
    }
    
    public Iterator<Map.Entry<FieldDescriptorType, Object>> iterator() {
        if (this.hasLazyField) {
            return new LazyField.LazyIterator<FieldDescriptorType>(this.fields.entrySet().iterator());
        }
        return this.fields.entrySet().iterator();
    }
    
    public boolean hasField(final FieldDescriptorType descriptor) {
        if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
        }
        return this.fields.get(descriptor) != null;
    }
    
    public Object getField(final FieldDescriptorType descriptor) {
        final Object o = this.fields.get(descriptor);
        if (o instanceof LazyField) {
            return ((LazyField)o).getValue();
        }
        return o;
    }
    
    public void setField(final FieldDescriptorType descriptor, Object value) {
        if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            final List newList = new ArrayList();
            newList.addAll((Collection)value);
            for (final Object element : newList) {
                verifyType(descriptor.getLiteType(), element);
            }
            value = newList;
        }
        else {
            verifyType(descriptor.getLiteType(), value);
        }
        if (value instanceof LazyField) {
            this.hasLazyField = true;
        }
        this.fields.put(descriptor, value);
    }
    
    public void clearField(final FieldDescriptorType descriptor) {
        this.fields.remove(descriptor);
        if (this.fields.isEmpty()) {
            this.hasLazyField = false;
        }
    }
    
    public int getRepeatedFieldCount(final FieldDescriptorType descriptor) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        final Object value = this.getField(descriptor);
        if (value == null) {
            return 0;
        }
        return ((List)value).size();
    }
    
    public Object getRepeatedField(final FieldDescriptorType descriptor, final int index) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        final Object value = this.getField(descriptor);
        if (value == null) {
            throw new IndexOutOfBoundsException();
        }
        return ((List)value).get(index);
    }
    
    public void setRepeatedField(final FieldDescriptorType descriptor, final int index, final Object value) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        final Object list = this.getField(descriptor);
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        verifyType(descriptor.getLiteType(), value);
        ((List)list).set(index, value);
    }
    
    public void addRepeatedField(final FieldDescriptorType descriptor, final Object value) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        verifyType(descriptor.getLiteType(), value);
        final Object existingValue = this.getField(descriptor);
        List<Object> list;
        if (existingValue == null) {
            list = new ArrayList<Object>();
            this.fields.put(descriptor, list);
        }
        else {
            list = (List<Object>)existingValue;
        }
        list.add(value);
    }
    
    private static void verifyType(final WireFormat.FieldType type, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        boolean isValid = false;
        switch (type.getJavaType()) {
            case INT: {
                isValid = (value instanceof Integer);
                break;
            }
            case LONG: {
                isValid = (value instanceof Long);
                break;
            }
            case FLOAT: {
                isValid = (value instanceof Float);
                break;
            }
            case DOUBLE: {
                isValid = (value instanceof Double);
                break;
            }
            case BOOLEAN: {
                isValid = (value instanceof Boolean);
                break;
            }
            case STRING: {
                isValid = (value instanceof String);
                break;
            }
            case BYTE_STRING: {
                isValid = (value instanceof ByteString);
                break;
            }
            case ENUM: {
                isValid = (value instanceof Internal.EnumLite);
                break;
            }
            case MESSAGE: {
                isValid = (value instanceof MessageLite || value instanceof LazyField);
                break;
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        }
    }
    
    public boolean isInitialized() {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            if (!this.isInitialized(this.fields.getArrayEntryAt(i))) {
                return false;
            }
        }
        for (final Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            if (!this.isInitialized(entry)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isInitialized(final Map.Entry<FieldDescriptorType, Object> entry) {
        final FieldDescriptorType descriptor = entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
                for (final MessageLite element : entry.getValue()) {
                    if (!element.isInitialized()) {
                        return false;
                    }
                }
            }
            else {
                final Object value = entry.getValue();
                if (value instanceof MessageLite) {
                    if (!((MessageLite)value).isInitialized()) {
                        return false;
                    }
                }
                else {
                    if (value instanceof LazyField) {
                        return true;
                    }
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
            }
        }
        return true;
    }
    
    static int getWireFormatForFieldType(final WireFormat.FieldType type, final boolean isPacked) {
        if (isPacked) {
            return 2;
        }
        return type.getWireType();
    }
    
    public void mergeFrom(final FieldSet<FieldDescriptorType> other) {
        for (int i = 0; i < other.fields.getNumArrayEntries(); ++i) {
            this.mergeFromField(other.fields.getArrayEntryAt(i));
        }
        for (final Map.Entry<FieldDescriptorType, Object> entry : other.fields.getOverflowEntries()) {
            this.mergeFromField(entry);
        }
    }
    
    private void mergeFromField(final Map.Entry<FieldDescriptorType, Object> entry) {
        final FieldDescriptorType descriptor = entry.getKey();
        Object otherValue = entry.getValue();
        if (otherValue instanceof LazyField) {
            otherValue = ((LazyField)otherValue).getValue();
        }
        if (descriptor.isRepeated()) {
            final Object value = this.getField(descriptor);
            if (value == null) {
                this.fields.put(descriptor, new ArrayList((Collection<?>)otherValue));
            }
            else {
                ((List)value).addAll((Collection)otherValue);
            }
        }
        else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            final Object value = this.getField(descriptor);
            if (value == null) {
                this.fields.put(descriptor, otherValue);
            }
            else {
                this.fields.put(descriptor, descriptor.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build());
            }
        }
        else {
            this.fields.put(descriptor, otherValue);
        }
    }
    
    public static Object readPrimitiveField(final CodedInputStream input, final WireFormat.FieldType type) throws IOException {
        switch (type) {
            case DOUBLE: {
                return input.readDouble();
            }
            case FLOAT: {
                return input.readFloat();
            }
            case INT64: {
                return input.readInt64();
            }
            case UINT64: {
                return input.readUInt64();
            }
            case INT32: {
                return input.readInt32();
            }
            case FIXED64: {
                return input.readFixed64();
            }
            case FIXED32: {
                return input.readFixed32();
            }
            case BOOL: {
                return input.readBool();
            }
            case STRING: {
                return input.readString();
            }
            case BYTES: {
                return input.readBytes();
            }
            case UINT32: {
                return input.readUInt32();
            }
            case SFIXED32: {
                return input.readSFixed32();
            }
            case SFIXED64: {
                return input.readSFixed64();
            }
            case SINT32: {
                return input.readSInt32();
            }
            case SINT64: {
                return input.readSInt64();
            }
            case GROUP: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
            }
            case MESSAGE: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
            }
            case ENUM: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
            }
            default: {
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
            }
        }
    }
    
    public void writeTo(final CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            final Map.Entry<FieldDescriptorType, Object> entry = this.fields.getArrayEntryAt(i);
            writeField(entry.getKey(), entry.getValue(), output);
        }
        final Iterator i$ = this.fields.getOverflowEntries().iterator();
        while (i$.hasNext()) {
            final Map.Entry<FieldDescriptorType, Object> entry = i$.next();
            writeField(entry.getKey(), entry.getValue(), output);
        }
    }
    
    public void writeMessageSetTo(final CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            this.writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
        }
        for (final Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            this.writeMessageSetTo(entry, output);
        }
    }
    
    private void writeMessageSetTo(final Map.Entry<FieldDescriptorType, Object> entry, final CodedOutputStream output) throws IOException {
        final FieldDescriptorType descriptor = entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            output.writeMessageSetExtension(entry.getKey().getNumber(), entry.getValue());
        }
        else {
            writeField(descriptor, entry.getValue(), output);
        }
    }
    
    private static void writeElement(final CodedOutputStream output, final WireFormat.FieldType type, final int number, final Object value) throws IOException {
        if (type == WireFormat.FieldType.GROUP) {
            output.writeGroup(number, (MessageLite)value);
        }
        else {
            output.writeTag(number, getWireFormatForFieldType(type, false));
            writeElementNoTag(output, type, value);
        }
    }
    
    private static void writeElementNoTag(final CodedOutputStream output, final WireFormat.FieldType type, final Object value) throws IOException {
        switch (type) {
            case DOUBLE: {
                output.writeDoubleNoTag((double)value);
                break;
            }
            case FLOAT: {
                output.writeFloatNoTag((float)value);
                break;
            }
            case INT64: {
                output.writeInt64NoTag((long)value);
                break;
            }
            case UINT64: {
                output.writeUInt64NoTag((long)value);
                break;
            }
            case INT32: {
                output.writeInt32NoTag((int)value);
                break;
            }
            case FIXED64: {
                output.writeFixed64NoTag((long)value);
                break;
            }
            case FIXED32: {
                output.writeFixed32NoTag((int)value);
                break;
            }
            case BOOL: {
                output.writeBoolNoTag((boolean)value);
                break;
            }
            case STRING: {
                output.writeStringNoTag((String)value);
                break;
            }
            case GROUP: {
                output.writeGroupNoTag((MessageLite)value);
                break;
            }
            case MESSAGE: {
                output.writeMessageNoTag((MessageLite)value);
                break;
            }
            case BYTES: {
                output.writeBytesNoTag((ByteString)value);
                break;
            }
            case UINT32: {
                output.writeUInt32NoTag((int)value);
                break;
            }
            case SFIXED32: {
                output.writeSFixed32NoTag((int)value);
                break;
            }
            case SFIXED64: {
                output.writeSFixed64NoTag((long)value);
                break;
            }
            case SINT32: {
                output.writeSInt32NoTag((int)value);
                break;
            }
            case SINT64: {
                output.writeSInt64NoTag((long)value);
                break;
            }
            case ENUM: {
                output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
                break;
            }
        }
    }
    
    public static void writeField(final FieldDescriptorLite<?> descriptor, final Object value, final CodedOutputStream output) throws IOException {
        final WireFormat.FieldType type = descriptor.getLiteType();
        final int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            final List<?> valueList = (List<?>)value;
            if (descriptor.isPacked()) {
                output.writeTag(number, 2);
                int dataSize = 0;
                for (final Object element : valueList) {
                    dataSize += computeElementSizeNoTag(type, element);
                }
                output.writeRawVarint32(dataSize);
                for (final Object element : valueList) {
                    writeElementNoTag(output, type, element);
                }
            }
            else {
                for (final Object element2 : valueList) {
                    writeElement(output, type, number, element2);
                }
            }
        }
        else if (value instanceof LazyField) {
            writeElement(output, type, number, ((LazyField)value).getValue());
        }
        else {
            writeElement(output, type, number, value);
        }
    }
    
    public int getSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            final Map.Entry<FieldDescriptorType, Object> entry = this.fields.getArrayEntryAt(i);
            size += computeFieldSize(entry.getKey(), entry.getValue());
        }
        final Iterator i$ = this.fields.getOverflowEntries().iterator();
        while (i$.hasNext()) {
            final Map.Entry<FieldDescriptorType, Object> entry = i$.next();
            size += computeFieldSize(entry.getKey(), entry.getValue());
        }
        return size;
    }
    
    public int getMessageSetSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            size += this.getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
        }
        for (final Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            size += this.getMessageSetSerializedSize(entry);
        }
        return size;
    }
    
    private int getMessageSetSerializedSize(final Map.Entry<FieldDescriptorType, Object> entry) {
        final FieldDescriptorType descriptor = entry.getKey();
        final Object value = entry.getValue();
        if (descriptor.getLiteJavaType() != WireFormat.JavaType.MESSAGE || descriptor.isRepeated() || descriptor.isPacked()) {
            return computeFieldSize(descriptor, value);
        }
        if (value instanceof LazyField) {
            return CodedOutputStream.computeLazyFieldMessageSetExtensionSize(entry.getKey().getNumber(), (LazyField)value);
        }
        return CodedOutputStream.computeMessageSetExtensionSize(entry.getKey().getNumber(), (MessageLite)value);
    }
    
    private static int computeElementSize(final WireFormat.FieldType type, final int number, final Object value) {
        int tagSize = CodedOutputStream.computeTagSize(number);
        if (type == WireFormat.FieldType.GROUP) {
            tagSize *= 2;
        }
        return tagSize + computeElementSizeNoTag(type, value);
    }
    
    private static int computeElementSizeNoTag(final WireFormat.FieldType type, final Object value) {
        switch (type) {
            case DOUBLE: {
                return CodedOutputStream.computeDoubleSizeNoTag((double)value);
            }
            case FLOAT: {
                return CodedOutputStream.computeFloatSizeNoTag((float)value);
            }
            case INT64: {
                return CodedOutputStream.computeInt64SizeNoTag((long)value);
            }
            case UINT64: {
                return CodedOutputStream.computeUInt64SizeNoTag((long)value);
            }
            case INT32: {
                return CodedOutputStream.computeInt32SizeNoTag((int)value);
            }
            case FIXED64: {
                return CodedOutputStream.computeFixed64SizeNoTag((long)value);
            }
            case FIXED32: {
                return CodedOutputStream.computeFixed32SizeNoTag((int)value);
            }
            case BOOL: {
                return CodedOutputStream.computeBoolSizeNoTag((boolean)value);
            }
            case STRING: {
                return CodedOutputStream.computeStringSizeNoTag((String)value);
            }
            case GROUP: {
                return CodedOutputStream.computeGroupSizeNoTag((MessageLite)value);
            }
            case BYTES: {
                return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
            }
            case UINT32: {
                return CodedOutputStream.computeUInt32SizeNoTag((int)value);
            }
            case SFIXED32: {
                return CodedOutputStream.computeSFixed32SizeNoTag((int)value);
            }
            case SFIXED64: {
                return CodedOutputStream.computeSFixed64SizeNoTag((long)value);
            }
            case SINT32: {
                return CodedOutputStream.computeSInt32SizeNoTag((int)value);
            }
            case SINT64: {
                return CodedOutputStream.computeSInt64SizeNoTag((long)value);
            }
            case MESSAGE: {
                if (value instanceof LazyField) {
                    return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField)value);
                }
                return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
            }
            case ENUM: {
                return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
            }
            default: {
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
            }
        }
    }
    
    public static int computeFieldSize(final FieldDescriptorLite<?> descriptor, final Object value) {
        final WireFormat.FieldType type = descriptor.getLiteType();
        final int number = descriptor.getNumber();
        if (!descriptor.isRepeated()) {
            return computeElementSize(type, number, value);
        }
        if (descriptor.isPacked()) {
            int dataSize = 0;
            for (final Object element : (List)value) {
                dataSize += computeElementSizeNoTag(type, element);
            }
            return dataSize + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeRawVarint32Size(dataSize);
        }
        int size = 0;
        for (final Object element : (List)value) {
            size += computeElementSize(type, number, element);
        }
        return size;
    }
    
    static {
        DEFAULT_INSTANCE = new FieldSet(true);
    }
    
    public interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T>
    {
        int getNumber();
        
        WireFormat.FieldType getLiteType();
        
        WireFormat.JavaType getLiteJavaType();
        
        boolean isRepeated();
        
        boolean isPacked();
        
        Internal.EnumLiteMap<?> getEnumType();
        
        MessageLite.Builder internalMergeFrom(final MessageLite.Builder p0, final MessageLite p1);
    }
}
