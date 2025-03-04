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
package io.github.pulsebeat02.murderrun.utils.item;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemFactory {

  private ItemFactory() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ItemStack createItemLocationWand() {
    return Item.builder(Material.BLAZE_ROD)
      .name(Message.ITEM_ARENA_NAME.build())
      .lore(Message.ITEM_ARENA_LORE.build())
      .pdc(Keys.ITEM_WAND, PersistentDataType.BOOLEAN, true)
      .model(1)
      .build();
  }

  public static ItemStack[] createKillerGear() {
    final ItemStack head = Item.builder(Material.WITHER_SKELETON_SKULL).model(1).build();
    final ItemStack chest = Item.builder(Material.LEATHER_CHESTPLATE)
      .dye(Color.RED)
      .enchantment(Enchantment.PROTECTION, 3)
      .model(1)
      .build();
    final ItemStack legs = Item.builder(Material.LEATHER_LEGGINGS).dye(Color.RED).enchantment(Enchantment.PROTECTION, 3).model(1).build();
    final ItemStack boots = Item.builder(Material.LEATHER_BOOTS).dye(Color.RED).enchantment(Enchantment.PROTECTION, 3).model(1).build();
    return new ItemStack[] { boots, legs, chest, head };
  }

  public static ItemStack createPlayerTracker(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.PLAYER_TRACKER, PersistentDataType.INTEGER, 0).build();
  }

  public static ItemStack createTranslocator(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, new byte[0]).build();
  }

  public static ItemStack createKillerTracker(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.KILLER_TRACKER, PersistentDataType.INTEGER, 0).build();
  }

  public static ItemStack createSmokeGrenade(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN, true).model(2).build();
  }

  public static ItemStack createFlashlight(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.FLASHLIGHT_USE, PersistentDataType.LONG, 0L).build();
  }

  public static ItemStack createFlashBang(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true).model(1).build();
  }

  public static ItemStack createSurvivorGear(final ItemStack stack) {
    return Item.builder(stack).enchantment(Enchantment.PROTECTION, 3).build();
  }

  public static ItemStack createPortalGun(final ItemStack stack) {
    final UUID uuid = UUID.randomUUID();
    final String data = uuid.toString();
    return Item.builder(stack)
      .pdc(Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, true)
      .pdc(Keys.UUID, PersistentDataType.STRING, data)
      .enchantment(Enchantment.INFINITY, 1)
      .unbreakable()
      .build();
  }

  public static ItemStack createHook(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.HOOK, PersistentDataType.BOOLEAN, true).unbreakable().build();
  }

  public static ItemStack createSpeedPendant(final ItemStack stack) {
    return Item.builder(stack).modifier(Attribute.MOVEMENT_SPEED, 0.03).build();
  }

  public static ItemStack createRedArrow(final ItemStack stack) {
    return Item.builder(stack).potionColor(Color.RED).build();
  }

  public static ItemStack createMedKit(final ItemStack stack) {
    return Item.builder(stack).potionColor(Color.RED).potion(PotionType.STRONG_HEALING).build();
  }

  public static ItemStack createGadget(
    final String pdcName,
    final Material material,
    final Component itemName,
    final Component itemLore,
    final @Nullable Consumer<ItemStack> consumer
  ) {
    return Item.builder(requireNonNull(material))
      .name(requireNonNull(itemName))
      .lore(requireNonNull(itemLore))
      .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, requireNonNull(pdcName))
      .consume(consumer)
      .hideAttributes()
      .build();
  }

  public static ItemStack createSaddle() {
    return Item.builder(Material.SADDLE).build();
  }

  public static ItemStack createShield(final ItemStack stack) {
    return Item.builder(stack).durability(5).build();
  }

  public static ItemStack createExcavator(final ItemStack stack) {
    return Item.builder(stack).pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true).durability(10).build();
  }

  public static ItemStack createDeathGear(final Material armor) {
    return Item.builder(armor).dye(Color.RED).build();
  }

  public static ItemStack createPlayerHead(final Player player) {
    return Item.builder(Material.PLAYER_HEAD).head(player).build();
  }

  public static ItemStack createGhostGear(final Material armor) {
    return Item.builder(armor).dye(Color.WHITE).build();
  }

  public static ItemStack createKnockBackBone() {
    return Item.builder(Material.BONE).enchantment(Enchantment.KNOCKBACK, 0).build();
  }

  public static ItemStack createFakePart() {
    return Item.builder(Material.DIAMOND).model(RandomUtils.generateInt(1, 6)).build();
  }

  public static ItemStack createCursedNote() {
    return Item.builder(Material.PAPER).name(Message.CURSED_NOTE_NAME.build()).build();
  }

  public static ItemStack createCarPart(final String uuid) {
    return Item.builder(Material.DIAMOND)
      .name(Message.CAR_PART_ITEM_NAME.build())
      .lore(Message.CAR_PART_ITEM_LORE.build())
      .model(RandomUtils.generateInt(1, 6))
      .pdc(Keys.CAR_PART_UUID, PersistentDataType.STRING, uuid)
      .build();
  }

  public static ItemStack createCurrency(final int amount) {
    return Item.builder(Material.NETHER_STAR).name(Message.MINEBUCKS.build()).amount(amount).model(1).build();
  }

  public static ItemStack createKillerArrow() {
    return Item.builder(Material.ARROW)
      .name(Message.ARROW_NAME.build())
      .lore(Message.ARROW_LORE.build())
      .enchantment(Enchantment.VANISHING_CURSE, 1)
      .hideAttributes()
      .build();
  }

  public static ItemStack createKillerSword() {
    return Item.builder(Material.DIAMOND_SWORD)
      .name(Message.KILLER_SWORD.build())
      .model(1)
      .hideAttributes()
      .modifier(Attribute.ATTACK_DAMAGE, 8)
      .pdc(Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true)
      .pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true)
      .enchantment(Enchantment.VANISHING_CURSE, 1)
      .unbreakable()
      .build();
  }
}
