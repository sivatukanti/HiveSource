// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import org.apache.commons.logging.LogFactory;
import com.google.common.io.Files;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class HiveTestUtils
{
    public static final Log LOG;
    public static final String JAVA_FILE_EXT = ".java";
    public static final String CLAZZ_FILE_EXT = ".class";
    public static final String JAR_FILE_EXT = ".jar";
    public static final String TXT_FILE_EXT = ".txt";
    
    public static String getFileFromClasspath(final String name) {
        final URL url = ClassLoader.getSystemResource(name);
        if (url == null) {
            throw new IllegalArgumentException("Could not find " + name);
        }
        return url.getPath();
    }
    
    private static void executeCmd(final String[] cmdArr, final File dir) throws IOException, InterruptedException {
        final Process p1 = Runtime.getRuntime().exec(cmdArr, null, dir);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final BufferedReader input = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
                try {
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                catch (IOException e) {
                    HiveTestUtils.LOG.error("Failed to execute the command due the exception " + e);
                }
            }
        }).start();
        p1.waitFor();
    }
    
    public static File genLocalJarForTest(final String pathToClazzFile, final String clazzName) throws IOException, InterruptedException {
        final String u = pathToClazzFile;
        final File dir = new File(u);
        final File parentDir = dir.getParentFile();
        final File f = new File(parentDir, clazzName + ".java");
        Files.copy(dir, f);
        executeCmd(new String[] { "javac", clazzName + ".java" }, parentDir);
        executeCmd(new String[] { "jar", "cf", clazzName + ".jar", clazzName + ".class" }, parentDir);
        f.delete();
        new File(parentDir, clazzName + ".class").delete();
        return new File(parentDir, clazzName + ".jar");
    }
    
    static {
        LOG = LogFactory.getLog(HiveTestUtils.class);
    }
}
