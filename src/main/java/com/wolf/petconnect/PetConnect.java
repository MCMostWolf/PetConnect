package com.wolf.petconnect;

import com.wolf.petconnect.config.ConfigCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;

import static com.wolf.petconnect.PetConnect.ClientInit.EXAMPLE_MAPPING;


@Mod(PetConnect.MODID)
public class PetConnect {
    public static final String MODID = "petconnect";
    private static final Random random = new Random();
    private static final List<ItemLike> EXCLUDED_ITEMS = List.of(
            Items.AIR, Items.BARRIER, Items.COMMAND_BLOCK,
            Items.STRUCTURE_BLOCK, Items.JIGSAW, Items.DEBUG_STICK
    );
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PetConnect.MODID);
    public PetConnect() {
        ModMessages.register();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PetConnect.ITEMS.register(eventBus);
        PetConnect.CREATIVE_MODE_TABS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(new PetConnectEventHandler());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.COMMON);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    }
    private void setup(final FMLClientSetupEvent event) {
        // 在setup阶段执行的代码
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, EXAMPLE_MAPPING.get());
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getRecipeManager();
        List<Recipe<?>> allRecipes = recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == RecipeType.CRAFTING)
                .toList();
    }
    public static final RegistryObject<Item> PET_CONNECT = ITEMS.register("pet_connect", () -> new Item(new Item.Properties().stacksTo(1)) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect", ConfigCommon.CONNECT_DISTANCE.get().floatValue()));
            if (stack.getTag() != null && stack.getTag().contains("havePetsNumber")) {
                tooltip.add(Component.translatable("tooltip.petconnect.pet_connect1", stack.getTag().getInt("havePetsNumber"), ConfigCommon.LIMIT_PETS_NUMBER.get()));
            }
            else {
                tooltip.add(Component.translatable("tooltip.petconnect.pet_connect1", 0, ConfigCommon.LIMIT_PETS_NUMBER.get()));
            }
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect3").append(ClientInit.EXAMPLE_MAPPING.get().getKey().getDisplayName()));
        }
        @Override
        public @NotNull Component getName(@NotNull ItemStack stack){
            return Component.translatable(super.getName(stack).getString()).withStyle(ChatFormatting.GOLD);
        }
    } );
    public static final RegistryObject<Item> PET_CONNECT1 = ITEMS.register("pet_connect1", () -> new Item(new Item.Properties().stacksTo(1)) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect2"));
            if (stack.getTag() != null && stack.getTag().contains("havePetsNumber")) {
                tooltip.add(Component.translatable("tooltip.petconnect.pet_connect1", stack.getTag().getInt("havePetsNumber"), ConfigCommon.LIMIT_PETS_NUMBER.get()));
            }
            else {
                tooltip.add(Component.translatable("tooltip.petconnect.pet_connect1", 0, ConfigCommon.LIMIT_PETS_NUMBER.get()));
            }
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect3").append(ClientInit.EXAMPLE_MAPPING.get().getKey().getDisplayName()));
        }
        @Override
        public @NotNull Component getName(@NotNull ItemStack stack){
            return Component.translatable(super.getName(stack).getString()).withStyle(ChatFormatting.GOLD);
        }
    } );
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "petconnect");

    public static final RegistryObject<CreativeModeTab> PET_CONNECT_TAB  = CREATIVE_MODE_TABS.register("pet_connect", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(PetConnect.PET_CONNECT.get())) // 设置物品栏图标
                    .title(Component.translatable("itemGroup.pet_connect_tab")) // 设置物品栏标题
                    .displayItems((pParameters, pOutput) -> {
                        // 添加物品到物品栏
                        pOutput.accept(PetConnect.PET_CONNECT.get());
                    })
                    .build());
    public static class ClientInit {
        public static final Lazy<KeyMapping> EXAMPLE_MAPPING = Lazy.of(() -> new KeyMapping(
                "key.petconnect.toggle",
                GLFW.GLFW_KEY_B,
                "key.categories.misc"
        ));

        public static void init() {
            // 添加键映射到 Minecraft 实例
            var options = Minecraft.getInstance().options;
            options.keyMappings = ArrayUtils.add(options.keyMappings, EXAMPLE_MAPPING.get());
        }
    }
}