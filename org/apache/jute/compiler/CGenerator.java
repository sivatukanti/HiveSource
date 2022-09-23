// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

import java.util.Iterator;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

class CGenerator
{
    private String mName;
    private ArrayList<JFile> mInclFiles;
    private ArrayList<JRecord> mRecList;
    private final File outputDirectory;
    
    CGenerator(final String name, final ArrayList<JFile> ilist, final ArrayList<JRecord> rlist, final File outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.mName = new File(name).getName();
        this.mInclFiles = ilist;
        this.mRecList = rlist;
    }
    
    void genCode() throws IOException {
        if (!this.outputDirectory.exists() && !this.outputDirectory.mkdirs()) {
            throw new IOException("unable to create output directory " + this.outputDirectory);
        }
        FileWriter c = null;
        FileWriter h = null;
        try {
            c = new FileWriter(new File(this.outputDirectory, this.mName + ".c"));
            h = new FileWriter(new File(this.outputDirectory, this.mName + ".h"));
            h.write("/**\n");
            h.write("* Licensed to the Apache Software Foundation (ASF) under one\n");
            h.write("* or more contributor license agreements.  See the NOTICE file\n");
            h.write("* distributed with this work for additional information\n");
            h.write("* regarding copyright ownership.  The ASF licenses this file\n");
            h.write("* to you under the Apache License, Version 2.0 (the\n");
            h.write("* \"License\"); you may not use this file except in compliance\n");
            h.write("* with the License.  You may obtain a copy of the License at\n");
            h.write("*\n");
            h.write("*     http://www.apache.org/licenses/LICENSE-2.0\n");
            h.write("*\n");
            h.write("* Unless required by applicable law or agreed to in writing, software\n");
            h.write("* distributed under the License is distributed on an \"AS IS\" BASIS,\n");
            h.write("* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
            h.write("* See the License for the specific language governing permissions and\n");
            h.write("* limitations under the License.\n");
            h.write("*/\n");
            h.write("\n");
            c.write("/**\n");
            c.write("* Licensed to the Apache Software Foundation (ASF) under one\n");
            c.write("* or more contributor license agreements.  See the NOTICE file\n");
            c.write("* distributed with this work for additional information\n");
            c.write("* regarding copyright ownership.  The ASF licenses this file\n");
            c.write("* to you under the Apache License, Version 2.0 (the\n");
            c.write("* \"License\"); you may not use this file except in compliance\n");
            c.write("* with the License.  You may obtain a copy of the License at\n");
            c.write("*\n");
            c.write("*     http://www.apache.org/licenses/LICENSE-2.0\n");
            c.write("*\n");
            c.write("* Unless required by applicable law or agreed to in writing, software\n");
            c.write("* distributed under the License is distributed on an \"AS IS\" BASIS,\n");
            c.write("* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
            c.write("* See the License for the specific language governing permissions and\n");
            c.write("* limitations under the License.\n");
            c.write("*/\n");
            c.write("\n");
            h.write("#ifndef __" + this.mName.toUpperCase().replace('.', '_') + "__\n");
            h.write("#define __" + this.mName.toUpperCase().replace('.', '_') + "__\n");
            h.write("#include \"recordio.h\"\n");
            for (final JFile f : this.mInclFiles) {
                h.write("#include \"" + f.getName() + ".h\"\n");
            }
            h.write("\n#ifdef __cplusplus\nextern \"C\" {\n#endif\n\n");
            c.write("#include <stdlib.h>\n");
            c.write("#include \"" + this.mName + ".h\"\n\n");
            for (final JRecord jr : this.mRecList) {
                jr.genCCode(h, c);
            }
            h.write("\n#ifdef __cplusplus\n}\n#endif\n\n");
            h.write("#endif //" + this.mName.toUpperCase().replace('.', '_') + "__\n");
        }
        finally {
            try {
                if (h != null) {
                    h.close();
                }
            }
            finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }
}
