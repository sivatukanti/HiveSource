// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jlink;

import java.util.zip.CRC32;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import org.apache.tools.ant.util.FileUtils;
import java.io.File;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class jlink
{
    private static final int BUFFER_SIZE = 8192;
    private static final int VECTOR_INIT_SIZE = 10;
    private String outfile;
    private Vector mergefiles;
    private Vector addfiles;
    private boolean compression;
    byte[] buffer;
    
    public jlink() {
        this.outfile = null;
        this.mergefiles = new Vector(10);
        this.addfiles = new Vector(10);
        this.compression = false;
        this.buffer = new byte[8192];
    }
    
    public void setOutfile(final String outfile) {
        if (outfile == null) {
            return;
        }
        this.outfile = outfile;
    }
    
    public void addMergeFile(final String fileToMerge) {
        if (fileToMerge == null) {
            return;
        }
        this.mergefiles.addElement(fileToMerge);
    }
    
    public void addAddFile(final String fileToAdd) {
        if (fileToAdd == null) {
            return;
        }
        this.addfiles.addElement(fileToAdd);
    }
    
    public void addMergeFiles(final String[] filesToMerge) {
        if (filesToMerge == null) {
            return;
        }
        for (int i = 0; i < filesToMerge.length; ++i) {
            this.addMergeFile(filesToMerge[i]);
        }
    }
    
    public void addAddFiles(final String[] filesToAdd) {
        if (filesToAdd == null) {
            return;
        }
        for (int i = 0; i < filesToAdd.length; ++i) {
            this.addAddFile(filesToAdd[i]);
        }
    }
    
    public void setCompression(final boolean compress) {
        this.compression = compress;
    }
    
    public void link() throws Exception {
        final ZipOutputStream output = new ZipOutputStream(new FileOutputStream(this.outfile));
        if (this.compression) {
            output.setMethod(8);
            output.setLevel(-1);
        }
        else {
            output.setMethod(0);
        }
        final Enumeration merges = this.mergefiles.elements();
        while (merges.hasMoreElements()) {
            final String path = merges.nextElement();
            final File f = new File(path);
            if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip")) {
                this.mergeZipJarContents(output, f);
            }
            else {
                this.addAddFile(path);
            }
        }
        final Enumeration adds = this.addfiles.elements();
        while (adds.hasMoreElements()) {
            final String name = adds.nextElement();
            final File f2 = new File(name);
            if (f2.isDirectory()) {
                this.addDirContents(output, f2, f2.getName() + '/', this.compression);
            }
            else {
                this.addFile(output, f2, "", this.compression);
            }
        }
        FileUtils.close(output);
    }
    
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("usage: jlink output input1 ... inputN");
            System.exit(1);
        }
        final jlink linker = new jlink();
        linker.setOutfile(args[0]);
        for (int i = 1; i < args.length; ++i) {
            linker.addMergeFile(args[i]);
        }
        try {
            linker.link();
        }
        catch (Exception ex) {
            System.err.print(ex.getMessage());
        }
    }
    
    private void mergeZipJarContents(final ZipOutputStream output, final File f) throws IOException {
        if (!f.exists()) {
            return;
        }
        final ZipFile zipf = new ZipFile(f);
        final Enumeration entries = zipf.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry inputEntry = entries.nextElement();
            final String inputEntryName = inputEntry.getName();
            final int index = inputEntryName.indexOf("META-INF");
            if (index < 0) {
                try {
                    output.putNextEntry(this.processEntry(zipf, inputEntry));
                }
                catch (ZipException ex) {
                    final String mess = ex.getMessage();
                    if (mess.indexOf("duplicate") >= 0) {
                        continue;
                    }
                    throw ex;
                }
                final InputStream in = zipf.getInputStream(inputEntry);
                final int len = this.buffer.length;
                int count = -1;
                while ((count = in.read(this.buffer, 0, len)) > 0) {
                    output.write(this.buffer, 0, count);
                }
                in.close();
                output.closeEntry();
            }
        }
        zipf.close();
    }
    
    private void addDirContents(final ZipOutputStream output, final File dir, final String prefix, final boolean compress) throws IOException {
        final String[] contents = dir.list();
        for (int i = 0; i < contents.length; ++i) {
            final String name = contents[i];
            final File file = new File(dir, name);
            if (file.isDirectory()) {
                this.addDirContents(output, file, prefix + name + '/', compress);
            }
            else {
                this.addFile(output, file, prefix, compress);
            }
        }
    }
    
    private String getEntryName(final File file, final String prefix) {
        final String name = file.getName();
        if (!name.endsWith(".class")) {
            InputStream input = null;
            try {
                input = new FileInputStream(file);
                final String className = ClassNameReader.getClassName(input);
                if (className != null) {
                    return className.replace('.', '/') + ".class";
                }
            }
            catch (IOException ioe) {}
            finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        System.out.println("From " + file.getPath() + " and prefix " + prefix + ", creating entry " + prefix + name);
        return prefix + name;
    }
    
    private void addFile(final ZipOutputStream output, final File file, final String prefix, final boolean compress) throws IOException {
        if (!file.exists()) {
            return;
        }
        final ZipEntry entry = new ZipEntry(this.getEntryName(file, prefix));
        entry.setTime(file.lastModified());
        entry.setSize(file.length());
        if (!compress) {
            entry.setCrc(this.calcChecksum(file));
        }
        final FileInputStream input = new FileInputStream(file);
        this.addToOutputStream(output, input, entry);
    }
    
    private void addToOutputStream(final ZipOutputStream output, final InputStream input, final ZipEntry ze) throws IOException {
        try {
            output.putNextEntry(ze);
        }
        catch (ZipException zipEx) {
            input.close();
            return;
        }
        int numBytes = -1;
        while ((numBytes = input.read(this.buffer)) > 0) {
            output.write(this.buffer, 0, numBytes);
        }
        output.closeEntry();
        input.close();
    }
    
    private ZipEntry processEntry(final ZipFile zip, final ZipEntry inputEntry) {
        String name = inputEntry.getName();
        if (!inputEntry.isDirectory() && !name.endsWith(".class")) {
            try {
                final InputStream input = zip.getInputStream(zip.getEntry(name));
                final String className = ClassNameReader.getClassName(input);
                input.close();
                if (className != null) {
                    name = className.replace('.', '/') + ".class";
                }
            }
            catch (IOException ex) {}
        }
        final ZipEntry outputEntry = new ZipEntry(name);
        outputEntry.setTime(inputEntry.getTime());
        outputEntry.setExtra(inputEntry.getExtra());
        outputEntry.setComment(inputEntry.getComment());
        outputEntry.setTime(inputEntry.getTime());
        if (this.compression) {
            outputEntry.setMethod(8);
        }
        else {
            outputEntry.setMethod(0);
            outputEntry.setCrc(inputEntry.getCrc());
            outputEntry.setSize(inputEntry.getSize());
        }
        return outputEntry;
    }
    
    private long calcChecksum(final File f) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
        return this.calcChecksum(in);
    }
    
    private long calcChecksum(final InputStream in) throws IOException {
        final CRC32 crc = new CRC32();
        final int len = this.buffer.length;
        int count = -1;
        int haveRead = 0;
        while ((count = in.read(this.buffer, 0, len)) > 0) {
            haveRead += count;
            crc.update(this.buffer, 0, count);
        }
        in.close();
        return crc.getValue();
    }
}
