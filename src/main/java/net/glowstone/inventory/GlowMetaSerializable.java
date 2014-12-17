package net.glowstone.inventory;

import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Class used to manage the deserialization of ItemMeta.
 *
 */
@SerializableAs("ItemMeta")
public class GlowMetaSerializable implements ConfigurationSerializable {

    static final ImmutableMap<Class<? extends GlowMetaItem>, String> classes;
    static final ImmutableMap<String, Constructor<? extends GlowMetaItem>> constructors;

    static {
        classes = ImmutableMap.<Class<? extends GlowMetaItem>, String>builder()
                .put(GlowMetaBanner.class, "BANNER")
                .put(GlowMetaBook.class, "BOOK")
                .put(GlowMetaFirework.class, "FIREWORK")
                .put(GlowMetaFireworkEffect.class, "FIREWORK_EFFECT")
                .put(GlowMetaItem.class, "UNSPECIFIED")
                .put(GlowMetaLeatherArmor.class, "LEATHER_ARMOR")
                .put(GlowMetaSkull.class, "SKULL")
                .build();

        ImmutableMap.Builder<String, Constructor<? extends GlowMetaItem>> constructorBuilder = ImmutableMap.builder();
        for (Map.Entry<Class<? extends GlowMetaItem>, String> entry : classes.entrySet()) {
            try {
                constructorBuilder.put(entry.getValue(), entry.getKey().getDeclaredConstructor(Map.class));
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }
        constructors = constructorBuilder.build();
    }

    private GlowMetaSerializable() { }

    public static ItemMeta deserialize(Map<String, Object> map) throws Throwable {
        String type = GlowMetaItem.getObject(String.class, map, "meta-type", false);
        Constructor<? extends GlowMetaItem> constructor = constructors.get(type);

        if (constructor == null) {
            throw new IllegalArgumentException(type + " is not a valid item meta");
        }

        try {
            return constructor.newInstance(map);
        } catch (InvocationTargetException | InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw e.getCause();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        throw new AssertionError();
    }
}
