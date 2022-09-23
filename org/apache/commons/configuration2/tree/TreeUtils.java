// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import java.util.Map;
import java.io.PrintStream;

public final class TreeUtils
{
    private TreeUtils() {
    }
    
    public static void printTree(final PrintStream stream, final ImmutableNode result) {
        if (stream != null) {
            printTree(stream, "", result);
        }
    }
    
    private static void printTree(final PrintStream stream, final String indent, final ImmutableNode result) {
        final StringBuilder buffer = new StringBuilder(indent).append("<").append(result.getNodeName());
        for (final Map.Entry<String, Object> e : result.getAttributes().entrySet()) {
            buffer.append(' ').append(e.getKey()).append("='").append(e.getValue()).append("'");
        }
        buffer.append(">");
        stream.print(buffer.toString());
        if (result.getValue() != null) {
            stream.print(result.getValue());
        }
        boolean newline = false;
        if (!result.getChildren().isEmpty()) {
            stream.print("\n");
            for (final ImmutableNode child : result.getChildren()) {
                printTree(stream, indent + "  ", child);
            }
            newline = true;
        }
        if (newline) {
            stream.print(indent);
        }
        stream.println("</" + result.getNodeName() + ">");
    }
}
