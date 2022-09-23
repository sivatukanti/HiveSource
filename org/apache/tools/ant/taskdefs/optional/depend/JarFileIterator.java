// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class JarFileIterator implements ClassFileIterator
{
    private ZipInputStream jarStream;
    
    public JarFileIterator(final InputStream stream) throws IOException {
        this.jarStream = new ZipInputStream(stream);
    }
    
    public ClassFile getNextClassFile() {
        ClassFile nextElement = null;
        try {
            ZipEntry jarEntry = this.jarStream.getNextEntry();
            while (nextElement == null && jarEntry != null) {
                final String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                    final ClassFile javaClass = new ClassFile();
                    javaClass.read(this.jarStream);
                    nextElement = javaClass;
                }
                else {
                    jarEntry = this.jarStream.getNextEntry();
                }
            }
        }
        catch (IOException e) {
            final String message = e.getMessage();
            String text = e.getClass().getName();
            if (message != null) {
                text = text + ": " + message;
            }
            throw new RuntimeException("Problem reading JAR file: " + text);
        }
        return nextElement;
    }
}
