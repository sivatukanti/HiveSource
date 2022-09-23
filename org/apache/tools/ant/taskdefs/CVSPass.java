// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.tools.ant.util.StringUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class CVSPass extends Task
{
    private String cvsRoot;
    private File passFile;
    private String password;
    private final char[] shifts;
    
    public CVSPass() {
        this.cvsRoot = null;
        this.passFile = null;
        this.password = null;
        this.shifts = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', 'r', 'x', '5', 'O', '`', 'm', 'H', 'l', 'F', '@', 'L', 'C', 't', 'J', 'D', 'W', 'o', '4', 'K', 'w', '1', '\"', 'R', 'Q', '_', 'A', 'p', 'V', 'v', 'n', 'z', 'i', ')', '9', 'S', '+', '.', 'f', '(', 'Y', '&', 'g', '-', '2', '*', '{', '[', '#', '}', '7', '6', 'B', '|', '~', ';', '/', '\\', 'G', 's', 'N', 'X', 'k', 'j', '8', '$', 'y', 'u', 'h', 'e', 'd', 'E', 'I', 'c', '?', '^', ']', '\'', '%', '=', '0', ':', 'q', ' ', 'Z', ',', 'b', '<', '3', '!', 'a', '>', 'M', 'T', 'P', 'U', '\u00df', '\u00e1', '\u00d8', '»', '¦', '\u00e5', '½', '\u00de', '¼', '\u008d', '\u00f9', '\u0094', '\u00c8', '¸', '\u0088', '\u00f8', '¾', '\u00c7', 'ª', 'µ', '\u00cc', '\u008a', '\u00e8', '\u00da', '·', '\u00ff', '\u00ea', '\u00dc', '\u00f7', '\u00d5', '\u00cb', '\u00e2', '\u00c1', '®', '¬', '\u00e4', '\u00fc', '\u00d9', '\u00c9', '\u0083', '\u00e6', '\u00c5', '\u00d3', '\u0091', '\u00ee', '¡', '³', ' ', '\u00d4', '\u00cf', '\u00dd', '\u00fe', '\u00ad', '\u00ca', '\u0092', '\u00e0', '\u0097', '\u008c', '\u00c4', '\u00cd', '\u0082', '\u0087', '\u0085', '\u008f', '\u00f6', '\u00c0', '\u009f', '\u00f4', '\u00ef', '¹', '¨', '\u00d7', '\u0090', '\u008b', '¥', '´', '\u009d', '\u0093', 'º', '\u00d6', '°', '\u00e3', '\u00e7', '\u00db', '©', '¯', '\u009c', '\u00ce', '\u00c6', '\u0081', '¤', '\u0096', '\u00d2', '\u009a', '±', '\u0086', '\u007f', '¶', '\u0080', '\u009e', '\u00d0', '¢', '\u0084', '§', '\u00d1', '\u0095', '\u00f1', '\u0099', '\u00fb', '\u00ed', '\u00ec', '«', '\u00c3', '\u00f3', '\u00e9', '\u00fd', '\u00f0', '\u00c2', '\u00fa', '¿', '\u009b', '\u008e', '\u0089', '\u00f5', '\u00eb', '£', '\u00f2', '²', '\u0098' };
        this.passFile = new File(System.getProperty("cygwin.user.home", System.getProperty("user.home")) + File.separatorChar + ".cvspass");
    }
    
    @Override
    public final void execute() throws BuildException {
        if (this.cvsRoot == null) {
            throw new BuildException("cvsroot is required");
        }
        if (this.password == null) {
            throw new BuildException("password is required");
        }
        this.log("cvsRoot: " + this.cvsRoot, 4);
        this.log("password: " + this.password, 4);
        this.log("passFile: " + this.passFile, 4);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            final StringBuffer buf = new StringBuffer();
            if (this.passFile.exists()) {
                reader = new BufferedReader(new FileReader(this.passFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(this.cvsRoot)) {
                        buf.append(line).append(StringUtils.LINE_SEP);
                    }
                }
            }
            final String pwdfile = buf.toString() + this.cvsRoot + " A" + this.mangle(this.password);
            this.log("Writing -> " + pwdfile, 4);
            writer = new BufferedWriter(new FileWriter(this.passFile));
            writer.write(pwdfile);
            writer.newLine();
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {}
            }
            FileUtils.close(writer);
        }
    }
    
    private final String mangle(final String password) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < password.length(); ++i) {
            buf.append(this.shifts[password.charAt(i)]);
        }
        return buf.toString();
    }
    
    public void setCvsroot(final String cvsRoot) {
        this.cvsRoot = cvsRoot;
    }
    
    public void setPassfile(final File passFile) {
        this.passFile = passFile;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
}
