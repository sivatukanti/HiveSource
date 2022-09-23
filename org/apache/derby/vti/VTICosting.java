// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;

public interface VTICosting
{
    public static final double defaultEstimatedRowCount = 10000.0;
    public static final double defaultEstimatedCost = 100000.0;
    
    double getEstimatedRowCount(final VTIEnvironment p0) throws SQLException;
    
    double getEstimatedCostPerInstantiation(final VTIEnvironment p0) throws SQLException;
    
    boolean supportsMultipleInstantiations(final VTIEnvironment p0) throws SQLException;
}
