// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.io.compress.Lz4Codec;
import org.apache.hadoop.crypto.OpensslCipher;
import org.apache.hadoop.io.erasurecode.ErasureCodeNative;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.io.compress.ZStandardCodec;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.apache.hadoop.io.compress.bzip2.Bzip2Factory;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NativeLibraryChecker
{
    public static final Logger LOG;
    
    public static void main(final String[] args) {
        final String usage = "NativeLibraryChecker [-a|-h]\n  -a  use -a to check all libraries are available\n      by default just check hadoop library (and\n      winutils.exe on Windows OS) is available\n      exit with error code 1 if check failed\n  -h  print this message\n";
        if (args.length > 1 || (args.length == 1 && !args[0].equals("-a") && !args[0].equals("-h"))) {
            System.err.println(usage);
            ExitUtil.terminate(1);
        }
        boolean checkAll = false;
        if (args.length == 1) {
            if (args[0].equals("-h")) {
                System.out.println(usage);
                return;
            }
            checkAll = true;
        }
        final Configuration conf = new Configuration();
        final boolean nativeHadoopLoaded = NativeCodeLoader.isNativeCodeLoaded();
        boolean zlibLoaded = false;
        boolean snappyLoaded = false;
        boolean isalLoaded = false;
        boolean zStdLoaded = false;
        final boolean lz4Loaded = nativeHadoopLoaded;
        final boolean bzip2Loaded = Bzip2Factory.isNativeBzip2Loaded(conf);
        boolean openSslLoaded = false;
        boolean winutilsExists = false;
        String openSslDetail = "";
        String hadoopLibraryName = "";
        String zlibLibraryName = "";
        String snappyLibraryName = "";
        String isalDetail = "";
        String zstdLibraryName = "";
        String lz4LibraryName = "";
        String bzip2LibraryName = "";
        String winutilsPath = null;
        if (nativeHadoopLoaded) {
            hadoopLibraryName = NativeCodeLoader.getLibraryName();
            zlibLoaded = ZlibFactory.isNativeZlibLoaded(conf);
            if (zlibLoaded) {
                zlibLibraryName = ZlibFactory.getLibraryName();
            }
            zStdLoaded = (NativeCodeLoader.buildSupportsZstd() && ZStandardCodec.isNativeCodeLoaded());
            if (zStdLoaded && NativeCodeLoader.buildSupportsZstd()) {
                zstdLibraryName = ZStandardCodec.getLibraryName();
            }
            snappyLoaded = (NativeCodeLoader.buildSupportsSnappy() && SnappyCodec.isNativeCodeLoaded());
            if (snappyLoaded && NativeCodeLoader.buildSupportsSnappy()) {
                snappyLibraryName = SnappyCodec.getLibraryName();
            }
            isalDetail = ErasureCodeNative.getLoadingFailureReason();
            if (isalDetail != null) {
                isalLoaded = false;
            }
            else {
                isalDetail = ErasureCodeNative.getLibraryName();
                isalLoaded = true;
            }
            openSslDetail = OpensslCipher.getLoadingFailureReason();
            if (openSslDetail != null) {
                openSslLoaded = false;
            }
            else {
                openSslDetail = OpensslCipher.getLibraryName();
                openSslLoaded = true;
            }
            if (lz4Loaded) {
                lz4LibraryName = Lz4Codec.getLibraryName();
            }
            if (bzip2Loaded) {
                bzip2LibraryName = Bzip2Factory.getLibraryName(conf);
            }
        }
        if (Shell.WINDOWS) {
            try {
                winutilsPath = Shell.getWinUtilsFile().getCanonicalPath();
                winutilsExists = true;
            }
            catch (IOException e) {
                NativeLibraryChecker.LOG.debug("No Winutils: ", e);
                winutilsPath = e.getMessage();
                winutilsExists = false;
            }
            System.out.printf("winutils: %b %s%n", winutilsExists, winutilsPath);
        }
        System.out.println("Native library checking:");
        System.out.printf("hadoop:  %b %s%n", nativeHadoopLoaded, hadoopLibraryName);
        System.out.printf("zlib:    %b %s%n", zlibLoaded, zlibLibraryName);
        System.out.printf("zstd  :  %b %s%n", zStdLoaded, zstdLibraryName);
        System.out.printf("snappy:  %b %s%n", snappyLoaded, snappyLibraryName);
        System.out.printf("lz4:     %b %s%n", lz4Loaded, lz4LibraryName);
        System.out.printf("bzip2:   %b %s%n", bzip2Loaded, bzip2LibraryName);
        System.out.printf("openssl: %b %s%n", openSslLoaded, openSslDetail);
        System.out.printf("ISA-L:   %b %s%n", isalLoaded, isalDetail);
        if (Shell.WINDOWS) {
            System.out.printf("winutils: %b %s%n", winutilsExists, winutilsPath);
        }
        if (!nativeHadoopLoaded || (Shell.WINDOWS && !winutilsExists) || (checkAll && (!zlibLoaded || !snappyLoaded || !lz4Loaded || !bzip2Loaded || !isalLoaded || !zStdLoaded))) {
            ExitUtil.terminate(1);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(NativeLibraryChecker.class);
    }
}
