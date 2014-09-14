package net.glowstone.inventory;

import net.glowstone.GlowServer;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.UUID;

public class GlowMetaSkull extends GlowMetaItem implements SkullMeta {

    PlayerProfile owner;

    public GlowMetaSkull(GlowMetaItem meta) {
        super(meta);
        if(meta == null || !(meta instanceof GlowMetaSkull)) {
            return;
        }
        GlowMetaSkull skull = (GlowMetaSkull) meta;
        this.owner = skull.owner;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal stuff

    @Override
    public SkullMeta clone() {
        return new GlowMetaSkull(this);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.SKULL_ITEM;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "SKULL");
        if(hasOwner()) {
            result.put("owner", owner);
        }
        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if(hasOwner()) {
            tag.putCompound("SkullOwner", owner.toNBT());
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if(tag.containsKey("SkullOwner")) {
            if(tag.isString("SkullOwner")) {
                String name = tag.getString("SkullOwner");
                UUID uuid = ((GlowServer) Bukkit.getServer()).getPlayerDataService().lookupUUID(name);
                owner = new PlayerProfile(name, uuid);
                //TODO: Use Mojang API to get properties
            } else if(tag.isCompound("SkullOwner")) {
                owner = PlayerProfile.fromNBT(tag.getCompound("SkullOwner"));
            }
        }
    }



    ////////////////////////////////////////////////////////////////////////////
    // Properties


    @Override
    public String getOwner() {
        return (hasOwner() ? owner.getName() : null);
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public boolean setOwner(String name) {
        PlayerProfile owner = BlockSkull.getProfile(name);
        if(owner == null) {
            return false;
        }
        this.owner = owner;
        return true;
    }
}
