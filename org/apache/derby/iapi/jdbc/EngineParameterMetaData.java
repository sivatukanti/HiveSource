// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;

public interface EngineParameterMetaData
{
    int getParameterCount();
    
    int isNullable(final int p0) throws SQLException;
    
    boolean isSigned(final int p0) throws SQLException;
    
    int getPrecision(final int p0) throws SQLException;
    
    int getScale(final int p0) throws SQLException;
    
    int getParameterType(final int p0) throws SQLException;
    
    String getParameterTypeName(final int p0) throws SQLException;
    
    String getParameterClassName(final int p0) throws SQLException;
    
    int getParameterMode(final int p0) throws SQLException;
}
