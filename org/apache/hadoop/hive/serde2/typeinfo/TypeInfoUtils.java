// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.typeinfo;

import java.util.LinkedList;
import java.util.Iterator;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Field;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.concurrent.ConcurrentHashMap;

public final class TypeInfoUtils
{
    static ConcurrentHashMap<TypeInfo, ObjectInspector> cachedStandardObjectInspector;
    static ConcurrentHashMap<TypeInfo, ObjectInspector> cachedStandardJavaObjectInspector;
    
    private TypeInfoUtils() {
    }
    
    private static TypeInfo getExtendedTypeInfoFromJavaType(Type t, final Method m) {
        if (t == Object.class) {
            return TypeInfoFactory.unknownTypeInfo;
        }
        if (t instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)t;
            if (List.class == pt.getRawType() || ArrayList.class == pt.getRawType()) {
                return TypeInfoFactory.getListTypeInfo(getExtendedTypeInfoFromJavaType(pt.getActualTypeArguments()[0], m));
            }
            if (Map.class == pt.getRawType() || HashMap.class == pt.getRawType()) {
                return TypeInfoFactory.getMapTypeInfo(getExtendedTypeInfoFromJavaType(pt.getActualTypeArguments()[0], m), getExtendedTypeInfoFromJavaType(pt.getActualTypeArguments()[1], m));
            }
            t = pt.getRawType();
        }
        if (!(t instanceof Class)) {
            throw new RuntimeException("Hive does not understand type " + t + " from " + m);
        }
        final Class<?> c = (Class<?>)t;
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaType(c)) {
            return getTypeInfoFromObjectInspector(PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaType(c).primitiveCategory));
        }
        if (PrimitiveObjectInspectorUtils.isPrimitiveJavaClass(c)) {
            return getTypeInfoFromObjectInspector(PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaClass(c).primitiveCategory));
        }
        if (PrimitiveObjectInspectorUtils.isPrimitiveWritableClass(c)) {
            return getTypeInfoFromObjectInspector(PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveWritableClass(c).primitiveCategory));
        }
        final Field[] fields = ObjectInspectorUtils.getDeclaredNonStaticFields(c);
        final ArrayList<String> fieldNames = new ArrayList<String>(fields.length);
        final ArrayList<TypeInfo> fieldTypeInfos = new ArrayList<TypeInfo>(fields.length);
        for (final Field field : fields) {
            fieldNames.add(field.getName());
            fieldTypeInfos.add(getExtendedTypeInfoFromJavaType(field.getGenericType(), m));
        }
        return TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypeInfos);
    }
    
    public static Type getArrayElementType(final Type t) {
        if (t instanceof Class && ((Class)t).isArray()) {
            final Class<?> arrayClass = (Class<?>)t;
            return arrayClass.getComponentType();
        }
        if (t instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType)t;
            return arrayType.getGenericComponentType();
        }
        return null;
    }
    
    public static List<TypeInfo> getParameterTypeInfos(final Method m, final int size) {
        final Type[] methodParameterTypes = m.getGenericParameterTypes();
        final Type lastParaElementType = getArrayElementType((methodParameterTypes.length == 0) ? null : methodParameterTypes[methodParameterTypes.length - 1]);
        final boolean isVariableLengthArgument = lastParaElementType != null;
        List<TypeInfo> typeInfos = null;
        if (!isVariableLengthArgument) {
            if (size != methodParameterTypes.length) {
                return null;
            }
            typeInfos = new ArrayList<TypeInfo>(methodParameterTypes.length);
            for (final Type methodParameterType : methodParameterTypes) {
                typeInfos.add(getExtendedTypeInfoFromJavaType(methodParameterType, m));
            }
        }
        else {
            if (size < methodParameterTypes.length - 1) {
                return null;
            }
            typeInfos = new ArrayList<TypeInfo>(size);
            for (int i = 0; i < methodParameterTypes.length - 1; ++i) {
                typeInfos.add(getExtendedTypeInfoFromJavaType(methodParameterTypes[i], m));
            }
            for (int i = methodParameterTypes.length - 1; i < size; ++i) {
                typeInfos.add(getExtendedTypeInfoFromJavaType(lastParaElementType, m));
            }
        }
        return typeInfos;
    }
    
    public static boolean hasParameters(final String typeName) {
        final int idx = typeName.indexOf(40);
        return idx != -1;
    }
    
    public static String getBaseName(final String typeName) {
        final int idx = typeName.indexOf(40);
        if (idx == -1) {
            return typeName;
        }
        return typeName.substring(0, idx);
    }
    
    public static boolean doPrimitiveCategoriesMatch(final TypeInfo ti1, final TypeInfo ti2) {
        return ti1.getCategory() == ObjectInspector.Category.PRIMITIVE && ti2.getCategory() == ObjectInspector.Category.PRIMITIVE && ((PrimitiveTypeInfo)ti1).getPrimitiveCategory() == ((PrimitiveTypeInfo)ti2).getPrimitiveCategory();
    }
    
    public static PrimitiveParts parsePrimitiveParts(final String typeInfoString) {
        final TypeInfoParser parser = new TypeInfoParser(typeInfoString);
        return parser.parsePrimitiveParts();
    }
    
    public static ObjectInspector getStandardWritableObjectInspectorFromTypeInfo(final TypeInfo typeInfo) {
        ObjectInspector result = TypeInfoUtils.cachedStandardObjectInspector.get(typeInfo);
        if (result == null) {
            switch (typeInfo.getCategory()) {
                case PRIMITIVE: {
                    result = PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector((PrimitiveTypeInfo)typeInfo);
                    break;
                }
                case LIST: {
                    final ObjectInspector elementObjectInspector = getStandardWritableObjectInspectorFromTypeInfo(((ListTypeInfo)typeInfo).getListElementTypeInfo());
                    result = ObjectInspectorFactory.getStandardListObjectInspector(elementObjectInspector);
                    break;
                }
                case MAP: {
                    final MapTypeInfo mapTypeInfo = (MapTypeInfo)typeInfo;
                    final ObjectInspector keyObjectInspector = getStandardWritableObjectInspectorFromTypeInfo(mapTypeInfo.getMapKeyTypeInfo());
                    final ObjectInspector valueObjectInspector = getStandardWritableObjectInspectorFromTypeInfo(mapTypeInfo.getMapValueTypeInfo());
                    result = ObjectInspectorFactory.getStandardMapObjectInspector(keyObjectInspector, valueObjectInspector);
                    break;
                }
                case STRUCT: {
                    final StructTypeInfo structTypeInfo = (StructTypeInfo)typeInfo;
                    final List<String> fieldNames = structTypeInfo.getAllStructFieldNames();
                    final List<TypeInfo> fieldTypeInfos = structTypeInfo.getAllStructFieldTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fieldTypeInfos.size());
                    for (int i = 0; i < fieldTypeInfos.size(); ++i) {
                        fieldObjectInspectors.add(getStandardWritableObjectInspectorFromTypeInfo(fieldTypeInfos.get(i)));
                    }
                    result = ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldObjectInspectors);
                    break;
                }
                case UNION: {
                    final UnionTypeInfo unionTypeInfo = (UnionTypeInfo)typeInfo;
                    final List<TypeInfo> objectTypeInfos = unionTypeInfo.getAllUnionObjectTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors2 = new ArrayList<ObjectInspector>(objectTypeInfos.size());
                    for (int j = 0; j < objectTypeInfos.size(); ++j) {
                        fieldObjectInspectors2.add(getStandardWritableObjectInspectorFromTypeInfo(objectTypeInfos.get(j)));
                    }
                    result = ObjectInspectorFactory.getStandardUnionObjectInspector(fieldObjectInspectors2);
                    break;
                }
                default: {
                    result = null;
                    break;
                }
            }
            final ObjectInspector prev = TypeInfoUtils.cachedStandardObjectInspector.putIfAbsent(typeInfo, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static ObjectInspector getStandardJavaObjectInspectorFromTypeInfo(final TypeInfo typeInfo) {
        ObjectInspector result = TypeInfoUtils.cachedStandardJavaObjectInspector.get(typeInfo);
        if (result == null) {
            switch (typeInfo.getCategory()) {
                case PRIMITIVE: {
                    result = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector((PrimitiveTypeInfo)typeInfo);
                    break;
                }
                case LIST: {
                    final ObjectInspector elementObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(((ListTypeInfo)typeInfo).getListElementTypeInfo());
                    result = ObjectInspectorFactory.getStandardListObjectInspector(elementObjectInspector);
                    break;
                }
                case MAP: {
                    final MapTypeInfo mapTypeInfo = (MapTypeInfo)typeInfo;
                    final ObjectInspector keyObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(mapTypeInfo.getMapKeyTypeInfo());
                    final ObjectInspector valueObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(mapTypeInfo.getMapValueTypeInfo());
                    result = ObjectInspectorFactory.getStandardMapObjectInspector(keyObjectInspector, valueObjectInspector);
                    break;
                }
                case STRUCT: {
                    final StructTypeInfo strucTypeInfo = (StructTypeInfo)typeInfo;
                    final List<String> fieldNames = strucTypeInfo.getAllStructFieldNames();
                    final List<TypeInfo> fieldTypeInfos = strucTypeInfo.getAllStructFieldTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fieldTypeInfos.size());
                    for (int i = 0; i < fieldTypeInfos.size(); ++i) {
                        fieldObjectInspectors.add(getStandardJavaObjectInspectorFromTypeInfo(fieldTypeInfos.get(i)));
                    }
                    result = ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldObjectInspectors);
                    break;
                }
                case UNION: {
                    final UnionTypeInfo unionTypeInfo = (UnionTypeInfo)typeInfo;
                    final List<TypeInfo> objectTypeInfos = unionTypeInfo.getAllUnionObjectTypeInfos();
                    final List<ObjectInspector> fieldObjectInspectors2 = new ArrayList<ObjectInspector>(objectTypeInfos.size());
                    for (int j = 0; j < objectTypeInfos.size(); ++j) {
                        fieldObjectInspectors2.add(getStandardJavaObjectInspectorFromTypeInfo(objectTypeInfos.get(j)));
                    }
                    result = ObjectInspectorFactory.getStandardUnionObjectInspector(fieldObjectInspectors2);
                    break;
                }
                default: {
                    result = null;
                    break;
                }
            }
            final ObjectInspector prev = TypeInfoUtils.cachedStandardJavaObjectInspector.putIfAbsent(typeInfo, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static TypeInfo getTypeInfoFromObjectInspector(final ObjectInspector oi) {
        if (oi == null) {
            return null;
        }
        TypeInfo result = null;
        switch (oi.getCategory()) {
            case PRIMITIVE: {
                final PrimitiveObjectInspector poi = (PrimitiveObjectInspector)oi;
                result = poi.getTypeInfo();
                break;
            }
            case LIST: {
                final ListObjectInspector loi = (ListObjectInspector)oi;
                result = TypeInfoFactory.getListTypeInfo(getTypeInfoFromObjectInspector(loi.getListElementObjectInspector()));
                break;
            }
            case MAP: {
                final MapObjectInspector moi = (MapObjectInspector)oi;
                result = TypeInfoFactory.getMapTypeInfo(getTypeInfoFromObjectInspector(moi.getMapKeyObjectInspector()), getTypeInfoFromObjectInspector(moi.getMapValueObjectInspector()));
                break;
            }
            case STRUCT: {
                final StructObjectInspector soi = (StructObjectInspector)oi;
                final List<? extends StructField> fields = soi.getAllStructFieldRefs();
                final List<String> fieldNames = new ArrayList<String>(fields.size());
                final List<TypeInfo> fieldTypeInfos = new ArrayList<TypeInfo>(fields.size());
                for (final StructField f : fields) {
                    fieldNames.add(f.getFieldName());
                    fieldTypeInfos.add(getTypeInfoFromObjectInspector(f.getFieldObjectInspector()));
                }
                result = TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypeInfos);
                break;
            }
            case UNION: {
                final UnionObjectInspector uoi = (UnionObjectInspector)oi;
                final List<TypeInfo> objectTypeInfos = new ArrayList<TypeInfo>();
                for (final ObjectInspector eoi : uoi.getObjectInspectors()) {
                    objectTypeInfos.add(getTypeInfoFromObjectInspector(eoi));
                }
                result = TypeInfoFactory.getUnionTypeInfo(objectTypeInfos);
                break;
            }
            default: {
                throw new RuntimeException("Unknown ObjectInspector category!");
            }
        }
        return result;
    }
    
    public static ArrayList<TypeInfo> getTypeInfosFromTypeString(final String typeString) {
        final TypeInfoParser parser = new TypeInfoParser(typeString);
        return parser.parseTypeInfos();
    }
    
    public static TypeInfo getTypeInfoFromTypeString(final String typeString) {
        final TypeInfoParser parser = new TypeInfoParser(typeString);
        return parser.parseTypeInfos().get(0);
    }
    
    public static boolean isConversionRequiredForComparison(final TypeInfo typeA, final TypeInfo typeB) {
        return !typeA.equals(typeB) && !doPrimitiveCategoriesMatch(typeA, typeB);
    }
    
    public static int getCharacterLengthForType(final PrimitiveTypeInfo typeInfo) {
        switch (typeInfo.getPrimitiveCategory()) {
            case STRING: {
                return 65535;
            }
            case CHAR:
            case VARCHAR: {
                final BaseCharTypeInfo baseCharTypeInfo = (BaseCharTypeInfo)typeInfo;
                return baseCharTypeInfo.getLength();
            }
            default: {
                return 0;
            }
        }
    }
    
    static {
        TypeInfoUtils.cachedStandardObjectInspector = new ConcurrentHashMap<TypeInfo, ObjectInspector>();
        TypeInfoUtils.cachedStandardJavaObjectInspector = new ConcurrentHashMap<TypeInfo, ObjectInspector>();
    }
    
    private static class TypeInfoParser
    {
        private final String typeInfoString;
        private final ArrayList<Token> typeInfoTokens;
        private ArrayList<TypeInfo> typeInfos;
        private int iToken;
        
        private static boolean isTypeChar(final char c) {
            return Character.isLetterOrDigit(c) || c == '_' || c == '.';
        }
        
        private static ArrayList<Token> tokenize(final String typeInfoString) {
            final ArrayList<Token> tokens = new ArrayList<Token>(0);
            int begin = 0;
            for (int end = 1; end <= typeInfoString.length(); ++end) {
                if (end == typeInfoString.length() || !isTypeChar(typeInfoString.charAt(end - 1)) || !isTypeChar(typeInfoString.charAt(end))) {
                    final Token t = new Token();
                    t.position = begin;
                    t.text = typeInfoString.substring(begin, end);
                    t.isType = isTypeChar(typeInfoString.charAt(begin));
                    tokens.add(t);
                    begin = end;
                }
            }
            return tokens;
        }
        
        public TypeInfoParser(final String typeInfoString) {
            this.typeInfoString = typeInfoString;
            this.typeInfoTokens = tokenize(typeInfoString);
        }
        
        public ArrayList<TypeInfo> parseTypeInfos() {
            this.typeInfos = new ArrayList<TypeInfo>();
            this.iToken = 0;
            while (this.iToken < this.typeInfoTokens.size()) {
                this.typeInfos.add(this.parseType());
                if (this.iToken < this.typeInfoTokens.size()) {
                    final Token separator = this.typeInfoTokens.get(this.iToken);
                    if (!",".equals(separator.text) && !";".equals(separator.text) && !":".equals(separator.text)) {
                        throw new IllegalArgumentException("Error: ',', ':', or ';' expected at position " + separator.position + " from '" + this.typeInfoString + "' " + this.typeInfoTokens);
                    }
                    ++this.iToken;
                }
            }
            return this.typeInfos;
        }
        
        private Token peek() {
            if (this.iToken < this.typeInfoTokens.size()) {
                return this.typeInfoTokens.get(this.iToken);
            }
            return null;
        }
        
        private Token expect(final String item) {
            return this.expect(item, null);
        }
        
        private Token expect(final String item, final String alternative) {
            if (this.iToken >= this.typeInfoTokens.size()) {
                throw new IllegalArgumentException("Error: " + item + " expected at the end of '" + this.typeInfoString + "'");
            }
            final Token t = this.typeInfoTokens.get(this.iToken);
            if (item.equals("type")) {
                if (!"array".equals(t.text) && !"map".equals(t.text) && !"struct".equals(t.text) && !"uniontype".equals(t.text) && null == PrimitiveObjectInspectorUtils.getTypeEntryFromTypeName(t.text) && !t.text.equals(alternative)) {
                    throw new IllegalArgumentException("Error: " + item + " expected at the position " + t.position + " of '" + this.typeInfoString + "' but '" + t.text + "' is found.");
                }
            }
            else if (item.equals("name")) {
                if (!t.isType && !t.text.equals(alternative)) {
                    throw new IllegalArgumentException("Error: " + item + " expected at the position " + t.position + " of '" + this.typeInfoString + "' but '" + t.text + "' is found.");
                }
            }
            else if (!item.equals(t.text) && !t.text.equals(alternative)) {
                throw new IllegalArgumentException("Error: " + item + " expected at the position " + t.position + " of '" + this.typeInfoString + "' but '" + t.text + "' is found.");
            }
            ++this.iToken;
            return t;
        }
        
        private String[] parseParams() {
            final List<String> params = new LinkedList<String>();
            Token t = this.peek();
            if (t != null && t.text.equals("(")) {
                this.expect("(");
                for (t = this.peek(); t == null || !t.text.equals(")"); t = this.expect(",", ")")) {
                    params.add(this.expect("name").text);
                }
                if (params.size() == 0) {
                    throw new IllegalArgumentException("type parameters expected for type string " + this.typeInfoString);
                }
            }
            return params.toArray(new String[params.size()]);
        }
        
        private TypeInfo parseType() {
            final Token t = this.expect("type");
            final PrimitiveObjectInspectorUtils.PrimitiveTypeEntry typeEntry = PrimitiveObjectInspectorUtils.getTypeEntryFromTypeName(t.text);
            if (typeEntry != null && typeEntry.primitiveCategory != PrimitiveObjectInspector.PrimitiveCategory.UNKNOWN) {
                final String[] params = this.parseParams();
                Label_0221: {
                    switch (typeEntry.primitiveCategory) {
                        case CHAR:
                        case VARCHAR: {
                            if (params == null || params.length == 0) {
                                throw new IllegalArgumentException(typeEntry.typeName + " type is specified without length: " + this.typeInfoString);
                            }
                            int length = 1;
                            if (params.length == 1) {
                                length = Integer.valueOf(params[0]);
                                if (typeEntry.primitiveCategory == PrimitiveObjectInspector.PrimitiveCategory.VARCHAR) {
                                    BaseCharUtils.validateVarcharParameter(length);
                                    return TypeInfoFactory.getVarcharTypeInfo(length);
                                }
                                BaseCharUtils.validateCharParameter(length);
                                return TypeInfoFactory.getCharTypeInfo(length);
                            }
                            else {
                                if (params.length > 1) {
                                    throw new IllegalArgumentException("Type " + typeEntry.typeName + " only takes one parameter, but " + params.length + " is seen");
                                }
                                break Label_0221;
                            }
                            break;
                        }
                        case DECIMAL: {
                            int precision = 10;
                            int scale = 0;
                            if (params != null) {
                                if (params.length != 0) {
                                    if (params.length == 2) {
                                        precision = Integer.valueOf(params[0]);
                                        scale = Integer.valueOf(params[1]);
                                        HiveDecimalUtils.validateParameter(precision, scale);
                                    }
                                    else if (params.length > 2) {
                                        throw new IllegalArgumentException("Type decimal only takes two parameter, but " + params.length + " is seen");
                                    }
                                }
                            }
                            return TypeInfoFactory.getDecimalTypeInfo(precision, scale);
                        }
                        default: {
                            return TypeInfoFactory.getPrimitiveTypeInfo(typeEntry.typeName);
                        }
                    }
                }
            }
            else {
                if ("array".equals(t.text)) {
                    this.expect("<");
                    final TypeInfo listElementType = this.parseType();
                    this.expect(">");
                    return TypeInfoFactory.getListTypeInfo(listElementType);
                }
                if ("map".equals(t.text)) {
                    this.expect("<");
                    final TypeInfo mapKeyType = this.parseType();
                    this.expect(",");
                    final TypeInfo mapValueType = this.parseType();
                    this.expect(">");
                    return TypeInfoFactory.getMapTypeInfo(mapKeyType, mapValueType);
                }
                if ("struct".equals(t.text)) {
                    final ArrayList<String> fieldNames = new ArrayList<String>();
                    final ArrayList<TypeInfo> fieldTypeInfos = new ArrayList<TypeInfo>();
                    boolean first = true;
                    while (true) {
                        if (first) {
                            this.expect("<");
                            first = false;
                        }
                        else {
                            final Token separator = this.expect(">", ",");
                            if (separator.text.equals(">")) {
                                break;
                            }
                        }
                        final Token name = this.expect("name", ">");
                        if (name.text.equals(">")) {
                            break;
                        }
                        fieldNames.add(name.text);
                        this.expect(":");
                        fieldTypeInfos.add(this.parseType());
                    }
                    return TypeInfoFactory.getStructTypeInfo(fieldNames, fieldTypeInfos);
                }
                if ("uniontype".equals(t.text)) {
                    final List<TypeInfo> objectTypeInfos = new ArrayList<TypeInfo>();
                    boolean first2 = true;
                    while (true) {
                        if (first2) {
                            this.expect("<");
                            first2 = false;
                        }
                        else {
                            final Token separator2 = this.expect(">", ",");
                            if (separator2.text.equals(">")) {
                                break;
                            }
                        }
                        objectTypeInfos.add(this.parseType());
                    }
                    return TypeInfoFactory.getUnionTypeInfo(objectTypeInfos);
                }
                throw new RuntimeException("Internal error parsing position " + t.position + " of '" + this.typeInfoString + "'");
            }
        }
        
        public PrimitiveParts parsePrimitiveParts() {
            final PrimitiveParts parts = new PrimitiveParts();
            final Token t = this.expect("type");
            parts.typeName = t.text;
            parts.typeParams = this.parseParams();
            return parts;
        }
        
        private static class Token
        {
            public int position;
            public String text;
            public boolean isType;
            
            @Override
            public String toString() {
                return "" + this.position + ":" + this.text;
            }
        }
    }
    
    public static class PrimitiveParts
    {
        public String typeName;
        public String[] typeParams;
    }
}
