// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.Task;
import org.xml.sax.HandlerBase;

public class DescriptorHandler extends HandlerBase
{
    private static final int DEFAULT_HASH_TABLE_SIZE = 10;
    private static final int STATE_LOOKING_EJBJAR = 1;
    private static final int STATE_IN_EJBJAR = 2;
    private static final int STATE_IN_BEANS = 3;
    private static final int STATE_IN_SESSION = 4;
    private static final int STATE_IN_ENTITY = 5;
    private static final int STATE_IN_MESSAGE = 6;
    private Task owningTask;
    private String publicId;
    private static final String EJB_REF = "ejb-ref";
    private static final String EJB_LOCAL_REF = "ejb-local-ref";
    private static final String HOME_INTERFACE = "home";
    private static final String REMOTE_INTERFACE = "remote";
    private static final String LOCAL_HOME_INTERFACE = "local-home";
    private static final String LOCAL_INTERFACE = "local";
    private static final String BEAN_CLASS = "ejb-class";
    private static final String PK_CLASS = "prim-key-class";
    private static final String EJB_NAME = "ejb-name";
    private static final String EJB_JAR = "ejb-jar";
    private static final String ENTERPRISE_BEANS = "enterprise-beans";
    private static final String ENTITY_BEAN = "entity";
    private static final String SESSION_BEAN = "session";
    private static final String MESSAGE_BEAN = "message-driven";
    private int parseState;
    protected String currentElement;
    protected String currentText;
    protected Hashtable ejbFiles;
    protected String ejbName;
    private Hashtable fileDTDs;
    private Hashtable resourceDTDs;
    private boolean inEJBRef;
    private Hashtable urlDTDs;
    private File srcDir;
    
    public DescriptorHandler(final Task task, final File srcDir) {
        this.publicId = null;
        this.parseState = 1;
        this.currentElement = null;
        this.currentText = null;
        this.ejbFiles = null;
        this.ejbName = null;
        this.fileDTDs = new Hashtable();
        this.resourceDTDs = new Hashtable();
        this.inEJBRef = false;
        this.urlDTDs = new Hashtable();
        this.owningTask = task;
        this.srcDir = srcDir;
    }
    
    public void registerDTD(final String publicId, final String location) {
        if (location == null) {
            return;
        }
        File fileDTD = new File(location);
        if (!fileDTD.exists()) {
            fileDTD = this.owningTask.getProject().resolveFile(location);
        }
        if (fileDTD.exists()) {
            if (publicId != null) {
                this.fileDTDs.put(publicId, fileDTD);
                this.owningTask.log("Mapped publicId " + publicId + " to file " + fileDTD, 3);
            }
            return;
        }
        if (this.getClass().getResource(location) != null && publicId != null) {
            this.resourceDTDs.put(publicId, location);
            this.owningTask.log("Mapped publicId " + publicId + " to resource " + location, 3);
        }
        try {
            if (publicId != null) {
                final URL urldtd = new URL(location);
                this.urlDTDs.put(publicId, urldtd);
            }
        }
        catch (MalformedURLException ex) {}
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        this.publicId = publicId;
        final File dtdFile = this.fileDTDs.get(publicId);
        if (dtdFile != null) {
            try {
                this.owningTask.log("Resolved " + publicId + " to local file " + dtdFile, 3);
                return new InputSource(new FileInputStream(dtdFile));
            }
            catch (FileNotFoundException ex) {}
        }
        final String dtdResourceName = this.resourceDTDs.get(publicId);
        if (dtdResourceName != null) {
            final InputStream is = this.getClass().getResourceAsStream(dtdResourceName);
            if (is != null) {
                this.owningTask.log("Resolved " + publicId + " to local resource " + dtdResourceName, 3);
                return new InputSource(is);
            }
        }
        final URL dtdUrl = this.urlDTDs.get(publicId);
        if (dtdUrl != null) {
            try {
                final InputStream is2 = dtdUrl.openStream();
                this.owningTask.log("Resolved " + publicId + " to url " + dtdUrl, 3);
                return new InputSource(is2);
            }
            catch (IOException ex2) {}
        }
        this.owningTask.log("Could not resolve ( publicId: " + publicId + ", systemId: " + systemId + ") to a local entity", 2);
        return null;
    }
    
    public Hashtable getFiles() {
        return (this.ejbFiles == null) ? new Hashtable() : this.ejbFiles;
    }
    
    public String getPublicId() {
        return this.publicId;
    }
    
    public String getEjbName() {
        return this.ejbName;
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.ejbFiles = new Hashtable(10, 1.0f);
        this.currentElement = null;
        this.inEJBRef = false;
    }
    
    @Override
    public void startElement(final String name, final AttributeList attrs) throws SAXException {
        this.currentElement = name;
        this.currentText = "";
        if (name.equals("ejb-ref") || name.equals("ejb-local-ref")) {
            this.inEJBRef = true;
        }
        else if (this.parseState == 1 && name.equals("ejb-jar")) {
            this.parseState = 2;
        }
        else if (this.parseState == 2 && name.equals("enterprise-beans")) {
            this.parseState = 3;
        }
        else if (this.parseState == 3 && name.equals("session")) {
            this.parseState = 4;
        }
        else if (this.parseState == 3 && name.equals("entity")) {
            this.parseState = 5;
        }
        else if (this.parseState == 3 && name.equals("message-driven")) {
            this.parseState = 6;
        }
    }
    
    @Override
    public void endElement(final String name) throws SAXException {
        this.processElement();
        this.currentText = "";
        this.currentElement = "";
        if (name.equals("ejb-ref") || name.equals("ejb-local-ref")) {
            this.inEJBRef = false;
        }
        else if (this.parseState == 5 && name.equals("entity")) {
            this.parseState = 3;
        }
        else if (this.parseState == 4 && name.equals("session")) {
            this.parseState = 3;
        }
        else if (this.parseState == 6 && name.equals("message-driven")) {
            this.parseState = 3;
        }
        else if (this.parseState == 3 && name.equals("enterprise-beans")) {
            this.parseState = 2;
        }
        else if (this.parseState == 2 && name.equals("ejb-jar")) {
            this.parseState = 1;
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.currentText += new String(ch, start, length);
    }
    
    protected void processElement() {
        if (this.inEJBRef || (this.parseState != 5 && this.parseState != 4 && this.parseState != 6)) {
            return;
        }
        if (this.currentElement.equals("home") || this.currentElement.equals("remote") || this.currentElement.equals("local") || this.currentElement.equals("local-home") || this.currentElement.equals("ejb-class") || this.currentElement.equals("prim-key-class")) {
            File classFile = null;
            String className = this.currentText.trim();
            if (!className.startsWith("java.") && !className.startsWith("javax.")) {
                className = className.replace('.', File.separatorChar);
                className += ".class";
                classFile = new File(this.srcDir, className);
                this.ejbFiles.put(className, classFile);
            }
        }
        if (this.currentElement.equals("ejb-name") && this.ejbName == null) {
            this.ejbName = this.currentText.trim();
        }
    }
}
