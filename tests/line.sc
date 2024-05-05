import java.awt.Graphics
import scala.util.control.Breaks.{break, breakable}

case class Line(x1: Int, y1: Int, x2: Int, y2: Int) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    //println(s"Drawing line from ($x1, $y1) to ($x2, $y2)")
    var x = x1
    var y = -y1  // Invert y-coordinate at start
    val xEnd = x2
    val yEnd = -y2  // Also invert y-coordinate at end
    val dx = Math.abs(x2 - x1)
    val dy = Math.abs(y2 - y1)
    val sx = if (x1 < x2) 1 else -1
    val sy = if (y1 < y2) -1 else 1  // Invert step direction for y
    var err = (if (dx > dy) dx else -dy) / 2

    breakable{
      while (true) {
        g.drawLine(x, y, x, y)  // Draw at the current point
        if (x == xEnd && y == yEnd) break  // Correct termination condition
        val e2 = err  // Declare e2 here within the loop
        if (e2 > -dx) {
          err -= dy
          x += sx
        }
        if (e2 < dy) {
          err += dx
          y += sy  // Adjust y in the correct direction based on inverted coordinates
        }
      }
    }
  }
}