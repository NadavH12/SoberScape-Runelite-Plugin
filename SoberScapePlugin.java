package net.runelite.client.plugins.SoberScape;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import javax.inject.Inject;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "SoberScape", description = "Removes references to alcohol", tags = {"Sober"})

public class SoberScapePlugin extends Plugin {

    @Inject private SoberScapeConfig config;
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private OverlayManager overlayManager;
    @Inject private SoberScapeOverlay overlay;
    private Widget invUpdateWidget;

    private final List<Integer> alcoholicDrinks = ImmutableList.of(ItemID.ASGARNIAN_ALE, ItemID.ASGOLDIAN_ALE, ItemID.AXEMANS_FOLLY, ItemID.BANDITS_BREW, ItemID.CHEFS_DELIGHT,
                                                                    ItemID.CIDER, ItemID.DRAGON_BITTER, ItemID.DWARVEN_STOUT, ItemID.GREENMANS_ALE, ItemID.GROG, ItemID.LIZARDKICKER,
                                                                    ItemID.MOONLIGHT_MEAD, ItemID.SLAYERS_RESPITE, ItemID.WIZARDS_MIND_BOMB, ItemID.BEER, ItemID.BEER_TANKARD,
                                                                    ItemID.BLOOD_PINT, ItemID.BLOODY_BRACER, ItemID.ELVEN_DAWN, ItemID.KEG_OF_BEER, ItemID.KHALI_BREW, ItemID.BRANDY,
                                                                    ItemID.BLURBERRY_SPECIAL, ItemID.CHOC_SATURDAY, ItemID.DRUNK_DRAGON, ItemID.FRUIT_BLAST, ItemID.PINEAPPLE_PUNCH,
                                                                    ItemID.SHORT_GREEN_GUY, ItemID.WIZARD_BLIZZARD, ItemID.GIN, ItemID.RUM, ItemID.KARAMJAN_RUM, ItemID.BOTTLE_OF_WINE,
                                                                    ItemID.JUG_OF_WINE, ItemID.UNFERMENTED_WINE, ItemID.JUG_OF_BAD_WINE, ItemID.HALF_FULL_WINE_JUG, ItemID.VODKA,
                                                                    ItemID.WHISKY);

    @Provides
    SoberScapeConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SoberScapeConfig.class);
    }

    @Override
    public void startUp() {
        overlayManager.add(overlay);
        clientThread.invoke(this::redrawInventory);
    }

    @Override
    public void shutDown() {
        clientThread.invoke(this::redrawInventory);
        overlayManager.remove(overlay);
    }

    private void redrawInventory(){
        WidgetInfo invyWidgeInfo = WidgetInfo.INVENTORY;
        Widget invyWidge = client.getWidget(invyWidgeInfo);
        if(invyWidge != null) {
            Object[] invyChangedScript = invyWidge.getOnInvTransmitListener();
            client.runScript(invyChangedScript);
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        int scriptID = scriptPreFired.getScriptId();

        if(scriptID == 153){ //scriptID 153 = [proc,interface_inv_update_big]
            int[] intStack = client.getIntStack();
            int intStackSize = client.getIntStackSize();
            int desiredWidgetIndex = intStack[intStackSize - 6];
            invUpdateWidget = client.getWidget(desiredWidgetIndex);
        }

        else if (scriptID == 975){//scriptID 975 = [proc,deathkeep_left_setsection]
            int[] intStack = client.getIntStack();
            int intStackSize = client.getIntStackSize();
            int desiredWidgetIndex = intStack[intStackSize - 5];
            invUpdateWidget = client.getWidget(desiredWidgetIndex);
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        int scriptID = scriptPostFired.getScriptId();

        // [proc,inventory_build]
        if (scriptID == 6010) {
            Widget w = client.getWidget(WidgetInfo.INVENTORY);
            replaceItems(w);
            replaceItems(client.getWidget(268, 0)); // deposit box inventory
        }

        // [proc,bank_depositbox_update]
        else if (scriptID == 146) {
            replaceItems(client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER));
        }

        // [proc,bankmain_build]
        else if (scriptID == 277) {
            replaceItems(client.getWidget(WidgetInfo.BANK_TAB_CONTAINER));
            replaceItems(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER));
        }

        // [proc,bankside_build]
        else if (scriptID == 296) {
            replaceItems(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER));
        }

        // [proc,ge_offer_side_draw]
        else if (scriptID == 783) {
            replaceItems(client.getWidget(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER));
        }

        // [proc,ge_pricechecker_redraw]
        else if (scriptID == 787) {
            replaceItems(client.getWidget(WidgetInfo.GUIDE_PRICES_ITEMS_CONTAINER));
        }

        // [proc,itemsets_side_draw]
        else if (scriptID == 827) {
            replaceItems(client.getWidget(430, 0));
        }

        // [proc,bankside_worn_drawitem]
        else if (scriptID == 3327) {
            replaceItems(client.getWidget(15, 4));
        }

        // [proc,interface_inv_update_big]
        // [proc,deathkeep_left_redraw]
        else if (scriptID == 153 || scriptID == 975) {
            if (invUpdateWidget != null) {
                replaceItems(invUpdateWidget);
                invUpdateWidget = null;
            }
        }
    }

    private void replaceItems(Widget w) {
        if (w == null) return;
        for (int drinkID : alcoholicDrinks) {
            for (Widget i : w.getDynamicChildren()) {
                if (i.getItemId() == drinkID) {
                    i.setName("Dookie");
                    i.setItemId(ItemID.FOSSILISED_DUNG);
                }
            }
        }
    }
}
