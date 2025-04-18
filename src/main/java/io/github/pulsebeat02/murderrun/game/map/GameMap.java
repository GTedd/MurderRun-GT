/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;

public final class GameMap {

  private final Game game;

  private PartsManager partsManager;
  private GameEventManager eventManager;
  private TruckManager truckManager;
  private BlockWhitelistManager blockWhitelistManager;

  public GameMap(final Game game) {
    this.game = game;
  }

  public void start() {
    this.partsManager = new PartsManager(this);
    this.eventManager = new GameEventManager(this);
    this.truckManager = new TruckManager(this);
    this.blockWhitelistManager = new BlockWhitelistManager();
    this.eventManager.registerEvents();
    this.truckManager.spawnParticles();
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public void shutdown() {
    this.unregisterEvents();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  public Game getGame() {
    return this.game;
  }

  public PartsManager getCarPartManager() {
    return this.partsManager;
  }

  public TruckManager getTruckManager() {
    return this.truckManager;
  }

  public BlockWhitelistManager getBlockWhitelistManager() {
    return this.blockWhitelistManager;
  }
}
