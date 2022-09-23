// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

public class UserResponseItem
{
    protected String question;
    protected String challenge;
    protected String answer;
    
    public UserResponseItem(final String question, final String challenge) {
        this.question = question;
        this.challenge = challenge;
    }
}
