/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.secret;

import io.github.pulsebeat02.murderrun.secret.minesweeper.Minesweeper;
import io.github.pulsebeat02.murderrun.secret.rick.RickRoll;
import java.util.SplittableRandom;
import javax.swing.*;

public final class Main {

  private static final SplittableRandom RANDOM = new SplittableRandom();

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(Main::invokeRandomMenu);
  }

  private static void invokeRandomMenu() {
    final int random = RANDOM.nextInt(2);
    switch (random) {
      case 0 -> invokeMinesweeperMenu();
      case 1 -> invokeRickRollMenu();
      default -> throw new AssertionError("Invalid mode!");
    }
  }

  private static void invokeRickRollMenu() {
    final RickRoll rickRoll = new RickRoll();
    rickRoll.setVisible(true);
  }

  private static void invokeMinesweeperMenu() {
    final JFrame minesweeper = new Minesweeper();
    minesweeper.setVisible(true);
  }
}
