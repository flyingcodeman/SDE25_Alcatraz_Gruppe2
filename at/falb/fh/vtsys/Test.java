package at.falb.fh.vtsys;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import java.util.ArrayList;
import java.util.List;

/**
 * A test class initializing a local Alcatraz game -- illustrating how to use
 * the Alcatraz API.
 * @author falbj, Froihofer
 */
public class Test implements MoveListener {

  private static final byte NUM_PLAYER = 2;
  private static final Test TEST_GAMES[] = new Test[NUM_PLAYER];
  private static final Alcatraz ALCATRAZ_INSTANCES[] = new Alcatraz[NUM_PLAYER];

  private final List<Alcatraz> otherAlcatraz= new ArrayList<>(NUM_PLAYER - 1);

  public Test() {
  }

  public void addOther(Alcatraz t) {
    this.otherAlcatraz.add(t);
  }

  @Override
  public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
    System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
    for (Alcatraz a : otherAlcatraz) {
      try {
        a.doMove(a.getPlayer(player.getId()), a.getPrisoner(prisoner.getId()), rowOrCol, row, col);
      }
      catch (IllegalMoveException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void gameWon(Player player) {
    System.out.println("Player " + player.getId() + " wins.");
  }

  /**
   * @param args Command line args
   */
  public static void main(String[] args) {
    //Attention: This example instantiates several Alcatraz instances
    //           within a single process. If you implement the distributed
    //           version, only one Alcatraz instance is required per process
    //           as the other ones run in a separate process and communication
    //           happens via RMI.
    for (int i = 0; i < NUM_PLAYER; i++) {
      Test t = new Test();
      TEST_GAMES[i] = t;
      Alcatraz a = new Alcatraz();
      a.init(NUM_PLAYER,i);
      for (int j = 0; j<NUM_PLAYER; j++) {
        a.getPlayer(j).setName("Player "+(j+1));
      }
      a.addMoveListener(t);
      ALCATRAZ_INSTANCES[i] = a;
    }

    for (int i = 0; i<NUM_PLAYER; i++) {
      for (int j = 0; j < NUM_PLAYER; j++) {
        if (i != j) {
          TEST_GAMES[i].addOther(ALCATRAZ_INSTANCES[j]);
        }
      }
    }
    
    for (int i=0; i< NUM_PLAYER; i++) {
      ALCATRAZ_INSTANCES[i].showWindow();
      ALCATRAZ_INSTANCES[i].start();
    }

  }

}
