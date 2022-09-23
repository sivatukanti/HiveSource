// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.NoSuchElementException;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.Shell;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class Stat extends Shell
{
    private final Path original;
    private final Path qualified;
    private final Path path;
    private final long blockSize;
    private final boolean dereference;
    private FileStatus stat;
    
    public Stat(final Path path, final long blockSize, final boolean deref, final FileSystem fs) throws IOException {
        super(0L, true);
        this.original = path;
        final Path stripped = new Path(this.original.makeQualified(fs.getUri(), fs.getWorkingDirectory()).toUri().getPath());
        this.qualified = stripped.makeQualified(fs.getUri(), fs.getWorkingDirectory());
        this.path = new Path(this.qualified.toUri().getPath());
        this.blockSize = blockSize;
        this.dereference = deref;
        final Map<String, String> env = new HashMap<String, String>();
        env.put("LANG", "C");
        this.setEnvironment(env);
    }
    
    public FileStatus getFileStatus() throws IOException {
        this.run();
        return this.stat;
    }
    
    public static boolean isAvailable() {
        return Shell.LINUX || Shell.FREEBSD || Shell.MAC;
    }
    
    @VisibleForTesting
    FileStatus getFileStatusForTesting() {
        return this.stat;
    }
    
    @Override
    protected String[] getExecString() {
        String derefFlag = "-";
        if (this.dereference) {
            derefFlag = "-L";
        }
        if (Shell.LINUX) {
            return new String[] { "stat", derefFlag + "c", "%s,%F,%Y,%X,%a,%U,%G,%N", this.path.toString() };
        }
        if (Shell.FREEBSD || Shell.MAC) {
            return new String[] { "stat", derefFlag + "f", "%z,%HT,%m,%a,%Op,%Su,%Sg,`link' -> `%Y'", this.path.toString() };
        }
        throw new UnsupportedOperationException("stat is not supported on this platform");
    }
    
    @Override
    protected void parseExecResult(final BufferedReader lines) throws IOException {
        this.stat = null;
        final String line = lines.readLine();
        if (line == null) {
            throw new IOException("Unable to stat path: " + this.original);
        }
        if (line.endsWith("No such file or directory") || line.endsWith("Not a directory")) {
            throw new FileNotFoundException("File " + this.original + " does not exist");
        }
        if (line.endsWith("Too many levels of symbolic links")) {
            throw new IOException("Possible cyclic loop while following symbolic link " + this.original);
        }
        final StringTokenizer tokens = new StringTokenizer(line, ",");
        try {
            final long length = Long.parseLong(tokens.nextToken());
            final boolean isDir = tokens.nextToken().equalsIgnoreCase("directory");
            final long modTime = Long.parseLong(tokens.nextToken()) * 1000L;
            final long accessTime = Long.parseLong(tokens.nextToken()) * 1000L;
            String octalPerms = tokens.nextToken();
            if (octalPerms.length() > 4) {
                final int len = octalPerms.length();
                octalPerms = octalPerms.substring(len - 4, len);
            }
            final FsPermission perms = new FsPermission(Short.parseShort(octalPerms, 8));
            final String owner = tokens.nextToken();
            final String group = tokens.nextToken();
            final String symStr = tokens.nextToken();
            Path symlink = null;
            final String[] parts = symStr.split(" -> ");
            try {
                String target = parts[1];
                target = target.substring(1, target.length() - 1);
                if (!target.isEmpty()) {
                    symlink = new Path(target);
                }
            }
            catch (ArrayIndexOutOfBoundsException ex) {}
            this.stat = new FileStatus(length, isDir, 1, this.blockSize, modTime, accessTime, perms, owner, group, symlink, this.qualified);
        }
        catch (NumberFormatException e) {
            throw new IOException("Unexpected stat output: " + line, e);
        }
        catch (NoSuchElementException e2) {
            throw new IOException("Unexpected stat output: " + line, e2);
        }
    }
}
