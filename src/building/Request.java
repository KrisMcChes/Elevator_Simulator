/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package building;

import ExceptionHandling.EmptyReferenceException;
import ExceptionHandling.InvalidParameterException;
import elevators.Direction;

// simple class to store request data
public class Request {

    private int floor;
    private Direction direction;

    // constructor
    public Request(int floorIn, Direction directionIn) throws InvalidParameterException, EmptyReferenceException {

        if (floorIn < 1 | floorIn > Building.getInstance().getNumFloors()) {
            throw new InvalidParameterException("Request can't be created on an invalid floor. Floor passed: " + floorIn);
        }

        if (directionIn == null) {
            throw new EmptyReferenceException("Direction has to be UP or DOWN. No direction passed");
        }

        floor = floorIn;
        direction = directionIn;
    }

    // getters
    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }

}
