/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.EmptyReferenceException;
import building.Request;
import setup.Main;

import java.util.ArrayList;

// algorithm for choosing the requests for an elevator from pending list
public class PendingRequestChooserImpl implements PendingRequestChooser {

    @Override
    public ArrayList<Request> getPendingRequests(int elevatorFloor, ArrayList<Request> requests) throws EmptyReferenceException {

        ArrayList<Request> chosenRequests = new ArrayList<>(); // to hold the result

        // if list passed is not empty
        if (requests.size() > 0) {

            chosenRequests.add(requests.get(0)); // add the first request automatically - the one who waited longer
            System.out.printf("%s Pending request %d-%s assigned to an elevator\n", Main.getTimeStamp(), requests.get(0).getFloor(), requests.get(0).getDirection());

            // if there is more than one request in the list
            if (requests.size() > 1) {
                for (int i = 1; i<requests.size(); i++) {

                    // check if other requests are in the same direction
                    if (requests.get(0).getDirection() == requests.get(i).getDirection()) {
                        if (requests.get(0).getDirection() == Direction.UP) {
                            if (requests.get(0).getFloor() - requests.get(i).getFloor() < 0) {
                                chosenRequests.add(requests.get(i));
                            }
                        }
                        else
                            if (requests.get(0).getFloor() - requests.get(i).getFloor() > 0) {
                                chosenRequests.add(requests.get(i));
                        }
                    }
                }
            }
        }
        else
            throw new EmptyReferenceException("Empty request list passed");

        return chosenRequests;
    }
}
