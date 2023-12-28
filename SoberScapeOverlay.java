package net.runelite.client.plugins.SoberScape;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

//SoberScape Plugin In-Game Overlay
public class SoberScapeOverlay extends Overlay {

    private SoberScapePlugin plugin;
    private SoberScapeConfig config;
    private ItemManager itemManager;
    private PanelComponent image;

    @Inject
    private SoberScapeOverlay(SoberScapePlugin plugin, SoberScapeConfig config, ItemManager itemManager){
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        setOverlayProps();
        initializeImage();
    }

    public void setOverlayProps(){
        setPosition(OverlayPosition.TOP_CENTER);
        setPriority(OverlayPriority.HIGH);
    }

    public void initializeImage(){
        image = new PanelComponent();
        image.setBackgroundColor(new Color(150,0,0,150));
        image.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.KEBAB)));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
            return image.render(graphics);
    }
}
