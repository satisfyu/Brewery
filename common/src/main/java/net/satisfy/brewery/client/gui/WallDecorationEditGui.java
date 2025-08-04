package net.satisfy.brewery.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.core.block.entity.WallDecorationBlockEntity;
import net.satisfy.brewery.core.network.PacketHandler;
import net.satisfy.brewery.core.network.SetWallDecorationTextPacket;
import net.satisfy.brewery.core.util.BreweryIdentifier;

import java.util.List;

public class WallDecorationEditGui extends Screen {
    private final WallDecorationBlockEntity entity;
    private EditBox textField;

    public WallDecorationEditGui(WallDecorationBlockEntity entity) {
        super(Component.translatable("gui.brewery.wall_decoration.edit"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        int offsetX = 82;
        int offsetY = 7;

        this.textField = new EditBox(this.font,
                this.width / 2 - 100 + offsetX,
                this.height / 2 - 30 + offsetY,
                200, 20,
                Component.literal(""));
        textField.setValue(entity.getText(0).getString());
        textField.setMaxLength(8);
        textField.setBordered(false);
        textField.setTextColor(0xFADFB0);
        this.addRenderableWidget(textField);

        this.setInitialFocus(textField);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.brewery.wall_decoration.done"), button -> this.onClose())
                .bounds(this.width / 2 - 50, this.height - 40, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        ResourceLocation texture = new BreweryIdentifier("textures/block/gingerbread_heart.png");
        int w = 16 * 8;
        int h = 16 * 8;
        int x = (int)(this.width / 2.0 - 65);
        int y = (int)(this.height / 2.0 - 90);

        graphics.blit(texture, x, y, w, h, 0, 0, 16, 16, 16, 16);

        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 257 || keyCode == 335) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        List<String> texts = List.of(textField.getValue());
        PacketHandler.sendToServer(new SetWallDecorationTextPacket(entity.getBlockPos(), texts));
        super.onClose();
    }
}
