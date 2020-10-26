/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.EmptyReferenceException;
import building.Request;
import java.util.ArrayList;

// algorithm interface to chose pending requests for an IDLE elevator
public interface PendingRequestChooser {
    ArrayList<Request> getPendingRequests(int elevatorFloor, ArrayList<Request> requests) throws EmptyReferenceException;
}
