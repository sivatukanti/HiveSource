// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public interface Watcher
{
    void process(final WatchedEvent p0);
    
    @InterfaceAudience.Public
    public interface Event
    {
        @InterfaceAudience.Public
        public enum KeeperState
        {
            @Deprecated
            Unknown(-1), 
            Disconnected(0), 
            @Deprecated
            NoSyncConnected(1), 
            SyncConnected(3), 
            AuthFailed(4), 
            ConnectedReadOnly(5), 
            SaslAuthenticated(6), 
            Expired(-112);
            
            private final int intValue;
            
            private KeeperState(final int intValue) {
                this.intValue = intValue;
            }
            
            public int getIntValue() {
                return this.intValue;
            }
            
            public static KeeperState fromInt(final int intValue) {
                switch (intValue) {
                    case -1: {
                        return KeeperState.Unknown;
                    }
                    case 0: {
                        return KeeperState.Disconnected;
                    }
                    case 1: {
                        return KeeperState.NoSyncConnected;
                    }
                    case 3: {
                        return KeeperState.SyncConnected;
                    }
                    case 4: {
                        return KeeperState.AuthFailed;
                    }
                    case 5: {
                        return KeeperState.ConnectedReadOnly;
                    }
                    case 6: {
                        return KeeperState.SaslAuthenticated;
                    }
                    case -112: {
                        return KeeperState.Expired;
                    }
                    default: {
                        throw new RuntimeException("Invalid integer value for conversion to KeeperState");
                    }
                }
            }
        }
        
        @InterfaceAudience.Public
        public enum EventType
        {
            None(-1), 
            NodeCreated(1), 
            NodeDeleted(2), 
            NodeDataChanged(3), 
            NodeChildrenChanged(4);
            
            private final int intValue;
            
            private EventType(final int intValue) {
                this.intValue = intValue;
            }
            
            public int getIntValue() {
                return this.intValue;
            }
            
            public static EventType fromInt(final int intValue) {
                switch (intValue) {
                    case -1: {
                        return EventType.None;
                    }
                    case 1: {
                        return EventType.NodeCreated;
                    }
                    case 2: {
                        return EventType.NodeDeleted;
                    }
                    case 3: {
                        return EventType.NodeDataChanged;
                    }
                    case 4: {
                        return EventType.NodeChildrenChanged;
                    }
                    default: {
                        throw new RuntimeException("Invalid integer value for conversion to EventType");
                    }
                }
            }
        }
    }
}
