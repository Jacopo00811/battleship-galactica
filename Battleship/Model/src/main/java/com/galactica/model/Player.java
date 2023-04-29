package com.galactica.model;

import com.galactica.model.ships.Cruiser;
import com.galactica.model.ships.DeathStar;
import com.galactica.model.ships.Scout;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public abstract class Player {
    protected String name;
    protected Ship[] ships;
    protected Grid ownGrid;
    protected Grid opponentGrid;

    protected Cannon cannon = new Cannon();
    protected Grenade grenade = new Grenade();
    protected Laser laser = new Laser();

    protected static Scanner sc = new Scanner(System.in);

    public Player(Grid ownGrid, Grid opponentGrid) {
        ships = new Ship[3]; // Placeholder 5
        this.ownGrid = ownGrid;
        this.opponentGrid = opponentGrid;

        initializeShips();
    }

    public Player(String name, Grid ownGrid, Grid opponentGrid, Ship[] ships, Laser laser, Grenade grenade, Cannon cannon) {
        this.name = name;
        this.ownGrid = ownGrid;
        this.opponentGrid = opponentGrid;
        this.ships = ships;
        this.laser = laser;
        this.grenade = grenade;
        this.cannon = cannon;
    }

    public abstract void shoot(Coordinate coordinate, Weapon weaponToShoot, boolean gravityMode, boolean gravityUsed);

    public List<Coordinate> shootGrenade(Coordinate coordinate, Grenade grenade) {
        List<Coordinate> coordinateList = grenade.getScatterCoordinates(coordinate, opponentGrid);
        grenade.decrementAmountOfUses();
        checkOutcomeOfShot(coordinateList);
        return coordinateList;
    }

    public void shootLaser(Coordinate coordinate, char rowOrColumn, Laser laser) {
        List<Coordinate> coordinateList = laser.getLaserCoordinates(coordinate, opponentGrid, rowOrColumn);
        laser.decrementAmountOfUses();
        checkOutcomeOfShot(coordinateList);
    }

    public boolean shootCannon(Coordinate coordinate, boolean gravityMode, boolean gravityUsed) {
        List<Coordinate> coordinateList = new ArrayList<Coordinate>();
        coordinateList.add(coordinate);

        boolean hit = checkOutcomeOfShot(coordinateList);

        if (!hit && gravityMode && !gravityUsed) {
            List<Planet> opponentPlanets = opponentGrid.getPlanets();
            for (Planet planet : opponentPlanets) {
                if (!gravityUsed) {
                    Coordinate rebound = planet.getPlanetRebound(coordinate);
                    if (rebound != null) {
                        shootCannon(rebound, true, true);
                    }
                }
            }
        }
        return hit;
    }

    private void initializeShips() {
        ships[0] = new DeathStar(1);
        ships[1] = new Cruiser(2);
        ships[2] = new Scout(3);
    }

    public boolean areAllShipsSunk() {
        return Arrays.stream(ships).allMatch(ship -> ship.isSunk());
    }

    public boolean hasAllShipsPlaced() {
        return Arrays.stream(ships).allMatch(ship -> ship.isPlaced());
    }

    public Ship placeShip(Ship ship, Coordinate coordinate, Direction direction) {
        if (ownGrid.isValidShipPosition(ship, coordinate, direction)) {
            ship.setCoordinate(coordinate);
            ship.setDirection(direction);
            ownGrid.placeShip(ship, coordinate, direction);
            return ship;
        }
        return null;
    }

    public void removeShip(Ship ship) throws UnplacedShipException {
        ownGrid.removeShip(ship);
        ship.setCoordinate(null);
        ship.setDirection(null);
    }

    public String getName() {
        return name;
    }

    public Grid getOwnGrid() {
        return ownGrid;
    }

    public Grid getOpponentGrid() {
        return opponentGrid;
    }

    public Ship[] getShips() {
        return ships;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public Grenade getGrenade() {
        return grenade;
    }

    public Laser getLaser() {
        return laser;
    }

    public boolean checkOutcomeOfShot(List<Coordinate> coordinateList) {
        int asteroidsHit = 0;
        int shipsHit = 0;
        int shipsSunk = 0;
        int successfulHits = 0;
        List<Planet> planetsHit = new ArrayList<Planet>();
        List<Coordinate> coordinatesWithPlanets = new ArrayList<Coordinate>();

        for (Coordinate coordinate : coordinateList) {
            opponentGrid.setTile(coordinate, true);

            Asteroid asteroidAtCoordinate = opponentGrid.getAsteroidAtCoordinate(coordinate);
            Ship shipAtCoordinate = opponentGrid.getShipAtCoordinate(coordinate);
            Planet planet = opponentGrid.getPlanetAtCoordinate(coordinate);

            if (asteroidAtCoordinate != null) {
                asteroidsHit++;
                if (this instanceof AI) {
                    AI ai = (AI) this;
                    ai.updateCoordinateHit(coordinate);
                    ai.updateTracking(coordinate);
                }
            } else if (planet != null) {
                planetsHit.add(planet);
            } else if (shipAtCoordinate != null) {
                shipsHit++;
                if (this instanceof AI) {
                    AI ai = (AI) this;
                    ai.updateCoordinateHit(coordinate);
                    ai.updateTracking(coordinate);
                }
                boolean isShipSunk = opponentGrid.checkIfShipIsSunk(shipAtCoordinate);
                if (isShipSunk) {
                    shipAtCoordinate.setSunk(true);
                    shipsSunk++;
                }

            }
            if (this instanceof AI) {
                AI ai = (AI) this;
                ai.updateCoordinateHit(coordinate);
            }
        }

        for (Planet planet : planetsHit) {
            for (Coordinate coordinate : planet.getPlanetCoordinates()) {
                opponentGrid.setTile(coordinate, true);
                coordinatesWithPlanets.add(coordinate);
                if (this instanceof AI) {
                    AI ai = (AI) this;
                    ai.updateCoordinateHit(coordinate);
                }
            }

        }

        successfulHits = asteroidsHit + shipsHit;

        if (successfulHits + planetsHit.size() == 0) {
            System.out.println("Unlucky :( \n" + name + " didn't hit anything");

        } else {
            if (successfulHits == 1) {
                System.out.println(name + " hit something!");
            } else if (successfulHits > 1)
                System.out.println(name + " made " + successfulHits + " successful shots!");
            if (shipsSunk == 1) {
                System.out.println(name + " sunk a ship! 💥🚢");
            } else if (shipsSunk > 1) {
                System.out.println(name + " sunk " + shipsSunk + " ships! 💥🚢");
            }
        }
        if (planetsHit.size() == 1) {
            System.out.println(name + " hit a planet! 🌎");
        } else if (planetsHit.size() > 1) {
            System.out.println(name + " hit " + planetsHit.size() + " planets! 🌎");
        }

        return successfulHits > 0 && planetsHit.size() == 0;
    }


    public JsonArray toJsonArray(Ship[] ships) {
        JsonArray ja = new JsonArray();
        for (Ship ship : ships)
           ja.add(ship.toJsonObject());
        
           return ja;
    }

    public JsonObject toJsonObject() {
        JsonObject jo = new JsonObject();
        jo.put("name", name);
        jo.put("ships", toJsonArray(ships));
        jo.put("cannon", cannon.toJsonObject());
        jo.put("grenade", grenade.toJsonObject());
        jo.put("laser", laser.toJsonObject());

        return jo;
    }

    public static Ship[] fromJsonArraytoShipList(JsonArray ja) {
        Ship[] ships = new Ship[ja.size()];
        for (int i = 0; i < ja.size(); i++) {
            ships[i] = Ship.fromJsonObject((JsonObject) ja.get(i));
        }
        return ships;
    }

}
