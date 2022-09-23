// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public class SequenceMetaData extends MetaData
{
    protected String name;
    protected String datastoreSequence;
    protected String factoryClass;
    protected SequenceStrategy strategy;
    protected int initialValue;
    protected int allocationSize;
    
    public SequenceMetaData(final String name, final String strategyValue) {
        this.initialValue = -1;
        this.allocationSize = -1;
        this.name = name;
        this.strategy = SequenceStrategy.getStrategy(strategyValue);
    }
    
    public String getFullyQualifiedName() {
        final PackageMetaData pmd = (PackageMetaData)this.getParent();
        return pmd.getName() + "." + this.name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public SequenceMetaData setName(final String name) {
        this.name = (StringUtils.isWhitespace(name) ? this.name : name);
        return this;
    }
    
    public SequenceStrategy getStrategy() {
        return this.strategy;
    }
    
    public SequenceMetaData setStrategy(final SequenceStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    public String getDatastoreSequence() {
        return this.datastoreSequence;
    }
    
    public SequenceMetaData setDatastoreSequence(final String datastoreSequence) {
        this.datastoreSequence = (StringUtils.isWhitespace(datastoreSequence) ? null : datastoreSequence);
        return this;
    }
    
    public String getFactoryClass() {
        return this.factoryClass;
    }
    
    public SequenceMetaData setFactoryClass(final String factoryClass) {
        this.factoryClass = (StringUtils.isWhitespace(factoryClass) ? null : factoryClass);
        return this;
    }
    
    public int getInitialValue() {
        return this.initialValue;
    }
    
    public SequenceMetaData setInitialValue(final int initialValue) {
        this.initialValue = initialValue;
        return this;
    }
    
    public SequenceMetaData setInitialValue(final String initialValue) {
        if (!StringUtils.isWhitespace(initialValue)) {
            try {
                this.initialValue = Integer.parseInt(initialValue);
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    public int getAllocationSize() {
        return this.allocationSize;
    }
    
    public SequenceMetaData setAllocationSize(final int allocationSize) {
        this.allocationSize = allocationSize;
        return this;
    }
    
    public SequenceMetaData setAllocationSize(final String allocationSize) {
        if (!StringUtils.isWhitespace(allocationSize)) {
            try {
                this.allocationSize = Integer.parseInt(allocationSize);
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<sequence name=\"" + this.name + "\"");
        if (this.datastoreSequence != null) {
            sb.append(" datastore-sequence=\"" + this.datastoreSequence + "\"");
        }
        if (this.factoryClass != null) {
            sb.append(" factory-class=\"" + this.factoryClass + "\"");
        }
        if (this.initialValue >= 0) {
            sb.append(" initial-value=\"" + this.initialValue + "\"");
        }
        if (this.allocationSize >= 0) {
            sb.append(" allocation-size=\"" + this.allocationSize + "\"");
        }
        if (this.strategy != null) {
            sb.append(" strategy=\"" + this.strategy.toString() + "\">");
        }
        sb.append(">\n");
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</sequence>\n");
        return sb.toString();
    }
}
