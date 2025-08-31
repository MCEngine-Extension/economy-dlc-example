package io.github.mcengine.extension.dlc.economy.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
// If IMCEngineEconomyDLC exists in your codebase, adjust the import accordingly:
import io.github.mcengine.api.economy.extension.dlc.IMCEngineEconomyDLC;

import io.github.mcengine.extension.dlc.economy.example.command.EconomyDLCCommand;
import io.github.mcengine.extension.dlc.economy.example.listener.EconomyDLCListener;
import io.github.mcengine.extension.dlc.economy.example.tabcompleter.EconomyDLCTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;

/**
 * Main class for the Economy DLC example module.
 * <p>
 * Registers the {@code /economydlcexample} command and related event listeners.
 */
public class ExampleEconomyDLC implements IMCEngineEconomyDLC {

    /**
     * Custom extension logger for this module, with contextual labeling.
     */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Economy DLC example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "DLC", "EconomyExampleDLC");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EconomyDLCListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /economydlcexample command
            Command economyDlcExampleCommand = new Command("economydlcexample") {

                /**
                 * Handles command execution for /economydlcexample.
                 */
                private final EconomyDLCCommand handler = new EconomyDLCCommand();

                /**
                 * Handles tab-completion for /economydlcexample.
                 */
                private final EconomyDLCTabCompleter completer = new EconomyDLCTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            economyDlcExampleCommand.setDescription("Economy DLC example command.");
            economyDlcExampleCommand.setUsage("/economydlcexample");

            // Dynamically register the /economydlcexample command
            commandMap.register(plugin.getName().toLowerCase(), economyDlcExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEconomyDLC: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Economy DLC example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-economy-dlc-example");
    }
}
