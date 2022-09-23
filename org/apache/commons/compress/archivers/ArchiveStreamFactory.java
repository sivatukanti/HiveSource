// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.archivers;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import java.io.InputStream;

public class ArchiveStreamFactory
{
    public static final String AR = "ar";
    public static final String CPIO = "cpio";
    public static final String DUMP = "dump";
    public static final String JAR = "jar";
    public static final String TAR = "tar";
    public static final String ZIP = "zip";
    
    public ArchiveInputStream createArchiveInputStream(final String archiverName, final InputStream in) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
        }
        if ("ar".equalsIgnoreCase(archiverName)) {
            return new ArArchiveInputStream(in);
        }
        if ("zip".equalsIgnoreCase(archiverName)) {
            return new ZipArchiveInputStream(in);
        }
        if ("tar".equalsIgnoreCase(archiverName)) {
            return new TarArchiveInputStream(in);
        }
        if ("jar".equalsIgnoreCase(archiverName)) {
            return new JarArchiveInputStream(in);
        }
        if ("cpio".equalsIgnoreCase(archiverName)) {
            return new CpioArchiveInputStream(in);
        }
        if ("dump".equalsIgnoreCase(archiverName)) {
            return new DumpArchiveInputStream(in);
        }
        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }
    
    public ArchiveOutputStream createArchiveOutputStream(final String archiverName, final OutputStream out) throws ArchiveException {
        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }
        if (out == null) {
            throw new IllegalArgumentException("OutputStream must not be null.");
        }
        if ("ar".equalsIgnoreCase(archiverName)) {
            return new ArArchiveOutputStream(out);
        }
        if ("zip".equalsIgnoreCase(archiverName)) {
            return new ZipArchiveOutputStream(out);
        }
        if ("tar".equalsIgnoreCase(archiverName)) {
            return new TarArchiveOutputStream(out);
        }
        if ("jar".equalsIgnoreCase(archiverName)) {
            return new JarArchiveOutputStream(out);
        }
        if ("cpio".equalsIgnoreCase(archiverName)) {
            return new CpioArchiveOutputStream(out);
        }
        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }
    
    public ArchiveInputStream createArchiveInputStream(final InputStream in) throws ArchiveException {
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        }
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Mark is not supported.");
        }
        final byte[] signature = new byte[12];
        in.mark(signature.length);
        try {
            int signatureLength = in.read(signature);
            in.reset();
            if (ZipArchiveInputStream.matches(signature, signatureLength)) {
                return new ZipArchiveInputStream(in);
            }
            if (JarArchiveInputStream.matches(signature, signatureLength)) {
                return new JarArchiveInputStream(in);
            }
            if (ArArchiveInputStream.matches(signature, signatureLength)) {
                return new ArArchiveInputStream(in);
            }
            if (CpioArchiveInputStream.matches(signature, signatureLength)) {
                return new CpioArchiveInputStream(in);
            }
            final byte[] dumpsig = new byte[32];
            in.mark(dumpsig.length);
            signatureLength = in.read(dumpsig);
            in.reset();
            if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
                return new DumpArchiveInputStream(in);
            }
            final byte[] tarheader = new byte[512];
            in.mark(tarheader.length);
            signatureLength = in.read(tarheader);
            in.reset();
            if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
                return new TarArchiveInputStream(in);
            }
            if (signatureLength >= 512) {
                try {
                    final TarArchiveInputStream tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
                    tais.getNextEntry();
                    return new TarArchiveInputStream(in);
                }
                catch (Exception ex) {}
            }
        }
        catch (IOException e) {
            throw new ArchiveException("Could not use reset and mark operations.", e);
        }
        throw new ArchiveException("No Archiver found for the stream signature");
    }
}
