// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.TreeMap;
import java.util.Collections;
import java.util.Collection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.Reader;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Properties;
import java.util.Arrays;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileDescriptor;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.List;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.ResourceBundle;

public class ConsoleReader implements ConsoleOperations
{
    static final int TAB_WIDTH = 4;
    String prompt;
    private boolean useHistory;
    private boolean usePagination;
    public static final String CR;
    private static ResourceBundle loc;
    public static SortedMap KEYMAP_NAMES;
    private final short[] keybindings;
    private boolean bellEnabled;
    private Character mask;
    private static final Character NULL_MASK;
    private int autoprintThreshhold;
    private final Terminal terminal;
    private CompletionHandler completionHandler;
    InputStream in;
    final Writer out;
    final CursorBuffer buf;
    static PrintWriter debugger;
    History history;
    final List completors;
    private Character echoCharacter;
    private Map triggeredActions;
    
    public void addTriggeredAction(final char c, final ActionListener listener) {
        this.triggeredActions.put(new Character(c), listener);
    }
    
    public ConsoleReader() throws IOException {
        this(new FileInputStream(FileDescriptor.in), new PrintWriter(new OutputStreamWriter(System.out, System.getProperty("jline.WindowsTerminal.output.encoding", System.getProperty("file.encoding")))));
    }
    
    public ConsoleReader(final InputStream in, final Writer out) throws IOException {
        this(in, out, null);
    }
    
    public ConsoleReader(final InputStream in, final Writer out, final InputStream bindings) throws IOException {
        this(in, out, bindings, Terminal.getTerminal());
    }
    
    public ConsoleReader(final InputStream in, final Writer out, InputStream bindings, final Terminal term) throws IOException {
        this.useHistory = true;
        this.usePagination = false;
        this.bellEnabled = true;
        this.mask = null;
        this.autoprintThreshhold = Integer.getInteger("jline.completion.threshold", 100);
        this.completionHandler = new CandidateListCompletionHandler();
        this.buf = new CursorBuffer();
        this.history = new History();
        this.completors = new LinkedList();
        this.echoCharacter = null;
        this.triggeredActions = new HashMap();
        this.terminal = term;
        this.setInput(in);
        this.out = out;
        if (bindings == null) {
            try {
                final String bindingFile = System.getProperty("jline.keybindings", new File(System.getProperty("user.home", ".jlinebindings.properties")).getAbsolutePath());
                if (new File(bindingFile).isFile()) {
                    bindings = new FileInputStream(new File(bindingFile));
                }
            }
            catch (Exception e) {
                if (ConsoleReader.debugger != null) {
                    e.printStackTrace(ConsoleReader.debugger);
                }
            }
        }
        if (bindings == null) {
            bindings = this.terminal.getDefaultBindings();
        }
        Arrays.fill(this.keybindings = new short[131070], (short)(-99));
        if (bindings != null) {
            final Properties p = new Properties();
            p.load(bindings);
            bindings.close();
            for (final String val : p.keySet()) {
                try {
                    final Short code = new Short(val);
                    final String op = p.getProperty(val);
                    final Short opval = (Short)ConsoleReader.KEYMAP_NAMES.get(op);
                    if (opval == null) {
                        continue;
                    }
                    this.keybindings[code] = opval;
                }
                catch (NumberFormatException nfe) {
                    this.consumeException(nfe);
                }
            }
        }
    }
    
    public Terminal getTerminal() {
        return this.terminal;
    }
    
    public void setDebug(final PrintWriter debugger) {
        ConsoleReader.debugger = debugger;
    }
    
    public void setInput(final InputStream in) {
        this.in = in;
    }
    
    public InputStream getInput() {
        return this.in;
    }
    
    public String readLine() throws IOException {
        return this.readLine((String)null);
    }
    
    public String readLine(final Character mask) throws IOException {
        return this.readLine(null, mask);
    }
    
    public void setBellEnabled(final boolean bellEnabled) {
        this.bellEnabled = bellEnabled;
    }
    
    public boolean getBellEnabled() {
        return this.bellEnabled;
    }
    
    public int getTermwidth() {
        return Terminal.setupTerminal().getTerminalWidth();
    }
    
    public int getTermheight() {
        return Terminal.setupTerminal().getTerminalHeight();
    }
    
    public void setAutoprintThreshhold(final int autoprintThreshhold) {
        this.autoprintThreshhold = autoprintThreshhold;
    }
    
    public int getAutoprintThreshhold() {
        return this.autoprintThreshhold;
    }
    
    int getKeyForAction(final short logicalAction) {
        for (int i = 0; i < this.keybindings.length; ++i) {
            if (this.keybindings[i] == logicalAction) {
                return i;
            }
        }
        return -1;
    }
    
    int clearEcho(final int c) throws IOException {
        if (!this.terminal.getEcho()) {
            return 0;
        }
        final int num = this.countEchoCharacters((char)c);
        this.back(num);
        this.drawBuffer(num);
        return num;
    }
    
    int countEchoCharacters(final char c) {
        if (c == '\t') {
            final int tabstop = 8;
            final int position = this.getCursorPosition();
            return tabstop - position % tabstop;
        }
        return this.getPrintableCharacters(c).length();
    }
    
    StringBuffer getPrintableCharacters(final char ch) {
        final StringBuffer sbuff = new StringBuffer();
        if (ch >= ' ') {
            if (ch < '\u007f') {
                sbuff.append(ch);
            }
            else if (ch == '\u007f') {
                sbuff.append('^');
                sbuff.append('?');
            }
            else {
                sbuff.append('M');
                sbuff.append('-');
                if (ch >= ' ') {
                    if (ch < '\u00ff') {
                        sbuff.append((char)(ch - '\u0080'));
                    }
                    else {
                        sbuff.append('^');
                        sbuff.append('?');
                    }
                }
                else {
                    sbuff.append('^');
                    sbuff.append((char)(ch - '\u0080' + 64));
                }
            }
        }
        else {
            sbuff.append('^');
            sbuff.append((char)(ch + '@'));
        }
        return sbuff;
    }
    
    int getCursorPosition() {
        return ((this.prompt == null) ? 0 : this.prompt.length()) + this.buf.cursor;
    }
    
    public String readLine(final String prompt) throws IOException {
        return this.readLine(prompt, null);
    }
    
    public void setDefaultPrompt(final String prompt) {
        this.prompt = prompt;
    }
    
    public String getDefaultPrompt() {
        return this.prompt;
    }
    
    public String readLine(final String prompt, final Character mask) throws IOException {
        this.mask = mask;
        if (prompt != null) {
            this.prompt = prompt;
        }
        try {
            this.terminal.beforeReadLine(this, this.prompt, mask);
            if (this.prompt != null && this.prompt.length() > 0) {
                this.out.write(this.prompt);
                this.out.flush();
            }
            if (!this.terminal.isSupported()) {
                return this.readLine(this.in);
            }
            while (true) {
                final int[] next = this.readBinding();
                if (next == null) {
                    return null;
                }
                final int c = next[0];
                final int code = next[1];
                if (c == -1) {
                    return null;
                }
                boolean success = true;
                switch (code) {
                    case -59: {
                        if (this.buf.buffer.length() == 0) {
                            return null;
                        }
                        break;
                    }
                    case -58: {
                        success = this.complete();
                        break;
                    }
                    case -1: {
                        success = this.setCursorPosition(0);
                        break;
                    }
                    case -7: {
                        success = this.killLine();
                        break;
                    }
                    case -8: {
                        success = this.clearScreen();
                        break;
                    }
                    case -15: {
                        success = this.resetLine();
                        break;
                    }
                    case -6: {
                        this.moveToEnd();
                        this.printNewline();
                        return this.finishBuffer();
                    }
                    case -41: {
                        success = this.backspace();
                        break;
                    }
                    case -56: {
                        success = this.deleteCurrentCharacter();
                        break;
                    }
                    case -3: {
                        success = this.moveToEnd();
                        break;
                    }
                    case -4: {
                        success = (this.moveCursor(-1) != 0);
                        break;
                    }
                    case -19: {
                        success = (this.moveCursor(1) != 0);
                        break;
                    }
                    case -9: {
                        success = this.moveHistory(true);
                        break;
                    }
                    case -11: {
                        success = this.moveHistory(false);
                        break;
                    }
                    case -13: {
                        break;
                    }
                    case -60: {
                        success = this.paste();
                        break;
                    }
                    case -16: {
                        success = this.deletePreviousWord();
                        break;
                    }
                    case -43: {
                        success = this.previousWord();
                        break;
                    }
                    case -55: {
                        success = this.nextWord();
                        break;
                    }
                    case -61: {
                        success = this.history.moveToFirstEntry();
                        if (success) {
                            this.setBuffer(this.history.current());
                            break;
                        }
                        break;
                    }
                    case -62: {
                        success = this.history.moveToLastEntry();
                        if (success) {
                            this.setBuffer(this.history.current());
                            break;
                        }
                        break;
                    }
                    case -63: {
                        this.moveInternal(-this.buf.buffer.length());
                        this.killLine();
                        break;
                    }
                    case -48: {
                        this.buf.setOvertyping(!this.buf.isOvertyping());
                        break;
                    }
                    default: {
                        if (c != 0) {
                            final ActionListener action = this.triggeredActions.get(new Character((char)c));
                            if (action != null) {
                                action.actionPerformed(null);
                            }
                            else {
                                this.putChar(c, true);
                            }
                            break;
                        }
                        success = false;
                        break;
                    }
                }
                if (!success) {
                    this.beep();
                }
                this.flushConsole();
            }
        }
        finally {
            this.terminal.afterReadLine(this, this.prompt, mask);
        }
    }
    
    private String readLine(final InputStream in) throws IOException {
        final StringBuffer buf = new StringBuffer();
        while (true) {
            final int i = in.read();
            if (i == -1 || i == 10 || i == 13) {
                break;
            }
            buf.append((char)i);
        }
        return buf.toString();
    }
    
    private int[] readBinding() throws IOException {
        final int c = this.readVirtualKey();
        if (c == -1) {
            return null;
        }
        final short code = this.keybindings[c];
        if (ConsoleReader.debugger != null) {
            debug("    translated: " + c + ": " + code);
        }
        return new int[] { c, code };
    }
    
    private final boolean moveHistory(final boolean next) throws IOException {
        if (next && !this.history.next()) {
            return false;
        }
        if (!next && !this.history.previous()) {
            return false;
        }
        this.setBuffer(this.history.current());
        return true;
    }
    
    public boolean paste() throws IOException {
        Clipboard clipboard;
        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        catch (Exception e) {
            return false;
        }
        if (clipboard == null) {
            return false;
        }
        final Transferable transferable = clipboard.getContents(null);
        if (transferable == null) {
            return false;
        }
        try {
            Object content = transferable.getTransferData(DataFlavor.plainTextFlavor);
            if (content == null) {
                try {
                    content = new DataFlavor().getReaderForText(transferable);
                }
                catch (Exception ex) {}
            }
            if (content == null) {
                return false;
            }
            String value;
            if (content instanceof Reader) {
                value = "";
                String line = null;
                final BufferedReader read = new BufferedReader((Reader)content);
                while ((line = read.readLine()) != null) {
                    if (value.length() > 0) {
                        value += "\n";
                    }
                    value += line;
                }
            }
            else {
                value = content.toString();
            }
            if (value == null) {
                return true;
            }
            this.putString(value);
            return true;
        }
        catch (UnsupportedFlavorException ufe) {
            if (ConsoleReader.debugger != null) {
                debug(ufe + "");
            }
            return false;
        }
    }
    
    public boolean killLine() throws IOException {
        final int cp = this.buf.cursor;
        final int len = this.buf.buffer.length();
        if (cp >= len) {
            return false;
        }
        final int num = this.buf.buffer.length() - cp;
        this.clearAhead(num);
        for (int i = 0; i < num; ++i) {
            this.buf.buffer.deleteCharAt(len - i - 1);
        }
        return true;
    }
    
    public boolean clearScreen() throws IOException {
        if (!this.terminal.isANSISupported()) {
            return false;
        }
        this.printString("\u001b[2J");
        this.flushConsole();
        this.printString("\u001b[1;1H");
        this.flushConsole();
        this.redrawLine();
        return true;
    }
    
    private final boolean complete() throws IOException {
        if (this.completors.size() == 0) {
            return false;
        }
        final List candidates = new LinkedList();
        final String bufstr = this.buf.buffer.toString();
        final int cursor = this.buf.cursor;
        int position = -1;
        for (final Completor comp : this.completors) {
            if ((position = comp.complete(bufstr, cursor, candidates)) != -1) {
                break;
            }
        }
        return candidates.size() != 0 && this.completionHandler.complete(this, candidates, position);
    }
    
    public CursorBuffer getCursorBuffer() {
        return this.buf;
    }
    
    public void printColumns(final Collection stuff) throws IOException {
        if (stuff == null || stuff.size() == 0) {
            return;
        }
        final int width = this.getTermwidth();
        int maxwidth = 0;
        final Iterator i = stuff.iterator();
        while (i.hasNext()) {
            maxwidth = Math.max(maxwidth, i.next().toString().length());
        }
        final StringBuffer line = new StringBuffer();
        int showLines;
        if (this.usePagination) {
            showLines = this.getTermheight() - 1;
        }
        else {
            showLines = Integer.MAX_VALUE;
        }
        for (final String cur : stuff) {
            if (line.length() + maxwidth > width) {
                this.printString(line.toString().trim());
                this.printNewline();
                line.setLength(0);
                if (--showLines == 0) {
                    this.printString(ConsoleReader.loc.getString("display-more"));
                    this.flushConsole();
                    final int c = this.readVirtualKey();
                    if (c == 13 || c == 10) {
                        showLines = 1;
                    }
                    else if (c != 113) {
                        showLines = this.getTermheight() - 1;
                    }
                    this.back(ConsoleReader.loc.getString("display-more").length());
                    if (c == 113) {
                        break;
                    }
                }
            }
            this.pad(cur, maxwidth + 3, line);
        }
        if (line.length() > 0) {
            this.printString(line.toString().trim());
            this.printNewline();
            line.setLength(0);
        }
    }
    
    private final void pad(final String toPad, final int len, final StringBuffer appendTo) {
        appendTo.append(toPad);
        int i = 0;
        while (i < len - toPad.length()) {
            ++i;
            appendTo.append(' ');
        }
    }
    
    public boolean addCompletor(final Completor completor) {
        return this.completors.add(completor);
    }
    
    public boolean removeCompletor(final Completor completor) {
        return this.completors.remove(completor);
    }
    
    public Collection getCompletors() {
        return Collections.unmodifiableList((List<?>)this.completors);
    }
    
    final boolean resetLine() throws IOException {
        if (this.buf.cursor == 0) {
            return false;
        }
        this.backspaceAll();
        return true;
    }
    
    public final boolean setCursorPosition(final int position) throws IOException {
        return this.moveCursor(position - this.buf.cursor) != 0;
    }
    
    private final void setBuffer(final String buffer) throws IOException {
        if (buffer.equals(this.buf.buffer.toString())) {
            return;
        }
        int sameIndex = 0;
        for (int i = 0, l1 = buffer.length(), l2 = this.buf.buffer.length(); i < l1 && i < l2 && buffer.charAt(i) == this.buf.buffer.charAt(i); ++i) {
            ++sameIndex;
        }
        final int diff = this.buf.buffer.length() - sameIndex;
        this.backspace(diff);
        this.killLine();
        this.buf.buffer.setLength(sameIndex);
        this.putString(buffer.substring(sameIndex));
    }
    
    public final void redrawLine() throws IOException {
        this.printCharacter(13);
        this.flushConsole();
        this.drawLine();
    }
    
    public final void drawLine() throws IOException {
        if (this.prompt != null) {
            this.printString(this.prompt);
        }
        this.printString(this.buf.buffer.toString());
        if (this.buf.length() != this.buf.cursor) {
            this.back(this.buf.length() - this.buf.cursor);
        }
    }
    
    public final void printNewline() throws IOException {
        this.printString(ConsoleReader.CR);
        this.flushConsole();
    }
    
    final String finishBuffer() {
        final String str = this.buf.buffer.toString();
        if (str.length() > 0) {
            if (this.mask == null && this.useHistory) {
                this.history.addToHistory(str);
            }
            else {
                this.mask = null;
            }
        }
        this.history.moveToEnd();
        this.buf.buffer.setLength(0);
        this.buf.cursor = 0;
        return str;
    }
    
    public final void putString(final String str) throws IOException {
        this.buf.write(str);
        this.printString(str);
        this.drawBuffer();
    }
    
    public final void printString(final String str) throws IOException {
        this.printCharacters(str.toCharArray());
    }
    
    private final void putChar(final int c, final boolean print) throws IOException {
        this.buf.write((char)c);
        if (print) {
            if (this.mask == null) {
                this.printCharacter(c);
            }
            else if (this.mask != '\0') {
                this.printCharacter(this.mask);
            }
            this.drawBuffer();
        }
    }
    
    private final void drawBuffer(final int clear) throws IOException {
        final char[] chars = this.buf.buffer.substring(this.buf.cursor).toCharArray();
        if (this.mask != null) {
            Arrays.fill(chars, this.mask);
        }
        this.printCharacters(chars);
        this.clearAhead(clear);
        this.back(chars.length);
        this.flushConsole();
    }
    
    private final void drawBuffer() throws IOException {
        this.drawBuffer(0);
    }
    
    private final void clearAhead(final int num) throws IOException {
        if (num == 0) {
            return;
        }
        this.printCharacters(' ', num);
        this.flushConsole();
        this.back(num);
        this.flushConsole();
    }
    
    private final void back(final int num) throws IOException {
        this.printCharacters('\b', num);
        this.flushConsole();
    }
    
    public final void beep() throws IOException {
        if (!this.getBellEnabled()) {
            return;
        }
        this.printCharacter(7);
        this.flushConsole();
    }
    
    private final void printCharacter(final int c) throws IOException {
        if (c == 9) {
            final char[] cbuf = new char[4];
            Arrays.fill(cbuf, ' ');
            this.out.write(cbuf);
            return;
        }
        this.out.write(c);
    }
    
    private final void printCharacters(final char[] c) throws IOException {
        int len = 0;
        for (int i = 0; i < c.length; ++i) {
            if (c[i] == '\t') {
                len += 4;
            }
            else {
                ++len;
            }
        }
        char[] cbuf;
        if (len == c.length) {
            cbuf = c;
        }
        else {
            cbuf = new char[len];
            int pos = 0;
            for (int j = 0; j < c.length; ++j) {
                if (c[j] == '\t') {
                    Arrays.fill(cbuf, pos, pos + 4, ' ');
                    pos += 4;
                }
                else {
                    cbuf[pos] = c[j];
                    ++pos;
                }
            }
        }
        this.out.write(cbuf);
    }
    
    private final void printCharacters(final char c, final int num) throws IOException {
        if (num == 1) {
            this.printCharacter(c);
        }
        else {
            final char[] chars = new char[num];
            Arrays.fill(chars, c);
            this.printCharacters(chars);
        }
    }
    
    public final void flushConsole() throws IOException {
        this.out.flush();
    }
    
    private final int backspaceAll() throws IOException {
        return this.backspace(Integer.MAX_VALUE);
    }
    
    private final int backspace(final int num) throws IOException {
        if (this.buf.cursor == 0) {
            return 0;
        }
        int count = 0;
        count = this.moveCursor(-1 * num) * -1;
        this.buf.buffer.delete(this.buf.cursor, this.buf.cursor + count);
        this.drawBuffer(count);
        return count;
    }
    
    public final boolean backspace() throws IOException {
        return this.backspace(1) == 1;
    }
    
    private final boolean moveToEnd() throws IOException {
        if (this.moveCursor(1) == 0) {
            return false;
        }
        while (this.moveCursor(1) != 0) {}
        return true;
    }
    
    private final boolean deleteCurrentCharacter() throws IOException {
        final boolean success = this.buf.buffer.length() > 0;
        if (!success) {
            return false;
        }
        if (this.buf.cursor == this.buf.buffer.length()) {
            return false;
        }
        this.buf.buffer.deleteCharAt(this.buf.cursor);
        this.drawBuffer(1);
        return true;
    }
    
    private final boolean previousWord() throws IOException {
        while (this.isDelimiter(this.buf.current()) && this.moveCursor(-1) != 0) {}
        while (!this.isDelimiter(this.buf.current()) && this.moveCursor(-1) != 0) {}
        return true;
    }
    
    private final boolean nextWord() throws IOException {
        while (this.isDelimiter(this.buf.current()) && this.moveCursor(1) != 0) {}
        while (!this.isDelimiter(this.buf.current()) && this.moveCursor(1) != 0) {}
        return true;
    }
    
    private final boolean deletePreviousWord() throws IOException {
        while (this.isDelimiter(this.buf.current()) && this.backspace()) {}
        while (!this.isDelimiter(this.buf.current()) && this.backspace()) {}
        return true;
    }
    
    public final int moveCursor(final int num) throws IOException {
        int where = num;
        if (this.buf.cursor == 0 && where < 0) {
            return 0;
        }
        if (this.buf.cursor == this.buf.buffer.length() && where > 0) {
            return 0;
        }
        if (this.buf.cursor + where < 0) {
            where = -this.buf.cursor;
        }
        else if (this.buf.cursor + where > this.buf.buffer.length()) {
            where = this.buf.buffer.length() - this.buf.cursor;
        }
        this.moveInternal(where);
        return where;
    }
    
    public static void debug(final String str) {
        if (ConsoleReader.debugger != null) {
            ConsoleReader.debugger.println(str);
            ConsoleReader.debugger.flush();
        }
    }
    
    private final void moveInternal(final int where) throws IOException {
        final CursorBuffer buf = this.buf;
        buf.cursor += where;
        if (where < 0) {
            int len = 0;
            for (int i = this.buf.cursor; i < this.buf.cursor - where; ++i) {
                if (this.buf.getBuffer().charAt(i) == '\t') {
                    len += 4;
                }
                else {
                    ++len;
                }
            }
            final char[] cbuf = new char[len];
            Arrays.fill(cbuf, '\b');
            this.out.write(cbuf);
            return;
        }
        if (this.buf.cursor == 0) {
            return;
        }
        if (this.mask == null) {
            this.printCharacters(this.buf.buffer.substring(this.buf.cursor - where, this.buf.cursor).toCharArray());
            return;
        }
        final char c = this.mask;
        if (ConsoleReader.NULL_MASK.equals(this.mask)) {
            return;
        }
        this.printCharacters(c, Math.abs(where));
    }
    
    public final int readVirtualKey() throws IOException {
        final int c = this.terminal.readVirtualKey(this.in);
        if (ConsoleReader.debugger != null) {
            debug("keystroke: " + c + "");
        }
        this.clearEcho(c);
        return c;
    }
    
    public final int readCharacter(final char[] allowed) throws IOException {
        Arrays.sort(allowed);
        char c;
        while (Arrays.binarySearch(allowed, c = (char)this.readVirtualKey()) < 0) {}
        return c;
    }
    
    private final int delete(final int num) throws IOException {
        this.buf.buffer.delete(this.buf.cursor, this.buf.cursor + 1);
        this.drawBuffer(1);
        return 1;
    }
    
    public final boolean replace(final int num, final String replacement) {
        this.buf.buffer.replace(this.buf.cursor - num, this.buf.cursor, replacement);
        try {
            this.moveCursor(-num);
            this.drawBuffer(Math.max(0, num - replacement.length()));
            this.moveCursor(replacement.length());
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public final boolean delete() throws IOException {
        return this.delete(1) == 1;
    }
    
    public void setHistory(final History history) {
        this.history = history;
    }
    
    public History getHistory() {
        return this.history;
    }
    
    public void setCompletionHandler(final CompletionHandler completionHandler) {
        this.completionHandler = completionHandler;
    }
    
    public CompletionHandler getCompletionHandler() {
        return this.completionHandler;
    }
    
    public void setEchoCharacter(final Character echoCharacter) {
        this.echoCharacter = echoCharacter;
    }
    
    public Character getEchoCharacter() {
        return this.echoCharacter;
    }
    
    private void consumeException(final Throwable e) {
    }
    
    private boolean isDelimiter(final char c) {
        return !Character.isLetterOrDigit(c);
    }
    
    public void setUseHistory(final boolean useHistory) {
        this.useHistory = useHistory;
    }
    
    public boolean getUseHistory() {
        return this.useHistory;
    }
    
    public void setUsePagination(final boolean usePagination) {
        this.usePagination = usePagination;
    }
    
    public boolean getUsePagination() {
        return this.usePagination;
    }
    
    static {
        CR = System.getProperty("line.separator");
        ConsoleReader.loc = ResourceBundle.getBundle(CandidateListCompletionHandler.class.getName());
        final Map names = new TreeMap();
        names.put("MOVE_TO_BEG", new Short((short)(-1)));
        names.put("MOVE_TO_END", new Short((short)(-3)));
        names.put("PREV_CHAR", new Short((short)(-4)));
        names.put("NEWLINE", new Short((short)(-6)));
        names.put("KILL_LINE", new Short((short)(-7)));
        names.put("PASTE", new Short((short)(-60)));
        names.put("CLEAR_SCREEN", new Short((short)(-8)));
        names.put("NEXT_HISTORY", new Short((short)(-9)));
        names.put("PREV_HISTORY", new Short((short)(-11)));
        names.put("START_OF_HISTORY", new Short((short)(-61)));
        names.put("END_OF_HISTORY", new Short((short)(-62)));
        names.put("REDISPLAY", new Short((short)(-13)));
        names.put("KILL_LINE_PREV", new Short((short)(-15)));
        names.put("DELETE_PREV_WORD", new Short((short)(-16)));
        names.put("NEXT_CHAR", new Short((short)(-19)));
        names.put("REPEAT_PREV_CHAR", new Short((short)(-20)));
        names.put("SEARCH_PREV", new Short((short)(-21)));
        names.put("REPEAT_NEXT_CHAR", new Short((short)(-24)));
        names.put("SEARCH_NEXT", new Short((short)(-25)));
        names.put("PREV_SPACE_WORD", new Short((short)(-27)));
        names.put("TO_END_WORD", new Short((short)(-29)));
        names.put("REPEAT_SEARCH_PREV", new Short((short)(-34)));
        names.put("PASTE_PREV", new Short((short)(-36)));
        names.put("REPLACE_MODE", new Short((short)(-37)));
        names.put("SUBSTITUTE_LINE", new Short((short)(-38)));
        names.put("TO_PREV_CHAR", new Short((short)(-39)));
        names.put("NEXT_SPACE_WORD", new Short((short)(-40)));
        names.put("DELETE_PREV_CHAR", new Short((short)(-41)));
        names.put("ADD", new Short((short)(-42)));
        names.put("PREV_WORD", new Short((short)(-43)));
        names.put("CHANGE_META", new Short((short)(-44)));
        names.put("DELETE_META", new Short((short)(-45)));
        names.put("END_WORD", new Short((short)(-46)));
        names.put("NEXT_CHAR", new Short((short)(-19)));
        names.put("INSERT", new Short((short)(-48)));
        names.put("REPEAT_SEARCH_NEXT", new Short((short)(-49)));
        names.put("PASTE_NEXT", new Short((short)(-50)));
        names.put("REPLACE_CHAR", new Short((short)(-51)));
        names.put("SUBSTITUTE_CHAR", new Short((short)(-52)));
        names.put("TO_NEXT_CHAR", new Short((short)(-53)));
        names.put("UNDO", new Short((short)(-54)));
        names.put("NEXT_WORD", new Short((short)(-55)));
        names.put("DELETE_NEXT_CHAR", new Short((short)(-56)));
        names.put("CHANGE_CASE", new Short((short)(-57)));
        names.put("COMPLETE", new Short((short)(-58)));
        names.put("EXIT", new Short((short)(-59)));
        names.put("CLEAR_LINE", new Short((short)(-63)));
        ConsoleReader.KEYMAP_NAMES = new TreeMap(Collections.unmodifiableMap((Map<? extends K, ? extends V>)names));
        NULL_MASK = new Character('\0');
    }
}
