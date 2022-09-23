// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.Files;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class AllowSymLinkAliasChecker implements ContextHandler.AliasCheck
{
    private static final Logger LOG;
    
    @Override
    public boolean check(final String uri, final Resource resource) {
        if (!(resource instanceof PathResource)) {
            return false;
        }
        final PathResource pathResource = (PathResource)resource;
        try {
            final Path path = pathResource.getPath();
            final Path alias = pathResource.getAliasPath();
            if (path.equals(alias)) {
                return false;
            }
            if (this.hasSymbolicLink(path) && Files.isSameFile(path, alias)) {
                if (AllowSymLinkAliasChecker.LOG.isDebugEnabled()) {
                    AllowSymLinkAliasChecker.LOG.debug("Allow symlink {} --> {}", resource, pathResource.getAliasPath());
                }
                return true;
            }
        }
        catch (Exception e) {
            AllowSymLinkAliasChecker.LOG.ignore(e);
        }
        return false;
    }
    
    private boolean hasSymbolicLink(final Path path) {
        if (Files.isSymbolicLink(path)) {
            return true;
        }
        Path base = path.getRoot();
        for (final Path segment : path) {
            base = base.resolve(segment);
            if (Files.isSymbolicLink(base)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOG = Log.getLogger(AllowSymLinkAliasChecker.class);
    }
}
