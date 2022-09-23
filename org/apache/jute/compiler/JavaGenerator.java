// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

import java.io.IOException;
import java.util.Iterator;
import java.io.File;
import java.util.ArrayList;

class JavaGenerator
{
    private ArrayList<JRecord> mRecList;
    private final File outputDirectory;
    
    JavaGenerator(final String name, final ArrayList<JFile> incl, final ArrayList<JRecord> records, final File outputDirectory) {
        this.mRecList = records;
        this.outputDirectory = outputDirectory;
    }
    
    void genCode() throws IOException {
        for (final JRecord rec : this.mRecList) {
            rec.genJavaCode(this.outputDirectory);
        }
    }
}
