// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.util.LineOrientedOutputStream;

class RedirectingOutputStream extends LineOrientedOutputStream
{
    private final ChangeLogParser parser;
    
    public RedirectingOutputStream(final ChangeLogParser parser) {
        this.parser = parser;
    }
    
    @Override
    protected void processLine(final String line) {
        this.parser.stdout(line);
    }
}
