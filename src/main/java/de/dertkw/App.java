package de.dertkw;

import de.dertkw.refreshrate.RefreshRate;
import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        try {
            // prepare stuff
            File dir = Paths.get(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().toFile();
            if (dir.isFile()) {
                dir = dir.getParentFile();
            }
            File logs = new File(dir, "logs");
            if (!logs.exists()) {
                logs.mkdir();
            }
            String level = Arrays.asList(args).contains("debug") ? "debug" : "info";
            String format = "{date: HH:mm:ss.SSS} {class}.{method}() {level}: {message}";

            // configure logger
            Configuration.set("writer1", "console");
            Configuration.set("writer1.level", level);
            Configuration.set("writer1.format", format);
            Configuration.set("writer2", "file");
            Configuration.set("writer2.level", level);
            Configuration.set("writer2.file", new File(logs, "refreshrate-" + System.currentTimeMillis() + ".log").getAbsolutePath());
            Configuration.set("writer2.format", format);

            // launch app
            final RefreshRate app = new RefreshRate();

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