// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FileUtils;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.Options;
import java.security.PrivilegedExceptionAction;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import org.apache.hadoop.util.RunJar;
import java.io.File;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import java.util.concurrent.ExecutionException;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileSystem;
import com.google.common.util.concurrent.Futures;
import com.google.common.cache.CacheLoader;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FileStatus;
import java.util.concurrent.Future;
import com.google.common.cache.LoadingCache;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.FileContext;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.Path;
import java.util.concurrent.Callable;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class FSDownload implements Callable<Path>
{
    private static final Log LOG;
    private FileContext files;
    private final UserGroupInformation userUgi;
    private Configuration conf;
    private LocalResource resource;
    private final LoadingCache<Path, Future<FileStatus>> statCache;
    private Path destDirPath;
    private static final FsPermission cachePerms;
    static final FsPermission PUBLIC_FILE_PERMS;
    static final FsPermission PRIVATE_FILE_PERMS;
    static final FsPermission PUBLIC_DIR_PERMS;
    static final FsPermission PRIVATE_DIR_PERMS;
    
    public FSDownload(final FileContext files, final UserGroupInformation ugi, final Configuration conf, final Path destDirPath, final LocalResource resource) {
        this(files, ugi, conf, destDirPath, resource, null);
    }
    
    public FSDownload(final FileContext files, final UserGroupInformation ugi, final Configuration conf, final Path destDirPath, final LocalResource resource, final LoadingCache<Path, Future<FileStatus>> statCache) {
        this.conf = conf;
        this.destDirPath = destDirPath;
        this.files = files;
        this.userUgi = ugi;
        this.resource = resource;
        this.statCache = statCache;
    }
    
    LocalResource getResource() {
        return this.resource;
    }
    
    private void createDir(final Path path, final FsPermission perm) throws IOException {
        this.files.mkdir(path, perm, false);
        if (!perm.equals(this.files.getUMask().applyUMask(perm))) {
            this.files.setPermission(path, perm);
        }
    }
    
    public static CacheLoader<Path, Future<FileStatus>> createStatusCacheLoader(final Configuration conf) {
        return new CacheLoader<Path, Future<FileStatus>>() {
            @Override
            public Future<FileStatus> load(final Path path) {
                try {
                    final FileSystem fs = path.getFileSystem(conf);
                    return Futures.immediateFuture(fs.getFileStatus(path));
                }
                catch (Throwable th) {
                    return (Future<FileStatus>)Futures.immediateFailedFuture(th);
                }
            }
        };
    }
    
    @VisibleForTesting
    static boolean isPublic(final FileSystem fs, Path current, final FileStatus sStat, final LoadingCache<Path, Future<FileStatus>> statCache) throws IOException {
        current = fs.makeQualified(current);
        return checkPublicPermsForAll(fs, sStat, FsAction.READ_EXECUTE, FsAction.READ) && ((Shell.WINDOWS && fs instanceof LocalFileSystem) || ancestorsHaveExecutePermissions(fs, current.getParent(), statCache));
    }
    
    private static boolean checkPublicPermsForAll(final FileSystem fs, final FileStatus status, final FsAction dir, final FsAction file) throws IOException {
        final FsPermission perms = status.getPermission();
        final FsAction otherAction = perms.getOtherAction();
        if (!status.isDirectory()) {
            return otherAction.implies(file);
        }
        if (!otherAction.implies(dir)) {
            return false;
        }
        for (final FileStatus child : fs.listStatus(status.getPath())) {
            if (!checkPublicPermsForAll(fs, child, dir, file)) {
                return false;
            }
        }
        return true;
    }
    
    @VisibleForTesting
    static boolean ancestorsHaveExecutePermissions(final FileSystem fs, final Path path, final LoadingCache<Path, Future<FileStatus>> statCache) throws IOException {
        for (Path current = path; current != null; current = current.getParent()) {
            if (!checkPermissionOfOther(fs, current, FsAction.EXECUTE, statCache)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean checkPermissionOfOther(final FileSystem fs, final Path path, final FsAction action, final LoadingCache<Path, Future<FileStatus>> statCache) throws IOException {
        final FileStatus status = getFileStatus(fs, path, statCache);
        final FsPermission perms = status.getPermission();
        final FsAction otherAction = perms.getOtherAction();
        return otherAction.implies(action);
    }
    
    private static FileStatus getFileStatus(final FileSystem fs, final Path path, final LoadingCache<Path, Future<FileStatus>> statCache) throws IOException {
        if (statCache == null) {
            return fs.getFileStatus(path);
        }
        try {
            return statCache.get(path).get();
        }
        catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new IOException(cause);
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw new IOException(e2);
        }
    }
    
    private Path copy(final Path sCopy, final Path dstdir) throws IOException {
        final FileSystem sourceFs = sCopy.getFileSystem(this.conf);
        final Path dCopy = new Path(dstdir, "tmp_" + sCopy.getName());
        final FileStatus sStat = sourceFs.getFileStatus(sCopy);
        if (sStat.getModificationTime() != this.resource.getTimestamp()) {
            throw new IOException("Resource " + sCopy + " changed on src filesystem (expected " + this.resource.getTimestamp() + ", was " + sStat.getModificationTime());
        }
        if (this.resource.getVisibility() == LocalResourceVisibility.PUBLIC && !isPublic(sourceFs, sCopy, sStat, this.statCache)) {
            throw new IOException("Resource " + sCopy + " is not publicly accessable and as such cannot be part of the" + " public cache.");
        }
        FileUtil.copy(sourceFs, sStat, FileSystem.getLocal(this.conf), dCopy, false, true, this.conf);
        return dCopy;
    }
    
    private long unpack(final File localrsrc, final File dst) throws IOException {
        switch (this.resource.getType()) {
            case ARCHIVE: {
                final String lowerDst = dst.getName().toLowerCase();
                if (lowerDst.endsWith(".jar")) {
                    RunJar.unJar(localrsrc, dst);
                }
                else if (lowerDst.endsWith(".zip")) {
                    FileUtil.unZip(localrsrc, dst);
                }
                else if (lowerDst.endsWith(".tar.gz") || lowerDst.endsWith(".tgz") || lowerDst.endsWith(".tar")) {
                    FileUtil.unTar(localrsrc, dst);
                }
                else {
                    FSDownload.LOG.warn("Cannot unpack " + localrsrc);
                    if (!localrsrc.renameTo(dst)) {
                        throw new IOException("Unable to rename file: [" + localrsrc + "] to [" + dst + "]");
                    }
                }
                break;
            }
            case PATTERN: {
                final String lowerDst = dst.getName().toLowerCase();
                if (lowerDst.endsWith(".jar")) {
                    final String p = this.resource.getPattern();
                    RunJar.unJar(localrsrc, dst, (p == null) ? RunJar.MATCH_ANY : Pattern.compile(p));
                    final File newDst = new File(dst, dst.getName());
                    if (!dst.exists() && !dst.mkdir()) {
                        throw new IOException("Unable to create directory: [" + dst + "]");
                    }
                    if (!localrsrc.renameTo(newDst)) {
                        throw new IOException("Unable to rename file: [" + localrsrc + "] to [" + newDst + "]");
                    }
                }
                else if (lowerDst.endsWith(".zip")) {
                    FSDownload.LOG.warn("Treating [" + localrsrc + "] as an archive even though it " + "was specified as PATTERN");
                    FileUtil.unZip(localrsrc, dst);
                }
                else if (lowerDst.endsWith(".tar.gz") || lowerDst.endsWith(".tgz") || lowerDst.endsWith(".tar")) {
                    FSDownload.LOG.warn("Treating [" + localrsrc + "] as an archive even though it " + "was specified as PATTERN");
                    FileUtil.unTar(localrsrc, dst);
                }
                else {
                    FSDownload.LOG.warn("Cannot unpack " + localrsrc);
                    if (!localrsrc.renameTo(dst)) {
                        throw new IOException("Unable to rename file: [" + localrsrc + "] to [" + dst + "]");
                    }
                }
                break;
            }
            default: {
                if (!localrsrc.renameTo(dst)) {
                    throw new IOException("Unable to rename file: [" + localrsrc + "] to [" + dst + "]");
                }
                break;
            }
        }
        if (localrsrc.isFile()) {
            try {
                this.files.delete(new Path(localrsrc.toString()), false);
            }
            catch (IOException ex) {}
        }
        return 0L;
    }
    
    @Override
    public Path call() throws Exception {
        Path sCopy;
        try {
            sCopy = ConverterUtils.getPathFromYarnURL(this.resource.getResource());
        }
        catch (URISyntaxException e) {
            throw new IOException("Invalid resource", e);
        }
        this.createDir(this.destDirPath, FSDownload.cachePerms);
        final Path dst_work = new Path(this.destDirPath + "_tmp");
        this.createDir(dst_work, FSDownload.cachePerms);
        final Path dFinal = this.files.makeQualified(new Path(dst_work, sCopy.getName()));
        try {
            final Path dTmp = (null == this.userUgi) ? this.files.makeQualified(this.copy(sCopy, dst_work)) : this.userUgi.doAs((PrivilegedExceptionAction<Path>)new PrivilegedExceptionAction<Path>() {
                @Override
                public Path run() throws Exception {
                    return FSDownload.this.files.makeQualified(FSDownload.this.copy(sCopy, dst_work));
                }
            });
            this.unpack(new File(dTmp.toUri()), new File(dFinal.toUri()));
            this.changePermissions(dFinal.getFileSystem(this.conf), dFinal);
            this.files.rename(dst_work, this.destDirPath, Options.Rename.OVERWRITE);
        }
        catch (Exception e2) {
            try {
                this.files.delete(this.destDirPath, true);
            }
            catch (IOException ex) {}
            throw e2;
        }
        finally {
            try {
                this.files.delete(dst_work, true);
            }
            catch (FileNotFoundException ex2) {}
            this.conf = null;
            this.resource = null;
        }
        return this.files.makeQualified(new Path(this.destDirPath, sCopy.getName()));
    }
    
    private void changePermissions(final FileSystem fs, final Path path) throws IOException, InterruptedException {
        final File f = new File(path.toUri());
        if (FileUtils.isSymlink(f)) {
            return;
        }
        final boolean isDir = f.isDirectory();
        FsPermission perm = FSDownload.cachePerms;
        if (this.resource.getVisibility() == LocalResourceVisibility.PUBLIC) {
            perm = (isDir ? FSDownload.PUBLIC_DIR_PERMS : FSDownload.PUBLIC_FILE_PERMS);
        }
        else {
            perm = (isDir ? FSDownload.PRIVATE_DIR_PERMS : FSDownload.PRIVATE_FILE_PERMS);
        }
        FSDownload.LOG.debug("Changing permissions for path " + path + " to perm " + perm);
        final FsPermission fPerm = perm;
        if (null == this.userUgi) {
            this.files.setPermission(path, perm);
        }
        else {
            this.userUgi.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    FSDownload.this.files.setPermission(path, fPerm);
                    return null;
                }
            });
        }
        if (isDir) {
            final FileStatus[] arr$;
            final FileStatus[] statuses = arr$ = fs.listStatus(path);
            for (final FileStatus status : arr$) {
                this.changePermissions(fs, status.getPath());
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(FSDownload.class);
        cachePerms = new FsPermission((short)493);
        PUBLIC_FILE_PERMS = new FsPermission((short)365);
        PRIVATE_FILE_PERMS = new FsPermission((short)320);
        PUBLIC_DIR_PERMS = new FsPermission((short)493);
        PRIVATE_DIR_PERMS = new FsPermission((short)448);
    }
}
