/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.UUIDConverter;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "player_statistics")
public final class PlayerStatistics implements Serializable {

  @Serial
  private static final long serialVersionUID = 8818715610268669533L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "uuid")
  @Convert(converter = UUIDConverter.class)
  private final UUID uuid;

  @Column(name = "fastest_win_killer")
  private long fastestWinKiller;

  @Column(name = "fastest_win_survivor")
  private long fastestWinSurvivor;

  @Column(name = "total_kills")
  private int totalKills;

  @Column(name = "total_deaths")
  private int totalDeaths;

  @Column(name = "total_wins")
  private int totalWins;

  @Column(name = "total_losses")
  private int totalLosses;

  @Column(name = "total_games")
  private int totalGames;

  @Column(name = "win_loss_ratio")
  private float winLossRatio;

  public PlayerStatistics(final UUID uuid) {
    this.uuid = uuid;
    this.fastestWinKiller = -1;
    this.fastestWinSurvivor = -1;
  }

  public void insertFastestWinKiller(final long win) {
    this.fastestWinKiller = Math.min(this.fastestWinKiller, win);
  }

  public void insertFastestWinSurvivor(final long win) {
    this.fastestWinSurvivor = Math.min(this.fastestWinSurvivor, win);
  }

  public void incrementTotalKills() {
    this.totalKills++;
  }

  public void incrementTotalDeaths() {
    this.totalDeaths++;
  }

  public void incrementTotalWins() {
    this.totalWins++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalLosses() {
    this.totalLosses++;
    this.calculateWinLossRatio();
    this.incrementTotalGames();
  }

  public void incrementTotalGames() { // dont need to update
    this.totalGames++;
  }

  public void calculateWinLossRatio() { // dont need to update
    if (this.totalLosses != 0) {
      this.winLossRatio = (float) this.totalWins / this.totalLosses;
    }
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public long getFastestWinKiller() {
    return this.fastestWinKiller;
  }

  public long getFastestWinSurvivor() {
    return this.fastestWinSurvivor;
  }

  public int getTotalKills() {
    return this.totalKills;
  }

  public int getTotalDeaths() {
    return this.totalDeaths;
  }

  public int getTotalWins() {
    return this.totalWins;
  }

  public int getTotalLosses() {
    return this.totalLosses;
  }

  public int getTotalGames() {
    return this.totalGames;
  }

  public float getWinLossRatio() {
    return this.winLossRatio;
  }
}
