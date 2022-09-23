// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class CreateSequenceNode extends DDLStatementNode
{
    private TableName _sequenceName;
    private DataTypeDescriptor _dataType;
    private Long _initialValue;
    private Long _stepValue;
    private Long _maxValue;
    private Long _minValue;
    private Boolean _cycle;
    public static final int SEQUENCE_ELEMENT_COUNT = 1;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) throws StandardException {
        this.initAndCheck(this._sequenceName = (TableName)o);
        if (o2 != null) {
            this._dataType = (DataTypeDescriptor)o2;
        }
        else {
            this._dataType = DataTypeDescriptor.INTEGER;
        }
        this._stepValue = (Long)((o4 != null) ? o4 : new Long(1L));
        if (this._dataType.getTypeId().equals(TypeId.SMALLINT_ID)) {
            this._minValue = (Long)((o6 != null) ? o6 : new Long(-32768L));
            this._maxValue = (Long)((o5 != null) ? o5 : new Long(32767L));
        }
        else if (this._dataType.getTypeId().equals(TypeId.INTEGER_ID)) {
            this._minValue = (Long)((o6 != null) ? o6 : new Long(-2147483648L));
            this._maxValue = (Long)((o5 != null) ? o5 : new Long(2147483647L));
        }
        else {
            this._minValue = (Long)((o6 != null) ? o6 : new Long(Long.MIN_VALUE));
            this._maxValue = (Long)((o5 != null) ? o5 : new Long(Long.MAX_VALUE));
        }
        if (o3 != null) {
            this._initialValue = (Long)o3;
        }
        else if (this._stepValue > 0L) {
            this._initialValue = this._minValue;
        }
        else {
            this._initialValue = this._maxValue;
        }
        this._cycle = (Boolean)((o7 != null) ? o7 : Boolean.FALSE);
        this.implicitCreateSchema = true;
    }
    
    public String toString() {
        return "";
    }
    
    public void bindStatement() throws StandardException {
        this.getCompilerContext();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        if (this._sequenceName.getSchemaName() == null) {
            this._sequenceName.setSchemaName(schemaDescriptor.getSchemaName());
        }
        if (this._dataType.getTypeId().equals(TypeId.SMALLINT_ID)) {
            if (this._minValue < -32768L || this._minValue >= 32767L) {
                throw StandardException.newException("42XAE", "MINVALUE", "SMALLINT", "-32768", "32767");
            }
            if (this._maxValue <= -32768L || this._maxValue > 32767L) {
                throw StandardException.newException("42XAE", "MAXVALUE", "SMALLINT", "-32768", "32767");
            }
        }
        else if (this._dataType.getTypeId().equals(TypeId.INTEGER_ID)) {
            if (this._minValue < -2147483648L || this._minValue >= 2147483647L) {
                throw StandardException.newException("42XAE", "MINVALUE", "INTEGER", "-2147483648", "2147483647");
            }
            if (this._maxValue <= -2147483648L || this._maxValue > 2147483647L) {
                throw StandardException.newException("42XAE", "MAXVALUE", "INTEGER", "-2147483648", "2147483647");
            }
        }
        else {
            if (this._minValue < Long.MIN_VALUE || this._minValue >= Long.MAX_VALUE) {
                throw StandardException.newException("42XAE", "MINVALUE", "BIGINT", "-9223372036854775808", "9223372036854775807");
            }
            if (this._maxValue <= Long.MIN_VALUE || this._maxValue > Long.MAX_VALUE) {
                throw StandardException.newException("42XAE", "MAXVALUE", "BIGINT", "-9223372036854775808", "9223372036854775807");
            }
        }
        if (this._minValue >= this._maxValue) {
            throw StandardException.newException("42XAF", this._minValue.toString(), this._maxValue.toString());
        }
        if (this._initialValue < this._minValue || this._initialValue > this._maxValue) {
            throw StandardException.newException("42XAG", this._initialValue.toString(), this._minValue.toString(), this._maxValue.toString());
        }
        if (this._stepValue == 0L) {
            throw StandardException.newException("42XAC");
        }
    }
    
    public String statementToString() {
        return "CREATE SEQUENCE";
    }
    
    public ConstantAction makeConstantAction() {
        return this.getGenericConstantActionFactory().getCreateSequenceConstantAction(this._sequenceName, this._dataType, this._initialValue, this._stepValue, this._maxValue, this._minValue, this._cycle);
    }
}
