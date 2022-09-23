// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.io.Closeable;
import java.io.FileInputStream;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import java.util.Iterator;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.hadoop.io.IOUtils;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.PathIsDirectoryException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class CopyCommands
{
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Merge.class, "-getmerge");
        factory.addClass(Cp.class, "-cp");
        factory.addClass(CopyFromLocal.class, "-copyFromLocal");
        factory.addClass(CopyToLocal.class, "-copyToLocal");
        factory.addClass(Get.class, "-get");
        factory.addClass(Put.class, "-put");
        factory.addClass(AppendToFile.class, "-appendToFile");
    }
    
    public static class Merge extends FsCommand
    {
        public static final String NAME = "getmerge";
        public static final String USAGE = "[-nl] [-skip-empty-file] <src> <localdst>";
        public static final String DESCRIPTION = "Get all the files in the directories that match the source file pattern and merge and sort them to only one file on local fs. <src> is kept.\n-nl: Add a newline character at the end of each file.\n-skip-empty-file: Do not add new line character for empty file.";
        protected PathData dst;
        protected String delimiter;
        private boolean skipEmptyFileDelimiter;
        protected List<PathData> srcs;
        
        public Merge() {
            this.dst = null;
            this.delimiter = null;
            this.srcs = null;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            try {
                final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "nl", "skip-empty-file" });
                cf.parse(args);
                this.delimiter = (cf.getOpt("nl") ? "\n" : null);
                this.skipEmptyFileDelimiter = cf.getOpt("skip-empty-file");
                this.dst = new PathData(new URI(args.removeLast()), this.getConf());
                if (this.dst.exists && this.dst.stat.isDirectory()) {
                    throw new PathIsDirectoryException(this.dst.toString());
                }
                this.srcs = new LinkedList<PathData>();
            }
            catch (URISyntaxException e) {
                throw new IOException("unexpected URISyntaxException", e);
            }
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> items) throws IOException {
            super.processArguments(items);
            if (this.exitCode != 0) {
                return;
            }
            final FSDataOutputStream out = this.dst.fs.create(this.dst.path);
            try {
                for (final PathData src : this.srcs) {
                    if (src.stat.getLen() != 0L) {
                        try (final FSDataInputStream in = src.fs.open(src.path)) {
                            IOUtils.copyBytes(in, out, this.getConf(), false);
                            this.writeDelimiter(out);
                        }
                    }
                    else {
                        if (this.skipEmptyFileDelimiter) {
                            continue;
                        }
                        this.writeDelimiter(out);
                    }
                }
            }
            finally {
                out.close();
            }
        }
        
        private void writeDelimiter(final FSDataOutputStream out) throws IOException {
            if (this.delimiter != null) {
                out.write(this.delimiter.getBytes("UTF-8"));
            }
        }
        
        @Override
        protected void processNonexistentPath(final PathData item) throws IOException {
            this.exitCode = 1;
            super.processNonexistentPath(item);
        }
        
        @Override
        protected void processPath(final PathData src) throws IOException {
            if (src.stat.isDirectory()) {
                if (this.getDepth() == 0) {
                    this.recursePath(src);
                }
            }
            else {
                this.srcs.add(src);
            }
        }
        
        @Override
        protected boolean isSorted() {
            return true;
        }
    }
    
    static class Cp extends CommandWithDestination
    {
        public static final String NAME = "cp";
        public static final String USAGE = "[-f] [-p | -p[topax]] [-d] <src> ... <dst>";
        public static final String DESCRIPTION = "Copy files that match the file pattern <src> to a destination.  When copying multiple files, the destination must be a directory. Passing -p preserves status [topax] (timestamps, ownership, permission, ACLs, XAttr). If -p is specified with no <arg>, then preserves timestamps, ownership, permission. If -pa is specified, then preserves permission also because ACL is a super-set of permission. Passing -f overwrites the destination if it already exists. raw namespace extended attributes are preserved if (1) they are supported (HDFS only) and, (2) all of the source and target pathnames are in the /.reserved/raw hierarchy. raw namespace xattr preservation is determined solely by the presence (or absence) of the /.reserved/raw prefix and not by the -p option. Passing -d will skip creation of temporary file(<dst>._COPYING_).\n";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            this.popPreserveOption(args);
            final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "f", "d" });
            cf.parse(args);
            this.setDirectWrite(cf.getOpt("d"));
            this.setOverwrite(cf.getOpt("f"));
            this.setRecursive(true);
            this.getRemoteDestination(args);
        }
        
        private void popPreserveOption(final List<String> args) {
            final Iterator<String> iter = args.iterator();
            while (iter.hasNext()) {
                final String cur = iter.next();
                if (cur.equals("--")) {
                    break;
                }
                if (cur.startsWith("-p")) {
                    iter.remove();
                    if (cur.length() == 2) {
                        this.setPreserve(true);
                    }
                    else {
                        final String attributes = cur.substring(2);
                        for (int index = 0; index < attributes.length(); ++index) {
                            this.preserve(FileAttribute.getAttribute(attributes.charAt(index)));
                        }
                    }
                }
            }
        }
    }
    
    public static class Get extends CommandWithDestination
    {
        public static final String NAME = "get";
        public static final String USAGE = "[-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst>";
        public static final String DESCRIPTION = "Copy files that match the file pattern <src> to the local name.  <src> is kept.  When copying multiple files, the destination must be a directory. Passing -f overwrites the destination if it already exists and -p preserves access and modification times, ownership and the mode.\n";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "crc", "ignoreCrc", "p", "f" });
            cf.parse(args);
            this.setWriteChecksum(cf.getOpt("crc"));
            this.setVerifyChecksum(!cf.getOpt("ignoreCrc"));
            this.setPreserve(cf.getOpt("p"));
            this.setOverwrite(cf.getOpt("f"));
            this.setRecursive(true);
            this.getLocalDestination(args);
        }
    }
    
    public static class Put extends CommandWithDestination
    {
        public static final String NAME = "put";
        public static final String USAGE = "[-f] [-p] [-l] [-d] <localsrc> ... <dst>";
        public static final String DESCRIPTION = "Copy files from the local file system into fs. Copying fails if the file already exists, unless the -f flag is given.\nFlags:\n  -p : Preserves access and modification times, ownership and the mode.\n  -f : Overwrites the destination if it already exists.\n  -l : Allow DataNode to lazily persist the file to disk. Forces\n       replication factor of 1. This flag will result in reduced\n       durability. Use with care.\n  -d : Skip creation of temporary file(<dst>._COPYING_).\n";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "f", "p", "l", "d" });
            cf.parse(args);
            this.setOverwrite(cf.getOpt("f"));
            this.setPreserve(cf.getOpt("p"));
            this.setLazyPersist(cf.getOpt("l"));
            this.setDirectWrite(cf.getOpt("d"));
            this.getRemoteDestination(args);
            this.setRecursive(true);
        }
        
        @Override
        protected List<PathData> expandArgument(final String arg) throws IOException {
            final List<PathData> items = new LinkedList<PathData>();
            try {
                items.add(new PathData(new URI(arg), this.getConf()));
            }
            catch (URISyntaxException e) {
                if (!Path.WINDOWS) {
                    throw new IOException("unexpected URISyntaxException", e);
                }
                items.add(new PathData(arg, this.getConf()));
            }
            return items;
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            if (args.size() == 1 && args.get(0).toString().equals("-")) {
                this.copyStreamToTarget(System.in, this.getTargetPath(args.get(0)));
                return;
            }
            super.processArguments(args);
        }
    }
    
    public static class CopyFromLocal extends Put
    {
        private ThreadPoolExecutor executor;
        private int numThreads;
        private static final int MAX_THREADS;
        public static final String NAME = "copyFromLocal";
        public static final String USAGE = "[-f] [-p] [-l] [-d] [-t <thread count>] <localsrc> ... <dst>";
        public static final String DESCRIPTION = "Copy files from the local file system into fs. Copying fails if the file already exists, unless the -f flag is given.\nFlags:\n  -p : Preserves access and modification times, ownership and the mode.\n  -f : Overwrites the destination if it already exists.\n  -t <thread count> : Number of threads to be used, default is 1.\n  -l : Allow DataNode to lazily persist the file to disk. Forces replication factor of 1. This flag will result in reduced durability. Use with care.\n  -d : Skip creation of temporary file(<dst>._COPYING_).\n";
        
        public CopyFromLocal() {
            this.executor = null;
            this.numThreads = 1;
        }
        
        private void setNumberThreads(final String numberThreadsString) {
            if (numberThreadsString == null) {
                this.numThreads = 1;
            }
            else {
                final int parsedValue = Integer.parseInt(numberThreadsString);
                if (parsedValue <= 1) {
                    this.numThreads = 1;
                }
                else if (parsedValue > CopyFromLocal.MAX_THREADS) {
                    this.numThreads = CopyFromLocal.MAX_THREADS;
                }
                else {
                    this.numThreads = parsedValue;
                }
            }
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "f", "p", "l", "d" });
            cf.addOptionWithValue("t");
            cf.parse(args);
            this.setNumberThreads(cf.getOptValue("t"));
            this.setOverwrite(cf.getOpt("f"));
            this.setPreserve(cf.getOpt("p"));
            this.setLazyPersist(cf.getOpt("l"));
            this.setDirectWrite(cf.getOpt("d"));
            this.getRemoteDestination(args);
            this.setRecursive(true);
        }
        
        private void copyFile(final PathData src, final PathData target) throws IOException {
            if (this.isPathRecursable(src)) {
                throw new PathIsDirectoryException(src.toString());
            }
            super.copyFileToTarget(src, target);
        }
        
        @Override
        protected void copyFileToTarget(final PathData src, final PathData target) throws IOException {
            if (this.numThreads == 1) {
                this.copyFile(src, target);
                return;
            }
            final Runnable task = () -> {
                try {
                    this.copyFile(src, target);
                }
                catch (IOException e) {
                    this.displayError(e);
                }
                return;
            };
            this.executor.submit(task);
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            this.executor = new ThreadPoolExecutor(this.numThreads, this.numThreads, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
            super.processArguments(args);
            this.executor.shutdown();
            try {
                this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            }
            catch (InterruptedException e) {
                this.executor.shutdownNow();
                this.displayError(e);
                Thread.currentThread().interrupt();
            }
        }
        
        @VisibleForTesting
        public int getNumThreads() {
            return this.numThreads;
        }
        
        @VisibleForTesting
        public ThreadPoolExecutor getExecutor() {
            return this.executor;
        }
        
        static {
            MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
        }
    }
    
    public static class CopyToLocal extends Get
    {
        public static final String NAME = "copyToLocal";
        public static final String USAGE = "[-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst>";
        public static final String DESCRIPTION = "Identical to the -get command.";
    }
    
    public static class AppendToFile extends CommandWithDestination
    {
        public static final String NAME = "appendToFile";
        public static final String USAGE = "<localsrc> ... <dst>";
        public static final String DESCRIPTION = "Appends the contents of all the given local files to the given dst file. The dst file will be created if it does not exist. If <localSrc> is -, then the input is read from stdin.";
        private static final int DEFAULT_IO_LENGTH = 1048576;
        boolean readStdin;
        
        public AppendToFile() {
            this.readStdin = false;
        }
        
        @Override
        protected List<PathData> expandArgument(final String arg) throws IOException {
            final List<PathData> items = new LinkedList<PathData>();
            if (arg.equals("-")) {
                this.readStdin = true;
            }
            else {
                try {
                    items.add(new PathData(new URI(arg), this.getConf()));
                }
                catch (URISyntaxException e) {
                    if (!Path.WINDOWS) {
                        throw new IOException("Unexpected URISyntaxException: " + e.toString());
                    }
                    items.add(new PathData(arg, this.getConf()));
                }
            }
            return items;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            if (args.size() < 2) {
                throw new IOException("missing destination argument");
            }
            this.getRemoteDestination(args);
            super.processOptions(args);
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            if (!this.dst.exists) {
                this.dst.fs.create(this.dst.path, false).close();
            }
            FileInputStream is = null;
            try (final FSDataOutputStream fos = this.dst.fs.append(this.dst.path)) {
                if (this.readStdin) {
                    if (args.size() != 0) {
                        throw new IOException("stdin (-) must be the sole input argument when present");
                    }
                    IOUtils.copyBytes(System.in, fos, 1048576);
                }
                for (final PathData source : args) {
                    is = new FileInputStream(source.toFile());
                    IOUtils.copyBytes(is, fos, 1048576);
                    IOUtils.closeStream(is);
                    is = null;
                }
            }
            finally {
                if (is != null) {
                    IOUtils.closeStream(is);
                }
            }
        }
    }
}
