// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.misc;

import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;

public class Stats
{
    public static final String ANTLRWORKS_DIR = "antlrworks";
    
    public static double stddev(final int[] X) {
        final int m = X.length;
        if (m <= 1) {
            return 0.0;
        }
        final double xbar = avg(X);
        double s2 = 0.0;
        for (int i = 0; i < m; ++i) {
            s2 += (X[i] - xbar) * (X[i] - xbar);
        }
        s2 /= m - 1;
        return Math.sqrt(s2);
    }
    
    public static double avg(final int[] X) {
        double xbar = 0.0;
        final int m = X.length;
        if (m == 0) {
            return 0.0;
        }
        for (int i = 0; i < m; ++i) {
            xbar += X[i];
        }
        if (xbar >= 0.0) {
            return xbar / m;
        }
        return 0.0;
    }
    
    public static int min(final int[] X) {
        int min = Integer.MAX_VALUE;
        final int m = X.length;
        if (m == 0) {
            return 0;
        }
        for (int i = 0; i < m; ++i) {
            if (X[i] < min) {
                min = X[i];
            }
        }
        return min;
    }
    
    public static int max(final int[] X) {
        int max = Integer.MIN_VALUE;
        final int m = X.length;
        if (m == 0) {
            return 0;
        }
        for (int i = 0; i < m; ++i) {
            if (X[i] > max) {
                max = X[i];
            }
        }
        return max;
    }
    
    public static double avg(final List<Integer> X) {
        double xbar = 0.0;
        final int m = X.size();
        if (m == 0) {
            return 0.0;
        }
        for (int i = 0; i < m; ++i) {
            xbar += X.get(i);
        }
        if (xbar >= 0.0) {
            return xbar / m;
        }
        return 0.0;
    }
    
    public static int min(final List<Integer> X) {
        int min = Integer.MAX_VALUE;
        final int m = X.size();
        if (m == 0) {
            return 0;
        }
        for (int i = 0; i < m; ++i) {
            if (X.get(i) < min) {
                min = X.get(i);
            }
        }
        return min;
    }
    
    public static int max(final List<Integer> X) {
        int max = Integer.MIN_VALUE;
        final int m = X.size();
        if (m == 0) {
            return 0;
        }
        for (int i = 0; i < m; ++i) {
            if (X.get(i) > max) {
                max = X.get(i);
            }
        }
        return max;
    }
    
    public static int sum(final int[] X) {
        int s = 0;
        final int m = X.length;
        if (m == 0) {
            return 0;
        }
        for (int i = 0; i < m; ++i) {
            s += X[i];
        }
        return s;
    }
    
    public static void writeReport(final String filename, final String data) throws IOException {
        final String absoluteFilename = getAbsoluteFileName(filename);
        final File f = new File(absoluteFilename);
        final File parent = f.getParentFile();
        parent.mkdirs();
        final FileOutputStream fos = new FileOutputStream(f, true);
        final BufferedOutputStream bos = new BufferedOutputStream(fos);
        final PrintStream ps = new PrintStream(bos);
        ps.println(data);
        ps.close();
        bos.close();
        fos.close();
    }
    
    public static String getAbsoluteFileName(final String filename) {
        return System.getProperty("user.home") + File.separator + "antlrworks" + File.separator + filename;
    }
}
