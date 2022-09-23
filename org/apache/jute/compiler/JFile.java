// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class JFile
{
    private String mName;
    private ArrayList<JFile> mInclFiles;
    private ArrayList<JRecord> mRecords;
    
    public JFile(final String name, final ArrayList<JFile> inclFiles, final ArrayList<JRecord> recList) {
        this.mName = name;
        this.mInclFiles = inclFiles;
        this.mRecords = recList;
    }
    
    String getName() {
        final int idx = this.mName.lastIndexOf(47);
        return (idx > 0) ? this.mName.substring(idx) : this.mName;
    }
    
    public void genCode(final String language, final File outputDirectory) throws IOException {
        if ("c++".equals(language)) {
            final CppGenerator gen = new CppGenerator(this.mName, this.mInclFiles, this.mRecords, outputDirectory);
            gen.genCode();
        }
        else if ("java".equals(language)) {
            final JavaGenerator gen2 = new JavaGenerator(this.mName, this.mInclFiles, this.mRecords, outputDirectory);
            gen2.genCode();
        }
        else if ("c".equals(language)) {
            final CGenerator gen3 = new CGenerator(this.mName, this.mInclFiles, this.mRecords, outputDirectory);
            gen3.genCode();
        }
        else {
            if (!"csharp".equals(language)) {
                throw new IOException("Cannnot recognize language:" + language);
            }
            final CSharpGenerator gen4 = new CSharpGenerator(this.mName, this.mInclFiles, this.mRecords, outputDirectory);
            gen4.genCode();
        }
    }
}
