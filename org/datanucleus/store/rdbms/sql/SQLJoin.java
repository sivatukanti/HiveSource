// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;

public class SQLJoin
{
    private JoinType type;
    private SQLTable table;
    private SQLTable joinedTable;
    private BooleanExpression condition;
    
    public SQLJoin(final JoinType type, final SQLTable tbl, final SQLTable joinedTbl, final BooleanExpression condition) {
        if (type != JoinType.NON_ANSI_JOIN && type != JoinType.INNER_JOIN && type != JoinType.LEFT_OUTER_JOIN && type != JoinType.RIGHT_OUTER_JOIN && type != JoinType.CROSS_JOIN) {
            throw new NucleusException("Unsupported join type specified : " + type);
        }
        if (tbl == null) {
            throw new NucleusException("Specification of join must supply the table reference");
        }
        this.type = type;
        this.table = tbl;
        this.joinedTable = joinedTbl;
        this.condition = condition;
    }
    
    public JoinType getType() {
        return this.type;
    }
    
    public void setType(final JoinType type) {
        this.type = type;
    }
    
    public SQLTable getTable() {
        return this.table;
    }
    
    public SQLTable getJoinedTable() {
        return this.joinedTable;
    }
    
    public BooleanExpression getCondition() {
        return this.condition;
    }
    
    public void addAndCondition(final BooleanExpression expr) {
        this.condition = this.condition.and(expr);
    }
    
    @Override
    public String toString() {
        if (this.type == JoinType.CROSS_JOIN) {
            return "JoinType: CROSSJOIN " + this.type + " tbl=" + this.table;
        }
        if (this.type == JoinType.INNER_JOIN || this.type == JoinType.LEFT_OUTER_JOIN) {
            return "JoinType: " + ((this.type == JoinType.INNER_JOIN) ? "INNERJOIN" : "OUTERJOIN") + " tbl=" + this.table + " joinedTbl=" + this.joinedTable;
        }
        return super.toString();
    }
    
    public String toFromClause(final DatastoreAdapter dba, final boolean lock) {
        if (this.type != JoinType.NON_ANSI_JOIN) {
            final StringBuffer result = new StringBuffer();
            if (this.type == JoinType.INNER_JOIN) {
                result.append("INNER JOIN ");
            }
            else if (this.type == JoinType.LEFT_OUTER_JOIN) {
                result.append("LEFT OUTER JOIN ");
            }
            else if (this.type == JoinType.RIGHT_OUTER_JOIN) {
                result.append("RIGHT OUTER JOIN ");
            }
            else if (this.type == JoinType.CROSS_JOIN) {
                result.append("CROSS JOIN ");
            }
            result.append(this.table);
            if (this.type == JoinType.INNER_JOIN || this.type == JoinType.LEFT_OUTER_JOIN || this.type == JoinType.RIGHT_OUTER_JOIN) {
                result.append(" ON ");
                if (this.condition != null) {
                    result.append(this.condition.toSQLText().toSQL());
                }
            }
            if (lock && dba.supportsOption("LockOptionWithinJoinClause")) {
                result.append(" WITH ").append(dba.getSelectWithLockOption());
            }
            return result.toString();
        }
        return "" + this.table;
    }
    
    public enum JoinType
    {
        NON_ANSI_JOIN, 
        INNER_JOIN, 
        LEFT_OUTER_JOIN, 
        RIGHT_OUTER_JOIN, 
        CROSS_JOIN;
    }
}
