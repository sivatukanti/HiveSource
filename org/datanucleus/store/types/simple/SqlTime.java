// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import java.io.ObjectStreamException;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.types.SCO;
import java.sql.Time;

public class SqlTime extends Time implements SCO
{
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    
    public SqlTime(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        super(0L);
        this.ownerOP = op;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise() {
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        super.setTime(((Time)o).getTime());
    }
    
    @Override
    public Object getValue() {
        return new Time(this.getTime());
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
        return this.ownerMmd.getFullFieldName();
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
        return new Time(this.getTime());
    }
    
    @Override
    public void attachCopy(final Object value) {
        final long oldValue = this.getTime();
        this.initialise(value, false, true);
        final long newValue = ((Time)value).getTime();
        if (oldValue != newValue) {
            this.makeDirty();
        }
    }
    
    @Override
    public Object clone() {
        final Object obj = super.clone();
        ((SqlTime)obj).unsetOwner();
        return obj;
    }
    
    @Override
    public void setTime(final long time) {
        super.setTime(time);
        this.makeDirty();
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new Time(this.getTime());
    }
}
