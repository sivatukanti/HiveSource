// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.sftp;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import java.io.InputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import java.util.ArrayList;
import org.apache.hadoop.fs.permission.FsPermission;
import com.jcraft.jsch.SftpATTRS;
import java.util.Iterator;
import java.util.Vector;
import com.jcraft.jsch.SftpException;
import org.apache.hadoop.fs.FileStatus;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.Path;
import com.jcraft.jsch.ChannelSftp;
import java.net.URLDecoder;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.hadoop.fs.FileSystem;

public class SFTPFileSystem extends FileSystem
{
    public static final Logger LOG;
    private SFTPConnectionPool connectionPool;
    private URI uri;
    private static final int DEFAULT_SFTP_PORT = 22;
    private static final int DEFAULT_MAX_CONNECTION = 5;
    public static final int DEFAULT_BUFFER_SIZE = 1048576;
    public static final int DEFAULT_BLOCK_SIZE = 4096;
    public static final String FS_SFTP_USER_PREFIX = "fs.sftp.user.";
    public static final String FS_SFTP_PASSWORD_PREFIX = "fs.sftp.password.";
    public static final String FS_SFTP_HOST = "fs.sftp.host";
    public static final String FS_SFTP_HOST_PORT = "fs.sftp.host.port";
    public static final String FS_SFTP_KEYFILE = "fs.sftp.keyfile";
    public static final String FS_SFTP_CONNECTION_MAX = "fs.sftp.connection.max";
    public static final String E_SAME_DIRECTORY_ONLY = "only same directory renames are supported";
    public static final String E_HOST_NULL = "Invalid host specified";
    public static final String E_USER_NULL = "No user specified for sftp connection. Expand URI or credential file.";
    public static final String E_PATH_DIR = "Path %s is a directory.";
    public static final String E_FILE_STATUS = "Failed to get file status";
    public static final String E_FILE_NOTFOUND = "File %s does not exist.";
    public static final String E_FILE_EXIST = "File already exists: %s";
    public static final String E_CREATE_DIR = "create(): Mkdirs failed to create: %s";
    public static final String E_DIR_CREATE_FROMFILE = "Can't make directory for path %s since it is a file.";
    public static final String E_MAKE_DIR_FORPATH = "Can't make directory for path \"%s\" under \"%s\".";
    public static final String E_DIR_NOTEMPTY = "Directory: %s is not empty.";
    public static final String E_FILE_CHECK_FAILED = "File check failed";
    public static final String E_SPATH_NOTEXIST = "Source path %s does not exist";
    public static final String E_DPATH_EXIST = "Destination path %s already exist, cannot rename!";
    public static final String E_FAILED_GETHOME = "Failed to get home directory";
    public static final String E_FAILED_DISCONNECT = "Failed to disconnect";
    
    private void setConfigurationFromURI(final URI uriInfo, final Configuration conf) throws IOException {
        String host = uriInfo.getHost();
        host = ((host == null) ? conf.get("fs.sftp.host", null) : host);
        if (host == null) {
            throw new IOException("Invalid host specified");
        }
        conf.set("fs.sftp.host", host);
        int port = uriInfo.getPort();
        port = ((port == -1) ? conf.getInt("fs.sftp.host.port", 22) : port);
        conf.setInt("fs.sftp.host.port", port);
        final String userAndPwdFromUri = uriInfo.getUserInfo();
        if (userAndPwdFromUri != null) {
            final String[] userPasswdInfo = userAndPwdFromUri.split(":");
            String user = userPasswdInfo[0];
            user = URLDecoder.decode(user, "UTF-8");
            conf.set("fs.sftp.user." + host, user);
            if (userPasswdInfo.length > 1) {
                conf.set("fs.sftp.password." + host + "." + user, userPasswdInfo[1]);
            }
        }
        final String user2 = conf.get("fs.sftp.user." + host);
        if (user2 == null || user2.equals("")) {
            throw new IllegalStateException("No user specified for sftp connection. Expand URI or credential file.");
        }
        final int connectionMax = conf.getInt("fs.sftp.connection.max", 5);
        this.connectionPool = new SFTPConnectionPool(connectionMax);
    }
    
    private ChannelSftp connect() throws IOException {
        final Configuration conf = this.getConf();
        final String host = conf.get("fs.sftp.host", null);
        final int port = conf.getInt("fs.sftp.host.port", 22);
        final String user = conf.get("fs.sftp.user." + host, null);
        final String pwd = conf.get("fs.sftp.password." + host + "." + user, null);
        final String keyFile = conf.get("fs.sftp.keyfile", null);
        final ChannelSftp channel = this.connectionPool.connect(host, port, user, pwd, keyFile);
        return channel;
    }
    
    private void disconnect(final ChannelSftp channel) throws IOException {
        this.connectionPool.disconnect(channel);
    }
    
    private Path makeAbsolute(final Path workDir, final Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return new Path(workDir, path);
    }
    
    private boolean exists(final ChannelSftp channel, final Path file) throws IOException {
        try {
            this.getFileStatus(channel, file);
            return true;
        }
        catch (FileNotFoundException fnfe) {
            return false;
        }
        catch (IOException ioe) {
            throw new IOException("Failed to get file status", ioe);
        }
    }
    
    private FileStatus getFileStatus(final ChannelSftp client, final Path file) throws IOException {
        FileStatus fileStat = null;
        Path workDir;
        try {
            workDir = new Path(client.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absolute = this.makeAbsolute(workDir, file);
        final Path parentPath = absolute.getParent();
        if (parentPath == null) {
            final long length = -1L;
            final boolean isDir = true;
            final int blockReplication = 1;
            final long blockSize = 4096L;
            final long modTime = -1L;
            final Path root = new Path("/");
            return new FileStatus(length, isDir, blockReplication, blockSize, modTime, root.makeQualified(this.getUri(), this.getWorkingDirectory()));
        }
        final String pathName = parentPath.toUri().getPath();
        Vector<ChannelSftp.LsEntry> sftpFiles;
        try {
            sftpFiles = (Vector<ChannelSftp.LsEntry>)client.ls(pathName);
        }
        catch (SftpException e2) {
            throw new FileNotFoundException(String.format("File %s does not exist.", file));
        }
        if (sftpFiles == null) {
            throw new FileNotFoundException(String.format("File %s does not exist.", file));
        }
        for (final ChannelSftp.LsEntry sftpFile : sftpFiles) {
            if (sftpFile.getFilename().equals(file.getName())) {
                fileStat = this.getFileStatus(client, sftpFile, parentPath);
                break;
            }
        }
        if (fileStat == null) {
            throw new FileNotFoundException(String.format("File %s does not exist.", file));
        }
        return fileStat;
    }
    
    private FileStatus getFileStatus(final ChannelSftp channel, final ChannelSftp.LsEntry sftpFile, final Path parentPath) throws IOException {
        final SftpATTRS attr = sftpFile.getAttrs();
        long length = attr.getSize();
        boolean isDir = attr.isDir();
        final boolean isLink = attr.isLink();
        if (isLink) {
            String link = parentPath.toUri().getPath() + "/" + sftpFile.getFilename();
            try {
                link = channel.realpath(link);
                final Path linkParent = new Path("/", link);
                final FileStatus fstat = this.getFileStatus(channel, linkParent);
                isDir = fstat.isDirectory();
                length = fstat.getLen();
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        final int blockReplication = 1;
        final long blockSize = 4096L;
        final long modTime = attr.getMTime() * 1000L;
        final long accessTime = attr.getATime() * 1000L;
        final FsPermission permission = this.getPermissions(sftpFile);
        final String user = Integer.toString(attr.getUId());
        final String group = Integer.toString(attr.getGId());
        final Path filePath = new Path(parentPath, sftpFile.getFilename());
        return new FileStatus(length, isDir, blockReplication, blockSize, modTime, accessTime, permission, user, group, filePath.makeQualified(this.getUri(), this.getWorkingDirectory()));
    }
    
    private FsPermission getPermissions(final ChannelSftp.LsEntry sftpFile) {
        return new FsPermission((short)sftpFile.getAttrs().getPermissions());
    }
    
    private boolean mkdirs(final ChannelSftp client, final Path file, final FsPermission permission) throws IOException {
        boolean created = true;
        Path workDir;
        try {
            workDir = new Path(client.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absolute = this.makeAbsolute(workDir, file);
        final String pathName = absolute.getName();
        if (!this.exists(client, absolute)) {
            final Path parent = absolute.getParent();
            created = (parent == null || this.mkdirs(client, parent, FsPermission.getDefault()));
            if (created) {
                final String parentDir = parent.toUri().getPath();
                final boolean succeeded = true;
                try {
                    final String previousCwd = client.pwd();
                    client.cd(parentDir);
                    client.mkdir(pathName);
                    client.cd(previousCwd);
                }
                catch (SftpException e2) {
                    throw new IOException(String.format("Can't make directory for path \"%s\" under \"%s\".", pathName, parentDir));
                }
                created &= succeeded;
            }
        }
        else if (this.isFile(client, absolute)) {
            throw new IOException(String.format("Can't make directory for path %s since it is a file.", absolute));
        }
        return created;
    }
    
    private boolean isFile(final ChannelSftp channel, final Path file) throws IOException {
        try {
            return !this.getFileStatus(channel, file).isDirectory();
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException ioe) {
            throw new IOException("File check failed", ioe);
        }
    }
    
    private boolean delete(final ChannelSftp channel, final Path file, final boolean recursive) throws IOException {
        Path workDir;
        try {
            workDir = new Path(channel.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absolute = this.makeAbsolute(workDir, file);
        final String pathName = absolute.toUri().getPath();
        FileStatus fileStat = null;
        try {
            fileStat = this.getFileStatus(channel, absolute);
        }
        catch (FileNotFoundException e2) {
            return false;
        }
        if (!fileStat.isDirectory()) {
            boolean status = true;
            try {
                channel.rm(pathName);
            }
            catch (SftpException e3) {
                status = false;
            }
            return status;
        }
        boolean status = true;
        final FileStatus[] dirEntries = this.listStatus(channel, absolute);
        if (dirEntries != null && dirEntries.length > 0) {
            if (!recursive) {
                throw new IOException(String.format("Directory: %s is not empty.", file));
            }
            for (int i = 0; i < dirEntries.length; ++i) {
                this.delete(channel, new Path(absolute, dirEntries[i].getPath()), recursive);
            }
        }
        try {
            channel.rmdir(pathName);
        }
        catch (SftpException e4) {
            status = false;
        }
        return status;
    }
    
    private FileStatus[] listStatus(final ChannelSftp client, final Path file) throws IOException {
        Path workDir;
        try {
            workDir = new Path(client.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absolute = this.makeAbsolute(workDir, file);
        final FileStatus fileStat = this.getFileStatus(client, absolute);
        if (!fileStat.isDirectory()) {
            return new FileStatus[] { fileStat };
        }
        Vector<ChannelSftp.LsEntry> sftpFiles;
        try {
            sftpFiles = (Vector<ChannelSftp.LsEntry>)client.ls(absolute.toUri().getPath());
        }
        catch (SftpException e2) {
            throw new IOException(e2);
        }
        final ArrayList<FileStatus> fileStats = new ArrayList<FileStatus>();
        for (int i = 0; i < sftpFiles.size(); ++i) {
            final ChannelSftp.LsEntry entry = sftpFiles.get(i);
            final String fname = entry.getFilename();
            if (!".".equalsIgnoreCase(fname) && !"..".equalsIgnoreCase(fname)) {
                fileStats.add(this.getFileStatus(client, entry, absolute));
            }
        }
        return fileStats.toArray(new FileStatus[fileStats.size()]);
    }
    
    private boolean rename(final ChannelSftp channel, final Path src, final Path dst) throws IOException {
        Path workDir;
        try {
            workDir = new Path(channel.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absoluteSrc = this.makeAbsolute(workDir, src);
        final Path absoluteDst = this.makeAbsolute(workDir, dst);
        if (!this.exists(channel, absoluteSrc)) {
            throw new IOException(String.format("Source path %s does not exist", src));
        }
        if (this.exists(channel, absoluteDst)) {
            throw new IOException(String.format("Destination path %s already exist, cannot rename!", dst));
        }
        boolean renamed = true;
        try {
            final String previousCwd = channel.pwd();
            channel.cd("/");
            channel.rename(src.toUri().getPath(), dst.toUri().getPath());
            channel.cd(previousCwd);
        }
        catch (SftpException e2) {
            renamed = false;
        }
        return renamed;
    }
    
    @Override
    public void initialize(final URI uriInfo, final Configuration conf) throws IOException {
        super.initialize(uriInfo, conf);
        this.setConfigurationFromURI(uriInfo, conf);
        this.setConf(conf);
        this.uri = uriInfo;
    }
    
    @Override
    public URI getUri() {
        return this.uri;
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        final ChannelSftp channel = this.connect();
        Path workDir;
        try {
            workDir = new Path(channel.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        Path absolute = this.makeAbsolute(workDir, f);
        final FileStatus fileStat = this.getFileStatus(channel, absolute);
        if (fileStat.isDirectory()) {
            this.disconnect(channel);
            throw new IOException(String.format("Path %s is a directory.", f));
        }
        InputStream is;
        try {
            absolute = new Path("/", channel.realpath(absolute.toUri().getPath()));
            is = channel.get(absolute.toUri().getPath());
        }
        catch (SftpException e2) {
            throw new IOException(e2);
        }
        final FSDataInputStream fis = new FSDataInputStream(new SFTPInputStream(is, channel, this.statistics));
        return fis;
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        final ChannelSftp client = this.connect();
        Path workDir;
        try {
            workDir = new Path(client.pwd());
        }
        catch (SftpException e) {
            throw new IOException(e);
        }
        final Path absolute = this.makeAbsolute(workDir, f);
        if (this.exists(client, f)) {
            if (!overwrite) {
                this.disconnect(client);
                throw new IOException(String.format("File already exists: %s", f));
            }
            this.delete(client, f, false);
        }
        Path parent = absolute.getParent();
        if (parent == null || !this.mkdirs(client, parent, FsPermission.getDefault())) {
            parent = ((parent == null) ? new Path("/") : parent);
            this.disconnect(client);
            throw new IOException(String.format("create(): Mkdirs failed to create: %s", parent));
        }
        OutputStream os;
        try {
            final String previousCwd = client.pwd();
            client.cd(parent.toUri().getPath());
            os = client.put(f.getName());
            client.cd(previousCwd);
        }
        catch (SftpException e2) {
            throw new IOException(e2);
        }
        final FSDataOutputStream fos = new FSDataOutputStream(os, this.statistics) {
            @Override
            public void close() throws IOException {
                super.close();
                SFTPFileSystem.this.disconnect(client);
            }
        };
        return fos;
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        throw new UnsupportedOperationException("Append is not supported by SFTPFileSystem");
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        final ChannelSftp channel = this.connect();
        try {
            final boolean success = this.rename(channel, src, dst);
            return success;
        }
        finally {
            this.disconnect(channel);
        }
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        final ChannelSftp channel = this.connect();
        try {
            final boolean success = this.delete(channel, f, recursive);
            return success;
        }
        finally {
            this.disconnect(channel);
        }
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        final ChannelSftp client = this.connect();
        try {
            final FileStatus[] stats = this.listStatus(client, f);
            return stats;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.getHomeDirectory();
    }
    
    @Override
    public Path getHomeDirectory() {
        ChannelSftp channel = null;
        try {
            channel = this.connect();
            final Path homeDir = new Path(channel.pwd());
            return homeDir;
        }
        catch (Exception ioe) {
            return null;
        }
        finally {
            try {
                this.disconnect(channel);
            }
            catch (IOException ioe2) {
                return null;
            }
        }
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        final ChannelSftp client = this.connect();
        try {
            final boolean success = this.mkdirs(client, f, permission);
            return success;
        }
        finally {
            this.disconnect(client);
        }
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        final ChannelSftp channel = this.connect();
        try {
            final FileStatus status = this.getFileStatus(channel, f);
            return status;
        }
        finally {
            this.disconnect(channel);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SFTPFileSystem.class);
    }
}
