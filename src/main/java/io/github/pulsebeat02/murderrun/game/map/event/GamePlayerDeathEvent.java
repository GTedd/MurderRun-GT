package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderWinCode;
import io.github.pulsebeat02.murderrun.game.gadget.DeathTask;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Innocent;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class GamePlayerDeathEvent implements Listener {

  private final MurderGame game;

  public GamePlayerDeathEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {

    final Player player = event.getEntity();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidEventPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = optional.get();
    if (this.checkDeathCancellation(gamePlayer)) {
      return;
    }

    final MurderPlayerManager manager = this.game.getPlayerManager();
    final PlayerDeathManager death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);
    event.setKeepInventory(false);

    this.playDeathSoundEffect();
    this.runDeathTasks(gamePlayer);

    if (this.allInnocentDead()) {
      this.game.finishGame(MurderWinCode.MURDERERS);
    }
  }

  private void runDeathTasks(final GamePlayer player) {
    final Collection<DeathTask> tasks = player.getDeathTasks();
    final Iterator<DeathTask> iterator = tasks.iterator();
    while (iterator.hasNext()) {
      final DeathTask task = iterator.next();
      task.run();
      iterator.remove();
    }
  }

  private boolean checkDeathCancellation(final GamePlayer player) {
    final Collection<DeathTask> tasks = player.getDeathTasks();
    boolean cancel;
    final Iterator<DeathTask> iterator = tasks.iterator();
    while (iterator.hasNext()) {
      final DeathTask task = iterator.next();
      cancel = task.isCancelDeath();
      if (cancel) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  private void playDeathSoundEffect() {
    AdventureUtils.playSoundForAllParticipants(this.game, FXSound.DEATH);
  }

  private boolean allInnocentDead() {
    final MurderPlayerManager manager = this.game.getPlayerManager();
    final Collection<Innocent> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }
}