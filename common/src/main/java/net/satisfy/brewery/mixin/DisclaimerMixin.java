package net.satisfy.brewery.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.satisfy.brewery.Brewery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;

@Mixin(LevelLoadingScreen.class)
public abstract class DisclaimerMixin extends Screen {
    @Unique
    private static final Component[] DISCLAIMERS;

    static {
        DISCLAIMERS = new Component[]{
                Component.translatable("disclaimer.brewery.drugs"),
                Component.translatable("disclaimer.brewery.drive"),
                Component.translatable("disclaimer.brewery.alcohol")
        };
    }

    @Unique
    private final Component disclaimer = DISCLAIMERS[RandomSource.create().nextInt(0, DISCLAIMERS.length)];
    @Unique
    private boolean added = false;

    protected DisclaimerMixin(Component component) {
        super(component);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderDisclaimer(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        String[] disclaimers = this.disclaimer.getString().split("\n");
        if (!this.added) {
            this.added = true;
            Component title = Component.translatable("disclaimer.brewery.disclaimer").withStyle(ChatFormatting.YELLOW);
            int width = this.font.width(title);
            int x = (this.width - width) / 2;
            this.addRenderableWidget(new PlainTextButton(x, this.height - this.font.lineHeight * (disclaimers.length + 1) - 4, width, 10, title, (button) -> {
                String url = "https://www.who.int/news-room/fact-sheets/detail/alcohol";
                try {
                    Util.getPlatform().openUri(new URI(url));
                } catch (URISyntaxException e) {
                    Brewery.LOGGER.debug("Unable to load URL: [" + url + "]");
                }
            }, this.font));
        }
        for (int y = disclaimers.length; y > 0; y--) {
            String info = disclaimers[disclaimers.length - y];
            int width = this.font.width(info);
            int x = (this.width - width) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0, 0.0, 2);
            guiGraphics.drawString(this.font, Component.translatable(info).withStyle(ChatFormatting.WHITE), x, this.height - this.font.lineHeight * y - 4, 0xFFFF55);
            guiGraphics.pose().popPose();

        }
        super.render(guiGraphics, i, j, f);
    }
}
