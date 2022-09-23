// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

final class CommandUtils
{
    static String formatDescription(final String usage, final String... desciptions) {
        final StringBuilder b = new StringBuilder(usage + ": " + desciptions[0]);
        for (int i = 1; i < desciptions.length; ++i) {
            b.append("\n\t\t" + desciptions[i]);
        }
        return b.toString();
    }
}
