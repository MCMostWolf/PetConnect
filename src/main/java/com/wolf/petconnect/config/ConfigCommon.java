package com.wolf.petconnect.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCommon {
    public static ForgeConfigSpec COMMON;
    public static ForgeConfigSpec.IntValue LIMIT_PETS_NUMBER;
    public static ForgeConfigSpec.DoubleValue CONNECT_DISTANCE;
    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CONFIG_BUILDER.comment("配置文件");
        CONFIG_BUILDER.push("petconnect");
        LIMIT_PETS_NUMBER = CONFIG_BUILDER.comment("限制宠物数量，默认为10")
                .defineInRange("limit_pets_number", 10, 0, Integer.MAX_VALUE);
        CONNECT_DISTANCE = CONFIG_BUILDER.comment("连接距离，默认为10")
                .defineInRange("connect_distance", 10.0, 0.0, Double.MAX_VALUE);
        CONFIG_BUILDER.pop();
        COMMON = CONFIG_BUILDER.build();
    }
}
