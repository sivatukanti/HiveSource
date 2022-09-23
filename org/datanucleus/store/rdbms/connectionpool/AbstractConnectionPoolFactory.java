// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import org.datanucleus.PersistenceConfiguration;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.datanucleus.store.StoreManager;
import org.datanucleus.ClassLoaderResolver;

public abstract class AbstractConnectionPoolFactory implements ConnectionPoolFactory
{
    protected void loadDriver(final String dbDriver, final ClassLoaderResolver clr) {
        try {
            clr.classForName(dbDriver).newInstance();
        }
        catch (Exception e) {
            try {
                Class.forName(dbDriver).newInstance();
            }
            catch (Exception e2) {
                throw new DatastoreDriverNotFoundException(dbDriver);
            }
        }
    }
    
    public static Properties getPropertiesForDriver(final StoreManager storeMgr) {
        final Properties dbProps = new Properties();
        String dbUser = storeMgr.getConnectionUserName();
        if (dbUser == null) {
            dbUser = "";
        }
        dbProps.setProperty("user", dbUser);
        String dbPassword = storeMgr.getConnectionPassword();
        if (dbPassword == null) {
            dbPassword = "";
        }
        dbProps.setProperty("password", dbPassword);
        final PersistenceConfiguration conf = storeMgr.getNucleusContext().getPersistenceConfiguration();
        final String drvPropsString = (String)conf.getProperty("datanucleus.connectionPool.driverProps");
        if (drvPropsString != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PrintWriter pw = new PrintWriter(baos);
            final StringTokenizer st = new StringTokenizer(drvPropsString, ",");
            while (st.hasMoreTokens()) {
                final String prop = st.nextToken();
                pw.println(prop);
            }
            pw.flush();
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            final Properties drvProps = new Properties();
            try {
                drvProps.load(bais);
            }
            catch (IOException ex) {}
            dbProps.putAll(drvProps);
        }
        return dbProps;
    }
}
