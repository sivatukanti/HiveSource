// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder> implements GeneratedMessage.BuilderParent
{
    private GeneratedMessage.BuilderParent parent;
    private BType builder;
    private MType message;
    private boolean isClean;
    
    public SingleFieldBuilder(final MType message, final GeneratedMessage.BuilderParent parent, final boolean isClean) {
        if (message == null) {
            throw new NullPointerException();
        }
        this.message = message;
        this.parent = parent;
        this.isClean = isClean;
    }
    
    public void dispose() {
        this.parent = null;
    }
    
    public MType getMessage() {
        if (this.message == null) {
            this.message = (MType)this.builder.buildPartial();
        }
        return this.message;
    }
    
    public MType build() {
        this.isClean = true;
        return this.getMessage();
    }
    
    public BType getBuilder() {
        if (this.builder == null) {
            (this.builder = (BType)this.message.newBuilderForType(this)).mergeFrom(this.message);
            this.builder.markClean();
        }
        return this.builder;
    }
    
    public IType getMessageOrBuilder() {
        if (this.builder != null) {
            return (IType)this.builder;
        }
        return (IType)this.message;
    }
    
    public SingleFieldBuilder<MType, BType, IType> setMessage(final MType message) {
        if (message == null) {
            throw new NullPointerException();
        }
        this.message = message;
        if (this.builder != null) {
            this.builder.dispose();
            this.builder = null;
        }
        this.onChanged();
        return this;
    }
    
    public SingleFieldBuilder<MType, BType, IType> mergeFrom(final MType value) {
        if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
            this.message = value;
        }
        else {
            this.getBuilder().mergeFrom(value);
        }
        this.onChanged();
        return this;
    }
    
    public SingleFieldBuilder<MType, BType, IType> clear() {
        this.message = (MType)((this.message != null) ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
        if (this.builder != null) {
            this.builder.dispose();
            this.builder = null;
        }
        this.onChanged();
        return this;
    }
    
    private void onChanged() {
        if (this.builder != null) {
            this.message = null;
        }
        if (this.isClean && this.parent != null) {
            this.parent.markDirty();
            this.isClean = false;
        }
    }
    
    public void markDirty() {
        this.onChanged();
    }
}
