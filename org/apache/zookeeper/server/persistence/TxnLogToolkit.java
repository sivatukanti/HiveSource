// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import java.io.OutputStream;
import java.io.InputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.server.TraceFormatter;
import java.util.Date;
import java.text.DateFormat;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.IOException;
import java.util.zip.Checksum;
import java.util.zip.Adler32;
import java.io.EOFException;
import org.apache.jute.OutputArchive;
import org.apache.jute.InputArchive;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.jute.BinaryOutputArchive;
import java.io.FileOutputStream;
import org.apache.jute.BinaryInputArchive;
import java.io.FileInputStream;
import java.io.File;
import java.io.Closeable;

public class TxnLogToolkit implements Closeable
{
    private File txnLogFile;
    private boolean recoveryMode;
    private boolean verbose;
    private FileInputStream txnFis;
    private BinaryInputArchive logStream;
    private int crcFixed;
    private FileOutputStream recoveryFos;
    private BinaryOutputArchive recoveryOa;
    private File recoveryLogFile;
    private FilePadding filePadding;
    private boolean force;
    
    public static void main(final String[] args) throws Exception {
        final TxnLogToolkit lt = parseCommandLine(args);
        try {
            lt.dump(new Scanner(System.in));
            lt.printStat();
        }
        catch (TxnLogToolkitParseException e) {
            System.err.println(e.getMessage() + "\n");
            TxnLogToolkitCliParser.printHelpAndExit(e.getExitCode());
        }
        catch (TxnLogToolkitException e2) {
            System.err.println(e2.getMessage());
            System.exit(e2.getExitCode());
        }
        finally {
            lt.close();
        }
    }
    
    public TxnLogToolkit(final boolean recoveryMode, final boolean verbose, final String txnLogFileName, final boolean force) throws FileNotFoundException, TxnLogToolkitException {
        this.recoveryMode = false;
        this.verbose = false;
        this.crcFixed = 0;
        this.filePadding = new FilePadding();
        this.force = false;
        this.recoveryMode = recoveryMode;
        this.verbose = verbose;
        this.force = force;
        this.txnLogFile = new File(txnLogFileName);
        if (!this.txnLogFile.exists() || !this.txnLogFile.canRead()) {
            throw new TxnLogToolkitException(1, "File doesn't exist or not readable: %s", new Object[] { this.txnLogFile });
        }
        if (recoveryMode) {
            this.recoveryLogFile = new File(this.txnLogFile.toString() + ".fixed");
            if (this.recoveryLogFile.exists()) {
                throw new TxnLogToolkitException(1, "Recovery file %s already exists or not writable", new Object[] { this.recoveryLogFile });
            }
        }
        this.openTxnLogFile();
        if (recoveryMode) {
            this.openRecoveryFile();
        }
    }
    
    public void dump(final Scanner scanner) throws Exception {
        this.crcFixed = 0;
        final FileHeader fhdr = new FileHeader();
        fhdr.deserialize(this.logStream, "fileheader");
        if (fhdr.getMagic() != FileTxnLog.TXNLOG_MAGIC) {
            throw new TxnLogToolkitException(2, "Invalid magic number for %s", new Object[] { this.txnLogFile.getName() });
        }
        System.out.println("ZooKeeper Transactional Log File with dbid " + fhdr.getDbid() + " txnlog format version " + fhdr.getVersion());
        if (this.recoveryMode) {
            fhdr.serialize(this.recoveryOa, "fileheader");
            this.recoveryFos.flush();
            this.filePadding.setCurrentSize(this.recoveryFos.getChannel().position());
        }
        int count = 0;
        while (true) {
            long crcValue;
            byte[] bytes;
            try {
                crcValue = this.logStream.readLong("crcvalue");
                bytes = this.logStream.readBuffer("txnEntry");
            }
            catch (EOFException e) {
                System.out.println("EOF reached after " + count + " txns.");
                return;
            }
            if (bytes.length == 0) {
                System.out.println("EOF reached after " + count + " txns.");
                return;
            }
            final Checksum crc = new Adler32();
            crc.update(bytes, 0, bytes.length);
            if (crcValue != crc.getValue()) {
                if (this.recoveryMode) {
                    if (!this.force) {
                        this.printTxn(bytes, "CRC ERROR");
                        if (this.askForFix(scanner)) {
                            crcValue = crc.getValue();
                            ++this.crcFixed;
                        }
                    }
                    else {
                        crcValue = crc.getValue();
                        this.printTxn(bytes, "CRC FIXED");
                        ++this.crcFixed;
                    }
                }
                else {
                    this.printTxn(bytes, "CRC ERROR");
                }
            }
            if (!this.recoveryMode || this.verbose) {
                this.printTxn(bytes);
            }
            if (this.logStream.readByte("EOR") != 66) {
                throw new TxnLogToolkitException(1, "Last transaction was partial.", new Object[0]);
            }
            if (this.recoveryMode) {
                this.filePadding.padFile(this.recoveryFos.getChannel());
                this.recoveryOa.writeLong(crcValue, "crcvalue");
                this.recoveryOa.writeBuffer(bytes, "txnEntry");
                this.recoveryOa.writeByte((byte)66, "EOR");
            }
            ++count;
        }
    }
    
    private boolean askForFix(final Scanner scanner) throws TxnLogToolkitException {
        while (true) {
            System.out.print("Would you like to fix it (Yes/No/Abort) ? ");
            final char answer = Character.toUpperCase(scanner.next().charAt(0));
            switch (answer) {
                case 'Y': {
                    return true;
                }
                case 'N': {
                    return false;
                }
                case 'A': {
                    throw new TxnLogToolkitException(0, "Recovery aborted.", new Object[0]);
                }
                default: {
                    continue;
                }
            }
        }
    }
    
    private void printTxn(final byte[] bytes) throws IOException {
        this.printTxn(bytes, "");
    }
    
    private void printTxn(final byte[] bytes, final String prefix) throws IOException {
        final TxnHeader hdr = new TxnHeader();
        final Record txn = SerializeUtils.deserializeTxn(bytes, hdr);
        final String txns = String.format("%s session 0x%s cxid 0x%s zxid 0x%s %s %s", DateFormat.getDateTimeInstance(3, 1).format(new Date(hdr.getTime())), Long.toHexString(hdr.getClientId()), Long.toHexString(hdr.getCxid()), Long.toHexString(hdr.getZxid()), TraceFormatter.op2String(hdr.getType()), txn);
        if (prefix != null && !"".equals(prefix.trim())) {
            System.out.print(prefix + " - ");
        }
        if (txns.endsWith("\n")) {
            System.out.print(txns);
        }
        else {
            System.out.println(txns);
        }
    }
    
    private void openTxnLogFile() throws FileNotFoundException {
        this.txnFis = new FileInputStream(this.txnLogFile);
        this.logStream = BinaryInputArchive.getArchive(this.txnFis);
    }
    
    private void closeTxnLogFile() throws IOException {
        if (this.txnFis != null) {
            this.txnFis.close();
        }
    }
    
    private void openRecoveryFile() throws FileNotFoundException {
        this.recoveryFos = new FileOutputStream(this.recoveryLogFile);
        this.recoveryOa = BinaryOutputArchive.getArchive(this.recoveryFos);
    }
    
    private void closeRecoveryFile() throws IOException {
        if (this.recoveryFos != null) {
            this.recoveryFos.close();
        }
    }
    
    private static TxnLogToolkit parseCommandLine(final String[] args) throws TxnLogToolkitException, FileNotFoundException {
        final TxnLogToolkitCliParser parser = new TxnLogToolkitCliParser();
        parser.parse(args);
        return new TxnLogToolkit(parser.isRecoveryMode(), parser.isVerbose(), parser.getTxnLogFileName(), parser.isForce());
    }
    
    private void printStat() {
        if (this.recoveryMode) {
            System.out.printf("Recovery file %s has been written with %d fixed CRC error(s)%n", this.recoveryLogFile, this.crcFixed);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.recoveryMode) {
            this.closeRecoveryFile();
        }
        this.closeTxnLogFile();
    }
    
    static class TxnLogToolkitException extends Exception
    {
        private static final long serialVersionUID = 1L;
        private int exitCode;
        
        TxnLogToolkitException(final int exitCode, final String message, final Object... params) {
            super(String.format(message, params));
            this.exitCode = exitCode;
        }
        
        int getExitCode() {
            return this.exitCode;
        }
    }
    
    static class TxnLogToolkitParseException extends TxnLogToolkitException
    {
        private static final long serialVersionUID = 1L;
        
        TxnLogToolkitParseException(final int exitCode, final String message, final Object... params) {
            super(exitCode, message, params);
        }
    }
}
