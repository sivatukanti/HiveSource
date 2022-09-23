// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.Collections;
import java.io.File;
import java.util.List;

public class FileNameCompletor implements Completor
{
    public int complete(final String buf, final int cursor, final List candidates) {
        String translated;
        final String buffer = translated = ((buf == null) ? "" : buf);
        if (translated.startsWith("~" + File.separator)) {
            translated = System.getProperty("user.home") + translated.substring(1);
        }
        else if (translated.startsWith("~")) {
            translated = new File(System.getProperty("user.home")).getParentFile().getAbsolutePath();
        }
        else if (!translated.startsWith(File.separator)) {
            translated = new File("").getAbsolutePath() + File.separator + translated;
        }
        final File f = new File(translated);
        File dir;
        if (translated.endsWith(File.separator)) {
            dir = f;
        }
        else {
            dir = f.getParentFile();
        }
        final File[] entries = (dir == null) ? new File[0] : dir.listFiles();
        try {
            return this.matchFiles(buffer, translated, entries, candidates);
        }
        finally {
            this.sortFileNames(candidates);
        }
    }
    
    protected void sortFileNames(final List fileNames) {
        Collections.sort((List<Comparable>)fileNames);
    }
    
    public int matchFiles(final String buffer, final String translated, final File[] entries, final List candidates) {
        if (entries == null) {
            return -1;
        }
        int matches = 0;
        for (int i = 0; i < entries.length; ++i) {
            if (entries[i].getAbsolutePath().startsWith(translated)) {
                ++matches;
            }
        }
        for (int i = 0; i < entries.length; ++i) {
            if (entries[i].getAbsolutePath().startsWith(translated)) {
                final String name = entries[i].getName() + ((matches == 1 && entries[i].isDirectory()) ? File.separator : " ");
                candidates.add(name);
            }
        }
        final int index = buffer.lastIndexOf(File.separator);
        return index + File.separator.length();
    }
}
