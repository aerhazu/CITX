// CIT.X v1.0 — by Aerhazu

package com.example.citx;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;

public class CITXMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private void registerCommands(CommandDispatcher < ServerCommandSource > dispatcher) {

        dispatcher.register(

                CommandManager.literal("citx")

                        // --------------------------------------------------
                        // DEFAULT
                        // /citx <extension>
                        // --------------------------------------------------

                        .then(CommandManager.argument("input", StringArgumentType.greedyString())
                                .executes(ctx ->
                                        executeExtend(
                                                StringArgumentType.getString(ctx, "input")
                                        )
                                )
                        )

                        // --------------------------------------------------
                        // EXTEND
                        // --------------------------------------------------

                        .then(CommandManager.literal("extend")
                                .then(CommandManager.argument("extension", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestExtension(builder))
                                        .executes(ctx ->
                                                executeExtend(
                                                        StringArgumentType.getString(ctx, "extension")
                                                )
                                        )
                                )
                        )

                        // --------------------------------------------------
                        // E
                        // --------------------------------------------------

                        .then(CommandManager.literal("e")
                                .then(CommandManager.argument("extension", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestExtension(builder))
                                        .executes(ctx ->
                                                executeExtend(
                                                        StringArgumentType.getString(ctx, "extension")
                                                )
                                        )
                                )
                        )

                        // --------------------------------------------------
                        // RENAME
                        // --------------------------------------------------

                        .then(CommandManager.literal("rename")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestRename(builder))
                                        .executes(ctx ->
                                                executeRename(
                                                        StringArgumentType.getString(ctx, "name")
                                                )
                                        )
                                )
                        )

                        // --------------------------------------------------
                        // R
                        // --------------------------------------------------

                        .then(CommandManager.literal("r")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> suggestRename(builder))
                                        .executes(ctx ->
                                                executeRename(
                                                        StringArgumentType.getString(ctx, "name")
                                                )
                                        )
                                )
                        )

                        // --------------------------------------------------
                        // RESET
                        // --------------------------------------------------

                        .then(CommandManager.literal("reset")
                                .executes(ctx -> executeReset())
                        )

                        // --------------------------------------------------
                        // X
                        // --------------------------------------------------

                        .then(CommandManager.literal("x")
                                .executes(ctx -> executeReset())
                        )

        );
    }
    // =========================================================
    // EXECUTE EXTEND
    // =========================================================

    private int executeExtend(String extension) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!hasHeldItem(client)) {
            showNoItem(client);
            return 0;
        }

        extension = sanitizeExtension(extension);

        ItemStack stack = getHeldItem(client);

        String currentName = stack.getName().getString();
        String baseName = getBaseName(currentName);

        String newName = (baseName + extension).replaceAll("__+", "_");

        stack.setCustomName(Text.literal(newName));

        refreshItem(client);

        playSuccessSound(client);

        showRenameMessage(client, newName);

        return 1;
    }

    // =========================================================
    // EXECUTE RENAME
    // =========================================================

    private int executeRename(String name) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!hasHeldItem(client)) {
            showNoItem(client);
            return 0;
        }

        ItemStack stack = getHeldItem(client);

        stack.setCustomName(Text.literal(name));

        refreshItem(client);

        playSuccessSound(client);

        showRenameMessage(client, name);

        return 1;
    }

    // =========================================================
    // EXECUTE RESET
    // =========================================================

    private int executeReset() {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!hasHeldItem(client)) {
            showNoItem(client);
            return 0;
        }

        ItemStack stack = getHeldItem(client);

        stack.setCustomName(null);

        refreshItem(client);

        playSuccessSound(client);

        client.player.sendMessage(
                Text.literal("§aItem name reset."),
                true
        );

        return 1;
    }

    // =========================================================
    // TAB COMPLETION
    // EXTENSION
    // =========================================================

    private CompletableFuture < Suggestions > suggestExtension(SuggestionsBuilder builder) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (hasHeldItem(client)) {

            String currentName = getHeldItem(client)
                    .getName()
                    .getString();

            builder.suggest(getExtension(currentName));
        }

        return builder.buildFuture();
    }

    // =========================================================
    // TAB COMPLETION
    // RENAME
    // =========================================================

    private CompletableFuture < Suggestions > suggestRename(SuggestionsBuilder builder) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (hasHeldItem(client)) {

            builder.suggest(
                    getHeldItem(client)
                            .getName()
                            .getString()
            );
        }

        return builder.buildFuture();
    }
    // =========================================================
    // HELPER
    // PLAYER IS HOLDING AN ITEM
    // =========================================================

    private boolean hasHeldItem(MinecraftClient client) {
        return client.player != null &&
                !client.player.getMainHandStack().isEmpty();
    }

    // =========================================================
    // HELPER
    // GET HELD ITEM
    // =========================================================

    private ItemStack getHeldItem(MinecraftClient client) {
        return client.player.getMainHandStack();
    }

    // =========================================================
    // HELPER
    // GET BASE NAME
    // =========================================================

    private String getBaseName(String currentName) {

        int underscore = currentName.indexOf('_');

        if (underscore == -1) {
            return currentName;
        }

        return currentName.substring(0, underscore);
    }

    // =========================================================
    // HELPER
    // GET CURRENT EXTENSION
    // =========================================================

    private String getExtension(String currentName) {

        int underscore = currentName.indexOf('_');

        if (underscore == -1) {
            return "_";
        }

        return currentName.substring(underscore);
    }

    // =========================================================
    // HELPER
    // SANITIZE EXTENSION
    // =========================================================

    private String sanitizeExtension(String extension) {

        extension = extension.trim();

        if (!extension.startsWith("_")) {
            extension = "_" + extension;
        }

        extension = extension.replaceAll("__+", "_");

        return extension;
    }

    // =========================================================
    // HELPER
    // SHOW NO ITEM
    // =========================================================

    private void showNoItem(MinecraftClient client) {

        if (client.player == null) {
            return;
        }

        playErrorSound(client);

        client.player.sendMessage(
                Text.literal("You must be holding an item to use this command.")
                        .formatted(Formatting.RED),
                true
        );
    }

    // =========================================================
    // HELPER
    // SHOW SUCCESS MESSAGE
    // =========================================================

    private void showRenameMessage(MinecraftClient client, String newName) {

        if (client.player == null) {
            return;
        }

        client.player.sendMessage(
                Text.literal("§aItem renamed to: §r" + newName),
                true
        );
    }

    // =========================================================
    // HELPER
    // REFRESH ITEM
    // =========================================================

    private void refreshItem(MinecraftClient client) {

        if (client.player == null) {}

        /*
         * Placeholder.
         *
         * Minecraft already notices the ItemStack has changed.
         * The remaining issue appears to be CIT model caching,
         * not the ItemStack itself.
         *
         * We'll replace this later if we discover a reliable
         * client-side refresh method.
         */
    }
    // =========================================================
    // SUCCESS SOUND
    // =========================================================

    private void playSuccessSound(MinecraftClient client) {

        if (client.player == null) {
            return;
        }

        client.player.playSound(
                SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
                SoundCategory.PLAYERS,
                0.2F,
                1.00F
        );
    }
    // =========================================================
    // ERROR SOUND
    // =========================================================

    private void playErrorSound(MinecraftClient client) {

        if (client.player == null) {
            return;
        }

        client.player.playSound(
                SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
                0.2F,
                -2.50F
        );
    }
}