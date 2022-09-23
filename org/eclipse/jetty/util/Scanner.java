// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class Scanner extends AbstractLifeCycle
{
    private static final Logger LOG;
    private static int __scannerId;
    private int _scanInterval;
    private int _scanCount;
    private final List<Listener> _listeners;
    private final Map<String, TimeNSize> _prevScan;
    private final Map<String, TimeNSize> _currentScan;
    private FilenameFilter _filter;
    private final List<File> _scanDirs;
    private volatile boolean _running;
    private boolean _reportExisting;
    private boolean _reportDirs;
    private Timer _timer;
    private TimerTask _task;
    private int _scanDepth;
    private final Map<String, Notification> _notifications;
    
    public Scanner() {
        this._scanCount = 0;
        this._listeners = new ArrayList<Listener>();
        this._prevScan = new HashMap<String, TimeNSize>();
        this._currentScan = new HashMap<String, TimeNSize>();
        this._scanDirs = new ArrayList<File>();
        this._running = false;
        this._reportExisting = true;
        this._reportDirs = true;
        this._scanDepth = 0;
        this._notifications = new HashMap<String, Notification>();
    }
    
    public synchronized int getScanInterval() {
        return this._scanInterval;
    }
    
    public synchronized void setScanInterval(final int scanInterval) {
        this._scanInterval = scanInterval;
        this.schedule();
    }
    
    public void setScanDirs(final List<File> dirs) {
        this._scanDirs.clear();
        this._scanDirs.addAll(dirs);
    }
    
    public synchronized void addScanDir(final File dir) {
        this._scanDirs.add(dir);
    }
    
    public List<File> getScanDirs() {
        return Collections.unmodifiableList((List<? extends File>)this._scanDirs);
    }
    
    public void setRecursive(final boolean recursive) {
        this._scanDepth = (recursive ? -1 : 0);
    }
    
    public boolean getRecursive() {
        return this._scanDepth == -1;
    }
    
    public int getScanDepth() {
        return this._scanDepth;
    }
    
    public void setScanDepth(final int scanDepth) {
        this._scanDepth = scanDepth;
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
    
    public boolean getReportExistingFilesOnStartup() {
        return this._reportExisting;
    }
    
    public void setReportDirs(final boolean dirs) {
        this._reportDirs = dirs;
    }
    
    public boolean getReportDirs() {
        return this._reportDirs;
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
    
    public synchronized void doStart() {
        if (this._running) {
            return;
        }
        this._running = true;
        if (this._reportExisting) {
            this.scan();
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
            @Override
            public void run() {
                Scanner.this.scan();
            }
        };
    }
    
    public Timer newTimer() {
        return new Timer("Scanner-" + Scanner.__scannerId++, true);
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
                this._timer.schedule(this._task, 1010L * this.getScanInterval(), 1010L * this.getScanInterval());
            }
        }
    }
    
    public synchronized void doStop() {
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
    
    public boolean exists(final String path) {
        for (final File dir : this._scanDirs) {
            if (new File(dir, path).exists()) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized void scan() {
        this.reportScanStart(++this._scanCount);
        this.scanFiles();
        this.reportDifferences(this._currentScan, this._prevScan);
        this._prevScan.clear();
        this._prevScan.putAll(this._currentScan);
        this.reportScanEnd(this._scanCount);
        for (final Listener l : this._listeners) {
            try {
                if (!(l instanceof ScanListener)) {
                    continue;
                }
                ((ScanListener)l).scan();
            }
            catch (Exception e) {
                Scanner.LOG.warn(e);
            }
            catch (Error e2) {
                Scanner.LOG.warn(e2);
            }
        }
    }
    
    public synchronized void scanFiles() {
        if (this._scanDirs == null) {
            return;
        }
        this._currentScan.clear();
        for (final File dir : this._scanDirs) {
            if (dir != null && dir.exists()) {
                try {
                    this.scanFile(dir.getCanonicalFile(), this._currentScan, 0);
                }
                catch (IOException e) {
                    Scanner.LOG.warn("Error scanning files.", e);
                }
            }
        }
    }
    
    public synchronized void reportDifferences(final Map<String, TimeNSize> currentScan, final Map<String, TimeNSize> oldScan) {
        final Set<String> oldScanKeys = new HashSet<String>(oldScan.keySet());
        for (final Map.Entry<String, TimeNSize> entry : currentScan.entrySet()) {
            final String file = entry.getKey();
            if (!oldScanKeys.contains(file)) {
                final Notification old = this._notifications.put(file, Notification.ADDED);
                if (old == null) {
                    continue;
                }
                switch (old) {
                    case REMOVED:
                    case CHANGED: {
                        this._notifications.put(file, Notification.CHANGED);
                        continue;
                    }
                }
            }
            else {
                if (oldScan.get(file).equals(currentScan.get(file))) {
                    continue;
                }
                final Notification old = this._notifications.put(file, Notification.CHANGED);
                if (old == null) {
                    continue;
                }
                switch (old) {
                    case ADDED: {
                        this._notifications.put(file, Notification.ADDED);
                        continue;
                    }
                }
            }
        }
        for (final String file2 : oldScan.keySet()) {
            if (!currentScan.containsKey(file2)) {
                final Notification old2 = this._notifications.put(file2, Notification.REMOVED);
                if (old2 == null) {
                    continue;
                }
                switch (old2) {
                    case ADDED: {
                        this._notifications.remove(file2);
                        continue;
                    }
                }
            }
        }
        if (Scanner.LOG.isDebugEnabled()) {
            Scanner.LOG.debug("scanned " + this._scanDirs + ": " + this._notifications, new Object[0]);
        }
        final List<String> bulkChanges = new ArrayList<String>();
        final Iterator<Map.Entry<String, Notification>> iter = this._notifications.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, Notification> entry2 = iter.next();
            final String file3 = entry2.getKey();
            if (oldScan.containsKey(file3)) {
                if (!oldScan.get(file3).equals(currentScan.get(file3))) {
                    continue;
                }
            }
            else if (currentScan.containsKey(file3)) {
                continue;
            }
            final Notification notification = entry2.getValue();
            iter.remove();
            bulkChanges.add(file3);
            switch (notification) {
                case ADDED: {
                    this.reportAddition(file3);
                    continue;
                }
                case CHANGED: {
                    this.reportChange(file3);
                    continue;
                }
                case REMOVED: {
                    this.reportRemoval(file3);
                    continue;
                }
            }
        }
        if (!bulkChanges.isEmpty()) {
            this.reportBulkChanges(bulkChanges);
        }
    }
    
    private void scanFile(final File f, final Map<String, TimeNSize> scanInfoMap, final int depth) {
        try {
            if (!f.exists()) {
                return;
            }
            if (f.isFile() || (depth > 0 && this._reportDirs && f.isDirectory())) {
                if (this._filter == null || (this._filter != null && this._filter.accept(f.getParentFile(), f.getName()))) {
                    if (Scanner.LOG.isDebugEnabled()) {
                        Scanner.LOG.debug("scan accepted {}", f);
                    }
                    final String name = f.getCanonicalPath();
                    scanInfoMap.put(name, new TimeNSize(f.lastModified(), f.isDirectory() ? 0L : f.length()));
                }
                else if (Scanner.LOG.isDebugEnabled()) {
                    Scanner.LOG.debug("scan rejected {}", f);
                }
            }
            if (f.isDirectory() && (depth < this._scanDepth || this._scanDepth == -1 || this._scanDirs.contains(f))) {
                final File[] files = f.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; ++i) {
                        this.scanFile(files[i], scanInfoMap, depth + 1);
                    }
                }
                else {
                    Scanner.LOG.warn("Error listing files in directory {}", f);
                }
            }
        }
        catch (IOException e) {
            Scanner.LOG.warn("Error scanning watched files", e);
        }
    }
    
    private void warn(final Object listener, final String filename, final Throwable th) {
        Scanner.LOG.warn(listener + " failed on '" + filename, th);
    }
    
    private void reportAddition(final String filename) {
        for (final Listener l : this._listeners) {
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
        for (final Listener l : this._listeners) {
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
    
    private void reportBulkChanges(final List<String> filenames) {
        for (final Listener l : this._listeners) {
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
    
    private void reportScanStart(final int cycle) {
        for (final Listener listener : this._listeners) {
            try {
                if (!(listener instanceof ScanCycleListener)) {
                    continue;
                }
                ((ScanCycleListener)listener).scanStarted(cycle);
            }
            catch (Exception e) {
                Scanner.LOG.warn(listener + " failed on scan start for cycle " + cycle, e);
            }
        }
    }
    
    private void reportScanEnd(final int cycle) {
        for (final Listener listener : this._listeners) {
            try {
                if (!(listener instanceof ScanCycleListener)) {
                    continue;
                }
                ((ScanCycleListener)listener).scanEnded(cycle);
            }
            catch (Exception e) {
                Scanner.LOG.warn(listener + " failed on scan end for cycle " + cycle, e);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(Scanner.class);
        Scanner.__scannerId = 0;
    }
    
    public enum Notification
    {
        ADDED, 
        CHANGED, 
        REMOVED;
    }
    
    static class TimeNSize
    {
        final long _lastModified;
        final long _size;
        
        public TimeNSize(final long lastModified, final long size) {
            this._lastModified = lastModified;
            this._size = size;
        }
        
        @Override
        public int hashCode() {
            return (int)this._lastModified ^ (int)this._size;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof TimeNSize) {
                final TimeNSize tns = (TimeNSize)o;
                return tns._lastModified == this._lastModified && tns._size == this._size;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "[lm=" + this._lastModified + ",s=" + this._size + "]";
        }
    }
    
    public interface ScanCycleListener extends Listener
    {
        void scanStarted(final int p0) throws Exception;
        
        void scanEnded(final int p0) throws Exception;
    }
    
    public interface Listener
    {
    }
    
    public interface BulkListener extends Listener
    {
        void filesChanged(final List<String> p0) throws Exception;
    }
    
    public interface DiscreteListener extends Listener
    {
        void fileChanged(final String p0) throws Exception;
        
        void fileAdded(final String p0) throws Exception;
        
        void fileRemoved(final String p0) throws Exception;
    }
    
    public interface ScanListener extends Listener
    {
        void scan();
    }
}
