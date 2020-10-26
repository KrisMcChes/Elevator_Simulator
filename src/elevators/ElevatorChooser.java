/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.InvalidParameterException;
import building.Request;

// algorithm interface for choosing the best elevator for given request
public interface ElevatorChooser {

    boolean getBestElevator(int elevatorFloor, Direction elevatorDirection, int floorRequest, Direction floorRequestDirection, Request r) throws InvalidParameterException;

}
