/*
 * Copyright (c) 2014 Dries007
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the
 * disclaimer below) provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 *
 *  * Neither the name of Dries007 nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
 * GRANTED BY THIS LICENSE.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.dries007.tfcnei.recipeHandlers;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.dunk.tfc.Items.ItemClothingPiece;
import com.dunk.tfc.Items.ItemFlatGeneric;
import com.dunk.tfc.Items.ItemLeather;
import com.dunk.tfc.Items.ItemLooseRock;
import com.dunk.tfc.Reference;
import com.dunk.tfc.api.Crafting.CraftingManagerTFC;
import com.dunk.tfc.api.Crafting.ShapedRecipesTFC;
import com.dunk.tfc.api.TFCItems;
import net.dries007.tfcnei.util.Helper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dries007
 */
public class KnappingRecipeHandler extends TemplateRecipeHandler
{
    private static List<ShapedRecipesTFC> recipeList;
    private static List<ItemLeather> clothItems;

    @Override
    public String getGuiTexture()
    {
        return Reference.MOD_ID + ":" + Reference.ASSET_PATH_GUI + "gui_knapping.png";
    }

    @Override
    public String getRecipeName()
    {
        return "Knapping";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "knapping";
    }
    

    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null)
        {
        	clothItems = new ArrayList<ItemLeather>();
			for(Object io : Item.itemRegistry) {
				if (ItemLeather.class.isInstance(io)) {
					clothItems.add((ItemLeather)io);
				}
			}
            recipeList = new ArrayList<>();
            List<IRecipe> allRecipes = CraftingManagerTFC.getInstance().getRecipeList();
            for (IRecipe recipe : allRecipes)
                if (recipe.getRecipeSize() > 9 && recipe instanceof ShapedRecipesTFC) // Filter out junk for optimisation. All knapping recipes are > 9 and are shaped
                {
                	ShapedRecipesTFC recipeTFC = (ShapedRecipesTFC)recipe; 
                    ItemStack[] inputs = recipeTFC.getRecipeItems(); // Get inputs
                    for (ItemStack inStack : inputs)
                    {
                        if (inStack == null) continue; // Loop over until we find a not null entry
                        if (!(inStack.getItem() instanceof ItemFlatGeneric)) break; // If its not a flat type item, break out now
                        recipeList.add(recipeTFC);
                        break;
                    }
                }
        }
        return super.newInstance();
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(0, 0, 5 * 16, 5 * 16), "knapping"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("knapping") && getClass() == KnappingRecipeHandler.class)
        {
            for (ShapedRecipesTFC recipe : recipeList)
                arecipes.add(new CachedKnappingRecipe(recipe));
        }
        else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (ShapedRecipesTFC recipe : recipeList)
        {
            if (Helper.areItemStacksEqual(result, recipe.getRecipeOutput())) arecipes.add(new CachedKnappingRecipe(recipe));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        Item ingItem = ingredient.getItem();
    	Item flatType = null; 
    	if ((ingItem instanceof ItemLooseRock)) {
    		flatType = ((ItemLooseRock) ingItem).getSpecialCraftingType();	
        } else {
        	for (ItemLeather cloth : clothItems) {
				if( ingItem == cloth) {
					flatType = cloth.getSpecialCraftingType();
					break;
				} else if( ingItem == cloth.getSpecialCraftingType()) {
					flatType = ingItem;
					break;
				}
        	}
        } 
    	if (flatType == null) return;
        
        for (ShapedRecipesTFC recipe : recipeList)
        {
            for (ItemStack inStack : recipe.getRecipeItems())
            {
                if (inStack == null || flatType != inStack.getItem()) continue;
                if (flatType == TFCItems.flatClay)
                {
                    if (ingredient.getItemDamage() == 0 && inStack.getItemDamage() == 1) // Compare to see if the ingredient is normal clay
                        arecipes.add(new CachedKnappingRecipe(recipe));
                    else if (ingredient.getItemDamage() == 1 && inStack.getItemDamage() == 3) // Compare to see if the ingredient is fire clay
                        arecipes.add(new CachedKnappingRecipe(recipe));
                }
                
                else if(ingItem instanceof ItemLeather) {
                	//in the case of hides we want everything
                	arecipes.add(new CachedKnappingRecipe(recipe));
                }
                else if (inStack.getItemDamage() == Short.MAX_VALUE || ingredient.getItemDamage() == inStack.getItemDamage()) {
                	// In this case match damage value of stone too.
                	arecipes.add(new CachedKnappingRecipe(recipe));
                }
                break;
            }
        }
    }

    public class CachedKnappingRecipe extends CachedRecipe
    {
        final List<PositionedStack> inputs;
        final PositionedStack result;
        PositionedStack actualInput;

        public CachedKnappingRecipe(ShapedRecipesTFC recipe)
        {
            int W = recipe.getRecipeWidth();
            int H = recipe.getRecipeHeight();
            ItemStack off = null;
            ItemStack[] inputItems = recipe.getRecipeItems();
            for (ItemStack inStack : inputItems)
            {
                if (inStack == null) continue;
                if (inStack.getItem() == TFCItems.flatClay) // if its clay, we need some special code
                {
                    if (inStack.getItemDamage() == 1) // Normal clay (damage value 1)
                    {
                        off = new ItemStack(inStack.getItem(), 1, 0); // Dark texture has data value 0
                        setActualInput(new ItemStack(TFCItems.clayBall, 5, 0));
                    }
                    else if (inStack.getItemDamage() == 3) // Fire clay (damage value 3)
                    {
                        off = new ItemStack(inStack.getItem(), 1, 2); // Dark texture has data value 3
                        setActualInput(new ItemStack(TFCItems.clayBall, 5, 1));
                    }
                }
                else if (inStack.getItem() == TFCItems.flatRock) 
                {
                	setActualInput(new ItemStack(TFCItems.looseRock, 1, inStack.getItemDamage()));
                }
                else
                {
					for (ItemLeather cloth : clothItems) {
						if( inStack.getItem() == cloth.getSpecialCraftingType())
						{
							if( cloth.getHasSizes() )
							{
								//gen size permutations, not that you'd ever use a large....
								ItemStack[] sizes = new ItemStack[] { 
									new ItemStack(cloth, 1, 0),
									new ItemStack(cloth, 1, 1),
									new ItemStack(cloth, 1, 2)
								};

								setActualInput(sizes);

							}
							else
							{
								//Just the one
								setActualInput(new ItemStack(cloth));
							}
							
							break;
						}
						
					}
                }
                break;
            }
            this.inputs = new ArrayList<>();
            for (int h = 0; h < H; h++)
                for (int w = 0; w < W; w++)
                {
                    if (inputItems[h * W + w] != null) this.inputs.add(new PositionedStack(inputItems[h * W + w], 16 * w, 16 * h));
                    else if (off != null) this.inputs.add(new PositionedStack(off, 16 * w, 16 * h));
                }
            this.result = new PositionedStack(recipe.getRecipeOutput(), 123, 33);
        }

        @Override
        public List<PositionedStack> getIngredients()
        {
        	return inputs;
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public PositionedStack getOtherStack()
        {
            if (actualInput != null) {
                actualInput.setPermutationToRender(cycleticks / 24 % actualInput.items.length);
            }

            return actualInput;
        }

        public void setActualInput(ItemStack itemStack)
        {
            actualInput = new PositionedStack(itemStack, 123, 10);
        }
        public void setActualInput(ItemStack[] altItemStack)
        {
            actualInput = new PositionedStack(altItemStack, 123, 10, true);
        }
    }
}
