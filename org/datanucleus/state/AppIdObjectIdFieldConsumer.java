// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.api.ApiAdapter;
import javax.jdo.spi.PersistenceCapable;
import org.datanucleus.store.fieldmanager.FieldManager;

public class AppIdObjectIdFieldConsumer implements FieldManager, PersistenceCapable.ObjectIdFieldConsumer
{
    ApiAdapter api;
    FieldManager fm;
    
    public AppIdObjectIdFieldConsumer(final ApiAdapter api, final FieldManager fm) {
        this.api = api;
        this.fm = fm;
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        this.fm.storeBooleanField(fieldNumber, value);
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        this.fm.storeByteField(fieldNumber, value);
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        this.fm.storeCharField(fieldNumber, value);
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        this.fm.storeDoubleField(fieldNumber, value);
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        this.fm.storeFloatField(fieldNumber, value);
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        this.fm.storeIntField(fieldNumber, value);
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        this.fm.storeLongField(fieldNumber, value);
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        this.fm.storeShortField(fieldNumber, value);
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        this.fm.storeStringField(fieldNumber, value);
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        if (this.api.isPersistable(value)) {
            final PersistenceCapable pc = (PersistenceCapable)value;
            pc.jdoCopyKeyFieldsFromObjectId(this, pc.jdoGetObjectId());
            return;
        }
        this.fm.storeObjectField(fieldNumber, value);
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return this.fm.fetchBooleanField(fieldNumber);
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        return this.fm.fetchByteField(fieldNumber);
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        return this.fm.fetchCharField(fieldNumber);
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return this.fm.fetchDoubleField(fieldNumber);
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        return this.fm.fetchFloatField(fieldNumber);
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        return this.fm.fetchIntField(fieldNumber);
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        return this.fm.fetchLongField(fieldNumber);
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        return this.fm.fetchShortField(fieldNumber);
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        return this.fm.fetchStringField(fieldNumber);
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        return this.fm.fetchObjectField(fieldNumber);
    }
}
