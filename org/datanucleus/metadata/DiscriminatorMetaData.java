// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;

public class DiscriminatorMetaData extends MetaData
{
    protected DiscriminatorStrategy strategy;
    protected String columnName;
    protected String value;
    protected IndexedValue indexed;
    protected ColumnMetaData columnMetaData;
    protected IndexMetaData indexMetaData;
    
    public DiscriminatorMetaData(final DiscriminatorMetaData dmd) {
        super(null, dmd);
        this.strategy = null;
        this.columnName = null;
        this.value = null;
        this.indexed = null;
        this.columnMetaData = null;
        this.columnName = dmd.columnName;
        this.value = dmd.value;
        this.strategy = dmd.strategy;
        this.indexed = dmd.indexed;
        if (dmd.columnMetaData != null) {
            this.setColumnMetaData(new ColumnMetaData(dmd.columnMetaData));
        }
        if (dmd.indexMetaData != null) {
            this.setIndexMetaData(new IndexMetaData(dmd.indexMetaData));
        }
    }
    
    public DiscriminatorMetaData() {
        this.strategy = null;
        this.columnName = null;
        this.value = null;
        this.indexed = null;
        this.columnMetaData = null;
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.value != null && this.strategy == null) {
            this.strategy = DiscriminatorStrategy.VALUE_MAP;
        }
        else if (this.strategy == null) {
            this.strategy = DiscriminatorStrategy.CLASS_NAME;
        }
        if (this.strategy == DiscriminatorStrategy.VALUE_MAP && this.value == null) {
            final AbstractClassMetaData cmd = (AbstractClassMetaData)this.parent.getParent();
            if (cmd instanceof InterfaceMetaData || (cmd instanceof ClassMetaData && !((ClassMetaData)cmd).isAbstract())) {
                final String className = cmd.getFullClassName();
                NucleusLogger.METADATA.warn(DiscriminatorMetaData.LOCALISER.msg("044103", className));
                this.value = className;
            }
        }
        if (this.columnMetaData == null && this.columnName != null) {
            (this.columnMetaData = new ColumnMetaData()).setName(this.columnName);
            this.columnMetaData.parent = this;
            this.columnMetaData.initialise(clr, mmgr);
        }
        if (this.indexMetaData == null && this.columnMetaData != null && this.indexed != null && this.indexed != IndexedValue.FALSE) {
            (this.indexMetaData = new IndexMetaData()).setUnique(this.indexed == IndexedValue.UNIQUE);
            this.indexMetaData.addColumn(this.columnMetaData);
        }
        if (this.indexMetaData != null) {
            this.indexMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
    }
    
    public ColumnMetaData getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public void setColumnMetaData(final ColumnMetaData columnMetaData) {
        this.columnMetaData = columnMetaData;
        this.columnMetaData.parent = this;
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.setColumnMetaData(colmd);
        return colmd;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final void setIndexMetaData(final IndexMetaData indexMetaData) {
        this.indexMetaData = indexMetaData;
        this.indexMetaData.parent = this;
    }
    
    public IndexMetaData newIndexMetaData() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.setIndexMetaData(idxmd);
        return idxmd;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public DiscriminatorMetaData setValue(final String value) {
        if (!StringUtils.isWhitespace(value)) {
            this.value = value;
        }
        return this;
    }
    
    public String getColumnName() {
        if (this.columnMetaData != null && this.columnMetaData.getName() != null) {
            return this.columnMetaData.getName();
        }
        return this.columnName;
    }
    
    public DiscriminatorMetaData setColumnName(final String columnName) {
        this.columnName = (StringUtils.isWhitespace(columnName) ? null : columnName);
        return this;
    }
    
    public final DiscriminatorStrategy getStrategy() {
        return this.strategy;
    }
    
    public DiscriminatorMetaData setStrategy(final DiscriminatorStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    public DiscriminatorMetaData setStrategy(final String strategy) {
        this.strategy = DiscriminatorStrategy.getDiscriminatorStrategy(strategy);
        return this;
    }
    
    public IndexedValue getIndexed() {
        return this.indexed;
    }
    
    public DiscriminatorMetaData setIndexed(final IndexedValue indexed) {
        this.indexed = indexed;
        return this;
    }
    
    public DiscriminatorMetaData setIndexed(final String indexed) {
        this.indexed = IndexedValue.getIndexedValue(indexed);
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<discriminator");
        if (this.strategy != null) {
            sb.append(" strategy=\"" + this.strategy.toString() + "\"");
        }
        if (this.columnName != null && this.columnMetaData == null) {
            sb.append(" column=\"" + this.columnName + "\"");
        }
        if (this.value != null) {
            sb.append(" value=\"" + this.value + "\"");
        }
        if (this.indexed != null) {
            sb.append(" indexed=\"" + this.indexed.toString() + "\"");
        }
        sb.append(">\n");
        if (this.columnMetaData != null) {
            sb.append(this.columnMetaData.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</discriminator>\n");
        return sb.toString();
    }
}
