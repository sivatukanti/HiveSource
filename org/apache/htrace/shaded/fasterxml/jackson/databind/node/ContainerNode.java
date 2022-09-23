// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.node;

import org.apache.htrace.shaded.fasterxml.jackson.core.TreeNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;

public abstract class ContainerNode<T extends ContainerNode<T>> extends BaseJsonNode implements JsonNodeCreator
{
    protected final JsonNodeFactory _nodeFactory;
    
    protected ContainerNode(final JsonNodeFactory nc) {
        this._nodeFactory = nc;
    }
    
    @Override
    public abstract JsonToken asToken();
    
    @Override
    public String asText() {
        return "";
    }
    
    @Override
    public abstract int size();
    
    @Override
    public abstract JsonNode get(final int p0);
    
    @Override
    public abstract JsonNode get(final String p0);
    
    @Override
    public final ArrayNode arrayNode() {
        return this._nodeFactory.arrayNode();
    }
    
    @Override
    public final ObjectNode objectNode() {
        return this._nodeFactory.objectNode();
    }
    
    @Override
    public final NullNode nullNode() {
        return this._nodeFactory.nullNode();
    }
    
    @Override
    public final BooleanNode booleanNode(final boolean v) {
        return this._nodeFactory.booleanNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final byte v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final short v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final int v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final long v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final BigInteger v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final float v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final double v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final NumericNode numberNode(final BigDecimal v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Byte v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Short v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Integer v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Long v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Float v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final ValueNode numberNode(final Double v) {
        return this._nodeFactory.numberNode(v);
    }
    
    @Override
    public final TextNode textNode(final String text) {
        return this._nodeFactory.textNode(text);
    }
    
    @Override
    public final BinaryNode binaryNode(final byte[] data) {
        return this._nodeFactory.binaryNode(data);
    }
    
    @Override
    public final BinaryNode binaryNode(final byte[] data, final int offset, final int length) {
        return this._nodeFactory.binaryNode(data, offset, length);
    }
    
    @Override
    public final ValueNode pojoNode(final Object pojo) {
        return this._nodeFactory.pojoNode(pojo);
    }
    
    @Deprecated
    public final POJONode POJONode(final Object pojo) {
        return (POJONode)this._nodeFactory.pojoNode(pojo);
    }
    
    public abstract T removeAll();
}
