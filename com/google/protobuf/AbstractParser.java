// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;
import java.io.IOException;

public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType>
{
    private static final ExtensionRegistryLite EMPTY_REGISTRY;
    
    private UninitializedMessageException newUninitializedMessageException(final MessageType message) {
        if (message instanceof AbstractMessageLite) {
            return ((AbstractMessageLite)message).newUninitializedMessageException();
        }
        return new UninitializedMessageException(message);
    }
    
    private MessageType checkMessageInitialized(final MessageType message) throws InvalidProtocolBufferException {
        if (message != null && !message.isInitialized()) {
            throw this.newUninitializedMessageException(message).asInvalidProtocolBufferException().setUnfinishedMessage(message);
        }
        return message;
    }
    
    public MessageType parsePartialFrom(final CodedInputStream input) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.checkMessageInitialized(this.parsePartialFrom(input, extensionRegistry));
    }
    
    public MessageType parseFrom(final CodedInputStream input) throws InvalidProtocolBufferException {
        return this.parseFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parsePartialFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            final CodedInputStream input = data.newCodedInput();
            final MessageType message = this.parsePartialFrom(input, extensionRegistry);
            try {
                input.checkLastTagWas(0);
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(message);
            }
            return message;
        }
        catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
        catch (IOException e3) {
            throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e3);
        }
    }
    
    public MessageType parsePartialFrom(final ByteString data) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(data, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.checkMessageInitialized(this.parsePartialFrom(data, extensionRegistry));
    }
    
    public MessageType parseFrom(final ByteString data) throws InvalidProtocolBufferException {
        return this.parseFrom(data, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parsePartialFrom(final byte[] data, final int off, final int len, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            final CodedInputStream input = CodedInputStream.newInstance(data, off, len);
            final MessageType message = this.parsePartialFrom(input, extensionRegistry);
            try {
                input.checkLastTagWas(0);
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(message);
            }
            return message;
        }
        catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
        catch (IOException e3) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e3);
        }
    }
    
    public MessageType parsePartialFrom(final byte[] data, final int off, final int len) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(data, off, len, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parsePartialFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(data, 0, data.length, extensionRegistry);
    }
    
    public MessageType parsePartialFrom(final byte[] data) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(data, 0, data.length, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseFrom(final byte[] data, final int off, final int len, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.checkMessageInitialized(this.parsePartialFrom(data, off, len, extensionRegistry));
    }
    
    public MessageType parseFrom(final byte[] data, final int off, final int len) throws InvalidProtocolBufferException {
        return this.parseFrom(data, off, len, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.parseFrom(data, 0, data.length, extensionRegistry);
    }
    
    public MessageType parseFrom(final byte[] data) throws InvalidProtocolBufferException {
        return this.parseFrom(data, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parsePartialFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        final CodedInputStream codedInput = CodedInputStream.newInstance(input);
        final MessageType message = this.parsePartialFrom(codedInput, extensionRegistry);
        try {
            codedInput.checkLastTagWas(0);
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        }
        return message;
    }
    
    public MessageType parsePartialFrom(final InputStream input) throws InvalidProtocolBufferException {
        return this.parsePartialFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.checkMessageInitialized(this.parsePartialFrom(input, extensionRegistry));
    }
    
    public MessageType parseFrom(final InputStream input) throws InvalidProtocolBufferException {
        return this.parseFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parsePartialDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        int size;
        try {
            final int firstByte = input.read();
            if (firstByte == -1) {
                return null;
            }
            size = CodedInputStream.readRawVarint32(firstByte, input);
        }
        catch (IOException e) {
            throw new InvalidProtocolBufferException(e.getMessage());
        }
        final InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
        return this.parsePartialFrom(limitedInput, extensionRegistry);
    }
    
    public MessageType parsePartialDelimitedFrom(final InputStream input) throws InvalidProtocolBufferException {
        return this.parsePartialDelimitedFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    public MessageType parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return this.checkMessageInitialized(this.parsePartialDelimitedFrom(input, extensionRegistry));
    }
    
    public MessageType parseDelimitedFrom(final InputStream input) throws InvalidProtocolBufferException {
        return this.parseDelimitedFrom(input, AbstractParser.EMPTY_REGISTRY);
    }
    
    static {
        EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();
    }
}
