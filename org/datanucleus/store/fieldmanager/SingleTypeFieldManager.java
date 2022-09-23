// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

public class SingleTypeFieldManager implements FieldManager
{
    private boolean booleanValue;
    private char charValue;
    private byte byteValue;
    private short shortValue;
    private int intValue;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private String stringValue;
    private Object objectValue;
    
    public SingleTypeFieldManager() {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
    }
    
    public SingleTypeFieldManager(final boolean booleanValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.booleanValue = booleanValue;
    }
    
    public SingleTypeFieldManager(final char charValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.charValue = charValue;
    }
    
    public SingleTypeFieldManager(final byte byteValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.byteValue = byteValue;
    }
    
    public SingleTypeFieldManager(final short shortValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.shortValue = shortValue;
    }
    
    public SingleTypeFieldManager(final int intValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.intValue = intValue;
    }
    
    public SingleTypeFieldManager(final long longValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.longValue = longValue;
    }
    
    public SingleTypeFieldManager(final float floatValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.floatValue = floatValue;
    }
    
    public SingleTypeFieldManager(final double doubleValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.doubleValue = doubleValue;
    }
    
    public SingleTypeFieldManager(final String stringValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.stringValue = stringValue;
    }
    
    public SingleTypeFieldManager(final Object objectValue) {
        this.booleanValue = false;
        this.charValue = '\0';
        this.byteValue = 0;
        this.shortValue = 0;
        this.intValue = 0;
        this.longValue = 0L;
        this.floatValue = 0.0f;
        this.doubleValue = 0.0;
        this.stringValue = null;
        this.objectValue = null;
        this.objectValue = objectValue;
    }
    
    @Override
    public void storeBooleanField(final int fieldNum, final boolean value) {
        this.booleanValue = value;
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNum) {
        return this.booleanValue;
    }
    
    @Override
    public void storeCharField(final int fieldNum, final char value) {
        this.charValue = value;
    }
    
    @Override
    public char fetchCharField(final int fieldNum) {
        return this.charValue;
    }
    
    @Override
    public void storeByteField(final int fieldNum, final byte value) {
        this.byteValue = value;
    }
    
    @Override
    public byte fetchByteField(final int fieldNum) {
        return this.byteValue;
    }
    
    @Override
    public void storeShortField(final int fieldNum, final short value) {
        this.shortValue = value;
    }
    
    @Override
    public short fetchShortField(final int fieldNum) {
        return this.shortValue;
    }
    
    @Override
    public void storeIntField(final int fieldNum, final int value) {
        this.intValue = value;
    }
    
    @Override
    public int fetchIntField(final int fieldNum) {
        return this.intValue;
    }
    
    @Override
    public void storeLongField(final int fieldNum, final long value) {
        this.longValue = value;
    }
    
    @Override
    public long fetchLongField(final int fieldNum) {
        return this.longValue;
    }
    
    @Override
    public void storeFloatField(final int fieldNum, final float value) {
        this.floatValue = value;
    }
    
    @Override
    public float fetchFloatField(final int fieldNum) {
        return this.floatValue;
    }
    
    @Override
    public void storeDoubleField(final int fieldNum, final double value) {
        this.doubleValue = value;
    }
    
    @Override
    public double fetchDoubleField(final int fieldNum) {
        return this.doubleValue;
    }
    
    @Override
    public void storeStringField(final int fieldNum, final String value) {
        this.stringValue = value;
    }
    
    @Override
    public String fetchStringField(final int fieldNum) {
        return this.stringValue;
    }
    
    @Override
    public void storeObjectField(final int fieldNum, final Object value) {
        this.objectValue = value;
    }
    
    @Override
    public Object fetchObjectField(final int fieldNum) {
        return this.objectValue;
    }
}
