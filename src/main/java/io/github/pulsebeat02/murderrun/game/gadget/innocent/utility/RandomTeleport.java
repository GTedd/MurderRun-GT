package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class RandomTeleport extends MurderGadget {

  public RandomTeleport() {
    super(
        "random_teleport",
        Material.GOLDEN_CARROT,
        Locale.TP_ME_AWAY_FROM_HERE_TRAP_NAME.build(),
        Locale.TP_ME_AWAY_FROM_HERE_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Location temp = new Location(world, coords[0], 0, coords[1]);
    final Block block = world.getHighestBlockAt(temp);
    final Location top = block.getLocation();
    player.teleport(top);
  }
}