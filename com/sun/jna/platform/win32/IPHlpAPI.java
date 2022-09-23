// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import java.util.Map;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Pointer;
import com.sun.jna.Library;

public interface IPHlpAPI extends Library
{
    public static final IPHlpAPI INSTANCE = Native.load("IPHlpAPI", IPHlpAPI.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int IF_MAX_STRING_SIZE = 256;
    public static final int IF_MAX_PHYS_ADDRESS_LENGTH = 32;
    public static final int MAX_INTERFACE_NAME_LEN = 256;
    public static final int MAXLEN_IFDESCR = 256;
    public static final int MAXLEN_PHYSADDR = 8;
    public static final int MAX_HOSTNAME_LEN = 128;
    public static final int MAX_DOMAIN_NAME_LEN = 128;
    public static final int MAX_SCOPE_ID_LEN = 256;
    
    int GetIfEntry(final MIB_IFROW p0);
    
    int GetIfEntry2(final MIB_IF_ROW2 p0);
    
    int GetNetworkParams(final Pointer p0, final IntByReference p1);
    
    @FieldOrder({ "wszName", "dwIndex", "dwType", "dwMtu", "dwSpeed", "dwPhysAddrLen", "bPhysAddr", "dwAdminStatus", "dwOperStatus", "dwLastChange", "dwInOctets", "dwInUcastPkts", "dwInNUcastPkts", "dwInDiscards", "dwInErrors", "dwInUnknownProtos", "dwOutOctets", "dwOutUcastPkts", "dwOutNUcastPkts", "dwOutDiscards", "dwOutErrors", "dwOutQLen", "dwDescrLen", "bDescr" })
    public static class MIB_IFROW extends Structure
    {
        public char[] wszName;
        public int dwIndex;
        public int dwType;
        public int dwMtu;
        public int dwSpeed;
        public int dwPhysAddrLen;
        public byte[] bPhysAddr;
        public int dwAdminStatus;
        public int dwOperStatus;
        public int dwLastChange;
        public int dwInOctets;
        public int dwInUcastPkts;
        public int dwInNUcastPkts;
        public int dwInDiscards;
        public int dwInErrors;
        public int dwInUnknownProtos;
        public int dwOutOctets;
        public int dwOutUcastPkts;
        public int dwOutNUcastPkts;
        public int dwOutDiscards;
        public int dwOutErrors;
        public int dwOutQLen;
        public int dwDescrLen;
        public byte[] bDescr;
        
        public MIB_IFROW() {
            this.wszName = new char[256];
            this.bPhysAddr = new byte[8];
            this.bDescr = new byte[256];
        }
    }
    
    @FieldOrder({ "InterfaceLuid", "InterfaceIndex", "InterfaceGuid", "Alias", "Description", "PhysicalAddressLength", "PhysicalAddress", "PermanentPhysicalAddress", "Mtu", "Type", "TunnelType", "MediaType", "PhysicalMediumType", "AccessType", "DirectionType", "InterfaceAndOperStatusFlags", "OperStatus", "AdminStatus", "MediaConnectState", "NetworkGuid", "ConnectionType", "TransmitLinkSpeed", "ReceiveLinkSpeed", "InOctets", "InUcastPkts", "InNUcastPkts", "InDiscards", "InErrors", "InUnknownProtos", "InUcastOctets", "InMulticastOctets", "InBroadcastOctets", "OutOctets", "OutUcastPkts", "OutNUcastPkts", "OutDiscards", "OutErrors", "OutUcastOctets", "OutMulticastOctets", "OutBroadcastOctets", "OutQLen" })
    public static class MIB_IF_ROW2 extends Structure
    {
        public long InterfaceLuid;
        public int InterfaceIndex;
        public Guid.GUID InterfaceGuid;
        public char[] Alias;
        public char[] Description;
        public int PhysicalAddressLength;
        public byte[] PhysicalAddress;
        public byte[] PermanentPhysicalAddress;
        public int Mtu;
        public int Type;
        public int TunnelType;
        public int MediaType;
        public int PhysicalMediumType;
        public int AccessType;
        public int DirectionType;
        public byte InterfaceAndOperStatusFlags;
        public int OperStatus;
        public int AdminStatus;
        public int MediaConnectState;
        public Guid.GUID NetworkGuid;
        public int ConnectionType;
        public long TransmitLinkSpeed;
        public long ReceiveLinkSpeed;
        public long InOctets;
        public long InUcastPkts;
        public long InNUcastPkts;
        public long InDiscards;
        public long InErrors;
        public long InUnknownProtos;
        public long InUcastOctets;
        public long InMulticastOctets;
        public long InBroadcastOctets;
        public long OutOctets;
        public long OutUcastPkts;
        public long OutNUcastPkts;
        public long OutDiscards;
        public long OutErrors;
        public long OutUcastOctets;
        public long OutMulticastOctets;
        public long OutBroadcastOctets;
        public long OutQLen;
        
        public MIB_IF_ROW2() {
            this.Alias = new char[257];
            this.Description = new char[257];
            this.PhysicalAddress = new byte[32];
            this.PermanentPhysicalAddress = new byte[32];
        }
    }
    
    @FieldOrder({ "String" })
    public static class IP_ADDRESS_STRING extends Structure
    {
        public byte[] String;
        
        public IP_ADDRESS_STRING() {
            this.String = new byte[16];
        }
    }
    
    @FieldOrder({ "Next", "IpAddress", "IpMask", "Context" })
    public static class IP_ADDR_STRING extends Structure
    {
        public ByReference Next;
        public IP_ADDRESS_STRING IpAddress;
        public IP_ADDRESS_STRING IpMask;
        public int Context;
        
        public static class ByReference extends IP_ADDR_STRING implements Structure.ByReference
        {
        }
    }
    
    @FieldOrder({ "HostName", "DomainName", "CurrentDnsServer", "DnsServerList", "NodeType", "ScopeId", "EnableRouting", "EnableProxy", "EnableDns" })
    public static class FIXED_INFO extends Structure
    {
        public byte[] HostName;
        public byte[] DomainName;
        public IP_ADDR_STRING.ByReference CurrentDnsServer;
        public IP_ADDR_STRING DnsServerList;
        public int NodeType;
        public byte[] ScopeId;
        public int EnableRouting;
        public int EnableProxy;
        public int EnableDns;
        
        public FIXED_INFO(final Pointer p) {
            super(p);
            this.HostName = new byte[132];
            this.DomainName = new byte[132];
            this.ScopeId = new byte[260];
            this.read();
        }
        
        public FIXED_INFO() {
            this.HostName = new byte[132];
            this.DomainName = new byte[132];
            this.ScopeId = new byte[260];
        }
    }
}
