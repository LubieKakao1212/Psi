/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 * 
 * Psi is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [30/01/2016, 16:42:31 (GMT)]
 */
package vazkii.psi.client.render.entity;

import java.awt.Color;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.client.core.handler.ShaderHandler;
import vazkii.psi.common.entity.EntitySpellCircle;
import vazkii.psi.common.lib.LibResources;

public class RenderSpellCircle extends Render<EntitySpellCircle> {

	private static final ResourceLocation[] layers = new ResourceLocation[] {
			new ResourceLocation(String.format(LibResources.MISC_SPELL_CIRCLE, 0)),
			new ResourceLocation(String.format(LibResources.MISC_SPELL_CIRCLE, 1)),
			new ResourceLocation(String.format(LibResources.MISC_SPELL_CIRCLE, 2)),
		};
	
	public RenderSpellCircle(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySpellCircle entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		float s = 1F / 16F;
		
		float alive = entity.getTimeAlive() + partialTicks;
		float s1 = Math.min(1F, alive / EntitySpellCircle.CAST_DELAY);
		if(alive > EntitySpellCircle.LIVE_TIME - EntitySpellCircle.CAST_DELAY)
			s1 = 1F - Math.min(1F, Math.max(0, (alive - (EntitySpellCircle.LIVE_TIME - EntitySpellCircle.CAST_DELAY))) / EntitySpellCircle.CAST_DELAY);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - s1 * 2, y + 0.01, z - s1 * 2);
		GlStateManager.scale(s, s, s);
		GlStateManager.scale(s1, 1F, s1);
		GlStateManager.rotate(90F, 1F, 0F, 0F);
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		ShaderHandler.useShader(ShaderHandler.rawColor);
		
    	int colorVal = ICADColorizer.DEFAULT_SPELL_COLOR;
    	ItemStack colorizer = entity.getDataWatcher().getWatchableObjectItemStack(20);
    	if(colorizer != null && colorizer.getItem() instanceof ICADColorizer)
    		colorVal = ((ICADColorizer) colorizer.getItem()).getColor(colorizer);

		for(int i = 0; i < layers.length; i++) {
			Color color = new Color(colorVal);
			if(i == 2)
				color = color.brighter();
			
			float r = (float) color.getRed() / 255F;
			float g = (float) color.getGreen() / 255F;
			float b = (float) color.getBlue() / 255F;
			
			float d = 2F / s;
			GlStateManager.pushMatrix();
			GlStateManager.translate(d, d, 0F);
			float rot = alive;
			if(i == 0)
				rot = -rot;
			GlStateManager.rotate(rot, 0F, 0F, 1F);
			GlStateManager.translate(-d, -d, 0F);
			
			if(i == 1)
				GlStateManager.color(1F, 1F, 1F);
			else GlStateManager.color(r, g, b);
			
			renderManager.renderEngine.bindTexture(layers[i]);
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 64, 64, 64, 64);
			GlStateManager.popMatrix();
			GlStateManager.translate(0F, 0F, -0.5F);
		}
		
		ShaderHandler.releaseShader();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpellCircle entity) {
		return null;
	}

}