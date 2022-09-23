// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.util.HashSet;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class CandidateListCompletionHandler implements CompletionHandler
{
    private static ResourceBundle loc;
    private boolean eagerNewlines;
    
    public CandidateListCompletionHandler() {
        this.eagerNewlines = true;
    }
    
    public void setAlwaysIncludeNewline(final boolean eagerNewlines) {
        this.eagerNewlines = eagerNewlines;
    }
    
    public boolean complete(final ConsoleReader reader, final List candidates, final int pos) throws IOException {
        final CursorBuffer buf = reader.getCursorBuffer();
        if (candidates.size() != 1) {
            if (candidates.size() > 1) {
                final String value = this.getUnambiguousCompletions(candidates);
                final String bufString = buf.toString();
                setBuffer(reader, value, pos);
            }
            if (this.eagerNewlines) {
                reader.printNewline();
            }
            printCandidates(reader, candidates, this.eagerNewlines);
            reader.drawLine();
            return true;
        }
        final String value = candidates.get(0).toString();
        if (value.equals(buf.toString())) {
            return false;
        }
        setBuffer(reader, value, pos);
        return true;
    }
    
    public static void setBuffer(final ConsoleReader reader, final String value, final int offset) throws IOException {
        while (reader.getCursorBuffer().cursor > offset && reader.backspace()) {}
        reader.putString(value);
        reader.setCursorPosition(offset + value.length());
    }
    
    public static final void printCandidates(final ConsoleReader reader, Collection candidates, final boolean eagerNewlines) throws IOException {
        final Set distinct = new HashSet(candidates);
        if (distinct.size() > reader.getAutoprintThreshhold()) {
            if (!eagerNewlines) {
                reader.printNewline();
            }
            reader.printString(MessageFormat.format(CandidateListCompletionHandler.loc.getString("display-candidates"), new Integer(candidates.size())) + " ");
            reader.flushConsole();
            final String noOpt = CandidateListCompletionHandler.loc.getString("display-candidates-no");
            final String yesOpt = CandidateListCompletionHandler.loc.getString("display-candidates-yes");
            int c;
            while ((c = reader.readCharacter(new char[] { yesOpt.charAt(0), noOpt.charAt(0) })) != -1) {
                if (noOpt.startsWith(new String(new char[] { (char)c }))) {
                    reader.printNewline();
                    return;
                }
                if (yesOpt.startsWith(new String(new char[] { (char)c }))) {
                    break;
                }
                reader.beep();
            }
        }
        if (distinct.size() != candidates.size()) {
            final Collection copy = new ArrayList();
            for (final Object next : candidates) {
                if (!copy.contains(next)) {
                    copy.add(next);
                }
            }
            candidates = copy;
        }
        reader.printNewline();
        reader.printColumns(candidates);
    }
    
    private final String getUnambiguousCompletions(final List candidates) {
        if (candidates == null || candidates.size() == 0) {
            return null;
        }
        final String[] strings = candidates.toArray(new String[candidates.size()]);
        final String first = strings[0];
        final StringBuffer candidate = new StringBuffer();
        for (int i = 0; i < first.length() && this.startsWith(first.substring(0, i + 1), strings); ++i) {
            candidate.append(first.charAt(i));
        }
        return candidate.toString();
    }
    
    private final boolean startsWith(final String starts, final String[] candidates) {
        for (int i = 0; i < candidates.length; ++i) {
            if (!candidates[i].startsWith(starts)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        CandidateListCompletionHandler.loc = ResourceBundle.getBundle(CandidateListCompletionHandler.class.getName());
    }
}
