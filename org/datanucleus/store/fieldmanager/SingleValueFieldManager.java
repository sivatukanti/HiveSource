// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

public class SingleValueFieldManager implements FieldManager
{
    private Object fieldValue;
    
    public SingleValueFieldManager() {
        this.fieldValue = null;
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        this.fieldValue = (value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return (boolean)this.fieldValue;
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        this.fieldValue = value;
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        return (char)this.fieldValue;
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        this.fieldValue = value;
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        return (byte)this.fieldValue;
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        this.fieldValue = value;
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        return (short)this.fieldValue;
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        this.fieldValue = value;
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        return (int)this.fieldValue;
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        this.fieldValue = value;
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        return (long)this.fieldValue;
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        this.fieldValue = value;
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        return (float)this.fieldValue;
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        this.fieldValue = value;
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return (double)this.fieldValue;
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        this.fieldValue = value;
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        return (String)this.fieldValue;
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        this.fieldValue = value;
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        return this.fieldValue;
    }
}
