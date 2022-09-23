// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.io.Closeable;

public interface Connection extends Closeable
{
    void addListener(final Listener p0);
    
    void removeListener(final Listener p0);
    
    void onOpen();
    
    void onClose();
    
    EndPoint getEndPoint();
    
    void close();
    
    boolean onIdleExpired();
    
    int getMessagesIn();
    
    int getMessagesOut();
    
    long getBytesIn();
    
    long getBytesOut();
    
    long getCreatedTimeStamp();
    
    public interface Listener
    {
        void onOpened(final Connection p0);
        
        void onClosed(final Connection p0);
        
        public static class Adapter implements Listener
        {
            @Override
            public void onOpened(final Connection connection) {
            }
            
            @Override
            public void onClosed(final Connection connection) {
            }
        }
    }
    
    public interface UpgradeTo extends Connection
    {
        void onUpgradeTo(final ByteBuffer p0);
    }
    
    public interface UpgradeFrom extends Connection
    {
        ByteBuffer onUpgradeFrom();
    }
}
