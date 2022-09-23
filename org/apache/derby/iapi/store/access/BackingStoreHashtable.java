// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import java.util.NoSuchElementException;
import java.util.Iterator;
import org.apache.derby.iapi.util.PropertyUtil;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.derby.iapi.services.cache.ClassSize;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Properties;
import java.util.HashMap;

public class BackingStoreHashtable
{
    private TransactionController tc;
    private HashMap hash_table;
    private int[] key_column_numbers;
    private boolean remove_duplicates;
    private boolean skipNullKeyColumns;
    private Properties auxillary_runtimestats;
    private RowSource row_source;
    private long max_inmemory_rowcnt;
    private long inmemory_rowcnt;
    private long max_inmemory_size;
    private boolean keepAfterCommit;
    private static final int ARRAY_LIST_SIZE;
    private DiskHashtable diskHashtable;
    
    private BackingStoreHashtable() {
    }
    
    public BackingStoreHashtable(final TransactionController tc, final RowSource row_source, final int[] key_column_numbers, final boolean remove_duplicates, final long n, final long max_inmemory_rowcnt, final int n2, final float loadFactor, final boolean skipNullKeyColumns, final boolean keepAfterCommit) throws StandardException {
        this.key_column_numbers = key_column_numbers;
        this.remove_duplicates = remove_duplicates;
        this.row_source = row_source;
        this.skipNullKeyColumns = skipNullKeyColumns;
        this.max_inmemory_rowcnt = max_inmemory_rowcnt;
        if (max_inmemory_rowcnt > 0L) {
            this.max_inmemory_size = Long.MAX_VALUE;
        }
        else {
            this.max_inmemory_size = Runtime.getRuntime().totalMemory() / 100L;
        }
        this.tc = tc;
        this.keepAfterCommit = keepAfterCommit;
        if (n2 != -1) {
            this.hash_table = ((loadFactor == -1.0f) ? new HashMap(n2) : new HashMap(n2, loadFactor));
        }
        else {
            this.hash_table = ((n <= 0L || row_source == null) ? new HashMap() : ((n < this.max_inmemory_size) ? new HashMap((int)n) : null));
        }
        if (row_source != null) {
            final boolean needsToClone = row_source.needsToClone();
            DataValueDescriptor[] nextRowFromRowSource;
            while ((nextRowFromRowSource = this.getNextRowFromRowSource()) != null) {
                if (this.hash_table == null) {
                    this.hash_table = new HashMap((int)(this.max_inmemory_size / (double)this.getEstimatedMemUsage(nextRowFromRowSource)));
                }
                this.add_row_to_hash_table(nextRowFromRowSource, needsToClone);
            }
        }
        if (this.hash_table == null) {
            this.hash_table = new HashMap();
        }
    }
    
    private DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        DataValueDescriptor[] array = this.row_source.getNextRowFromRowSource();
        if (this.skipNullKeyColumns) {
            while (array != null) {
                int n;
                for (n = 0; n < this.key_column_numbers.length && !array[this.key_column_numbers[n]].isNull(); ++n) {}
                if (n == this.key_column_numbers.length) {
                    return array;
                }
                array = this.row_source.getNextRowFromRowSource();
            }
        }
        return array;
    }
    
    private static DataValueDescriptor[] cloneRow(final DataValueDescriptor[] array) throws StandardException {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                array2[i] = array[i].cloneValue(false);
            }
        }
        return array2;
    }
    
    static DataValueDescriptor[] shallowCloneRow(final DataValueDescriptor[] array) throws StandardException {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                array2[i] = array[i].cloneHolder();
            }
        }
        return array2;
    }
    
    private void add_row_to_hash_table(DataValueDescriptor[] cloneRow, final boolean b) throws StandardException {
        if (this.spillToDisk(cloneRow)) {
            return;
        }
        if (b) {
            cloneRow = cloneRow(cloneRow);
        }
        final Object buildHashKey = KeyHasher.buildHashKey(cloneRow, this.key_column_numbers);
        final DataValueDescriptor[] put;
        if ((put = this.hash_table.put(buildHashKey, cloneRow)) == null) {
            this.doSpaceAccounting(cloneRow, false);
        }
        else if (!this.remove_duplicates) {
            List<?> value;
            if (put instanceof List) {
                this.doSpaceAccounting(cloneRow, false);
                value = (List<?>)(Object)put;
            }
            else {
                value = new ArrayList<Object>(2);
                value.add(put);
                this.doSpaceAccounting(cloneRow, true);
            }
            value.add(cloneRow);
            this.hash_table.put(buildHashKey, value);
        }
    }
    
    private void doSpaceAccounting(final DataValueDescriptor[] array, final boolean b) {
        ++this.inmemory_rowcnt;
        if (this.max_inmemory_rowcnt <= 0L) {
            this.max_inmemory_size -= this.getEstimatedMemUsage(array);
            if (b) {
                this.max_inmemory_size -= BackingStoreHashtable.ARRAY_LIST_SIZE;
            }
        }
    }
    
    private boolean spillToDisk(final DataValueDescriptor[] array) throws StandardException {
        if (this.diskHashtable == null) {
            if (this.max_inmemory_rowcnt > 0L) {
                if (this.inmemory_rowcnt < this.max_inmemory_rowcnt) {
                    return false;
                }
            }
            else if (this.max_inmemory_size > this.getEstimatedMemUsage(array)) {
                return false;
            }
            this.diskHashtable = new DiskHashtable(this.tc, array, null, this.key_column_numbers, this.remove_duplicates, this.keepAfterCommit);
        }
        final Object buildHashKey = KeyHasher.buildHashKey(array, this.key_column_numbers);
        final Object value = this.hash_table.get(buildHashKey);
        if (value != null) {
            if (this.remove_duplicates) {
                return true;
            }
            if (value instanceof List) {
                final List<DataValueDescriptor[]> list = (List<DataValueDescriptor[]>)value;
                for (int i = list.size() - 1; i >= 0; --i) {
                    this.diskHashtable.put(buildHashKey, list.get(i));
                }
            }
            else {
                this.diskHashtable.put(buildHashKey, (Object[])value);
            }
            this.hash_table.remove(buildHashKey);
        }
        this.diskHashtable.put(buildHashKey, array);
        return true;
    }
    
    private long getEstimatedMemUsage(final DataValueDescriptor[] array) {
        long n = 0L;
        for (int i = 0; i < array.length; ++i) {
            n = n + array[i].estimateMemoryUsage() + ClassSize.refSize;
        }
        return n + ClassSize.refSize;
    }
    
    public void close() throws StandardException {
        this.hash_table = null;
        if (this.diskHashtable != null) {
            this.diskHashtable.close();
            this.diskHashtable = null;
        }
    }
    
    public Enumeration elements() throws StandardException {
        if (this.diskHashtable == null) {
            return Collections.enumeration(this.hash_table.values());
        }
        return new BackingStoreHashtableEnumeration();
    }
    
    public Object get(final Object key) throws StandardException {
        final Object value = this.hash_table.get(key);
        if (this.diskHashtable == null || value != null) {
            return value;
        }
        return this.diskHashtable.get(key);
    }
    
    public void getAllRuntimeStats(final Properties properties) throws StandardException {
        if (this.auxillary_runtimestats != null) {
            PropertyUtil.copyProperties(this.auxillary_runtimestats, properties);
        }
    }
    
    public Object remove(final Object key) throws StandardException {
        final Object remove = this.hash_table.remove(key);
        if (remove != null || this.diskHashtable == null) {
            return remove;
        }
        return this.diskHashtable.remove(key);
    }
    
    public void setAuxillaryRuntimeStats(final Properties auxillary_runtimestats) throws StandardException {
        this.auxillary_runtimestats = auxillary_runtimestats;
    }
    
    public boolean putRow(final boolean b, final DataValueDescriptor[] array) throws StandardException {
        if (this.skipNullKeyColumns) {
            for (int i = 0; i < this.key_column_numbers.length; ++i) {
                if (array[this.key_column_numbers[i]].isNull()) {
                    return false;
                }
            }
        }
        final Object buildHashKey = KeyHasher.buildHashKey(array, this.key_column_numbers);
        if (this.remove_duplicates && this.get(buildHashKey) != null) {
            return false;
        }
        this.add_row_to_hash_table(array, b);
        return true;
    }
    
    public int size() throws StandardException {
        if (this.diskHashtable == null) {
            return this.hash_table.size();
        }
        return this.hash_table.size() + this.diskHashtable.size();
    }
    
    static {
        ARRAY_LIST_SIZE = ClassSize.estimateBaseFromCatalog(ArrayList.class);
    }
    
    private class BackingStoreHashtableEnumeration implements Enumeration
    {
        private Iterator memoryIterator;
        private Enumeration diskEnumeration;
        
        BackingStoreHashtableEnumeration() {
            this.memoryIterator = BackingStoreHashtable.this.hash_table.values().iterator();
            if (BackingStoreHashtable.this.diskHashtable != null) {
                try {
                    this.diskEnumeration = BackingStoreHashtable.this.diskHashtable.elements();
                }
                catch (StandardException ex) {
                    this.diskEnumeration = null;
                }
            }
        }
        
        public boolean hasMoreElements() {
            if (this.memoryIterator != null) {
                if (this.memoryIterator.hasNext()) {
                    return true;
                }
                this.memoryIterator = null;
            }
            return this.diskEnumeration != null && this.diskEnumeration.hasMoreElements();
        }
        
        public Object nextElement() throws NoSuchElementException {
            if (this.memoryIterator != null) {
                if (this.memoryIterator.hasNext()) {
                    return this.memoryIterator.next();
                }
                this.memoryIterator = null;
            }
            return this.diskEnumeration.nextElement();
        }
    }
}
