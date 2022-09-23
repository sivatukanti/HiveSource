// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

public final class QueryResult<T>
{
    private final T node;
    private final String attributeName;
    
    private QueryResult(final T nd, final String attr) {
        this.node = nd;
        this.attributeName = attr;
    }
    
    public static <T> QueryResult<T> createNodeResult(final T resultNode) {
        return new QueryResult<T>(resultNode, null);
    }
    
    public static <T> QueryResult<T> createAttributeResult(final T parentNode, final String attrName) {
        return new QueryResult<T>(parentNode, attrName);
    }
    
    public T getNode() {
        return this.node;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public boolean isAttributeResult() {
        return StringUtils.isNotEmpty(this.getAttributeName());
    }
    
    public Object getAttributeValue(final NodeHandler<T> handler) {
        if (!this.isAttributeResult()) {
            throw new IllegalStateException("This is not an attribute result! Attribute value cannot be fetched.");
        }
        return handler.getAttributeValue(this.getNode(), this.getAttributeName());
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getNode()).append(this.getAttributeName()).toHashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueryResult)) {
            return false;
        }
        final QueryResult<?> c = (QueryResult<?>)obj;
        return new EqualsBuilder().append(this.getNode(), c.getNode()).append(this.getAttributeName(), c.getAttributeName()).isEquals();
    }
    
    @Override
    public String toString() {
        final ToStringBuilder sb = new ToStringBuilder(this);
        if (this.isAttributeResult()) {
            sb.append("parentNode", this.getNode()).append("attribute", this.getAttributeName());
        }
        else {
            sb.append("resultNode", this.getNode());
        }
        return sb.toString();
    }
}
