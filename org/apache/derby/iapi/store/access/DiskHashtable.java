// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.DataValueDescriptor;

public class DiskHashtable
{
    private final long rowConglomerateId;
    private ConglomerateController rowConglomerate;
    private final long btreeConglomerateId;
    private ConglomerateController btreeConglomerate;
    private final DataValueDescriptor[] btreeRow;
    private final int[] key_column_numbers;
    private final boolean remove_duplicates;
    private final TransactionController tc;
    private final DataValueDescriptor[] row;
    private final DataValueDescriptor[] scanKey;
    private int size;
    private boolean keepStatistics;
    private final boolean keepAfterCommit;
    
    public DiskHashtable(final TransactionController tc, final DataValueDescriptor[] array, final int[] array2, final int[] key_column_numbers, final boolean remove_duplicates, final boolean keepAfterCommit) throws StandardException {
        this.scanKey = new DataValueDescriptor[] { new SQLInteger() };
        this.tc = tc;
        this.key_column_numbers = key_column_numbers;
        this.remove_duplicates = remove_duplicates;
        this.keepAfterCommit = keepAfterCommit;
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        this.keepStatistics = (languageConnectionContext != null && languageConnectionContext.getRunTimeStatisticsMode());
        this.row = new DataValueDescriptor[array.length];
        for (int i = 0; i < this.row.length; ++i) {
            this.row[i] = array[i].getNewNull();
        }
        final int n = keepAfterCommit ? 3 : 1;
        this.rowConglomerateId = tc.createConglomerate("heap", array, null, array2, null, n);
        this.rowConglomerate = tc.openConglomerate(this.rowConglomerateId, keepAfterCommit, 4, 7, 0);
        this.btreeRow = new DataValueDescriptor[] { new SQLInteger(), this.rowConglomerate.newRowLocationTemplate() };
        final Properties properties = new Properties();
        properties.put("baseConglomerateId", String.valueOf(this.rowConglomerateId));
        properties.put("rowLocationColumn", "1");
        properties.put("allowDuplicates", "false");
        properties.put("nKeyFields", "2");
        properties.put("nUniqueColumns", "2");
        properties.put("maintainParentLinks", "false");
        this.btreeConglomerateId = tc.createConglomerate("BTREE", this.btreeRow, null, new int[] { 0, 0 }, properties, n);
        this.btreeConglomerate = tc.openConglomerate(this.btreeConglomerateId, keepAfterCommit, 4, 7, 0);
    }
    
    public void close() throws StandardException {
        this.btreeConglomerate.close();
        this.rowConglomerate.close();
        this.tc.dropConglomerate(this.btreeConglomerateId);
        this.tc.dropConglomerate(this.rowConglomerateId);
    }
    
    public boolean put(final Object o, final Object[] array) throws StandardException {
        boolean b = false;
        if (this.remove_duplicates || this.keepStatistics) {
            b = (this.getRemove(o, false, true) != null);
            if (this.remove_duplicates && b) {
                return false;
            }
        }
        this.rowConglomerate.insertAndFetchLocation((DataValueDescriptor[])array, (RowLocation)this.btreeRow[1]);
        this.btreeRow[0].setValue(o.hashCode());
        this.btreeConglomerate.insert(this.btreeRow);
        if (this.keepStatistics && !b) {
            ++this.size;
        }
        return true;
    }
    
    public Object get(final Object o) throws StandardException {
        return this.getRemove(o, false, false);
    }
    
    private Object getRemove(final Object o, final boolean b, final boolean b2) throws StandardException {
        final int hashCode = o.hashCode();
        int n = 0;
        Object shallowCloneRow = null;
        this.scanKey[0].setValue(hashCode);
        final ScanController openScan = this.tc.openScan(this.btreeConglomerateId, false, b ? 4 : 0, 7, 1, null, this.scanKey, 1, null, this.scanKey, -1);
        try {
            while (openScan.fetchNext(this.btreeRow)) {
                if (this.rowConglomerate.fetch((RowLocation)this.btreeRow[1], this.row, null) && this.rowMatches(this.row, o)) {
                    if (b2) {
                        return this;
                    }
                    if (++n == 1) {
                        shallowCloneRow = BackingStoreHashtable.shallowCloneRow(this.row);
                    }
                    else {
                        Object o2;
                        if (n == 2) {
                            o2 = new Vector<DataValueDescriptor[]>(2);
                            ((Vector<DataValueDescriptor[]>)o2).add((DataValueDescriptor[])shallowCloneRow);
                            shallowCloneRow = o2;
                        }
                        else {
                            o2 = shallowCloneRow;
                        }
                        ((Vector<DataValueDescriptor[]>)o2).add(BackingStoreHashtable.shallowCloneRow(this.row));
                    }
                    if (b) {
                        this.rowConglomerate.delete((RowLocation)this.btreeRow[1]);
                        openScan.delete();
                        --this.size;
                    }
                    if (this.remove_duplicates) {
                        return shallowCloneRow;
                    }
                    continue;
                }
            }
        }
        finally {
            openScan.close();
        }
        return shallowCloneRow;
    }
    
    private boolean rowMatches(final DataValueDescriptor[] array, final Object obj) {
        if (this.key_column_numbers.length == 1) {
            return array[this.key_column_numbers[0]].equals(obj);
        }
        final KeyHasher keyHasher = (KeyHasher)obj;
        for (int i = 0; i < this.key_column_numbers.length; ++i) {
            if (!array[this.key_column_numbers[i]].equals(keyHasher.getObject(i))) {
                return false;
            }
        }
        return true;
    }
    
    public Object remove(final Object o) throws StandardException {
        return this.getRemove(o, true, false);
    }
    
    public int size() {
        return this.size;
    }
    
    public Enumeration elements() throws StandardException {
        return new ElementEnum();
    }
    
    private class ElementEnum implements Enumeration
    {
        private ScanController scan;
        private boolean hasMore;
        private RowLocation rowloc;
        
        ElementEnum() {
            try {
                this.scan = DiskHashtable.this.tc.openScan(DiskHashtable.this.rowConglomerateId, DiskHashtable.this.keepAfterCommit, 0, 7, 0, null, null, 0, null, null, 0);
                if (!(this.hasMore = this.scan.next())) {
                    this.scan.close();
                    this.scan = null;
                }
                else if (DiskHashtable.this.keepAfterCommit) {
                    this.rowloc = DiskHashtable.this.rowConglomerate.newRowLocationTemplate();
                    this.scan.fetchLocation(this.rowloc);
                }
            }
            catch (StandardException ex) {
                this.hasMore = false;
                if (this.scan != null) {
                    try {
                        this.scan.close();
                    }
                    catch (StandardException ex2) {}
                    this.scan = null;
                }
            }
        }
        
        public boolean hasMoreElements() {
            return this.hasMore;
        }
        
        public Object nextElement() {
            if (!this.hasMore) {
                throw new NoSuchElementException();
            }
            try {
                if (this.scan.isHeldAfterCommit() && !this.scan.positionAtRowLocation(this.rowloc)) {
                    throw StandardException.newException("24000");
                }
                this.scan.fetch(DiskHashtable.this.row);
                final DataValueDescriptor[] shallowCloneRow = BackingStoreHashtable.shallowCloneRow(DiskHashtable.this.row);
                if (!(this.hasMore = this.scan.next())) {
                    this.scan.close();
                    this.scan = null;
                }
                else if (DiskHashtable.this.keepAfterCommit) {
                    this.scan.fetchLocation(this.rowloc);
                }
                return shallowCloneRow;
            }
            catch (StandardException ex) {
                if (this.scan != null) {
                    try {
                        this.scan.close();
                    }
                    catch (StandardException ex2) {}
                    this.scan = null;
                }
                throw new NoSuchElementException();
            }
        }
    }
}
