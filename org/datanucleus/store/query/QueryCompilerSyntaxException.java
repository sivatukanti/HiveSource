// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.exceptions.NucleusUserException;

public class QueryCompilerSyntaxException extends NucleusUserException
{
    public QueryCompilerSyntaxException(final String msg, final int position, final String stringToCompile) {
        super(msg + " at character " + (position + 1) + " in \"" + stringToCompile + '\"');
    }
    
    public QueryCompilerSyntaxException(final String msg) {
        super(msg);
    }
}
