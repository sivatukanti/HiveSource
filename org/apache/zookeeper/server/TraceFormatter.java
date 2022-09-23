// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.text.DateFormat;
import java.nio.ByteBuffer;
import java.io.FileInputStream;

public class TraceFormatter
{
    public static String op2String(final int op) {
        switch (op) {
            case 0: {
                return "notification";
            }
            case 1: {
                return "create";
            }
            case 2: {
                return "delete";
            }
            case 3: {
                return "exists";
            }
            case 4: {
                return "getDate";
            }
            case 5: {
                return "setData";
            }
            case 14: {
                return "multi";
            }
            case 6: {
                return "getACL";
            }
            case 7: {
                return "setACL";
            }
            case 8: {
                return "getChildren";
            }
            case 12: {
                return "getChildren2";
            }
            case 11: {
                return "ping";
            }
            case -10: {
                return "createSession";
            }
            case -11: {
                return "closeSession";
            }
            case -1: {
                return "error";
            }
            default: {
                return "unknown " + op;
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("USAGE: TraceFormatter trace_file");
            System.exit(2);
        }
        final FileChannel fc = new FileInputStream(args[0]).getChannel();
        while (true) {
            ByteBuffer bb = ByteBuffer.allocate(41);
            fc.read(bb);
            bb.flip();
            final byte app = bb.get();
            final long time = bb.getLong();
            final long id = bb.getLong();
            final int cxid = bb.getInt();
            final long zxid = bb.getLong();
            final int txnType = bb.getInt();
            final int type = bb.getInt();
            final int len = bb.getInt();
            bb = ByteBuffer.allocate(len);
            fc.read(bb);
            bb.flip();
            String path = "n/a";
            if (bb.remaining() > 0 && type != -10) {
                final int pathLen = bb.getInt();
                final byte[] b = new byte[pathLen];
                bb.get(b);
                path = new String(b);
            }
            System.out.println(DateFormat.getDateTimeInstance(3, 1).format(new Date(time)) + ": " + (char)app + " id=0x" + Long.toHexString(id) + " cxid=" + cxid + " op=" + op2String(type) + " zxid=0x" + Long.toHexString(zxid) + " txnType=" + txnType + " len=" + len + " path=" + path);
        }
    }
}
