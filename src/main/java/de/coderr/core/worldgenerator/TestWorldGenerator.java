package de.coderr.core.worldgenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestWorldGenerator extends ChunkGenerator
{
    public List<BlockPopulator> getDefaultPopulators(World world) {

        return new ArrayList<BlockPopulator>();
    }

    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkY, BiomeGrid grid) {
        ChunkData data = createChunkData(world);

        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                data.setBlock(x,0,z, Material.BEDROCK);
                data.setBlock(x,1,z, Material.BEDROCK);
                data.setBlock(x,2,z, Material.SANDSTONE);
                data.setBlock(x,3,z, Material.SANDSTONE);
                data.setBlock(x,4,z, Material.SANDSTONE);
                data.setBlock(x,5,z, Material.SANDSTONE);
                data.setBlock(x,6,z, Material.SANDSTONE);
                data.setBlock(x,7,z, Material.SANDSTONE);
                data.setBlock(x,8,z, Material.SANDSTONE);
                data.setBlock(x,9,z, Material.SANDSTONE);
            }
        }

        return data;
    }
}
