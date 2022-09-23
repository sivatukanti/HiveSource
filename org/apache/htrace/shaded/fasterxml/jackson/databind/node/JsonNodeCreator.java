// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonNodeCreator
{
    ValueNode booleanNode(final boolean p0);
    
    ValueNode nullNode();
    
    ValueNode numberNode(final byte p0);
    
    ValueNode numberNode(final Byte p0);
    
    ValueNode numberNode(final short p0);
    
    ValueNode numberNode(final Short p0);
    
    ValueNode numberNode(final int p0);
    
    ValueNode numberNode(final Integer p0);
    
    ValueNode numberNode(final long p0);
    
    ValueNode numberNode(final Long p0);
    
    ValueNode numberNode(final BigInteger p0);
    
    ValueNode numberNode(final float p0);
    
    ValueNode numberNode(final Float p0);
    
    ValueNode numberNode(final double p0);
    
    ValueNode numberNode(final Double p0);
    
    ValueNode numberNode(final BigDecimal p0);
    
    ValueNode textNode(final String p0);
    
    ValueNode binaryNode(final byte[] p0);
    
    ValueNode binaryNode(final byte[] p0, final int p1, final int p2);
    
    ValueNode pojoNode(final Object p0);
    
    ArrayNode arrayNode();
    
    ObjectNode objectNode();
}
