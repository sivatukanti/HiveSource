// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class SocksMessage
{
    private final MessageType messageType;
    private final ProtocolVersion protocolVersion;
    
    protected SocksMessage(final MessageType messageType) {
        this.protocolVersion = ProtocolVersion.SOCKS5;
        if (messageType == null) {
            throw new NullPointerException("messageType");
        }
        this.messageType = messageType;
    }
    
    public MessageType getMessageType() {
        return this.messageType;
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public abstract void encodeAsByteBuf(final ChannelBuffer p0) throws Exception;
    
    public enum MessageType
    {
        REQUEST, 
        RESPONSE, 
        UNKNOWN;
    }
    
    public enum AuthScheme
    {
        NO_AUTH((byte)0), 
        AUTH_GSSAPI((byte)1), 
        AUTH_PASSWORD((byte)2), 
        UNKNOWN((byte)(-1));
        
        private final byte b;
        
        private AuthScheme(final byte b) {
            this.b = b;
        }
        
        public static AuthScheme fromByte(final byte b) {
            for (final AuthScheme code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return AuthScheme.UNKNOWN;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum CmdType
    {
        CONNECT((byte)1), 
        BIND((byte)2), 
        UDP((byte)3), 
        UNKNOWN((byte)(-1));
        
        private final byte b;
        
        private CmdType(final byte b) {
            this.b = b;
        }
        
        public static CmdType fromByte(final byte b) {
            for (final CmdType code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return CmdType.UNKNOWN;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum AddressType
    {
        IPv4((byte)1), 
        DOMAIN((byte)3), 
        IPv6((byte)4), 
        UNKNOWN((byte)(-1));
        
        private final byte b;
        
        private AddressType(final byte b) {
            this.b = b;
        }
        
        public static AddressType fromByte(final byte b) {
            for (final AddressType code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return AddressType.UNKNOWN;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum AuthStatus
    {
        SUCCESS((byte)0), 
        FAILURE((byte)(-1));
        
        private final byte b;
        
        private AuthStatus(final byte b) {
            this.b = b;
        }
        
        public static AuthStatus fromByte(final byte b) {
            for (final AuthStatus code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return AuthStatus.FAILURE;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum CmdStatus
    {
        SUCCESS((byte)0), 
        FAILURE((byte)1), 
        FORBIDDEN((byte)2), 
        NETWORK_UNREACHABLE((byte)3), 
        HOST_UNREACHABLE((byte)4), 
        REFUSED((byte)5), 
        TTL_EXPIRED((byte)6), 
        COMMAND_NOT_SUPPORTED((byte)7), 
        ADDRESS_NOT_SUPPORTED((byte)8), 
        UNASSIGNED((byte)(-1));
        
        private final byte b;
        
        private CmdStatus(final byte b) {
            this.b = b;
        }
        
        public static CmdStatus fromByte(final byte b) {
            for (final CmdStatus code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return CmdStatus.UNASSIGNED;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum ProtocolVersion
    {
        SOCKS4a((byte)4), 
        SOCKS5((byte)5), 
        UNKNOWN((byte)(-1));
        
        private final byte b;
        
        private ProtocolVersion(final byte b) {
            this.b = b;
        }
        
        public static ProtocolVersion fromByte(final byte b) {
            for (final ProtocolVersion code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return ProtocolVersion.UNKNOWN;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
    
    public enum SubnegotiationVersion
    {
        AUTH_PASSWORD((byte)1), 
        UNKNOWN((byte)(-1));
        
        private final byte b;
        
        private SubnegotiationVersion(final byte b) {
            this.b = b;
        }
        
        public static SubnegotiationVersion fromByte(final byte b) {
            for (final SubnegotiationVersion code : values()) {
                if (code.b == b) {
                    return code;
                }
            }
            return SubnegotiationVersion.UNKNOWN;
        }
        
        public byte getByteValue() {
            return this.b;
        }
    }
}
