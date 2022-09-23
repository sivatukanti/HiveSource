// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.ArrayList;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import java.util.Map;
import java.nio.file.AccessDeniedException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.nativeio.NativeIO;
import org.apache.hadoop.util.StringUtils;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.FileSystems;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.concurrent.ExecutionException;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.io.FileNotFoundException;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.util.Shell;
import java.io.File;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class FileUtil
{
    private static final Logger LOG;
    public static final int SYMLINK_NO_PRIVILEGE = 2;
    private static final int BUFFER_SIZE = 8192;
    
    public static Path[] stat2Paths(final FileStatus[] stats) {
        if (stats == null) {
            return null;
        }
        final Path[] ret = new Path[stats.length];
        for (int i = 0; i < stats.length; ++i) {
            ret[i] = stats[i].getPath();
        }
        return ret;
    }
    
    public static Path[] stat2Paths(final FileStatus[] stats, final Path path) {
        if (stats == null) {
            return new Path[] { path };
        }
        return stat2Paths(stats);
    }
    
    public static void fullyDeleteOnExit(final File file) {
        file.deleteOnExit();
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (final File child : files) {
                    fullyDeleteOnExit(child);
                }
            }
        }
    }
    
    public static boolean fullyDelete(final File dir) {
        return fullyDelete(dir, false);
    }
    
    public static boolean fullyDelete(final File dir, final boolean tryGrantPermissions) {
        if (tryGrantPermissions) {
            final File parent = dir.getParentFile();
            grantPermissions(parent);
        }
        return deleteImpl(dir, false) || (fullyDeleteContents(dir, tryGrantPermissions) && deleteImpl(dir, true));
    }
    
    public static String readLink(final File f) {
        if (f == null) {
            FileUtil.LOG.warn("Can not read a null symLink");
            return "";
        }
        try {
            return Shell.execCommand(Shell.getReadlinkCommand(f.toString())).trim();
        }
        catch (IOException x) {
            return "";
        }
    }
    
    private static void grantPermissions(final File f) {
        setExecutable(f, true);
        setReadable(f, true);
        setWritable(f, true);
    }
    
    private static boolean deleteImpl(final File f, final boolean doLog) {
        if (f == null) {
            FileUtil.LOG.warn("null file argument.");
            return false;
        }
        final boolean wasDeleted = f.delete();
        if (wasDeleted) {
            return true;
        }
        final boolean ex = f.exists();
        if (doLog && ex) {
            FileUtil.LOG.warn("Failed to delete file or dir [" + f.getAbsolutePath() + "]: it still exists.");
        }
        return !ex;
    }
    
    public static boolean fullyDeleteContents(final File dir) {
        return fullyDeleteContents(dir, false);
    }
    
    public static boolean fullyDeleteContents(final File dir, final boolean tryGrantPermissions) {
        if (tryGrantPermissions) {
            grantPermissions(dir);
        }
        boolean deletionSucceeded = true;
        final File[] contents = dir.listFiles();
        if (contents != null) {
            for (int i = 0; i < contents.length; ++i) {
                if (contents[i].isFile()) {
                    if (!deleteImpl(contents[i], true)) {
                        deletionSucceeded = false;
                    }
                }
                else {
                    boolean b = false;
                    b = deleteImpl(contents[i], false);
                    if (!b) {
                        if (!fullyDelete(contents[i], tryGrantPermissions)) {
                            deletionSucceeded = false;
                        }
                    }
                }
            }
        }
        return deletionSucceeded;
    }
    
    @Deprecated
    public static void fullyDelete(final FileSystem fs, final Path dir) throws IOException {
        fs.delete(dir, true);
    }
    
    private static void checkDependencies(final FileSystem srcFS, final Path src, final FileSystem dstFS, final Path dst) throws IOException {
        if (srcFS == dstFS) {
            final String srcq = srcFS.makeQualified(src).toString() + "/";
            final String dstq = dstFS.makeQualified(dst).toString() + "/";
            if (dstq.startsWith(srcq)) {
                if (srcq.length() == dstq.length()) {
                    throw new IOException("Cannot copy " + src + " to itself.");
                }
                throw new IOException("Cannot copy " + src + " to its subdirectory " + dst);
            }
        }
    }
    
    public static boolean copy(final FileSystem srcFS, final Path src, final FileSystem dstFS, final Path dst, final boolean deleteSource, final Configuration conf) throws IOException {
        return copy(srcFS, src, dstFS, dst, deleteSource, true, conf);
    }
    
    public static boolean copy(final FileSystem srcFS, final Path[] srcs, final FileSystem dstFS, final Path dst, final boolean deleteSource, final boolean overwrite, final Configuration conf) throws IOException {
        boolean gotException = false;
        boolean returnVal = true;
        final StringBuilder exceptions = new StringBuilder();
        if (srcs.length == 1) {
            return copy(srcFS, srcs[0], dstFS, dst, deleteSource, overwrite, conf);
        }
        try {
            final FileStatus sdst = dstFS.getFileStatus(dst);
            if (!sdst.isDirectory()) {
                throw new IOException("copying multiple files, but last argument `" + dst + "' is not a directory");
            }
        }
        catch (FileNotFoundException e) {
            throw new IOException("`" + dst + "': specified destination directory does not exist", e);
        }
        for (final Path src : srcs) {
            try {
                if (!copy(srcFS, src, dstFS, dst, deleteSource, overwrite, conf)) {
                    returnVal = false;
                }
            }
            catch (IOException e2) {
                gotException = true;
                exceptions.append(e2.getMessage());
                exceptions.append("\n");
            }
        }
        if (gotException) {
            throw new IOException(exceptions.toString());
        }
        return returnVal;
    }
    
    public static boolean copy(final FileSystem srcFS, final Path src, final FileSystem dstFS, final Path dst, final boolean deleteSource, final boolean overwrite, final Configuration conf) throws IOException {
        final FileStatus fileStatus = srcFS.getFileStatus(src);
        return copy(srcFS, fileStatus, dstFS, dst, deleteSource, overwrite, conf);
    }
    
    public static boolean copy(final FileSystem srcFS, final FileStatus srcStatus, final FileSystem dstFS, Path dst, final boolean deleteSource, final boolean overwrite, final Configuration conf) throws IOException {
        final Path src = srcStatus.getPath();
        dst = checkDest(src.getName(), dstFS, dst, overwrite);
        if (srcStatus.isDirectory()) {
            checkDependencies(srcFS, src, dstFS, dst);
            if (!dstFS.mkdirs(dst)) {
                return false;
            }
            final FileStatus[] contents = srcFS.listStatus(src);
            for (int i = 0; i < contents.length; ++i) {
                copy(srcFS, contents[i], dstFS, new Path(dst, contents[i].getPath().getName()), deleteSource, overwrite, conf);
            }
        }
        else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = srcFS.open(src);
                out = dstFS.create(dst, overwrite);
                IOUtils.copyBytes(in, out, conf, true);
            }
            catch (IOException e) {
                IOUtils.closeStream(out);
                IOUtils.closeStream(in);
                throw e;
            }
        }
        return !deleteSource || srcFS.delete(src, true);
    }
    
    public static boolean copy(final File src, final FileSystem dstFS, Path dst, final boolean deleteSource, final Configuration conf) throws IOException {
        dst = checkDest(src.getName(), dstFS, dst, false);
        if (src.isDirectory()) {
            if (!dstFS.mkdirs(dst)) {
                return false;
            }
            final File[] contents = listFiles(src);
            for (int i = 0; i < contents.length; ++i) {
                copy(contents[i], dstFS, new Path(dst, contents[i].getName()), deleteSource, conf);
            }
        }
        else if (src.isFile()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = dstFS.create(dst);
                IOUtils.copyBytes(in, out, conf);
            }
            catch (IOException e) {
                IOUtils.closeStream(out);
                IOUtils.closeStream(in);
                throw e;
            }
        }
        else {
            if (!src.canRead()) {
                throw new IOException(src.toString() + ": Permission denied");
            }
            throw new IOException(src.toString() + ": No such file or directory");
        }
        return !deleteSource || fullyDelete(src);
    }
    
    public static boolean copy(final FileSystem srcFS, final Path src, final File dst, final boolean deleteSource, final Configuration conf) throws IOException {
        final FileStatus filestatus = srcFS.getFileStatus(src);
        return copy(srcFS, filestatus, dst, deleteSource, conf);
    }
    
    private static boolean copy(final FileSystem srcFS, final FileStatus srcStatus, final File dst, final boolean deleteSource, final Configuration conf) throws IOException {
        final Path src = srcStatus.getPath();
        if (srcStatus.isDirectory()) {
            if (!dst.mkdirs()) {
                return false;
            }
            final FileStatus[] contents = srcFS.listStatus(src);
            for (int i = 0; i < contents.length; ++i) {
                copy(srcFS, contents[i], new File(dst, contents[i].getPath().getName()), deleteSource, conf);
            }
        }
        else {
            final InputStream in = srcFS.open(src);
            IOUtils.copyBytes(in, new FileOutputStream(dst), conf);
        }
        return !deleteSource || srcFS.delete(src, true);
    }
    
    private static Path checkDest(final String srcName, final FileSystem dstFS, final Path dst, final boolean overwrite) throws IOException {
        FileStatus sdst;
        try {
            sdst = dstFS.getFileStatus(dst);
        }
        catch (FileNotFoundException e) {
            sdst = null;
        }
        if (null != sdst) {
            if (sdst.isDirectory()) {
                if (null == srcName) {
                    throw new PathIsDirectoryException(dst.toString());
                }
                return checkDest(null, dstFS, new Path(dst, srcName), overwrite);
            }
            else if (!overwrite) {
                throw new PathExistsException(dst.toString(), "Target " + dst + " already exists");
            }
        }
        return dst;
    }
    
    public static String makeShellPath(final String filename) throws IOException {
        return filename;
    }
    
    public static String makeShellPath(final File file) throws IOException {
        return makeShellPath(file, false);
    }
    
    public static String makeSecureShellPath(final File file) throws IOException {
        if (Shell.WINDOWS) {
            throw new UnsupportedOperationException("Not implemented for Windows");
        }
        return makeShellPath(file, false).replace("'", "'\\''");
    }
    
    public static String makeShellPath(final File file, final boolean makeCanonicalPath) throws IOException {
        if (makeCanonicalPath) {
            return makeShellPath(file.getCanonicalPath());
        }
        return makeShellPath(file.toString());
    }
    
    public static long getDU(final File dir) {
        long size = 0L;
        if (!dir.exists()) {
            return 0L;
        }
        if (!dir.isDirectory()) {
            return dir.length();
        }
        final File[] allFiles = dir.listFiles();
        if (allFiles != null) {
            for (int i = 0; i < allFiles.length; ++i) {
                boolean isSymLink;
                try {
                    isSymLink = FileUtils.isSymlink(allFiles[i]);
                }
                catch (IOException ioe) {
                    isSymLink = true;
                }
                if (!isSymLink) {
                    size += getDU(allFiles[i]);
                }
            }
        }
        return size;
    }
    
    public static void unZip(final InputStream inputStream, final File toDir) throws IOException {
        try (final ZipInputStream zip = new ZipInputStream(inputStream)) {
            int numOfFailedLastModifiedSet = 0;
            final String targetDirPath = toDir.getCanonicalPath() + File.separator;
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory()) {
                    final File file = new File(toDir, entry.getName());
                    if (!file.getCanonicalPath().startsWith(targetDirPath)) {
                        throw new IOException("expanding " + entry.getName() + " would create file outside of " + toDir);
                    }
                    final File parent = file.getParentFile();
                    if (!parent.mkdirs() && !parent.isDirectory()) {
                        throw new IOException("Mkdirs failed to create " + parent.getAbsolutePath());
                    }
                    try (final OutputStream out = new FileOutputStream(file)) {
                        IOUtils.copyBytes(zip, out, 8192);
                    }
                    if (!file.setLastModified(entry.getTime())) {
                        ++numOfFailedLastModifiedSet;
                    }
                }
            }
            if (numOfFailedLastModifiedSet > 0) {
                FileUtil.LOG.warn("Could not set last modfied time for {} file(s)", (Object)numOfFailedLastModifiedSet);
            }
        }
    }
    
    public static void unZip(final File inFile, final File unzipDir) throws IOException {
        final ZipFile zipFile = new ZipFile(inFile);
        try {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            final String targetDirPath = unzipDir.getCanonicalPath() + File.separator;
            while (entries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                if (!entry.isDirectory()) {
                    final InputStream in = zipFile.getInputStream(entry);
                    try {
                        final File file = new File(unzipDir, entry.getName());
                        if (!file.getCanonicalPath().startsWith(targetDirPath)) {
                            throw new IOException("expanding " + entry.getName() + " would create file outside of " + unzipDir);
                        }
                        if (!file.getParentFile().mkdirs() && !file.getParentFile().isDirectory()) {
                            throw new IOException("Mkdirs failed to create " + file.getParentFile().toString());
                        }
                        final OutputStream out = new FileOutputStream(file);
                        try {
                            final byte[] buffer = new byte[8192];
                            int i;
                            while ((i = in.read(buffer)) != -1) {
                                out.write(buffer, 0, i);
                            }
                        }
                        finally {
                            out.close();
                        }
                    }
                    finally {
                        in.close();
                    }
                }
            }
        }
        finally {
            zipFile.close();
        }
    }
    
    private static void runCommandOnStream(final InputStream inputStream, final String command) throws IOException, InterruptedException, ExecutionException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_2        /* executor */
        //     2: new             Ljava/lang/ProcessBuilder;
        //     5: dup            
        //     6: iconst_0       
        //     7: anewarray       Ljava/lang/String;
        //    10: invokespecial   java/lang/ProcessBuilder.<init>:([Ljava/lang/String;)V
        //    13: astore_3        /* builder */
        //    14: aload_3         /* builder */
        //    15: iconst_3       
        //    16: anewarray       Ljava/lang/String;
        //    19: dup            
        //    20: iconst_0       
        //    21: getstatic       org/apache/hadoop/util/Shell.WINDOWS:Z
        //    24: ifeq            32
        //    27: ldc             "cmd"
        //    29: goto            34
        //    32: ldc             "bash"
        //    34: aastore        
        //    35: dup            
        //    36: iconst_1       
        //    37: getstatic       org/apache/hadoop/util/Shell.WINDOWS:Z
        //    40: ifeq            48
        //    43: ldc             "/c"
        //    45: goto            50
        //    48: ldc             "-c"
        //    50: aastore        
        //    51: dup            
        //    52: iconst_2       
        //    53: aload_1         /* command */
        //    54: aastore        
        //    55: invokevirtual   java/lang/ProcessBuilder.command:([Ljava/lang/String;)Ljava/lang/ProcessBuilder;
        //    58: pop            
        //    59: aload_3         /* builder */
        //    60: invokevirtual   java/lang/ProcessBuilder.start:()Ljava/lang/Process;
        //    63: astore          process
        //    65: iconst_2       
        //    66: invokestatic    java/util/concurrent/Executors.newFixedThreadPool:(I)Ljava/util/concurrent/ExecutorService;
        //    69: astore_2        /* executor */
        //    70: aload_2         /* executor */
        //    71: aload           process
        //    73: invokedynamic   BootstrapMethod #0, run:(Ljava/lang/Process;)Ljava/lang/Runnable;
        //    78: invokeinterface java/util/concurrent/ExecutorService.submit:(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;
        //    83: astore          output
        //    85: aload_2         /* executor */
        //    86: aload           process
        //    88: invokedynamic   BootstrapMethod #1, run:(Ljava/lang/Process;)Ljava/lang/Runnable;
        //    93: invokeinterface java/util/concurrent/ExecutorService.submit:(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;
        //    98: astore          error
        //   100: aload_0         /* inputStream */
        //   101: aload           process
        //   103: invokevirtual   java/lang/Process.getOutputStream:()Ljava/io/OutputStream;
        //   106: invokestatic    org/apache/commons/io/IOUtils.copy:(Ljava/io/InputStream;Ljava/io/OutputStream;)I
        //   109: pop            
        //   110: aload           process
        //   112: invokevirtual   java/lang/Process.getOutputStream:()Ljava/io/OutputStream;
        //   115: invokevirtual   java/io/OutputStream.close:()V
        //   118: goto            134
        //   121: astore          8
        //   123: aload           process
        //   125: invokevirtual   java/lang/Process.getOutputStream:()Ljava/io/OutputStream;
        //   128: invokevirtual   java/io/OutputStream.close:()V
        //   131: aload           8
        //   133: athrow         
        //   134: aload           error
        //   136: invokeinterface java/util/concurrent/Future.get:()Ljava/lang/Object;
        //   141: pop            
        //   142: aload           output
        //   144: invokeinterface java/util/concurrent/Future.get:()Ljava/lang/Object;
        //   149: pop            
        //   150: aload_2         /* executor */
        //   151: ifnull          160
        //   154: aload_2         /* executor */
        //   155: invokeinterface java/util/concurrent/ExecutorService.shutdown:()V
        //   160: aload           process
        //   162: invokevirtual   java/lang/Process.waitFor:()I
        //   165: istore          exitCode
        //   167: goto            192
        //   170: astore          9
        //   172: aload_2         /* executor */
        //   173: ifnull          182
        //   176: aload_2         /* executor */
        //   177: invokeinterface java/util/concurrent/ExecutorService.shutdown:()V
        //   182: aload           process
        //   184: invokevirtual   java/lang/Process.waitFor:()I
        //   187: istore          exitCode
        //   189: aload           9
        //   191: athrow         
        //   192: iload           exitCode
        //   194: ifeq            226
        //   197: new             Ljava/io/IOException;
        //   200: dup            
        //   201: ldc             "Error executing command. %s Process exited with exit code %d."
        //   203: iconst_2       
        //   204: anewarray       Ljava/lang/Object;
        //   207: dup            
        //   208: iconst_0       
        //   209: aload_1         /* command */
        //   210: aastore        
        //   211: dup            
        //   212: iconst_1       
        //   213: iload           exitCode
        //   215: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   218: aastore        
        //   219: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   222: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   225: athrow         
        //   226: return         
        //    Exceptions:
        //  throws java.io.IOException
        //  throws java.lang.InterruptedException
        //  throws java.util.concurrent.ExecutionException
        //    StackMapTable: 00 0B FF 00 20 00 04 07 01 DA 07 01 BA 07 02 18 07 02 19 00 04 07 02 19 07 02 1A 07 02 1A 01 FF 00 01 00 04 07 01 DA 07 01 BA 07 02 18 07 02 19 00 05 07 02 19 07 02 1A 07 02 1A 01 07 01 BA FF 00 0D 00 04 07 01 DA 07 01 BA 07 02 18 07 02 19 00 04 07 02 19 07 02 1A 07 02 1A 01 FF 00 01 00 04 07 01 DA 07 01 BA 07 02 18 07 02 19 00 05 07 02 19 07 02 1A 07 02 1A 01 07 01 BA FF 00 46 00 08 07 01 DA 07 01 BA 07 02 18 07 02 19 07 02 1B 00 07 02 1C 07 02 1C 00 01 07 01 FB 0C F8 00 19 49 07 01 FB FF 00 0B 00 0A 07 01 DA 07 01 BA 07 02 18 07 02 19 07 02 1B 00 00 00 00 07 01 FB 00 00 FF 00 09 00 06 07 01 DA 07 01 BA 07 02 18 07 02 19 07 02 1B 01 00 00 21
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  100    110    121    134    Any
        //  121    123    121    134    Any
        //  65     150    170    192    Any
        //  170    172    170    192    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:374)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static void unTar(final InputStream inputStream, final File untarDir, final boolean gzipped) throws IOException, InterruptedException, ExecutionException {
        if (!untarDir.mkdirs() && !untarDir.isDirectory()) {
            throw new IOException("Mkdirs failed to create " + untarDir);
        }
        if (Shell.WINDOWS) {
            unTarUsingJava(inputStream, untarDir, gzipped);
        }
        else {
            unTarUsingTar(inputStream, untarDir, gzipped);
        }
    }
    
    public static void unTar(final File inFile, final File untarDir) throws IOException {
        if (!untarDir.mkdirs() && !untarDir.isDirectory()) {
            throw new IOException("Mkdirs failed to create " + untarDir);
        }
        final boolean gzipped = inFile.toString().endsWith("gz");
        if (Shell.WINDOWS) {
            unTarUsingJava(inFile, untarDir, gzipped);
        }
        else {
            unTarUsingTar(inFile, untarDir, gzipped);
        }
    }
    
    private static void unTarUsingTar(final InputStream inputStream, final File untarDir, final boolean gzipped) throws IOException, InterruptedException, ExecutionException {
        final StringBuilder untarCommand = new StringBuilder();
        if (gzipped) {
            untarCommand.append("gzip -dc | (");
        }
        untarCommand.append("cd '");
        untarCommand.append(makeSecureShellPath(untarDir));
        untarCommand.append("' && ");
        untarCommand.append("tar -x ");
        if (gzipped) {
            untarCommand.append(")");
        }
        runCommandOnStream(inputStream, untarCommand.toString());
    }
    
    private static void unTarUsingTar(final File inFile, final File untarDir, final boolean gzipped) throws IOException {
        final StringBuffer untarCommand = new StringBuffer();
        if (gzipped) {
            untarCommand.append(" gzip -dc '");
            untarCommand.append(makeSecureShellPath(inFile));
            untarCommand.append("' | (");
        }
        untarCommand.append("cd '");
        untarCommand.append(makeSecureShellPath(untarDir));
        untarCommand.append("' && ");
        untarCommand.append("tar -xf ");
        if (gzipped) {
            untarCommand.append(" -)");
        }
        else {
            untarCommand.append(makeSecureShellPath(inFile));
        }
        final String[] shellCmd = { "bash", "-c", untarCommand.toString() };
        final Shell.ShellCommandExecutor shexec = new Shell.ShellCommandExecutor(shellCmd);
        shexec.execute();
        final int exitcode = shexec.getExitCode();
        if (exitcode != 0) {
            throw new IOException("Error untarring file " + inFile + ". Tar process exited with exit code " + exitcode);
        }
    }
    
    static void unTarUsingJava(final File inFile, final File untarDir, final boolean gzipped) throws IOException {
        InputStream inputStream = null;
        TarArchiveInputStream tis = null;
        try {
            if (gzipped) {
                inputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(inFile)));
            }
            else {
                inputStream = new BufferedInputStream(new FileInputStream(inFile));
            }
            tis = new TarArchiveInputStream(inputStream);
            for (TarArchiveEntry entry = tis.getNextTarEntry(); entry != null; entry = tis.getNextTarEntry()) {
                unpackEntries(tis, entry, untarDir);
            }
        }
        finally {
            IOUtils.cleanupWithLogger(FileUtil.LOG, tis, inputStream);
        }
    }
    
    private static void unTarUsingJava(InputStream inputStream, final File untarDir, final boolean gzipped) throws IOException {
        TarArchiveInputStream tis = null;
        try {
            if (gzipped) {
                inputStream = new BufferedInputStream(new GZIPInputStream(inputStream));
            }
            else {
                inputStream = new BufferedInputStream(inputStream);
            }
            tis = new TarArchiveInputStream(inputStream);
            for (TarArchiveEntry entry = tis.getNextTarEntry(); entry != null; entry = tis.getNextTarEntry()) {
                unpackEntries(tis, entry, untarDir);
            }
        }
        finally {
            IOUtils.cleanupWithLogger(FileUtil.LOG, tis, inputStream);
        }
    }
    
    private static void unpackEntries(final TarArchiveInputStream tis, final TarArchiveEntry entry, final File outputDir) throws IOException {
        final String targetDirPath = outputDir.getCanonicalPath() + File.separator;
        final File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getCanonicalPath().startsWith(targetDirPath)) {
            throw new IOException("expanding " + entry.getName() + " would create entry outside of " + outputDir);
        }
        if (entry.isDirectory()) {
            final File subDir = new File(outputDir, entry.getName());
            if (!subDir.mkdirs() && !subDir.isDirectory()) {
                throw new IOException("Mkdirs failed to create tar internal dir " + outputDir);
            }
            for (final TarArchiveEntry e : entry.getDirectoryEntries()) {
                unpackEntries(tis, e, subDir);
            }
        }
        else {
            if (entry.isSymbolicLink()) {
                Files.createSymbolicLink(FileSystems.getDefault().getPath(outputDir.getPath(), entry.getName()), FileSystems.getDefault().getPath(entry.getLinkName(), new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
                return;
            }
            if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
                throw new IOException("Mkdirs failed to create tar internal dir " + outputDir);
            }
            if (entry.isLink()) {
                final File src = new File(outputDir, entry.getLinkName());
                org.apache.hadoop.fs.HardLink.createHardLink(src, outputFile);
                return;
            }
            final byte[] data = new byte[2048];
            try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                int count;
                while ((count = tis.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
            }
        }
    }
    
    public static int symLink(final String target, final String linkname) throws IOException {
        if (target == null || linkname == null) {
            FileUtil.LOG.warn("Can not create a symLink with a target = " + target + " and link =" + linkname);
            return 1;
        }
        final File targetFile = new File(Path.getPathWithoutSchemeAndAuthority(new Path(target)).toString());
        final File linkFile = new File(Path.getPathWithoutSchemeAndAuthority(new Path(linkname)).toString());
        final String[] cmd = Shell.getSymlinkCommand(targetFile.toString(), linkFile.toString());
        Shell.ShellCommandExecutor shExec;
        try {
            if (Shell.WINDOWS && linkFile.getParentFile() != null && !new Path(target).isAbsolute()) {
                shExec = new Shell.ShellCommandExecutor(cmd, linkFile.getParentFile());
            }
            else {
                shExec = new Shell.ShellCommandExecutor(cmd);
            }
            shExec.execute();
        }
        catch (Shell.ExitCodeException ec) {
            final int returnVal = ec.getExitCode();
            if (Shell.WINDOWS && returnVal == 2) {
                FileUtil.LOG.warn("Fail to create symbolic links on Windows. The default security settings in Windows disallow non-elevated administrators and all non-administrators from creating symbolic links. This behavior can be changed in the Local Security Policy management console");
            }
            else if (returnVal != 0) {
                FileUtil.LOG.warn("Command '" + StringUtils.join(" ", cmd) + "' failed " + returnVal + " with: " + ec.getMessage());
            }
            return returnVal;
        }
        catch (IOException e) {
            if (FileUtil.LOG.isDebugEnabled()) {
                FileUtil.LOG.debug("Error while create symlink " + linkname + " to " + target + ". Exception: " + StringUtils.stringifyException(e));
            }
            throw e;
        }
        return shExec.getExitCode();
    }
    
    public static int chmod(final String filename, final String perm) throws IOException, InterruptedException {
        return chmod(filename, perm, false);
    }
    
    public static int chmod(final String filename, final String perm, final boolean recursive) throws IOException {
        final String[] cmd = Shell.getSetPermissionCommand(perm, recursive);
        final String[] args = new String[cmd.length + 1];
        System.arraycopy(cmd, 0, args, 0, cmd.length);
        args[cmd.length] = new File(filename).getPath();
        final Shell.ShellCommandExecutor shExec = new Shell.ShellCommandExecutor(args);
        try {
            shExec.execute();
        }
        catch (IOException e) {
            if (FileUtil.LOG.isDebugEnabled()) {
                FileUtil.LOG.debug("Error while changing permission : " + filename + " Exception: " + StringUtils.stringifyException(e));
            }
        }
        return shExec.getExitCode();
    }
    
    public static void setOwner(final File file, final String username, final String groupname) throws IOException {
        if (username == null && groupname == null) {
            throw new IOException("username == null && groupname == null");
        }
        final String arg = ((username == null) ? "" : username) + ((groupname == null) ? "" : (":" + groupname));
        final String[] cmd = Shell.getSetOwnerCommand(arg);
        execCommand(file, cmd);
    }
    
    public static boolean setReadable(final File f, final boolean readable) {
        if (Shell.WINDOWS) {
            try {
                final String permission = readable ? "u+r" : "u-r";
                chmod(f.getCanonicalPath(), permission, false);
                return true;
            }
            catch (IOException ex) {
                return false;
            }
        }
        return f.setReadable(readable);
    }
    
    public static boolean setWritable(final File f, final boolean writable) {
        if (Shell.WINDOWS) {
            try {
                final String permission = writable ? "u+w" : "u-w";
                chmod(f.getCanonicalPath(), permission, false);
                return true;
            }
            catch (IOException ex) {
                return false;
            }
        }
        return f.setWritable(writable);
    }
    
    public static boolean setExecutable(final File f, final boolean executable) {
        if (Shell.WINDOWS) {
            try {
                final String permission = executable ? "u+x" : "u-x";
                chmod(f.getCanonicalPath(), permission, false);
                return true;
            }
            catch (IOException ex) {
                return false;
            }
        }
        return f.setExecutable(executable);
    }
    
    public static boolean canRead(final File f) {
        if (Shell.WINDOWS) {
            try {
                return NativeIO.Windows.access(f.getCanonicalPath(), NativeIO.Windows.AccessRight.ACCESS_READ);
            }
            catch (IOException e) {
                return false;
            }
        }
        return f.canRead();
    }
    
    public static boolean canWrite(final File f) {
        if (Shell.WINDOWS) {
            try {
                return NativeIO.Windows.access(f.getCanonicalPath(), NativeIO.Windows.AccessRight.ACCESS_WRITE);
            }
            catch (IOException e) {
                return false;
            }
        }
        return f.canWrite();
    }
    
    public static boolean canExecute(final File f) {
        if (Shell.WINDOWS) {
            try {
                return NativeIO.Windows.access(f.getCanonicalPath(), NativeIO.Windows.AccessRight.ACCESS_EXECUTE);
            }
            catch (IOException e) {
                return false;
            }
        }
        return f.canExecute();
    }
    
    public static void setPermission(final File f, final FsPermission permission) throws IOException {
        final FsAction user = permission.getUserAction();
        final FsAction group = permission.getGroupAction();
        final FsAction other = permission.getOtherAction();
        if (group != other || NativeIO.isAvailable() || Shell.WINDOWS) {
            execSetPermission(f, permission);
            return;
        }
        boolean rv = true;
        rv = f.setReadable(group.implies(FsAction.READ), false);
        checkReturnValue(rv, f, permission);
        if (group.implies(FsAction.READ) != user.implies(FsAction.READ)) {
            rv = f.setReadable(user.implies(FsAction.READ), true);
            checkReturnValue(rv, f, permission);
        }
        rv = f.setWritable(group.implies(FsAction.WRITE), false);
        checkReturnValue(rv, f, permission);
        if (group.implies(FsAction.WRITE) != user.implies(FsAction.WRITE)) {
            rv = f.setWritable(user.implies(FsAction.WRITE), true);
            checkReturnValue(rv, f, permission);
        }
        rv = f.setExecutable(group.implies(FsAction.EXECUTE), false);
        checkReturnValue(rv, f, permission);
        if (group.implies(FsAction.EXECUTE) != user.implies(FsAction.EXECUTE)) {
            rv = f.setExecutable(user.implies(FsAction.EXECUTE), true);
            checkReturnValue(rv, f, permission);
        }
    }
    
    private static void checkReturnValue(final boolean rv, final File p, final FsPermission permission) throws IOException {
        if (!rv) {
            throw new IOException("Failed to set permissions of path: " + p + " to " + String.format("%04o", permission.toShort()));
        }
    }
    
    private static void execSetPermission(final File f, final FsPermission permission) throws IOException {
        if (NativeIO.isAvailable()) {
            NativeIO.POSIX.chmod(f.getCanonicalPath(), permission.toShort());
        }
        else {
            execCommand(f, Shell.getSetPermissionCommand(String.format("%04o", permission.toShort()), false));
        }
    }
    
    static String execCommand(final File f, final String... cmd) throws IOException {
        final String[] args = new String[cmd.length + 1];
        System.arraycopy(cmd, 0, args, 0, cmd.length);
        args[cmd.length] = f.getCanonicalPath();
        final String output = Shell.execCommand(args);
        return output;
    }
    
    public static final File createLocalTempFile(final File basefile, final String prefix, final boolean isDeleteOnExit) throws IOException {
        final File tmp = File.createTempFile(prefix + basefile.getName(), "", basefile.getParentFile());
        if (isDeleteOnExit) {
            tmp.deleteOnExit();
        }
        return tmp;
    }
    
    public static void replaceFile(final File src, final File target) throws IOException {
        if (!src.renameTo(target)) {
            int retries = 5;
            while (target.exists() && !target.delete() && retries-- >= 0) {
                try {
                    Thread.sleep(1000L);
                    continue;
                }
                catch (InterruptedException e) {
                    throw new IOException("replaceFile interrupted.");
                }
                break;
            }
            if (!src.renameTo(target)) {
                throw new IOException("Unable to rename " + src + " to " + target);
            }
        }
    }
    
    public static File[] listFiles(final File dir) throws IOException {
        final File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("Invalid directory or I/O error occurred for dir: " + dir.toString());
        }
        return files;
    }
    
    public static String[] list(final File dir) throws IOException {
        if (!canRead(dir)) {
            throw new AccessDeniedException(dir.toString(), null, "Permission denied");
        }
        final String[] fileNames = dir.list();
        if (fileNames == null) {
            throw new IOException("Invalid directory or I/O error occurred for dir: " + dir.toString());
        }
        return fileNames;
    }
    
    public static String[] createJarWithClassPath(final String inputClassPath, final Path pwd, final Map<String, String> callerEnv) throws IOException {
        return createJarWithClassPath(inputClassPath, pwd, pwd, callerEnv);
    }
    
    public static String[] createJarWithClassPath(final String inputClassPath, final Path pwd, final Path targetDir, final Map<String, String> callerEnv) throws IOException {
        final Map<String, String> env = Shell.WINDOWS ? new CaseInsensitiveMap(callerEnv) : callerEnv;
        final String[] classPathEntries = inputClassPath.split(File.pathSeparator);
        for (int i = 0; i < classPathEntries.length; ++i) {
            classPathEntries[i] = StringUtils.replaceTokens(classPathEntries[i], StringUtils.ENV_VAR_PATTERN, env);
        }
        final File workingDir = new File(pwd.toString());
        if (!workingDir.mkdirs()) {
            FileUtil.LOG.debug("mkdirs false for " + workingDir + ", execution will continue");
        }
        final StringBuilder unexpandedWildcardClasspath = new StringBuilder();
        final List<String> classPathEntryList = new ArrayList<String>(classPathEntries.length);
        for (final String classPathEntry : classPathEntries) {
            if (classPathEntry.length() != 0) {
                if (classPathEntry.endsWith("*")) {
                    final List<Path> jars = getJarsInDirectory(classPathEntry);
                    if (!jars.isEmpty()) {
                        for (final Path jar : jars) {
                            classPathEntryList.add(jar.toUri().toURL().toExternalForm());
                        }
                    }
                    else {
                        unexpandedWildcardClasspath.append(File.pathSeparator);
                        unexpandedWildcardClasspath.append(classPathEntry);
                    }
                }
                else {
                    File fileCpEntry = null;
                    if (!new Path(classPathEntry).isAbsolute()) {
                        fileCpEntry = new File(targetDir.toString(), classPathEntry);
                    }
                    else {
                        fileCpEntry = new File(classPathEntry);
                    }
                    String classPathEntryUrl = fileCpEntry.toURI().toURL().toExternalForm();
                    if (classPathEntry.endsWith("/") && !classPathEntryUrl.endsWith("/")) {
                        classPathEntryUrl += "/";
                    }
                    classPathEntryList.add(classPathEntryUrl);
                }
            }
        }
        final String jarClassPath = StringUtils.join(" ", classPathEntryList);
        final Manifest jarManifest = new Manifest();
        jarManifest.getMainAttributes().putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
        jarManifest.getMainAttributes().putValue(Attributes.Name.CLASS_PATH.toString(), jarClassPath);
        final File classPathJar = File.createTempFile("classpath-", ".jar", workingDir);
        try (final FileOutputStream fos = new FileOutputStream(classPathJar);
             final BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            final JarOutputStream jos = new JarOutputStream(bos, jarManifest);
            jos.close();
        }
        final String[] jarCp = { classPathJar.getCanonicalPath(), unexpandedWildcardClasspath.toString() };
        return jarCp;
    }
    
    public static List<Path> getJarsInDirectory(final String path) {
        return getJarsInDirectory(path, true);
    }
    
    public static List<Path> getJarsInDirectory(String path, final boolean useLocal) {
        final List<Path> paths = new ArrayList<Path>();
        try {
            if (!path.endsWith("*")) {
                path = path + File.separator + "*";
            }
            final Path globPath = new Path(path).suffix("{.jar,.JAR}");
            final FileContext context = useLocal ? FileContext.getLocalFSFileContext() : FileContext.getFileContext(globPath.toUri());
            final FileStatus[] files = context.util().globStatus(globPath);
            if (files != null) {
                for (final FileStatus file : files) {
                    paths.add(file.getPath());
                }
            }
        }
        catch (IOException ex) {}
        return paths;
    }
    
    public static boolean compareFs(final FileSystem srcFs, final FileSystem destFs) {
        if (srcFs == null || destFs == null) {
            return false;
        }
        final URI srcUri = srcFs.getUri();
        final URI dstUri = destFs.getUri();
        if (srcUri.getScheme() == null) {
            return false;
        }
        if (!srcUri.getScheme().equals(dstUri.getScheme())) {
            return false;
        }
        String srcHost = srcUri.getHost();
        String dstHost = dstUri.getHost();
        if (srcHost != null && dstHost != null) {
            if (srcHost.equals(dstHost)) {
                return srcUri.getPort() == dstUri.getPort();
            }
            try {
                srcHost = InetAddress.getByName(srcHost).getCanonicalHostName();
                dstHost = InetAddress.getByName(dstHost).getCanonicalHostName();
            }
            catch (UnknownHostException ue) {
                if (FileUtil.LOG.isDebugEnabled()) {
                    FileUtil.LOG.debug("Could not compare file-systems. Unknown host: ", ue);
                }
                return false;
            }
            if (!srcHost.equals(dstHost)) {
                return false;
            }
        }
        else {
            if (srcHost == null && dstHost != null) {
                return false;
            }
            if (srcHost != null) {
                return false;
            }
        }
        return srcUri.getPort() == dstUri.getPort();
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileUtil.class);
    }
    
    @Deprecated
    public static class HardLink extends org.apache.hadoop.fs.HardLink
    {
    }
}
