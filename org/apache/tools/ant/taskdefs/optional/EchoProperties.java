// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.EnumeratedAttribute;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.Writer;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.tools.ant.util.DOMElementWriter;
import java.io.OutputStreamWriter;
import org.w3c.dom.Node;
import java.util.TreeSet;
import java.util.Comparator;
import org.apache.tools.ant.util.JavaEnvUtils;
import java.util.Set;
import org.apache.tools.ant.util.CollectionUtils;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.Enumeration;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.PropertySet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public class EchoProperties extends Task
{
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private File inFile;
    private File destfile;
    private boolean failonerror;
    private Vector propertySets;
    private String format;
    private String prefix;
    private String regex;
    
    public EchoProperties() {
        this.inFile = null;
        this.destfile = null;
        this.failonerror = true;
        this.propertySets = new Vector();
        this.format = "text";
    }
    
    public void setSrcfile(final File file) {
        this.inFile = file;
    }
    
    public void setDestfile(final File destfile) {
        this.destfile = destfile;
    }
    
    public void setFailOnError(final boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public void setPrefix(final String prefix) {
        if (prefix != null && prefix.length() != 0) {
            this.prefix = prefix;
            final PropertySet ps = new PropertySet();
            ps.setProject(this.getProject());
            ps.appendPrefix(prefix);
            this.addPropertyset(ps);
        }
    }
    
    public void setRegex(final String regex) {
        if (regex != null && regex.length() != 0) {
            this.regex = regex;
            final PropertySet ps = new PropertySet();
            ps.setProject(this.getProject());
            ps.appendRegex(regex);
            this.addPropertyset(ps);
        }
    }
    
    public void addPropertyset(final PropertySet ps) {
        this.propertySets.addElement(ps);
    }
    
    public void setFormat(final FormatAttribute ea) {
        this.format = ea.getValue();
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.prefix != null && this.regex != null) {
            throw new BuildException("Please specify either prefix or regex, but not both", this.getLocation());
        }
        final Hashtable allProps = new Hashtable();
        if (this.inFile == null && this.propertySets.size() == 0) {
            allProps.putAll(this.getProject().getProperties());
        }
        else if (this.inFile != null) {
            if (this.inFile.exists() && this.inFile.isDirectory()) {
                final String message = "srcfile is a directory!";
                if (this.failonerror) {
                    throw new BuildException(message, this.getLocation());
                }
                this.log(message, 0);
                return;
            }
            else if (this.inFile.exists() && !this.inFile.canRead()) {
                final String message = "Can not read from the specified srcfile!";
                if (this.failonerror) {
                    throw new BuildException(message, this.getLocation());
                }
                this.log(message, 0);
                return;
            }
            else {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(this.inFile);
                    final Properties props = new Properties();
                    props.load(in);
                    allProps.putAll(props);
                }
                catch (FileNotFoundException fnfe) {
                    final String message2 = "Could not find file " + this.inFile.getAbsolutePath();
                    if (this.failonerror) {
                        throw new BuildException(message2, fnfe, this.getLocation());
                    }
                    this.log(message2, 1);
                    return;
                }
                catch (IOException ioe) {
                    final String message2 = "Could not read file " + this.inFile.getAbsolutePath();
                    if (this.failonerror) {
                        throw new BuildException(message2, ioe, this.getLocation());
                    }
                    this.log(message2, 1);
                    return;
                }
                finally {
                    FileUtils.close(in);
                }
            }
        }
        final Enumeration e = this.propertySets.elements();
        while (e.hasMoreElements()) {
            final PropertySet ps = e.nextElement();
            allProps.putAll(ps.getProperties());
        }
        OutputStream os = null;
        try {
            if (this.destfile == null) {
                os = new ByteArrayOutputStream();
                this.saveProperties(allProps, os);
                this.log(os.toString(), 2);
            }
            else if (this.destfile.exists() && this.destfile.isDirectory()) {
                final String message2 = "destfile is a directory!";
                if (this.failonerror) {
                    throw new BuildException(message2, this.getLocation());
                }
                this.log(message2, 0);
                return;
            }
            else if (this.destfile.exists() && !this.destfile.canWrite()) {
                final String message2 = "Can not write to the specified destfile!";
                if (this.failonerror) {
                    throw new BuildException(message2, this.getLocation());
                }
                this.log(message2, 0);
                return;
            }
            else {
                os = new FileOutputStream(this.destfile);
                this.saveProperties(allProps, os);
            }
        }
        catch (IOException ioe2) {
            if (this.failonerror) {
                throw new BuildException(ioe2, this.getLocation());
            }
            this.log(ioe2.getMessage(), 2);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected void saveProperties(final Hashtable allProps, final OutputStream os) throws IOException, BuildException {
        final List keyList = new ArrayList(allProps.keySet());
        Collections.sort((List<Comparable>)keyList);
        final Properties props = new Properties() {
            private static final long serialVersionUID = 5090936442309201654L;
            
            @Override
            public Enumeration keys() {
                return CollectionUtils.asEnumeration(keyList.iterator());
            }
            
            @Override
            public Set entrySet() {
                Set result = super.entrySet();
                if (JavaEnvUtils.isKaffe()) {
                    final TreeSet t = new TreeSet(new Comparator() {
                        public int compare(final Object o1, final Object o2) {
                            final String key1 = ((Map.Entry)o1).getKey();
                            final String key2 = ((Map.Entry)o2).getKey();
                            return key1.compareTo(key2);
                        }
                    });
                    t.addAll(result);
                    result = t;
                }
                return result;
            }
        };
        for (int size = keyList.size(), i = 0; i < size; ++i) {
            final String name = keyList.get(i).toString();
            final String value = allProps.get(name).toString();
            props.setProperty(name, value);
        }
        if ("text".equals(this.format)) {
            this.jdkSaveProperties(props, os, "Ant properties");
        }
        else if ("xml".equals(this.format)) {
            this.xmlSaveProperties(props, os);
        }
    }
    
    private List sortProperties(final Properties props) {
        final List sorted = new ArrayList(props.size());
        final Enumeration e = props.propertyNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            sorted.add(new Tuple(name, props.getProperty(name)));
        }
        Collections.sort((List<Comparable>)sorted);
        return sorted;
    }
    
    protected void xmlSaveProperties(final Properties props, final OutputStream os) throws IOException {
        final Document doc = getDocumentBuilder().newDocument();
        final Element rootElement = doc.createElement("properties");
        final List sorted = this.sortProperties(props);
        for (final Tuple tuple : sorted) {
            final Element propElement = doc.createElement("property");
            propElement.setAttribute("name", tuple.key);
            propElement.setAttribute("value", tuple.value);
            rootElement.appendChild(propElement);
        }
        Writer wri = null;
        try {
            wri = new OutputStreamWriter(os, "UTF8");
            wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            new DOMElementWriter().write(rootElement, wri, 0, "\t");
            wri.flush();
        }
        catch (IOException ioe) {
            throw new BuildException("Unable to write XML file", ioe);
        }
        finally {
            FileUtils.close(wri);
        }
    }
    
    protected void jdkSaveProperties(final Properties props, final OutputStream os, final String header) throws IOException {
        try {
            props.store(os, header);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe, this.getLocation());
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ioex) {
                    this.log("Failed to close output stream");
                }
            }
        }
    }
    
    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static class FormatAttribute extends EnumeratedAttribute
    {
        private String[] formats;
        
        public FormatAttribute() {
            this.formats = new String[] { "xml", "text" };
        }
        
        @Override
        public String[] getValues() {
            return this.formats;
        }
    }
    
    private static final class Tuple implements Comparable
    {
        private String key;
        private String value;
        
        private Tuple(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        public int compareTo(final Object o) {
            final Tuple that = (Tuple)o;
            return this.key.compareTo(that.key);
        }
    }
}
