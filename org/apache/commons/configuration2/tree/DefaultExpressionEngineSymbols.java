// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class DefaultExpressionEngineSymbols
{
    public static final String DEFAULT_PROPERTY_DELIMITER = ".";
    public static final String DEFAULT_ESCAPED_DELIMITER = "..";
    public static final String DEFAULT_ATTRIBUTE_START = "[@";
    public static final String DEFAULT_ATTRIBUTE_END = "]";
    public static final String DEFAULT_INDEX_START = "(";
    public static final String DEFAULT_INDEX_END = ")";
    public static final DefaultExpressionEngineSymbols DEFAULT_SYMBOLS;
    private final String propertyDelimiter;
    private final String escapedDelimiter;
    private final String attributeStart;
    private final String attributeEnd;
    private final String indexStart;
    private final String indexEnd;
    
    private DefaultExpressionEngineSymbols(final Builder b) {
        this.propertyDelimiter = b.propertyDelimiter;
        this.escapedDelimiter = b.escapedDelimiter;
        this.indexStart = b.indexStart;
        this.indexEnd = b.indexEnd;
        this.attributeStart = b.attributeStart;
        this.attributeEnd = b.attributeEnd;
    }
    
    public String getPropertyDelimiter() {
        return this.propertyDelimiter;
    }
    
    public String getEscapedDelimiter() {
        return this.escapedDelimiter;
    }
    
    public String getAttributeStart() {
        return this.attributeStart;
    }
    
    public String getAttributeEnd() {
        return this.attributeEnd;
    }
    
    public String getIndexStart() {
        return this.indexStart;
    }
    
    public String getIndexEnd() {
        return this.indexEnd;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getPropertyDelimiter()).append(this.getEscapedDelimiter()).append(this.getIndexStart()).append(this.getIndexEnd()).append(this.getAttributeStart()).append(this.getAttributeEnd()).toHashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultExpressionEngineSymbols)) {
            return false;
        }
        final DefaultExpressionEngineSymbols c = (DefaultExpressionEngineSymbols)obj;
        return new EqualsBuilder().append(this.getPropertyDelimiter(), c.getPropertyDelimiter()).append(this.getEscapedDelimiter(), c.getEscapedDelimiter()).append(this.getIndexStart(), c.getIndexStart()).append(this.getIndexEnd(), c.getIndexEnd()).append(this.getAttributeStart(), c.getAttributeStart()).append(this.getAttributeEnd(), c.getAttributeEnd()).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("propertyDelimiter", this.getPropertyDelimiter()).append("escapedDelimiter", this.getEscapedDelimiter()).append("indexStart", this.getIndexStart()).append("indexEnd", this.getIndexEnd()).append("attributeStart", this.getAttributeStart()).append("attributeEnd", this.getAttributeEnd()).toString();
    }
    
    private static DefaultExpressionEngineSymbols createDefaultSmybols() {
        return new Builder().setPropertyDelimiter(".").setEscapedDelimiter("..").setIndexStart("(").setIndexEnd(")").setAttributeStart("[@").setAttributeEnd("]").create();
    }
    
    static {
        DEFAULT_SYMBOLS = createDefaultSmybols();
    }
    
    public static class Builder
    {
        private String propertyDelimiter;
        private String escapedDelimiter;
        private String attributeStart;
        private String attributeEnd;
        private String indexStart;
        private String indexEnd;
        
        public Builder() {
        }
        
        public Builder(final DefaultExpressionEngineSymbols c) {
            this.propertyDelimiter = c.getPropertyDelimiter();
            this.escapedDelimiter = c.getEscapedDelimiter();
            this.indexStart = c.getIndexStart();
            this.indexEnd = c.getIndexEnd();
            this.attributeStart = c.getAttributeStart();
            this.attributeEnd = c.getAttributeEnd();
        }
        
        public Builder setPropertyDelimiter(final String d) {
            this.propertyDelimiter = d;
            return this;
        }
        
        public Builder setEscapedDelimiter(final String ed) {
            this.escapedDelimiter = ed;
            return this;
        }
        
        public Builder setIndexStart(final String is) {
            this.indexStart = is;
            return this;
        }
        
        public Builder setIndexEnd(final String ie) {
            this.indexEnd = ie;
            return this;
        }
        
        public Builder setAttributeStart(final String as) {
            this.attributeStart = as;
            return this;
        }
        
        public Builder setAttributeEnd(final String ae) {
            this.attributeEnd = ae;
            return this;
        }
        
        public DefaultExpressionEngineSymbols create() {
            return new DefaultExpressionEngineSymbols(this, null);
        }
    }
}
