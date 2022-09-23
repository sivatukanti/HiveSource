// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FileUtil;
import java.io.File;
import org.apache.derby.iapi.services.context.ContextService;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.net.MalformedURLException;
import java.io.FileInputStream;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.FileInfoDescriptor;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.TransactionController;
import java.io.InputStream;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class JarUtil
{
    private LanguageConnectionContext lcc;
    private String schemaName;
    private String sqlName;
    private FileResource fr;
    private DataDictionary dd;
    private DataDescriptorGenerator ddg;
    
    private JarUtil(final LanguageConnectionContext lcc, final String schemaName, final String sqlName) throws StandardException {
        this.schemaName = schemaName;
        this.sqlName = sqlName;
        this.lcc = lcc;
        this.fr = lcc.getTransactionExecute().getFileHandler();
        this.dd = lcc.getDataDictionary();
        this.ddg = this.dd.getDataDescriptorGenerator();
    }
    
    public static long install(final LanguageConnectionContext languageConnectionContext, final String s, final String s2, final String s3) throws StandardException {
        final JarUtil jarUtil = new JarUtil(languageConnectionContext, s, s2);
        InputStream openJarURL = null;
        try {
            openJarURL = openJarURL(s3);
            return jarUtil.add(openJarURL);
        }
        catch (IOException ex) {
            throw StandardException.newException("46001", ex, s3);
        }
        finally {
            try {
                if (openJarURL != null) {
                    openJarURL.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private long add(final InputStream inputStream) throws StandardException {
        this.dd.startWriting(this.lcc);
        final FileInfoDescriptor info = this.getInfo();
        if (info != null) {
            throw StandardException.newException("X0Y32.S", info.getDescriptorType(), this.sqlName, info.getSchemaDescriptor().getDescriptorType(), this.schemaName);
        }
        final SchemaDescriptor schemaDescriptor = this.dd.getSchemaDescriptor(this.schemaName, null, true);
        try {
            this.notifyLoader(false);
            this.dd.invalidateAllSPSPlans();
            final UUID uuid = Monitor.getMonitor().getUUIDFactory().createUUID();
            final long setJar = this.setJar(mkExternalName(uuid, this.schemaName, this.sqlName, this.fr.getSeparatorChar()), inputStream, true, 0L);
            this.dd.addDescriptor(this.ddg.newFileInfoDescriptor(uuid, schemaDescriptor, this.sqlName, setJar), schemaDescriptor, 12, false, this.lcc.getTransactionExecute());
            return setJar;
        }
        finally {
            this.notifyLoader(true);
        }
    }
    
    public static void drop(final LanguageConnectionContext languageConnectionContext, final String s, final String s2) throws StandardException {
        new JarUtil(languageConnectionContext, s, s2).drop();
    }
    
    private void drop() throws StandardException {
        this.dd.startWriting(this.lcc);
        final FileInfoDescriptor info = this.getInfo();
        if (info == null) {
            throw StandardException.newException("X0X13.S", this.sqlName, this.schemaName);
        }
        final String serviceProperty = PropertyUtil.getServiceProperty(this.lcc.getTransactionExecute(), "derby.database.classpath");
        if (serviceProperty != null) {
            final String[][] dbClassPath = IdUtil.parseDbClassPath(serviceProperty);
            boolean b = false;
            for (int i = 0; i < dbClassPath.length; ++i) {
                if (dbClassPath.length == 2 && dbClassPath[i][0].equals(this.schemaName) && dbClassPath[i][1].equals(this.sqlName)) {
                    b = true;
                }
            }
            if (b) {
                throw StandardException.newException("X0X07.S", IdUtil.mkQualifiedName(this.schemaName, this.sqlName), serviceProperty);
            }
        }
        try {
            this.notifyLoader(false);
            this.dd.invalidateAllSPSPlans();
            this.dd.getDependencyManager().invalidateFor(info, 17, this.lcc);
            final UUID uuid = info.getUUID();
            this.dd.dropFileInfoDescriptor(info);
            this.fr.remove(mkExternalName(uuid, this.schemaName, this.sqlName, this.fr.getSeparatorChar()), info.getGenerationId());
        }
        finally {
            this.notifyLoader(true);
        }
    }
    
    public static long replace(final LanguageConnectionContext languageConnectionContext, final String s, final String s2, final String s3) throws StandardException {
        final JarUtil jarUtil = new JarUtil(languageConnectionContext, s, s2);
        InputStream openJarURL = null;
        try {
            openJarURL = openJarURL(s3);
            return jarUtil.replace(openJarURL);
        }
        catch (IOException ex) {
            throw StandardException.newException("46001", ex, s3);
        }
        finally {
            try {
                if (openJarURL != null) {
                    openJarURL.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private long replace(final InputStream inputStream) throws StandardException {
        this.dd.startWriting(this.lcc);
        final FileInfoDescriptor info = this.getInfo();
        if (info == null) {
            throw StandardException.newException("X0X13.S", this.sqlName, this.schemaName);
        }
        try {
            this.notifyLoader(false);
            this.dd.invalidateAllSPSPlans();
            this.dd.dropFileInfoDescriptor(info);
            final long setJar = this.setJar(mkExternalName(info.getUUID(), this.schemaName, this.sqlName, this.fr.getSeparatorChar()), inputStream, false, info.getGenerationId());
            this.dd.addDescriptor(this.ddg.newFileInfoDescriptor(info.getUUID(), info.getSchemaDescriptor(), this.sqlName, setJar), info.getSchemaDescriptor(), 12, false, this.lcc.getTransactionExecute());
            return setJar;
        }
        finally {
            this.notifyLoader(true);
        }
    }
    
    private FileInfoDescriptor getInfo() throws StandardException {
        return this.dd.getFileInfoDescriptor(this.dd.getSchemaDescriptor(this.schemaName, null, true), this.sqlName);
    }
    
    private void notifyLoader(final boolean b) throws StandardException {
        this.lcc.getLanguageConnectionFactory().getClassFactory().notifyModifyJar(b);
    }
    
    private static InputStream openJarURL(final String s) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    try {
                        return new URL(s).openStream();
                    }
                    catch (MalformedURLException ex) {
                        return new FileInputStream(s);
                    }
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private long setJar(final String s, final InputStream inputStream, final boolean b, final long n) throws StandardException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException {
                    long value;
                    if (b) {
                        value = JarUtil.this.fr.add(s, inputStream);
                    }
                    else {
                        value = JarUtil.this.fr.replace(s, n, inputStream);
                    }
                    return new Long(value);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
    }
    
    public static String mkExternalName(final UUID uuid, final String s, final String s2, final char c) throws StandardException {
        return mkExternalNameInternal(uuid, s, s2, c, false, false);
    }
    
    private static String mkExternalNameInternal(final UUID uuid, final String str, final String str2, final char c, final boolean b, final boolean b2) throws StandardException {
        final StringBuffer sb = new StringBuffer(30);
        sb.append("jar");
        sb.append(c);
        boolean checkVersion = false;
        if (!b) {
            checkVersion = ((LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext")).getDataDictionary().checkVersion(210, null);
        }
        if ((!b && checkVersion) || (b && b2)) {
            sb.append(uuid.toString());
            sb.append(".jar");
        }
        else {
            sb.append(str);
            sb.append(c);
            sb.append(str2);
            sb.append(".jar");
        }
        return sb.toString();
    }
    
    public static void upgradeJar(final TransactionController transactionController, final FileInfoDescriptor fileInfoDescriptor) throws StandardException {
        final FileResource fileHandler = transactionController.getFileHandler();
        FileUtil.copyFile(new File(fileHandler.getAsFile(mkExternalNameInternal(fileInfoDescriptor.getUUID(), fileInfoDescriptor.getSchemaDescriptor().getSchemaName(), fileInfoDescriptor.getName(), File.separatorChar, true, false), fileInfoDescriptor.getGenerationId()).getPath()), new File(fileHandler.getAsFile(mkExternalNameInternal(fileInfoDescriptor.getUUID(), fileInfoDescriptor.getSchemaDescriptor().getSchemaName(), fileInfoDescriptor.getName(), File.separatorChar, true, true), fileInfoDescriptor.getGenerationId()).getPath()), null);
    }
}
