package com.cout970.magneticraft.api.energy.impl

import coffee.cypher.mcextlib.extensions.vectors.component1
import coffee.cypher.mcextlib.extensions.vectors.component2
import coffee.cypher.mcextlib.extensions.vectors.component3
import com.cout970.magneticraft.api.energy.IElectricConection
import com.cout970.magneticraft.api.energy.IElectricNode

/**
 * Created by cout970 on 11/06/2016.
 */
open class ElectricConnection(
        val a: IElectricNode,
        val b: IElectricNode
) : IElectricConection {

    override fun getFirstNode(): IElectricNode = a

    override fun getSecondNode(): IElectricNode = b

    override fun getSeparationDistance(): Double {
        val (x, y, z) = b.getPos()
        return a.getPos().getDistance(x, y, z)
    }

    override fun iterate() {
        //total resistance of the connection
        val R = (a.getResistance() + b.getResistance()) * getSeparationDistance()
        //number of iterations needed to avoid instabilities with voltages
        val times = (2/R).toInt()
        for(i in 0..times){
            //voltage difference between the conductors
            val V = a.getVoltage() - b.getVoltage()
            //Ohm's law with 'times' to divide the amperage in the different iterations of the for loop
            val I = (V / R) / times
            //moves the charge between the conductors
            //this follows the Kirchhoff's first law
            a.applyCurrent(-I)
            b.applyCurrent(I)
        }
    }
}