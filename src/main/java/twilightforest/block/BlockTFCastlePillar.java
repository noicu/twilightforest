package twilightforest.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twilightforest.block.enums.CastlePillarVariant;
import twilightforest.client.ModelRegisterCallback;
import twilightforest.client.ModelUtils;
import twilightforest.item.TFItems;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockTFCastlePillar extends Block implements ModelRegisterCallback {
    public static final PropertyEnum<BlockLog.EnumAxis> AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
    public static final PropertyEnum<CastlePillarVariant> VARIANT = PropertyEnum.create("variant", CastlePillarVariant.class);

    BlockTFCastlePillar() {
        super(Material.ROCK);
        this.setHardness(100F);
        this.setResistance(15F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(TFItems.creativeTab);
        this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, CastlePillarVariant.ENCASED).withProperty(AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        ItemStack stack = placer.getHeldItem(hand);
        return this.getDefaultState()
                .withProperty(AXIS, (stack.getMetadata() & 1) == 1 ? BlockLog.EnumAxis.NONE : BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()))
                .withProperty(VARIANT, CastlePillarVariant.values()[stack.getMetadata()/2]);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal() << 2 | state.getValue(AXIS).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, CastlePillarVariant.values()[(meta & 0b1100) >> 2]).withProperty(AXIS, BlockLog.EnumAxis.values()[meta & 0b11]);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
        for (CastlePillarVariant variant : CastlePillarVariant.values()) {
            par3List.add(new ItemStack(par1, 1, variant.ordinal()*2));
            par3List.add(new ItemStack(par1, 1, (variant.ordinal()*2)+1));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel() {
        CastlePillarVariant[] variantsList = CastlePillarVariant.values();

        for (int i = 0; i < variantsList.length; i++) {
            ModelUtils.registerToState(this, i*2, this.getDefaultState()
                    .withProperty(AXIS, BlockLog.EnumAxis.Y)
                    .withProperty(VARIANT, variantsList[i]));

            ModelUtils.registerToState(this, (i*2)+1, this.getDefaultState()
                    .withProperty(AXIS, BlockLog.EnumAxis.NONE)
                    .withProperty(VARIANT, variantsList[i]));
        }
    }
}