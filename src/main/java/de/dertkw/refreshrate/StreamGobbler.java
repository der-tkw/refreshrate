package de.dertkw.refreshrate;

import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamGobbler extends Thread {
    private final InputStream is;
    private final String type;
    private final List<String> output = new ArrayList<>();

    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public List<String> getOutput() {
        return output;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                output.add(line);
                Logger.debug(type + ": " + line);
            }
        } catch (IOException ioe) {
            Logger.error(ioe);
        }
    }
}
