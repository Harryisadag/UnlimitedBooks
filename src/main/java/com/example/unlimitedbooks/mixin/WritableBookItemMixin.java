
package com.example.unlimitedbooks.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WritableBookItem.class)
public class WritableBookItemMixin {

    @ModifyArg(
        method = "writeBook",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;add(Lnet/minecraft/nbt/NbtElement;)Z"),
        index = 0
    )
    private NbtString bypassPageLimit(NbtString original) {
        // Allows very large text chunks by skipping limit check
        return original;
    }
}
