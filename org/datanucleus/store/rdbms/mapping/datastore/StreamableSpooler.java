// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.lang.ref.Reference;
import java.util.HashSet;
import java.util.Collection;
import java.lang.ref.ReferenceQueue;
import org.datanucleus.util.NucleusLogger;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.datanucleus.exceptions.NucleusFatalUserException;
import java.io.File;

public class StreamableSpooler
{
    protected static StreamableSpooler _instance;
    protected StreamableSpoolerGC gcInstance;
    protected File spoolDirectory;
    
    public static StreamableSpooler instance() {
        return StreamableSpooler._instance;
    }
    
    private StreamableSpooler() {
        this.gcInstance = null;
        String spool = null;
        spool = System.getProperty("datanucleus.binarystream.spool.directory");
        if (spool != null) {
            final File f = new File(spool);
            if (!f.isDirectory()) {
                throw new NucleusFatalUserException("Invalid binarystream spool directory:" + spool);
            }
            this.spoolDirectory = f;
        }
        else {
            spool = System.getProperty("user.dir");
            if (spool != null) {
                final File f = new File(spool);
                if (!f.isDirectory()) {
                    throw new NucleusFatalUserException("Invalid binarystream spool directory:" + spool);
                }
                this.spoolDirectory = f;
            }
        }
        if (spool == null) {
            throw new NucleusFatalUserException("Cannot get binary stream spool directory");
        }
    }
    
    public void spoolStreamTo(final InputStream is, final File target) throws IOException {
        copyStream(is, new BufferedOutputStream(new FileOutputStream(target)), false, true);
    }
    
    public File spoolStream(final InputStream is) throws IOException {
        final File spool = File.createTempFile("datanucleus.binarystream-", ".bin", this.spoolDirectory);
        if (this.gcInstance == null) {
            this.gcInstance = new StreamableSpoolerGC();
        }
        this.gcInstance.add(spool);
        NucleusLogger.GENERAL.debug("spool file created: " + spool.getAbsolutePath());
        spool.deleteOnExit();
        copyStream(is, new BufferedOutputStream(new FileOutputStream(spool)), false, true);
        return spool;
    }
    
    public StreamableSpoolerGC getGCInstance() {
        return this.gcInstance;
    }
    
    public static void copyStream(final InputStream is, final OutputStream os) throws IOException {
        copyStream(is, os, false, false);
    }
    
    public static void copyStream(final InputStream is, final OutputStream os, final boolean close_src, final boolean close_dest) throws IOException {
        int b;
        while ((b = is.read()) != -1) {
            os.write(b);
        }
        if (close_src) {
            is.close();
        }
        if (close_dest) {
            os.close();
        }
    }
    
    static {
        StreamableSpooler._instance = new StreamableSpooler();
    }
    
    public class StreamableSpoolerGC extends Thread
    {
        protected ReferenceQueue refQ;
        protected Collection references;
        
        public StreamableSpoolerGC() {
            this.references = new HashSet();
            this.refQ = new ReferenceQueue();
            this.setDaemon(true);
            this.start();
        }
        
        public void add(final File f) {
            try {
                final FileWeakReference fwr = new FileWeakReference(f, this.refQ);
                this.references.add(fwr);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        final Reference ref = this.refQ.remove(0L);
                        final FileWeakReference fwr = (FileWeakReference)ref;
                        fwr.gc();
                        this.references.remove(fwr);
                    }
                }
                catch (IllegalArgumentException ex2) {
                    continue;
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                    for (final FileWeakReference fwr : this.references) {
                        System.err.println(fwr.getFilename() + " not gc'ed");
                    }
                }
                break;
            }
        }
        
        @Override
        public void interrupt() {
            System.gc();
            System.runFinalization();
            super.interrupt();
        }
    }
    
    class FileWeakReference extends WeakReference
    {
        protected String filename;
        
        public FileWeakReference(final File f) throws IOException {
            super(f);
            this.filename = f.getCanonicalPath();
        }
        
        public FileWeakReference(final File f, final ReferenceQueue refQ) throws IOException {
            super(f, refQ);
            this.filename = f.getCanonicalPath();
        }
        
        public void gc() {
            if (this.filename != null) {
                final File f = new File(this.filename);
                f.delete();
                System.err.println(this.filename + " deleted");
                this.filename = null;
            }
        }
        
        public String getFilename() {
            return this.filename;
        }
    }
}
