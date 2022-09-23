// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.VoidObjectInspector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import java.util.Map;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableBinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableTimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableDateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableHiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableDoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableFloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableLongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableIntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.SettableBooleanObjectInspector;

public final class ObjectInspectorConverters
{
    private static Converter getConverter(final PrimitiveObjectInspector inputOI, final PrimitiveObjectInspector outputOI) {
        switch (outputOI.getPrimitiveCategory()) {
            case BOOLEAN: {
                return new PrimitiveObjectInspectorConverter.BooleanConverter(inputOI, (SettableBooleanObjectInspector)outputOI);
            }
            case BYTE: {
                return new PrimitiveObjectInspectorConverter.ByteConverter(inputOI, (SettableByteObjectInspector)outputOI);
            }
            case SHORT: {
                return new PrimitiveObjectInspectorConverter.ShortConverter(inputOI, (SettableShortObjectInspector)outputOI);
            }
            case INT: {
                return new PrimitiveObjectInspectorConverter.IntConverter(inputOI, (SettableIntObjectInspector)outputOI);
            }
            case LONG: {
                return new PrimitiveObjectInspectorConverter.LongConverter(inputOI, (SettableLongObjectInspector)outputOI);
            }
            case FLOAT: {
                return new PrimitiveObjectInspectorConverter.FloatConverter(inputOI, (SettableFloatObjectInspector)outputOI);
            }
            case DOUBLE: {
                return new PrimitiveObjectInspectorConverter.DoubleConverter(inputOI, (SettableDoubleObjectInspector)outputOI);
            }
            case STRING: {
                if (outputOI instanceof WritableStringObjectInspector) {
                    return new PrimitiveObjectInspectorConverter.TextConverter(inputOI);
                }
                if (outputOI instanceof JavaStringObjectInspector) {
                    return new PrimitiveObjectInspectorConverter.StringConverter(inputOI);
                }
                return new PrimitiveObjectInspectorConverter.HiveCharConverter(inputOI, (SettableHiveCharObjectInspector)outputOI);
            }
            case CHAR: {
                return new PrimitiveObjectInspectorConverter.HiveCharConverter(inputOI, (SettableHiveCharObjectInspector)outputOI);
            }
            case VARCHAR: {
                return new PrimitiveObjectInspectorConverter.HiveVarcharConverter(inputOI, (SettableHiveVarcharObjectInspector)outputOI);
            }
            case DATE: {
                return new PrimitiveObjectInspectorConverter.DateConverter(inputOI, (SettableDateObjectInspector)outputOI);
            }
            case TIMESTAMP: {
                return new PrimitiveObjectInspectorConverter.TimestampConverter(inputOI, (SettableTimestampObjectInspector)outputOI);
            }
            case INTERVAL_YEAR_MONTH: {
                return new PrimitiveObjectInspectorConverter.HiveIntervalYearMonthConverter(inputOI, (SettableHiveIntervalYearMonthObjectInspector)outputOI);
            }
            case INTERVAL_DAY_TIME: {
                return new PrimitiveObjectInspectorConverter.HiveIntervalDayTimeConverter(inputOI, (SettableHiveIntervalDayTimeObjectInspector)outputOI);
            }
            case BINARY: {
                return new PrimitiveObjectInspectorConverter.BinaryConverter(inputOI, (SettableBinaryObjectInspector)outputOI);
            }
            case DECIMAL: {
                return new PrimitiveObjectInspectorConverter.HiveDecimalConverter(inputOI, (SettableHiveDecimalObjectInspector)outputOI);
            }
            default: {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + " not supported yet.");
            }
        }
    }
    
    public static Converter getConverter(final ObjectInspector inputOI, final ObjectInspector outputOI) {
        if (inputOI.equals(outputOI)) {
            return new IdentityConverter();
        }
        switch (outputOI.getCategory()) {
            case PRIMITIVE: {
                return getConverter((PrimitiveObjectInspector)inputOI, (PrimitiveObjectInspector)outputOI);
            }
            case STRUCT: {
                return new StructConverter(inputOI, (SettableStructObjectInspector)outputOI);
            }
            case LIST: {
                return new ListConverter(inputOI, (SettableListObjectInspector)outputOI);
            }
            case MAP: {
                return new MapConverter(inputOI, (SettableMapObjectInspector)outputOI);
            }
            case UNION: {
                return new UnionConverter(inputOI, (SettableUnionObjectInspector)outputOI);
            }
            default: {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + " not supported yet.");
            }
        }
    }
    
    public static ObjectInspector getConvertedOI(final ObjectInspector inputOI, final ObjectInspector outputOI, final Map<ObjectInspector, Boolean> oiSettableProperties) {
        return getConvertedOI(inputOI, outputOI, oiSettableProperties, true);
    }
    
    public static ObjectInspector getConvertedOI(final ObjectInspector inputOI, final ObjectInspector outputOI) {
        return getConvertedOI(inputOI, outputOI, null, true);
    }
    
    public static ObjectInspector getConvertedOI(final ObjectInspector inputOI, final ObjectInspector outputOI, final Map<ObjectInspector, Boolean> oiSettableProperties, final boolean equalsCheck) {
        if ((equalsCheck && inputOI.equals(outputOI)) || ObjectInspectorUtils.hasAllFieldsSettable(outputOI, oiSettableProperties)) {
            return outputOI;
        }
        switch (outputOI.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector primOutputOI = (PrimitiveObjectInspector)outputOI;
                return PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(primOutputOI.getTypeInfo());
            }
            case STRUCT: {
                final StructObjectInspector structOutputOI = (StructObjectInspector)outputOI;
                final List<? extends StructField> listFields = structOutputOI.getAllStructFieldRefs();
                final List<String> structFieldNames = new ArrayList<String>(listFields.size());
                final List<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>(listFields.size());
                for (final StructField listField : listFields) {
                    structFieldNames.add(listField.getFieldName());
                    structFieldObjectInspectors.add(getConvertedOI(listField.getFieldObjectInspector(), listField.getFieldObjectInspector(), oiSettableProperties, false));
                }
                return ObjectInspectorFactory.getStandardStructObjectInspector(structFieldNames, structFieldObjectInspectors);
            }
            case LIST: {
                final ListObjectInspector listOutputOI = (ListObjectInspector)outputOI;
                return ObjectInspectorFactory.getStandardListObjectInspector(getConvertedOI(listOutputOI.getListElementObjectInspector(), listOutputOI.getListElementObjectInspector(), oiSettableProperties, false));
            }
            case MAP: {
                final MapObjectInspector mapOutputOI = (MapObjectInspector)outputOI;
                return ObjectInspectorFactory.getStandardMapObjectInspector(getConvertedOI(mapOutputOI.getMapKeyObjectInspector(), mapOutputOI.getMapKeyObjectInspector(), oiSettableProperties, false), getConvertedOI(mapOutputOI.getMapValueObjectInspector(), mapOutputOI.getMapValueObjectInspector(), oiSettableProperties, false));
            }
            case UNION: {
                final UnionObjectInspector unionOutputOI = (UnionObjectInspector)outputOI;
                final List<ObjectInspector> unionListFields = unionOutputOI.getObjectInspectors();
                final List<ObjectInspector> unionFieldObjectInspectors = new ArrayList<ObjectInspector>(unionListFields.size());
                for (final ObjectInspector listField2 : unionListFields) {
                    unionFieldObjectInspectors.add(getConvertedOI(listField2, listField2, oiSettableProperties, false));
                }
                return ObjectInspectorFactory.getStandardUnionObjectInspector(unionFieldObjectInspectors);
            }
            default: {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + " not supported yet.");
            }
        }
    }
    
    private ObjectInspectorConverters() {
    }
    
    public static class IdentityConverter implements Converter
    {
        @Override
        public Object convert(final Object input) {
            return input;
        }
    }
    
    public static class ListConverter implements Converter
    {
        ListObjectInspector inputOI;
        SettableListObjectInspector outputOI;
        ObjectInspector inputElementOI;
        ObjectInspector outputElementOI;
        ArrayList<Converter> elementConverters;
        Object output;
        
        public ListConverter(final ObjectInspector inputOI, final SettableListObjectInspector outputOI) {
            if (inputOI instanceof ListObjectInspector) {
                this.inputOI = (ListObjectInspector)inputOI;
                this.outputOI = outputOI;
                this.inputElementOI = this.inputOI.getListElementObjectInspector();
                this.outputElementOI = outputOI.getListElementObjectInspector();
                this.output = outputOI.create(0);
                this.elementConverters = new ArrayList<Converter>();
            }
            else if (!(inputOI instanceof VoidObjectInspector)) {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + "not supported yet.");
            }
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            final int size = this.inputOI.getListLength(input);
            while (this.elementConverters.size() < size) {
                this.elementConverters.add(ObjectInspectorConverters.getConverter(this.inputElementOI, this.outputElementOI));
            }
            this.outputOI.resize(this.output, size);
            for (int index = 0; index < size; ++index) {
                final Object inputElement = this.inputOI.getListElement(input, index);
                final Object outputElement = this.elementConverters.get(index).convert(inputElement);
                this.outputOI.set(this.output, index, outputElement);
            }
            return this.output;
        }
    }
    
    public static class StructConverter implements Converter
    {
        StructObjectInspector inputOI;
        SettableStructObjectInspector outputOI;
        List<? extends StructField> inputFields;
        List<? extends StructField> outputFields;
        ArrayList<Converter> fieldConverters;
        Object output;
        
        public StructConverter(final ObjectInspector inputOI, final SettableStructObjectInspector outputOI) {
            if (inputOI instanceof StructObjectInspector) {
                this.inputOI = (StructObjectInspector)inputOI;
                this.outputOI = outputOI;
                this.inputFields = this.inputOI.getAllStructFieldRefs();
                this.outputFields = outputOI.getAllStructFieldRefs();
                final int minFields = Math.min(this.inputFields.size(), this.outputFields.size());
                this.fieldConverters = new ArrayList<Converter>(minFields);
                for (int f = 0; f < minFields; ++f) {
                    this.fieldConverters.add(ObjectInspectorConverters.getConverter(((StructField)this.inputFields.get(f)).getFieldObjectInspector(), ((StructField)this.outputFields.get(f)).getFieldObjectInspector()));
                }
                this.output = outputOI.create();
            }
            else if (!(inputOI instanceof VoidObjectInspector)) {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + "not supported yet.");
            }
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            final int minFields = Math.min(this.inputFields.size(), this.outputFields.size());
            for (int f = 0; f < minFields; ++f) {
                final Object inputFieldValue = this.inputOI.getStructFieldData(input, (StructField)this.inputFields.get(f));
                final Object outputFieldValue = this.fieldConverters.get(f).convert(inputFieldValue);
                this.outputOI.setStructFieldData(this.output, (StructField)this.outputFields.get(f), outputFieldValue);
            }
            for (int f = minFields; f < this.outputFields.size(); ++f) {
                this.outputOI.setStructFieldData(this.output, (StructField)this.outputFields.get(f), null);
            }
            return this.output;
        }
    }
    
    public static class UnionConverter implements Converter
    {
        UnionObjectInspector inputOI;
        SettableUnionObjectInspector outputOI;
        List<? extends ObjectInspector> inputFields;
        List<? extends ObjectInspector> outputFields;
        ArrayList<Converter> fieldConverters;
        Object output;
        
        public UnionConverter(final ObjectInspector inputOI, final SettableUnionObjectInspector outputOI) {
            if (inputOI instanceof UnionObjectInspector) {
                this.inputOI = (UnionObjectInspector)inputOI;
                this.outputOI = outputOI;
                this.inputFields = this.inputOI.getObjectInspectors();
                this.outputFields = outputOI.getObjectInspectors();
                final int minFields = Math.min(this.inputFields.size(), this.outputFields.size());
                this.fieldConverters = new ArrayList<Converter>(minFields);
                for (int f = 0; f < minFields; ++f) {
                    this.fieldConverters.add(ObjectInspectorConverters.getConverter((ObjectInspector)this.inputFields.get(f), (ObjectInspector)this.outputFields.get(f)));
                }
                this.output = outputOI.create();
            }
            else if (!(inputOI instanceof VoidObjectInspector)) {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + "not supported yet.");
            }
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            final int minFields = Math.min(this.inputFields.size(), this.outputFields.size());
            for (int f = 0; f < minFields; ++f) {
                final Object outputFieldValue = this.fieldConverters.get(f).convert(this.inputOI);
                this.outputOI.addField(this.output, (ObjectInspector)outputFieldValue);
            }
            for (int f = minFields; f < this.outputFields.size(); ++f) {
                this.outputOI.addField(this.output, null);
            }
            return this.output;
        }
    }
    
    public static class MapConverter implements Converter
    {
        MapObjectInspector inputOI;
        SettableMapObjectInspector outputOI;
        ObjectInspector inputKeyOI;
        ObjectInspector outputKeyOI;
        ObjectInspector inputValueOI;
        ObjectInspector outputValueOI;
        ArrayList<Converter> keyConverters;
        ArrayList<Converter> valueConverters;
        Object output;
        
        public MapConverter(final ObjectInspector inputOI, final SettableMapObjectInspector outputOI) {
            if (inputOI instanceof MapObjectInspector) {
                this.inputOI = (MapObjectInspector)inputOI;
                this.outputOI = outputOI;
                this.inputKeyOI = this.inputOI.getMapKeyObjectInspector();
                this.outputKeyOI = outputOI.getMapKeyObjectInspector();
                this.inputValueOI = this.inputOI.getMapValueObjectInspector();
                this.outputValueOI = outputOI.getMapValueObjectInspector();
                this.keyConverters = new ArrayList<Converter>();
                this.valueConverters = new ArrayList<Converter>();
                this.output = outputOI.create();
            }
            else if (!(inputOI instanceof VoidObjectInspector)) {
                throw new RuntimeException("Hive internal error: conversion of " + inputOI.getTypeName() + " to " + outputOI.getTypeName() + "not supported yet.");
            }
        }
        
        @Override
        public Object convert(final Object input) {
            if (input == null) {
                return null;
            }
            final Map<?, ?> map = this.inputOI.getMap(input);
            final int size = map.size();
            while (this.keyConverters.size() < size) {
                this.keyConverters.add(ObjectInspectorConverters.getConverter(this.inputKeyOI, this.outputKeyOI));
                this.valueConverters.add(ObjectInspectorConverters.getConverter(this.inputValueOI, this.outputValueOI));
            }
            this.outputOI.clear(this.output);
            int entryID = 0;
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                final Object inputKey = entry.getKey();
                final Object inputValue = entry.getValue();
                final Object outputKey = this.keyConverters.get(entryID).convert(inputKey);
                final Object outputValue = this.valueConverters.get(entryID).convert(inputValue);
                ++entryID;
                this.outputOI.put(this.output, outputKey, outputValue);
            }
            return this.output;
        }
    }
    
    public interface Converter
    {
        Object convert(final Object p0);
    }
}
