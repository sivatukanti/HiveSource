// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.conf;

import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import org.apache.hadoop.conf.Configuration;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;

public class SystemVariables
{
    private static final Log l4j;
    protected static Pattern varPat;
    public static final String ENV_PREFIX = "env:";
    public static final String SYSTEM_PREFIX = "system:";
    public static final String HIVECONF_PREFIX = "hiveconf:";
    public static final String HIVEVAR_PREFIX = "hivevar:";
    public static final String METACONF_PREFIX = "metaconf:";
    public static final String SET_COLUMN_NAME = "set";
    
    protected String getSubstitute(final Configuration conf, final String var) {
        String val = null;
        try {
            if (var.startsWith("system:")) {
                val = System.getProperty(var.substring("system:".length()));
            }
        }
        catch (SecurityException se) {
            SystemVariables.l4j.warn("Unexpected SecurityException in Configuration", se);
        }
        if (val == null && var.startsWith("env:")) {
            val = System.getenv(var.substring("env:".length()));
        }
        if (val == null && conf != null && var.startsWith("hiveconf:")) {
            val = conf.get(var.substring("hiveconf:".length()));
        }
        return val;
    }
    
    public static boolean containsVar(final String expr) {
        return expr != null && SystemVariables.varPat.matcher(expr).find();
    }
    
    static String substitute(final String expr) {
        return (expr == null) ? null : new SystemVariables().substitute(null, expr, 1);
    }
    
    static String substitute(final Configuration conf, final String expr) {
        return (expr == null) ? null : new SystemVariables().substitute(conf, expr, 1);
    }
    
    protected final String substitute(final Configuration conf, final String expr, final int depth) {
        final Matcher match = SystemVariables.varPat.matcher("");
        String eval = expr;
        final StringBuilder builder = new StringBuilder();
        int s;
        for (s = 0; s <= depth; ++s) {
            match.reset(eval);
            builder.setLength(0);
            int prev = 0;
            boolean found = false;
            while (match.find(prev)) {
                final String group = match.group();
                final String var = group.substring(2, group.length() - 1);
                String substitute = this.getSubstitute(conf, var);
                if (substitute == null) {
                    substitute = group;
                }
                else {
                    found = true;
                }
                builder.append(eval.substring(prev, match.start())).append(substitute);
                prev = match.end();
            }
            if (!found) {
                return eval;
            }
            builder.append(eval.substring(prev));
            eval = builder.toString();
        }
        if (s > depth) {
            throw new IllegalStateException("Variable substitution depth is deeper than " + depth + " for expression " + expr);
        }
        return eval;
    }
    
    static {
        l4j = LogFactory.getLog(SystemVariables.class);
        SystemVariables.varPat = Pattern.compile("\\$\\{[^\\}\\$ ]+\\}");
    }
}
