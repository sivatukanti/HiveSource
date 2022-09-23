// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import javax.xml.bind.annotation.XmlElement;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "delegationtoken")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineDelegationTokenResponse
{
    private String type;
    private Object content;
    
    @XmlElement(name = "type")
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    @XmlElement(name = "content")
    public Object getContent() {
        return this.content;
    }
    
    public void setContent(final Object content) {
        this.content = content;
    }
}
