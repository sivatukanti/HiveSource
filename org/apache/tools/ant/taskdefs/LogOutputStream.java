// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.LineOrientedOutputStream;

public class LogOutputStream extends LineOrientedOutputStream
{
    private ProjectComponent pc;
    private int level;
    
    public LogOutputStream(final ProjectComponent pc) {
        this.level = 2;
        this.pc = pc;
    }
    
    public LogOutputStream(final Task task, final int level) {
        this((ProjectComponent)task, level);
    }
    
    public LogOutputStream(final ProjectComponent pc, final int level) {
        this(pc);
        this.level = level;
    }
    
    @Override
    protected void processBuffer() {
        try {
            super.processBuffer();
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible IOException caught: " + e);
        }
    }
    
    @Override
    protected void processLine(final String line) {
        this.processLine(line, this.level);
    }
    
    protected void processLine(final String line, final int level) {
        this.pc.log(line, level);
    }
    
    public int getMessageLevel() {
        return this.level;
    }
}
