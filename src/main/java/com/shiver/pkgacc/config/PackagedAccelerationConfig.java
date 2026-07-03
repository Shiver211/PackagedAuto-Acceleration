package com.shiver.pkgacc.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class PackagedAccelerationConfig {

    public static int maxSpeedCards = 20;
    public static double speedPerCard = 0.5D;
    public static int maxEnergyCards = 20;
    public static double energyPerCard = 0.5D;

    private PackagedAccelerationConfig() {}

    public static void load(File file) {
        Configuration config = new Configuration(file);
        String speedCategory = "speed_card";
        maxSpeedCards = config.get(speedCategory, "max_speed_cards", maxSpeedCards, "Maximum speed cards per machine.", 0, 64).getInt();
        speedPerCard = config.get(speedCategory, "speed_per_card", speedPerCard, "Speed multiplier added by each card.", 0D, Double.MAX_VALUE).getDouble();
        String energyCategory = "energy_card";
        maxEnergyCards = config.get(energyCategory, "max_energy_cards", maxEnergyCards, "Maximum energy cards per machine.", 0, 64).getInt();
        energyPerCard = config.get(energyCategory, "energy_per_card", energyPerCard, "Energy capacity multiplier added by each card.", 0D, Double.MAX_VALUE).getDouble();
        if(config.hasChanged()) {
            config.save();
        }
    }
}
