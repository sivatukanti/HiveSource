// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.table.Table;

public class SQLTableAlphaNamer implements SQLTableNamer
{
    static String[] CHARS;
    
    @Override
    public String getAliasForTable(final SQLStatement stmt, final Table table, final String groupName) {
        final SQLTableGroup tableGrp = stmt.tableGroups.get(groupName);
        String groupLetters = null;
        int numTablesInGroup = 0;
        if (tableGrp == null || tableGrp.getNumberOfTables() == 0) {
            int number = stmt.tableGroups.size();
            groupLetters = this.getLettersForNumber(number);
            boolean nameClashes = true;
            while (nameClashes) {
                if (stmt.primaryTable != null && stmt.primaryTable.alias.getIdentifierName().equalsIgnoreCase(groupLetters)) {
                    ++number;
                    groupLetters = this.getLettersForNumber(number);
                }
                else if (stmt.tables == null) {
                    nameClashes = false;
                }
                else if (stmt.tables.containsKey(groupLetters)) {
                    ++number;
                    groupLetters = this.getLettersForNumber(number);
                }
                else if (stmt.tables.containsKey(groupLetters + "0")) {
                    ++number;
                    groupLetters = this.getLettersForNumber(number);
                }
                else {
                    nameClashes = false;
                }
            }
            numTablesInGroup = 0;
        }
        else {
            final SQLTable refSqlTbl = tableGrp.getTables()[0];
            final String baseTableAlias = refSqlTbl.getAlias().toString();
            final String quote = stmt.getRDBMSManager().getDatastoreAdapter().getIdentifierQuoteString();
            int lettersStartPoint = 0;
            if (baseTableAlias.startsWith(quote)) {
                lettersStartPoint = quote.length();
            }
            int lettersLength = 1;
            if (baseTableAlias.length() > lettersStartPoint + 1 && Character.isLetter(baseTableAlias.charAt(lettersStartPoint + 1))) {
                lettersLength = 2;
            }
            groupLetters = baseTableAlias.substring(lettersStartPoint, lettersStartPoint + lettersLength);
            numTablesInGroup = tableGrp.getNumberOfTables();
            for (int i = 0; i < stmt.getNumberOfUnions(); ++i) {
                final int num = stmt.unions.get(i).getTableGroup(tableGrp.getName()).getNumberOfTables();
                if (num > numTablesInGroup) {
                    numTablesInGroup = num;
                }
            }
        }
        if (stmt.parent == null) {
            return groupLetters + numTablesInGroup;
        }
        if (stmt.parent.parent == null) {
            return groupLetters + numTablesInGroup + "_SUB";
        }
        if (stmt.parent.parent.parent != null) {
            return groupLetters + numTablesInGroup + "_SUB_SUB_SUB";
        }
        return groupLetters + numTablesInGroup + "_SUB_SUB";
    }
    
    private String getLettersForNumber(final int number) {
        String groupLetters;
        if (number >= SQLTableAlphaNamer.CHARS.length) {
            groupLetters = SQLTableAlphaNamer.CHARS[number / 26] + SQLTableAlphaNamer.CHARS[number % 26];
        }
        else {
            groupLetters = SQLTableAlphaNamer.CHARS[number];
        }
        return groupLetters;
    }
    
    static {
        SQLTableAlphaNamer.CHARS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    }
}
