package com.alexh1800.AdvancedMetronome;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


@Slf4j
@PluginDescriptor(
        name = "Tick Beats Advanced Metronome",
        description = "Metronome with advanced audio and visual configurations",
        tags = {"tick", "beat", "visual", "helper", "metronome", "sound"}
)
//suppressing unused warning in IDE at class level as there are a lot of them with RL Plugins
@SuppressWarnings("unused") //comment this line out if you want to see unused warnings
public class AdvancedMetronomePlugin extends Plugin {

    //Needed for Guice Dependency Injection
    @Inject
    private Client client;

    @Inject
    private AdvancedMetronomeConfig config;

    @Inject
    private VisualOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private InputManager inputManager;

    @Inject
    private SoundManager soundManager;


    //Holds the tick count//
    public int tickCount = 0;

    //Holds the Beat Number of which beat to play/
    public int beatNumber = 1;


    protected void startUp()
    {
        log.info("Metronome started");

        tickCount = config.startTick();

        // Attach the tick overlay
        overlayManager.add(overlay);

        // Register the key input listener
        keyManager.registerKeyListener(inputManager);


    }

    @Override
    protected void shutDown()
    {
        log.info("Tick Beats Advanced Metronome Plugin stopped");
        overlayManager.remove(overlay);
        keyManager.unregisterKeyListener(inputManager);
    }


    /**
     * Fires on every game tick (.6s). Updates the metronome tick count.
     * suppress the unused warning from ide
     */
    @Subscribe
    public void onGameTick(GameTick tick)
    {

        //if the reset key is being held, don't do anything on the game tick
        if(inputManager.resetKeyIsHeld)
        {
            return;
        }

        // Get the max tick count from the user config (clamped between 1–8)
        //int maxTicks = Math.max(1, Math.min(8, config.tickCount()));
        int maxTicks = config.tickCount();

        // Increment the tick counter and wrap back to 1 if over max
        tickCount = (tickCount % maxTicks) + 1;

        // If Audio Metronome is enabled Play the audio cue for the current tick
        if(config.enableAudioMetronome()){
            soundManager.playSound(beatNumber, tickCount);
        }

    }



    /**
     * Required by RuneLite to provide config interface.
     */
    @Provides
    AdvancedMetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AdvancedMetronomeConfig.class);
    }




}
