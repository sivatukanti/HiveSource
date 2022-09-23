// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class TraceEvent extends GuessingEvent
{
    private int ruleNum;
    private int data;
    public static int ENTER;
    public static int EXIT;
    public static int DONE_PARSING;
    
    public TraceEvent(final Object o) {
        super(o);
    }
    
    public TraceEvent(final Object o, final int n, final int n2, final int n3, final int n4) {
        super(o);
        this.setValues(n, n2, n3, n4);
    }
    
    public int getData() {
        return this.data;
    }
    
    public int getRuleNum() {
        return this.ruleNum;
    }
    
    void setData(final int data) {
        this.data = data;
    }
    
    void setRuleNum(final int ruleNum) {
        this.ruleNum = ruleNum;
    }
    
    void setValues(final int n, final int ruleNum, final int n2, final int data) {
        super.setValues(n, n2);
        this.setRuleNum(ruleNum);
        this.setData(data);
    }
    
    public String toString() {
        return "ParserTraceEvent [" + ((this.getType() == TraceEvent.ENTER) ? "enter," : "exit,") + this.getRuleNum() + "," + this.getGuessing() + "]";
    }
    
    static {
        TraceEvent.ENTER = 0;
        TraceEvent.EXIT = 1;
        TraceEvent.DONE_PARSING = 2;
    }
}
