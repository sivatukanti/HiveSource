// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.protocol.TProtocolFactory;
import parquet.org.apache.thrift.TProcessor;

public class TFileProcessor
{
    private TProcessor processor_;
    private TProtocolFactory inputProtocolFactory_;
    private TProtocolFactory outputProtocolFactory_;
    private TFileTransport inputTransport_;
    private TTransport outputTransport_;
    
    public TFileProcessor(final TProcessor processor, final TProtocolFactory protocolFactory, final TFileTransport inputTransport, final TTransport outputTransport) {
        this.processor_ = processor;
        this.outputProtocolFactory_ = protocolFactory;
        this.inputProtocolFactory_ = protocolFactory;
        this.inputTransport_ = inputTransport;
        this.outputTransport_ = outputTransport;
    }
    
    public TFileProcessor(final TProcessor processor, final TProtocolFactory inputProtocolFactory, final TProtocolFactory outputProtocolFactory, final TFileTransport inputTransport, final TTransport outputTransport) {
        this.processor_ = processor;
        this.inputProtocolFactory_ = inputProtocolFactory;
        this.outputProtocolFactory_ = outputProtocolFactory;
        this.inputTransport_ = inputTransport;
        this.outputTransport_ = outputTransport;
    }
    
    private void processUntil(final int lastChunk) throws TException {
        final TProtocol ip = this.inputProtocolFactory_.getProtocol(this.inputTransport_);
        final TProtocol op = this.outputProtocolFactory_.getProtocol(this.outputTransport_);
        int curChunk = this.inputTransport_.getCurChunk();
        try {
            while (lastChunk >= curChunk) {
                this.processor_.process(ip, op);
                final int newChunk = curChunk = this.inputTransport_.getCurChunk();
            }
        }
        catch (TTransportException e) {
            if (e.getType() != 4) {
                throw e;
            }
        }
    }
    
    public void processChunk(int startChunkNum, int endChunkNum) throws TException {
        final int numChunks = this.inputTransport_.getNumChunks();
        if (endChunkNum < 0) {
            endChunkNum += numChunks;
        }
        if (startChunkNum < 0) {
            startChunkNum += numChunks;
        }
        if (endChunkNum < startChunkNum) {
            throw new TException("endChunkNum " + endChunkNum + " is less than " + startChunkNum);
        }
        this.inputTransport_.seekToChunk(startChunkNum);
        this.processUntil(endChunkNum);
    }
    
    public void processChunk(final int chunkNum) throws TException {
        this.processChunk(chunkNum, chunkNum);
    }
    
    public void processChunk() throws TException {
        this.processChunk(this.inputTransport_.getCurChunk());
    }
}
