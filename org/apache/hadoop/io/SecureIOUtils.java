// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.io.nativeio.NativeIO;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.RandomAccessFile;
import java.io.File;
import org.apache.hadoop.fs.FileSystem;

public class SecureIOUtils
{
    private static final boolean skipSecurity;
    private static final FileSystem rawFilesystem;
    
    public static RandomAccessFile openForRandomRead(final File f, final String mode, final String expectedOwner, final String expectedGroup) throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return new RandomAccessFile(f, mode);
        }
        return forceSecureOpenForRandomRead(f, mode, expectedOwner, expectedGroup);
    }
    
    @VisibleForTesting
    protected static RandomAccessFile forceSecureOpenForRandomRead(final File f, final String mode, final String expectedOwner, final String expectedGroup) throws IOException {
        final RandomAccessFile raf = new RandomAccessFile(f, mode);
        boolean success = false;
        try {
            final NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(raf.getFD());
            checkStat(f, stat.getOwner(), stat.getGroup(), expectedOwner, expectedGroup);
            success = true;
            return raf;
        }
        finally {
            if (!success) {
                raf.close();
            }
        }
    }
    
    public static FSDataInputStream openFSDataInputStream(final File file, final String expectedOwner, final String expectedGroup) throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return SecureIOUtils.rawFilesystem.open(new Path(file.getAbsolutePath()));
        }
        return forceSecureOpenFSDataInputStream(file, expectedOwner, expectedGroup);
    }
    
    @VisibleForTesting
    protected static FSDataInputStream forceSecureOpenFSDataInputStream(final File file, final String expectedOwner, final String expectedGroup) throws IOException {
        final FSDataInputStream in = SecureIOUtils.rawFilesystem.open(new Path(file.getAbsolutePath()));
        boolean success = false;
        try {
            final NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(in.getFileDescriptor());
            checkStat(file, stat.getOwner(), stat.getGroup(), expectedOwner, expectedGroup);
            success = true;
            return in;
        }
        finally {
            if (!success) {
                in.close();
            }
        }
    }
    
    public static FileInputStream openForRead(final File f, final String expectedOwner, final String expectedGroup) throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            return new FileInputStream(f);
        }
        return forceSecureOpenForRead(f, expectedOwner, expectedGroup);
    }
    
    @VisibleForTesting
    protected static FileInputStream forceSecureOpenForRead(final File f, final String expectedOwner, final String expectedGroup) throws IOException {
        final FileInputStream fis = new FileInputStream(f);
        boolean success = false;
        try {
            final NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(fis.getFD());
            checkStat(f, stat.getOwner(), stat.getGroup(), expectedOwner, expectedGroup);
            success = true;
            return fis;
        }
        finally {
            if (!success) {
                fis.close();
            }
        }
    }
    
    private static FileOutputStream insecureCreateForWrite(final File f, final int permissions) throws IOException {
        if (f.exists()) {
            throw new AlreadyExistsException("File " + f + " already exists");
        }
        final FileOutputStream fos = new FileOutputStream(f);
        boolean success = false;
        try {
            SecureIOUtils.rawFilesystem.setPermission(new Path(f.getAbsolutePath()), new FsPermission((short)permissions));
            success = true;
            return fos;
        }
        finally {
            if (!success) {
                fos.close();
            }
        }
    }
    
    public static FileOutputStream createForWrite(final File f, final int permissions) throws IOException {
        if (SecureIOUtils.skipSecurity) {
            return insecureCreateForWrite(f, permissions);
        }
        return NativeIO.getCreateForWriteFileOutputStream(f, permissions);
    }
    
    private static void checkStat(final File f, final String owner, final String group, final String expectedOwner, final String expectedGroup) throws IOException {
        boolean success = true;
        if (expectedOwner != null && !expectedOwner.equals(owner)) {
            if (Path.WINDOWS) {
                final UserGroupInformation ugi = UserGroupInformation.createRemoteUser(expectedOwner);
                final String adminsGroupString = "Administrators";
                success = (owner.equals("Administrators") && ugi.getGroups().contains("Administrators"));
            }
            else {
                success = false;
            }
        }
        if (!success) {
            throw new IOException("Owner '" + owner + "' for path " + f + " did not match expected owner '" + expectedOwner + "'");
        }
    }
    
    static {
        final boolean shouldBeSecure = UserGroupInformation.isSecurityEnabled();
        final boolean canBeSecure = NativeIO.isAvailable();
        if (!canBeSecure && shouldBeSecure) {
            throw new RuntimeException("Secure IO is not possible without native code extensions.");
        }
        try {
            rawFilesystem = FileSystem.getLocal(new Configuration()).getRaw();
        }
        catch (IOException ie) {
            throw new RuntimeException("Couldn't obtain an instance of RawLocalFileSystem.");
        }
        skipSecurity = !canBeSecure;
    }
    
    public static class AlreadyExistsException extends IOException
    {
        private static final long serialVersionUID = 1L;
        
        public AlreadyExistsException(final String msg) {
            super(msg);
        }
        
        public AlreadyExistsException(final Throwable cause) {
            super(cause);
        }
    }
}
