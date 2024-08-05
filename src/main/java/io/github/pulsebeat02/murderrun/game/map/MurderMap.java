package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.game.map.part.CarPartManager;

public final class MurderMap {

  private final MurderGame game;
  private CarPartManager carPartManager;
  private GameEventManager eventManager;
  private MurderMapResetManager resetManager;
  private TruckManager truckManager;

  public MurderMap(final MurderGame game) {
    this.game = game;
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public MurderMapResetManager getResetManager() {
    return this.resetManager;
  }

  public void start() {
    this.createManagers();
    this.registerEvents();
    this.spawnParts();
  }

  private void createManagers() {
    this.carPartManager = new CarPartManager(this);
    this.eventManager = new GameEventManager(this);
    this.resetManager = new MurderMapResetManager(this);
    this.truckManager = new TruckManager(this);
  }

  private void registerEvents() {
    this.eventManager.registerEvents();
  }

  private void spawnParts() {
    this.carPartManager.spawnParts();
    this.truckManager.spawnParticles();
  }

  public void shutdown() {
    this.unregisterEvents();
    this.stopExecutors();
    this.resetWorld();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  private void stopExecutors() {
    this.carPartManager.shutdownExecutor();
    this.truckManager.shutdownExecutor();
  }

  private void resetWorld() {
    this.resetManager.resetMap();
  }

  public MurderGame getGame() {
    return this.game;
  }

  public CarPartManager getCarPartManager() {
    return this.carPartManager;
  }

  public TruckManager getTruckManager() {
    return this.truckManager;
  }
}