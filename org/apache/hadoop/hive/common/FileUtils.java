// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.hadoop.util.Shell;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hive.shims.HadoopShims;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.LocalFileSystem;
import java.net.URISyntaxException;
import org.apache.hadoop.hive.conf.HiveConf;
import java.security.AccessControlException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileStatus;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import java.util.BitSet;
import org.apache.hadoop.fs.PathFilter;
import org.apache.commons.logging.Log;

public final class FileUtils
{
    private static final Log LOG;
    public static final PathFilter HIDDEN_FILES_PATH_FILTER;
    public static final PathFilter STAGING_DIR_PATH_FILTER;
    static BitSet charToEscape;
    
    public static Path makeQualified(final Path path, final Configuration conf) throws IOException {
        if (!path.isAbsolute()) {
            return path.makeQualified(FileSystem.get(conf));
        }
        final URI fsUri = FileSystem.getDefaultUri(conf);
        final URI pathUri = path.toUri();
        String scheme = pathUri.getScheme();
        String authority = pathUri.getAuthority();
        if (scheme == null) {
            scheme = fsUri.getScheme();
            authority = fsUri.getAuthority();
            if (authority == null) {
                authority = "";
            }
        }
        else if (authority == null) {
            if (scheme.equals(fsUri.getScheme()) && fsUri.getAuthority() != null) {
                authority = fsUri.getAuthority();
            }
            else {
                authority = "";
            }
        }
        return new Path(scheme, authority, pathUri.getPath());
    }
    
    private FileUtils() {
    }
    
    public static String makePartName(final List<String> partCols, final List<String> vals) {
        return makePartName(partCols, vals, null);
    }
    
    public static String makePartName(final List<String> partCols, final List<String> vals, final String defaultStr) {
        final StringBuilder name = new StringBuilder();
        for (int i = 0; i < partCols.size(); ++i) {
            if (i > 0) {
                name.append("/");
            }
            name.append(escapePathName(partCols.get(i).toLowerCase(), defaultStr));
            name.append('=');
            name.append(escapePathName(vals.get(i), defaultStr));
        }
        return name.toString();
    }
    
    public static String makeDefaultListBucketingDirName(final List<String> skewedCols, final String name) {
        final String defaultDir = escapePathName(name);
        final StringBuilder defaultDirPath = new StringBuilder();
        for (int i = 0; i < skewedCols.size(); ++i) {
            if (i > 0) {
                defaultDirPath.append("/");
            }
            defaultDirPath.append(defaultDir);
        }
        final String lbDirName = defaultDirPath.toString();
        return lbDirName;
    }
    
    public static String makeListBucketingDirName(final List<String> lbCols, final List<String> vals) {
        final StringBuilder name = new StringBuilder();
        for (int i = 0; i < lbCols.size(); ++i) {
            if (i > 0) {
                name.append("/");
            }
            name.append(escapePathName(lbCols.get(i).toLowerCase()));
            name.append('=');
            name.append(escapePathName(vals.get(i)));
        }
        return name.toString();
    }
    
    static boolean needsEscaping(final char c) {
        return c >= '\0' && c < FileUtils.charToEscape.size() && FileUtils.charToEscape.get(c);
    }
    
    public static String escapePathName(final String path) {
        return escapePathName(path, null);
    }
    
    public static String escapePathName(final String path, final String defaultPath) {
        if (path != null && path.length() != 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length(); ++i) {
                final char c = path.charAt(i);
                if (needsEscaping(c)) {
                    sb.append('%');
                    sb.append(String.format("%1$02X", (int)c));
                }
                else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        if (defaultPath == null) {
            return "__HIVE_DEFAULT_PARTITION__";
        }
        return defaultPath;
    }
    
    public static String unescapePathName(final String path) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length(); ++i) {
            final char c = path.charAt(i);
            if (c == '%' && i + 2 < path.length()) {
                int code = -1;
                try {
                    code = Integer.valueOf(path.substring(i + 1, i + 3), 16);
                }
                catch (Exception e) {
                    code = -1;
                }
                if (code >= 0) {
                    sb.append((char)code);
                    i += 2;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    public static void listStatusRecursively(final FileSystem fs, final FileStatus fileStatus, final List<FileStatus> results) throws IOException {
        if (fileStatus.isDir()) {
            for (final FileStatus stat : fs.listStatus(fileStatus.getPath(), FileUtils.HIDDEN_FILES_PATH_FILTER)) {
                listStatusRecursively(fs, stat, results);
            }
        }
        else {
            results.add(fileStatus);
        }
    }
    
    public static FileStatus getPathOrParentThatExists(final FileSystem fs, final Path path) throws IOException {
        final FileStatus stat = getFileStatusOrNull(fs, path);
        if (stat != null) {
            return stat;
        }
        final Path parentPath = path.getParent();
        return getPathOrParentThatExists(fs, parentPath);
    }
    
    public static void checkFileAccessWithImpersonation(final FileSystem fs, final FileStatus stat, final FsAction action, final String user) throws IOException, AccessControlException, InterruptedException, Exception {
        final UserGroupInformation ugi = Utils.getUGI();
        final String currentUser = ugi.getShortUserName();
        if (user == null || currentUser.equals(user)) {
            ShimLoader.getHadoopShims().checkFileAccess(fs, stat, action);
            return;
        }
        final UserGroupInformation proxyUser = UserGroupInformation.createProxyUser(user, UserGroupInformation.getLoginUser());
        proxyUser.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final FileSystem fsAsUser = FileSystem.get(fs.getUri(), fs.getConf());
                ShimLoader.getHadoopShims().checkFileAccess(fsAsUser, stat, action);
                return null;
            }
        });
    }
    
    public static boolean isActionPermittedForFileHierarchy(final FileSystem fs, final FileStatus fileStatus, final String userName, final FsAction action) throws Exception {
        final boolean isDir = fileStatus.isDir();
        final FsAction dirActionNeeded = action;
        if (isDir) {
            dirActionNeeded.and(FsAction.EXECUTE);
        }
        try {
            checkFileAccessWithImpersonation(fs, fileStatus, action, userName);
        }
        catch (AccessControlException err) {
            return false;
        }
        if (!isDir) {
            return true;
        }
        final FileStatus[] listStatus;
        final FileStatus[] childStatuses = listStatus = fs.listStatus(fileStatus.getPath());
        for (final FileStatus childStatus : listStatus) {
            if (!isActionPermittedForFileHierarchy(fs, childStatus, userName, action)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isLocalFile(final HiveConf conf, final String fileName) {
        try {
            return isLocalFile(conf, new URI(fileName));
        }
        catch (URISyntaxException e) {
            FileUtils.LOG.warn("Unable to create URI from " + fileName, e);
            return false;
        }
    }
    
    public static boolean isLocalFile(final HiveConf conf, final URI fileUri) {
        try {
            final FileSystem fsForFile = FileSystem.get(fileUri, conf);
            return LocalFileSystem.class.isInstance(fsForFile);
        }
        catch (IOException e) {
            FileUtils.LOG.warn("Unable to get FileSystem for " + fileUri, e);
            return false;
        }
    }
    
    public static boolean isOwnerOfFileHierarchy(final FileSystem fs, final FileStatus fileStatus, final String userName) throws IOException {
        if (!fileStatus.getOwner().equals(userName)) {
            return false;
        }
        if (!fileStatus.isDir()) {
            return true;
        }
        final FileStatus[] listStatus;
        final FileStatus[] childStatuses = listStatus = fs.listStatus(fileStatus.getPath());
        for (final FileStatus childStatus : listStatus) {
            if (!isOwnerOfFileHierarchy(fs, childStatus, userName)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean mkdir(final FileSystem fs, final Path f, final boolean inheritPerms, final Configuration conf) throws IOException {
        FileUtils.LOG.info("Creating directory if it doesn't exist: " + f);
        if (!inheritPerms) {
            return fs.mkdirs(f);
        }
        try {
            return fs.getFileStatus(f).isDir();
        }
        catch (FileNotFoundException ex) {
            Path lastExistingParent = f;
            Path firstNonExistentParent = null;
            while (!fs.exists(lastExistingParent)) {
                firstNonExistentParent = lastExistingParent;
                lastExistingParent = lastExistingParent.getParent();
            }
            final boolean success = fs.mkdirs(f);
            if (!success) {
                return false;
            }
            final HadoopShims shim = ShimLoader.getHadoopShims();
            final HadoopShims.HdfsFileStatus fullFileStatus = shim.getFullFileStatus(conf, fs, lastExistingParent);
            try {
                shim.setFullFileStatus(conf, fullFileStatus, fs, firstNonExistentParent);
            }
            catch (Exception e) {
                FileUtils.LOG.warn("Error setting permissions of " + firstNonExistentParent, e);
            }
            return true;
        }
    }
    
    public static boolean copy(final FileSystem srcFS, final Path src, final FileSystem dstFS, final Path dst, final boolean deleteSource, final boolean overwrite, final HiveConf conf) throws IOException {
        final HadoopShims shims = ShimLoader.getHadoopShims();
        boolean copied;
        if (srcFS.getUri().getScheme().equals("hdfs") && srcFS.getFileStatus(src).getLen() > conf.getLongVar(HiveConf.ConfVars.HIVE_EXEC_COPYFILE_MAXSIZE)) {
            FileUtils.LOG.info("Source is " + srcFS.getFileStatus(src).getLen() + " bytes. (MAX: " + conf.getLongVar(HiveConf.ConfVars.HIVE_EXEC_COPYFILE_MAXSIZE) + ")");
            FileUtils.LOG.info("Launch distributed copy (distcp) job.");
            copied = shims.runDistCp(src, dst, conf);
            if (copied && deleteSource) {
                srcFS.delete(src, true);
            }
        }
        else {
            copied = FileUtil.copy(srcFS, src, dstFS, dst, deleteSource, overwrite, conf);
        }
        final boolean inheritPerms = conf.getBoolVar(HiveConf.ConfVars.HIVE_WAREHOUSE_SUBDIR_INHERIT_PERMS);
        if (copied && inheritPerms) {
            final HadoopShims.HdfsFileStatus fullFileStatus = shims.getFullFileStatus(conf, dstFS, dst);
            try {
                shims.setFullFileStatus(conf, fullFileStatus, dstFS, dst);
            }
            catch (Exception e) {
                FileUtils.LOG.warn("Error setting permissions or group of " + dst, e);
            }
        }
        return copied;
    }
    
    public static boolean trashFilesUnderDir(final FileSystem fs, final Path f, final Configuration conf) throws FileNotFoundException, IOException {
        final FileStatus[] statuses = fs.listStatus(f, FileUtils.HIDDEN_FILES_PATH_FILTER);
        boolean result = true;
        for (final FileStatus status : statuses) {
            result &= moveToTrash(fs, status.getPath(), conf);
        }
        return result;
    }
    
    public static boolean moveToTrash(final FileSystem fs, final Path f, final Configuration conf) throws IOException {
        FileUtils.LOG.info("deleting  " + f);
        final HadoopShims hadoopShim = ShimLoader.getHadoopShims();
        if (hadoopShim.moveToAppropriateTrash(fs, f, conf)) {
            FileUtils.LOG.info("Moved to trash: " + f);
            return true;
        }
        final boolean result = fs.delete(f, true);
        if (!result) {
            FileUtils.LOG.error("Failed to delete " + f);
        }
        return result;
    }
    
    public static boolean isSubDir(final Path p1, final Path p2, final FileSystem fs) {
        final String path1 = fs.makeQualified(p1).toString();
        final String path2 = fs.makeQualified(p2).toString();
        return path1.startsWith(path2);
    }
    
    public static boolean renameWithPerms(final FileSystem fs, final Path sourcePath, final Path destPath, final boolean inheritPerms, final Configuration conf) throws IOException {
        FileUtils.LOG.info("Renaming " + sourcePath + " to " + destPath);
        if (!inheritPerms) {
            return fs.rename(sourcePath, destPath);
        }
        if (fs.rename(sourcePath, destPath)) {
            final HadoopShims shims = ShimLoader.getHadoopShims();
            final HadoopShims.HdfsFileStatus fullFileStatus = shims.getFullFileStatus(conf, fs, destPath.getParent());
            try {
                shims.setFullFileStatus(conf, fullFileStatus, fs, destPath);
            }
            catch (Exception e) {
                FileUtils.LOG.warn("Error setting permissions or group of " + destPath, e);
            }
            return true;
        }
        return false;
    }
    
    public static boolean equalsFileSystem(final FileSystem fs1, final FileSystem fs2) {
        return fs1.getUri().equals(fs2.getUri());
    }
    
    public static void checkDeletePermission(final Path path, final Configuration conf, final String user) throws AccessControlException, InterruptedException, Exception {
        if (path == null) {
            return;
        }
        final FileSystem fs = path.getFileSystem(conf);
        FileStatus stat = null;
        try {
            stat = fs.getFileStatus(path);
        }
        catch (FileNotFoundException ex) {}
        if (stat == null) {
            return;
        }
        checkFileAccessWithImpersonation(fs, stat, FsAction.WRITE, user);
        final HadoopShims shims = ShimLoader.getHadoopShims();
        if (!shims.supportStickyBit()) {
            return;
        }
        final FileStatus parStatus = fs.getFileStatus(path.getParent());
        if (!shims.hasStickyBit(parStatus.getPermission())) {
            return;
        }
        if (parStatus.getOwner().equals(user)) {
            return;
        }
        final FileStatus childStatus = fs.getFileStatus(path);
        if (childStatus.getOwner().equals(user)) {
            return;
        }
        final String msg = String.format("Permission Denied: User %s can't delete %s because sticky bit is set on the parent dir and user does not own this file or its parent", user, path);
        throw new IOException(msg);
    }
    
    public static FileStatus getFileStatusOrNull(final FileSystem fs, final Path path) throws IOException {
        try {
            return fs.getFileStatus(path);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
    
    static {
        LOG = LogFactory.getLog(FileUtils.class.getName());
        HIDDEN_FILES_PATH_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path p) {
                final String name = p.getName();
                return !name.startsWith("_") && !name.startsWith(".");
            }
        };
        STAGING_DIR_PATH_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path p) {
                final String name = p.getName();
                return !name.startsWith(".");
            }
        };
        FileUtils.charToEscape = new BitSet(128);
        for (char c = '\0'; c < ' '; ++c) {
            FileUtils.charToEscape.set(c);
        }
        final char[] array;
        final char[] clist = array = new char[] { '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\"', '#', '%', '\'', '*', '/', ':', '=', '?', '\\', '\u007f', '{', '[', ']', '^' };
        for (final char c2 : array) {
            FileUtils.charToEscape.set(c2);
        }
        if (Shell.WINDOWS) {
            final char[] array2;
            final char[] winClist = array2 = new char[] { ' ', '<', '>', '|' };
            for (final char c3 : array2) {
                FileUtils.charToEscape.set(c3);
            }
        }
    }
}
