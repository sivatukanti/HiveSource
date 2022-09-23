// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.io;

import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;

public interface StorageFile
{
    public static final int NO_FILE_LOCK_SUPPORT = 0;
    public static final int EXCLUSIVE_FILE_LOCK = 1;
    public static final int EXCLUSIVE_FILE_LOCK_NOT_AVAILABLE = 2;
    
    String[] list();
    
    boolean canWrite();
    
    boolean exists();
    
    boolean isDirectory();
    
    boolean delete();
    
    boolean deleteAll();
    
    String getPath();
    
    String getCanonicalPath() throws IOException;
    
    String getName();
    
    URL getURL() throws MalformedURLException;
    
    boolean createNewFile() throws IOException;
    
    boolean renameTo(final StorageFile p0);
    
    boolean mkdir();
    
    boolean mkdirs();
    
    long length();
    
    StorageFile getParentDir();
    
    boolean setReadOnly();
    
    OutputStream getOutputStream() throws FileNotFoundException;
    
    OutputStream getOutputStream(final boolean p0) throws FileNotFoundException;
    
    InputStream getInputStream() throws FileNotFoundException;
    
    int getExclusiveFileLock() throws StandardException;
    
    void releaseExclusiveFileLock();
    
    StorageRandomAccessFile getRandomAccessFile(final String p0) throws FileNotFoundException;
    
    void limitAccessToOwner();
}
