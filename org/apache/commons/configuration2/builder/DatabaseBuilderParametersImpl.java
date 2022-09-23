// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import javax.sql.DataSource;

public class DatabaseBuilderParametersImpl extends BasicBuilderParameters implements DatabaseBuilderProperties<DatabaseBuilderParametersImpl>
{
    private static final String PROP_DATA_SOURCE = "dataSource";
    private static final String PROP_TABLE = "table";
    private static final String PROP_KEY_COLUMN = "keyColumn";
    private static final String PROP_VALUE_COLUMN = "valueColumn";
    private static final String PROP_CONFIG_NAME_COLUMN = "configurationNameColumn";
    private static final String PROP_CONFIG_NAME = "configurationName";
    private static final String PROP_AUTO_COMMIT = "autoCommit";
    
    @Override
    public DatabaseBuilderParametersImpl setDataSource(final DataSource src) {
        this.storeProperty("dataSource", src);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setTable(final String tname) {
        this.storeProperty("table", tname);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setKeyColumn(final String name) {
        this.storeProperty("keyColumn", name);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setValueColumn(final String name) {
        this.storeProperty("valueColumn", name);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setConfigurationNameColumn(final String name) {
        this.storeProperty("configurationNameColumn", name);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setConfigurationName(final String name) {
        this.storeProperty("configurationName", name);
        return this;
    }
    
    @Override
    public DatabaseBuilderParametersImpl setAutoCommit(final boolean f) {
        this.storeProperty("autoCommit", f);
        return this;
    }
}
