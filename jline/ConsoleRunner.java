// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.List;
import java.util.StringTokenizer;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class ConsoleRunner
{
    public static final String property = "jline.history";
    
    public static void main(final String[] args) throws Exception {
        String historyFileName = null;
        final List argList = new ArrayList(Arrays.asList(args));
        if (argList.size() == 0) {
            usage();
            return;
        }
        historyFileName = System.getProperty("jline.history", null);
        final String mainClass = argList.remove(0);
        final ConsoleReader reader = new ConsoleReader();
        if (historyFileName != null) {
            reader.setHistory(new History(new File(System.getProperty("user.home"), ".jline-" + mainClass + "." + historyFileName + ".history")));
        }
        else {
            reader.setHistory(new History(new File(System.getProperty("user.home"), ".jline-" + mainClass + ".history")));
        }
        final String completors = System.getProperty(ConsoleRunner.class.getName() + ".completors", "");
        final List completorList = new ArrayList();
        final StringTokenizer tok = new StringTokenizer(completors, ",");
        while (tok.hasMoreTokens()) {
            completorList.add(Class.forName(tok.nextToken()).newInstance());
        }
        if (completorList.size() > 0) {
            reader.addCompletor(new ArgumentCompletor(completorList));
        }
        ConsoleReaderInputStream.setIn(reader);
        try {
            Class.forName(mainClass).getMethod("main", String[].class).invoke(null, argList.toArray(new String[0]));
        }
        finally {
            ConsoleReaderInputStream.restoreIn();
        }
    }
    
    private static void usage() {
        System.out.println("Usage: \n   java [-Djline.history='name'] " + ConsoleRunner.class.getName() + " <target class name> [args]" + "\n\nThe -Djline.history option will avoid history" + "\nmangling when running ConsoleRunner on the same application." + "\n\nargs will be passed directly to the target class name.");
    }
}
