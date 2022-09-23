// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management;

public class FactoryStatistics extends AbstractStatistics implements FactoryStatisticsMBean
{
    int connectionActiveCurrent;
    int connectionActiveHigh;
    int connectionActiveTotal;
    
    public FactoryStatistics(final String name) {
        super(name);
    }
    
    @Override
    public int getConnectionActiveCurrent() {
        return this.connectionActiveCurrent;
    }
    
    @Override
    public int getConnectionActiveHigh() {
        return this.connectionActiveHigh;
    }
    
    @Override
    public int getConnectionActiveTotal() {
        return this.connectionActiveTotal;
    }
    
    public void incrementActiveConnections() {
        ++this.connectionActiveCurrent;
        ++this.connectionActiveTotal;
        this.connectionActiveHigh = Math.max(this.connectionActiveHigh, this.connectionActiveCurrent);
    }
    
    public void decrementActiveConnections() {
        --this.connectionActiveCurrent;
    }
}
