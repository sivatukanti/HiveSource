// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import java.io.ObjectStreamException;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.types.SCO;

public class Date extends java.util.Date implements SCO
{
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    
    public Date(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        this.ownerOP = op;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise() {
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        super.setTime(((java.util.Date)o).getTime());
    }
    
    @Override
    public Object getValue() {
        return new java.util.Date(this.getTime());
    }
    
    @Override
    public void unsetOwner() {
        this.ownerOP = null;
        this.ownerMmd = null;
    }
    
    @Override
    public Object getOwner() {
        return (this.ownerOP != null) ? this.ownerOP.getObject() : null;
    }
    
    @Override
    public String getFieldName() {
        return this.ownerMmd.getName();
    }
    
    public void makeDirty() {
        if (this.ownerOP != null) {
            this.ownerOP.makeDirty(this.ownerMmd.getAbsoluteFieldNumber());
            if (!this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
    }
    
    @Override
    public Object detachCopy(final FetchPlanState state) {
        return new java.util.Date(this.getTime());
    }
    
    @Override
    public void attachCopy(final Object value) {
        final long oldValue = this.getTime();
        this.initialise(value, false, true);
        final long newValue = ((java.util.Date)value).getTime();
        if (oldValue != newValue) {
            this.makeDirty();
        }
    }
    
    @Override
    public Object clone() {
        final Object obj = super.clone();
        ((Date)obj).unsetOwner();
        return obj;
    }
    
    @Override
    public void setTime(final long time) {
        super.setTime(time);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setYear(final int year) {
        super.setYear(year);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setMonth(final int month) {
        super.setMonth(month);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setDate(final int date) {
        super.setDate(date);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setHours(final int hours) {
        super.setHours(hours);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setMinutes(final int minutes) {
        super.setMinutes(minutes);
        this.makeDirty();
    }
    
    @Override
    @Deprecated
    public void setSeconds(final int seconds) {
        super.setSeconds(seconds);
        this.makeDirty();
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new java.util.Date(this.getTime());
    }
}
