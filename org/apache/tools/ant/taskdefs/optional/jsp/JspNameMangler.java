// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp;

import org.apache.tools.ant.util.StringUtils;
import java.io.File;

public class JspNameMangler implements JspMangler
{
    public static final String[] keywords;
    
    public String mapJspToJavaName(final File jspFile) {
        return this.mapJspToBaseName(jspFile) + ".java";
    }
    
    private String mapJspToBaseName(final File jspFile) {
        String className = this.stripExtension(jspFile);
        for (int i = 0; i < JspNameMangler.keywords.length; ++i) {
            if (className.equals(JspNameMangler.keywords[i])) {
                className += "%";
                break;
            }
        }
        final StringBuffer modifiedClassName = new StringBuffer(className.length());
        final char firstChar = className.charAt(0);
        if (Character.isJavaIdentifierStart(firstChar)) {
            modifiedClassName.append(firstChar);
        }
        else {
            modifiedClassName.append(mangleChar(firstChar));
        }
        for (int j = 1; j < className.length(); ++j) {
            final char subChar = className.charAt(j);
            if (Character.isJavaIdentifierPart(subChar)) {
                modifiedClassName.append(subChar);
            }
            else {
                modifiedClassName.append(mangleChar(subChar));
            }
        }
        return modifiedClassName.toString();
    }
    
    private String stripExtension(final File jspFile) {
        return StringUtils.removeSuffix(jspFile.getName(), ".jsp");
    }
    
    private static String mangleChar(char ch) {
        if (ch == File.separatorChar) {
            ch = '/';
        }
        final String s = Integer.toHexString(ch);
        final int nzeros = 5 - s.length();
        final char[] result = new char[6];
        result[0] = '_';
        for (int i = 1; i <= nzeros; ++i) {
            result[i] = '0';
        }
        int resultIndex = 0;
        for (int j = nzeros + 1; j < 6; ++j) {
            result[j] = s.charAt(resultIndex++);
        }
        return new String(result);
    }
    
    public String mapPath(final String path) {
        return null;
    }
    
    static {
        keywords = new String[] { "assert", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };
    }
}
