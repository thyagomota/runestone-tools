/*
 * Metropolitan State University of Denver
 * Author: Thyago Mota
 * Description: defines an assignment
 */

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Assignment(private var name: String) {

  val items = new ArrayBuffer[(String, String)]()

  def load() = {
    val in = Source.fromFile(Assignment.ASSIGNMENTS_FOLDER + "/" + name)
    for (line <- in.getLines()) {
      val fields = line.split(",")
      items.addOne((fields(0), fields(1)))
    }
    in.close()
  }

  def contains(item: (String, String)): Boolean = {
    for (item_ <- items) {
      if (item.equals(item_))
        return true
    }
    false
  }

  def total() = items.length

  def getItems() = items

  override def toString: String = {
    var out = name + "\n"
    for ((event, act) <- items) {
      out += "(" + event + "," + act + ")\n"
    }
    out
  }
}

object Assignment {

  val ASSIGNMENTS_FOLDER = "assignments"

  def main(args: Array[String]) = {
    val asg = new Assignment("read_01")
    asg.load()
    print(asg)
    if (asg.contains(("activecode", "lcfc1")))
      println("Found!")
    else
      println("Not found!")
  }
}


