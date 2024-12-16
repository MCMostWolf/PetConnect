package com.wolf.petconnect;

import com.wolf.petconnect.config.ConfigCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

import static com.wolf.petconnect.PetConnectEventHandler.havePetsNumber;

@Mod(PetConnect.MODID)
public class PetConnect {
    public static final String MODID = "petconnect";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PetConnect.MODID);
    public static final Lazy<KeyMapping> EXAMPLE_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.petconnect.toggle", // 名称
            GLFW.GLFW_KEY_B,         // 默认键位 (小写 'b')
            "key.categories.misc"
    ));

    public PetConnect() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PetConnect.ITEMS.register(eventBus);
        PetConnect.CREATIVE_MODE_TABS.register(eventBus);
        KeyMapping.set(EXAMPLE_MAPPING.get().getKey(), true);
        MinecraftForge.EVENT_BUS.register(new PetConnectEventHandler());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.COMMON);
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, EXAMPLE_MAPPING.get());
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
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect3").append(EXAMPLE_MAPPING.get().getKey().getDisplayName()));
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
            tooltip.add(Component.translatable("tooltip.petconnect.pet_connect3").append(EXAMPLE_MAPPING.get().getKey().getDisplayName()));
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


}