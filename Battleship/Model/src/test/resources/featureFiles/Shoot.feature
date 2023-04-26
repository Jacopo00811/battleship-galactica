@tag
Feature: Playing a turn

  @tag1
  Scenario: Successfully shoot with a cannon at a ship
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "without" gravity mode
    And "My opponent places" a "Cruiser" in direction "h" on coordinate "a" 1
    When "I shoot" a cannon at coordinate "a" 1
    Then The tile "a" 1 on "my opponent's" grid is hit

  @tag2
  Scenario: Successfully shoot with a cannon at an asteroid
    Given I have started a new game on a size 10 grid in "multi" player mode, "with" asteroid mode, "without" gravity mode
    When "I shoot" a cannon at "an asteroid"
    Then The "asteroid" on my opponent's grid is hit

  @tag3
  Scenario: Successfully shoot with a cannon at a planet
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "with" gravity mode
    When "I shoot" a cannon at "a planet"
    Then The "planet" on my opponent's grid is hit
    And The entire planet on "my opponent's" grid is revealed

  @tag4
  Scenario: Successfully shoot with a laser at row
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "without" gravity mode
    And "My opponent places" a "Cruiser" in direction "h" on coordinate "a" 1
    When I shot a laser at row 1
    Then The row 1 on my opponent's grid is hit
    And The ship is sunk
    And I can no longer shoot with a laser

  @tag5
  Scenario: Successfully shoot with a laser at column
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "without" gravity mode
    And "My opponent places" a "Cruiser" in direction "v" on coordinate "a" 1
    When I shot a laser at column "a"
    Then The column "a" on my opponent's grid is hit
    And The ship is sunk
    And I can no longer shoot with a laser

  @tag6
  Scenario: Successfully shoot with a grenade
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "without" gravity mode
    And "My opponent places" a "Cruiser" in direction "h" on coordinate "a" 1
    When I shot a grenade at coordinate "a" 2
    Then The tile "a" 2 on "my opponent's" grid is hit
    And 2 random adjacent tiles on "my opponent's" grid are hit

  @tag7
  Scenario: AI shoots with a cannon
    Given I have started a new game on a size 10 grid in "single" player mode, "without" asteroid mode, "without" gravity mode
    And The AI places its ships
    And "I place" a "Cruiser" in direction "h" on coordinate "a" 0
    When "The AI shoots" a cannon at coordinate "a" 9
    Then The tile "a" 9 on "my" grid is hit

  @tag8
  Scenario: AI tracks ship if hit
    Given I have started a new game on a size 10 grid in "single" player mode, "without" asteroid mode, "without" gravity mode
    And The AI places its ships
    And "I place" a "Cruiser" in direction "v" on coordinate "b" 5
    And "The AI shoots" a cannon at coordinate "b" 5
    When The AI tries to track the ship until it's sunk
    Then The tile "b" 5 on "my" grid is hit
    And The tile "c" 5 on "my" grid is hit
    And The tile "b" 4 on "my" grid is hit
    And The tile "a" 5 on "my" grid is hit
    And The tile "b" 6 on "my" grid is hit
    And The tile "b" 7 on "my" grid is hit
    And The ship is sunk

  @tag9
  Scenario: User's shot is rebounded by planet
    Given I have started a new game on a size 10 grid in "multi" player mode, "without" asteroid mode, "with" gravity mode
    When "I shoot" a cannon <shot> a planet
    Then The shot gets rebounded <rebound> the planet on "my opponent's" grid

    Examples: 
      | shot          | rebound       |
      | "above"       | "below"       |
      | "below"       | "above"       |
      | "left of"     | "right of"    |
      | "right of"    | "left of"     |
      | "right above" | "left below"  |
      | "left below"  | "right above" |
      | "right below" | "left above"  |
      | "left above"  | "right below" |

  @tag10
  Scenario: AI's shot is rebounded by planet
    Given I have started a new game on a size 10 grid in "single" player mode, "without" asteroid mode, "with" gravity mode
    When "The AI shoots" a cannon <shot> a planet
    Then The shot gets rebounded <rebound> the planet on "my" grid

    Examples: 
      | shot          | rebound       |
      | "above"       | "below"       |
      | "below"       | "above"       |
      | "left of"     | "right of"    |
      | "right of"    | "left of"     |
      | "right above" | "left below"  |
      | "left below"  | "right above" |
      | "right below" | "left above"  |
      | "left above"  | "right below" |
