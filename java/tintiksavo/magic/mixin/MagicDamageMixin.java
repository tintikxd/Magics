package tintiksavo.magic.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tintiksavo.magic.item.MagicArmorItem;

@Mixin(LivingEntity.class)
public abstract class MagicDamageMixin {
    private static final TagKey<EntityType<?>> MAGIC_ENTITIES =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("magic", "magic"));

    /**
     * Magic-tagged attackers use only 40% of the target's armor value.
     * If the target wears at least one piece of Magic armor, the final
     * post-armor damage is reduced by a further 20%.
     */
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void magic$adjustDamageFromMagicEntities(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Float> cir
    ) {
        LivingEntity target = (LivingEntity) (Object) this;
        if (source.getEntity() == null || !source.getEntity().getType().is(MAGIC_ENTITIES)) {
            return;
        }

        float adjustedDamage = amount;

        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            float armor = (float) target.getArmorValue();
            float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

            // 60% armor penetration: only 40% of the armor value is used.
            adjustedDamage = CombatRules.getDamageAfterAbsorb(amount, armor * 0.4F, toughness);
        }

        if (target instanceof Player player && wearsMagicArmor(player)) {
            adjustedDamage *= 0.8F;
        }

        cir.setReturnValue(adjustedDamage);
    }

    /**
     * Magic-tagged attackers reduce the effective protection-enchantment value by 25%.
     * For example, Protection IV's contribution is treated as 75% of its normal value.
     */
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private void magic$penetrateProtectionEnchantments(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Float> cir
    ) {
        if (source.getEntity() == null || !source.getEntity().getType().is(MAGIC_ENTITIES)) {
            return;
        }

        // Preserve vanilla behavior for damage that bypasses effects/enchantments.
        if (source.is(DamageTypeTags.BYPASSES_EFFECTS)
                || source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            cir.setReturnValue(amount);
            return;
        }

        LivingEntity target = (LivingEntity) (Object) this;
        int protection = EnchantmentHelper.getDamageProtection((Iterable<ItemStack>) target, source);
        if (protection > 0) {
            float effectiveProtection = protection * 0.75F;
            amount = CombatRules.getDamageAfterMagicAbsorb(amount, effectiveProtection);
        }
        cir.setReturnValue(amount);
    }

    private static boolean wearsMagicArmor(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof MagicArmorItem) {
                return true;
            }
        }
        return false;
    }
}
