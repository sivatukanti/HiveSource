// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import java.util.Date;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.store.access.TransactionController;

public final class Timeout
{
    public static final int TABLE_AND_ROWLOCK = 2;
    public static final int ALL = -1;
    public static final String newline = "\n";
    private TransactionController tc;
    private TableNameInfo tabInfo;
    private Latch currentLock;
    private char[] outputRow;
    private StringBuffer sb;
    private Hashtable currentRow;
    private final long currentTime;
    private final Enumeration lockTable;
    private static final String[] column;
    private static final int LENGTHOFTABLE;
    private static final char LINE = '-';
    private static final char SEPARATOR = '|';
    
    private Timeout(final Latch currentLock, final Enumeration lockTable, final long currentTime) {
        this.currentLock = currentLock;
        this.lockTable = lockTable;
        this.currentTime = currentTime;
    }
    
    private StandardException createException() {
        try {
            this.buildLockTableString();
        }
        catch (StandardException ex) {
            return ex;
        }
        final StandardException exception = StandardException.newException("40XL1.T.1", this.sb.toString());
        exception.setReport(2);
        return exception;
    }
    
    private String buildLockTableString() throws StandardException {
        this.sb = new StringBuffer(8192);
        this.outputRow = new char[Timeout.LENGTHOFTABLE];
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext");
        if (languageConnectionContext != null) {
            this.tc = languageConnectionContext.getTransactionExecute();
        }
        try {
            this.tabInfo = new TableNameInfo(languageConnectionContext, true);
        }
        catch (Exception ex) {}
        this.sb.append("\n");
        this.sb.append(new Date(this.currentTime));
        this.sb.append("\n");
        for (int i = 0; i < Timeout.column.length; ++i) {
            this.sb.append(Timeout.column[i]);
            this.sb.append('|');
        }
        this.sb.append("\n");
        for (int j = 0; j < Timeout.LENGTHOFTABLE; ++j) {
            this.sb.append('-');
        }
        this.sb.append("\n");
        if (this.currentLock != null) {
            this.dumpLock();
            if (this.timeoutInfoHash()) {
                this.sb.append("*** The following row is the victim ***");
                this.sb.append("\n");
                this.sb.append(this.outputRow);
                this.sb.append("\n");
                this.sb.append("*** The above row is the victim ***");
                this.sb.append("\n");
            }
            else {
                this.sb.append("*** A victim was chosen, but it cannot be printed because the lockable object, " + this.currentLock + ", does not want to participate ***");
                this.sb.append("\n");
            }
        }
        if (this.lockTable != null) {
            while (this.lockTable.hasMoreElements()) {
                this.currentLock = this.lockTable.nextElement();
                this.dumpLock();
                if (this.timeoutInfoHash()) {
                    this.sb.append(this.outputRow);
                    this.sb.append("\n");
                }
                else {
                    this.sb.append("*** A latch/lock, " + this.currentLock + ", exist in the lockTable that cannot be printed ***");
                    this.sb.append("\n");
                }
            }
            for (int k = 0; k < Timeout.LENGTHOFTABLE; ++k) {
                this.sb.append('-');
            }
            this.sb.append("\n");
        }
        return this.sb.toString();
    }
    
    static StandardException buildException(final Latch latch, final Enumeration enumeration, final long n) {
        return new Timeout(latch, enumeration, n).createException();
    }
    
    public static String buildString(final Enumeration enumeration, final long n) throws StandardException {
        return new Timeout(null, enumeration, n).buildLockTableString();
    }
    
    private void dumpLock() throws StandardException {
        final Hashtable<String, Long> currentRow = new Hashtable<String, Long>(17);
        final Object qualifier = this.currentLock.getQualifier();
        if (!this.currentLock.getLockable().lockAttributes(-1, currentRow)) {
            this.currentRow = null;
            return;
        }
        Long n = currentRow.get("CONGLOMID");
        if (n == null && currentRow.get("CONTAINERID") != null && this.tc != null) {
            n = new Long(this.tc.findConglomid(currentRow.get("CONTAINERID")));
            currentRow.put("CONGLOMID", n);
        }
        if (currentRow.get("CONTAINERID") == null && n != null && this.tc != null) {
            try {
                currentRow.put("CONTAINERID", new Long(this.tc.findContainerid(n)));
            }
            catch (Exception ex) {}
        }
        currentRow.put("LOCKOBJ", (Long)this.currentLock);
        currentRow.put("XID", (Long)String.valueOf(this.currentLock.getCompatabilitySpace().getOwner()));
        currentRow.put("MODE", (Long)qualifier.toString());
        currentRow.put("LOCKCOUNT", (Long)Integer.toString(this.currentLock.getCount()));
        currentRow.put("STATE", (Long)((this.currentLock.getCount() != 0) ? "GRANT" : "WAIT"));
        if (this.tabInfo != null && n != null) {
            try {
                currentRow.put("TABLENAME", (Long)this.tabInfo.getTableName(n));
            }
            catch (NullPointerException ex2) {
                currentRow.put("TABLENAME", n);
            }
            try {
                final String indexName = this.tabInfo.getIndexName(n);
                if (indexName != null) {
                    currentRow.put("INDEXNAME", (Long)indexName);
                }
                else if (currentRow.get("TYPE").equals("LATCH")) {
                    currentRow.put("INDEXNAME", currentRow.get("MODE"));
                }
                else {
                    currentRow.put("INDEXNAME", (Long)"NULL");
                }
            }
            catch (Exception ex3) {
                if ("CONTAINERID" != null) {
                    currentRow.put("INDEXNAME", (Long)"CONTAINERID");
                }
                else {
                    currentRow.put("INDEXNAME", (Long)"NULL");
                }
            }
            currentRow.put("TABLETYPE", (Long)this.tabInfo.getTableType(n));
        }
        else {
            if (n != null) {
                currentRow.put("TABLENAME", (Long)"CONGLOMID");
            }
            else {
                currentRow.put("TABLENAME", (Long)"NULL");
            }
            if ("CONTAINERID" != null) {
                currentRow.put("INDEXNAME", (Long)"CONTAINERID");
            }
            else {
                currentRow.put("INDEXNAME", (Long)"NULL");
            }
            currentRow.put("TABLETYPE", (Long)this.currentLock.toString());
        }
        this.currentRow = currentRow;
    }
    
    private void cpArray(final String s, final int n, final int n2) {
        int i = 0;
        final int n3 = n2 - n;
        if (s != null) {
            while (i < s.length()) {
                if (n3 - i == 0) {
                    break;
                }
                this.outputRow[i + n] = s.charAt(i);
                ++i;
            }
        }
        while (i + n != n2) {
            this.outputRow[i + n] = ' ';
            ++i;
        }
        this.outputRow[n2] = '|';
    }
    
    private boolean timeoutInfoHash() {
        if (this.currentRow == null) {
            return false;
        }
        final String[] array = { "XID", "TYPE", "MODE", "LOCKCOUNT", "LOCKNAME", "STATE", "TABLETYPE", "INDEXNAME", "TABLENAME" };
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            this.cpArray(this.currentRow.get(array[i]).toString(), n, n + Timeout.column[i].length());
            n = n + Timeout.column[i].length() + 1;
        }
        return true;
    }
    
    static {
        (column = new String[9])[0] = "XID       ";
        Timeout.column[1] = "TYPE         ";
        Timeout.column[2] = "MODE";
        Timeout.column[3] = "LOCKCOUNT";
        Timeout.column[4] = "LOCKNAME                                                                        ";
        Timeout.column[5] = "STATE";
        Timeout.column[6] = "TABLETYPE / LOCKOBJ                   ";
        Timeout.column[7] = "INDEXNAME / CONTAINER_ID / (MODE for LATCH only)  ";
        Timeout.column[8] = "TABLENAME / CONGLOM_ID                ";
        int n = 0;
        for (int i = 0; i < Timeout.column.length; ++i) {
            n += Timeout.column[i].length();
        }
        LENGTHOFTABLE = n + Timeout.column.length;
    }
}
