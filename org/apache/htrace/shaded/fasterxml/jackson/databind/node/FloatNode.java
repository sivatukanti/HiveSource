// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.node;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberOutput;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;

public class FloatNode extends NumericNode
{
    protected final float _value;
    
    public FloatNode(final float v) {
        this._value = v;
    }
    
    public static FloatNode valueOf(final float v) {
        return new FloatNode(v);
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_FLOAT;
    }
    
    @Override
    public JsonParser.NumberType numberType() {
        return JsonParser.NumberType.FLOAT;
    }
    
    @Override
    public boolean isFloatingPointNumber() {
        return true;
    }
    
    @Override
    public boolean isFloat() {
        return true;
    }
    
    @Override
    public boolean canConvertToInt() {
        return this._value >= -2.14748365E9f && this._value <= 2.14748365E9f;
    }
    
    @Override
    public boolean canConvertToLong() {
        return this._value >= -9.223372E18f && this._value <= 9.223372E18f;
    }
    
    @Override
    public Number numberValue() {
        return this._value;
    }
    
    @Override
    public short shortValue() {
        return (short)this._value;
    }
    
    @Override
    public int intValue() {
        return (int)this._value;
    }
    
    @Override
    public long longValue() {
        return (long)this._value;
    }
    
    @Override
    public float floatValue() {
        return this._value;
    }
    
    @Override
    public double doubleValue() {
        return this._value;
    }
    
    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(this._value);
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return this.decimalValue().toBigInteger();
    }
    
    @Override
    public String asText() {
        return NumberOutput.toString(this._value);
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNumber(this._value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof FloatNode) {
            final float otherValue = ((FloatNode)o)._value;
            return Float.compare(this._value, otherValue) == 0;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this._value);
    }
}
