// 
// Decompiled by Procyon v0.5.36
// 

package antlr.build;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

class StreamScarfer extends Thread
{
    InputStream is;
    String type;
    Tool tool;
    
    StreamScarfer(final InputStream is, final String type, final Tool tool) {
        this.is = is;
        this.type = type;
        this.tool = tool;
    }
    
    public void run() {
        try {
            String line;
            while ((line = new BufferedReader(new InputStreamReader(this.is)).readLine()) != null) {
                if (this.type == null || this.type.equals("stdout")) {
                    this.tool.stdout(line);
                }
                else {
                    this.tool.stderr(line);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
