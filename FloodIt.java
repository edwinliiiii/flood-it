import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;

// our game utilities
class Utils {

  // sets up a new random board of cells
  static public ArrayList<ArrayList<Cell>> setup(ArrayList<ArrayList<Cell>> board) {
    board.clear();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      board.add(new ArrayList<Cell>());
    }

    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        board.get(i)
            .add(new Cell(
                (i % FloodItWorld.BOARD_SIZE)
                    * (FloodItWorld.WINDOW_WIDTH / FloodItWorld.BOARD_SIZE),
                (i % FloodItWorld.BOARD_SIZE)
                    * ((FloodItWorld.WINDOW_HEIGHT - 50) / FloodItWorld.BOARD_SIZE + 50)));
        board.get(i).get(j).x = i;
        board.get(i).get(j).y = j;
      }
    }
    return board;
  }

  // configures the tops of all cells in the random board
  // EFFECT: each cell will have a correct top
  static public void setTops(ArrayList<ArrayList<Cell>> board) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        if (i >= 1) {
          board.get(i).get(j).top = board.get(i - 1).get(j);
        }
      }
    }
  }

  // configures the bottoms of all cells in the random board
  // EFFECT: each cell will have a correct bottom
  static public void setBots(ArrayList<ArrayList<Cell>> board) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        if (i < FloodItWorld.BOARD_SIZE - 1) {
          board.get(i).get(j).bottom = board.get(i + 1).get(j);
        }
      }
    }
  }

  // configures the rights for all cells in the random board
  // EFFECT: each cell will have a correct right
  static public void setRights(ArrayList<ArrayList<Cell>> board) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        if (j < FloodItWorld.BOARD_SIZE - 1) {
          board.get(i).get(j).right = board.get(i).get(j + 1);
        }
      }
    }
  }

  // configures the lefts for all cells in the random board
  // EFFECT: each cell will have a correct left
  static public void setLefts(ArrayList<ArrayList<Cell>> board) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        if (j > 0) {
          board.get(i).get(j).left = board.get(i).get(j - 1);
        }
      }
    }
  }
}

// Representing our FloodIt game world
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  // All of the colors of the game
  static ArrayList<Color> colors = new ArrayList<Color>(
      Arrays.asList(Color.red, Color.cyan, Color.green, Color.yellow, Color.magenta, Color.orange));
  // Board and Cell size
  static int BOARD_SIZE = 10;
  static int CELL_SIZE = 50;
  // Window sizes
  static int WINDOW_WIDTH = BOARD_SIZE * CELL_SIZE;
  static int WINDOW_HEIGHT = (BOARD_SIZE * CELL_SIZE) + 50;

  // Counting the number of tries
  int remaining = (int) (BOARD_SIZE * colors.size()) / 2;

  // Cells that need to be flooded
  ArrayList<Cell> floodQ = new ArrayList<Cell>();

  // Temporary flooding color
  Color colorTemp;

  // the state of our world
  String worldState = "static";

  // Constructor
  public FloodItWorld(ArrayList<ArrayList<Cell>> board) {
    this.board = board;
  }

  // creates the scene to be displayed for the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(FloodItWorld.WINDOW_WIDTH, FloodItWorld.WINDOW_HEIGHT);
    for (ArrayList<Cell> arr : this.board) {
      for (Cell c : arr) {
        c.draw(scene);
      }
    }

    // puts as a flood any conjoined Cells of the same color to the top left on
    // initiation
    board.get(0).get(0).flooded = false;

    scene.placeImageXY(new TextImage("Floods Remaining:" + remaining, 20, Color.black), 150,
        FloodItWorld.WINDOW_HEIGHT - 40);
    return scene;
  }

  // handler for mouse clicks
  public void onMouseClicked(Posn pos) {
    // checks to see if this click is within the bounds of our game board
    if ((int) (pos.y / 50) >= 10) {
      return;
    }
    
    // gets color for the flood
    Color color = this.board.get((int) (pos.x / 50)).get((int) (pos.y / 50)).color;

    // checks to see if this click was valid
    if (color.equals(this.board.get(0).get(0).color)) {
      return;
    }

    // updates the counter of floods left
    remaining = remaining - 1;

    // this.board.get(0).get(0).color = color;
    this.colorTemp = color;
    this.worldState = "flooding";
    this.floodQ.add(this.board.get(0).get(0));
  }

  // handler for each game tick
  public void onTick() {
    if (this.worldState.equals("flooding")) {
      this.handleFlood();

      if (this.floodQ.size() == 0) {
        this.worldState = "static";
        this.resetFloods();
      }
    }
  }

  // handles the flooding waterfall effect
  public void handleFlood() {
    ArrayList<Cell> floodQTemp = new ArrayList<Cell>();

    for (int i = 0; i < floodQ.size(); i++) {
      Cell c = floodQ.get(i);
      if (c.left != null && c.left.color.equals(c.color) && !c.left.flooded) {
        floodQTemp.add(c.left);
      }
      if (c.right != null && c.right.color.equals(c.color) && !c.right.flooded) {
        floodQTemp.add(c.right);
      }
      if (c.top != null && c.top.color.equals(c.color) && !c.top.flooded) {
        floodQTemp.add(c.top);
      }
      if (c.bottom != null && c.bottom.color.equals(c.color) && !c.bottom.flooded) {
        floodQTemp.add(c.bottom);
      }

      c.color = this.colorTemp;
      c.flooded = true;
    }

    this.floodQ = floodQTemp;
  }

  // resets the flood for whole board
  public void resetFloods() {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.board.get(i).get(j).flooded = false;
      }
    }
  }

  // handles for key events
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      Utils.setup(board);
      Utils.setBots(board);
      Utils.setLefts(board);
      Utils.setRights(board);
      Utils.setTops(board);
      this.floodQ = new ArrayList<Cell>();
      this.worldState = "static";
      this.remaining = (int) (BOARD_SIZE * colors.size()) / 2;
    }
  }

  // checks when the Game ends
  public WorldEnd worldEnds() {
    if (this.win(board)) {
      return new WorldEnd(true, this.makeEndScene(0));
    }
    else if (remaining < 1) {
      return new WorldEnd(true, this.makeEndScene(1));
    }
    return new WorldEnd(false, this.makeEndScene(2));

  }

  // makes the end scene (for winning and losing)
  public WorldScene makeEndScene(int x) {
    WorldScene endScene = new WorldScene(FloodItWorld.WINDOW_WIDTH, FloodItWorld.WINDOW_HEIGHT);
    if (x == 0) {
      endScene.placeImageXY(new TextImage("You Won!", 75, Color.black), 250, 250);
    }
    else if (x == 1) {
      endScene.placeImageXY(new TextImage("You Lost!", 75, Color.black), 250, 250);
    }
    return endScene;

  }

  // helper for ending the game, checks if the board is flooded
  public boolean win(ArrayList<ArrayList<Cell>> board) {
    for (ArrayList<Cell> arr : board) {
      for (Cell c : arr) {
        if (!c.color.equals(this.colorTemp)) {
          return false;
        }
      }
    }
    return true;
  }
}

// Examples Class for our World
class WorldExamples {
  ArrayList<ArrayList<Cell>> finale;
  ArrayList<ArrayList<Cell>> finale2;

  // for test set neigthbors
  void initConditions() {
    finale = Utils.setup(new ArrayList<ArrayList<Cell>>()); // randomized board
  }

  // all else
  void initConditions2() {
    finale = Utils.setup(new ArrayList<ArrayList<Cell>>()); // randomized board
    Utils.setBots(finale);
    Utils.setLefts(finale);
    Utils.setRights(finale);
    Utils.setTops(finale);
  }

  // testing setTops method
  void testSetTops(Tester t) {
    initConditions();
    t.checkExpect(finale.get(0).get(0).top, null);
    t.checkExpect(finale.get(3).get(0).top, null);

    Utils.setTops(finale);
    t.checkExpect(finale.get(0).get(7).top, null);
    t.checkExpect(finale.get(3).get(0).top, finale.get(2).get(0));
  }

  // testing setBots method
  void testSetBots(Tester t) {
    initConditions();
    t.checkExpect(finale.get(9).get(0).bottom, null);
    t.checkExpect(finale.get(3).get(0).bottom, null);

    Utils.setBots(finale);
    t.checkExpect(finale.get(9).get(0).bottom, null);
    t.checkExpect(finale.get(3).get(0).bottom, finale.get(4).get(0));
  }

  // testing setLefts method
  void testSetLefts(Tester t) {
    initConditions();
    t.checkExpect(finale.get(0).get(0).left, null);
    t.checkExpect(finale.get(3).get(4).left, null);

    Utils.setLefts(finale);
    t.checkExpect(finale.get(5).get(0).left, null);
    t.checkExpect(finale.get(3).get(4).left, finale.get(3).get(3));
  }

  // testing setRights method
  void testSetRights(Tester t) {
    initConditions();
    t.checkExpect(finale.get(0).get(9).right, null);
    t.checkExpect(finale.get(3).get(5).right, null);

    Utils.setRights(finale);
    t.checkExpect(finale.get(0).get(9).right, null);
    t.checkExpect(finale.get(3).get(5).right, finale.get(3).get(6));
  }

  // testing for makeScene method
  void testMakeScene(Tester t) {
    initConditions2();
    // FloodItWorld g2 = new FloodItWorld(finale2, 2);
    // t.checkExpect(g2.makeScene(), null);
  }

  // testing worldEnd method
  void testWorldEnd(Tester t) {
    initConditions2();
    FloodItWorld g = new FloodItWorld(finale);

    // no change
    t.checkExpect(g.worldEnds(), new WorldEnd(false, g.makeEndScene(2)));

    // win
    for (ArrayList<Cell> arr : finale) {
      for (Cell c : arr) {
        c.flooded = true;
      }
    }
    t.checkExpect(g.worldEnds(), new WorldEnd(true, g.makeEndScene(0)));

    // lose
    finale.get(0).get(1).flooded = false;
    g.remaining = 0;
    t.checkExpect(g.worldEnds(), new WorldEnd(true, g.makeEndScene(1)));
  }

  // testing makeEndScene method
  void testMakeEndScene(Tester t) {
    initConditions2();
    FloodItWorld g = new FloodItWorld(finale);

    // winning screen
    WorldScene endScene = new WorldScene(500, 550);
    endScene.placeImageXY(new TextImage("You Won!", 75, Color.black), 250, 250);
    t.checkExpect(g.makeEndScene(0), endScene);

    // losing screen
    WorldScene endScene2 = new WorldScene(500, 550);
    endScene2.placeImageXY(new TextImage("You Lost!", 75, Color.black), 250, 250);
    t.checkExpect(g.makeEndScene(1), endScene2);
  }

  // testing win method
  void testWin(Tester t) {
    initConditions2();
    FloodItWorld g = new FloodItWorld(finale);

    t.checkExpect(g.win(finale), false);

    for (ArrayList<Cell> arr : finale) {
      for (Cell c : arr) {
        c.flooded = true;
      }
    }
    t.checkExpect(g.win(finale), true);
  }

  // testing the game
  void testGame(Tester t) {
    initConditions2();
    FloodItWorld g = new FloodItWorld(finale);
    g.bigBang(FloodItWorld.WINDOW_WIDTH, FloodItWorld.WINDOW_HEIGHT, 1.0 / 15);
  }
}