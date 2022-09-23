// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

public class ClassFileUtils
{
    public static String convertSlashName(final String name) {
        return name.replace('\\', '.').replace('/', '.');
    }
    
    public static String convertDotName(final String dotName) {
        return dotName.replace('.', '/');
    }
}
