// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.logging;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

@ChannelHandler.Sharable
public class LoggingHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler
{
    private static final InternalLogLevel DEFAULT_LEVEL;
    private static final String NEWLINE;
    private static final String[] BYTE2HEX;
    private static final String[] HEXPADDING;
    private static final String[] BYTEPADDING;
    private static final char[] BYTE2CHAR;
    private final InternalLogger logger;
    private final InternalLogLevel level;
    private final boolean hexDump;
    
    public LoggingHandler() {
        this(true);
    }
    
    public LoggingHandler(final InternalLogLevel level) {
        this(level, true);
    }
    
    public LoggingHandler(final boolean hexDump) {
        this(LoggingHandler.DEFAULT_LEVEL, hexDump);
    }
    
    public LoggingHandler(final InternalLogLevel level, final boolean hexDump) {
        if (level == null) {
            throw new NullPointerException("level");
        }
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
        this.level = level;
        this.hexDump = hexDump;
    }
    
    public LoggingHandler(final Class<?> clazz) {
        this(clazz, true);
    }
    
    public LoggingHandler(final Class<?> clazz, final boolean hexDump) {
        this(clazz, LoggingHandler.DEFAULT_LEVEL, hexDump);
    }
    
    public LoggingHandler(final Class<?> clazz, final InternalLogLevel level) {
        this(clazz, level, true);
    }
    
    public LoggingHandler(final Class<?> clazz, final InternalLogLevel level, final boolean hexDump) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        if (level == null) {
            throw new NullPointerException("level");
        }
        this.logger = InternalLoggerFactory.getInstance(clazz);
        this.level = level;
        this.hexDump = hexDump;
    }
    
    public LoggingHandler(final String name) {
        this(name, true);
    }
    
    public LoggingHandler(final String name, final boolean hexDump) {
        this(name, LoggingHandler.DEFAULT_LEVEL, hexDump);
    }
    
    public LoggingHandler(final String name, final InternalLogLevel level, final boolean hexDump) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (level == null) {
            throw new NullPointerException("level");
        }
        this.logger = InternalLoggerFactory.getInstance(name);
        this.level = level;
        this.hexDump = hexDump;
    }
    
    public InternalLogger getLogger() {
        return this.logger;
    }
    
    public InternalLogLevel getLevel() {
        return this.level;
    }
    
    public void log(final ChannelEvent e) {
        if (this.getLogger().isEnabled(this.level)) {
            String msg = e.toString();
            if (this.hexDump && e instanceof MessageEvent) {
                final MessageEvent me = (MessageEvent)e;
                if (me.getMessage() instanceof ChannelBuffer) {
                    msg += formatBuffer((ChannelBuffer)me.getMessage());
                }
            }
            if (e instanceof ExceptionEvent) {
                this.getLogger().log(this.level, msg, ((ExceptionEvent)e).getCause());
            }
            else {
                this.getLogger().log(this.level, msg);
            }
        }
    }
    
    private static String formatBuffer(final ChannelBuffer buf) {
        final int length = buf.readableBytes();
        final int rows = length / 16 + ((length % 15 != 0) ? 1 : 0) + 4;
        final StringBuilder dump = new StringBuilder(rows * 80);
        dump.append(LoggingHandler.NEWLINE + "         +-------------------------------------------------+" + LoggingHandler.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + LoggingHandler.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        final int startIndex = buf.readerIndex();
        int endIndex;
        int i;
        for (endIndex = buf.writerIndex(), i = startIndex; i < endIndex; ++i) {
            final int relIdx = i - startIndex;
            final int relIdxMod16 = relIdx & 0xF;
            if (relIdxMod16 == 0) {
                dump.append(LoggingHandler.NEWLINE);
                dump.append(Long.toHexString(((long)relIdx & 0xFFFFFFFFL) | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
            dump.append(LoggingHandler.BYTE2HEX[buf.getUnsignedByte(i)]);
            if (relIdxMod16 == 15) {
                dump.append(" |");
                for (int j = i - 15; j <= i; ++j) {
                    dump.append(LoggingHandler.BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
        }
        if ((i - startIndex & 0xF) != 0x0) {
            final int remainder = length & 0xF;
            dump.append(LoggingHandler.HEXPADDING[remainder]);
            dump.append(" |");
            for (int k = i - remainder; k < i; ++k) {
                dump.append(LoggingHandler.BYTE2CHAR[buf.getUnsignedByte(k)]);
            }
            dump.append(LoggingHandler.BYTEPADDING[remainder]);
            dump.append('|');
        }
        dump.append(LoggingHandler.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        return dump.toString();
    }
    
    public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.log(e);
        ctx.sendUpstream(e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        this.log(e);
        ctx.sendDownstream(e);
    }
    
    static {
        DEFAULT_LEVEL = InternalLogLevel.DEBUG;
        NEWLINE = String.format("%n", new Object[0]);
        BYTE2HEX = new String[256];
        HEXPADDING = new String[16];
        BYTEPADDING = new String[16];
        BYTE2CHAR = new char[256];
        int i;
        for (i = 0; i < 10; ++i) {
            final StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append(i);
            LoggingHandler.BYTE2HEX[i] = buf.toString();
        }
        while (i < 16) {
            final StringBuilder buf = new StringBuilder(3);
            buf.append(" 0");
            buf.append((char)(97 + i - 10));
            LoggingHandler.BYTE2HEX[i] = buf.toString();
            ++i;
        }
        while (i < LoggingHandler.BYTE2HEX.length) {
            final StringBuilder buf = new StringBuilder(3);
            buf.append(' ');
            buf.append(Integer.toHexString(i));
            LoggingHandler.BYTE2HEX[i] = buf.toString();
            ++i;
        }
        for (i = 0; i < LoggingHandler.HEXPADDING.length; ++i) {
            final int padding = LoggingHandler.HEXPADDING.length - i;
            final StringBuilder buf2 = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; ++j) {
                buf2.append("   ");
            }
            LoggingHandler.HEXPADDING[i] = buf2.toString();
        }
        for (i = 0; i < LoggingHandler.BYTEPADDING.length; ++i) {
            final int padding = LoggingHandler.BYTEPADDING.length - i;
            final StringBuilder buf2 = new StringBuilder(padding);
            for (int j = 0; j < padding; ++j) {
                buf2.append(' ');
            }
            LoggingHandler.BYTEPADDING[i] = buf2.toString();
        }
        for (i = 0; i < LoggingHandler.BYTE2CHAR.length; ++i) {
            if (i <= 31 || i >= 127) {
                LoggingHandler.BYTE2CHAR[i] = '.';
            }
            else {
                LoggingHandler.BYTE2CHAR[i] = (char)i;
            }
        }
    }
}
