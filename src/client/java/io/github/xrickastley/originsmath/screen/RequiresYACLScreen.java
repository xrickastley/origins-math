package io.github.xrickastley.originsmath.screen;

import io.github.xrickastley.originsmath.config.OriginsMathConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RequiresYACLScreen extends Screen {
	private final Screen parent;
	private final Text text = Text
		.literal("You need to download ").formatted(Formatting.GRAY)
		.append(OriginsMathConfig.textWithLink(Text.literal("YetAnotherConfigLib (YACL)"), "https://modrinth.com/mod/yacl"))
		.append(Text.literal(" to use the config menu!").formatted(Formatting.GRAY));

	public RequiresYACLScreen(final Screen parent) {
		super(Text.literal("Origins: Math Config"));

		this.parent = parent;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		this.renderBackground(context);
		this.drawCenteredText(context, text, 0xffffffff);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		final OrderedText orderedText = text.asOrderedText();

		final int x = (this.width - this.textRenderer.getWidth(orderedText)) / 2;
		final int y = (this.height - this.textRenderer.fontHeight) / 2;
		final int dx = this.textRenderer.getWidth(orderedText);
		final int dy = this.height - this.textRenderer.fontHeight;

		if ((x <= mouseX && mouseX <= (x + dx)) && (y <= mouseY && mouseY <= (y + dy))) {
			ConfirmLinkScreen.open("https://modrinth.com/mod/yacl", this, false);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void drawCenteredText(DrawContext context, Text text, int color) {
		final OrderedText orderedText = text.asOrderedText();

		context.drawText(
			this.textRenderer,
			orderedText,
			(this.width - this.textRenderer.getWidth(orderedText)) / 2,
			(this.height - this.textRenderer.fontHeight) / 2,
			color,
			false
		);
	}

	@Override
	public void close() {
		this.client.setScreen(parent);
	}
}
