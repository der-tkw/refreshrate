package de.timbooo;

import de.timbooo.refreshrate.RefreshRate;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        final RefreshRate app = new RefreshRate();
        try {
            app.start();
        } catch (Exception e) {
            try {
                e.printStackTrace(new PrintStream(File.createTempFile("refreshrate-", ".crashlog")));
            } catch (IOException io) {
                // print and give up
                io.printStackTrace();
            }
            System.exit(-1);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                app.refresh();
            }
        };

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(runnable , 0, 5, TimeUnit.MINUTES);
    }
}