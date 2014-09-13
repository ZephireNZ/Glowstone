package net.glowstone.block.entity;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.entity.meta.PlayerProperty;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.SkullType;

import java.util.Arrays;
import java.util.UUID;

public class TESkull extends TileEntity {

    private byte type;
    private byte rotation;
    private PlayerProfile owner;

    public TESkull(GlowBlock block) {
        super(block);
        setSaveId("Skull");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        type = tag.getByte("SkullType");
        rotation = tag.getByte("Rot");
        if(tag.containsKey("Owner")) {
            CompoundTag ownerTag = tag.getCompound("Owner");

            String uuidStr = ownerTag.getString("Id");
            String name = ownerTag.getString("Name");

            owner = new PlayerProfile(name, UUID.fromString(uuidStr));
            // NBT: {Properties: {textures: [{Signature: "", Value: {}}]}}
            CompoundTag texturesTag = ownerTag.getCompound("Properties").getCompoundList("textures").get(0);
            PlayerProperty textures = new PlayerProperty("textures", texturesTag.getString("Value"), texturesTag.getString("Signature"));
            owner.getProperties().add(textures);
        } else if(tag.containsKey("ExtraType")) {
            // Pre-1.8 uses just a name, instead of a profile object
            String name = tag.getString("ExtraType");
            if(name != null && !name.isEmpty()) {
                UUID uuid = ((GlowServer) Bukkit.getServer()).getPlayerDataService().lookupUUID(name);
                owner = new PlayerProfile(name, uuid);
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("SkullType", type);
        tag.putByte("Rot", rotation);
        if(type == BlockSkull.getType(SkullType.PLAYER) && owner != null) {
            CompoundTag ownerTag = new CompoundTag();
            ownerTag.putString("Id", this.owner.getUniqueId().toString());
            ownerTag.putString("Name", this.owner.getName());

            CompoundTag propertiesTag = new CompoundTag();
            for(PlayerProperty property : owner.getProperties()) {
                CompoundTag propertyValueTag = new CompoundTag();
                propertyValueTag.putString("Signature", property.getSignature());
                propertyValueTag.putString("Value", property.getValue());

                propertiesTag.putCompoundList(property.getName(), Arrays.asList(propertyValueTag));
            }
            if(!propertiesTag.isEmpty()) { // Only add properties if not empty
                ownerTag.putCompound("Properties", propertiesTag);
            }
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
        player.sendSkullChange(getBlock().getLocation(), nbt);
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

    public PlayerProfile getOwner() {
        return owner;
    }

    public void setOwner(PlayerProfile owner) {
        this.owner = owner;
        this.type = BlockSkull.getType(SkullType.PLAYER);
    }
}
