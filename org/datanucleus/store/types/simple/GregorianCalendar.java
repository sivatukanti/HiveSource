// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import java.util.TimeZone;
import java.util.Date;
import java.io.ObjectStreamException;
import org.datanucleus.state.FetchPlanState;
import java.util.Calendar;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.types.SCO;

public class GregorianCalendar extends java.util.GregorianCalendar implements SCO
{
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    
    public GregorianCalendar(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        this.ownerOP = op;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise() {
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final Calendar cal = (Calendar)o;
        super.setTimeInMillis(cal.getTime().getTime());
        super.setTimeZone(cal.getTimeZone());
    }
    
    @Override
    public Object getValue() {
        final java.util.GregorianCalendar cal = new java.util.GregorianCalendar(this.getTimeZone());
        cal.setTime(this.getTime());
        return cal;
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
        final java.util.GregorianCalendar cal = new java.util.GregorianCalendar(this.getTimeZone());
        cal.setTime(this.getTime());
        return cal;
    }
    
    @Override
    public void attachCopy(final Object value) {
        final long oldValue = this.getTimeInMillis();
        this.initialise(value, false, true);
        final long newValue = ((Calendar)value).getTime().getTime();
        if (oldValue != newValue) {
            this.makeDirty();
        }
    }
    
    @Override
    public Object clone() {
        final Object obj = super.clone();
        ((GregorianCalendar)obj).unsetOwner();
        return obj;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        final java.util.GregorianCalendar cal = new java.util.GregorianCalendar(this.getTimeZone());
        cal.setTime(this.getTime());
        return cal;
    }
    
    @Override
    public void add(final int field, final int amount) {
        super.add(field, amount);
        this.makeDirty();
    }
    
    @Override
    public void roll(final int field, final boolean up) {
        super.roll(field, up);
        this.makeDirty();
    }
    
    @Override
    public void roll(final int field, final int amount) {
        super.roll(field, amount);
        this.makeDirty();
    }
    
    @Override
    public void set(final int field, final int value) {
        super.set(field, value);
        this.makeDirty();
    }
    
    @Override
    public void setGregorianChange(final Date date) {
        super.setGregorianChange(date);
        this.makeDirty();
    }
    
    @Override
    public void setFirstDayOfWeek(final int value) {
        super.setFirstDayOfWeek(value);
        this.makeDirty();
    }
    
    @Override
    public void setLenient(final boolean lenient) {
        super.setLenient(lenient);
        this.makeDirty();
    }
    
    @Override
    public void setMinimalDaysInFirstWeek(final int value) {
        super.setMinimalDaysInFirstWeek(value);
        this.makeDirty();
    }
    
    @Override
    public void setTimeInMillis(final long millis) {
        super.setTimeInMillis(millis);
        this.makeDirty();
    }
    
    @Override
    public void setTimeZone(final TimeZone value) {
        super.setTimeZone(value);
        this.makeDirty();
    }
}
