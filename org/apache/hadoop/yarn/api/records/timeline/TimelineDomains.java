// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "domains")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineDomains
{
    private List<TimelineDomain> domains;
    
    public TimelineDomains() {
        this.domains = new ArrayList<TimelineDomain>();
    }
    
    @XmlElement(name = "domains")
    public List<TimelineDomain> getDomains() {
        return this.domains;
    }
    
    public void addDomain(final TimelineDomain domain) {
        this.domains.add(domain);
    }
    
    public void addDomains(final List<TimelineDomain> domains) {
        this.domains.addAll(domains);
    }
    
    public void setDomains(final List<TimelineDomain> domains) {
        this.domains = domains;
    }
}
