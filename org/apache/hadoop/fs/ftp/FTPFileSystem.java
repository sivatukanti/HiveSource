// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.ftp;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.commons.net.ftp.FTPFile;
import java.io.OutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FileStatus;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.net.NetUtils;
import java.net.ConnectException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPClient;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.FileSystem;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FTPFileSystem extends FileSystem
{
    public static final Logger LOG;
    public static final int DEFAULT_BUFFER_SIZE = 1048576;
    public static final int DEFAULT_BLOCK_SIZE = 4096;
    public static final String FS_FTP_USER_PREFIX = "fs.ftp.user.";
    public static final String FS_FTP_HOST = "fs.ftp.host";
    public static final String FS_FTP_HOST_PORT = "fs.ftp.host.port";
    public static final String FS_FTP_PASSWORD_PREFIX = "fs.ftp.password.";
    public static final String FS_FTP_DATA_CONNECTION_MODE = "fs.ftp.data.connection.mode";
    public static final String FS_FTP_TRANSFER_MODE = "fs.ftp.transfer.mode";
    public static final String E_SAME_DIRECTORY_ONLY = "only same directory renames are supported";
    private URI uri;
    
    @Override
    public String getScheme() {
        return "ftp";
    }
    
    @Override
    protected int getDefaultPort() {
        return 21;
    }
    
    @Override
    public void initialize(final URI uri, final Configuration conf) throws IOException {
        super.initialize(uri, conf);
        String host = uri.getHost();
        host = ((host == null) ? conf.get("fs.ftp.host", null) : host);
        if (host == null) {
            throw new IOException("Invalid host specified");
        }
        conf.set("fs.ftp.host", host);
        int port = uri.getPort();
        port = ((port == -1) ? 21 : port);
        conf.setInt("fs.ftp.host.port", port);
        String userAndPassword = uri.getUserInfo();
        if (userAndPassword == null) {
            userAndPassword = conf.get("fs.ftp.user." + host, null) + ":" + conf.get("fs.ftp.password." + host, null);
        }
        final String[] userPasswdInfo = userAndPassword.split(":");
        Preconditions.checkState(userPasswdInfo.length > 1, (Object)"Invalid username / password");
        conf.set("fs.ftp.user." + host, userPasswdInfo[0]);
        conf.set("fs.ftp.password." + host, userPasswdInfo[1]);
        this.setConf(conf);
        this.uri = uri;
    }
    
    private FTPClient connect() throws IOException {
        FTPClient client = null;
        final Configuration conf = this.getConf();
        final String host = conf.get("fs.ftp.host");
        final int port = conf.getInt("fs.ftp.host.port", 21);
        final String user = conf.get("fs.ftp.user." + host);
        final String password = conf.get("fs.ftp.password." + host);
        client = new FTPClient();
        client.connect(host, port);
        final int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            throw NetUtils.wrapException(host, port, "(unknown)", 0, new ConnectException("Server response " + reply));
        }
        if (client.login(user, password)) {
            client.setFileTransferMode(this.getTransferMode(conf));
            client.setFileType(2);
            client.setBufferSize(1048576);
            this.setDataConnectionMode(client, conf);
            return client;
        }
        throw new IOException("Login failed on server - " + host + ", port - " + port + " as user '" + user + "'");
    }
    
    @VisibleForTesting
    int getTransferMode(final Configuration conf) {
        final String mode = conf.get("fs.ftp.transfer.mode");
        int ret = 11;
        if (mode == null) {
            return ret;
        }
        final String upper = mode.toUpperCase();
        if (upper.equals("STREAM_TRANSFER_MODE")) {
            ret = 10;
        }
        else if (upper.equals("COMPRESSED_TRANSFER_MODE")) {
            ret = 12;
        }
        else if (!upper.equals("BLOCK_TRANSFER_MODE")) {
            FTPFileSystem.LOG.warn("Cannot parse the value for fs.ftp.transfer.mode: " + mode + ". Using default.");
        }
        return ret;
    }
    
    @VisibleForTesting
    void setDataConnectionMode(final FTPClient client, final Configuration conf) throws IOException {
        final String mode = conf.get("fs.ftp.data.connection.mode");
        if (mode == null) {
            return;
        }
        final String upper = mode.toUpperCase();
        if (upper.equals("PASSIVE_LOCAL_DATA_CONNECTION_MODE")) {
            client.enterLocalPassiveMode();
        }
        else if (upper.equals("PASSIVE_REMOTE_DATA_CONNECTION_MODE")) {
            client.enterRemotePassiveMode();
        }
        else if (!upper.equals("ACTIVE_LOCAL_DATA_CONNECTION_MODE")) {
            FTPFileSystem.LOG.warn("Cannot parse the value for fs.ftp.data.connection.mode: " + mode + ". Using default.");
        }
    }
    
    private void disconnect(final FTPClient client) throws IOException {
        if (client != null) {
            if (!client.isConnected()) {
                throw new FTPException("Client not connected");
            }
            final boolean logoutSuccess = client.logout();
            client.disconnect();
            if (!logoutSuccess) {
                FTPFileSystem.LOG.warn("Logout failed while disconnecting, error code - " + client.getReplyCode());
            }
        }
    }
    
    private Path makeAbsolute(final Path workDir, final Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return new Path(workDir, path);
    }
    
    @Override
    public FSDataInputStream open(final Path file, final int bufferSize) throws IOException {
        final FTPClient client = this.connect();
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        final FileStatus fileStat = this.getFileStatus(client, absolute);
        if (fileStat.isDirectory()) {
            this.disconnect(client);
            throw new FileNotFoundException("Path " + file + " is a directory.");
        }
        client.allocate(bufferSize);
        final Path parent = absolute.getParent();
        client.changeWorkingDirectory(parent.toUri().getPath());
        final InputStream is = client.retrieveFileStream(file.getName());
        final FSDataInputStream fis = new FSDataInputStream(new FTPInputStream(is, client, this.statistics));
        if (!FTPReply.isPositivePreliminary(client.getReplyCode())) {
            fis.close();
            throw new IOException("Unable to open file: " + file + ", Aborting");
        }
        return fis;
    }
    
    @Override
    public FSDataOutputStream create(final Path file, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        final FTPClient client = this.connect();
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        FileStatus status;
        try {
            status = this.getFileStatus(client, file);
        }
        catch (FileNotFoundException fnfe) {
            status = null;
        }
        if (status != null) {
            if (!overwrite || status.isDirectory()) {
                this.disconnect(client);
                throw new FileAlreadyExistsException("File already exists: " + file);
            }
            this.delete(client, file, false);
        }
        Path parent = absolute.getParent();
        if (parent == null || !this.mkdirs(client, parent, FsPermission.getDirDefault())) {
            parent = ((parent == null) ? new Path("/") : parent);
            this.disconnect(client);
            throw new IOException("create(): Mkdirs failed to create: " + parent);
        }
        client.allocate(bufferSize);
        client.changeWorkingDirectory(parent.toUri().getPath());
        final FSDataOutputStream fos = new FSDataOutputStream(client.storeFileStream(file.getName()), this.statistics) {
            @Override
            public void close() throws IOException {
                super.close();
                if (!client.isConnected()) {
                    throw new FTPException("Client not connected");
                }
                final boolean cmdCompleted = client.completePendingCommand();
                FTPFileSystem.this.disconnect(client);
                if (!cmdCompleted) {
                    throw new FTPException("Could not complete transfer, Reply Code - " + client.getReplyCode());
                }
            }
        };
        if (!FTPReply.isPositivePreliminary(client.getReplyCode())) {
            fos.close();
            throw new IOException("Unable to create file: " + file + ", Aborting");
        }
        return fos;
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        throw new UnsupportedOperationException("Append is not supported by FTPFileSystem");
    }
    
    private boolean exists(final FTPClient client, final Path file) throws IOException {
        try {
            this.getFileStatus(client, file);
            return true;
        }
        catch (FileNotFoundException fnfe) {
            return false;
        }
    }
    
    @Override
    public boolean delete(final Path file, final boolean recursive) throws IOException {
        final FTPClient client = this.connect();
        try {
            final boolean success = this.delete(client, file, recursive);
            return success;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    private boolean delete(final FTPClient client, final Path file, final boolean recursive) throws IOException {
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        final String pathName = absolute.toUri().getPath();
        try {
            final FileStatus fileStat = this.getFileStatus(client, absolute);
            if (fileStat.isFile()) {
                return client.deleteFile(pathName);
            }
        }
        catch (FileNotFoundException e) {
            return false;
        }
        final FileStatus[] dirEntries = this.listStatus(client, absolute);
        if (dirEntries != null && dirEntries.length > 0 && !recursive) {
            throw new IOException("Directory: " + file + " is not empty.");
        }
        for (final FileStatus dirEntry : dirEntries) {
            this.delete(client, new Path(absolute, dirEntry.getPath()), recursive);
        }
        return client.removeDirectory(pathName);
    }
    
    @VisibleForTesting
    FsAction getFsAction(final int accessGroup, final FTPFile ftpFile) {
        FsAction action = FsAction.NONE;
        if (ftpFile.hasPermission(accessGroup, 0)) {
            action = action.or(FsAction.READ);
        }
        if (ftpFile.hasPermission(accessGroup, 1)) {
            action = action.or(FsAction.WRITE);
        }
        if (ftpFile.hasPermission(accessGroup, 2)) {
            action = action.or(FsAction.EXECUTE);
        }
        return action;
    }
    
    private FsPermission getPermissions(final FTPFile ftpFile) {
        final FsAction user = this.getFsAction(0, ftpFile);
        final FsAction group = this.getFsAction(1, ftpFile);
        final FsAction others = this.getFsAction(2, ftpFile);
        return new FsPermission(user, group, others);
    }
    
    @Override
    public URI getUri() {
        return this.uri;
    }
    
    @Override
    public FileStatus[] listStatus(final Path file) throws IOException {
        final FTPClient client = this.connect();
        try {
            final FileStatus[] stats = this.listStatus(client, file);
            return stats;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    private FileStatus[] listStatus(final FTPClient client, final Path file) throws IOException {
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        final FileStatus fileStat = this.getFileStatus(client, absolute);
        if (fileStat.isFile()) {
            return new FileStatus[] { fileStat };
        }
        final FTPFile[] ftpFiles = client.listFiles(absolute.toUri().getPath());
        final FileStatus[] fileStats = new FileStatus[ftpFiles.length];
        for (int i = 0; i < ftpFiles.length; ++i) {
            fileStats[i] = this.getFileStatus(ftpFiles[i], absolute);
        }
        return fileStats;
    }
    
    @Override
    public FileStatus getFileStatus(final Path file) throws IOException {
        final FTPClient client = this.connect();
        try {
            final FileStatus status = this.getFileStatus(client, file);
            return status;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    private FileStatus getFileStatus(final FTPClient client, final Path file) throws IOException {
        FileStatus fileStat = null;
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        final Path parentPath = absolute.getParent();
        if (parentPath == null) {
            final long length = -1L;
            final boolean isDir = true;
            final int blockReplication = 1;
            final long blockSize = 4096L;
            final long modTime = -1L;
            final Path root = new Path("/");
            return new FileStatus(length, isDir, blockReplication, blockSize, modTime, this.makeQualified(root));
        }
        final String pathName = parentPath.toUri().getPath();
        final FTPFile[] ftpFiles = client.listFiles(pathName);
        if (ftpFiles == null) {
            throw new FileNotFoundException("File " + file + " does not exist.");
        }
        for (final FTPFile ftpFile : ftpFiles) {
            if (ftpFile.getName().equals(file.getName())) {
                fileStat = this.getFileStatus(ftpFile, parentPath);
                break;
            }
        }
        if (fileStat == null) {
            throw new FileNotFoundException("File " + file + " does not exist.");
        }
        return fileStat;
    }
    
    private FileStatus getFileStatus(final FTPFile ftpFile, final Path parentPath) {
        final long length = ftpFile.getSize();
        final boolean isDir = ftpFile.isDirectory();
        final int blockReplication = 1;
        final long blockSize = 4096L;
        final long modTime = ftpFile.getTimestamp().getTimeInMillis();
        final long accessTime = 0L;
        final FsPermission permission = this.getPermissions(ftpFile);
        final String user = ftpFile.getUser();
        final String group = ftpFile.getGroup();
        final Path filePath = new Path(parentPath, ftpFile.getName());
        return new FileStatus(length, isDir, blockReplication, blockSize, modTime, accessTime, permission, user, group, this.makeQualified(filePath));
    }
    
    @Override
    public boolean mkdirs(final Path file, final FsPermission permission) throws IOException {
        final FTPClient client = this.connect();
        try {
            final boolean success = this.mkdirs(client, file, permission);
            return success;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    private boolean mkdirs(final FTPClient client, final Path file, final FsPermission permission) throws IOException {
        boolean created = true;
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absolute = this.makeAbsolute(workDir, file);
        final String pathName = absolute.getName();
        if (!this.exists(client, absolute)) {
            final Path parent = absolute.getParent();
            created = (parent == null || this.mkdirs(client, parent, FsPermission.getDirDefault()));
            if (created) {
                final String parentDir = parent.toUri().getPath();
                client.changeWorkingDirectory(parentDir);
                created = (created && client.makeDirectory(pathName));
            }
        }
        else if (this.isFile(client, absolute)) {
            throw new ParentNotDirectoryException(String.format("Can't make directory for path %s since it is a file.", absolute));
        }
        return created;
    }
    
    private boolean isFile(final FTPClient client, final Path file) {
        try {
            return this.getFileStatus(client, file).isFile();
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException ioe) {
            throw new FTPException("File check failed", ioe);
        }
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        final FTPClient client = this.connect();
        try {
            final boolean success = this.rename(client, src, dst);
            return success;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    private boolean isParentOf(final Path parent, final Path child) {
        final URI parentURI = parent.toUri();
        String parentPath = parentURI.getPath();
        if (!parentPath.endsWith("/")) {
            parentPath += "/";
        }
        final URI childURI = child.toUri();
        final String childPath = childURI.getPath();
        return childPath.startsWith(parentPath);
    }
    
    private boolean rename(final FTPClient client, final Path src, final Path dst) throws IOException {
        final Path workDir = new Path(client.printWorkingDirectory());
        final Path absoluteSrc = this.makeAbsolute(workDir, src);
        Path absoluteDst = this.makeAbsolute(workDir, dst);
        if (!this.exists(client, absoluteSrc)) {
            throw new FileNotFoundException("Source path " + src + " does not exist");
        }
        if (this.isDirectory(absoluteDst)) {
            absoluteDst = new Path(absoluteDst, absoluteSrc.getName());
        }
        if (this.exists(client, absoluteDst)) {
            throw new FileAlreadyExistsException("Destination path " + dst + " already exists");
        }
        final String parentSrc = absoluteSrc.getParent().toUri().toString();
        final String parentDst = absoluteDst.getParent().toUri().toString();
        if (this.isParentOf(absoluteSrc, absoluteDst)) {
            throw new IOException("Cannot rename " + absoluteSrc + " under itself : " + absoluteDst);
        }
        if (!parentSrc.equals(parentDst)) {
            throw new IOException("Cannot rename source: " + absoluteSrc + " to " + absoluteDst + " -" + "only same directory renames are supported");
        }
        final String from = absoluteSrc.getName();
        final String to = absoluteDst.getName();
        client.changeWorkingDirectory(parentSrc);
        final boolean renamed = client.rename(from, to);
        return renamed;
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.getHomeDirectory();
    }
    
    @Override
    public Path getHomeDirectory() {
        FTPClient client = null;
        try {
            client = this.connect();
            final Path homeDir = new Path(client.printWorkingDirectory());
            return homeDir;
        }
        catch (IOException ioe) {
            throw new FTPException("Failed to get home directory", ioe);
        }
        finally {
            try {
                this.disconnect(client);
            }
            catch (IOException ioe2) {
                throw new FTPException("Failed to disconnect", ioe2);
            }
        }
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
    }
    
    static {
        LOG = LoggerFactory.getLogger(FTPFileSystem.class);
    }
}
