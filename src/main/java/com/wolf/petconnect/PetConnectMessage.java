package com.wolf.petconnect;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PetConnectMessage {
    private final int slot;
    private final ItemStack newItemStack;

    public PetConnectMessage(int slot, ItemStack newItemStack) {
        this.slot = slot;
        this.newItemStack = newItemStack;
    }

    public static void encode(PetConnectMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.slot);
        buffer.writeItem(msg.newItemStack);
    }

    public static PetConnectMessage decode(FriendlyByteBuf buffer) {
        return new PetConnectMessage(buffer.readInt(), buffer.readItem());
    }

    public static void handle(PetConnectMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getInventory().setItem(msg.slot, msg.newItemStack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}