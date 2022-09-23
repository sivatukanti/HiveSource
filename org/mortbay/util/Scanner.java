// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.IOException;
import java.util.Set;
import org.mortbay.log.Log;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.List;

public class Scanner
{
    private int _scanInterval;
    private List _listeners;
    private Map _prevScan;
    private Map _currentScan;
    private FilenameFilter _filter;
    private List _scanDirs;
    private volatile boolean _running;
    private boolean _reportExisting;
    private Timer _timer;
    private TimerTask _task;
    private boolean _recursive;
    
    public Scanner() {
        this._listeners = Collections.synchronizedList(new ArrayList<Object>());
        this._prevScan = new HashMap();
        this._currentScan = new HashMap();
        this._running = false;
        this._reportExisting = true;
        this._recursive = true;
    }
    
    public int getScanInterval() {
        return this._scanInterval;
    }
    
    public synchronized void setScanInterval(final int scanInterval) {
        this._scanInterval = scanInterval;
        this.schedule();
    }
    
    public void setScanDir(final File dir) {
        (this._scanDirs = new ArrayList()).add(dir);
    }
    
    public File getScanDir() {
        return (this._scanDirs == null) ? null : this._scanDirs.get(0);
    }
    
    public void setScanDirs(final List dirs) {
        this._scanDirs = dirs;
    }
    
    public List getScanDirs() {
        return this._scanDirs;
    }
    
    public void setRecursive(final boolean recursive) {
        this._recursive = recursive;
    }
    
    public boolean getRecursive() {
        return this._recursive;
    }
    
    public void setFilenameFilter(final FilenameFilter filter) {
        this._filter = filter;
    }
    
    public FilenameFilter getFilenameFilter() {
        return this._filter;
    }
    
    public void setReportExistingFilesOnStartup(final boolean reportExisting) {
        this._reportExisting = reportExisting;
    }
    
    public synchronized void addListener(final Listener listener) {
        if (listener == null) {
            return;
        }
        this._listeners.add(listener);
    }
    
    public synchronized void removeListener(final Listener listener) {
        if (listener == null) {
            return;
        }
        this._listeners.remove(listener);
    }
    
    public synchronized void start() {
        if (this._running) {
            return;
        }
        this._running = true;
        if (this._reportExisting) {
            this.scan();
        }
        else {
            this.scanFiles();
            this._prevScan.putAll(this._currentScan);
        }
        this.schedule();
    }
    
    public TimerTask newTimerTask() {
        return new TimerTask() {
            public void run() {
                Scanner.this.scan();
            }
        };
    }
    
    public Timer newTimer() {
        return new Timer(true);
    }
    
    public void schedule() {
        if (this._running) {
            if (this._timer != null) {
                this._timer.cancel();
            }
            if (this._task != null) {
                this._task.cancel();
            }
            if (this.getScanInterval() > 0) {
                this._timer = this.newTimer();
                this._task = this.newTimerTask();
                this._timer.schedule(this._task, 1000L * this.getScanInterval(), 1000L * this.getScanInterval());
            }
        }
    }
    
    public synchronized void stop() {
        if (this._running) {
            this._running = false;
            if (this._timer != null) {
                this._timer.cancel();
            }
            if (this._task != null) {
                this._task.cancel();
            }
            this._task = null;
            this._timer = null;
        }
    }
    
    public void scan() {
        this.scanFiles();
        this.reportDifferences(this._currentScan, this._prevScan);
        this._prevScan.clear();
        this._prevScan.putAll(this._currentScan);
    }
    
    public void scanFiles() {
        if (this._scanDirs == null) {
            return;
        }
        this._currentScan.clear();
        for (final File dir : this._scanDirs) {
            if (dir != null && dir.exists()) {
                this.scanFile(dir, this._currentScan);
            }
        }
    }
    
    public void reportDifferences(final Map currentScan, final Map oldScan) {
        final List bulkChanges = new ArrayList();
        final Set oldScanKeys = new HashSet(oldScan.keySet());
        for (final Map.Entry entry : currentScan.entrySet()) {
            if (!oldScanKeys.contains(entry.getKey())) {
                Log.debug("File added: " + entry.getKey());
                this.reportAddition(entry.getKey());
                bulkChanges.add(entry.getKey());
            }
            else if (!oldScan.get(entry.getKey()).equals(entry.getValue())) {
                Log.debug("File changed: " + entry.getKey());
                this.reportChange(entry.getKey());
                oldScanKeys.remove(entry.getKey());
                bulkChanges.add(entry.getKey());
            }
            else {
                oldScanKeys.remove(entry.getKey());
            }
        }
        if (!oldScanKeys.isEmpty()) {
            for (final String filename : oldScanKeys) {
                Log.debug("File removed: " + filename);
                this.reportRemoval(filename);
                bulkChanges.add(filename);
            }
        }
        if (!bulkChanges.isEmpty()) {
            this.reportBulkChanges(bulkChanges);
        }
    }
    
    private void scanFile(final File f, final Map scanInfoMap) {
        try {
            if (!f.exists()) {
                return;
            }
            if (f.isFile()) {
                if (this._filter == null || (this._filter != null && this._filter.accept(f.getParentFile(), f.getName()))) {
                    final String name = f.getCanonicalPath();
                    final long lastModified = f.lastModified();
                    scanInfoMap.put(name, new Long(lastModified));
                }
            }
            else if (f.isDirectory() && (this._recursive || this._scanDirs.contains(f))) {
                final File[] files = f.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    this.scanFile(files[i], scanInfoMap);
                }
            }
        }
        catch (IOException e) {
            Log.warn("Error scanning watched files", e);
        }
    }
    
    private void warn(final Object listener, final String filename, final Throwable th) {
        Log.warn(th);
        Log.warn(listener + " failed on '" + filename);
    }
    
    private void reportAddition(final String filename) {
        for (final Object l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) {
                    continue;
                }
                ((DiscreteListener)l).fileAdded(filename);
            }
            catch (Exception e) {
                this.warn(l, filename, e);
            }
            catch (Error e2) {
                this.warn(l, filename, e2);
            }
        }
    }
    
    private void reportRemoval(final String filename) {
        for (final Object l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) {
                    continue;
                }
                ((DiscreteListener)l).fileRemoved(filename);
            }
            catch (Exception e) {
                this.warn(l, filename, e);
            }
            catch (Error e2) {
                this.warn(l, filename, e2);
            }
        }
    }
    
    private void reportChange(final String filename) {
        for (final Object l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) {
                    continue;
                }
                ((DiscreteListener)l).fileChanged(filename);
            }
            catch (Exception e) {
                this.warn(l, filename, e);
            }
            catch (Error e2) {
                this.warn(l, filename, e2);
            }
        }
    }
    
    private void reportBulkChanges(final List filenames) {
        for (final Object l : this._listeners) {
            try {
                if (!(l instanceof BulkListener)) {
                    continue;
                }
                ((BulkListener)l).filesChanged(filenames);
            }
            catch (Exception e) {
                this.warn(l, filenames.toString(), e);
            }
            catch (Error e2) {
                this.warn(l, filenames.toString(), e2);
            }
        }
    }
    
    public interface BulkListener extends Listener
    {
        void filesChanged(final List p0) throws Exception;
    }
    
    public interface Listener
    {
    }
    
    public interface DiscreteListener extends Listener
    {
        void fileChanged(final String p0) throws Exception;
        
        void fileAdded(final String p0) throws Exception;
        
        void fileRemoved(final String p0) throws Exception;
    }
}
