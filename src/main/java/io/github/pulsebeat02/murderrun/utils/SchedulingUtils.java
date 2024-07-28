package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicLong;

public final class SchedulingUtils {

    private static final Plugin PLUGIN;

    static {
        final PluginManager manager = Bukkit.getPluginManager();
        final Plugin plugin = manager.getPlugin("MurderRun");
        if (plugin == null) {
            throw new AssertionError("Failed to retrieve plugin class!");
        }
        PLUGIN = plugin;
    }

    private SchedulingUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void scheduleTask(final Runnable runnable, final long delay) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(PLUGIN, runnable, delay);
    }

    public static void scheduleRepeatingTaskDuration(final Runnable runnable, final long delay, final long period, final long duration) {

        final class CustomRunnable extends BukkitRunnable {

            final AtomicLong time = new AtomicLong(duration);

            @Override
            public void run() {
                runnable.run();
                final long raw = time.decrementAndGet();
                if (raw <= 0) {
                    this.cancel();
                }
            }
        }

        CustomRunnable custom = new CustomRunnable();
        custom.runTaskTimer(PLUGIN, delay, period);

    }
}