// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.util.Map;
import java.io.PrintWriter;
import org.apache.zookeeper.WatchedEvent;
import java.util.Set;
import java.util.Iterator;
import org.apache.zookeeper.Watcher;
import java.util.HashSet;
import java.util.HashMap;
import org.slf4j.Logger;

public class WatchManager
{
    private static final Logger LOG;
    private final HashMap<String, HashSet<Watcher>> watchTable;
    private final HashMap<Watcher, HashSet<String>> watch2Paths;
    
    public WatchManager() {
        this.watchTable = new HashMap<String, HashSet<Watcher>>();
        this.watch2Paths = new HashMap<Watcher, HashSet<String>>();
    }
    
    public synchronized int size() {
        int result = 0;
        for (final Set<Watcher> watches : this.watchTable.values()) {
            result += watches.size();
        }
        return result;
    }
    
    public synchronized void addWatch(final String path, final Watcher watcher) {
        HashSet<Watcher> list = this.watchTable.get(path);
        if (list == null) {
            list = new HashSet<Watcher>(4);
            this.watchTable.put(path, list);
        }
        list.add(watcher);
        HashSet<String> paths = this.watch2Paths.get(watcher);
        if (paths == null) {
            paths = new HashSet<String>();
            this.watch2Paths.put(watcher, paths);
        }
        paths.add(path);
    }
    
    public synchronized void removeWatcher(final Watcher watcher) {
        final HashSet<String> paths = this.watch2Paths.remove(watcher);
        if (paths == null) {
            return;
        }
        for (final String p : paths) {
            final HashSet<Watcher> list = this.watchTable.get(p);
            if (list != null) {
                list.remove(watcher);
                if (list.size() != 0) {
                    continue;
                }
                this.watchTable.remove(p);
            }
        }
    }
    
    public Set<Watcher> triggerWatch(final String path, final Watcher.Event.EventType type) {
        return this.triggerWatch(path, type, null);
    }
    
    public Set<Watcher> triggerWatch(final String path, final Watcher.Event.EventType type, final Set<Watcher> supress) {
        final WatchedEvent e = new WatchedEvent(type, Watcher.Event.KeeperState.SyncConnected, path);
        final HashSet<Watcher> watchers;
        synchronized (this) {
            watchers = this.watchTable.remove(path);
            if (watchers == null || watchers.isEmpty()) {
                if (WatchManager.LOG.isTraceEnabled()) {
                    ZooTrace.logTraceMessage(WatchManager.LOG, 64L, "No watchers for " + path);
                }
                return null;
            }
            for (final Watcher w : watchers) {
                final HashSet<String> paths = this.watch2Paths.get(w);
                if (paths != null) {
                    paths.remove(path);
                }
            }
        }
        for (final Watcher w2 : watchers) {
            if (supress != null && supress.contains(w2)) {
                continue;
            }
            w2.process(e);
        }
        return watchers;
    }
    
    @Override
    public synchronized String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.watch2Paths.size()).append(" connections watching ").append(this.watchTable.size()).append(" paths\n");
        int total = 0;
        for (final HashSet<String> paths : this.watch2Paths.values()) {
            total += paths.size();
        }
        sb.append("Total watches:").append(total);
        return sb.toString();
    }
    
    public synchronized void dumpWatches(final PrintWriter pwriter, final boolean byPath) {
        if (byPath) {
            for (final Map.Entry<String, HashSet<Watcher>> e : this.watchTable.entrySet()) {
                pwriter.println(e.getKey());
                for (final Watcher w : e.getValue()) {
                    pwriter.print("\t0x");
                    pwriter.print(Long.toHexString(((ServerCnxn)w).getSessionId()));
                    pwriter.print("\n");
                }
            }
        }
        else {
            for (final Map.Entry<Watcher, HashSet<String>> e2 : this.watch2Paths.entrySet()) {
                pwriter.print("0x");
                pwriter.println(Long.toHexString(e2.getKey().getSessionId()));
                for (final String path : e2.getValue()) {
                    pwriter.print("\t");
                    pwriter.println(path);
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(WatchManager.class);
    }
}
