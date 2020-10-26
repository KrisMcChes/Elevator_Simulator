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
import elevators.ElevatorController;
import gui.ElevatorDisplay;
import setup.JsonWorker;
import setup.Main;
import sun.invoke.empty.Empty;

import java.util.ArrayList;

public class Building {

	private static Building myBuilding;
	private static ElevatorController myController; // controller owns elevators
	private Floor[] floors;
	private static int numFloors;

	// if Building exists get a reference, if not create one
	public static Building getInstance() throws InvalidParameterException {
		if (myBuilding == null)
			myBuilding = new Building();
		return myBuilding;
	}

	// constructor
	private Building() throws InvalidParameterException {

		// get number of floors
		JsonWorker jw = new JsonWorker("data/buildingInput.json");

		numFloors = jw.getNumberFloors();
		if (numFloors < 3) {
			throw new InvalidParameterException("Building can't have less than two floors. Value passed: "+numFloors);
		}

		// initialize floors array
		floors = new Floor[numFloors];

		// initialize gui
		ElevatorDisplay.getInstance().initialize(numFloors);

		// create Floor objects
		for (int i=0; i<numFloors; i++) {
			floors[i] = new Floor(i+1);
		}
		// initialize controller
		myController = ElevatorController.getInstance();
	}

	// get information about number of floors in the building
	public int getNumFloors() { return numFloors; }

	// get Floor object
	private Floor getFloor(int floorID) {
		return floors[floorID-1]; //floors stored in the array, [0] - the first element
	}

	// create a person and add to floor
	public void addPerson(Person p) throws InvalidParameterException, EmptyReferenceException {

        if (p != null) {
            getFloor(p.getStartFloor()).addToFloor(p); // add to a floor

            try {
                Request r = new Request(p.getStartFloor(), p.getPersonTravelDirection()); // create a request

                System.out.printf("%s Person %s pressed %s button on Floor %d\n",
                        Main.getTimeStamp(), p.getPersonName(), p.getPersonTravelDirection(), p.getStartFloor());
                try {
                    p.setWaitTimeStart(System.currentTimeMillis());    // start wait time for person
                } catch (InvalidParameterException e) {
                    System.out.println(e.getMessage());
                }

                try {
					myController.manageRequest(r); // send request to E.Controller
				} catch (EmptyReferenceException e) {
					System.out.println(e.getMessage());
				}

            } catch (EmptyReferenceException | InvalidParameterException e) {
                System.out.println(e.getMessage());
            }
        }
    }

	// get an array of Person objects from Floor who want to enter the elevator, send this array back to Elevator Controller
	public ArrayList<Person> getRiders(int floorNumber, Direction direction){
		ArrayList<Person> tempRiders = getFloor(floorNumber).getRiders(direction);
		return tempRiders;
	}

	// give to the current floor people who finished their ride
	public void finishedRide(int floorNumber, ArrayList<Person> finishedRiders) {
		getFloor(floorNumber).addToDoneList(finishedRiders);
	}

}
