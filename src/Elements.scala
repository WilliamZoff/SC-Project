//@Logika: --par --par-branch --background type
import org.sireum.{Option => _, String => _, None => _, Some => _, _}
import java.awt.{BasicStroke, Color, Graphics, Graphics2D}
import scala.jdk.CollectionConverters._
import scala.math._
import scala.util.control.Breaks._


trait GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit
}

object GraphicalElementsManager {
  var elements: List[GraphicalElement] = List()
  var boundingBox: Option[BoundingBox] = None

  def addElement(element: GraphicalElement): Unit = {
    if (boundingBox.isEmpty && !element.isInstanceOf[BoundingBox]) {
      throw new IllegalStateException("BOUNDING-BOX must be the first command")
    }
    elements = elements :+ element
  }

  def setBoundingBox(box: BoundingBox): Unit = {
    Contract(
      Requires(elements.isEmpty),
      Ensures(boundingBox.nonEmpty && boundingBox.contains(box))
    )
    if (elements.nonEmpty) {
      throw new IllegalStateException("BOUNDING-BOX must be the first command")
    }
    boundingBox = Some(box)
  }

  def getJavaElements: java.util.List[GraphicalElement] = {
    elements.asJava
  }

  def parseColor(colorName: String): Color = {
    colorName.toLowerCase match {
      case "black" => Color.BLACK
      case "red" => Color.RED
      case "green" => Color.GREEN
      case "blue" => Color.BLUE
      case "yellow" => Color.YELLOW
      case _ => Color.BLACK // Default color
    }
  }

  // Ensure all elements respect the optional bounding box
  def drawWithClipping(g: Graphics, element: GraphicalElement): Unit = {
    GraphicalElementsManager.boundingBox.foreach { box =>
      g.setClip(box.x, box.y, box.width, box.height)
    }
    element.draw(g)
    g.setClip(null) // Reset clipping to draw normally outside the method
  }

  // Utility to parse sub-commands within a DRAW command
  def parseSubCommands(subCommand: String): Option[GraphicalElement] = {
    val trimmed = subCommand.trim
    if (trimmed.nonEmpty) Some(parseCommandToElement(trimmed))
    else None
  }

  def parseCommandToElement(command: String): GraphicalElement = {
    println(s"Original command: $command")  // Log the original command

    val trimmedCommand = command.trim.replaceAll("\\s+", " ")
    println(s"Trimmed and normalized command: $trimmedCommand")  // Log the normalized command

    val commandType = trimmedCommand.takeWhile(_ != '(').trim
    println(s"Command type: $commandType")  // Log the extracted command type

    val args = trimmedCommand.substring(trimmedCommand.indexOf('(') + 1, trimmedCommand.lastIndexOf(')'))
      .split(",")
      .map(_.trim)
    println(s"Arguments: ${args.mkString(", ")}")  // Log the arguments

    commandType.toUpperCase match {
      case "BOUNDING-BOX" =>
        val box = BoundingBox(args(0).toInt, args(1).toInt, args(2).toInt, args(3).toInt)
        GraphicalElementsManager.setBoundingBox(box)
        box
      case "LINE" =>
        Line(args(0).toInt, args(1).toInt, args(2).toInt, args(3).toInt)
      case "RECTANGLE" =>
        Rectangle(args(0).toInt, args(1).toInt, args(2).toInt, args(3).toInt)
      case "CIRCLE" =>
        Circle(args(0).toInt, args(1).toInt, args(2).toInt)
      case "TEXT" =>
        val text = args.last.replace("\"", "")
        Text(args(0).toInt, args(1).toInt, text)
      case "DRAW" =>
        val color = parseColor(args(0))
        val elements = args.tail.map(parseCommandToElement).toList // Parsing each element to be drawn
        DrawableGroup(color, elements)
      case "FILL" =>
        val color = parseColor(args(0))
        val elementToFill = parseCommandToElement(args(1)) // Recursively parse element to fill
        FilledElement(color, elementToFill)
      case _ =>
        throw new IllegalArgumentException(s"Unknown command: $commandType")
    }
  }
}

// BoundingBox command - sets area to draw in
case class BoundingBox(x1: Int, y1: Int, x2: Int, y2: Int) extends GraphicalElement {
  val x: Int = Math.min(x1, x2)
  val y: Int = Math.min(-y1, -y2)  // Inverting y for consistency
  val width: Int = Math.abs(x2 - x1)
  val height: Int = Math.abs(y2 - y1)

  def draw(g: Graphics, fill: Boolean = false): Unit = {
    val g2d = g.asInstanceOf[Graphics2D] // Cast to use advanced features

    // Set the stroke as dashed
    val dash = Array(10.0f, 10.0f) // Dashed pattern
    val stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, dash, 0.0f)
    g2d.setStroke(stroke)

    // Set color to red
    g2d.setColor(Color.RED)

    // Draw the rectangle with an adjustment to include all borders
    g2d.drawRect(x, y, width - 1, height - 1)

    // Reset stroke to default for other elements if needed
    g2d.setStroke(new BasicStroke())
  }
}


// Draw command - draws elements in list
case class DrawableGroup(color: Color, elements: List[GraphicalElement]) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    g.setColor(color)
    elements.foreach(_.draw(g))
    g.setColor(Color.BLACK) // Reset to default after drawing
  }
}

// FilledEllement command - takes color and element to fill
case class FilledElement(color: Color, element: GraphicalElement) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    g.setColor(color)
    element.draw(g, fill = true)
    g.setColor(Color.BLACK) // Reset to default after drawing
  }
}


// Line element using Bresenham's algorithm
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


// Rectangle element with fill support
case class Rectangle(x1: Int, y1: Int, x2: Int, y2: Int) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    val x = Math.min(x1, x2)
    val y = Math.min(-y1, -y2)
    val width = Math.abs(x2 - x1)
    val height = Math.abs(y2 - y1)
    if (fill) g.fillRect(x, y, width, height)
    else g.drawRect(x, y, width, height)
  }
}

// Circle element using the Midpoint circle algorithm
case class Circle(x: Int, y: Int, radius: Int) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    var x0 = 0
    var y0 = radius
    var d = 3 - 2 * radius

    // Helper method to draw a point or line based on the fill flag
    def drawPointOrLine(cx: Int, cy: Int): Unit = {
      if (fill) {
        // Draw horizontal lines for filling
        g.drawLine(x - cx, -y + cy, x + cx, -y + cy)  // Line across the diameter at this y
        g.drawLine(x - cx, -y - cy, x + cx, -y - cy)  // Same for the opposite side
      } else {
        // Draw single points for the outline
        g.drawLine(x + cx, -y + cy, x + cx, -y + cy)
        g.drawLine(x - cx, -y + cy, x - cx, -y + cy)
        g.drawLine(x + cx, -y - cy, x + cx, -y - cy)
        g.drawLine(x - cx, -y - cy, x - cx, -y - cy)
      }
    }

    // Apply the Midpoint Circle Algorithm
    while (y0 >= x0) {
      // Draw points or lines from the calculated points
      drawPointOrLine(x0, y0)   // Quadrants 1 and 2
      drawPointOrLine(y0, x0)   // Quadrants 3 and 4

      // Update for next points
      if (d < 0) {
        d += 4 * x0 + 6
      } else {
        d += 4 * (x0 - y0) + 10
        y0 -= 1
      }
      x0 += 1
    }
  }
}

// Text element
case class Text(x: Int, y: Int, text: String) extends GraphicalElement {
  def draw(g: Graphics, fill: Boolean = false): Unit = {
    // Invert y-coordinate for text drawing
    g.drawString(text, x, -y)
  }
}
