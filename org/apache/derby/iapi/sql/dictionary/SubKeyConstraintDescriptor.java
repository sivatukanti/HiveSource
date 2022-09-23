// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;

public class SubKeyConstraintDescriptor extends SubConstraintDescriptor
{
    UUID indexId;
    UUID keyConstraintId;
    int raDeleteRule;
    int raUpdateRule;
    
    public SubKeyConstraintDescriptor(final UUID uuid, final UUID indexId) {
        super(uuid);
        this.indexId = indexId;
    }
    
    public SubKeyConstraintDescriptor(final UUID uuid, final UUID uuid2, final UUID keyConstraintId) {
        this(uuid, uuid2);
        this.keyConstraintId = keyConstraintId;
    }
    
    public SubKeyConstraintDescriptor(final UUID uuid, final UUID uuid2, final UUID keyConstraintId, final int raDeleteRule, final int raUpdateRule) {
        this(uuid, uuid2);
        this.keyConstraintId = keyConstraintId;
        this.raDeleteRule = raDeleteRule;
        this.raUpdateRule = raUpdateRule;
    }
    
    public UUID getIndexId() {
        return this.indexId;
    }
    
    public UUID getKeyConstraintId() {
        return this.keyConstraintId;
    }
    
    public boolean hasBackingIndex() {
        return true;
    }
    
    public int getRaDeleteRule() {
        return this.raDeleteRule;
    }
    
    public int getRaUpdateRule() {
        return this.raUpdateRule;
    }
    
    public String toString() {
        return "";
    }
}
