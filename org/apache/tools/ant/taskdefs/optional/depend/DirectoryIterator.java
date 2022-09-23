// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.File;
import java.util.Enumeration;
import java.util.Stack;

public class DirectoryIterator implements ClassFileIterator
{
    private Stack enumStack;
    private Enumeration currentEnum;
    
    public DirectoryIterator(final File rootDirectory, final boolean changeInto) throws IOException {
        this.enumStack = new Stack();
        final Vector filesInRoot = this.getDirectoryEntries(rootDirectory);
        this.currentEnum = filesInRoot.elements();
    }
    
    private Vector getDirectoryEntries(final File directory) {
        final Vector files = new Vector();
        final String[] filesInDir = directory.list();
        if (filesInDir != null) {
            for (int length = filesInDir.length, i = 0; i < length; ++i) {
                files.addElement(new File(directory, filesInDir[i]));
            }
        }
        return files;
    }
    
    public ClassFile getNextClassFile() {
        ClassFile nextElement = null;
        try {
            while (nextElement == null) {
                if (this.currentEnum.hasMoreElements()) {
                    final File element = this.currentEnum.nextElement();
                    if (element.isDirectory()) {
                        this.enumStack.push(this.currentEnum);
                        final Vector files = this.getDirectoryEntries(element);
                        this.currentEnum = files.elements();
                    }
                    else {
                        final FileInputStream inFileStream = new FileInputStream(element);
                        if (!element.getName().endsWith(".class")) {
                            continue;
                        }
                        final ClassFile javaClass = new ClassFile();
                        javaClass.read(inFileStream);
                        nextElement = javaClass;
                    }
                }
                else {
                    if (this.enumStack.empty()) {
                        break;
                    }
                    this.currentEnum = this.enumStack.pop();
                }
            }
        }
        catch (IOException e) {
            nextElement = null;
        }
        return nextElement;
    }
}
