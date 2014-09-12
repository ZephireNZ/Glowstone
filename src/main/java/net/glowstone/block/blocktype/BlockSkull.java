package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;

import java.util.Collection;

public class BlockSkull extends BlockType {

    public BlockSkull() {
        setDrops(new ItemStack(Material.SKULL_ITEM));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        Skull skull = (Skull) state.getData();
        skull.setFacingDirection(face);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESkull(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        GlowSkull skull = (GlowSkull) block.getState();
        skull.setSkullType(GlowSkull.getType(holding.getDurability()));
        if(skull.getSkullType() == SkullType.PLAYER) {
            SkullMeta meta = (SkullMeta) holding.getItemMeta();
            if(meta != null) {
                skull.setOwner(meta.getOwner());
            }
        }
        skull.update();
        //TODO: Rotation
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        GlowSkull skull = (GlowSkull) block.getState();

        ItemStack drop = new ItemStack(Material.SKULL_ITEM, 1);
        if(skull.hasOwner()) {
            SkullMeta meta = (SkullMeta) drop.getItemMeta();
            meta.setOwner(skull.getOwner());
            drop.setItemMeta(meta);
        }
        drop.setDurability((short) skull.getSkullType().ordinal());

        return super.getDrops(block);
    }
}
