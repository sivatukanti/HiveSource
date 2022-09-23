// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.compressors;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import java.io.InputStream;

public class CompressorStreamFactory
{
    public static final String BZIP2 = "bzip2";
    public static final String GZIP = "gz";
    public static final String PACK200 = "pack200";
    public static final String XZ = "xz";
    
    public CompressorInputStream createCompressorInputStream(final InputStream in) throws CompressorException {
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        }
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Mark is not supported.");
        }
        final byte[] signature = new byte[12];
        in.mark(signature.length);
        try {
            final int signatureLength = in.read(signature);
            in.reset();
            if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
                return new BZip2CompressorInputStream(in);
            }
            if (GzipCompressorInputStream.matches(signature, signatureLength)) {
                return new GzipCompressorInputStream(in);
            }
            if (XZCompressorInputStream.matches(signature, signatureLength)) {
                return new XZCompressorInputStream(in);
            }
            if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
                return new Pack200CompressorInputStream(in);
            }
        }
        catch (IOException e) {
            throw new CompressorException("Failed to detect Compressor from InputStream.", e);
        }
        throw new CompressorException("No Compressor found for the stream signature.");
    }
    
    public CompressorInputStream createCompressorInputStream(final String name, final InputStream in) throws CompressorException {
        if (name == null || in == null) {
            throw new IllegalArgumentException("Compressor name and stream must not be null.");
        }
        try {
            if ("gz".equalsIgnoreCase(name)) {
                return new GzipCompressorInputStream(in);
            }
            if ("bzip2".equalsIgnoreCase(name)) {
                return new BZip2CompressorInputStream(in);
            }
            if ("xz".equalsIgnoreCase(name)) {
                return new XZCompressorInputStream(in);
            }
            if ("pack200".equalsIgnoreCase(name)) {
                return new Pack200CompressorInputStream(in);
            }
        }
        catch (IOException e) {
            throw new CompressorException("Could not create CompressorInputStream.", e);
        }
        throw new CompressorException("Compressor: " + name + " not found.");
    }
    
    public CompressorOutputStream createCompressorOutputStream(final String name, final OutputStream out) throws CompressorException {
        if (name == null || out == null) {
            throw new IllegalArgumentException("Compressor name and stream must not be null.");
        }
        try {
            if ("gz".equalsIgnoreCase(name)) {
                return new GzipCompressorOutputStream(out);
            }
            if ("bzip2".equalsIgnoreCase(name)) {
                return new BZip2CompressorOutputStream(out);
            }
            if ("xz".equalsIgnoreCase(name)) {
                return new XZCompressorOutputStream(out);
            }
            if ("pack200".equalsIgnoreCase(name)) {
                return new Pack200CompressorOutputStream(out);
            }
        }
        catch (IOException e) {
            throw new CompressorException("Could not create CompressorOutputStream", e);
        }
        throw new CompressorException("Compressor: " + name + " not found.");
    }
}
