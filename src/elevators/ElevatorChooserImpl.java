/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.InvalidParameterException;
import building.Request;

// algorithm for choosing the best elevator for given request
public class ElevatorChooserImpl implements ElevatorChooser {

    @Override
    public boolean getBestElevator(int elevatorFloor, Direction elevatorDirection, int floorRequest, Direction floorRequestDirection, Request r) throws InvalidParameterException {

        // is there an elevator on this floor?
        if (elevatorFloor == floorRequest) {

            // if there are no current requests
            if (r == null)

                // if IDLE or going to the same direction -> then choose this elevator
                if (elevatorDirection == Direction.IDLE || elevatorDirection == floorRequestDirection) {
                    return true;
            }

            // if elevator already moving towards some request
            else

                // if IDLE or going to the same direction
                if (elevatorDirection == Direction.IDLE || elevatorDirection == floorRequestDirection)

                    // check if direction of the priority request is the same as new request -> if true take new request
                    if (r.getDirection() == floorRequestDirection)
                        return true;
        }

        // is there an elevator already moving?
        else if (elevatorDirection != Direction.IDLE) {

            // is it moving to the direction of the request?
            if (isElevatorInRightDirection(elevatorFloor, elevatorDirection, floorRequest, floorRequestDirection, r) == true) {
                return true;
            }
        }

        // is there an IDLE elevator?
        else if (elevatorDirection == Direction.IDLE) {
            return true;
        }

        // otherwise this elevator shouldn't take new request
        return false;
    }

    // check if elevator is moving to the right direction
    private boolean isElevatorInRightDirection(int elevatorFloor, Direction elevatorDirection, int floorRequest, Direction floorRequestDirection, Request r) throws InvalidParameterException {

        // compute direction from current elevator's floor towards new request
        Direction directionTowardsRequest = ElevatorController.getDirection(elevatorFloor,floorRequest);

        // if elevator doesn't have any current floor requests
        if (r == null) {
            // check if elevator  moving towards new request
            if (directionTowardsRequest == elevatorDirection)
                // check if elevator moving to the right direction as the direction of the new request
                if (elevatorDirection == floorRequestDirection)
                    return true;
        }

        // if elevator already have request in work
        else
            // check if elevator  moving towards new request
            if (directionTowardsRequest == elevatorDirection)
                // check if elevator moving to the right direction as the direction of the new request
                if (elevatorDirection == floorRequestDirection)
                    // check if new request won't interfere with the old one
                    if (r.getDirection() == floorRequestDirection)
                        return true;

        return false;

    }
}


