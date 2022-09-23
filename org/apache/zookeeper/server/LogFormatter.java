// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.jute.Record;
import java.util.zip.Checksum;
import java.util.Date;
import java.text.DateFormat;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.IOException;
import java.util.zip.Adler32;
import java.io.EOFException;
import org.apache.zookeeper.server.persistence.FileTxnLog;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.server.persistence.FileHeader;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class LogFormatter
{
    private static final Logger LOG;
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: LogFormatter log_file");
            System.exit(2);
        }
        final FileInputStream fis = new FileInputStream(args[0]);
        final BinaryInputArchive logStream = BinaryInputArchive.getArchive(fis);
        final FileHeader fhdr = new FileHeader();
        fhdr.deserialize(logStream, "fileheader");
        if (fhdr.getMagic() != FileTxnLog.TXNLOG_MAGIC) {
            System.err.println("Invalid magic number for " + args[0]);
            System.exit(2);
        }
        System.out.println("ZooKeeper Transactional Log File with dbid " + fhdr.getDbid() + " txnlog format version " + fhdr.getVersion());
        int count = 0;
        while (true) {
            long crcValue;
            byte[] bytes;
            try {
                crcValue = logStream.readLong("crcvalue");
                bytes = logStream.readBuffer("txnEntry");
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
                throw new IOException("CRC doesn't match " + crcValue + " vs " + crc.getValue());
            }
            final TxnHeader hdr = new TxnHeader();
            final Record txn = SerializeUtils.deserializeTxn(bytes, hdr);
            System.out.println(DateFormat.getDateTimeInstance(3, 1).format(new Date(hdr.getTime())) + " session 0x" + Long.toHexString(hdr.getClientId()) + " cxid 0x" + Long.toHexString(hdr.getCxid()) + " zxid 0x" + Long.toHexString(hdr.getZxid()) + " " + TraceFormatter.op2String(hdr.getType()) + " " + txn);
            if (logStream.readByte("EOR") != 66) {
                LogFormatter.LOG.error("Last transaction was partial.");
                throw new EOFException("Last transaction was partial.");
            }
            ++count;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(LogFormatter.class);
    }
}
