package io.github.xrickastley.originsmath.powers;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.xrickastley.originsmath.OriginsMath;
import io.github.xrickastley.originsmath.util.InstanceValueSupplier;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class BlockLinkedResourcePower extends SuppliedLinkedVariableIntPower<CachedBlockPosition> {
	private final boolean renderBox;
	private final Vec3i offset;

	private BlockLinkedResourcePower(PowerType<?> type, LivingEntity entity, BlockProperty property, Vec3i offset, boolean renderBox) {
		super(
			type, 
			entity, 
			property, 
			() -> new CachedBlockPosition(
				entity.getWorld(),
				entity.getBlockPos().add(offset), 
				true
			)
		);

		System.out.println(property);

		this.renderBox = renderBox;
		this.offset = offset;
	}

	public boolean shouldRenderBox() {
		return renderBox;
	}

	public BlockPos getTargetBlockPos() {
		return this.entity
			.getBlockPos()
			.add(this.offset);
	}

	public static PowerFactory<?> createFactory() {
		return new PowerFactory<>(
			OriginsMath.identifier("block_linked_resource"),
			new SerializableData()
				.add("property", BlockLinkedResourcePower.BLOCK_PROPERTY)
				.add("render_box", SerializableDataTypes.BOOLEAN, false)
				.add("offset", BlockLinkedResourcePower.INTEGER_VECTOR, new Vec3i(0, 0, 0)),
			data -> (powerType, livingEntity) -> new BlockLinkedResourcePower(
				powerType, 
				livingEntity, 
				data.get("property"),
				data.get("offset"),
				data.getBoolean("render_box")
			)
		);
	}

	public static final SerializableDataType<BlockProperty> BLOCK_PROPERTY = SerializableDataType.enumValue(BlockProperty.class);
	
	public static final SerializableDataType<Vec3i> INTEGER_VECTOR = new SerializableDataType<>(
		Vec3i.class,
		(packetByteBuf, vec3i) -> {
            packetByteBuf.writeInt(vec3i.getX());
            packetByteBuf.writeInt(vec3i.getY());
            packetByteBuf.writeInt(vec3i.getZ());
        },
		packetByteBuf -> new Vec3i(
            packetByteBuf.readInt(),
            packetByteBuf.readInt(),
            packetByteBuf.readInt()
		),
        json -> {
            if (json instanceof final JsonObject jsonObj) {
                return new Vec3i(
                    JsonHelper.getInt(jsonObj, "x", 0),
                    JsonHelper.getInt(jsonObj, "y", 0),
                    JsonHelper.getInt(jsonObj, "z", 0)
                );
            } else {
                throw new JsonParseException("Expected an object with x, y, and z fields.");
            }
        },
        vec3i -> {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("x", vec3i.getX());
            jsonObj.addProperty("y", vec3i.getY());
            jsonObj.addProperty("z", vec3i.getZ());
            return jsonObj;
        }
	);

	private static enum BlockProperty implements InstanceValueSupplier<CachedBlockPosition> {
		BLAST_RESISTANCE		(cbp -> cbp.getBlockState().getBlock().getBlastResistance()),
		BLOCK_LIGHT_LEVEL		(cbp -> cbp.getWorld().getLightLevel(LightType.BLOCK, cbp.getBlockPos())),
		COMPARATOR_OUTPUT		(cbp -> cbp.getBlockState().getComparatorOutput(((World) cbp.getWorld()), cbp.getBlockPos())),
		HARDNESS				(cbp -> cbp.getBlockState().getBlock().getHardness()),
		LIGHT_LEVEL				(cbp -> cbp.getWorld().getLightLevel(cbp.getBlockPos())),
		LUMINANCE				(cbp -> cbp.getBlockState().getLuminance()),
		OPACITY					(cbp -> cbp.getBlockState().getOpacity(cbp.getWorld(), cbp.getBlockPos())),
		SKY_LIGHT_LEVEL			(cbp -> cbp.getWorld().getLightLevel(LightType.SKY, cbp.getBlockPos())),
		SLIPPERINESS			(cbp -> cbp.getBlockState().getBlock().getSlipperiness()),
		X               		(cbp -> cbp.getBlockPos().getX()),
		Y               		(cbp -> cbp.getBlockPos().getY()),
		Z               		(cbp -> cbp.getBlockPos().getZ()),
		TEST					(cbp -> {
			System.out.print(cbp.getBlockPos());
			
			return 1;
		});
		
		private final Function<CachedBlockPosition, Number> supplier;

		BlockProperty(Function<CachedBlockPosition, Number> supplier) {
			this.supplier = supplier;
		}

		public int supplyValue(CachedBlockPosition biome) {
			return supplier
				.apply(biome)
				.intValue();
		}

		public Number supplyAsNumber(CachedBlockPosition biome) {
			return supplier.apply(biome);
		}
	} 	
}
