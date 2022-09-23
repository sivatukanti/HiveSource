// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.exceptions.NucleusException;

public abstract class AbstractFieldManager implements FieldManager
{
    private String failureMessage(final String method) {
        return "Somehow " + this.getClass().getName() + "." + method + "() was called, which should have been impossible";
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        throw new NucleusException(this.failureMessage("storeBooleanField")).setFatal();
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchBooleanField")).setFatal();
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        throw new NucleusException(this.failureMessage("storeCharField")).setFatal();
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchCharField")).setFatal();
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        throw new NucleusException(this.failureMessage("storeByteField")).setFatal();
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchByteField")).setFatal();
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        throw new NucleusException(this.failureMessage("storeShortField")).setFatal();
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchShortField")).setFatal();
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        throw new NucleusException(this.failureMessage("storeIntField")).setFatal();
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchIntField")).setFatal();
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        throw new NucleusException(this.failureMessage("storeLongField")).setFatal();
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchLongField")).setFatal();
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        throw new NucleusException(this.failureMessage("storeFloatField")).setFatal();
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchFloatField")).setFatal();
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        throw new NucleusException(this.failureMessage("storeDoubleField")).setFatal();
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchDoubleField")).setFatal();
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        throw new NucleusException(this.failureMessage("storeStringField")).setFatal();
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchStringField")).setFatal();
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        throw new NucleusException(this.failureMessage("storeObjectField")).setFatal();
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        throw new NucleusException(this.failureMessage("fetchObjectField")).setFatal();
    }
}
