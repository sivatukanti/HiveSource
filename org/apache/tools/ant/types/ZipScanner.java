// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Enumeration;
import java.io.File;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.zip.ZipEntry;
import java.io.IOException;
import java.util.zip.ZipException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.ant.types.resources.FileProvider;
import java.util.Map;

public class ZipScanner extends ArchiveScanner
{
    @Override
    protected void fillMapsFromArchive(final Resource src, final String encoding, final Map<String, Resource> fileEntries, final Map<String, Resource> matchFileEntries, final Map<String, Resource> dirEntries, final Map<String, Resource> matchDirEntries) {
        ZipEntry entry = null;
        ZipFile zf = null;
        File srcFile = null;
        final FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            srcFile = fp.getFile();
            try {
                try {
                    zf = new ZipFile(srcFile, encoding);
                }
                catch (ZipException ex) {
                    throw new BuildException("Problem reading " + srcFile, ex);
                }
                catch (IOException ex2) {
                    throw new BuildException("Problem opening " + srcFile, ex2);
                }
                final Enumeration<ZipEntry> e = zf.getEntries();
                while (e.hasMoreElements()) {
                    entry = e.nextElement();
                    final Resource r = new ZipResource(srcFile, encoding, entry);
                    String name = entry.getName();
                    if (entry.isDirectory()) {
                        name = ArchiveScanner.trimSeparator(name);
                        dirEntries.put(name, r);
                        if (!this.match(name)) {
                            continue;
                        }
                        matchDirEntries.put(name, r);
                    }
                    else {
                        fileEntries.put(name, r);
                        if (!this.match(name)) {
                            continue;
                        }
                        matchFileEntries.put(name, r);
                    }
                }
            }
            finally {
                ZipFile.closeQuietly(zf);
            }
            return;
        }
        throw new BuildException("Only file provider resources are supported");
    }
}
