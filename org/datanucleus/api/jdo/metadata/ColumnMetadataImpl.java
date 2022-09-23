// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;

public class ColumnMetadataImpl extends AbstractMetadataImpl implements ColumnMetadata
{
    public ColumnMetadataImpl(final ColumnMetaData internal) {
        super(internal);
    }
    
    public ColumnMetaData getInternal() {
        return (ColumnMetaData)this.internalMD;
    }
    
    public Boolean getAllowsNull() {
        return this.getInternal().getAllowsNull();
    }
    
    public String getDefaultValue() {
        return this.getInternal().getDefaultValue();
    }
    
    public String getInsertValue() {
        return this.getInternal().getInsertValue();
    }
    
    public String getJDBCType() {
        return this.getInternal().getJdbcType();
    }
    
    public Integer getLength() {
        return this.getInternal().getLength();
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public Integer getPosition() {
        return this.getInternal().getPosition();
    }
    
    public String getSQLType() {
        return this.getInternal().getSqlType();
    }
    
    public Integer getScale() {
        return this.getInternal().getScale();
    }
    
    public String getTarget() {
        return this.getInternal().getTarget();
    }
    
    public String getTargetField() {
        return this.getInternal().getTargetMember();
    }
    
    public ColumnMetadata setAllowsNull(final boolean flag) {
        this.getInternal().setAllowsNull(flag);
        return this;
    }
    
    public ColumnMetadata setDefaultValue(final String val) {
        this.getInternal().setDefaultValue(val);
        return this;
    }
    
    public ColumnMetadata setInsertValue(final String val) {
        this.getInternal().setInsertValue(val);
        return this;
    }
    
    public ColumnMetadata setJDBCType(final String type) {
        this.getInternal().setJdbcType(type);
        return this;
    }
    
    public ColumnMetadata setLength(final int len) {
        this.getInternal().setLength(len);
        return this;
    }
    
    public ColumnMetadata setName(final String name) {
        this.getInternal().setName(name);
        return this;
    }
    
    public ColumnMetadata setPosition(final int pos) {
        this.getInternal().setPosition(pos);
        return this;
    }
    
    public ColumnMetadata setSQLType(final String type) {
        this.getInternal().setSqlType(type);
        return this;
    }
    
    public ColumnMetadata setScale(final int scale) {
        this.getInternal().setScale(scale);
        return this;
    }
    
    public ColumnMetadata setTarget(final String tgt) {
        this.getInternal().setTarget(tgt);
        return this;
    }
    
    public ColumnMetadata setTargetField(final String tgt) {
        this.getInternal().setTargetMember(tgt);
        return this;
    }
}
