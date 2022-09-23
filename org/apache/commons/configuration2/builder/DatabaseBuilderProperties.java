// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import javax.sql.DataSource;

public interface DatabaseBuilderProperties<T>
{
    T setDataSource(final DataSource p0);
    
    T setTable(final String p0);
    
    T setKeyColumn(final String p0);
    
    T setValueColumn(final String p0);
    
    T setConfigurationNameColumn(final String p0);
    
    T setConfigurationName(final String p0);
    
    T setAutoCommit(final boolean p0);
}
