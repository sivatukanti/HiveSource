// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import javax.swing.JFrame;
import java.util.Enumeration;
import java.util.Iterator;
import java.net.URLConnection;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Set;
import java.io.File;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.Collection;
import java.util.Arrays;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.io.IOException;

public class ClassNameCompletor extends SimpleCompletor
{
    public ClassNameCompletor() throws IOException {
        this((SimpleCompletorFilter)null);
    }
    
    public ClassNameCompletor(final SimpleCompletorFilter filter) throws IOException {
        super(getClassNames(), filter);
        this.setDelimiter(".");
    }
    
    public static String[] getClassNames() throws IOException {
        final Set urls = new HashSet();
        for (ClassLoader loader = ClassNameCompletor.class.getClassLoader(); loader != null; loader = loader.getParent()) {
            if (loader instanceof URLClassLoader) {
                urls.addAll(Arrays.asList(((URLClassLoader)loader).getURLs()));
            }
        }
        final Class[] systemClasses = { String.class, JFrame.class };
        for (int i = 0; i < systemClasses.length; ++i) {
            final URL classURL = systemClasses[i].getResource("/" + systemClasses[i].getName().replace('.', '/') + ".class");
            if (classURL != null) {
                final URLConnection uc = classURL.openConnection();
                if (uc instanceof JarURLConnection) {
                    urls.add(((JarURLConnection)uc).getJarFileURL());
                }
            }
        }
        final Set classes = new HashSet();
        for (final URL url : urls) {
            final File file = new File(url.getFile());
            if (file.isDirectory()) {
                final Set files = getClassFiles(file.getAbsolutePath(), new HashSet(), file, new int[] { 200 });
                classes.addAll(files);
            }
            else {
                if (file == null) {
                    continue;
                }
                if (!file.isFile()) {
                    continue;
                }
                final JarFile jf = new JarFile(file);
                final Enumeration e = jf.entries();
                while (e.hasMoreElements()) {
                    final JarEntry entry = e.nextElement();
                    if (entry == null) {
                        continue;
                    }
                    final String name = entry.getName();
                    if (!name.endsWith(".class")) {
                        continue;
                    }
                    classes.add(name);
                }
            }
        }
        final Set classNames = new TreeSet();
        for (final String name2 : classes) {
            classNames.add(name2.replace('/', '.').substring(0, name2.length() - 6));
        }
        return classNames.toArray(new String[classNames.size()]);
    }
    
    private static Set getClassFiles(final String root, final Set holder, final File directory, final int[] maxDirectories) {
        if (maxDirectories[0]-- < 0) {
            return holder;
        }
        final File[] files = directory.listFiles();
        for (int i = 0; files != null && i < files.length; ++i) {
            final String name = files[i].getAbsolutePath();
            if (name.startsWith(root)) {
                if (files[i].isDirectory()) {
                    getClassFiles(root, holder, files[i], maxDirectories);
                }
                else if (files[i].getName().endsWith(".class")) {
                    holder.add(files[i].getAbsolutePath().substring(root.length() + 1));
                }
            }
        }
        return holder;
    }
}
