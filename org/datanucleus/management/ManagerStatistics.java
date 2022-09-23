// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management;

public class ManagerStatistics extends AbstractStatistics implements ManagerStatisticsMBean
{
    public ManagerStatistics(final String name, final FactoryStatistics parent) {
        super(name);
        this.parent = parent;
    }
}
