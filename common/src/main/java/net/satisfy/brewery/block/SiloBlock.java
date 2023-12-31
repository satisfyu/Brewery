package net.satisfy.brewery.block;

import de.cristelknight.doapi.common.block.FacingBlock;
import net.satisfy.brewery.entity.SiloBlockEntity;
import net.satisfy.brewery.registry.BlockEntityRegistry;
import net.satisfy.brewery.registry.ObjectRegistry;
import net.satisfy.brewery.util.silo.ConnectivityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SiloBlock extends FacingBlock implements EntityBlock {
    public static final HashMap<Item, Item> DRYERS = new HashMap<>();
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    public SiloBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(OPEN, false)
                .setValue(SHAPE, Shape.NONE)
                .setValue(FACING, Direction.NORTH));
    }

    public Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    public BlockState setFacing(BlockState state, Direction facing) {
        return state.setValue(FACING, facing);
    }

    public static void addDry(ItemLike itemLike, ItemLike resultItem) {
        if (itemLike.asItem() != Items.AIR && resultItem.asItem() != Items.AIR)
            DRYERS.put(itemLike.asItem(), resultItem.asItem());
    }

    public static void registerDryers() {
        addDry(Items.WHEAT, ObjectRegistry.DRIED_WHEAT.get());
        addDry(ObjectRegistry.CORN.get(), ObjectRegistry.DRIED_CORN.get());
        addDry(ObjectRegistry.BARLEY.get(), ObjectRegistry.DRIED_BARLEY.get());

    }

    public static boolean isDryItem(ItemStack itemStack) {
        return DRYERS.containsKey(itemStack.getItem());
    }

    public static boolean isSilo(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return (item instanceof BlockItem bi) &&
                (bi.getBlock() == ObjectRegistry.SILO_WOOD.get() ||
                        bi.getBlock() == ObjectRegistry.SILO_COPPER.get());
    }

    public static boolean isSilo(BlockState state) {
        return state.getBlock() instanceof SiloBlock;
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (level.isClientSide)
            return itemStack.isEmpty() || isDryItem(itemStack) ? InteractionResult.SUCCESS : isSilo(itemStack) || player.isDiscrete() ? InteractionResult.PASS : InteractionResult.CONSUME;
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof SiloBlockEntity siloBE)
            if (itemStack.isEmpty()) {
                if (player.isDiscrete()) {
                    ItemStack returnStack = siloBE.tryRemoveItem();
                    if (!returnStack.isEmpty())
                        player.addItem(itemStack);
                } else {
                    //TODO SET OPEN / CLOSE
                }
                return InteractionResult.SUCCESS;
            } else if (isDryItem(itemStack) && siloBE.tryAddItem(itemStack))
                return InteractionResult.SUCCESS;
        return isSilo(itemStack) || player.isDiscrete() ? InteractionResult.PASS : InteractionResult.CONSUME;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (notify)
            return;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SiloBlockEntity container)
            container.updateConnectivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean moved) {
        if (blockState.hasBlockEntity() && (blockState.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (!(be instanceof SiloBlockEntity siloBE))
                return;
            Containers.dropContents(level, blockPos, siloBE);
            //level.updateNeighbourForOutputSignal(blockPos, this);
            level.removeBlockEntity(blockPos);
            ConnectivityHandler.splitMulti(siloBE);
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return blockState2.is(this) || super.skipRendering(blockState, blockState2, direction);
    }


    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return level.getBlockEntity(blockPos, BlockEntityRegistry.SILO.get()).map(AbstractContainerMenu::getRedstoneSignalFromContainer).orElse(0);
    }

    static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0, 4, 0, 16, 16, 16); // TODO ?

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context == CollisionContext.empty())
            return CAMPFIRE_SMOKE_CLIP;
        return state.getShape(world, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, OPEN, SHAPE, FACING);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker<?>) {
                ((BlockEntityTicker<T>) blockEntity).tick(world, pos, state1, blockEntity);
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SiloBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) { //TODO ?
        if (mirror == Mirror.NONE)
            return state;
        boolean x = mirror == Mirror.FRONT_BACK;
        return switch (state.getValue(SHAPE)) {
            case NORTH -> state.setValue(SHAPE, Shape.SOUTH);
            case NORTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_WEST);
            case EAST -> state.setValue(SHAPE, Shape.WEST);
            case SOUTH_EAST -> state.setValue(SHAPE, Shape.NORTH_WEST);
            case SOUTH -> state.setValue(SHAPE, Shape.NORTH);
            case SOUTH_WEST -> state.setValue(SHAPE, Shape.NORTH_EAST);
            case WEST -> state.setValue(SHAPE, Shape.EAST);
            case NORTH_WEST -> state.setValue(SHAPE, Shape.SOUTH_EAST);
            default -> state;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        return switch (state.getValue(SHAPE)) {
            case NORTH -> state.setValue(SHAPE, Shape.EAST);
            case NORTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_EAST);
            case EAST -> state.setValue(SHAPE, Shape.SOUTH);
            case SOUTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_WEST);
            case SOUTH -> state.setValue(SHAPE, Shape.WEST);
            case SOUTH_WEST -> state.setValue(SHAPE, Shape.NORTH_WEST);
            case WEST -> state.setValue(SHAPE, Shape.NORTH);
            case NORTH_WEST -> state.setValue(SHAPE, Shape.NORTH_EAST);
            default -> state;
        };
    }


    public enum Shape implements StringRepresentable {
        NONE, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}