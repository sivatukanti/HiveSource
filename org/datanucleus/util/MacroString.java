// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;

public class MacroString
{
    protected static final Localiser LOCALISER;
    private final String thisClassName;
    private final Imports imports;
    private final String macroString;
    
    public MacroString(final String className, final String importsString, final String macroString) {
        this.thisClassName = className;
        this.macroString = macroString;
        this.imports = new Imports();
        if (this.thisClassName != null) {
            this.imports.importPackage(this.thisClassName);
        }
        if (importsString != null) {
            this.imports.parseImports(importsString);
        }
    }
    
    public String substituteMacros(final MacroHandler mh, final ClassLoaderResolver clr) {
        StringBuilder outBuf = new StringBuilder();
        int right;
        for (int curr = 0; curr < this.macroString.length(); curr = right + 1) {
            final int left;
            if ((left = this.macroString.indexOf(123, curr)) < 0) {
                outBuf.append(this.macroString.substring(curr));
                break;
            }
            outBuf.append(this.macroString.substring(curr, left));
            if ((right = this.macroString.indexOf(125, left + 1)) < 0) {
                throw new NucleusUserException(MacroString.LOCALISER.msg("031000", this.macroString));
            }
            final IdentifierMacro im = this.parseIdentifierMacro(this.macroString.substring(left + 1, right), clr);
            mh.onIdentifierMacro(im);
            outBuf.append(im.value);
        }
        final String tmpString = outBuf.toString();
        outBuf = new StringBuilder();
        for (int curr2 = 0; curr2 < tmpString.length(); curr2 = right + 1) {
            final int left;
            if ((left = tmpString.indexOf(63, curr2)) < 0) {
                outBuf.append(tmpString.substring(curr2));
                break;
            }
            outBuf.append(tmpString.substring(curr2, left));
            if ((right = tmpString.indexOf(63, left + 1)) < 0) {
                throw new NucleusUserException(MacroString.LOCALISER.msg("031001", tmpString));
            }
            final ParameterMacro pm = new ParameterMacro(tmpString.substring(left + 1, right));
            mh.onParameterMacro(pm);
            outBuf.append('?');
        }
        return outBuf.toString();
    }
    
    private Class resolveClassDeclaration(final String className, final ClassLoaderResolver clr) {
        try {
            return className.equals("this") ? clr.classForName(this.thisClassName, null) : this.imports.resolveClassDeclaration(className, clr, null);
        }
        catch (ClassNotResolvedException e) {
            return null;
        }
    }
    
    private IdentifierMacro parseIdentifierMacro(final String unparsed, final ClassLoaderResolver clr) {
        String className = null;
        String fieldName = null;
        String subfieldName = null;
        Class c = this.resolveClassDeclaration(unparsed, clr);
        if (c == null) {
            final int lastDot = unparsed.lastIndexOf(46);
            if (lastDot < 0) {
                throw new NucleusUserException(MacroString.LOCALISER.msg("031002", unparsed));
            }
            fieldName = unparsed.substring(lastDot + 1);
            className = unparsed.substring(0, lastDot);
            c = this.resolveClassDeclaration(className, clr);
            if (c == null) {
                final int lastDot2 = unparsed.lastIndexOf(46, lastDot - 1);
                if (lastDot2 < 0) {
                    throw new NucleusUserException(MacroString.LOCALISER.msg("031002", unparsed));
                }
                subfieldName = fieldName;
                fieldName = unparsed.substring(lastDot2 + 1, lastDot);
                className = unparsed.substring(0, lastDot2);
                c = this.resolveClassDeclaration(className, clr);
                if (c == null) {
                    throw new NucleusUserException(MacroString.LOCALISER.msg("031002", unparsed));
                }
            }
        }
        return new IdentifierMacro(unparsed, c.getName(), fieldName, subfieldName);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    public static class IdentifierMacro
    {
        public final String unparsed;
        public final String className;
        public final String fieldName;
        public final String subfieldName;
        public String value;
        
        IdentifierMacro(final String unparsed, final String className, final String fieldName, final String subfieldName) {
            this.unparsed = unparsed;
            this.className = className;
            this.fieldName = fieldName;
            this.subfieldName = subfieldName;
            this.value = null;
        }
        
        @Override
        public String toString() {
            return "{" + this.unparsed + "}";
        }
    }
    
    public static class ParameterMacro
    {
        public final String parameterName;
        
        ParameterMacro(final String parameterName) {
            this.parameterName = parameterName;
        }
        
        @Override
        public String toString() {
            return "?" + this.parameterName + "?";
        }
    }
    
    public interface MacroHandler
    {
        void onIdentifierMacro(final IdentifierMacro p0);
        
        void onParameterMacro(final ParameterMacro p0);
    }
}
