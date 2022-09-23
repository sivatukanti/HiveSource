// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class UserResponser
{
    private final List<UserResponseItem> items;
    
    public UserResponser() {
        this.items = new ArrayList<UserResponseItem>(1);
    }
    
    public void respondQuestions() {
    }
    
    public UserResponseItem findQuestion(final String question) {
        for (final UserResponseItem ri : this.items) {
            if (ri.question.equals(question)) {
                return ri;
            }
        }
        return null;
    }
    
    public void askQuestion(final String question, final String challenge) {
        final UserResponseItem ri = this.findQuestion(question);
        if (ri == null) {
            this.items.add(new UserResponseItem(question, challenge));
        }
        else {
            ri.challenge = challenge;
        }
    }
    
    public String getChallenge(final String question) {
        final UserResponseItem ri = this.findQuestion(question);
        if (ri != null) {
            return ri.challenge;
        }
        return null;
    }
    
    public void setAnswer(final String question, final String answer) {
        final UserResponseItem ri = this.findQuestion(question);
        if (ri == null) {
            throw new IllegalArgumentException("Question isn't exist for the answer");
        }
        ri.answer = answer;
    }
    
    public String getAnswer(final String question) {
        final UserResponseItem ri = this.findQuestion(question);
        if (ri != null) {
            return ri.answer;
        }
        return null;
    }
}
