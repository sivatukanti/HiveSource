// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.annotations.IdGeneratorStrategy;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.IdentityMetaData;
import javax.jdo.metadata.DatastoreIdentityMetadata;

public class DatastoreIdentityMetadataImpl extends AbstractMetadataImpl implements DatastoreIdentityMetadata
{
    public DatastoreIdentityMetadataImpl(final IdentityMetaData idmd) {
        super(idmd);
    }
    
    public IdentityMetaData getInternal() {
        return (IdentityMetaData)this.internalMD;
    }
    
    public String getColumn() {
        return this.getInternal().getColumnName();
    }
    
    public ColumnMetadata[] getColumns() {
        final ColumnMetaData internalColmd = this.getInternal().getColumnMetaData();
        if (internalColmd == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = { null };
        for (int i = 0; i < colmds.length; ++i) {
            colmds[i] = new ColumnMetadataImpl(internalColmd);
            colmds[i].parent = this;
        }
        return colmds;
    }
    
    public String getCustomStrategy() {
        final IdentityStrategy strategy = this.getInternal().getValueStrategy();
        if (strategy != IdentityStrategy.IDENTITY && strategy != IdentityStrategy.INCREMENT && strategy != IdentityStrategy.NATIVE && strategy != IdentityStrategy.SEQUENCE && strategy != IdentityStrategy.UUIDHEX && strategy != IdentityStrategy.UUIDSTRING && strategy != null) {
            return strategy.toString();
        }
        return null;
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? 1 : 0;
    }
    
    public String getSequence() {
        return this.getInternal().getSequence();
    }
    
    public IdGeneratorStrategy getStrategy() {
        final IdentityStrategy strategy = this.getInternal().getValueStrategy();
        if (strategy == IdentityStrategy.IDENTITY) {
            return IdGeneratorStrategy.IDENTITY;
        }
        if (strategy == IdentityStrategy.INCREMENT) {
            return IdGeneratorStrategy.INCREMENT;
        }
        if (strategy == IdentityStrategy.NATIVE) {
            return IdGeneratorStrategy.NATIVE;
        }
        if (strategy == IdentityStrategy.SEQUENCE) {
            return IdGeneratorStrategy.SEQUENCE;
        }
        if (strategy == IdentityStrategy.UUIDHEX) {
            return IdGeneratorStrategy.UUIDHEX;
        }
        if (strategy == IdentityStrategy.UUIDSTRING) {
            return IdGeneratorStrategy.UUIDSTRING;
        }
        return IdGeneratorStrategy.UNSPECIFIED;
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public DatastoreIdentityMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public DatastoreIdentityMetadata setCustomStrategy(final String strategy) {
        this.getInternal().setValueStrategy(IdentityStrategy.getIdentityStrategy(strategy));
        return this;
    }
    
    public DatastoreIdentityMetadata setSequence(final String seq) {
        this.getInternal().setSequence(seq);
        return this;
    }
    
    public DatastoreIdentityMetadata setStrategy(final IdGeneratorStrategy strategy) {
        if (strategy == IdGeneratorStrategy.IDENTITY) {
            this.getInternal().setValueStrategy(IdentityStrategy.IDENTITY);
        }
        else if (strategy == IdGeneratorStrategy.INCREMENT) {
            this.getInternal().setValueStrategy(IdentityStrategy.INCREMENT);
        }
        else if (strategy == IdGeneratorStrategy.NATIVE) {
            this.getInternal().setValueStrategy(IdentityStrategy.NATIVE);
        }
        else if (strategy == IdGeneratorStrategy.SEQUENCE) {
            this.getInternal().setValueStrategy(IdentityStrategy.SEQUENCE);
        }
        else if (strategy == IdGeneratorStrategy.UUIDHEX) {
            this.getInternal().setValueStrategy(IdentityStrategy.UUIDHEX);
        }
        else if (strategy == IdGeneratorStrategy.UUIDSTRING) {
            this.getInternal().setValueStrategy(IdentityStrategy.UUIDSTRING);
        }
        return this;
    }
}
