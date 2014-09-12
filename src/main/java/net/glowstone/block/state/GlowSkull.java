package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

public class GlowSkull extends GlowBlockState implements Skull {

    private SkullType type;
    private OfflinePlayer owner;
    private BlockFace rotation;

    public GlowSkull(GlowBlock block) {
        super(block);
        type = getType(getTileEntity().getType());
        rotation = getRotation(getTileEntity().getRotation());
        owner = getTileEntity().getOwner();
    }

    public TESkull getTileEntity() {
        return (TESkull) getBlock().getTileEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if(result) {
            TESkull skull = getTileEntity();
            skull.setType(getType(type));
            skull.setRotation(getRotation(rotation));
            if(type == SkullType.PLAYER) {
                skull.setOwner(owner);
            }
            getTileEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public String getOwner() {
        return owner.getName();
    }

    @Override
    public boolean setOwner(String name) {
        if(name == null || name.length() > 16) return false;

        this.owner = Bukkit.getOfflinePlayer(name);
        this.setSkullType(SkullType.PLAYER);
        return true;
    }

    @Override
    public BlockFace getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(BlockFace rotation) {
        this.rotation = rotation;
    }

    @Override
    public SkullType getSkullType() {
        return type;
    }

    @Override
    public void setSkullType(SkullType type) {
        if(type != SkullType.PLAYER) {
            owner = null;
        }
        this.type = type;
    }

    public static SkullType getType(int id) {
        if(id >= SkullType.values().length || id < 0) throw new IllegalArgumentException("ID not a Skull type: " + id);
        return SkullType.values()[id];
    }

    public static byte getType(SkullType type) {
        return (byte) type.ordinal();
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
}
