// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.sql.ResultSet;

public interface NoPutResultSet extends ResultSet, RowLocationRetRowSource
{
    public static final String ABSOLUTE = "absolute";
    public static final String RELATIVE = "relative";
    public static final String FIRST = "first";
    public static final String NEXT = "next";
    public static final String LAST = "last";
    public static final String PREVIOUS = "previous";
    
    void markAsTopResultSet();
    
    void openCore() throws StandardException;
    
    void reopenCore() throws StandardException;
    
    ExecRow getNextRowCore() throws StandardException;
    
    int getPointOfAttachment();
    
    int getScanIsolationLevel();
    
    void setTargetResultSet(final TargetResultSet p0);
    
    void setNeedsRowLocation(final boolean p0);
    
    double getEstimatedRowCount();
    
    int resultSetNumber();
    
    void setCurrentRow(final ExecRow p0);
    
    boolean requiresRelocking();
    
    boolean isForUpdate();
    
    void updateRow(final ExecRow p0, final RowChanger p1) throws StandardException;
    
    void markRowAsDeleted() throws StandardException;
    
    void positionScanAtRowLocation(final RowLocation p0) throws StandardException;
}
