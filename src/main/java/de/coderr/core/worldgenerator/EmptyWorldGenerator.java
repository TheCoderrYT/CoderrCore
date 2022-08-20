package de.coderr.core.worldgenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator
{
    public List<BlockPopulator> getDefaultPopulators(World world) {

        return new ArrayList<BlockPopulator>();
    }

    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkY, BiomeGrid grid) {
        ChunkData data = createChunkData(world);

        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                data.setBlock(x,0,z, Material.AIR);
            }
        }

        return data;
    }
}
