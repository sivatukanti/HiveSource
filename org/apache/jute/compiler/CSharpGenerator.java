// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

import java.io.IOException;
import java.util.Iterator;
import java.io.File;
import java.util.ArrayList;

public class CSharpGenerator
{
    private ArrayList<JRecord> mRecList;
    private final File outputDirectory;
    
    CSharpGenerator(final String name, final ArrayList<JFile> ilist, final ArrayList<JRecord> rlist, final File outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.mRecList = rlist;
    }
    
    void genCode() throws IOException {
        for (final JRecord rec : this.mRecList) {
            rec.genCsharpCode(this.outputDirectory);
        }
    }
}
