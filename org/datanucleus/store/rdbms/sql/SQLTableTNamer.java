// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.table.Table;

public class SQLTableTNamer implements SQLTableNamer
{
    @Override
    public String getAliasForTable(final SQLStatement stmt, final Table table, final String groupName) {
        int number = 0;
        if (stmt.getPrimaryTable() != null) {
            int numTables = stmt.getNumberOfTables();
            for (int i = 0; i < stmt.getNumberOfUnions(); ++i) {
                final int num = stmt.unions.get(i).getNumberOfTables();
                if (num > numTables) {
                    numTables = num;
                }
            }
            number = ((numTables > 0) ? (numTables + 1) : 1);
        }
        if (stmt.parent == null) {
            return "T" + number;
        }
        if (stmt.parent.parent == null) {
            return "T" + number + "_SUB";
        }
        if (stmt.parent.parent.parent != null) {
            return "T" + number + "_SUB_SUB_SUB";
        }
        return "T" + number + "_SUB_SUB";
    }
}
