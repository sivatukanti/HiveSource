// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class FSLinkResolver<T>
{
    public static Path qualifySymlinkTarget(final URI pathURI, final Path pathWithLink, final Path target) {
        final URI targetUri = target.toUri();
        final String scheme = targetUri.getScheme();
        final String auth = targetUri.getAuthority();
        return (scheme == null && auth == null) ? target.makeQualified(pathURI, pathWithLink.getParent()) : target;
    }
    
    public abstract T next(final AbstractFileSystem p0, final Path p1) throws IOException, UnresolvedLinkException;
    
    public T resolve(final FileContext fc, final Path path) throws IOException {
        int count = 0;
        T in = null;
        Path p = path;
        AbstractFileSystem fs = fc.getFSofPath(p);
        boolean isLink = true;
        while (isLink) {
            try {
                in = this.next(fs, p);
                isLink = false;
            }
            catch (UnresolvedLinkException e) {
                if (!fc.resolveSymlinks) {
                    throw new IOException("Path " + path + " contains a symlink and symlink resolution is disabled (" + "fs.client.resolve.remote.symlinks" + ").", e);
                }
                if (!FileSystem.areSymlinksEnabled()) {
                    throw new IOException("Symlink resolution is disabled in this version of Hadoop.");
                }
                if (count++ > 32) {
                    throw new IOException("Possible cyclic loop while following symbolic link " + path);
                }
                p = qualifySymlinkTarget(fs.getUri(), p, fs.getLinkTarget(p));
                fs = fc.getFSofPath(p);
            }
        }
        return in;
    }
}
