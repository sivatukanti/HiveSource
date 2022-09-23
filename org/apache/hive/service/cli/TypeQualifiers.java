// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.util.Map;
import org.apache.hive.service.cli.thrift.TTypeQualifierValue;
import java.util.HashMap;
import org.apache.hive.service.cli.thrift.TTypeQualifiers;

public class TypeQualifiers
{
    private Integer characterMaximumLength;
    private Integer precision;
    private Integer scale;
    
    public Integer getCharacterMaximumLength() {
        return this.characterMaximumLength;
    }
    
    public void setCharacterMaximumLength(final int characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }
    
    public TTypeQualifiers toTTypeQualifiers() {
        TTypeQualifiers ret = null;
        final Map<String, TTypeQualifierValue> qMap = new HashMap<String, TTypeQualifierValue>();
        if (this.getCharacterMaximumLength() != null) {
            final TTypeQualifierValue val = new TTypeQualifierValue();
            val.setI32Value(this.getCharacterMaximumLength());
            qMap.put("characterMaximumLength", val);
        }
        if (this.precision != null) {
            final TTypeQualifierValue val = new TTypeQualifierValue();
            val.setI32Value(this.precision);
            qMap.put("precision", val);
        }
        if (this.scale != null) {
            final TTypeQualifierValue val = new TTypeQualifierValue();
            val.setI32Value(this.scale);
            qMap.put("scale", val);
        }
        if (qMap.size() > 0) {
            ret = new TTypeQualifiers(qMap);
        }
        return ret;
    }
    
    public static TypeQualifiers fromTTypeQualifiers(final TTypeQualifiers ttq) {
        TypeQualifiers ret = null;
        if (ttq != null) {
            ret = new TypeQualifiers();
            final Map<String, TTypeQualifierValue> tqMap = ttq.getQualifiers();
            if (tqMap.containsKey("characterMaximumLength")) {
                ret.setCharacterMaximumLength(tqMap.get("characterMaximumLength").getI32Value());
            }
            if (tqMap.containsKey("precision")) {
                ret.setPrecision(tqMap.get("precision").getI32Value());
            }
            if (tqMap.containsKey("scale")) {
                ret.setScale(tqMap.get("scale").getI32Value());
            }
        }
        return ret;
    }
    
    public static TypeQualifiers fromTypeInfo(final PrimitiveTypeInfo pti) {
        TypeQualifiers result = null;
        if (pti instanceof VarcharTypeInfo) {
            result = new TypeQualifiers();
            result.setCharacterMaximumLength(((VarcharTypeInfo)pti).getLength());
        }
        else if (pti instanceof CharTypeInfo) {
            result = new TypeQualifiers();
            result.setCharacterMaximumLength(((CharTypeInfo)pti).getLength());
        }
        else if (pti instanceof DecimalTypeInfo) {
            result = new TypeQualifiers();
            result.setPrecision(((DecimalTypeInfo)pti).precision());
            result.setScale(((DecimalTypeInfo)pti).scale());
        }
        return result;
    }
    
    public Integer getPrecision() {
        return this.precision;
    }
    
    public void setPrecision(final Integer precision) {
        this.precision = precision;
    }
    
    public Integer getScale() {
        return this.scale;
    }
    
    public void setScale(final Integer scale) {
        this.scale = scale;
    }
}
