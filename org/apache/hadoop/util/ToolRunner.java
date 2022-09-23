// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.ipc.CallerContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ToolRunner
{
    public static int run(Configuration conf, final Tool tool, final String[] args) throws Exception {
        if (CallerContext.getCurrent() == null) {
            final CallerContext ctx = new CallerContext.Builder("CLI").build();
            CallerContext.setCurrent(ctx);
        }
        if (conf == null) {
            conf = new Configuration();
        }
        final GenericOptionsParser parser = new GenericOptionsParser(conf, args);
        tool.setConf(conf);
        final String[] toolArgs = parser.getRemainingArgs();
        return tool.run(toolArgs);
    }
    
    public static int run(final Tool tool, final String[] args) throws Exception {
        return run(tool.getConf(), tool, args);
    }
    
    public static void printGenericCommandUsage(final PrintStream out) {
        GenericOptionsParser.printGenericCommandUsage(out);
    }
    
    public static boolean confirmPrompt(final String prompt) throws IOException {
        while (true) {
            System.err.print(prompt + " (Y or N) ");
            final StringBuilder responseBuilder = new StringBuilder();
            while (true) {
                final int c = System.in.read();
                if (c == -1 || c == 13 || c == 10) {
                    break;
                }
                responseBuilder.append((char)c);
            }
            final String response = responseBuilder.toString();
            if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
                return true;
            }
            if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
                return false;
            }
            System.err.println("Invalid input: " + response);
        }
    }
}
