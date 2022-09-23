// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

public class Jasper41Mangler implements JspMangler
{
    public String mapJspToJavaName(final File jspFile) {
        final String jspUri = jspFile.getAbsolutePath();
        final int start = jspUri.lastIndexOf(File.separatorChar) + 1;
        final int end = jspUri.length();
        final StringBuffer modifiedClassName = new StringBuffer(jspUri.length() - start);
        if (!Character.isJavaIdentifierStart(jspUri.charAt(start)) || jspUri.charAt(start) == '_') {
            modifiedClassName.append('_');
        }
        for (int i = start; i < end; ++i) {
            final char ch = jspUri.charAt(i);
            if (Character.isJavaIdentifierPart(ch)) {
                modifiedClassName.append(ch);
            }
            else if (ch == '.') {
                modifiedClassName.append('_');
            }
            else {
                modifiedClassName.append(mangleChar(ch));
            }
        }
        return modifiedClassName.toString();
    }
    
    private static String mangleChar(final char ch) {
        final String s = Integer.toHexString(ch);
        final int nzeros = 5 - s.length();
        final char[] result = new char[6];
        result[0] = '_';
        for (int i = 1; i <= nzeros; ++i) {
            result[i] = '0';
        }
        for (int i = nzeros + 1, j = 0; i < 6; ++i, ++j) {
            result[i] = s.charAt(j);
        }
        return new String(result);
    }
    
    public String mapPath(final String path) {
        return null;
    }
}
