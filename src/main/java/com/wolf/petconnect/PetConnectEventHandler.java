package com.wolf.petconnect;

import com.mojang.brigadier.CommandDispatcher;
import com.wolf.petconnect.config.ConfigCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.wolf.petconnect.PetConnect.*;

@Mod.EventBusSubscriber(modid = PetConnect.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PetConnectEventHandler {
    public static int havePetsNumber = 0;
    public static final ResourceKey<Level> MY_CUSTOM_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("petconnect:petconnect"));
    public static List<LivingEntity> findTamedPets(Player player, double range, BlockPos blockPos) {
        Level level = player.level();
        // 定义搜索区域
        AABB searchArea = new AABB(blockPos).inflate(range);
        // 获取并筛选驯养的宠物
        List<LivingEntity> livingEntities = new java.util.ArrayList<>(level.getEntitiesOfClass(TamableAnimal.class, searchArea)
                .stream()
                .filter(tamable -> tamable.isTame() && Objects.equals(tamable.getOwnerUUID(), player.getUUID()))
                .toList());
        if (ModList.get().isLoaded("goety")) {
            HadGoety.hadGoety(livingEntities, (ServerLevel) level, searchArea, player);
        }
        return livingEntities;
    }

    public static List<LivingEntity> findTamedPets1(Player player, double range) {
        ServerLevel targetWorld = Objects.requireNonNull(player.getServer()).getLevel(MY_CUSTOM_DIMENSION);
        BlockPos blockPos = new BlockPos(0, -510, 0);
        // 定义搜索区域
        AABB searchArea = new AABB(blockPos).inflate(range);
        // 获取并筛选驯养的宠物
        List<LivingEntity> livingEntity = new java.util.ArrayList<>(targetWorld.getEntitiesOfClass(TamableAnimal.class, searchArea)
                .stream()
                .filter(tamable -> tamable.isTame() && tamable.getOwnerUUID().equals(player.getUUID()))
                .toList());
        if (ModList.get().isLoaded("goety")) {
            HadGoety.hadGoety(livingEntity, targetWorld, searchArea, player);
        }
        return livingEntity;
    }

    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && player.getMainHandItem().is(PET_CONNECT.get())) {
            ItemStack itemstack = player.getMainHandItem();
            List<LivingEntity> tamedPets = findTamedPets(serverPlayer, ConfigCommon.CONNECT_DISTANCE.get(), serverPlayer.blockPosition());
            List<LivingEntity> existingPetsInTargetWorld = findTamedPets1(serverPlayer, 10.0);
            int petLimit = ConfigCommon.LIMIT_PETS_NUMBER.get();
            if (!tamedPets.isEmpty()) {
                    int remainingSlots = petLimit - existingPetsInTargetWorld.size();
                    if (remainingSlots > 0) {
                    for (LivingEntity pet : tamedPets) {
                        if (remainingSlots <= 0) {
                            player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc3").withStyle(ChatFormatting.RED), true);
                            break;
                        }
                        pet.level().addParticle(ParticleTypes.PORTAL, pet.getX(), pet.getY(), pet.getZ(), 1, 1, 1);
                        if (pet.level() instanceof ServerLevel) {
                            ServerLevel targetWorld = player.getServer().getLevel(MY_CUSTOM_DIMENSION);
                            if (targetWorld != null) {
                                try {
                                    float health = pet.getHealth();
                                    float yaw = player.getYRot(); // 使用玩家当前的偏航角
                                    float pitch = player.getXRot(); // 使用玩家当前的俯仰角
                                    Set<RelativeMovement> relativeMovements = EnumSet.noneOf(RelativeMovement.class); // 或者根据需要设置
                                    pet.setNoGravity(true);
                                    CompoundTag tag = pet.saveWithoutId(new CompoundTag());
                                    tag.putBoolean("NoAI", true);
                                    pet.load(tag);
                                    CommandDispatcher<CommandSourceStack> dispatcher = serverPlayer.getServer().getCommands().getDispatcher();
                                    CommandSourceStack source = serverPlayer.getServer().createCommandSourceStack();
                                    dispatcher.execute("execute in " + player.level().dimension().location() + " run particle minecraft:portal " + pet.getBlockX() + " " + pet.getBlockY() + " " + pet.getBlockZ() + " 0.12 0.3 0.12 0.2 80 force", source);
                                    pet.teleportTo(targetWorld, 0, -510, 0, relativeMovements, yaw, pitch);
                                    pet.setHealth(health);
                                    player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc").withStyle(ChatFormatting.GREEN), true);
                                    remainingSlots --;
                                    havePetsNumber ++;
                                    if (itemstack.getTag() != null && itemstack.getTag().contains("havePetsNumber")) {
                                        itemstack.getOrCreateTag().putInt("havePetsNumber", 1 + itemstack.getTag().getInt("havePetsNumber"));
                                    }
                                    else {
                                        itemstack.getOrCreateTag().putInt("havePetsNumber", 1);
                                    }

                                } catch (Exception e) {
                                    player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc1").withStyle(ChatFormatting.DARK_RED), true);
                                }
                            } else {
                                player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc2").withStyle(ChatFormatting.DARK_RED), true);
                            }
                        }
                    }
                    }
                    else {
                        player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc4").withStyle(ChatFormatting.RED), true);
                    }
                    event.setCanceled(true); // 取消默认行为

            }
            else {
                player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc5").withStyle(ChatFormatting.RED), true);
            }
        }
        else if (player instanceof ServerPlayer serverPlayer && player.getMainHandItem().is(PET_CONNECT1.get())) {
            ItemStack heldItem = player.getMainHandItem();
            List<LivingEntity> tamedPets = findTamedPets1(serverPlayer, 10.0);
            if (!tamedPets.isEmpty()) {
                for (LivingEntity pet : tamedPets) {
                    if (pet.level() instanceof ServerLevel) {
                        ServerLevel targetWorld = player.getServer().getLevel(MY_CUSTOM_DIMENSION);
                        if (targetWorld != null) {
                            try {
                                float health = pet.getHealth();
                                float yaw = player.getYRot(); // 使用玩家当前的偏航角
                                float pitch = player.getXRot(); // 使用玩家当前的俯仰角
                                Set<RelativeMovement> relativeMovements = EnumSet.noneOf(RelativeMovement.class); // 或者根据需要设置
                                pet.setNoGravity(false);
                                CompoundTag tag = pet.saveWithoutId(new CompoundTag());
                                tag.putBoolean("NoAI", false);
                                pet.load(tag);
                                pet.teleportTo(((ServerPlayer) player).serverLevel(), player.getX(), player.getY(), player.getZ(), relativeMovements, yaw, pitch);
                                pet.setHealth(health);
                                player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc6").withStyle(ChatFormatting.GREEN), true);
                                havePetsNumber = 0;
                                if (heldItem.getTag() != null && heldItem.getTag().contains("havePetsNumber")) {
                                    heldItem.removeTagKey("havePetsNumber");
                                }
                            } catch (Exception e) {
                                player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc1").withStyle(ChatFormatting.DARK_RED), true);
                            }
                        }
                        else {
                            player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc2").withStyle(ChatFormatting.DARK_RED), true);
                        }
                    }
                }
                event.setCanceled(true); // 取消默认行为
            }
            else {
                player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc7").withStyle(ChatFormatting.DARK_RED), true);
            }
        }
    }
    @SubscribeEvent
    public static void onPetInPetWorld(LivingHurtEvent event) {
        ServerLevel targetWorld = event.getEntity().getServer().getLevel(MY_CUSTOM_DIMENSION);
        if (event.getEntity().level() == targetWorld) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
            if (EXAMPLE_MAPPING.get().consumeClick()) {
                Player player = event.player;
                if (player instanceof ServerPlayer serverPlayer && player.getMainHandItem().is(PET_CONNECT.get())) {
                    player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc9").withStyle(ChatFormatting.GOLD), true);
                    serverPlayer.getSlot(player.getInventory().selected).set(new ItemStack(PET_CONNECT1.get()));
                }
                else if (player instanceof ServerPlayer serverPlayer && player.getMainHandItem().is(PET_CONNECT1.get())) {
                    player.displayClientMessage(Component.translatable("item.petconnect.pet_connect.desc8").withStyle(ChatFormatting.GOLD), true);
                    serverPlayer.getSlot(player.getInventory().selected).set(new ItemStack(PET_CONNECT.get()));
                }
            }
    }
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ChunkPos chunkPos = new ChunkPos(0,0);
        ServerLevel serverLevel = event.getEntity().getServer().getLevel(MY_CUSTOM_DIMENSION);
        serverLevel.getChunkSource().addRegionTicket(TicketType.FORCED, chunkPos, 0, chunkPos);
    }
    @SubscribeEvent
    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(EXAMPLE_MAPPING.get());
    }
}