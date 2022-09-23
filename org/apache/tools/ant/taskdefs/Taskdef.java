// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;

public class Taskdef extends Typedef
{
    public Taskdef() {
        this.setAdapterClass(TaskAdapter.class);
        this.setAdaptToClass(Task.class);
    }
}
