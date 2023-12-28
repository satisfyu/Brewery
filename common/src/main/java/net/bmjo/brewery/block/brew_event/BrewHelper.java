package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.brewingstation.BrewKettleBlock;
import net.bmjo.brewery.block.entity.BrewstationBlockEntity;
import net.bmjo.brewery.block.property.Liquid;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BrewHelper {

    @Nullable
    public static BlockState getState(Class<?> clazz, Set<BlockPos> components, Level level){
        BlockPos pos = getBlock(clazz, components, level);

        return pos == null ? null : level.getBlockState(pos);
    }

    @Nullable
    public static BlockState getState(Block block, Set<BlockPos> components, Level level){
        BlockPos pos = getBlock(block, components, level);

        return pos == null ? null : level.getBlockState(pos);
    }

    @Nullable
    public static BlockPos getBlock(Block block, Set<BlockPos> components, Level level) {
        for (BlockPos pos : components) {
            if (level.getBlockState(pos).getBlock() == block) {
                return pos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlock(Class<?> clazz, Set<BlockPos> components, Level level) {
        for (BlockPos pos : components) {
            if (clazz.isInstance(level.getBlockState(pos).getBlock())) {
                return pos;
            }
        }
        return null;
    }

    public static List<BrewEvent> possibleEvents(BrewstationBlockEntity entity) {
        List<BrewEvent> possibleEvents = new ArrayList<>();
        Set<BrewEvent> runningEvents = entity.getRunningEvents();

        for (BrewEvent event : BrewEvents.BREW_EVENTS.values()) {
            boolean canAdd = true;

            // Check if the event is already running
            if (runningEvents.contains(event)) {
                canAdd = false;
            } else {
                // Check for conflicts with currently running events
                for (BrewEvent runningEvent : runningEvents) {
                    if (conflictsWith(event, runningEvent)) { // Adjust this condition based on your event conflict logic
                        canAdd = false;
                        break; // No need to check further running events if there's a conflict
                    }
                }
            }

            if (canAdd) {
                possibleEvents.add(event);
            }
        }
        return possibleEvents;
    }

    private static boolean conflictsWith(BrewEvent event1, BrewEvent event2) {
        ResourceLocation id1 = BrewEvents.getId(event1);
        ResourceLocation id2 = BrewEvents.getId(event2);

        return (id1.equals(BrewEvents.WHISTLE_EVENT) && id2.equals(BrewEvents.KETTLE_EVENT)) ||
                (id1.equals(BrewEvents.KETTLE_EVENT) && id2.equals(BrewEvents.WHISTLE_EVENT));
    }

    public static BrewEvent getRdmEvent(BrewstationBlockEntity entity) {
        List<BrewEvent> possibleEvents = possibleEvents(entity);
        BrewEvent event = possibleEvents.get(entity.getLevel().getRandom().nextInt(possibleEvents.size()));
        event.setTime(entity.getLevel().getRandom().nextInt(8 * 20,20 * 20));
        return event;
    }

    public static void finishEvents(BrewstationBlockEntity entity){
        Set<BrewEvent> eventSet = entity.getRunningEvents();
        if(eventSet.isEmpty()) return;
        Iterator<BrewEvent> iterator = eventSet.iterator();
        while (iterator.hasNext()) {
            BrewEvent event = iterator.next();
            if(event == null) continue;
            endEvent(entity, iterator, event);
        }
    }

    public static void checkRunningEvents(BrewstationBlockEntity entity){
        Set<BrewEvent> eventSet = entity.getRunningEvents();
        if(eventSet.isEmpty()) return;
        Iterator<BrewEvent> iterator = eventSet.iterator();
        while (iterator.hasNext()) {
            BrewEvent event = iterator.next();
            if(event != null){
                event.tick(entity);
                if(event.isFinish(entity.getComponents(), entity.getLevel())){
                    endEvent(entity, iterator, event);
                    entity.growSolved();
                }
                else if (event.getTimeLeft() <= 0) {
                    endEvent(entity, iterator, event);
                }
            }
        }
    }

    public static void endEvent(BrewstationBlockEntity entity, Iterator<BrewEvent> iterator, BrewEvent event) {
        Brewery.LOGGER.warn("ending event! " + BrewEvents.getId(event));
        if (event == null) return;
        event.finish(entity.getComponents(), entity.getLevel());
        iterator.remove();
    }

    public static void saveAdditional(BrewstationBlockEntity entity, CompoundTag compoundTag) {
        Set<BrewEvent> events = entity.getRunningEvents();
        if (events.isEmpty()) return;
        ListTag list = new ListTag();
        for(BrewEvent event : events){
            CompoundTag tag = event.save(new CompoundTag());
            tag.putString("id", BrewEvents.getId(event).toString());
            tag.putInt("timeLeft", event.getTimeLeft());
            list.add(tag);
        }
        compoundTag.put("runningEvents", list);
    }

    public static void load(BrewstationBlockEntity entity, CompoundTag compoundTag) {
        if (!compoundTag.contains("runningEvents")) return;
        ListTag list = compoundTag.getList("runningEvents", Tag.TAG_COMPOUND);
        for(Tag tag : list){
            if(tag instanceof CompoundTag cTag){
                String id = cTag.getString("id");
                ResourceLocation location = ResourceLocation.tryParse(id);
                BrewEvent type = BrewEvents.byId(location);
                if(type == null) continue;
                type.load(compoundTag);
                type.setTime(cTag.getInt("timeLeft"));
                entity.getRunningEvents().add(type);
            }
        }
    }

    public static void resetWater(Set<BlockPos> components, Level level){
        if (components == null || level == null) return;
        BlockPos basinPos = BrewHelper.getBlock(BrewKettleBlock.class, components, level);
        if (basinPos != null) {
            BlockState basinState = level.getBlockState(basinPos);
            Liquid liquid = basinState.getValue(BlockStateRegistry.LIQUID);
            if(liquid.equals(Liquid.FILLED)) return;
            level.setBlockAndUpdate(basinPos, basinState.setValue(BlockStateRegistry.LIQUID, Liquid.FILLED));
        }
    }
}
