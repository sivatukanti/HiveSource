// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.io.BufferCache;

public class Ajp13Packet
{
    public static final int MAX_PACKET_SIZE = 8192;
    public static final int HDR_SIZE = 4;
    public static final int DATA_HDR_SIZE = 7;
    public static final int MAX_DATA_SIZE = 8185;
    public static final String FORWARD_REQUEST = "FORWARD REQUEST";
    public static final String SHUTDOWN = "SHUTDOWN";
    public static final String PING_REQUEST = "PING REQUEST";
    public static final String CPING_REQUEST = "CPING REQUEST";
    public static final String SEND_BODY_CHUNK = "SEND BODY CHUNK";
    public static final String SEND_HEADERS = "SEND HEADERS";
    public static final String END_RESPONSE = "END RESPONSE";
    public static final String GET_BODY_CHUNK = "GET BODY CHUNK";
    public static final String CPONG_REPLY = "CPONG REPLY";
    public static final int FORWARD_REQUEST_ORDINAL = 2;
    public static final int SHUTDOWN_ORDINAL = 7;
    public static final int PING_REQUEST_ORDINAL = 8;
    public static final int CPING_REQUEST_ORDINAL = 10;
    public static final int SEND_BODY_CHUNK_ORDINAL = 3;
    public static final int SEND_HEADERS_ORDINAL = 4;
    public static final int END_RESPONSE_ORDINAL = 5;
    public static final int GET_BODY_CHUNK_ORDINAL = 6;
    public static final int CPONG_REPLY_ORDINAL = 9;
    public static final BufferCache CACHE;
    
    static {
        (CACHE = new BufferCache()).add("FORWARD REQUEST", 2);
        Ajp13Packet.CACHE.add("SHUTDOWN", 7);
        Ajp13Packet.CACHE.add("PING REQUEST", 8);
        Ajp13Packet.CACHE.add("CPING REQUEST", 10);
        Ajp13Packet.CACHE.add("SEND BODY CHUNK", 3);
        Ajp13Packet.CACHE.add("SEND HEADERS", 4);
        Ajp13Packet.CACHE.add("END RESPONSE", 5);
        Ajp13Packet.CACHE.add("GET BODY CHUNK", 6);
        Ajp13Packet.CACHE.add("CPONG REPLY", 9);
    }
}
