import java.awt.Graphics

trait GraphicalElement {
  def draw(g: Graphics): Unit
}

case class Line(x1: Int, y1: Int, x2: Int, y2: Int) extends GraphicalElement {
  def draw(g: Graphics): Unit = {
    g.drawLine(x1, y1, x2, y2)
  }
}

case class Rectangle(x1: Int, y1: Int, x2: Int, y2: Int) extends GraphicalElement {
  def draw(g: Graphics): Unit = {
    g.drawRect(x1, y1, x2 - x1, y2 - y1)
  }
}

case class Circle(x: Int, y: Int, radius: Int) extends GraphicalElement {
  def draw(g: Graphics): Unit = {
    g.drawOval(x - radius, y - radius, radius * 2, radius * 2)
  }
}

case class Text(x: Int, y: Int, text: String) extends GraphicalElement {
  def draw(g: Graphics): Unit = {
    g.drawString(text, x, y)
  }
}
