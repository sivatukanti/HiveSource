// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.FileNotFoundException;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import org.apache.hadoop.fs.FileUtil;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.LocalFileSystem;
import java.io.File;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DiskChecker
{
    public static final Logger LOG;
    private static AtomicReference<FileIoProvider> fileIoProvider;
    private static final String DISK_IO_FILE_PREFIX = "DiskChecker.OK_TO_DELETE_.";
    @VisibleForTesting
    static final int DISK_IO_MAX_ITERATIONS = 3;
    
    public static void checkDir(final File dir) throws DiskErrorException {
        checkDirInternal(dir);
    }
    
    public static void checkDirWithDiskIo(final File dir) throws DiskErrorException {
        checkDirInternal(dir);
        doDiskIo(dir);
    }
    
    private static void checkDirInternal(final File dir) throws DiskErrorException {
        if (!mkdirsWithExistsCheck(dir)) {
            throw new DiskErrorException("Cannot create directory: " + dir.toString());
        }
        checkAccessByFileMethods(dir);
    }
    
    public static void checkDir(final LocalFileSystem localFS, final Path dir, final FsPermission expected) throws DiskErrorException, IOException {
        checkDirInternal(localFS, dir, expected);
    }
    
    public static void checkDirWithDiskIo(final LocalFileSystem localFS, final Path dir, final FsPermission expected) throws DiskErrorException, IOException {
        checkDirInternal(localFS, dir, expected);
        doDiskIo(localFS.pathToFile(dir));
    }
    
    private static void checkDirInternal(final LocalFileSystem localFS, final Path dir, final FsPermission expected) throws DiskErrorException, IOException {
        mkdirsWithExistsAndPermissionCheck(localFS, dir, expected);
        checkAccessByFileMethods(localFS.pathToFile(dir));
    }
    
    private static void checkAccessByFileMethods(final File dir) throws DiskErrorException {
        if (!dir.isDirectory()) {
            throw new DiskErrorException("Not a directory: " + dir.toString());
        }
        if (!FileUtil.canRead(dir)) {
            throw new DiskErrorException("Directory is not readable: " + dir.toString());
        }
        if (!FileUtil.canWrite(dir)) {
            throw new DiskErrorException("Directory is not writable: " + dir.toString());
        }
        if (!FileUtil.canExecute(dir)) {
            throw new DiskErrorException("Directory is not executable: " + dir.toString());
        }
    }
    
    private static boolean mkdirsWithExistsCheck(final File dir) {
        if (dir.mkdir() || dir.exists()) {
            return true;
        }
        File canonDir;
        try {
            canonDir = dir.getCanonicalFile();
        }
        catch (IOException e) {
            return false;
        }
        final String parent = canonDir.getParent();
        return parent != null && mkdirsWithExistsCheck(new File(parent)) && (canonDir.mkdir() || canonDir.exists());
    }
    
    static void mkdirsWithExistsAndPermissionCheck(final LocalFileSystem localFS, final Path dir, final FsPermission expected) throws IOException {
        final File directory = localFS.pathToFile(dir);
        boolean created = false;
        if (!directory.exists()) {
            created = mkdirsWithExistsCheck(directory);
        }
        if (created || !localFS.getFileStatus(dir).getPermission().equals(expected)) {
            localFS.setPermission(dir, expected);
        }
    }
    
    private static void doDiskIo(final File dir) throws DiskErrorException {
        try {
            IOException ioe = null;
            int i = 0;
            while (i < 3) {
                final File file = getFileNameForDiskIoCheck(dir, i + 1);
                try {
                    diskIoCheckWithoutNativeIo(file);
                    return;
                }
                catch (IOException e) {
                    ioe = e;
                    ++i;
                    continue;
                }
                break;
            }
            throw ioe;
        }
        catch (IOException e2) {
            throw new DiskErrorException("Error checking directory " + dir, e2);
        }
    }
    
    private static void diskIoCheckWithoutNativeIo(File file) throws IOException {
        FileOutputStream fos = null;
        try {
            final FileIoProvider provider = DiskChecker.fileIoProvider.get();
            fos = provider.get(file);
            provider.write(fos, new byte[1]);
            fos.getFD().sync();
            fos.close();
            fos = null;
            if (!file.delete() && file.exists()) {
                throw new IOException("Failed to delete " + file);
            }
            file = null;
        }
        finally {
            IOUtils.cleanup(null, fos);
            FileUtils.deleteQuietly(file);
        }
    }
    
    @VisibleForTesting
    static File getFileNameForDiskIoCheck(final File dir, final int iterationCount) {
        if (iterationCount < 3) {
            return new File(dir, "DiskChecker.OK_TO_DELETE_." + String.format("%03d", iterationCount));
        }
        return new File(dir, "DiskChecker.OK_TO_DELETE_." + UUID.randomUUID());
    }
    
    @VisibleForTesting
    static FileIoProvider replaceFileOutputStreamProvider(final FileIoProvider newFosProvider) {
        return DiskChecker.fileIoProvider.getAndSet(newFosProvider);
    }
    
    @VisibleForTesting
    static FileIoProvider getFileOutputStreamProvider() {
        return DiskChecker.fileIoProvider.get();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DiskChecker.class);
        DiskChecker.fileIoProvider = new AtomicReference<FileIoProvider>(new DefaultFileIoProvider());
    }
    
    public static class DiskErrorException extends IOException
    {
        public DiskErrorException(final String msg) {
            super(msg);
        }
        
        public DiskErrorException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
    
    public static class DiskOutOfSpaceException extends IOException
    {
        public DiskOutOfSpaceException(final String msg) {
            super(msg);
        }
    }
    
    private static class DefaultFileIoProvider implements FileIoProvider
    {
        @Override
        public FileOutputStream get(final File f) throws FileNotFoundException {
            return new FileOutputStream(f);
        }
        
        @Override
        public void write(final FileOutputStream fos, final byte[] data) throws IOException {
            fos.write(data);
        }
    }
    
    interface FileIoProvider
    {
        FileOutputStream get(final File p0) throws FileNotFoundException;
        
        void write(final FileOutputStream p0, final byte[] p1) throws IOException;
    }
}
