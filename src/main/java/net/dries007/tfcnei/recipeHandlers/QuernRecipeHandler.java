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
import com.bioxx.tfc.Reference;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.api.Crafting.QuernManager;
import com.bioxx.tfc.api.Crafting.QuernRecipe;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

/**
 * @author Dries007
 */
public class QuernRecipeHandler extends TemplateRecipeHandler
{
    private static List<QuernRecipe> recipeList;

    @Override
    public String getRecipeName()
    {
        return "Quern";
    }

    @Override
    public String getGuiTexture()
    {
        return Reference.ModID + ":" + Reference.AssetPathGui + "gui_quern.png";
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "quern";
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(61, 9, 18, 18), "quern"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals("quern") && getClass() == QuernRecipeHandler.class)
        {
            for (QuernRecipe recipe : recipeList) arecipes.add(new CachedQuernRecipe(recipe));
        }
        else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (QuernRecipe recipe : recipeList)
            if (recipe.getResult().isItemEqual(result))
                arecipes.add(new CachedQuernRecipe(recipe));
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        for (QuernRecipe recipe : recipeList)
            if (ingredient.getItem() == TFCItems.Quern || recipe.getInItem().isItemEqual(ingredient))
                arecipes.add(new CachedQuernRecipe(recipe));
    }

    @Override
    public TemplateRecipeHandler newInstance()
    {
        if (recipeList == null) recipeList = ReflectionHelper.getPrivateValue(QuernManager.class, QuernManager.getInstance(), "recipes");
        return super.newInstance();
    }

    public class CachedQuernRecipe extends CachedRecipe
    {
        PositionedStack ingred;
        PositionedStack result;

        public CachedQuernRecipe(ItemStack ingred, ItemStack result)
        {
            this.ingred = new PositionedStack(ingred, 61, 36);
            this.result = new PositionedStack(result, 88, 36);
        }

        public CachedQuernRecipe(QuernRecipe recipe)
        {
            this(recipe.getInItem(), recipe.getResult());
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }

        @Override
        public PositionedStack getIngredient()
        {
            return ingred;
        }

        @Override
        public PositionedStack getOtherStack()
        {
            return new PositionedStack(new ItemStack(TFCItems.Quern), 88, 9);
        }
    }
}