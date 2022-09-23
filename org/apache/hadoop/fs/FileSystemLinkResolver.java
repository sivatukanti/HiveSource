// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class FileSystemLinkResolver<T>
{
    public abstract T doCall(final Path p0) throws IOException, UnresolvedLinkException;
    
    public abstract T next(final FileSystem p0, final Path p1) throws IOException;
    
    public T resolve(final FileSystem filesys, final Path path) throws IOException {
        int count = 0;
        T in = null;
        Path p = path;
        FileSystem fs = filesys;
        boolean isLink = true;
        while (isLink) {
            try {
                in = this.doCall(p);
                isLink = false;
            }
            catch (UnresolvedLinkException e) {
                if (!filesys.resolveSymlinks) {
                    throw new IOException("Path " + path + " contains a symlink and symlink resolution is disabled (" + "fs.client.resolve.remote.symlinks" + ").", e);
                }
                if (!FileSystem.areSymlinksEnabled()) {
                    throw new IOException("Symlink resolution is disabled in this version of Hadoop.");
                }
                if (count++ > 32) {
                    throw new IOException("Possible cyclic loop while following symbolic link " + path);
                }
                p = FSLinkResolver.qualifySymlinkTarget(fs.getUri(), p, filesys.resolveLink(p));
                fs = FileSystem.getFSofPath(p, filesys.getConf());
                if (!fs.equals(filesys)) {
                    return this.next(fs, p);
                }
                continue;
            }
        }
        return in;
    }
}
