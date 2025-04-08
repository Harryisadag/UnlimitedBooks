package com.harryisadag.unlimitedbooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UnlimitedBooksMod implements ModInitializer {
    private static final int CHAR_LIMIT = 255;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("importbook")
                .then(CommandManager.argument("filename", StringArgumentType.word())
                .executes(context -> importBook(context, StringArgumentType.getString(context, "filename")))));
        });

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
    }

    private void onServerStart(MinecraftServer server) {}

    private int importBook(CommandContext<ServerCommandSource> context, String filename) {
        ServerCommandSource source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        try {
            Path bookPath = Path.of("config", filename + ".txt");
            if (!Files.exists(bookPath)) {
                source.sendError(Text.literal("File not found: " + bookPath));
                return 0;
            }

            String content = Files.readString(bookPath);
            List<String> pages = splitTextIntoPages(content);

            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            NbtCompound tag = new NbtCompound();
            tag.putString("author", player.getName().getString());
            tag.putString("title", filename);

            NbtList pageList = new NbtList();
            for (String page : pages) {
                pageList.add(NbtString.of(page));
            }
            tag.put("pages", pageList);
            book.setNbt(tag);

            player.giveItemStack(book);
            source.sendFeedback(() -> Text.literal("Book imported and given to you."), false);
            return 1;

        } catch (Exception e) {
            source.sendError(Text.literal("Failed to load book: " + e.getMessage()));
            return 0;
        }
    }

    private List<String> splitTextIntoPages(String text) {
        List<String> pages = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            int end = Math.min(index + CHAR_LIMIT, text.length());
            pages.add(text.substring(index, end));
            index = end;
        }
        return pages;
    }
}
