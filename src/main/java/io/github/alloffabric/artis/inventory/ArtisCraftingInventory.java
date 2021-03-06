package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;

import java.util.Optional;

public class ArtisCraftingInventory extends CraftingInventory {
	private final DefaultedList<ItemStack> stacks;
	private ArtisCraftingController container;

	public ArtisCraftingInventory(ArtisCraftingController container, int width, int height) {
		super(container, width, height);
		this.stacks = DefaultedList.ofSize((width * height) + 1, ItemStack.EMPTY);
		this.container = container;
	}

	@Override
	public int getInvSize() {
		return stacks.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return stacks.get(slot);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return Inventories.removeStack(stacks, slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(this.stacks, slot, amount);
		if (!stack.isEmpty()) {
			this.container.onContentChanged(this);
		}

		return stack;
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		this.container.onContentChanged(this);
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack stack : stacks) {
			finder.addNormalItem(stack);
		}
	}

	public ItemStack getCatalyst() {
		return getInvStack(getWidth() * getHeight());
	}

	public RecipeType getType() {
        Optional<CraftingRecipe> opt = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(container.getTableType(), container.getCraftInv(), getPlayer().getEntityWorld());
        Optional<CraftingRecipe> optCrafting = getPlayer().getEntityWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, container.getCraftInv(), getPlayer().getEntityWorld());
	    if (opt.isPresent()) {
            return (container).getTableType();
        } else if (optCrafting.isPresent()) {
            return RecipeType.CRAFTING;
        }
	    return (container).getTableType();
	}

	public boolean shouldCompareCatalyst() {
	    return container.getTableType().hasCatalystSlot();
    }

	public PlayerEntity getPlayer() {
		return container.getPlayer();
	}
}
