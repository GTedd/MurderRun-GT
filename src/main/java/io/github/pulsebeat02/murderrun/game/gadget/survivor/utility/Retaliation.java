package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends SurvivorGadget {

  private static final int MAX_DEATHS_COUNTED = 3;

  public Retaliation() {
    super(
        "retaliation",
        Material.GOLD_BLOCK,
        Locale.RETALIATION_TRAP_NAME.build(),
        Locale.RETALIATION_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component message = Locale.RETALIATION_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.checkForDeadPlayers(manager, player), 80L);
  }

  private void checkForDeadPlayers(final PlayerManager manager, final Player player) {
    final Collection<GamePlayer> deathCount = manager.getDead();
    final int dead = deathCount.size();
    final int effectLevel = Math.min(dead, MAX_DEATHS_COUNTED);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, effectLevel));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, effectLevel));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, effectLevel));
  }
}