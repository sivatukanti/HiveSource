// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class ECSchema implements Serializable
{
    private static final long serialVersionUID = 278215328L;
    public static final String NUM_DATA_UNITS_KEY = "numDataUnits";
    public static final String NUM_PARITY_UNITS_KEY = "numParityUnits";
    public static final String CODEC_NAME_KEY = "codec";
    private final String codecName;
    private final int numDataUnits;
    private final int numParityUnits;
    private final Map<String, String> extraOptions;
    
    public ECSchema(final Map<String, String> allOptions) {
        if (allOptions == null || allOptions.isEmpty()) {
            throw new IllegalArgumentException("No schema options are provided");
        }
        this.codecName = allOptions.get("codec");
        if (this.codecName == null || this.codecName.isEmpty()) {
            throw new IllegalArgumentException("No codec option is provided");
        }
        final int tmpNumDataUnits = this.extractIntOption("numDataUnits", allOptions);
        final int tmpNumParityUnits = this.extractIntOption("numParityUnits", allOptions);
        if (tmpNumDataUnits < 0 || tmpNumParityUnits < 0) {
            throw new IllegalArgumentException("No good option for numDataUnits or numParityUnits found ");
        }
        this.numDataUnits = tmpNumDataUnits;
        this.numParityUnits = tmpNumParityUnits;
        allOptions.remove("codec");
        allOptions.remove("numDataUnits");
        allOptions.remove("numParityUnits");
        this.extraOptions = Collections.unmodifiableMap((Map<? extends String, ? extends String>)allOptions);
    }
    
    public ECSchema(final String codecName, final int numDataUnits, final int numParityUnits) {
        this(codecName, numDataUnits, numParityUnits, null);
    }
    
    public ECSchema(final String codecName, final int numDataUnits, final int numParityUnits, Map<String, String> extraOptions) {
        assert codecName != null && !codecName.isEmpty();
        assert numDataUnits > 0 && numParityUnits > 0;
        this.codecName = codecName;
        this.numDataUnits = numDataUnits;
        this.numParityUnits = numParityUnits;
        if (extraOptions == null) {
            extraOptions = new HashMap<String, String>();
        }
        this.extraOptions = Collections.unmodifiableMap((Map<? extends String, ? extends String>)extraOptions);
    }
    
    private int extractIntOption(final String optionKey, final Map<String, String> options) {
        int result = -1;
        try {
            if (options.containsKey(optionKey)) {
                result = Integer.parseInt(options.get(optionKey));
                if (result <= 0) {
                    throw new IllegalArgumentException("Bad option value " + result + " found for " + optionKey);
                }
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Option value " + options.get(optionKey) + " for " + optionKey + " is found. It should be an integer");
        }
        return result;
    }
    
    public String getCodecName() {
        return this.codecName;
    }
    
    public Map<String, String> getExtraOptions() {
        return this.extraOptions;
    }
    
    public int getNumDataUnits() {
        return this.numDataUnits;
    }
    
    public int getNumParityUnits() {
        return this.numParityUnits;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ECSchema=[");
        sb.append("Codec=" + this.codecName + ", ");
        sb.append("numDataUnits=" + this.numDataUnits + ", ");
        sb.append("numParityUnits=" + this.numParityUnits);
        sb.append(this.extraOptions.isEmpty() ? "" : ", ");
        int i = 0;
        for (final Map.Entry<String, String> entry : this.extraOptions.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + ((++i < this.extraOptions.size()) ? ", " : ""));
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final ECSchema rhs = (ECSchema)o;
        return new EqualsBuilder().append(this.codecName, rhs.codecName).append(this.extraOptions, rhs.extraOptions).append(this.numDataUnits, rhs.numDataUnits).append(this.numParityUnits, rhs.numParityUnits).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1273158869, 1555022101).append(this.codecName).append(this.extraOptions).append(this.numDataUnits).append(this.numParityUnits).toHashCode();
    }
}
