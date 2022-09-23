// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.List;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.shell.CommandFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class Classpath
{
    private static final String usage = "classpath [--glob|--jar <path>|-h|--help] :\n  Prints the classpath needed to get the Hadoop jar and the required\n  libraries.\n  Options:\n\n  --glob       expand wildcards\n  --jar <path> write classpath as manifest in jar named <path>\n  -h, --help   print help\n";
    
    public static void main(final String[] args) {
        if (args.length < 1 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println("classpath [--glob|--jar <path>|-h|--help] :\n  Prints the classpath needed to get the Hadoop jar and the required\n  libraries.\n  Options:\n\n  --glob       expand wildcards\n  --jar <path> write classpath as manifest in jar named <path>\n  -h, --help   print help\n");
            return;
        }
        final List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        final CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "-glob", "-jar" });
        try {
            cf.parse(argsList);
        }
        catch (CommandFormat.UnknownOptionException e3) {
            terminate(1, "unrecognized option");
            return;
        }
        final String classPath = System.getProperty("java.class.path");
        if (cf.getOpt("-glob")) {
            System.out.println(classPath);
        }
        else if (cf.getOpt("-jar")) {
            if (argsList.isEmpty() || argsList.get(0) == null || argsList.get(0).isEmpty()) {
                terminate(1, "-jar option requires path of jar file to write");
                return;
            }
            final Path workingDir = new Path(System.getProperty("user.dir"));
            String tmpJarPath;
            try {
                tmpJarPath = FileUtil.createJarWithClassPath(classPath, workingDir, System.getenv())[0];
            }
            catch (IOException e) {
                terminate(1, "I/O error creating jar: " + e.getMessage());
                return;
            }
            final String jarPath = argsList.get(0);
            try {
                FileUtil.replaceFile(new File(tmpJarPath), new File(jarPath));
            }
            catch (IOException e2) {
                terminate(1, "I/O error renaming jar temporary file to path: " + e2.getMessage());
            }
        }
    }
    
    private static void terminate(final int status, final String msg) {
        System.err.println(msg);
        ExitUtil.terminate(status, msg);
    }
}
