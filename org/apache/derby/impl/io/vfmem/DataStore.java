// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import org.apache.derby.io.StorageFile;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class DataStore
{
    private static final char SEP;
    private static final String[] EMPTY_STR_ARR;
    private final Object LOCK;
    private final Object TMP_COUNTER_LOCK;
    private final Map files;
    private final String databaseName;
    private long tmpFileCounter;
    private boolean deleteMe;
    
    public DataStore(final String databaseName) {
        this.LOCK = new Object();
        this.TMP_COUNTER_LOCK = new Object();
        this.files = new HashMap(80);
        this.tmpFileCounter = 0L;
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public boolean scheduledForDeletion() {
        return this.deleteMe;
    }
    
    public DataStoreEntry createEntry(final String pathname, final boolean b) {
        final String path = new File(pathname).getPath();
        synchronized (this.LOCK) {
            if (this.files.containsKey(path)) {
                return null;
            }
            final String[] parentList = this.getParentList(path);
            for (int i = parentList.length - 1; i >= 0; --i) {
                final DataStoreEntry dataStoreEntry = this.files.get(parentList[i]);
                if (dataStoreEntry == null) {
                    return null;
                }
                if (!dataStoreEntry.isDirectory()) {
                    return null;
                }
            }
            final DataStoreEntry dataStoreEntry2 = new DataStoreEntry(path, b);
            this.files.put(path, dataStoreEntry2);
            return dataStoreEntry2;
        }
    }
    
    public boolean createAllParents(final String pathname) {
        final String[] parentList = this.getParentList(new File(pathname).getPath());
        synchronized (this.LOCK) {
            for (int i = parentList.length - 1; i >= 0; --i) {
                final String s = parentList[i];
                final DataStoreEntry dataStoreEntry = this.files.get(s);
                if (dataStoreEntry == null) {
                    this.createEntry(s, true);
                }
                else if (!dataStoreEntry.isDirectory()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean deleteEntry(final String pathname) {
        final String path = new File(pathname).getPath();
        final DataStoreEntry dataStoreEntry;
        synchronized (this.LOCK) {
            dataStoreEntry = this.files.remove(path);
            if (dataStoreEntry != null) {
                if (dataStoreEntry.isDirectory()) {
                    if (this.listChildren(path).length > 0) {
                        this.files.put(path, dataStoreEntry);
                        return false;
                    }
                    if (path.equals(this.databaseName) && this.files.get(this.databaseName) == null) {
                        this.deleteMe = true;
                    }
                }
                dataStoreEntry.release();
            }
        }
        return dataStoreEntry != null;
    }
    
    public DataStoreEntry getEntry(final String pathname) {
        synchronized (this.LOCK) {
            return this.files.get(new File(pathname).getPath());
        }
    }
    
    public boolean deleteAll(final String pathname) {
        final String path = new File(pathname).getPath();
        synchronized (this.LOCK) {
            final DataStoreEntry dataStoreEntry = this.files.remove(path);
            if (dataStoreEntry == null) {
                return false;
            }
            if (dataStoreEntry.isDirectory()) {
                final boolean deleteAll = this._deleteAll(path);
                if (this.files.get(this.databaseName) == null) {
                    this.deleteMe = true;
                }
                return deleteAll;
            }
            dataStoreEntry.release();
            return true;
        }
    }
    
    public String[] listChildren(final String pathname) {
        if (pathname.equals("")) {
            throw new IllegalArgumentException("The empty string is not a valid path");
        }
        String s = new File(pathname).getPath();
        if (s.charAt(s.length() - 1) != DataStore.SEP) {
            s += DataStore.SEP;
        }
        final ArrayList<String> list = new ArrayList<String>();
        synchronized (this.LOCK) {
            for (final String s2 : this.files.keySet()) {
                if (s2.startsWith(s)) {
                    list.add(s2.substring(s.length()));
                }
            }
        }
        return list.toArray(DataStore.EMPTY_STR_ARR);
    }
    
    public boolean move(final StorageFile storageFile, final StorageFile storageFile2) {
        final String path = new File(storageFile.getPath()).getPath();
        final String path2 = new File(storageFile2.getPath()).getPath();
        synchronized (this.LOCK) {
            if (this.files.containsKey(path2)) {
                return false;
            }
            final DataStoreEntry dataStoreEntry = this.files.remove(path);
            if (dataStoreEntry == null) {
                return false;
            }
            this.files.put(path2, dataStoreEntry);
            return true;
        }
    }
    
    public void purge() {
        synchronized (this.LOCK) {
            final Iterator<DataStoreEntry> iterator = this.files.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().release();
            }
            this.files.clear();
        }
    }
    
    private boolean _deleteAll(String string) {
        if (string.charAt(string.length() - 1) != DataStore.SEP) {
            string += DataStore.SEP;
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (final String e : this.files.keySet()) {
            if (e.startsWith(string)) {
                list.add(e);
            }
        }
        final Iterator<String> iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            ((DataStoreEntry)this.files.remove(iterator2.next())).release();
        }
        return true;
    }
    
    public long getTempFileCounter() {
        synchronized (this.TMP_COUNTER_LOCK) {
            return ++this.tmpFileCounter;
        }
    }
    
    private String[] getParentList(final String s) {
        final ArrayList list = new ArrayList<String>();
        String parent = s;
        while ((parent = new File(parent).getParent()) != null) {
            list.add(parent);
        }
        return list.toArray(new String[list.size()]);
    }
    
    static {
        SEP = PathUtil.SEP;
        EMPTY_STR_ARR = new String[0];
    }
}
