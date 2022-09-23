// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.plugin.ConfigurationElement;
import java.lang.reflect.Constructor;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.plugin.PluginManager;
import java.sql.Connection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public class DatastoreAdapterFactory
{
    private static final Localiser LOCALISER;
    
    public static DatastoreAdapterFactory getInstance() {
        return new DatastoreAdapterFactory();
    }
    
    protected DatastoreAdapterFactory() {
    }
    
    public DatastoreAdapter getDatastoreAdapter(final ClassLoaderResolver clr, final Connection conn, final String adapterClassName, final PluginManager pluginMgr) throws SQLException {
        DatastoreAdapter adapter = null;
        final DatabaseMetaData metadata = conn.getMetaData();
        adapter = this.getNewDatastoreAdapter(clr, metadata, adapterClassName, pluginMgr);
        if (adapter == null) {
            NucleusLogger.DATASTORE.warn(DatastoreAdapterFactory.LOCALISER.msg("051000"));
            adapter = new BaseDatastoreAdapter(metadata);
        }
        return adapter;
    }
    
    protected DatastoreAdapter getNewDatastoreAdapter(final ClassLoaderResolver clr, final DatabaseMetaData metadata, final String adapterClassName, final PluginManager pluginMgr) {
        if (metadata == null) {
            return null;
        }
        String productName = null;
        if (adapterClassName == null) {
            try {
                productName = metadata.getDatabaseProductName();
                if (productName == null) {
                    NucleusLogger.DATASTORE.error(DatastoreAdapterFactory.LOCALISER.msg("051024"));
                    return null;
                }
            }
            catch (SQLException sqe) {
                NucleusLogger.DATASTORE.error(DatastoreAdapterFactory.LOCALISER.msg("051025", sqe));
                return null;
            }
        }
        Object adapter_obj;
        try {
            final Class adapterClass = this.getAdapterClass(pluginMgr, adapterClassName, productName, clr);
            if (adapterClass == null) {
                return null;
            }
            final Object[] ctr_args = { metadata };
            final Class[] ctr_args_classes = { DatabaseMetaData.class };
            final Constructor ctr = adapterClass.getConstructor((Class[])ctr_args_classes);
            try {
                adapter_obj = ctr.newInstance(ctr_args);
            }
            catch (InvocationTargetException ite) {
                if (ite.getTargetException() != null && ite.getTargetException() instanceof NucleusDataStoreException) {
                    throw (NucleusDataStoreException)ite.getTargetException();
                }
                return null;
            }
            catch (Exception e) {
                NucleusLogger.DATASTORE.error(DatastoreAdapterFactory.LOCALISER.msg("051026", adapterClassName, e));
                return null;
            }
        }
        catch (ClassNotResolvedException ex) {
            NucleusLogger.DATASTORE.error(DatastoreAdapterFactory.LOCALISER.msg("051026", adapterClassName, ex));
            return null;
        }
        catch (NoSuchMethodException nsme) {
            NucleusLogger.DATASTORE.error(DatastoreAdapterFactory.LOCALISER.msg("051026", adapterClassName, nsme));
            return null;
        }
        return (DatastoreAdapter)adapter_obj;
    }
    
    protected Class getAdapterClass(final PluginManager pluginMgr, final String adapterClassName, final String productName, final ClassLoaderResolver clr) {
        final ConfigurationElement[] elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.store_datastoreadapter", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                if (adapterClassName != null) {
                    if (elems[i].getAttribute("class-name").equals(adapterClassName)) {
                        return pluginMgr.loadClass(elems[i].getExtension().getPlugin().getSymbolicName(), elems[i].getAttribute("class-name"));
                    }
                }
                else {
                    final String vendorId = elems[i].getAttribute("vendor-id");
                    if (productName.toLowerCase().indexOf(vendorId.toLowerCase()) >= 0) {
                        return pluginMgr.loadClass(elems[i].getExtension().getPlugin().getSymbolicName(), elems[i].getAttribute("class-name"));
                    }
                }
            }
        }
        if (adapterClassName != null) {
            return clr.classForName(adapterClassName, false);
        }
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
