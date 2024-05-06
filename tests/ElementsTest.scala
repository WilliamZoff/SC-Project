package tests

import elements.Line

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import java.awt.Graphics

class LineTest extends AnyFlatSpec with Matchers {
  val graphics = mock(classOf[Graphics])

  "Line.draw" should "handle negative coordinates correctly" in {
    val line = Line(0, 0, -10, 0)
    line.draw(graphics)
    verify(graphics, times(11)).drawLine(anyInt(), anyInt(), anyInt(), anyInt())
  }

  it should "draw vertical lines correctly" in {
    val line = Line(5, 5, 5, 15)
    line.draw(graphics)
    for (y <- 5 to 15 by 1) {
      verify(graphics).drawLine(5, -y, 5, -y)
    }
  }

  it should "draw a steep line correctly" in {
    val line = Line(2, 2, 2, 10)
    line.draw(graphics)
    for (y <- 2 to 10 by 1) {
      verify(graphics).drawLine(2, -y, 2, -y)
    }
  }

  it should "draw a horizontal line correctly" in {
    val line = Line(0, 0, 0, 0)
    line.draw(graphics)
    verify(graphics, times(2)).drawLine(0, 0, 0, 0)
  }
}
