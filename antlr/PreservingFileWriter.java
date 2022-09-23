// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

public class PreservingFileWriter extends FileWriter
{
    protected File target_file;
    protected File tmp_file;
    
    public PreservingFileWriter(final String s) throws IOException {
        super(s + ".antlr.tmp");
        this.target_file = new File(s);
        final String parent = this.target_file.getParent();
        if (parent != null) {
            final File file = new File(parent);
            if (!file.exists()) {
                throw new IOException("destination directory of '" + s + "' doesn't exist");
            }
            if (!file.canWrite()) {
                throw new IOException("destination directory of '" + s + "' isn't writeable");
            }
        }
        if (this.target_file.exists() && !this.target_file.canWrite()) {
            throw new IOException("cannot write to '" + s + "'");
        }
        this.tmp_file = new File(s + ".antlr.tmp");
    }
    
    public void close() throws IOException {
        Reader reader = null;
        Writer writer = null;
        try {
            super.close();
            final char[] array = new char[1024];
            if (this.target_file.length() == this.tmp_file.length()) {
                final char[] array2 = new char[1024];
                reader = new BufferedReader(new FileReader(this.tmp_file));
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.target_file));
                int i;
                for (i = 1; i != 0; i = 0) {
                    final int read = reader.read(array, 0, 1024);
                    if (read != bufferedReader.read(array2, 0, 1024)) {
                        i = 0;
                        break;
                    }
                    if (read == -1) {
                        break;
                    }
                    for (int j = 0; j < read; ++j) {
                        if (array[j] != array2[j]) {
                            break;
                        }
                    }
                }
                reader.close();
                bufferedReader.close();
                reader = null;
                if (i != 0) {
                    return;
                }
            }
            reader = new BufferedReader(new FileReader(this.tmp_file));
            writer = new BufferedWriter(new FileWriter(this.target_file));
            while (true) {
                final int read2 = reader.read(array, 0, 1024);
                if (read2 == -1) {
                    break;
                }
                writer.write(array, 0, read2);
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {}
            }
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ex2) {}
            }
            if (this.tmp_file != null && this.tmp_file.exists()) {
                this.tmp_file.delete();
                this.tmp_file = null;
            }
        }
    }
}
