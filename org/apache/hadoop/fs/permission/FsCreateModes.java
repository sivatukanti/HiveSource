// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.text.MessageFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class FsCreateModes extends FsPermission
{
    private static final long serialVersionUID = 580415341L;
    private final FsPermission unmasked;
    
    public static FsPermission applyUMask(final FsPermission mode, final FsPermission umask) {
        if (mode.getUnmasked() != null) {
            return mode;
        }
        return create(mode.applyUMask(umask), mode);
    }
    
    public static FsCreateModes create(final FsPermission masked, final FsPermission unmasked) {
        assert masked.getUnmasked() == null;
        assert unmasked.getUnmasked() == null;
        return new FsCreateModes(masked, unmasked);
    }
    
    private FsCreateModes(final FsPermission masked, final FsPermission unmasked) {
        super(masked);
        this.unmasked = unmasked;
        assert masked.getUnmasked() == null;
        assert unmasked.getUnmasked() == null;
    }
    
    @Override
    public FsPermission getMasked() {
        return this;
    }
    
    @Override
    public FsPermission getUnmasked() {
        return this.unmasked;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("'{' masked: {0}, unmasked: {1} '}'", super.toString(), this.getUnmasked());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final FsCreateModes that = (FsCreateModes)o;
        return this.getUnmasked().equals(that.getUnmasked());
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getUnmasked().hashCode();
        return result;
    }
}
