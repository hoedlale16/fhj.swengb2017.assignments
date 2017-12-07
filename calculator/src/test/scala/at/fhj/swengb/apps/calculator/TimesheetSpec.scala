package at.fhj.swengb.apps.calculator


import java.nio.file._
import scala.collection.JavaConverters._

import org.scalatest.WordSpecLike

class TimesheetSpec extends WordSpecLike {

  private val filePath: Path = Paths.get("C:\\workspace\\fhj.swengb2017.assignments\\calculator\\timesheet-calculator.adoc")

  private val fileContent: Seq[String] = Files.readAllLines(filePath).asScala

  private val expectedContent =
    """== Time expenditure: Calculator assignment
      |
      |[cols="1,1,4", options="header"]
      |.Time expenditure
      ||===
      || Date
      || Hours
      || Description
      || 29.11.17
      || 1
      || Review requirement document and code
      || 29.11.17
      || 3
      || Design GUI
      || 29.11.17
      || 1
      || Implement initial Controller calls from GUI (test print "Hello") on Console
      || 30.11.17
      || 1
      || Implement Controller to handle number buttons(incl. Comma)
      || 05.12.17
      || 4
      || Implement Polish Notation Enginge
      ||===
      |
      |"[== Time expenditure: Calculator assignment
      |
      |[cols="1,1,4", options="header"]
      |.Time expenditure
      ||===
      || Date
      || Hours
      || Description
      || 29.11.17
      || 1
      || Review requirement document and code
      || 29.11.17
      || 3
      || Design GUI
      || 29.11.17
      || 1
      || Implement initial Controller calls from GUI (test print "Hello") on Console
      || 30.11.17
      || 1
      || Implement Controller to handle number buttons(incl. Comma)
      || 05.12.17
      || 4
      || Implement Polish Notation Enginge
      |===""".stripMargin


  "Timesheet Spec" should {
    "This is always true" in {
      assert(true)
    }

    "timesheet-calculator" should {
      "not be the same like content" in {
        println(fileContent.mkString("\n"))
        assert(fileContent.mkString("\n") == expectedContent)
      }
    }
  }

}
