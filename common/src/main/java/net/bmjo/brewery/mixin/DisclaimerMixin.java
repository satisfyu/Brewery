package net.bmjo.brewery.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.bmjo.brewery.Brewery;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.MalformedURLException;
import java.net.URL;

@Mixin(LevelLoadingScreen.class)
public abstract class DisclaimerMixin extends Screen {
    @Unique
    private static final Component[] DISCLAIMERS;
    @Unique
    private final Component disclaimer = Component.translatable("disclaimer.brewery.disclaimer", DISCLAIMERS[RandomSource.create().nextInt(0, DISCLAIMERS.length)]).withStyle(ChatFormatting.YELLOW);
    @Unique
    private boolean added = false;

    protected DisclaimerMixin(Component component) {
        super(component);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderDisclaimer(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        if (!this.added) {
            this.added = true;
            int width = this.font.width(disclaimer);
            int x = (this.width - width) / 2;
            this.addRenderableWidget(new PlainTextButton(x, this.height - this.font.lineHeight - 4, width, 10, disclaimer, (button) -> {
                String url = "https://www.who.int/news-room/fact-sheets/detail/alcohol";
                try {
                    Util.getPlatform().openUrl(new URL(url));
                } catch (MalformedURLException e) {
                    Brewery.LOGGER.debug("Unable to load URL: [" + url + "]");
                }
            }, this.font));
        }
        super.render(poseStack, i, j, f);
    }


    static {
        DISCLAIMERS = new Component[]{
                Component.translatable("disclaimer.brewery.drugs"),
                Component.translatable("disclaimer.brewery.drive"),
                Component.translatable("disclaimer.brewery.alcohol")
        };
    }
}
