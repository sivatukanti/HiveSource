// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import java.io.IOException;
import java.io.Closeable;
import java.io.InputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.FileInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class CompressionUtils
{
    public static void tar(final String parentDir, final String[] inputFiles, final String outputFile) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(parentDir, outputFile));
            final TarArchiveOutputStream tOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(out)));
            for (int i = 0; i < inputFiles.length; ++i) {
                final File f = new File(parentDir, inputFiles[i]);
                final TarArchiveEntry tarEntry = new TarArchiveEntry(f, f.getName());
                tOut.setLongFileMode(2);
                tOut.putArchiveEntry(tarEntry);
                final FileInputStream input = new FileInputStream(f);
                try {
                    IOUtils.copy(input, tOut);
                }
                finally {
                    input.close();
                }
                tOut.closeArchiveEntry();
            }
            tOut.close();
        }
        finally {
            org.apache.hadoop.io.IOUtils.closeStream(out);
        }
    }
}
