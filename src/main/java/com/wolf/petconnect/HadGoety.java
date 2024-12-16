package com.wolf.petconnect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class HadGoety {
    public static void hadGoety(List<LivingEntity> livingEntity, ServerLevel targetWorld, AABB searchArea, Player player) {
        livingEntity.addAll(targetWorld.getEntitiesOfClass(com.Polarice3.Goety.common.entities.neutral.Owned.class, searchArea)
                .stream()
                .filter(tamable -> Objects.equals(tamable.getOwnerUUID(), player.getUUID()))
                .toList());
    }
}
