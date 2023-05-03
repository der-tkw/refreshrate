package de.dertkw;

import de.dertkw.refreshrate.RefreshRate;
import de.dertkw.refreshrate.Utils;
import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        try {
            boolean debug = Arrays.asList(args).contains("debug");
            String level = debug ? "debug" : "info";
            String format = "{date: HH:mm:ss.SSS} {class}.{method}() {level}: {message}";

            // configure logger
            Configuration.set("writer1", "console");
            Configuration.set("writer1.level", level);
            Configuration.set("writer1.format", format);
            Configuration.set("writer2", "file");
            Configuration.set("writer2.level", level);
            Configuration.set("writer2.file", Files.createTempFile("refreshrate-", ".log").toAbsolutePath().toString());
            Configuration.set("writer2.format", format);

            // ensure that CSR path is set
            if (Utils.getCSRPath() == null) {
                do {
                    Utils.triggerCSRPath();
                } while (Utils.getCSRPath() == null);
            }

            // launch app
            final RefreshRate app = new RefreshRate(debug);

            // update now and every 5 minutes
            Runnable runnable = app::update;
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            exec.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            Logger.error(e);
            System.exit(-1);
        }
    }
}