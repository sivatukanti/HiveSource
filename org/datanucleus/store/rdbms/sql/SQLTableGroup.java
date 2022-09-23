// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class SQLTableGroup
{
    String name;
    SQLJoin.JoinType joinType;
    Map<String, SQLTable> tablesByAlias;
    
    SQLTableGroup(final String name, final SQLJoin.JoinType joinType) {
        this.joinType = null;
        this.tablesByAlias = new HashMap<String, SQLTable>();
        this.name = name;
        this.joinType = joinType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public SQLJoin.JoinType getJoinType() {
        return this.joinType;
    }
    
    public void addTable(final SQLTable tbl) {
        this.tablesByAlias.put(tbl.getAlias().toString(), tbl);
    }
    
    public int getNumberOfTables() {
        return this.tablesByAlias.size();
    }
    
    public SQLTable[] getTables() {
        return this.tablesByAlias.values().toArray(new SQLTable[this.tablesByAlias.size()]);
    }
    
    @Override
    public String toString() {
        return "SQLTableGroup: " + this.name + " join=" + this.joinType + " tables=" + StringUtils.mapToString(this.tablesByAlias);
    }
}
