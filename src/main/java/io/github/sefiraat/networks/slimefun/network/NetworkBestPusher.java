package io.github.sefiraat.networks.slimefun.network;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.network.stackcaches.ItemStackCache;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class NetworkBestPusher extends NetworkDirectional {
    private static final int[] BACKGROUND_SLOTS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 15, 17, 18, 20, 22, 23, 27, 28, 30, 31, 36, 37, 38, 39, 40, 41 };
    private static final int[] TEMPLATE_BACKGROUND = new int[] { 16 };
    private static final int[] TEMPLATE_SLOTS = new int[] { 24, 25, 26, 33, 34, 35, 42, 43, 44 };
    private static final int NORTH_SLOT = 11;
    private static final int SOUTH_SLOT = 29;
    private static final int EAST_SLOT = 21;
    private static final int WEST_SLOT = 19;
    private static final int UP_SLOT = 14;
    private static final int DOWN_SLOT = 32;
    public static final CustomItemStack TEMPLATE_BACKGROUND_STACK = new CustomItemStack(Material.BLUE_STAINED_GLASS_PANE, "" + Theme.PASSIVE + "指定需要推送的物品", new String[0]);

    public NetworkBestPusher(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.PUSHER);
        for (int slot : TEMPLATE_SLOTS) {
            getSlotsToDrop().add(Integer.valueOf(slot));
        }
    }

    protected void onTick(@Nullable BlockMenu blockMenu, @Nonnull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu != null) {
            tryPushItem(blockMenu);
        }
    }
    private void tryPushItem(@Nonnull BlockMenu blockMenu) {
        NodeDefinition definition = (NodeDefinition)NetworkStorage.getAllNetworkObjects().get(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            return;
        }

        BlockFace direction = getCurrentDirection(blockMenu);
        BlockMenu targetMenu = StorageCacheUtils.getMenu(blockMenu.getBlock().getRelative(direction).getLocation());

        if (targetMenu == null) {
            return;
        }

        for (int itemSlot : getItemSlots()) {
            ItemStack testItem = blockMenu.getItemInSlot(itemSlot);

            if (testItem != null && testItem.getType() != Material.AIR) {
                ItemStack clone = testItem.clone();
                clone.setAmount(1);
                ItemRequest itemRequest = new ItemRequest(clone, clone.getMaxStackSize());
                          int[] slots = targetMenu.getPreset().getSlotsAccessedByItemTransport((DirtyChestMenu)targetMenu, ItemTransportFlow.INSERT, clone); int arrayOfInt1[], i;
                byte b;
                for (arrayOfInt1 = slots, i = arrayOfInt1.length, b = 0; b < i; ) { int slot = arrayOfInt1[b];
                    ItemStack itemStack = targetMenu.getItemInSlot(slot);

                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        int space = itemStack.getMaxStackSize() - itemStack.getAmount();
                        if (space > 0 && StackUtils.itemsMatch((ItemStackCache)itemRequest, itemStack, true)) {
                            itemRequest.setAmount(space);
                        } else {
                            b++;
                            continue;
                        }
                    } 

                    ItemStack retrieved = definition.getNode().getRoot().getItemStack(itemRequest);
                    if (retrieved != null) {
                        targetMenu.pushItem(retrieved, slots);
                        if (definition.getNode().getRoot().isDisplayParticles()) {
                            showParticle(blockMenu.getLocation(), direction);
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    protected int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Nullable
    protected int[] getOtherBackgroundSlots() {
        return TEMPLATE_BACKGROUND;
    }

    @Nullable
    protected CustomItemStack getOtherBackgroundStack() {
        return TEMPLATE_BACKGROUND_STACK;
    }

    public int getNorthSlot() {
        return NORTH_SLOT;
    }

    public int getSouthSlot() {
        return SOUTH_SLOT;
    }

    public int getEastSlot() {
        return EAST_SLOT;
    }

    public int getWestSlot() {
        return WEST_SLOT;
    }

    public int getUpSlot() {
        return UP_SLOT;
    }

    public int getDownSlot() {
        return DOWN_SLOT;
    }

    public int[] getItemSlots() {
        return TEMPLATE_SLOTS;
    }

    protected Particle.DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.MAROON, 1.0F);
    }
}