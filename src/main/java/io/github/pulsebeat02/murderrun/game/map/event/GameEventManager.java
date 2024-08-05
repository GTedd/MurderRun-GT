package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.map.MurderMap;
import java.util.Collection;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public final class GameEventManager {

  private final MurderGame game;
  private final Collection<Listener> events;

  public GameEventManager(final MurderMap map) {
    this.game = map.getGame();
    this.events = Set.of(
        new GamePlayerDeathEvent(this.game),
        new GamePlayerPickupCarPartEvent(this.game),
        new GamePlayerThrowCarPartEvent(this.game),
        new GamePlayerLeaveEvent(this.game),
        new GamePlayerHungerEvent(this.game),
        new GamePlayerBlockDropEvent(this.game),
        new GamePlayerRegenEvent(this.game));
  }

  public MurderGame getGame() {
    return this.game;
  }

  public Collection<Listener> getEvents() {
    return this.events;
  }

  public void registerEvents() {
    final MurderRun plugin = this.game.getPlugin();
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    for (final Listener listener : this.events) {
      manager.registerEvents(listener, plugin);
    }
  }

  public void unregisterEvents() {
    for (final Listener listener : this.events) {
      HandlerList.unregisterAll(listener);
    }
  }
}