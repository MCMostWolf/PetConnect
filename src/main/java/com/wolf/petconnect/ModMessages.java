package com.wolf.petconnect;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import static com.wolf.petconnect.PetConnect.MODID;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    // 移除静态初始化器，改为通过 register 方法初始化
    public static void register() {
        if (INSTANCE == null) {
            INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "messages"))
                    .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                    .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                    .networkProtocolVersion(() -> PROTOCOL_VERSION)
                    .simpleChannel();

            int id = 0;
            INSTANCE.messageBuilder(PetConnectMessage.class, id++)
                    .encoder(PetConnectMessage::encode)
                    .decoder(PetConnectMessage::decode)
                    .consumerNetworkThread(PetConnectMessage::handle)
                    .add();
        }
    }
}