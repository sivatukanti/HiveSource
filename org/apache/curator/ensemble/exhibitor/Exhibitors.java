// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.ensemble.exhibitor;

import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.Collection;

public class Exhibitors
{
    private final Collection<String> hostnames;
    private final int restPort;
    private final BackupConnectionStringProvider backupConnectionStringProvider;
    
    public Exhibitors(final Collection<String> hostnames, final int restPort, final BackupConnectionStringProvider backupConnectionStringProvider) {
        this.backupConnectionStringProvider = Preconditions.checkNotNull(backupConnectionStringProvider, (Object)"backupConnectionStringProvider cannot be null");
        this.hostnames = (Collection<String>)ImmutableList.copyOf((Collection<?>)hostnames);
        this.restPort = restPort;
    }
    
    public Collection<String> getHostnames() {
        return this.hostnames;
    }
    
    public int getRestPort() {
        return this.restPort;
    }
    
    public String getBackupConnectionString() throws Exception {
        return this.backupConnectionStringProvider.getBackupConnectionString();
    }
    
    public interface BackupConnectionStringProvider
    {
        String getBackupConnectionString() throws Exception;
    }
}
