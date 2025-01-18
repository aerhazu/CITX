package com.example.citx; // Update the package name to reflect the new ID

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CITXMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			registerCommands(dispatcher);
		});
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		// /citx command
		dispatcher.register(
				CommandManager.literal("citx")
						.then(CommandManager.argument("extension", StringArgumentType.string())
								.executes(context -> {
									// Get the extension argument
									String extension = StringArgumentType.getString(context, "extension").trim();

									// Ensure the extension starts with an underscore
									if (!extension.startsWith("_")) {
										extension = "_" + extension;
									}

									// Access Minecraft client
									MinecraftClient client = MinecraftClient.getInstance();

									if (client.player != null && !client.player.getMainHandStack().isEmpty()) {
										// Get the current item name
										String currentName = client.player.getMainHandStack().getName().getString();
										boolean hadDoubleUnderscores = false;

										// Fix double underscores in the extension
										if (extension.contains("__")) {
											extension = extension.replaceAll("__+", "_");
											hadDoubleUnderscores = true;
										}

										// Determine the base name and current extension
										String baseName = currentName.split("_", 2)[0]; // Original item name
										String newName = baseName + extension; // Add or replace the extension

										// Ensure no double underscores in the final name
										newName = newName.replaceAll("__+", "_");

										// Rename the item
										client.player.getMainHandStack().setCustomName(Text.literal(newName));

										// Feedback to the player
										client.player.sendMessage(
												Text.literal("§aItem renamed to: §r" + newName),
												true
										);

										// Notify the player if double underscores were fixed
										if (hadDoubleUnderscores) {
											client.player.sendMessage(
													Text.literal("§eDouble underscores detected. We fixed them for you!")
															.formatted(Formatting.YELLOW),
													false
											);
										}
									} else {
										// Show error if no item is held
										if (client.player != null) {
											client.player.sendMessage(
													Text.literal("§cYou must be holding an item to use this command.")
															.formatted(Formatting.RED),
													true
											);
										}
									}

									return 1; // Successful execution
								})
						)
		);

		// /citxreset command
		dispatcher.register(
				CommandManager.literal("citxreset")
						.executes(context -> {
							// Access Minecraft client
							MinecraftClient client = MinecraftClient.getInstance();

							if (client.player != null && !client.player.getMainHandStack().isEmpty()) {
								// Reset the custom name of the item
								client.player.getMainHandStack().setCustomName(null);

								// Feedback to the player
								client.player.sendMessage(
										Text.literal("§aItem name reset to its original state."),
										true
								);
							} else {
								// Show error if no item is held
								if (client.player != null) {
									client.player.sendMessage(
											Text.literal("§cYou must be holding an item to use this command.")
													.formatted(Formatting.RED),
											true
									);
								}
							}

							return 1; // Successful execution
						})
		);
	}
}
