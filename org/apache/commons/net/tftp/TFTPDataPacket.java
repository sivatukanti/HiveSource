// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public final class TFTPDataPacket extends TFTPPacket
{
    public static final int MAX_DATA_LENGTH = 512;
    public static final int MIN_DATA_LENGTH = 0;
    int _blockNumber;
    int _length;
    int _offset;
    byte[] _data;
    
    public TFTPDataPacket(final InetAddress destination, final int port, final int blockNumber, final byte[] data, final int offset, final int length) {
        super(3, destination, port);
        this._blockNumber = blockNumber;
        this._data = data;
        this._offset = offset;
        if (length > 512) {
            this._length = 512;
        }
        else {
            this._length = length;
        }
    }
    
    public TFTPDataPacket(final InetAddress destination, final int port, final int blockNumber, final byte[] data) {
        this(destination, port, blockNumber, data, 0, data.length);
    }
    
    TFTPDataPacket(final DatagramPacket datagram) throws TFTPPacketException {
        super(3, datagram.getAddress(), datagram.getPort());
        this._data = datagram.getData();
        this._offset = 4;
        if (this.getType() != this._data[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        this._blockNumber = ((this._data[2] & 0xFF) << 8 | (this._data[3] & 0xFF));
        this._length = datagram.getLength() - 4;
        if (this._length > 512) {
            this._length = 512;
        }
    }
    
    @Override
    DatagramPacket _newDatagram(final DatagramPacket datagram, final byte[] data) {
        data[0] = 0;
        data[1] = (byte)this._type;
        data[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        data[3] = (byte)(this._blockNumber & 0xFF);
        if (data != this._data) {
            System.arraycopy(this._data, this._offset, data, 4, this._length);
        }
        datagram.setAddress(this._address);
        datagram.setPort(this._port);
        datagram.setData(data);
        datagram.setLength(this._length + 4);
        return datagram;
    }
    
    @Override
    public DatagramPacket newDatagram() {
        final byte[] data = new byte[this._length + 4];
        data[0] = 0;
        data[1] = (byte)this._type;
        data[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        data[3] = (byte)(this._blockNumber & 0xFF);
        System.arraycopy(this._data, this._offset, data, 4, this._length);
        return new DatagramPacket(data, this._length + 4, this._address, this._port);
    }
    
    public int getBlockNumber() {
        return this._blockNumber;
    }
    
    public void setBlockNumber(final int blockNumber) {
        this._blockNumber = blockNumber;
    }
    
    public void setData(final byte[] data, final int offset, final int length) {
        this._data = data;
        this._offset = offset;
        this._length = length;
        if (length > 512) {
            this._length = 512;
        }
        else {
            this._length = length;
        }
    }
    
    public int getDataLength() {
        return this._length;
    }
    
    public int getDataOffset() {
        return this._offset;
    }
    
    public byte[] getData() {
        return this._data;
    }
    
    @Override
    public String toString() {
        return super.toString() + " DATA " + this._blockNumber + " " + this._length;
    }
}
