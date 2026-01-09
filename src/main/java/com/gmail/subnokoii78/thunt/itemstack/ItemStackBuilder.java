package com.gmail.subnokoii78.thunt.itemstack;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@NullMarked
public class ItemStackBuilder {
    private final ItemStack itemStack;

    public ItemStackBuilder(Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemStackBuilder() {
        this(Material.AIR);
    }

    private <T extends ItemMeta> ItemStackBuilder editMeta(Class<T> clazz, Consumer<T> consumer) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (clazz.isInstance(meta)) {
            consumer.accept(clazz.cast(meta));
            itemStack.setItemMeta(meta);
        }

        return this;
    }

    private ItemStackBuilder editMeta(Consumer<ItemMeta> consumer) {
        final ItemMeta meta = itemStack.getItemMeta();

        consumer.accept(meta);
        itemStack.setItemMeta(meta);

        return this;
    }

    private <T extends ItemMeta> ItemStackBuilder editMeta(Class<T> clazz, UnaryOperator<T> unaryOperator) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (clazz.isInstance(meta)) {
            itemStack.setItemMeta(unaryOperator.apply(clazz.cast(meta)));
        }

        return this;
    }

    private ItemStackBuilder editMeta(UnaryOperator<ItemMeta> unaryOperator) {
        final ItemMeta meta = itemStack.getItemMeta();

        itemStack.setItemMeta(unaryOperator.apply(meta));

        return this;
    }

    @ApiStatus.Experimental
    private <T> ItemStackBuilder editComponent(DataComponentType.Valued<T> type, Consumer<T> consumer) {
        final T data = itemStack.getData(type);

        consumer.accept(data);

        itemStack.setData(type, data);

        return this;
    }

    public ItemStackBuilder copyWithType(Material material) {
        return ItemStackBuilder.from(itemStack.withType(material));
    }

    public ItemStackBuilder count(int count) {
        itemStack.setAmount(count);
        return this;
    }

    public ItemStackBuilder maxStackSize(int size) {
        return editMeta(meta -> {
            meta.setMaxStackSize(size);
        });
    }

    public ItemStackBuilder itemName(TextComponent name) {
        editMeta(meta -> {
            meta.itemName(name);
        });
        return this;
    }

    public ItemStackBuilder customName(TextComponent name) {
        editMeta(meta -> {
            meta.customName(name);
        });
        return this;
    }

    public ItemStackBuilder lore(TextComponent line) {
        return editMeta(meta -> {
            final List<Component> lore = meta.lore();

            if (lore == null) {
                meta.lore(List.of(line));
            }
            else {
                lore.add(line);
                meta.lore(lore);
            }
        });
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        return editMeta(meta -> {
            meta.addEnchant(enchantment, level, true);
        });
    }

    public ItemStackBuilder hideFlag(ItemFlag flag) {
        return editMeta(meta -> {
            meta.addItemFlags(flag);
        });
    }

    public ItemStackBuilder hideTooltip(boolean flag) {
        return editMeta(meta -> {
            meta.setHideTooltip(flag);
        });
    }

    public ItemStackBuilder maxDamage(int damage) {
        return editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(damage);
        });
    }

    public ItemStackBuilder resetMaxDamage() {
        return editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(null);
        });
    }

    public ItemStackBuilder damage(int damage) {
        return editMeta(Damageable.class, meta -> {
            meta.setDamage(damage);
        });
    }

    public ItemStackBuilder unbreakable(boolean flag) {
        return editMeta(meta -> {
            meta.setUnbreakable(flag);
        });
    }

    public ItemStackBuilder repairCost(int cost) {
        return editMeta(Repairable.class, meta -> {
            meta.setRepairCost(cost);
        });
    }

    public ItemStackBuilder playerProfile(PlayerProfile profile) {
        return editMeta(SkullMeta.class, meta -> {
            meta.setPlayerProfile(profile);
        });
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, NamespacedKey id, double amount, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup, AttributeModifierDisplay display) {
        final AttributeModifier attributeModifier = new AttributeModifier(
            id,
            amount,
            operation,
            slotGroup
        );

        return editMeta(meta -> {
            meta.addAttributeModifier(attribute, attributeModifier);
        });
    }

    public ItemStackBuilder removeAttributeModifier(Attribute attribute) {
        return editMeta(meta -> {
            meta.removeAttributeModifier(attribute);
        });
    }

    public ItemStackBuilder potionEffect(PotionEffect effect) {
        return editMeta(PotionMeta.class, meta -> {
            meta.addCustomEffect(effect, true);
        });
    }

    public ItemStackBuilder potionEffectById(PotionType potionType) {
        return editMeta(PotionMeta.class, meta -> {
            meta.setBasePotionType(potionType);
        });
    }

    public ItemStackBuilder potionColor(Color color) {
        return editMeta(PotionMeta.class, meta -> {
            meta.setColor(color);
        });
    }

    public ItemStackBuilder chargedProjectile(ItemStack itemStack) {
        return editMeta(CrossbowMeta.class, meta -> {
            meta.addChargedProjectile(itemStack);
        });
    }

    public ItemStackBuilder leatherArmorColor(Color color) {
        return editMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(color);
        });
    }

    public ItemStackBuilder removeLeatherArmorColor() {
        return editMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(null);
        });
    }

    public ItemStackBuilder trim(TrimMaterial material, TrimPattern pattern) {
        return editMeta(ColorableArmorMeta.class, meta -> {
            meta.setTrim(new ArmorTrim(material, pattern));
        });
    }

    public ItemStackBuilder bookHeader(String author, String title, BookMeta.Generation generation) {
       return editMeta(BookMeta.class, meta -> {
           meta.setAuthor(author);
           meta.setTitle(title);
           meta.setGeneration(generation);
       });
    }

    public ItemStackBuilder bookPage(Component component) {
        return editMeta(BookMeta.class, meta -> {
            meta.addPages(component);
        });
    }

    public ItemStackBuilder fireworkPower(int power) {
        return editMeta(FireworkMeta.class, meta -> {
            meta.setPower(power);
        });
    }

    public ItemStackBuilder fireworkEffect(FireworkEffect effect) {
        return editMeta(FireworkEffectMeta.class, meta -> {
            meta.setEffect(effect);
        });
    }

    public ItemStackBuilder storedEnchantment(Enchantment enchantment, int level) {
        return editMeta(EnchantmentStorageMeta.class, meta -> {
            meta.addStoredEnchant(enchantment, level, true);
        });
    }

    public ItemStackBuilder glint(boolean flag) {
        editMeta(meta -> {
            meta.setEnchantmentGlintOverride(flag);
        });

        return this;
    }

    public ItemStackBuilder damageResistant(Tag<DamageType> damageTypeTag) {
        return editMeta(meta -> {
            meta.setDamageResistant(damageTypeTag);
        });
    }

    public ItemStackBuilder lodestone(Location target, boolean isLodestoneTracked) {
        return editMeta(CompassMeta.class, meta -> {
            meta.setLodestone(target);
            meta.setLodestoneTracked(isLodestoneTracked);
        });
    }

    public ItemStackBuilder itemModel(NamespacedKey id) {
        return editMeta(meta -> {
            meta.setItemModel(id);
        });
    }

    public ItemStackBuilder hideComponent(DataComponentType.Valued<?> type) {
        return editComponent(DataComponentTypes.TOOLTIP_DISPLAY, d -> {
            d.hiddenComponents().add(type);
        });
    }

    @ApiStatus.Experimental
    public ItemStackBuilder customModelDataFlags(Boolean... flags) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(Arrays.stream(flags).toList());
            meta.setCustomModelDataComponent(component);
        });
    }

    public ItemStackBuilder dataContainer(Consumer<PersistentDataContainer> callback) {
        return editMeta(meta -> {
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            callback.accept(container);
        });
    }

    public @NotNull ItemStackBuilder customData(@NotNull MojangsonPath path, @NotNull Object value) {
        final ItemStackCustomDataAccess access = ItemStackCustomDataAccess.of(itemStack);
        final MojangsonCompound compound = access.read();
        compound.set(path, value);
        access.write(compound);
        return this;
    }

    public ItemStackBuilder copy() {
        return ItemStackBuilder.from(itemStack.clone());
    }

    public ItemStack build() {
        return itemStack.clone();
    }

    public static ItemStackBuilder from(ItemStack itemStack) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.editMeta(meta -> {
            return itemStack.getItemMeta();
        });

        return itemStackBuilder;
    }
}
