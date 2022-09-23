// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.resource;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ResourceWeights
{
    public static final ResourceWeights NEUTRAL;
    private float[] weights;
    
    public ResourceWeights(final float memoryWeight, final float cpuWeight) {
        (this.weights = new float[ResourceType.values().length])[ResourceType.MEMORY.ordinal()] = memoryWeight;
        this.weights[ResourceType.CPU.ordinal()] = cpuWeight;
    }
    
    public ResourceWeights(final float weight) {
        this.weights = new float[ResourceType.values().length];
        this.setWeight(weight);
    }
    
    public ResourceWeights() {
        this.weights = new float[ResourceType.values().length];
    }
    
    public void setWeight(final float weight) {
        for (int i = 0; i < this.weights.length; ++i) {
            this.weights[i] = weight;
        }
    }
    
    public void setWeight(final ResourceType resourceType, final float weight) {
        this.weights[resourceType.ordinal()] = weight;
    }
    
    public float getWeight(final ResourceType resourceType) {
        return this.weights[resourceType.ordinal()];
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<");
        for (int i = 0; i < ResourceType.values().length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            final ResourceType resourceType = ResourceType.values()[i];
            sb.append(resourceType.name().toLowerCase());
            sb.append(String.format(" weight=%.1f", this.getWeight(resourceType)));
        }
        sb.append(">");
        return sb.toString();
    }
    
    static {
        NEUTRAL = new ResourceWeights(1.0f);
    }
}
