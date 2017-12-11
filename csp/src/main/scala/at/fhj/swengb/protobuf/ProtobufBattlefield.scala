package at.fhj.swengb.protobuf

import java.nio.file.{Files, Paths}

import at.fhj.swengb.protobuf.BattlefieldOuterClass.Battlefield
import at.fhj.swengb.protobuf.BattlefieldOuterClass.Battlefield.{Position, Vessel, VesselOrientation, VesselType}

import scala.collection.JavaConverters._

object ProtobufBattlefield {

  val filePath: String = "target/battefield.bin"

  def main(args: Array[String]): Unit = {

    //Create already clicked positions
    val clickedPos1: Position = Position.newBuilder().setX(0).setY(0).build()
    val clickedPos2: Position = Position.newBuilder().setX(0).setY(0).build()
    val clickedPos3: Position = Position.newBuilder().setX(4).setY(7).build()
    val clickedPos4: Position = Position.newBuilder().setX(3).setY(5).build()
    val clickedPos5: Position = Position.newBuilder().setX(2).setY(1).build()

    val clickedPosistions = Set(clickedPos1,clickedPos2,clickedPos3,clickedPos4,clickedPos5)

    //Creates my Fleet
    val myBattleship: Vessel = createProtobufVessel("Battleship",VesselType.Battleship,VesselOrientation.Horizontal,1,1)
    val myCruiser: Vessel = createProtobufVessel("Cruiser",VesselType.Cruiser,VesselOrientation.Vertical,4,5)
    val myDestroyer: Vessel = createProtobufVessel("Destroyer",VesselType.Destroyer,VesselOrientation.Horizontal,3,2)
    val mySubmarine: Vessel = createProtobufVessel("Submarine",VesselType.Submarine,VesselOrientation.Horizontal,7,7)

    val fleet: Seq[Vessel] = Seq(myBattleship,myCruiser,myDestroyer,mySubmarine)

    //Finally create Battlefiled to store and write it to dics
    val battlefield: Battlefield = createProtobufBattlefiled(10,10,clickedPosistions,fleet)
    writeProtobufTo(battlefield,filePath)

    //---------------------------------------------


    // reads data from disc and prints output
    val bfIn: Battlefield = readFromProtobuf(filePath)
    println(
      "read Battflefield-Size: " + bfIn.getFieldWidth + "/" + bfIn.getFieldHeight)
    println("HitPos: ")
    bfIn.getClickedPositionsList.asScala.foreach(e =>
      println(e.getX + "/" + e.getY))

    println("Vessels: ");
    bfIn.getVesselsList.asScala.foreach(e =>
      println(e.getName + "[" + e.getType + "] - StartPos: (" +
              e.getStartPos.getX + "/" + e.getStartPos.getY + ") - Orientation: " + e.getOrientation))

  }


  /**
    * Creates a new Object of Battlefield to store with Protobuf API
    * @param fieldWidth => Width of battlefile
    * @param fieldHeight => Height of battlefile
    * @param clickedPos Set of already clicked positions
    * @param vessels List of vessels from battlefield
    * @return Storeable object for protobuf API
    */
  private def createProtobufBattlefiled(fieldWidth: Int,
                               fieldHeight: Int,
                               clickedPos: Set[Position],
                               vessels: Seq[Vessel]): Battlefield = {

    //Create new Object battlefile
    val battlefield = BattlefieldOuterClass.Battlefield
      .newBuilder()
      .setFieldWidth(fieldWidth)
      .setFieldHeight(fieldHeight)

    //Add Clicked positions
    clickedPos.foreach(e => battlefield.addClickedPositions(e))

    //Add Vessesls
    vessels.foreach(e => battlefield.addVessels(e))


    //Build battlefield and return
    battlefield.build()
  }


  private def createProtobufVessel(name: String,
                                   vesselType: VesselType,
                                   orientation: VesselOrientation,
                                   startX: Int,
                                   startY: Int) = {
    Vessel
      .newBuilder()
      .setName(name)
      .setType(vesselType)
      .setOrientation(orientation)
      .setStartPos(Position.newBuilder().setX(startX).setY(startY).build())
      .build()
  }

  /**
    * Write Protobuf-Battlefield to given path
    * @param battlefield - Battlefield instance to write to
    * @param path filepath to write to
    */
  private def writeProtobufTo(battlefield: Battlefield, path: String) = {
    val target = Paths.get(path)
    println("wrote to: " + target.toAbsolutePath.toString)
    battlefield.writeTo(Files.newOutputStream(target))
  }

  /**
    * Read binary protobuf-File with API
    * @param path - Path from file to read from
    * @return
    */
  private def readFromProtobuf(path: String): Battlefield = {
    val target = Paths.get(path)
    println("read from: " + target.toAbsolutePath.toString)
    Battlefield.parseFrom(Files.newInputStream(target))
  }
}