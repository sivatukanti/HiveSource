// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

import java.util.Iterator;
import java.io.InputStream;
import java.util.Enumeration;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.util.depend.AbstractAnalyzer;

public class AntAnalyzer extends AbstractAnalyzer
{
    @Override
    protected void determineDependencies(final Vector<File> files, final Vector<String> classes) {
        final Hashtable<String, String> dependencies = new Hashtable<String, String>();
        final Hashtable<File, File> containers = new Hashtable<File, File>();
        final Hashtable<String, String> toAnalyze = new Hashtable<String, String>();
        final Enumeration<String> e = this.getRootClasses();
        while (e.hasMoreElements()) {
            final String classname = e.nextElement();
            toAnalyze.put(classname, classname);
        }
        int count = 0;
        final int maxCount = this.isClosureRequired() ? 1000 : 1;
        Hashtable<String, String> analyzedDeps = null;
        while (toAnalyze.size() != 0 && count++ < maxCount) {
            analyzedDeps = new Hashtable<String, String>();
            final Enumeration<String> e2 = toAnalyze.keys();
            while (e2.hasMoreElements()) {
                final String classname2 = e2.nextElement();
                dependencies.put(classname2, classname2);
                try {
                    final File container = this.getClassContainer(classname2);
                    if (container == null) {
                        continue;
                    }
                    containers.put(container, container);
                    ZipFile zipFile = null;
                    InputStream inStream = null;
                    try {
                        if (container.getName().endsWith(".class")) {
                            inStream = new FileInputStream(container.getPath());
                        }
                        else {
                            zipFile = new ZipFile(container.getPath());
                            final String entryName = classname2.replace('.', '/') + ".class";
                            final ZipEntry entry = new ZipEntry(entryName);
                            inStream = zipFile.getInputStream(entry);
                        }
                        final ClassFile classFile = new ClassFile();
                        classFile.read(inStream);
                        for (final String dependency : classFile.getClassRefs()) {
                            analyzedDeps.put(dependency, dependency);
                        }
                    }
                    finally {
                        FileUtils.close(inStream);
                        if (zipFile != null) {
                            zipFile.close();
                        }
                    }
                }
                catch (IOException ex) {}
            }
            toAnalyze.clear();
            for (final String className : analyzedDeps.values()) {
                if (!dependencies.containsKey(className)) {
                    toAnalyze.put(className, className);
                }
            }
        }
        for (final String className : analyzedDeps.values()) {
            dependencies.put(className, className);
        }
        files.removeAllElements();
        for (final File f : containers.keySet()) {
            files.add(f);
        }
        classes.removeAllElements();
        for (final String dependency2 : dependencies.keySet()) {
            classes.add(dependency2);
        }
    }
    
    @Override
    protected boolean supportsFileDependencies() {
        return true;
    }
}
