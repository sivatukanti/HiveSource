// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.ensemble.fixed;

import java.io.IOException;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.ensemble.EnsembleProvider;

public class FixedEnsembleProvider implements EnsembleProvider
{
    private final String connectionString;
    
    public FixedEnsembleProvider(final String connectionString) {
        this.connectionString = Preconditions.checkNotNull(connectionString, (Object)"connectionString cannot be null");
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public String getConnectionString() {
        return this.connectionString;
    }
}
