// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.tar.TarEntry;
import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.resources.TarResource;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.tar.TarInputStream;
import java.util.Map;

public class TarScanner extends ArchiveScanner
{
    @Override
    protected void fillMapsFromArchive(final Resource src, final String encoding, final Map<String, Resource> fileEntries, final Map<String, Resource> matchFileEntries, final Map<String, Resource> dirEntries, final Map<String, Resource> matchDirEntries) {
        TarEntry entry = null;
        TarInputStream ti = null;
        try {
            try {
                ti = new TarInputStream(src.getInputStream());
            }
            catch (IOException ex) {
                throw new BuildException("problem opening " + this.srcFile, ex);
            }
            while ((entry = ti.getNextEntry()) != null) {
                final Resource r = new TarResource(src, entry);
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
        catch (IOException ex) {
            throw new BuildException("problem reading " + this.srcFile, ex);
        }
        finally {
            FileUtils.close(ti);
        }
    }
}
