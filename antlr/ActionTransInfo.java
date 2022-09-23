// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class ActionTransInfo
{
    public boolean assignToRoot;
    public String refRuleRoot;
    public String followSetName;
    
    public ActionTransInfo() {
        this.assignToRoot = false;
        this.refRuleRoot = null;
        this.followSetName = null;
    }
    
    public String toString() {
        return "assignToRoot:" + this.assignToRoot + ", refRuleRoot:" + this.refRuleRoot + ", FOLLOW Set:" + this.followSetName;
    }
}
