/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package uk.akkiserver.immersivecookfarm.data.models;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;

public abstract class TRSRItemModelProvider extends ModelProvider<TRSRModelBuilder> {
	public TRSRItemModelProvider(PackOutput output, ExistingFileHelper existing) {
		super(output, ImmersiveCookFarm.MODID, ITEM_FOLDER, TRSRModelBuilder::new, existing);
	}
}
