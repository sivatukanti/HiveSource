// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.hive.metastore.hooks.JDOConnectionURLHook;
import org.apache.hadoop.hive.common.JavaUtils;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public class MetaStoreInit
{
    private static final Log LOG;
    
    static boolean updateConnectionURL(final HiveConf originalConf, final Configuration activeConf, final String badUrl, final MetaStoreInitData updateData) throws MetaException {
        String connectUrl = null;
        final String currentUrl = getConnectionURL(activeConf);
        try {
            initConnectionUrlHook(originalConf, updateData);
            if (updateData.urlHook != null) {
                if (badUrl != null) {
                    updateData.urlHook.notifyBadConnectionUrl(badUrl);
                }
                connectUrl = updateData.urlHook.getJdoConnectionUrl(originalConf);
            }
        }
        catch (Exception e) {
            MetaStoreInit.LOG.error("Exception while getting connection URL from the hook: " + e);
        }
        if (connectUrl != null && !connectUrl.equals(currentUrl)) {
            MetaStoreInit.LOG.error(String.format("Overriding %s with %s", HiveConf.ConfVars.METASTORECONNECTURLKEY.toString(), connectUrl));
            activeConf.set(HiveConf.ConfVars.METASTORECONNECTURLKEY.toString(), connectUrl);
            return true;
        }
        return false;
    }
    
    static String getConnectionURL(final Configuration conf) {
        return conf.get(HiveConf.ConfVars.METASTORECONNECTURLKEY.toString(), "");
    }
    
    private static synchronized void initConnectionUrlHook(final HiveConf hiveConf, final MetaStoreInitData updateData) throws ClassNotFoundException {
        final String className = hiveConf.get(HiveConf.ConfVars.METASTORECONNECTURLHOOK.toString(), "").trim();
        if (className.equals("")) {
            updateData.urlHookClassName = "";
            updateData.urlHook = null;
            return;
        }
        final boolean urlHookChanged = !updateData.urlHookClassName.equals(className);
        if (updateData.urlHook == null || urlHookChanged) {
            updateData.urlHookClassName = className.trim();
            final Class<?> urlHookClass = Class.forName(updateData.urlHookClassName, true, JavaUtils.getClassLoader());
            updateData.urlHook = ReflectionUtils.newInstance(urlHookClass, null);
        }
    }
    
    static {
        LOG = LogFactory.getLog(MetaStoreInit.class);
    }
    
    static class MetaStoreInitData
    {
        JDOConnectionURLHook urlHook;
        String urlHookClassName;
        
        MetaStoreInitData() {
            this.urlHook = null;
            this.urlHookClassName = "";
        }
    }
}
