/*
 * Metropolitan State University of Denver
 * Author: Thyago Mota
 * Description: script to parse runestone reading progress log files
 */

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import util.control.Breaks._

object RunestoneMain {

  val DATA_FOLDER = "data"
  val COURSE_ID   = "20FCS1050-001"
  var SID: String = null

  def main(args: Array[String]) = {

    // command-line validation
    if (args.length != 1 && args.length != 2) {
      println("Missing the assignment name!")
      System.exit(1)
    }
    val asg = new Assignment(args(0))
    asg.load()
    if (args.length == 2)
      SID = args(1)

    // prepare students map
    val students = Map[String, ArrayBuffer[(String, String)]]()

    // read file with ALL of the activities
    val in = Source.fromFile(DATA_FOLDER + "/data_for_" + COURSE_ID + ".csv")
    var count = 0
    for (line <- in.getLines()) {
      count += 1
      breakable {
        if (count == 1)
          break
        val fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
        if (fields.length != 8)
          break
        // val timestamp = fields(2)
        val sid = fields(1)
        if (SID != null && !SID.equals(sid))
          break
        val completed = students.get(sid) match {
          case Some(value) => value
          case None => val temp = new ArrayBuffer[(String, String)]()
            students.put(sid, temp)
            temp
        } // end "match"
        val event = fields(3)
        val compl = fields(4)
        if (event.equals("video") && !compl.equals("complete"))
          break
        if (event.equals("unittest") && !compl.contains("percent:100.00"))
          break
        val act   = fields(5)
        val item = (event, act)
        if (asg.contains(item) && !completed.contains(item))
          completed.addOne(item)
      } // end "breakable"
    } // end for "(line <- in.getLines())"
    in.close()

    // output results
    for ((sid, completed) <- students) {
      val points = Math.min(Math.round(completed.length / asg.total().toDouble * 10), 10)
      println(f"${sid},${points}")
      if (SID != null)
        println("\t*** completed ***")
        for (item <- completed)
          println("\t" + item)
        println("\t*** missing ***")
        for (item <- asg.getItems())
          if (!completed.contains(item))
            println("\t" + item)
    }
  } // end main
} // end object
