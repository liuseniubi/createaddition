package com.mrh0.createaddition.blocks.furnace_burner;

import java.util.Random;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class FurnaceBurner extends AbstractFurnaceBlock implements ITE<FurnaceBurnerTileEntity> {

	public FurnaceBurner(Properties props) {
		super(props);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return CATileEntities.FURNACE_BURNER.create();
	}

	@Override
	protected void openContainer(World world, BlockPos pos, PlayerEntity player) {
		if(world.isClientSide())
			return;
		TileEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof FurnaceBurnerTileEntity) {
			FurnaceBurnerTileEntity fbte = (FurnaceBurnerTileEntity) tileentity;
			ItemStack currentStack = fbte.getItem(FurnaceBurnerTileEntity.FUEL_SLOT);
			if(player.isShiftKeyDown()) {
				if(currentStack.getCount() > 0) {
					InventoryHelper.dropItemStack(world, player.position().x, player.position().y, player.position().z, currentStack.copy());
					fbte.removeItemNoUpdate(FurnaceBurnerTileEntity.FUEL_SLOT);
				}
				
				/*int i = currentStack.getCount();
				for(int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
					if(i <= 0)
						return;
					ItemStack slotStack = player.inventory.getStackInSlot(slot);
					if(slotStack.getItem() == currentStack.getItem()) {
						int x = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), currentStack.getCount());
						player.inventory.setInventorySlotContents(slot, new ItemStack(slotStack.getItem(), slotStack.getCount() + x));
						i-=x;
					}
				}
				if(i > 0)
					InventoryHelper.spawnItemStack(world, player.getPositionVec().x, player.getPositionVec().y, player.getPositionVec().z, new ItemStack(currentStack.getItem(), i));
				*/
				return;
			}
			
			ItemStack heald = player.getMainHandItem();
			if(!fbte.canPlaceItem(FurnaceBurnerTileEntity.FUEL_SLOT, heald))
				return;
			
			if(currentStack.isEmpty()) {
				fbte.setItem(FurnaceBurnerTileEntity.FUEL_SLOT, heald.copy());
				heald.setCount(0);
				return;
			}
			
			if(heald.getItem() != currentStack.getItem())
				return;
			
			ItemStack newStack = new ItemStack(currentStack.getItem(), Math.min(currentStack.getCount() + heald.getCount(), currentStack.getMaxStackSize()));
			heald.setCount(Util.getMergeRest(heald, currentStack));
			
			fbte.setItem(FurnaceBurnerTileEntity.FUEL_SLOT, newStack);
		}

	}

	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (!world.isClientSide())
			return;
		if (state.getValue(LIT)) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D)
				world.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F,
						false);

			Direction direction = state.getValue(FACING);
			Direction.Axis direction$axis = direction.getAxis();
			//double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
			double d6 = rand.nextDouble() * 6.0D / 16.0D;
			double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
			world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Class<FurnaceBurnerTileEntity> getTileEntityClass() {
		return FurnaceBurnerTileEntity.class;
	}
}
