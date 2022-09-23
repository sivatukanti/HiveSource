// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class WatchedEvent
{
    private final Watcher.Event.KeeperState keeperState;
    private final Watcher.Event.EventType eventType;
    private String path;
    
    public WatchedEvent(final Watcher.Event.EventType eventType, final Watcher.Event.KeeperState keeperState, final String path) {
        this.keeperState = keeperState;
        this.eventType = eventType;
        this.path = path;
    }
    
    public WatchedEvent(final WatcherEvent eventMessage) {
        this.keeperState = Watcher.Event.KeeperState.fromInt(eventMessage.getState());
        this.eventType = Watcher.Event.EventType.fromInt(eventMessage.getType());
        this.path = eventMessage.getPath();
    }
    
    public Watcher.Event.KeeperState getState() {
        return this.keeperState;
    }
    
    public Watcher.Event.EventType getType() {
        return this.eventType;
    }
    
    public String getPath() {
        return this.path;
    }
    
    @Override
    public String toString() {
        return "WatchedEvent state:" + this.keeperState + " type:" + this.eventType + " path:" + this.path;
    }
    
    public WatcherEvent getWrapper() {
        return new WatcherEvent(this.eventType.getIntValue(), this.keeperState.getIntValue(), this.path);
    }
}
