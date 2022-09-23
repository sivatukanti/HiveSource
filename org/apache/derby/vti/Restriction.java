// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import org.apache.derby.iapi.util.StringUtil;
import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;
import org.apache.derby.iapi.util.IdUtil;
import java.io.Serializable;

public abstract class Restriction implements Serializable
{
    public abstract String toSQL();
    
    protected String parenthesize(final String str) {
        return "( " + str + " )";
    }
    
    public static class ColumnQualifier extends Restriction
    {
        public static final long serialVersionUID = -8205388794606605844L;
        public static final int ORDER_OP_LESSTHAN = 0;
        public static final int ORDER_OP_EQUALS = 1;
        public static final int ORDER_OP_LESSOREQUALS = 2;
        public static final int ORDER_OP_GREATERTHAN = 3;
        public static final int ORDER_OP_GREATEROREQUALS = 4;
        public static final int ORDER_OP_ISNULL = 5;
        public static final int ORDER_OP_ISNOTNULL = 6;
        public static final int ORDER_OP_NOT_EQUALS = 7;
        private String[] OPERATOR_SYMBOLS;
        private String _columnName;
        private int _comparisonOperator;
        private Object _constantOperand;
        
        public ColumnQualifier(final String columnName, final int comparisonOperator, final Object constantOperand) {
            this.OPERATOR_SYMBOLS = new String[] { "<", "=", "<=", ">", ">=", "IS NULL", "IS NOT NULL", "!=" };
            this._columnName = columnName;
            this._comparisonOperator = comparisonOperator;
            this._constantOperand = constantOperand;
        }
        
        public String getColumnName() {
            return this._columnName;
        }
        
        public int getComparisonOperator() {
            return this._comparisonOperator;
        }
        
        public Object getConstantOperand() {
            return this._constantOperand;
        }
        
        public String toSQL() {
            final StringBuffer sb = new StringBuffer();
            sb.append(IdUtil.normalToDelimited(this._columnName));
            sb.append(" " + this.OPERATOR_SYMBOLS[this._comparisonOperator] + " ");
            if (this._constantOperand != null) {
                sb.append(this.toEscapedString(this._constantOperand));
            }
            return sb.toString();
        }
        
        protected String toEscapedString(final Object o) {
            if (o instanceof Timestamp) {
                return "TIMESTAMP('" + o.toString() + "')";
            }
            if (o instanceof Date) {
                return "DATE('" + o.toString() + "')";
            }
            if (o instanceof Time) {
                return "TIME('" + o.toString() + "')";
            }
            if (o instanceof String) {
                return "'" + o.toString() + "'";
            }
            if (o instanceof byte[]) {
                final byte[] array = (byte[])o;
                return "X'" + StringUtil.toHexString(array, 0, array.length) + "'";
            }
            return o.toString();
        }
    }
    
    public static class OR extends Restriction
    {
        public static final long serialVersionUID = -8205388794606605844L;
        private Restriction _leftChild;
        private Restriction _rightChild;
        
        public OR(final Restriction leftChild, final Restriction rightChild) {
            this._leftChild = leftChild;
            this._rightChild = rightChild;
        }
        
        public Restriction getLeftChild() {
            return this._leftChild;
        }
        
        public Restriction getRightChild() {
            return this._rightChild;
        }
        
        public String toSQL() {
            return this.parenthesize(this._leftChild.toSQL()) + " OR " + this.parenthesize(this._rightChild.toSQL());
        }
    }
    
    public static class AND extends Restriction
    {
        public static final long serialVersionUID = -8205388794606605844L;
        private Restriction _leftChild;
        private Restriction _rightChild;
        
        public AND(final Restriction leftChild, final Restriction rightChild) {
            this._leftChild = leftChild;
            this._rightChild = rightChild;
        }
        
        public Restriction getLeftChild() {
            return this._leftChild;
        }
        
        public Restriction getRightChild() {
            return this._rightChild;
        }
        
        public String toSQL() {
            return this.parenthesize(this._leftChild.toSQL()) + " AND " + this.parenthesize(this._rightChild.toSQL());
        }
    }
}
