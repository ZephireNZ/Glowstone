package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;

import java.util.UUID;

public class TESkull extends TileEntity {

    private byte type;
    private byte rotation;
    private OfflinePlayer owner;

    public TESkull(GlowBlock block) {
        super(block);
        setSaveId("Skull");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        type = tag.getByte("SkullType");
        rotation = tag.getByte("Rot");
        if(type == GlowSkull.getType(SkullType.PLAYER)) {
            if(tag.containsKey("Owner")) {
                String uuidStr = tag.getCompound("Owner").getString("Id");
                if(uuidStr != null && !uuidStr.isEmpty()) {
                    UUID uuid = UUID.fromString(uuidStr);
                    owner = Bukkit.getOfflinePlayer(uuid);
                    //TODO: Use PlayerProfile and Properties
                }
            } else if(tag.containsKey("ExtraType")) {
                String name = tag.getString("ExtraType");
                if(name != null && !name.isEmpty()) {
                    owner = Bukkit.getOfflinePlayer(name);
                }
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("SkullType", type);
        tag.putByte("Rot", rotation);
        if(type == GlowSkull.getType(SkullType.PLAYER)) {
            CompoundTag owner = new CompoundTag();
            owner.putString("Id", this.owner.getUniqueId().toString());
            owner.putString("Name", this.owner.getName());
            //TODO: Profile and Properties
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowSkull(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendBlockEntityChange(getBlock().getLocation(), nbt);
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getRotation() {
        return rotation;
    }

    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
        this.type = GlowSkull.getType(SkullType.PLAYER);
    }
}
