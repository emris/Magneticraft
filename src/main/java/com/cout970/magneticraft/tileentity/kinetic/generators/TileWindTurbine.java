package com.cout970.magneticraft.tileentity.kinetic.generators;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.block.BlockProperties;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticBase;
import com.cout970.magneticraft.util.FractalLib;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileWindTurbine extends TileKineticBase {

    private int tracer;
    private byte[] rayTrace;
    private int efficiency;
    private float speed;

    @Override
    public void update() {
        super.update();
        if (getWorld().getWorldTime() % 20 == 0) {
            double wind = 10000.0d * getWindSpeed();
            int power = (int) (wind * efficiency / 5780d);
            speed = (float) (Math.sqrt(power) * 0.1d);
            traceAir1();
        }
        network.applyForce(Math.max(0, speed));
    }

    private void traceAir1() {
        int yHeight = this.tracer / 17;
        int var2 = this.tracer % 17;
        Direction rightHand = getDirection().step(Direction.UP);
        Vect3i pos = getWorldRef().getPosition();
        pos.add(getDirection().toVect3i().multiply(2));
        pos.add(rightHand.toVect3i().multiply(var2 - 8));
        pos.add(0, yHeight, 0);
        int air;

        World w = getWorld();
        for (air = 0; air < 20 && Blocks.air.equals(w.getBlockState(pos.toBlockPos()).getBlock()); ++air) {
            pos.add(getDirection());
        }

        if (this.rayTrace == null) {
            this.rayTrace = new byte[289];
        }

        this.efficiency = this.efficiency - this.rayTrace[this.tracer] + air;
        this.rayTrace[this.tracer] = (byte) air;
        ++this.tracer;

        if (this.tracer >= 289) {
            this.tracer = 0;
        }
    }

    public Direction getDirection() {
        IBlockState variant = getWorldRef().getBlockState();
        return variant.getValue(BlockProperties.ATTRIBUTE_HORIZONTAL_DIRECTIONS);
    }

    public double getWindSpeed() {
        double tot = FractalLib.noise1D(2576710L, (double) getWorld().getWorldTime() * 1.0E-4D, 0.6F, 5);
        tot = Math.max(0.0D, 1.6D * (tot - 0.5D) + 0.5D);

        if (getWorld().isThundering()) {
            return 4.0D * tot;
        }

        if (getWorld().isRaining()) {
            return 0.5D + 0.5D * tot;
        }
        return tot;
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return getDirection().opposite().matches(offset);
    }

    @Override
    public double getLoss() {
        double speed = getNetwork().getSpeed();
        return super.getLoss() + speed * speed * 0.5D * 0.05;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.AxisAlignedBB getRenderBoundingBox(){
        return Cube.fullBlock().expand(new Vect3d(5,5,5)).translate(getWorldRef().getPosition().toVect3d()).toAxisAlignedBB();
    }
}