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
package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Entity
@Table(name = "lobby")
public final class Lobby implements Serializable {

  @Serial
  private static final long serialVersionUID = -3340383856074756744L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Convert(converter = LocationConverter.class)
  @Column(name = "lobby_spawn")
  private Location lobbySpawn;

  @Convert(converter = LocationConverter.class)
  @Column(name = "corners")
  private Location[] corners;

  public Lobby() {}

  public Lobby(final String name, final Location[] corners, final Location lobbySpawn) {
    this.name = name;
    this.corners = corners;
    this.lobbySpawn = lobbySpawn;
  }

  public String getName() {
    return this.name;
  }

  public Location getLobbySpawn() {
    return this.lobbySpawn;
  }

  public Location[] getCorners() {
    return this.corners;
  }

  public void relativizeLocations(final UUID uuid) {
    final String name = uuid.toString();
    final World world = requireNonNull(Bukkit.getWorld(name));
    this.corners[0].setWorld(world);
    this.corners[1].setWorld(world);
  }
}
