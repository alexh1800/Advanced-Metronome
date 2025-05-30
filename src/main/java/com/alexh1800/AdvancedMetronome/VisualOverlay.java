package com.alexh1800.AdvancedMetronome;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class VisualOverlay extends Overlay
{
    private final Client client;
    private final AdvancedMetronomePlugin plugin;
    private final AdvancedMetronomeConfig config;

    @Inject
    public VisualOverlay(Client client, AdvancedMetronomePlugin plugin, AdvancedMetronomeConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        // Set the overlay to move with the game world (e.g., above the player)
        setPosition(OverlayPosition.DYNAMIC);
        // Draw this above the main game scene (so it shows over your player)
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    //note render runs every time the screen is redrawn so works on every game tick without needing to be in onGameTick loop
    public Dimension render(Graphics2D graphics)
    {

        //if the visual metronome is turned off, don't draw it to the screen
        if (!config.enableVisualMetronome()){
            return null;
        }

        Player player = client.getLocalPlayer(); // Get your own character
        if (player == null)
        {
            log.info("Player");
            return null; // Game might still be loading
        }

        // Get the current tick number
        String tickText = String.valueOf(plugin.tickCount);

        // Determine where to draw the text (above your character)
        int zOffset = config.textOffset();
        Point textLocation = player.getCanvasTextLocation(graphics, tickText, zOffset);
        if (textLocation == null)
        {
            log.info("couldn't calculate text location");
            return null; // Couldn't calculate a position
        }

        // Set font from config
        graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));

        //get the width of text so that it can be centered
        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(tickText);

        //place the text on the screen
        int x = textLocation.getX() - (textWidth / 2);
        int y = textLocation.getY();

        // Draw black outline around text (offset by 1 pixel in each direction)
        graphics.setColor(Color.BLACK);
        graphics.drawString(tickText, x + 1, y);
        graphics.drawString(tickText, x - 1, y);
        graphics.drawString(tickText, x, y + 1);
        graphics.drawString(tickText, x, y - 1);

        // Draw actual text in user-selected font color
        graphics.setColor(config.fontColor());
        graphics.drawString(tickText, x, y);

        return null; // No fixed size needed — it's dynamically placed
    }
}
