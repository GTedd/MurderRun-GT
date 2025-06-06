/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.EntityReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class FakePart extends KillerGadget {

  public FakePart() {
    super(
      "fake_part",
      GameProperties.FAKE_PART_COST,
      ItemFactory.createGadget(
        "fake_part",
        GameProperties.FAKE_PART_MATERIAL,
        Message.FAKE_PART_NAME.build(),
        Message.FAKE_PART_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final Item fakeItem = this.spawnItem(location);

    final GameScheduler scheduler = game.getScheduler();
    final GamePlayerManager manager = game.getPlayerManager();
    final EntityReference reference = EntityReference.of(fakeItem);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticleOnPart(fakeItem), 0, 2, reference);

    final Runnable task = () -> this.handlePlayers(scheduler, manager, player, fakeItem);
    scheduler.scheduleRepeatedTask(task, 0, 20L, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.FAKE_PART_SOUND);

    return false;
  }

  private void handlePlayers(final GameScheduler scheduler, final GamePlayerManager manager, final GamePlayer killer, final Item item) {
    manager.applyToLivingSurvivors(survivor -> this.checkNear(scheduler, survivor, killer, item));
  }

  private void checkNear(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer, final Item item) {
    final Location origin = item.getLocation();
    final Location location = survivor.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.FAKE_PART_RADIUS;
    if (distance < radius * radius) {
      this.handleDebuff(scheduler, survivor, killer, item);
      final PlayerAudience audience = survivor.getAudience();
      final Component msg = Message.FAKE_PART_ACTIVATE.build();
      audience.sendMessage(msg);
      audience.playSound(GameProperties.FAKE_PART_EFFECT_SOUND);
    }
  }

  private void handleDebuff(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer, final Item item) {
    final int duration = GameProperties.FAKE_PART_DURATION;
    survivor.disableJump(scheduler, duration);
    survivor.disableWalkWithFOVEffects(duration);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1));
    item.remove();

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, duration);
  }

  private Item spawnItem(final Location location) {
    final ItemStack fake = ItemFactory.createFakePart();
    final World world = requireNonNull(location.getWorld());
    final Item item = world.dropItem(location, fake);
    item.setPickupDelay(Integer.MAX_VALUE);
    item.setUnlimitedLifetime(true);
    return item;
  }

  private void spawnParticleOnPart(final Item item) {
    final Location location = item.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 4, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 1));
  }
}
