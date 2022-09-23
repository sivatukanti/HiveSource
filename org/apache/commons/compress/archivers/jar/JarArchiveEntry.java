// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.archivers.jar;

import java.util.jar.JarEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class JarArchiveEntry extends ZipArchiveEntry
{
    private Attributes manifestAttributes;
    private Certificate[] certificates;
    
    public JarArchiveEntry(final ZipEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final String name) {
        super(name);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final ZipArchiveEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public JarArchiveEntry(final JarEntry entry) throws ZipException {
        super(entry);
        this.manifestAttributes = null;
        this.certificates = null;
    }
    
    public Attributes getManifestAttributes() {
        return this.manifestAttributes;
    }
    
    public Certificate[] getCertificates() {
        if (this.certificates != null) {
            final Certificate[] certs = new Certificate[this.certificates.length];
            System.arraycopy(this.certificates, 0, certs, 0, certs.length);
            return certs;
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
