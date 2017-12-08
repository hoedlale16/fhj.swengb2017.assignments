package at.fhj.swengb.apps.calculator


import java.nio.file._
import scala.collection.JavaConverters._

import org.scalatest.WordSpecLike

class TimesheetSpec extends WordSpecLike {

  private val filePath: Path = Paths.get("calculator/timesheet-calculator.adoc")
  
  private val originalContent =
    """== Time expenditure: Calculator assignment
      |
      |[cols="1,1,4", options="header"]
      |.Time expenditure
      ||===
      || Date
      || Hours
      || Description
      |
      || 29.11.17
      || 1
      || Review of this and that
      |
      || 30.11.17
      || 5
      || implemented css
      |
      || 11.07.17
      || 2
      || fixed bugs
      |
      ||===""".stripMargin


  "Timesheet Spec" should {
    "timesheet-calculator" should {
      "file exists" in {
        assert(Files.exists(filePath))
      }
      "not be the same like content" in {
        val fileContent: Seq[String] = Files.readAllLines(filePath).asScala
        assert(fileContent.mkString("\n") != originalContent)
      }
    }
  }

}
