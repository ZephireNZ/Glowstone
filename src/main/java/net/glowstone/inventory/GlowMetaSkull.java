package net.glowstone.inventory;

import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

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
            tag.putString("SkullOwner", getOwner());
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if(tag.containsKey("SkullOwner")) {
            setOwner(tag.getString("SkullOwner"));
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
