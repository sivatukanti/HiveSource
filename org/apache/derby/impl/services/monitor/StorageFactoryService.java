// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import java.util.NoSuchElementException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.io.WritableStorageFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.io.StorageFile;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.io.StorageFactory;
import org.apache.derby.iapi.services.monitor.PersistentService;

final class StorageFactoryService implements PersistentService
{
    private static final String SERVICE_PROPERTIES_EOF_TOKEN = "#--- last line, don't put anything after this line ---";
    private String home;
    private String canonicalHome;
    private final String subSubProtocol;
    private final Class storageFactoryClass;
    private StorageFactory rootStorageFactory;
    private char separatorChar;
    
    StorageFactoryService(final String subSubProtocol, final Class storageFactoryClass) throws StandardException {
        this.subSubProtocol = subSubProtocol;
        this.storageFactoryClass = storageFactoryClass;
        final Object environment = Monitor.getMonitor().getEnvironment();
        if (environment instanceof File) {
            final File file = (File)environment;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                    public Object run() throws IOException, StandardException {
                        StorageFactoryService.this.home = file.getPath();
                        StorageFactoryService.this.canonicalHome = file.getCanonicalPath();
                        StorageFactoryService.this.rootStorageFactory = StorageFactoryService.this.getStorageFactoryInstance(true, null, null, null);
                        if (StorageFactoryService.this.home != null) {
                            final StorageFile storageFile = StorageFactoryService.this.rootStorageFactory.newStorageFile(null);
                            if (storageFile.mkdirs()) {
                                storageFile.limitAccessToOwner();
                            }
                        }
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException ex2) {
                this.home = null;
                this.canonicalHome = null;
            }
        }
        if (this.rootStorageFactory == null) {
            try {
                this.rootStorageFactory = this.getStorageFactoryInstance(true, null, null, null);
            }
            catch (IOException ex) {
                throw Monitor.exceptionStartingModule(ex);
            }
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                StorageFactoryService.this.separatorChar = StorageFactoryService.this.rootStorageFactory.getSeparator();
                return null;
            }
        });
    }
    
    public boolean hasStorageFactory() {
        return true;
    }
    
    public StorageFactory getStorageFactoryInstance(final boolean b, final String s, final String s2, final String s3) throws StandardException, IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<StorageFactory>)new PrivilegedExceptionAction() {
                public Object run() throws InstantiationException, IllegalAccessException, IOException {
                    return StorageFactoryService.this.privGetStorageFactoryInstance(b, s, s2, s3);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw StandardException.newException("XBM0W.S", ex.getException(), this.subSubProtocol, this.storageFactoryClass);
        }
    }
    
    private StorageFactory privGetStorageFactoryInstance(final boolean b, final String s, final String s2, final String s3) throws InstantiationException, IllegalAccessException, IOException {
        final StorageFactory storageFactory = this.storageFactoryClass.newInstance();
        String substring;
        if (s != null && this.subSubProtocol != null && s.startsWith(this.subSubProtocol + ":")) {
            substring = s.substring(this.subSubProtocol.length() + 1);
        }
        else {
            substring = s;
        }
        storageFactory.init(b ? this.home : null, substring, s2, s3);
        return storageFactory;
    }
    
    public String getType() {
        return this.subSubProtocol;
    }
    
    public Enumeration getBootTimeServices() {
        if (this.home == null) {
            return null;
        }
        return new DirectoryList();
    }
    
    public Properties getServiceProperties(final String s, final Properties defaults) throws StandardException {
        final String recreateServiceRoot = this.recreateServiceRoot(s, defaults);
        final Properties properties = new Properties(defaults);
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException, StandardException, InstantiationException, IllegalAccessException {
                    if (recreateServiceRoot != null) {
                        final FileInputStream in = new FileInputStream(new File(recreateServiceRoot, "service.properties"));
                        try {
                            properties.load(new BufferedInputStream(in));
                        }
                        finally {
                            in.close();
                        }
                    }
                    else {
                        final StorageFactory access$400 = StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                        final StorageFile storageFile = access$400.newStorageFile("service.properties");
                        StorageFactoryService.this.resolveServicePropertiesFiles(access$400, storageFile);
                        try {
                            final InputStream inputStream = storageFile.getInputStream();
                            try {
                                properties.load(new BufferedInputStream(inputStream));
                            }
                            finally {
                                inputStream.close();
                            }
                        }
                        finally {
                            access$400.shutdown();
                        }
                    }
                    return null;
                }
            });
            return properties;
        }
        catch (PrivilegedActionException ex) {
            if (ex.getException() instanceof FileNotFoundException) {
                return null;
            }
            throw Monitor.exceptionStartingModule(ex.getException());
        }
        catch (SecurityException ex2) {
            throw Monitor.exceptionStartingModule(ex2);
        }
    }
    
    public void saveServiceProperties(final String s, final StorageFactory storageFactory, final Properties properties, final boolean b) throws StandardException {
        if (!(storageFactory instanceof WritableStorageFactory)) {
            throw StandardException.newException("XBM0P.D");
        }
        final WritableStorageFactory writableStorageFactory = (WritableStorageFactory)storageFactory;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException {
                    final StorageFile storageFile = b ? writableStorageFactory.newStorageFile("service.properties".concat("old")) : null;
                    final StorageFile storageFile2 = writableStorageFactory.newStorageFile("service.properties");
                    final FileOperationHelper fileOperationHelper = new FileOperationHelper();
                    if (b) {
                        fileOperationHelper.renameTo(storageFile2, storageFile, true);
                    }
                    OutputStream outputStream = null;
                    try {
                        outputStream = storageFile2.getOutputStream();
                        properties.store(outputStream, s + MessageService.getTextMessage("M001"));
                        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "ISO-8859-1"));
                        bufferedWriter.write("#--- last line, don't put anything after this line ---");
                        bufferedWriter.newLine();
                        writableStorageFactory.sync(outputStream, false);
                        bufferedWriter.close();
                        outputStream.close();
                        outputStream = null;
                    }
                    catch (IOException ex) {
                        if (storageFile != null) {
                            fileOperationHelper.renameTo(storageFile, storageFile2, false);
                        }
                        if (b) {
                            throw StandardException.newException("XBM0B.D", ex);
                        }
                        throw Monitor.exceptionStartingModule(ex);
                    }
                    finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            }
                            catch (IOException ex2) {}
                        }
                    }
                    if (storageFile != null && !fileOperationHelper.delete(storageFile, false)) {
                        Monitor.getStream().printlnWithHeader(MessageService.getTextMessage("M004", getMostAccuratePath(storageFile)));
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
    }
    
    public void createDataWarningFile(final StorageFactory storageFactory) throws StandardException {
        if (!(storageFactory instanceof WritableStorageFactory)) {
            throw StandardException.newException("XBM0P.D");
        }
        final WritableStorageFactory writableStorageFactory = (WritableStorageFactory)storageFactory;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException {
                    Writer writer = null;
                    try {
                        writer = new OutputStreamWriter(writableStorageFactory.newStorageFile("README_DO_NOT_TOUCH_FILES.txt").getOutputStream(), "UTF8");
                        writer.write(MessageService.getTextMessage("M005"));
                    }
                    catch (IOException ex) {}
                    finally {
                        if (writer != null) {
                            try {
                                ((OutputStreamWriter)writer).close();
                            }
                            catch (IOException ex2) {}
                        }
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
    }
    
    public void saveServiceProperties(final String s, final Properties properties) throws StandardException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException {
                    final File file = new File(s, "service.properties");
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        FileUtil.limitAccessToOwner(file);
                        properties.store(out, s + MessageService.getTextMessage("M001"));
                        out.getFD().sync();
                        out.close();
                        out = null;
                    }
                    catch (IOException ex) {
                        if (out != null) {
                            try {
                                out.close();
                            }
                            catch (IOException ex2) {}
                        }
                        throw Monitor.exceptionStartingModule(ex);
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
    }
    
    private void resolveServicePropertiesFiles(final StorageFactory storageFactory, final StorageFile storageFile) throws StandardException {
        final StorageFile storageFile2 = storageFactory.newStorageFile("service.properties".concat("old"));
        final FileOperationHelper fileOperationHelper = new FileOperationHelper();
        final boolean exists = fileOperationHelper.exists(storageFile, true);
        final boolean exists2 = fileOperationHelper.exists(storageFile2, true);
        if (exists && !exists2) {
            return;
        }
        if (exists2 && !exists) {
            fileOperationHelper.renameTo(storageFile2, storageFile, true);
            Monitor.getStream().printlnWithHeader(MessageService.getTextMessage("M002"));
        }
        else if (exists2 && exists) {
            BufferedReader bufferedReader = null;
            String s = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(storageFile.getPath()), "ISO-8859-1"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.trim().length() != 0) {
                        s = line;
                    }
                }
            }
            catch (IOException ex) {
                throw StandardException.newException("XJ113.S", ex, storageFile.getPath(), ex.getMessage());
            }
            finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
                catch (IOException ex2) {}
            }
            if (s != null && s.startsWith("#--- last line, don't put anything after this line ---")) {
                String s2;
                if (fileOperationHelper.delete(storageFile2, false)) {
                    s2 = MessageService.getTextMessage("M003");
                }
                else {
                    s2 = MessageService.getTextMessage("M004", getMostAccuratePath(storageFile2));
                }
                Monitor.getStream().printlnWithHeader(s2);
            }
            else {
                fileOperationHelper.delete(storageFile, false);
                fileOperationHelper.renameTo(storageFile2, storageFile, true);
                Monitor.getStream().printlnWithHeader(MessageService.getTextMessage("M002"));
            }
        }
    }
    
    protected String recreateServiceRoot(final String s, final Properties properties) throws StandardException {
        if (properties == null) {
            return null;
        }
        boolean b = false;
        boolean b2 = false;
        String s2 = properties.getProperty("createFrom");
        if (s2 != null) {
            b = true;
            b2 = false;
        }
        else {
            s2 = properties.getProperty("restoreFrom");
            if (s2 != null) {
                b = true;
                b2 = true;
            }
            else {
                s2 = properties.getProperty("rollForwardRecoveryFrom");
                if (s2 != null) {
                    try {
                        if (AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                            public Object run() throws IOException, StandardException, InstantiationException, IllegalAccessException {
                                final StorageFactory access$400 = StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                                try {
                                    return access$400.newStorageFile(null).exists() ? this : null;
                                }
                                finally {
                                    access$400.shutdown();
                                }
                            }
                        }) == null) {
                            b = true;
                            b2 = false;
                        }
                    }
                    catch (PrivilegedActionException ex) {
                        throw Monitor.exceptionStartingModule(ex.getException());
                    }
                }
            }
        }
        if (s2 != null) {
            final File file = new File(s2);
            if (this.fileExists(file)) {
                final File file2 = new File(s2, "service.properties");
                if (this.fileExists(file2)) {
                    if (b) {
                        this.createServiceRoot(s, b2);
                    }
                    Label_0196: {
                        try {
                            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                                public Object run() throws IOException, StandardException, InstantiationException, IllegalAccessException {
                                    final WritableStorageFactory writableStorageFactory = (WritableStorageFactory)StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                                    try {
                                        final StorageFile storageFile = writableStorageFactory.newStorageFile("service.properties");
                                        if (storageFile.exists() && !storageFile.delete()) {
                                            throw StandardException.newException("XBM0R.D", storageFile);
                                        }
                                        return null;
                                    }
                                    finally {
                                        writableStorageFactory.shutdown();
                                    }
                                }
                            });
                            break Label_0196;
                        }
                        catch (PrivilegedActionException ex2) {
                            throw Monitor.exceptionStartingModule(ex2.getException());
                        }
                        throw StandardException.newException("XBM0Q.D", file2);
                    }
                    properties.put("derby.__rt.inRestore", "True");
                    if (b) {
                        properties.put("derby.__rt.deleteRootOnError", "True");
                        return s2;
                    }
                    return s2;
                }
                throw StandardException.newException("XBM0Q.D", file2);
            }
            throw StandardException.newException("XBM0Y.D", file);
        }
        return s2;
    }
    
    public String createServiceRoot(final String s, final boolean b) throws StandardException {
        if (!(this.rootStorageFactory instanceof WritableStorageFactory)) {
            throw StandardException.newException("XBM0P.D");
        }
        Exception exception;
        try {
            return this.getProtocolLeadIn() + AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException, IOException, InstantiationException, IllegalAccessException {
                    final StorageFactory access$400 = StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                    try {
                        final StorageFile storageFile = access$400.newStorageFile(null);
                        if (storageFile.exists()) {
                            if (!b) {
                                StorageFactoryService.this.vetService(access$400, s);
                                throw StandardException.newException("XBM0J.D", StorageFactoryService.this.getDirectoryPath(s));
                            }
                            if (!storageFile.deleteAll()) {
                                throw StandardException.newException("XBM0I.D", StorageFactoryService.this.getDirectoryPath(s));
                            }
                        }
                        if (storageFile.mkdirs()) {
                            storageFile.limitAccessToOwner();
                            access$400.setCanonicalName(storageFile.getCanonicalPath());
                            try {
                                return access$400.getCanonicalName();
                            }
                            catch (IOException ex) {
                                storageFile.deleteAll();
                                throw ex;
                            }
                        }
                        throw StandardException.newException("XBM0H.D", storageFile);
                    }
                    finally {
                        access$400.shutdown();
                    }
                }
            });
        }
        catch (SecurityException ex) {
            exception = ex;
        }
        catch (PrivilegedActionException ex2) {
            exception = ex2.getException();
            if (exception instanceof StandardException) {
                throw (StandardException)exception;
            }
        }
        throw StandardException.newException("XBM0H.D", exception, s);
    }
    
    private void vetService(final StorageFactory storageFactory, final String s) throws StandardException {
        if (!storageFactory.newStorageFile("service.properties").exists() && storageFactory.newStorageFile("seg0").exists()) {
            throw StandardException.newException("XBM0A.D", s, "service.properties");
        }
    }
    
    private String getDirectoryPath(final String str) {
        final StringBuffer sb = new StringBuffer();
        if (this.home != null) {
            sb.append(this.home);
            sb.append(this.separatorChar);
        }
        if (this.separatorChar != '/') {
            sb.append(str.replace('/', this.separatorChar));
        }
        else {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public boolean removeServiceRoot(final String s) {
        if (!(this.rootStorageFactory instanceof WritableStorageFactory)) {
            return false;
        }
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException, IOException, InstantiationException, IllegalAccessException {
                    final StorageFactory access$400 = StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                    try {
                        return access$400.newStorageFile(null).deleteAll() ? this : null;
                    }
                    finally {
                        access$400.shutdown();
                    }
                }
            }) != null;
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
    }
    
    public String getCanonicalServiceName(String substring) throws StandardException {
        final int index = substring.indexOf(58);
        if (index < 2 && !this.getType().equals("directory")) {
            return null;
        }
        if (index > 1) {
            if (!substring.startsWith(this.getType() + ":")) {
                return null;
            }
            substring = substring.substring(index + 1);
        }
        final String s = substring;
        try {
            return this.getProtocolLeadIn() + AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction() {
                public Object run() throws StandardException, IOException, InstantiationException, IllegalAccessException {
                    final StorageFactory access$400 = StorageFactoryService.this.privGetStorageFactoryInstance(true, s, null, null);
                    try {
                        return access$400.getCanonicalName();
                    }
                    finally {
                        access$400.shutdown();
                    }
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw Monitor.exceptionStartingModule(ex.getException());
        }
    }
    
    public String getUserServiceName(String s) {
        if (this.home != null && s.length() > this.canonicalHome.length() + 1 && s.startsWith(this.canonicalHome)) {
            s = s.substring(this.canonicalHome.length());
            if (s.charAt(0) == this.separatorChar) {
                s = s.substring(1);
            }
        }
        return s.replace(this.separatorChar, '/');
    }
    
    public boolean isSameService(final String s, final String anObject) {
        return s.equals(anObject);
    }
    
    private final boolean fileExists(final File file) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return new Boolean(file.exists());
            }
        });
    }
    
    public Class getStorageFactoryClass() {
        return this.storageFactoryClass;
    }
    
    private String getProtocolLeadIn() {
        if (this.getType().equals("directory")) {
            return "";
        }
        return this.getType() + ":";
    }
    
    private static String getMostAccuratePath(final StorageFile storageFile) {
        String s = storageFile.getPath();
        try {
            s = storageFile.getCanonicalPath();
        }
        catch (IOException ex) {}
        return s;
    }
    
    private static class FileOperationHelper
    {
        private String operation;
        
        boolean exists(final StorageFile storageFile, final boolean b) throws StandardException {
            this.operation = "exists";
            boolean exists = false;
            try {
                exists = storageFile.exists();
            }
            catch (SecurityException ex) {
                this.handleSecPrivException(storageFile, b, ex);
            }
            return exists;
        }
        
        boolean delete(final StorageFile storageFile, final boolean b) throws StandardException {
            this.operation = "delete";
            boolean delete = false;
            try {
                delete = storageFile.delete();
            }
            catch (SecurityException ex) {
                this.handleSecPrivException(storageFile, b, ex);
            }
            if (b && !delete) {
                throw StandardException.newException("XBM0R.D", storageFile.getPath());
            }
            return delete;
        }
        
        boolean renameTo(final StorageFile storageFile, final StorageFile storageFile2, final boolean b) throws StandardException {
            this.operation = "renameTo";
            this.delete(storageFile2, false);
            boolean renameTo = false;
            try {
                renameTo = storageFile.renameTo(storageFile2);
            }
            catch (SecurityException ex) {
                StorageFile storageFile3 = storageFile2;
                try {
                    System.getSecurityManager().checkWrite(storageFile.getPath());
                }
                catch (SecurityException ex2) {
                    storageFile3 = storageFile;
                }
                this.handleSecPrivException(storageFile3, b, ex);
            }
            if (b && !renameTo) {
                throw StandardException.newException("XBM0S.D", storageFile.getPath(), storageFile2.getPath());
            }
            return renameTo;
        }
        
        private void handleSecPrivException(final StorageFile storageFile, final boolean b, final SecurityException ex) throws StandardException {
            if (b) {
                throw StandardException.newException("XBM0C.D", ex, this.operation, storageFile.getName(), ex.getMessage());
            }
            Monitor.getStream().printlnWithHeader(MessageService.getTextMessage("XBM0C.D", this.operation, getMostAccuratePath(storageFile), ex.getMessage()));
        }
    }
    
    final class DirectoryList implements Enumeration, PrivilegedAction
    {
        private String[] contents;
        private StorageFile systemDirectory;
        private int index;
        private boolean validIndex;
        private int actionCode;
        private static final int INIT_ACTION = 0;
        private static final int HAS_MORE_ELEMENTS_ACTION = 1;
        
        DirectoryList() {
            this.actionCode = 0;
            AccessController.doPrivileged((PrivilegedAction<Object>)this);
        }
        
        public boolean hasMoreElements() {
            if (this.contents == null) {
                return false;
            }
            if (this.validIndex) {
                return true;
            }
            this.actionCode = 1;
            return AccessController.doPrivileged((PrivilegedAction<Object>)this) != null;
        }
        
        public Object nextElement() throws NoSuchElementException {
            if (!this.hasMoreElements()) {
                throw new NoSuchElementException();
            }
            this.validIndex = false;
            return this.contents[this.index++];
        }
        
        public final Object run() {
            switch (this.actionCode) {
                case 0: {
                    this.systemDirectory = StorageFactoryService.this.rootStorageFactory.newStorageFile(null);
                    this.contents = this.systemDirectory.list();
                    return null;
                }
                case 1: {
                    while (this.index < this.contents.length) {
                        try {
                            final StorageFile storageFile = StorageFactoryService.this.rootStorageFactory.newStorageFile(this.contents[this.index]);
                            if (storageFile.isDirectory()) {
                                if (StorageFactoryService.this.rootStorageFactory.newStorageFile(storageFile, "service.properties").exists()) {
                                    this.contents[this.index] = storageFile.getCanonicalPath();
                                    this.validIndex = true;
                                    return this;
                                }
                            }
                        }
                        catch (Exception ex) {}
                        this.contents[this.index++] = null;
                    }
                    return null;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
