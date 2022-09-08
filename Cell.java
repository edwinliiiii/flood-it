import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.Random;

// Represents a single square of the game area
class Cell {
  // x and y positions
  int x;
  int y;
  // cell color
  Color color;
  // is flooded?
  boolean flooded = false;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  Random rand;

  // creates a cell at the given coordinates with a random color
  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.rand = new Random();
    this.color = FloodItWorld.colors.get(rand.nextInt(FloodItWorld.colors.size()));
  }

  // Constructor for testing
  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.rand = new Random();
  }

  // draws the Cell at the right position on the given WorldScene
  public void draw(WorldScene scene) {
    RectangleImage image = new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        "solid", this.color);
    scene.placeImageXY(image, this.x * 50 + 25, this.y * 50 + 25);
  }
}

class ExamplesCell {
  ExamplesCell() {
  }

  Cell c00 = new Cell(0, 0, Color.red, true, null, null, null, null);
  Cell c01 = new Cell(0, 1, Color.green, false, c00, null, null, null);
  Cell c10 = new Cell(1, 0, Color.red, false, null, c00, null, null);
  Cell c11 = new Cell(1, 1, Color.blue, false, c10, c01, null, null);

  void initConditions() {
    c00.bottom = c11;
    c00.right = c01;
    c01.bottom = c11;
    c10.right = c11;
  }

  // tests for draw method
  void testDraw(Tester t) {
    initConditions();

    WorldScene scene = new WorldScene(FloodItWorld.WINDOW_WIDTH, FloodItWorld.WINDOW_HEIGHT);
    WorldScene control = new WorldScene(FloodItWorld.WINDOW_WIDTH, FloodItWorld.WINDOW_HEIGHT);
    t.checkExpect(scene, control);

    c00.draw(control);

    RectangleImage image = new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        "solid", c00.color);
    scene.placeImageXY(image, 25, 25);
    t.checkExpect(scene, control);
  }
}
