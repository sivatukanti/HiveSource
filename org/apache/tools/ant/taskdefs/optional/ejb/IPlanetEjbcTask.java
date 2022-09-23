// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.Task;

public class IPlanetEjbcTask extends Task
{
    private File ejbdescriptor;
    private File iasdescriptor;
    private File dest;
    private Path classpath;
    private boolean keepgenerated;
    private boolean debug;
    private File iashome;
    
    public IPlanetEjbcTask() {
        this.keepgenerated = false;
        this.debug = false;
    }
    
    public void setEjbdescriptor(final File ejbdescriptor) {
        this.ejbdescriptor = ejbdescriptor;
    }
    
    public void setIasdescriptor(final File iasdescriptor) {
        this.iasdescriptor = iasdescriptor;
    }
    
    public void setDest(final File dest) {
        this.dest = dest;
    }
    
    public void setClasspath(final Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        }
        else {
            this.classpath.append(classpath);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setKeepgenerated(final boolean keepgenerated) {
        this.keepgenerated = keepgenerated;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setIashome(final File iashome) {
        this.iashome = iashome;
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        this.executeEjbc(this.getParser());
    }
    
    private void checkConfiguration() throws BuildException {
        if (this.ejbdescriptor == null) {
            final String msg = "The standard EJB descriptor must be specified using the \"ejbdescriptor\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.ejbdescriptor.exists() || !this.ejbdescriptor.isFile()) {
            final String msg = "The standard EJB descriptor (" + this.ejbdescriptor + ") was not found or isn't a file.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.iasdescriptor == null) {
            final String msg = "The iAS-speific XML descriptor must be specified using the \"iasdescriptor\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.iasdescriptor.exists() || !this.iasdescriptor.isFile()) {
            final String msg = "The iAS-specific XML descriptor (" + this.iasdescriptor + ") was not found or isn't a file.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.dest == null) {
            final String msg = "The destination directory must be specified using the \"dest\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.dest.exists() || !this.dest.isDirectory()) {
            final String msg = "The destination directory (" + this.dest + ") was not " + "found or isn't a directory.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.iashome != null && !this.iashome.isDirectory()) {
            final String msg = "If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").";
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private SAXParser getParser() throws BuildException {
        SAXParser saxParser = null;
        try {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(true);
            saxParser = saxParserFactory.newSAXParser();
        }
        catch (SAXException e) {
            final String msg = "Unable to create a SAXParser: " + e.getMessage();
            throw new BuildException(msg, e, this.getLocation());
        }
        catch (ParserConfigurationException e2) {
            final String msg = "Unable to create a SAXParser: " + e2.getMessage();
            throw new BuildException(msg, e2, this.getLocation());
        }
        return saxParser;
    }
    
    private void executeEjbc(final SAXParser saxParser) throws BuildException {
        final IPlanetEjbc ejbc = new IPlanetEjbc(this.ejbdescriptor, this.iasdescriptor, this.dest, this.getClasspath().toString(), saxParser);
        ejbc.setRetainSource(this.keepgenerated);
        ejbc.setDebugOutput(this.debug);
        if (this.iashome != null) {
            ejbc.setIasHomeDir(this.iashome);
        }
        try {
            ejbc.execute();
        }
        catch (IOException e) {
            final String msg = "An IOException occurred while trying to read the XML descriptor file: " + e.getMessage();
            throw new BuildException(msg, e, this.getLocation());
        }
        catch (SAXException e2) {
            final String msg = "A SAXException occurred while trying to read the XML descriptor file: " + e2.getMessage();
            throw new BuildException(msg, e2, this.getLocation());
        }
        catch (IPlanetEjbc.EjbcException e3) {
            final String msg = "An exception occurred while trying to run the ejbc utility: " + e3.getMessage();
            throw new BuildException(msg, e3, this.getLocation());
        }
    }
    
    private Path getClasspath() {
        Path cp = null;
        if (this.classpath == null) {
            cp = new Path(this.getProject()).concatSystemClasspath("last");
        }
        else {
            cp = this.classpath.concatSystemClasspath("ignore");
        }
        return cp;
    }
}
