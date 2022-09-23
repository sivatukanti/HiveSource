// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.state;

public enum ConnectionState
{
    CONNECTED {
        @Override
        public boolean isConnected() {
            return true;
        }
    }, 
    SUSPENDED {
        @Override
        public boolean isConnected() {
            return false;
        }
    }, 
    RECONNECTED {
        @Override
        public boolean isConnected() {
            return true;
        }
    }, 
    LOST {
        @Override
        public boolean isConnected() {
            return false;
        }
    }, 
    READ_ONLY {
        @Override
        public boolean isConnected() {
            return true;
        }
    };
    
    public abstract boolean isConnected();
}
