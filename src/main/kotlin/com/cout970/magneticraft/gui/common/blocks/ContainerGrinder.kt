package com.cout970.magneticraft.gui.common.blocks

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.util.misc.IBD
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 11/07/2016.
 */
class ContainerGrinder(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileGrinder>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let {
            for (i in 0 until 4) {
                addSlotToContainer(InSlotItemHandler(inv, i, 102 + 20 * i, 16))
            }
            addSlotToContainer(OutSlotItemHandler(inv, 4, 102, 48))
            addSlotToContainer(OutSlotItemHandler(inv, 5, 102, 66))
            addSlotToContainer(OutSlotItemHandler(inv, 6, 120, 48))
            addSlotToContainer(OutSlotItemHandler(inv, 7, 120, 66)
            )
        }
        bindPlayerInventory(player.inventory)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? = null

    override fun sendDataToClient(): IBD? {
        val data = IBD()
        tile!!
        data.setDouble(DATA_ID_VOLTAGE, tile.node.voltage)
        data.setFloat(DATA_ID_BURNING_TIME, tile.craftingProcess.timer)
        data.setInteger(DATA_ID_REDSTONE_POWER, tile.redPower)
        data.setFloat(DATA_ID_MACHINE_PRODUCTION, tile.production.average)
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getDouble(DATA_ID_VOLTAGE, { tile.node.voltage = it })
        ibd.getFloat(DATA_ID_BURNING_TIME, { tile.craftingProcess.timer = it })
        ibd.getInteger(DATA_ID_REDSTONE_POWER, { tile.redPower = it })
        ibd.getFloat(DATA_ID_MACHINE_PRODUCTION, { tile.production.storage = it })
    }


    class InSlotItemHandler(slot: IItemHandler, index: Int, xPosition: Int, yPosition: Int) : SlotItemHandler(slot, index, xPosition, yPosition) {
        override fun isItemValid(stack: ItemStack?): Boolean {
            return true
        }

        override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
            return true
        }
    }

    class OutSlotItemHandler(slot: IItemHandler, index: Int, xPosition: Int, yPosition: Int) : SlotItemHandler(slot, index, xPosition, yPosition) {
        override fun isItemValid(stack: ItemStack?): Boolean {
            return false
        }

        override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
            return true
        }
    }
}