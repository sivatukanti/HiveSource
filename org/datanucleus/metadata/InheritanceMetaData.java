// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassLoaderResolver;

public class InheritanceMetaData extends MetaData
{
    protected InheritanceStrategy strategy;
    protected JoinMetaData joinMetaData;
    protected DiscriminatorMetaData discriminatorMetaData;
    protected String strategyForTree;
    
    public InheritanceMetaData() {
        this.strategy = null;
        this.strategyForTree = null;
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.joinMetaData != null) {
            this.joinMetaData.initialise(clr, mmgr);
        }
        if (this.discriminatorMetaData != null) {
            this.discriminatorMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
    }
    
    public InheritanceMetaData setStrategyForTree(final String strategy) {
        this.strategyForTree = strategy;
        return this;
    }
    
    public String getStrategyForTree() {
        return this.strategyForTree;
    }
    
    public InheritanceStrategy getStrategy() {
        return this.strategy;
    }
    
    public InheritanceMetaData setStrategy(final InheritanceStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    public InheritanceMetaData setStrategy(final String strategy) {
        this.strategy = InheritanceStrategy.getInheritanceStrategy(strategy);
        return this;
    }
    
    public JoinMetaData getJoinMetaData() {
        return this.joinMetaData;
    }
    
    public void setJoinMetaData(final JoinMetaData joinMetaData) {
        this.joinMetaData = joinMetaData;
        if (this.joinMetaData != null) {
            this.joinMetaData.parent = this;
        }
    }
    
    public JoinMetaData newJoinMetadata() {
        final JoinMetaData joinmd = new JoinMetaData();
        this.setJoinMetaData(joinmd);
        return joinmd;
    }
    
    public DiscriminatorMetaData getDiscriminatorMetaData() {
        return this.discriminatorMetaData;
    }
    
    public void setDiscriminatorMetaData(final DiscriminatorMetaData discriminatorMetaData) {
        this.discriminatorMetaData = discriminatorMetaData;
        this.discriminatorMetaData.parent = this;
    }
    
    public DiscriminatorMetaData newDiscriminatorMetadata() {
        final DiscriminatorMetaData dismd = new DiscriminatorMetaData();
        this.setDiscriminatorMetaData(dismd);
        return dismd;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<inheritance strategy=\"" + this.strategy + "\">\n");
        if (this.joinMetaData != null) {
            sb.append(this.joinMetaData.toString(prefix + indent, indent));
        }
        if (this.discriminatorMetaData != null) {
            sb.append(this.discriminatorMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</inheritance>\n");
        return sb.toString();
    }
}
