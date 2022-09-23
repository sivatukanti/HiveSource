// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.util.Locale;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.derby.iapi.services.io.FileUtil;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.security.PrivilegedExceptionAction;

final class ExportWriteData extends ExportWriteDataAbstract implements PrivilegedExceptionAction
{
    private String outputFileName;
    private String lobsFileName;
    private boolean lobsInExtFile;
    private long lobFileOffset;
    private OutputStreamWriter aStream;
    private OutputStreamWriter lobCharStream;
    private BufferedOutputStream lobOutBinaryStream;
    private ByteArrayOutputStream lobByteArrayStream;
    private byte[] byteBuf;
    private char[] charBuf;
    
    ExportWriteData(final String outputFileName, final ControlInfo controlFileReader) throws Exception {
        this.lobsInExtFile = false;
        this.lobFileOffset = 0L;
        this.outputFileName = outputFileName;
        this.controlFileReader = controlFileReader;
        this.init();
    }
    
    ExportWriteData(final String outputFileName, final String lobsFileName, final ControlInfo controlFileReader) throws Exception {
        this.lobsInExtFile = false;
        this.lobFileOffset = 0L;
        this.outputFileName = outputFileName;
        this.lobsFileName = lobsFileName;
        this.controlFileReader = controlFileReader;
        this.lobsInExtFile = true;
        this.byteBuf = new byte[8192];
        this.charBuf = new char[8192];
        this.init();
    }
    
    private void init() throws Exception {
        this.loadPropertiesInfo();
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            throw ex.getException();
        }
    }
    
    public final Object run() throws Exception {
        this.openFiles();
        return null;
    }
    
    private void openFiles() throws Exception {
        this.outputFileName = FileUtil.stripProtocolFromFileName(this.outputFileName);
        if (this.lobsInExtFile) {
            this.lobsFileName = FileUtil.stripProtocolFromFileName(this.lobsFileName);
        }
        FileOutputStream out = null;
        FilterOutputStream filterOutputStream = null;
        FileOutputStream out2 = null;
        try {
            final File file = new File(this.outputFileName);
            out = new FileOutputStream(this.outputFileName);
            FileUtil.limitAccessToOwner(file);
            filterOutputStream = new BufferedOutputStream(out);
            this.aStream = ((this.dataCodeset == null) ? new OutputStreamWriter(filterOutputStream) : new OutputStreamWriter(filterOutputStream, this.dataCodeset));
            if (this.lobsInExtFile) {
                File file2 = new File(this.lobsFileName);
                if (file2.getParentFile() == null) {
                    file2 = new File(new File(this.outputFileName).getParentFile(), this.lobsFileName);
                }
                out2 = new FileOutputStream(file2);
                FileUtil.limitAccessToOwner(file2);
                this.lobOutBinaryStream = new BufferedOutputStream(out2);
                this.lobByteArrayStream = new ByteArrayOutputStream();
                this.lobCharStream = ((this.dataCodeset == null) ? new OutputStreamWriter(this.lobByteArrayStream) : new OutputStreamWriter(this.lobByteArrayStream, this.dataCodeset));
            }
        }
        catch (Exception ex) {
            if (this.aStream == null) {
                if (filterOutputStream != null) {
                    filterOutputStream.close();
                }
                else if (out != null) {
                    out.close();
                }
            }
            else {
                this.aStream.close();
                if (this.lobOutBinaryStream != null) {
                    this.lobOutBinaryStream.close();
                }
                else if (out2 != null) {
                    out2.close();
                }
            }
            throw ex;
        }
    }
    
    void writeColumnDefinitionOptionally(final String[] array, final String[] array2) throws Exception {
        final boolean b = true;
        if (this.columnDefinition.toUpperCase(Locale.ENGLISH).equals("True".toUpperCase(Locale.ENGLISH))) {
            for (int i = 0; i < array.length; ++i) {
                String fieldSeparator;
                if (i > 0) {
                    fieldSeparator = this.fieldSeparator;
                }
                else {
                    fieldSeparator = "";
                }
                String s = fieldSeparator + this.fieldStartDelimiter + array[i] + this.fieldStopDelimiter;
                if (!b) {
                    s = s + this.fieldSeparator + this.fieldStartDelimiter + array2[i] + this.fieldStopDelimiter;
                }
                this.aStream.write(s, 0, s.length());
            }
            this.aStream.write(this.recordSeparator, 0, this.recordSeparator.length());
        }
    }
    
    private void writeNextColumn(String doubleDelimiterString, final boolean b) throws Exception {
        if (doubleDelimiterString != null) {
            if (!b) {
                this.aStream.write(this.fieldStartDelimiter, 0, this.fieldStartDelimiter.length());
            }
            if (this.doubleDelimiter) {
                doubleDelimiterString = this.makeDoubleDelimiterString(doubleDelimiterString, this.fieldStartDelimiter);
            }
            this.aStream.write(doubleDelimiterString, 0, doubleDelimiterString.length());
            if (!b) {
                this.aStream.write(this.fieldStopDelimiter, 0, this.fieldStopDelimiter.length());
            }
        }
    }
    
    String writeBinaryColumnToExternalFile(final InputStream inputStream) throws Exception {
        long lng = 0L;
        if (inputStream != null) {
            for (int i = inputStream.read(this.byteBuf); i != -1; i = inputStream.read(this.byteBuf)) {
                this.lobOutBinaryStream.write(this.byteBuf, 0, i);
                lng += i;
            }
            inputStream.close();
            this.lobOutBinaryStream.flush();
        }
        else {
            lng = -1L;
        }
        final String string = this.lobsFileName + "." + this.lobFileOffset + "." + lng + "/";
        if (lng != -1L) {
            this.lobFileOffset += lng;
        }
        return string;
    }
    
    String writeCharColumnToExternalFile(final Reader reader) throws Exception {
        long lng = 0L;
        if (reader != null) {
            for (int i = reader.read(this.charBuf); i != -1; i = reader.read(this.charBuf)) {
                this.lobByteArrayStream.reset();
                this.lobCharStream.write(this.charBuf, 0, i);
                this.lobCharStream.flush();
                lng += this.lobByteArrayStream.size();
                this.lobByteArrayStream.writeTo(this.lobOutBinaryStream);
            }
            reader.close();
            this.lobOutBinaryStream.flush();
        }
        else {
            lng = -1L;
        }
        final String string = this.lobsFileName + "." + this.lobFileOffset + "." + lng + "/";
        if (lng != -1L) {
            this.lobFileOffset += lng;
        }
        return string;
    }
    
    public void writeData(final String[] array, final boolean[] array2) throws Exception {
        if (this.format.equals("ASCII_DELIMITED")) {
            this.writeNextColumn(array[0], array2[0]);
            for (int i = 1; i < array.length; ++i) {
                this.aStream.write(this.fieldSeparator, 0, this.fieldSeparator.length());
                this.writeNextColumn(array[i], array2[i]);
            }
            if (this.hasDelimiterAtEnd) {
                this.aStream.write(this.fieldSeparator, 0, this.fieldSeparator.length());
            }
        }
        this.aStream.write(this.recordSeparator, 0, this.recordSeparator.length());
    }
    
    public void noMoreRows() throws IOException {
        this.aStream.flush();
        this.aStream.close();
        if (this.lobsInExtFile) {
            if (this.lobOutBinaryStream != null) {
                this.lobOutBinaryStream.flush();
                this.lobOutBinaryStream.close();
            }
            if (this.lobCharStream != null) {
                this.lobCharStream.close();
            }
            if (this.lobByteArrayStream != null) {
                this.lobByteArrayStream.close();
            }
        }
    }
    
    private String makeDoubleDelimiterString(final String str, final String str2) {
        int i = str.indexOf(str2);
        if (i != -1) {
            StringBuffer insert = new StringBuffer(str);
            for (int length = str2.length(); i != -1; i = insert.toString().indexOf(str2, i + length + 1)) {
                insert = insert.insert(i, str2);
            }
            return insert.toString();
        }
        return str;
    }
}
