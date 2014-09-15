package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class BlockSkull extends BlockType {

    public static final int MAX_OWNER_LENGTH = 16;

    public BlockSkull() {
        setDrops(new ItemStack(Material.SKULL_ITEM));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if(!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skull = (Skull) data;
        skull.setFacingDirection(face);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESkull(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        GlowSkull skull = (GlowSkull) block.getState();
        skull.setSkullType(getType(holding.getDurability()));
        if(skull.getSkullType() == SkullType.PLAYER) {
            SkullMeta meta = (SkullMeta) holding.getItemMeta();
            if(meta != null) {
                skull.setOwner(meta.getOwner());
            }
        }
        MaterialData data = skull.getData();
        if(!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skullData = (Skull) data;

        if(skullData.getFacing() == BlockFace.SELF) { // Can be rotated
            // Calculate the rotation based on the player's facing direction
            Location loc = player.getLocation();
            // 22.5 = 360 / 16
            long facing = Math.round(loc.getYaw() / 22.5) + 8;
            byte rotation = (byte) (((facing % 16) + 16) % 16);
            skull.setRotation(getRotation(rotation));
        }
        skull.update();
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

        return Arrays.asList(drop);
    }

    public static PlayerProfile getProfile(String name) {
        if(name == null || name.length() > MAX_OWNER_LENGTH || name.isEmpty()) return null;

        UUID uuid = ((GlowServer) Bukkit.getServer()).getPlayerDataService().lookupUUID(name);
        return new PlayerProfile(name, uuid);
    }

    public static BlockFace getRotation(byte rotation) {
        switch (rotation) {
            case 0:
                return BlockFace.NORTH;
            case 1:
                return BlockFace.NORTH_NORTH_EAST;
            case 2:
                return BlockFace.NORTH_EAST;
            case 3:
                return BlockFace.EAST_NORTH_EAST;
            case 4:
                return BlockFace.EAST;
            case 5:
                return BlockFace.EAST_SOUTH_EAST;
            case 6:
                return BlockFace.SOUTH_EAST;
            case 7:
                return BlockFace.SOUTH_SOUTH_EAST;
            case 8:
                return BlockFace.SOUTH;
            case 9:
                return BlockFace.SOUTH_SOUTH_WEST;
            case 10:
                return BlockFace.SOUTH_WEST;
            case 11:
                return BlockFace.WEST_SOUTH_WEST;
            case 12:
                return BlockFace.WEST;
            case 13:
                return BlockFace.WEST_NORTH_WEST;
            case 14:
                return BlockFace.NORTH_WEST;
            case 15:
                return BlockFace.NORTH_NORTH_WEST;
        }
        throw new IllegalArgumentException("Not a valid skull rotation: " + rotation);
    }

    public static byte getRotation(BlockFace rotation) {
        switch(rotation) {
            case SOUTH:
                return 0x0;
            case SOUTH_SOUTH_WEST:
                return 0x1;
            case SOUTH_WEST:
                return 0x2;
            case WEST_SOUTH_WEST:
                return 0x3;
            case WEST:
                return 0x4;
            case WEST_NORTH_WEST:
                return 0x5;
            case NORTH_WEST:
                return 0x6;
            case NORTH_NORTH_WEST:
                return 0x7;
            case NORTH:
                return 0x8;
            case NORTH_NORTH_EAST:
                return 0x9;
            case NORTH_EAST:
                return 0xA;
            case EAST_NORTH_EAST:
                return 0xB;
            case EAST:
                return 0xC;
            case EAST_SOUTH_EAST:
                return 0xD;
            case SOUTH_EAST:
                return 0xE;
            case SOUTH_SOUTH_EAST:
                return 0xF;
        }
        throw new IllegalArgumentException("Not a valid skull rotation:" + rotation);
    }

    public static SkullType getType(int id) {
        if(id >= SkullType.values().length || id < 0) throw new IllegalArgumentException("ID not a Skull type: " + id);
        return SkullType.values()[id];
    }

    public static byte getType(SkullType type) {
        return (byte) type.ordinal();
    }

    public static boolean canRotate(Skull skull) {
        return skull.getFacing() == BlockFace.SELF;
    }
}
