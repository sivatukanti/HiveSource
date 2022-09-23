// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.io.File;
import org.apache.tools.ant.Task;

public class KeySubst extends Task
{
    private File source;
    private File dest;
    private String sep;
    private Hashtable<String, String> replacements;
    
    public KeySubst() {
        this.source = null;
        this.dest = null;
        this.sep = "*";
        this.replacements = new Hashtable<String, String>();
    }
    
    @Override
    public void execute() throws BuildException {
        this.log("!! KeySubst is deprecated. Use Filter + Copy instead. !!");
        this.log("Performing Substitutions");
        if (this.source == null || this.dest == null) {
            this.log("Source and destinations must not be null");
            return;
        }
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(this.source));
            this.dest.delete();
            bw = new BufferedWriter(new FileWriter(this.dest));
            String line = null;
            String newline = null;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                if (line.length() == 0) {
                    bw.newLine();
                }
                else {
                    newline = replace(line, this.replacements);
                    bw.write(newline);
                    bw.newLine();
                }
            }
            bw.flush();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            FileUtils.close(bw);
            FileUtils.close(br);
        }
    }
    
    public void setSrc(final File s) {
        this.source = s;
    }
    
    public void setDest(final File dest) {
        this.dest = dest;
    }
    
    public void setSep(final String sep) {
        this.sep = sep;
    }
    
    public void setKeys(final String keys) {
        if (keys != null && keys.length() > 0) {
            final StringTokenizer tok = new StringTokenizer(keys, this.sep, false);
            while (tok.hasMoreTokens()) {
                final String token = tok.nextToken().trim();
                final StringTokenizer itok = new StringTokenizer(token, "=", false);
                final String name = itok.nextToken();
                final String value = itok.nextToken();
                this.replacements.put(name, value);
            }
        }
    }
    
    public static void main(final String[] args) {
        try {
            final Hashtable<String, String> hash = new Hashtable<String, String>();
            hash.put("VERSION", "1.0.3");
            hash.put("b", "ffff");
            System.out.println(replace("$f ${VERSION} f ${b} jj $", hash));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String replace(final String origString, final Hashtable<String, String> keys) throws BuildException {
        final StringBuffer finalString = new StringBuffer();
        int index = 0;
        int i = 0;
        for (String key = null; (index = origString.indexOf("${", i)) > -1; i = index + 3 + key.length()) {
            key = origString.substring(index + 2, origString.indexOf("}", index + 3));
            finalString.append(origString.substring(i, index));
            if (keys.containsKey(key)) {
                finalString.append(keys.get(key));
            }
            else {
                finalString.append("${");
                finalString.append(key);
                finalString.append("}");
            }
        }
        finalString.append(origString.substring(i));
        return finalString.toString();
    }
}
