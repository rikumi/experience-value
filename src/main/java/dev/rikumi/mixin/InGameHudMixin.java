package dev.rikumi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	private MinecraftClient client;

	private int _lastDrawXpLevel = 0;
	private float _lastDrawXpProgress = 0f;
	private String _lastDrawXpString = "";

	@ModifyArg(
		method = "renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"),
		index = 0
	)
	private String injectGetStringWidth(String string) {
		return this.getString();
	}

	@ModifyArg(
		method = "renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
		index = 1
	)
	private String injectDrawString(String string) {
		return this.getString();
	}

	@ModifyArg(
		method = "renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
		index = 3
	)
	private int injectDrawTextY(int y) {
		return y + 4;
	}

	private String getString() {
		int xpLevel = this.client.player.experienceLevel;
		float xpProgress = this.client.player.experienceProgress;
		// some caching
		if (this._lastDrawXpLevel != xpLevel || this._lastDrawXpProgress != xpProgress) {
			int currentLevelXps = this.getExpForLevel(xpLevel);
			int nextLevelXps = this.getExpForLevel(xpLevel + 1);
			int addedXps = (int) Math.round((nextLevelXps - currentLevelXps) * xpProgress);
			this._lastDrawXpLevel = xpLevel;
			this._lastDrawXpProgress = xpProgress;
			this._lastDrawXpString = xpLevel + " (" + (currentLevelXps + addedXps) + "/" + (nextLevelXps + 1) + ")";
		}
		return this._lastDrawXpString;
	}

	// Code from https://github.com/NyaaCat/Ukit/blob/main/src/main/java/cat/nyaa/ukit/utils/ExperienceUtils.java
	private int getExpForLevel(int level) {
        if (level < 0) throw new IllegalArgumentException();
        else if (level <= 16) return (level + 6) * level;
        else if (level < 32)
            return (int)Math.round(2.5 * level * level - 40.5 * level + 360);
        else
            return (int)Math.round(4.5 * level * level - 162.5 * level + 2220);
    }
}
//  Mixin apply for mod experience-value failed experience-value.mixins.json:InGameHudMixin from mod experience-value -> net.minecraft.class_329: org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException @ModifyArg return type on net/minecraft/class_329::injectGetStringWidth must match the parameter type. ARG=Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo; RETURN=Ljava/lang/String; [PREINJECT Applicator Phase -> experience-value.mixins.json:InGameHudMixin from mod experience-value -> Prepare Injections ->  -> modify$zco000$experience-value$injectGetStringWidth(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)Ljava/lang/String; -> Prepare]
