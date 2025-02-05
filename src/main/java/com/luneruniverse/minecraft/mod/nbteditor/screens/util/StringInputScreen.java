package com.luneruniverse.minecraft.mod.nbteditor.screens.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVMisc;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.TextInst;
import com.luneruniverse.minecraft.mod.nbteditor.screens.TickableSupportingScreen;
import com.luneruniverse.minecraft.mod.nbteditor.util.MainUtil;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

public class StringInputScreen extends TickableSupportingScreen {
	
	private final Screen parent;
	private final Consumer<String> valueConsumer;
	private final Predicate<String> valueValidator;
	private TextFieldWidget value;
	private ButtonWidget ok;
	private String defaultValue;
	
	public StringInputScreen(Screen screen, Consumer<String> valueConsumer, Predicate<String> valueValidator) {
		super(TextInst.of("String Input"));
		
		this.parent = screen;
		this.valueConsumer = valueConsumer;
		this.valueValidator = valueValidator;
	}
	
	public void show(String defaultValue) {
		if (defaultValue != null)
			this.defaultValue = defaultValue;
		MainUtil.client.setScreen(this);
	}
	public void show() {
		show(null);
	}
	
	@Override
	protected void init() {
		parent.init(client, width, height);
		this.clearChildren();
		
		String prevValue = value == null ? (defaultValue == null ? "" : defaultValue) : value.getText();
		value = new TextFieldWidget(textRenderer, width / 2 - 104, height / 2 - 20, 208, 16, TextInst.of(""));
		value.setMaxLength(Integer.MAX_VALUE);
		value.setText(prevValue);
		this.addSelectableChild(value);
		setInitialFocus(value);
		
		ok = this.addDrawableChild(MVMisc.newButton(width / 2 - 104, height / 2 + 4, 100, 20, TextInst.translatable("nbteditor.ok"), btn -> {
			if (valueValidator.test(value.getText())) {
				client.setScreen(parent);
				valueConsumer.accept(value.getText());
			}
		}));
		this.addDrawableChild(MVMisc.newButton(width / 2 + 4, height / 2 + 4, 100, 20, TextInst.translatable("nbteditor.cancel"), btn -> {
			client.setScreen(parent);
		}));
		
		value.setChangedListener(str -> ok.active = valueValidator.test(str));
		ok.active = valueValidator.test(value.getText());
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		parent.render(matrices, -314, -314, delta);
		
		matrices.push();
		matrices.translate(0.0, 0.0, 500.0);
		super.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		value.render(matrices, mouseX, mouseY, delta);
		MainUtil.renderLogo(matrices);
		matrices.pop();
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		value.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE)
			close();
		if (keyCode == GLFW.GLFW_KEY_ENTER && ok.active) {
			client.setScreen(parent);
			valueConsumer.accept(value.getText());
			return true;
		}
		
		boolean output = !this.value.keyPressed(keyCode, scanCode, modifiers) && !this.value.isActive()
				? super.keyPressed(keyCode, scanCode, modifiers) : true;
		if (client.currentScreen != this)
			client.setScreen(parent);
		return output;
	}
	
}
