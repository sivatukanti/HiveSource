// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.fieldmanager;

import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.sql.PreparedStatement;
import org.datanucleus.state.ObjectProvider;

public class OldValueParameterSetter extends ParameterSetter
{
    public OldValueParameterSetter(final ObjectProvider op, final PreparedStatement stmt, final StatementClassMapping stmtMappings) {
        super(op, stmt, stmtMappings);
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeBooleanField(fieldNumber, (boolean)oldValue);
        }
        else {
            super.storeBooleanField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeCharField(fieldNumber, (char)oldValue);
        }
        else {
            super.storeCharField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeByteField(fieldNumber, (byte)oldValue);
        }
        else {
            super.storeByteField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeShortField(fieldNumber, (short)oldValue);
        }
        else {
            super.storeShortField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeIntField(fieldNumber, (int)oldValue);
        }
        else {
            super.storeIntField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeLongField(fieldNumber, (long)oldValue);
        }
        else {
            super.storeLongField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeFloatField(fieldNumber, (float)oldValue);
        }
        else {
            super.storeFloatField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeDoubleField(fieldNumber, (double)oldValue);
        }
        else {
            super.storeDoubleField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeStringField(fieldNumber, (String)oldValue);
        }
        else {
            super.storeStringField(fieldNumber, value);
        }
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        final Object oldValue = this.op.getAssociatedValue("FIELD_VALUE.ORIGINAL." + fieldNumber);
        if (oldValue != null) {
            super.storeObjectField(fieldNumber, oldValue);
        }
        else {
            super.storeObjectField(fieldNumber, value);
        }
    }
}
