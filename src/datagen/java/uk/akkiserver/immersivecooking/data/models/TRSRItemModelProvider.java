/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package uk.akkiserver.immersivecooking.data.models;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.akkiserver.immersivecooking.ImmersiveCooking;

public abstract class TRSRItemModelProvider extends ModelProvider<TRSRModelBuilder> {
	public TRSRItemModelProvider(PackOutput output, ExistingFileHelper existing) {
		super(output, ImmersiveCooking.MODID, ITEM_FOLDER, TRSRModelBuilder::new, existing);
	}
}
