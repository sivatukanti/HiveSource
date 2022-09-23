// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.TaskContainer;
import java.util.Vector;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.IntrospectionHelper;
import java.util.Enumeration;
import org.apache.tools.ant.Project;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;
import java.io.UnsupportedEncodingException;
import java.io.FileWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class AntStructure extends Task
{
    private static final String LINE_SEP;
    private File output;
    private StructurePrinter printer;
    
    public AntStructure() {
        this.printer = new DTDPrinter();
    }
    
    public void setOutput(final File output) {
        this.output = output;
    }
    
    public void add(final StructurePrinter p) {
        this.printer = p;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.output == null) {
            throw new BuildException("output attribute is required", this.getLocation());
        }
        PrintWriter out = null;
        try {
            try {
                out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.output), "UTF8"));
            }
            catch (UnsupportedEncodingException ue) {
                out = new PrintWriter(new FileWriter(this.output));
            }
            this.printer.printHead(out, this.getProject(), new Hashtable<String, Class<?>>(this.getProject().getTaskDefinitions()), new Hashtable<String, Class<?>>(this.getProject().getDataTypeDefinitions()));
            this.printer.printTargetDecl(out);
            for (final String typeName : this.getProject().getCopyOfDataTypeDefinitions().keySet()) {
                this.printer.printElementDecl(out, this.getProject(), typeName, this.getProject().getDataTypeDefinitions().get(typeName));
            }
            for (final String tName : this.getProject().getCopyOfTaskDefinitions().keySet()) {
                this.printer.printElementDecl(out, this.getProject(), tName, this.getProject().getTaskDefinitions().get(tName));
            }
            this.printer.printTail(out);
            if (out.checkError()) {
                throw new IOException("Encountered an error writing Ant structure");
            }
        }
        catch (IOException ioe) {
            throw new BuildException("Error writing " + this.output.getAbsolutePath(), ioe, this.getLocation());
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected boolean isNmtoken(final String s) {
        return DTDPrinter.isNmtoken(s);
    }
    
    protected boolean areNmtokens(final String[] s) {
        return DTDPrinter.areNmtokens(s);
    }
    
    static {
        LINE_SEP = System.getProperty("line.separator");
    }
    
    private static class DTDPrinter implements StructurePrinter
    {
        private static final String BOOLEAN = "%boolean;";
        private static final String TASKS = "%tasks;";
        private static final String TYPES = "%types;";
        private Hashtable<String, String> visited;
        
        private DTDPrinter() {
            this.visited = new Hashtable<String, String>();
        }
        
        public void printTail(final PrintWriter out) {
            this.visited.clear();
        }
        
        public void printHead(final PrintWriter out, final Project p, final Hashtable<String, Class<?>> tasks, final Hashtable<String, Class<?>> types) {
            this.printHead(out, tasks.keys(), types.keys());
        }
        
        private void printHead(final PrintWriter out, final Enumeration<String> tasks, final Enumeration<String> types) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            out.println("<!ENTITY % boolean \"(true|false|on|off|yes|no)\">");
            out.print("<!ENTITY % tasks \"");
            boolean first = true;
            while (tasks.hasMoreElements()) {
                final String tName = tasks.nextElement();
                if (!first) {
                    out.print(" | ");
                }
                else {
                    first = false;
                }
                out.print(tName);
            }
            out.println("\">");
            out.print("<!ENTITY % types \"");
            first = true;
            while (types.hasMoreElements()) {
                final String typeName = types.nextElement();
                if (!first) {
                    out.print(" | ");
                }
                else {
                    first = false;
                }
                out.print(typeName);
            }
            out.println("\">");
            out.println("");
            out.print("<!ELEMENT project (target | extension-point | ");
            out.print("%tasks;");
            out.print(" | ");
            out.print("%types;");
            out.println(")*>");
            out.println("<!ATTLIST project");
            out.println("          name    CDATA #IMPLIED");
            out.println("          default CDATA #IMPLIED");
            out.println("          basedir CDATA #IMPLIED>");
            out.println("");
        }
        
        public void printTargetDecl(final PrintWriter out) {
            out.print("<!ELEMENT target (");
            out.print("%tasks;");
            out.print(" | ");
            out.print("%types;");
            out.println(")*>");
            out.println("");
            this.printTargetAttrs(out, "target");
            out.println("<!ELEMENT extension-point EMPTY>");
            out.println("");
            this.printTargetAttrs(out, "extension-point");
        }
        
        private void printTargetAttrs(final PrintWriter out, final String tag) {
            out.print("<!ATTLIST ");
            out.println(tag);
            out.println("          id                      ID    #IMPLIED");
            out.println("          name                    CDATA #REQUIRED");
            out.println("          if                      CDATA #IMPLIED");
            out.println("          unless                  CDATA #IMPLIED");
            out.println("          depends                 CDATA #IMPLIED");
            out.println("          extensionOf             CDATA #IMPLIED");
            out.println("          onMissingExtensionPoint CDATA #IMPLIED");
            out.println("          description             CDATA #IMPLIED>");
            out.println("");
        }
        
        public void printElementDecl(final PrintWriter out, final Project p, final String name, final Class<?> element) {
            if (this.visited.containsKey(name)) {
                return;
            }
            this.visited.put(name, "");
            IntrospectionHelper ih = null;
            try {
                ih = IntrospectionHelper.getHelper(p, element);
            }
            catch (Throwable t) {
                return;
            }
            StringBuffer sb = new StringBuffer("<!ELEMENT ");
            sb.append(name).append(" ");
            if (Reference.class.equals(element)) {
                sb.append("EMPTY>").append(AntStructure.LINE_SEP);
                sb.append("<!ATTLIST ").append(name);
                sb.append(AntStructure.LINE_SEP).append("          id ID #IMPLIED");
                sb.append(AntStructure.LINE_SEP).append("          refid IDREF #IMPLIED");
                sb.append(">").append(AntStructure.LINE_SEP);
                out.println(sb);
                return;
            }
            final Vector<String> v = new Vector<String>();
            if (ih.supportsCharacters()) {
                v.addElement("#PCDATA");
            }
            if (TaskContainer.class.isAssignableFrom(element)) {
                v.addElement("%tasks;");
            }
            Enumeration<String> e = ih.getNestedElements();
            while (e.hasMoreElements()) {
                v.addElement(e.nextElement());
            }
            if (v.isEmpty()) {
                sb.append("EMPTY");
            }
            else {
                sb.append("(");
                final int count = v.size();
                for (int i = 0; i < count; ++i) {
                    if (i != 0) {
                        sb.append(" | ");
                    }
                    sb.append(v.elementAt(i));
                }
                sb.append(")");
                if (count > 1 || !v.elementAt(0).equals("#PCDATA")) {
                    sb.append("*");
                }
            }
            sb.append(">");
            out.println(sb);
            sb = new StringBuffer("<!ATTLIST ");
            sb.append(name);
            sb.append(AntStructure.LINE_SEP).append("          id ID #IMPLIED");
            e = ih.getAttributes();
            while (e.hasMoreElements()) {
                final String attrName = e.nextElement();
                if ("id".equals(attrName)) {
                    continue;
                }
                sb.append(AntStructure.LINE_SEP).append("          ").append(attrName).append(" ");
                final Class<?> type = ih.getAttributeType(attrName);
                if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                    sb.append("%boolean;").append(" ");
                }
                else if (Reference.class.isAssignableFrom(type)) {
                    sb.append("IDREF ");
                }
                else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
                    try {
                        final EnumeratedAttribute ea = (EnumeratedAttribute)type.newInstance();
                        final String[] values = ea.getValues();
                        if (values == null || values.length == 0 || !areNmtokens(values)) {
                            sb.append("CDATA ");
                        }
                        else {
                            sb.append("(");
                            for (int j = 0; j < values.length; ++j) {
                                if (j != 0) {
                                    sb.append(" | ");
                                }
                                sb.append(values[j]);
                            }
                            sb.append(") ");
                        }
                    }
                    catch (InstantiationException ie) {
                        sb.append("CDATA ");
                    }
                    catch (IllegalAccessException ie2) {
                        sb.append("CDATA ");
                    }
                }
                else if (type.getSuperclass() != null && type.getSuperclass().getName().equals("java.lang.Enum")) {
                    try {
                        final Object[] values2 = (Object[])type.getMethod("values", (Class<?>[])null).invoke(null, (Object[])null);
                        if (values2.length == 0) {
                            sb.append("CDATA ");
                        }
                        else {
                            sb.append('(');
                            for (int k = 0; k < values2.length; ++k) {
                                if (k != 0) {
                                    sb.append(" | ");
                                }
                                sb.append(type.getMethod("name", (Class<?>[])null).invoke(values2[k], (Object[])null));
                            }
                            sb.append(") ");
                        }
                    }
                    catch (Exception x) {
                        sb.append("CDATA ");
                    }
                }
                else {
                    sb.append("CDATA ");
                }
                sb.append("#IMPLIED");
            }
            sb.append(">").append(AntStructure.LINE_SEP);
            out.println(sb);
            final int count = v.size();
            for (int i = 0; i < count; ++i) {
                final String nestedName = v.elementAt(i);
                if (!"#PCDATA".equals(nestedName) && !"%tasks;".equals(nestedName) && !"%types;".equals(nestedName)) {
                    this.printElementDecl(out, p, nestedName, ih.getElementType(nestedName));
                }
            }
        }
        
        public static final boolean isNmtoken(final String s) {
            for (int length = s.length(), i = 0; i < length; ++i) {
                final char c = s.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_' && c != ':') {
                    return false;
                }
            }
            return true;
        }
        
        public static final boolean areNmtokens(final String[] s) {
            for (int i = 0; i < s.length; ++i) {
                if (!isNmtoken(s[i])) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public interface StructurePrinter
    {
        void printHead(final PrintWriter p0, final Project p1, final Hashtable<String, Class<?>> p2, final Hashtable<String, Class<?>> p3);
        
        void printTargetDecl(final PrintWriter p0);
        
        void printElementDecl(final PrintWriter p0, final Project p1, final String p2, final Class<?> p3);
        
        void printTail(final PrintWriter p0);
    }
}
