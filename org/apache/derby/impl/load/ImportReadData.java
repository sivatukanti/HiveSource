// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.File;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Locale;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.BufferedReader;
import java.security.PrivilegedExceptionAction;

final class ImportReadData implements PrivilegedExceptionAction
{
    private String inputFileName;
    private int[] columnWidths;
    private int rowWidth;
    private char[] tempString;
    private int numberOfCharsReadSoFar;
    private BufferedReader bufferedReader;
    private static final int START_SIZE = 10240;
    private char[] currentToken;
    private int currentTokenMaxSize;
    boolean foundStartDelimiter;
    int totalCharsSoFar;
    int positionOfNonWhiteSpaceCharInFront;
    int positionOfNonWhiteSpaceCharInBack;
    int lineNumber;
    int fieldStartDelimiterIndex;
    int fieldStopDelimiterIndex;
    int stopDelimiterPosition;
    boolean foundStartAndStopDelimiters;
    boolean streamOpenForReading;
    static final int DEFAULT_FORMAT_CODE = 0;
    static final int ASCII_FIXED_FORMAT_CODE = 1;
    private int formatCode;
    private boolean hasColumnDefinition;
    private char recordSeparatorChar0;
    private char fieldSeparatorChar0;
    private boolean recordSepStartNotWhite;
    private boolean fieldSepStartNotWhite;
    protected ControlInfo controlFileReader;
    protected int numberOfColumns;
    protected String[] columnTypes;
    protected char[] fieldSeparator;
    protected int fieldSeparatorLength;
    protected char[] recordSeparator;
    protected int recordSeparatorLength;
    protected String nullString;
    protected String columnDefinition;
    protected String format;
    protected String dataCodeset;
    protected char[] fieldStartDelimiter;
    protected int fieldStartDelimiterLength;
    protected char[] fieldStopDelimiter;
    protected int fieldStopDelimiterLength;
    protected boolean hasDelimiterAtEnd;
    private ImportLobFile[] lobFileHandles;
    private String lobFileName;
    private int lobOffset;
    private int lobLength;
    private boolean haveSep;
    
    private void loadPropertiesInfo() throws Exception {
        this.fieldSeparator = this.controlFileReader.getFieldSeparator().toCharArray();
        this.fieldSeparatorLength = this.fieldSeparator.length;
        this.recordSeparator = this.controlFileReader.getRecordSeparator().toCharArray();
        this.recordSeparatorLength = this.recordSeparator.length;
        this.nullString = this.controlFileReader.getNullString();
        this.columnDefinition = this.controlFileReader.getColumnDefinition();
        this.format = this.controlFileReader.getFormat();
        this.dataCodeset = this.controlFileReader.getDataCodeset();
        this.fieldStartDelimiter = this.controlFileReader.getFieldStartDelimiter().toCharArray();
        this.fieldStartDelimiterLength = this.fieldStartDelimiter.length;
        this.fieldStopDelimiter = this.controlFileReader.getFieldEndDelimiter().toCharArray();
        this.fieldStopDelimiterLength = this.fieldStopDelimiter.length;
        this.hasDelimiterAtEnd = this.controlFileReader.getHasDelimiterAtEnd();
        if (this.recordSeparatorLength > 0) {
            this.recordSeparatorChar0 = this.recordSeparator[0];
            this.recordSepStartNotWhite = !Character.isWhitespace(this.recordSeparatorChar0);
        }
        if (this.fieldSeparatorLength > 0) {
            this.fieldSeparatorChar0 = this.fieldSeparator[0];
            this.fieldSepStartNotWhite = !Character.isWhitespace(this.fieldSeparatorChar0);
        }
    }
    
    ImportReadData(final String inputFileName, final ControlInfo controlFileReader) throws Exception {
        this.currentToken = new char[10240];
        this.currentTokenMaxSize = 10240;
        this.formatCode = 0;
        this.recordSepStartNotWhite = true;
        this.fieldSepStartNotWhite = true;
        this.haveSep = true;
        this.inputFileName = inputFileName;
        this.controlFileReader = controlFileReader;
        this.loadPropertiesInfo();
        this.loadMetaData();
        this.lobFileHandles = new ImportLobFile[this.numberOfColumns];
    }
    
    int getNumberOfColumns() {
        return this.numberOfColumns;
    }
    
    protected void ignoreFirstRow() throws Exception {
        this.readNextToken(this.recordSeparator, 0, this.recordSeparatorLength, true);
    }
    
    protected void loadColumnTypes() throws Exception {
        this.findNumberOfColumnsInARow();
        this.closeStream();
        this.openFile();
        final String[] array = new String[this.numberOfColumns];
        this.readNextDelimitedRow(array);
        this.columnTypes = new String[this.numberOfColumns / 2];
        for (int i = 0; i < this.numberOfColumns; i += 2) {
            this.columnTypes[i / 2] = array[i + 1];
        }
        this.closeStream();
        this.openFile();
        this.numberOfColumns = 0;
    }
    
    private void openFile() throws Exception {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            throw ex.getException();
        }
    }
    
    public final Object run() throws Exception {
        this.realOpenFile();
        return null;
    }
    
    private void realOpenFile() throws Exception {
        InputStream openStream;
        try {
            try {
                final URL url = new URL(this.inputFileName);
                if (url.getProtocol().equals("file")) {
                    this.inputFileName = url.getFile();
                    throw new MalformedURLException();
                }
                openStream = url.openStream();
            }
            catch (MalformedURLException ex3) {
                openStream = new FileInputStream(this.inputFileName);
            }
        }
        catch (FileNotFoundException ex) {
            throw LoadError.dataFileNotFound(this.inputFileName, ex);
        }
        catch (SecurityException ex2) {
            throw LoadError.dataFileNotFound(this.inputFileName, ex2);
        }
        this.bufferedReader = new BufferedReader((this.dataCodeset == null) ? new InputStreamReader(openStream) : new InputStreamReader(openStream, this.dataCodeset), 32768);
        this.streamOpenForReading = true;
    }
    
    private void loadMetaData() throws Exception {
        this.openFile();
        if (this.columnDefinition.toUpperCase(Locale.ENGLISH).equals("True".toUpperCase(Locale.ENGLISH))) {
            this.hasColumnDefinition = true;
            this.ignoreFirstRow();
        }
        if (this.formatCode == 0) {
            this.findNumberOfColumnsInARow();
        }
        this.closeStream();
    }
    
    void closeStream() throws Exception {
        if (this.streamOpenForReading) {
            this.bufferedReader.close();
            this.streamOpenForReading = false;
        }
        if (this.lobFileHandles != null) {
            for (int i = 0; i < this.numberOfColumns; ++i) {
                if (this.lobFileHandles[i] != null) {
                    this.lobFileHandles[i].close();
                }
            }
        }
    }
    
    int findNumberOfColumnsInARow() throws Exception {
        this.numberOfColumns = 1;
        while (!this.readTokensUntilEndOfRecord()) {
            ++this.numberOfColumns;
        }
        if (this.hasDelimiterAtEnd) {
            --this.numberOfColumns;
        }
        if (this.numberOfCharsReadSoFar == 0) {
            this.numberOfColumns = 0;
        }
        return this.numberOfColumns;
    }
    
    private void checkForWhiteSpaceInFront() {
        if (this.positionOfNonWhiteSpaceCharInFront + 1 == this.totalCharsSoFar && !this.foundStartDelimiter && !this.foundStartAndStopDelimiters) {
            final char ch = this.currentToken[this.positionOfNonWhiteSpaceCharInFront];
            if (Character.isWhitespace(ch) && (this.recordSepStartNotWhite || ch != this.recordSeparatorChar0) && (this.fieldSepStartNotWhite || ch != this.fieldSeparatorChar0)) {
                ++this.positionOfNonWhiteSpaceCharInFront;
            }
        }
    }
    
    private void checkForWhiteSpaceInBack() {
        int n = 1;
        this.positionOfNonWhiteSpaceCharInBack = 0;
        for (int totalCharsSoFar = this.totalCharsSoFar; totalCharsSoFar > this.stopDelimiterPosition && n != 0; --totalCharsSoFar) {
            final char ch = this.currentToken[totalCharsSoFar];
            if (Character.isWhitespace(ch)) {
                if ((this.recordSepStartNotWhite || ch != this.recordSeparatorChar0) && (this.fieldSepStartNotWhite || ch != this.fieldSeparatorChar0)) {
                    ++this.positionOfNonWhiteSpaceCharInBack;
                }
            }
            else {
                n = 0;
            }
        }
    }
    
    boolean readTokensUntilEndOfRecord() throws Exception {
        int lookForPassedSeparator = 0;
        int lookForPassedSeparator2 = 0;
        this.fieldStopDelimiterIndex = 0;
        this.fieldStartDelimiterIndex = 0;
        this.totalCharsSoFar = 0;
        this.positionOfNonWhiteSpaceCharInFront = 0;
        this.foundStartDelimiter = false;
        this.foundStartAndStopDelimiters = false;
        this.numberOfCharsReadSoFar = 0;
        while (true) {
            final int read = this.bufferedReader.read();
            if (read == -1) {
                return true;
            }
            ++this.numberOfCharsReadSoFar;
            this.currentToken[this.totalCharsSoFar++] = (char)read;
            this.checkForWhiteSpaceInFront();
            if (this.totalCharsSoFar == this.currentTokenMaxSize) {
                this.currentTokenMaxSize *= 2;
                final char[] currentToken = new char[this.currentTokenMaxSize];
                System.arraycopy(this.currentToken, 0, currentToken, 0, this.totalCharsSoFar);
                this.currentToken = currentToken;
            }
            lookForPassedSeparator = this.lookForPassedSeparator(this.fieldSeparator, lookForPassedSeparator, this.fieldSeparatorLength, read, false);
            if (lookForPassedSeparator == -1) {
                return false;
            }
            if (this.foundStartDelimiter) {
                continue;
            }
            lookForPassedSeparator2 = this.lookForPassedSeparator(this.recordSeparator, lookForPassedSeparator2, this.recordSeparatorLength, read, true);
            if (lookForPassedSeparator2 == -1) {
                return true;
            }
        }
    }
    
    private int lookForPassedSeparator(final char[] array, int n, final int n2, final int n3, final boolean b) throws IOException {
        if (!this.foundStartDelimiter) {
            if (this.fieldStartDelimiterLength != 0 && !this.foundStartAndStopDelimiters && this.totalCharsSoFar != this.positionOfNonWhiteSpaceCharInFront && this.totalCharsSoFar - this.positionOfNonWhiteSpaceCharInFront <= this.fieldStartDelimiterLength) {
                if (n3 == this.fieldStartDelimiter[this.fieldStartDelimiterIndex]) {
                    ++this.fieldStartDelimiterIndex;
                    if (this.fieldStartDelimiterIndex == this.fieldStartDelimiterLength) {
                        this.foundStartDelimiter = true;
                        this.totalCharsSoFar = 0;
                        return this.positionOfNonWhiteSpaceCharInFront = 0;
                    }
                }
                else if (this.fieldStartDelimiterIndex > 0) {
                    this.reCheckRestOfTheCharacters(this.totalCharsSoFar - this.fieldStartDelimiterIndex, this.fieldStartDelimiter, this.fieldStartDelimiterLength);
                }
            }
            if (b) {
                if (n3 == 13 || n3 == 10) {
                    this.recordSeparatorChar0 = (char)n3;
                    if (n3 == 13) {
                        this.omitLineFeed();
                    }
                    --this.totalCharsSoFar;
                    return -1;
                }
                return n;
            }
            else if (n3 == array[n]) {
                if (++n == n2) {
                    this.totalCharsSoFar -= n2;
                    return -1;
                }
                return n;
            }
            else if (n > 0) {
                return this.reCheckRestOfTheCharacters(this.totalCharsSoFar - n, array, n2);
            }
        }
        else if (n3 == this.fieldStopDelimiter[this.fieldStopDelimiterIndex]) {
            ++this.fieldStopDelimiterIndex;
            if (this.fieldStopDelimiterIndex == this.fieldStopDelimiterLength) {
                if (!this.skipDoubleDelimiters(this.fieldStopDelimiter)) {
                    this.foundStartDelimiter = false;
                    this.totalCharsSoFar -= this.fieldStopDelimiterLength;
                    this.stopDelimiterPosition = this.totalCharsSoFar;
                    this.foundStartAndStopDelimiters = true;
                }
                else {
                    this.fieldStopDelimiterIndex = 0;
                }
                return 0;
            }
            return 0;
        }
        else if (this.fieldStopDelimiterIndex > 0) {
            this.reCheckRestOfTheCharacters(this.totalCharsSoFar - this.fieldStopDelimiterIndex, this.fieldStopDelimiter, this.fieldStopDelimiterLength);
            return 0;
        }
        return 0;
    }
    
    private int reCheckRestOfTheCharacters(final int n, final char[] array, final int n2) {
        int n3 = 0;
        for (int i = n; i < this.totalCharsSoFar; ++i) {
            if (this.currentToken[i] == array[n3]) {
                ++n3;
            }
            else {
                n3 = 0;
            }
        }
        return n3;
    }
    
    private boolean skipDoubleDelimiters(final char[] array) throws IOException {
        boolean b = true;
        final int length = array.length;
        this.bufferedReader.mark(length);
        for (int i = 0; i < length; ++i) {
            if (this.bufferedReader.read() != array[i]) {
                this.bufferedReader.reset();
                b = false;
                break;
            }
        }
        return b;
    }
    
    private void omitLineFeed() throws IOException {
        this.bufferedReader.mark(1);
        if (this.bufferedReader.read() != 10) {
            this.bufferedReader.reset();
        }
    }
    
    int getCurrentRowNumber() {
        return this.lineNumber;
    }
    
    boolean readNextRow(final String[] array) throws Exception {
        if (!this.streamOpenForReading) {
            this.openFile();
            if (this.hasColumnDefinition) {
                this.ignoreFirstRow();
            }
        }
        boolean b;
        if (this.formatCode == 0) {
            b = this.readNextDelimitedRow(array);
        }
        else {
            b = this.readNextFixedRow(array);
        }
        return b;
    }
    
    private boolean readNextFixedRow(final String[] array) throws Exception {
        int off = 0;
        int n = 0;
        while ((off += this.bufferedReader.read(this.tempString, off, this.rowWidth - off)) < this.rowWidth) {
            if (off == n - 1) {
                if (off == -1) {
                    return false;
                }
                if (n != this.rowWidth - this.recordSeparator.length) {
                    throw LoadError.unexpectedEndOfFile(this.lineNumber + 1);
                }
                this.haveSep = false;
                break;
            }
            else {
                n = off;
            }
        }
        int offset = 0;
        for (int i = 0; i < this.numberOfColumns; ++i) {
            final int count = this.columnWidths[i];
            if (count == 0) {
                array[i] = null;
            }
            else {
                final String s = new String(this.tempString, offset, count);
                if (s.trim().equals(this.nullString)) {
                    array[i] = null;
                }
                else {
                    array[i] = s;
                }
                offset += count;
            }
        }
        if (this.haveSep) {
            for (int j = this.recordSeparatorLength - 1; j >= 0; --j) {
                if (this.tempString[offset + j] != this.recordSeparator[j]) {
                    throw LoadError.recordSeparatorMissing(this.lineNumber + 1);
                }
            }
        }
        else {
            this.haveSep = true;
        }
        ++this.lineNumber;
        return true;
    }
    
    private boolean readNextDelimitedRow(final String[] array) throws Exception {
        final int n = this.numberOfColumns - 1;
        if (n < 0) {
            return false;
        }
        int i = 0;
        while (i < n) {
            if (!this.readNextToken(this.fieldSeparator, 0, this.fieldSeparatorLength, false)) {
                if (i == 0) {
                    return false;
                }
                throw LoadError.unexpectedEndOfFile(this.lineNumber + 1);
            }
            else {
                if (this.stopDelimiterPosition != 0 && this.stopDelimiterPosition != this.totalCharsSoFar) {
                    for (int j = this.stopDelimiterPosition + 1; j < this.totalCharsSoFar; ++j) {
                        if (!Character.isWhitespace(this.currentToken[j])) {
                            throw LoadError.dataAfterStopDelimiter(this.lineNumber + 1, i + 1);
                        }
                    }
                    this.totalCharsSoFar = this.stopDelimiterPosition;
                }
                if (this.totalCharsSoFar != -1) {
                    array[i] = new String(this.currentToken, this.positionOfNonWhiteSpaceCharInFront, this.totalCharsSoFar);
                }
                else {
                    array[i] = null;
                }
                ++i;
            }
        }
        if (this.readNextToken(this.recordSeparator, 0, this.recordSeparatorLength, true)) {
            if (this.stopDelimiterPosition != 0 && this.stopDelimiterPosition != this.totalCharsSoFar) {
                for (int k = this.stopDelimiterPosition + 1; k < this.totalCharsSoFar; ++k) {
                    if (!Character.isWhitespace(this.currentToken[k])) {
                        throw LoadError.dataAfterStopDelimiter(this.lineNumber + 1, this.numberOfColumns);
                    }
                }
                this.totalCharsSoFar = this.stopDelimiterPosition;
            }
            if (this.hasDelimiterAtEnd && this.fieldStopDelimiterLength <= 0) {
                --this.totalCharsSoFar;
            }
            if (this.totalCharsSoFar > -1) {
                if (!this.hasDelimiterAtEnd) {
                    array[n] = new String(this.currentToken, this.positionOfNonWhiteSpaceCharInFront, this.totalCharsSoFar);
                }
                else if (this.totalCharsSoFar == this.fieldSeparatorLength && this.isFieldSep(this.currentToken)) {
                    final String s = new String(this.currentToken, this.positionOfNonWhiteSpaceCharInFront, this.totalCharsSoFar);
                    if (this.currentToken[this.totalCharsSoFar + 1] == this.fieldStopDelimiter[0]) {
                        array[n] = s;
                    }
                    else {
                        array[n] = null;
                    }
                }
                else if (this.totalCharsSoFar > 0) {
                    array[n] = new String(this.currentToken, this.positionOfNonWhiteSpaceCharInFront, this.totalCharsSoFar);
                }
                else {
                    array[n] = null;
                }
            }
            else {
                array[n] = null;
            }
            ++this.lineNumber;
            return true;
        }
        if (n == 0) {
            return false;
        }
        throw LoadError.unexpectedEndOfFile(this.lineNumber + 1);
    }
    
    private boolean isFieldSep(final char[] array) {
        for (int n = 0; n < array.length && n < this.fieldSeparatorLength; ++n) {
            if (array[n] != this.fieldSeparator[n]) {
                return false;
            }
        }
        return true;
    }
    
    boolean readNextToken(final char[] array, int n, final int n2, final boolean b) throws Exception {
        this.fieldStopDelimiterIndex = 0;
        this.fieldStartDelimiterIndex = 0;
        this.totalCharsSoFar = 0;
        this.positionOfNonWhiteSpaceCharInFront = 0;
        this.stopDelimiterPosition = 0;
        this.foundStartAndStopDelimiters = false;
        this.foundStartDelimiter = false;
        while (true) {
            final int read = this.bufferedReader.read();
            if (read == -1) {
                return false;
            }
            this.currentToken[this.totalCharsSoFar++] = (char)read;
            this.checkForWhiteSpaceInFront();
            if (this.totalCharsSoFar == this.currentTokenMaxSize) {
                this.currentTokenMaxSize *= 2;
                final char[] currentToken = new char[this.currentTokenMaxSize];
                System.arraycopy(this.currentToken, 0, currentToken, 0, this.totalCharsSoFar);
                this.currentToken = currentToken;
            }
            final int lookForPassedSeparator = this.lookForPassedSeparator(array, n, n2, read, b);
            if (lookForPassedSeparator == -1) {
                if (!this.foundStartAndStopDelimiters) {
                    if (this.totalCharsSoFar == 0) {
                        this.totalCharsSoFar = -1;
                    }
                    else {
                        this.checkForWhiteSpaceInBack();
                        this.totalCharsSoFar = this.totalCharsSoFar - this.positionOfNonWhiteSpaceCharInFront - this.positionOfNonWhiteSpaceCharInBack;
                    }
                }
                return true;
            }
            n = lookForPassedSeparator;
        }
    }
    
    String getClobColumnFromExtFileAsString(final String s, final int n) throws SQLException {
        try {
            this.initExternalLobFile(s, n);
            if (this.lobLength == -1) {
                return null;
            }
            return this.lobFileHandles[n - 1].getString(this.lobOffset, this.lobLength);
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    Clob getClobColumnFromExtFile(final String s, final int n) throws SQLException {
        try {
            this.initExternalLobFile(s, n);
            if (this.lobLength == -1) {
                return null;
            }
            return new ImportClob(this.lobFileHandles[n - 1], this.lobOffset, this.lobLength);
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    Blob getBlobColumnFromExtFile(final String s, final int n) throws SQLException {
        this.initExternalLobFile(s, n);
        if (this.lobLength == -1) {
            return null;
        }
        return new ImportBlob(this.lobFileHandles[n - 1], this.lobOffset, this.lobLength);
    }
    
    private void initExternalLobFile(final String s, final int n) throws SQLException {
        final int lastIndex = s.lastIndexOf(".");
        final int lastIndex2 = s.lastIndexOf(".", lastIndex - 1);
        this.lobLength = Integer.parseInt(s.substring(lastIndex + 1, s.length() - 1));
        this.lobOffset = Integer.parseInt(s.substring(lastIndex2 + 1, lastIndex));
        this.lobFileName = s.substring(0, lastIndex2);
        if (this.lobFileHandles[n - 1] == null) {
            try {
                File file = new File(this.lobFileName);
                if (file.getParentFile() == null) {
                    file = new File(new File(this.inputFileName).getParentFile(), this.lobFileName);
                }
                this.lobFileHandles[n - 1] = new ImportLobFile(file, this.controlFileReader.getDataCodeset());
            }
            catch (Exception ex) {
                throw LoadError.unexpectedError(ex);
            }
        }
    }
}
