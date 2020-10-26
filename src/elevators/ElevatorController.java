/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.EmptyReferenceException;
import ExceptionHandling.InvalidParameterException;
import building.Building;
import building.Person;
import setup.JsonWorker;
import building.Request;
import gui.ElevatorDisplay;
import setup.Main;

import java.util.ArrayList;

public class ElevatorController implements ElevatorChooser, PendingRequestChooser {

	private static ElevatorController elevatorController;
	private Elevator[] elevators;		// E.Controller owns elevators
	private ElevatorChooser myChooser; 	// algorithm for picking up
	private PendingRequestChooser myPendingRequestChooser; // algorithm for pending requests
	private ArrayList<Request> pendingRequests = new ArrayList<>(); // hold requests if elevators can't take them right away

	// building can have only one E.controller
	public static ElevatorController getInstance() throws InvalidParameterException {
		if (elevatorController == null)
			elevatorController = new ElevatorController();
		return elevatorController;
	}

	// constructor
	private ElevatorController() throws InvalidParameterException {

		// get number of elevators
		JsonWorker jw = new JsonWorker("data/buildingInput.json");
		int numElevators = jw.getNumberElevators();
		if (numElevators < 1) {
			throw new InvalidParameterException("There has to be at least one elevator. Value passed: "+numElevators);
		}
		// initialize an array to hold elevator objects
		elevators = new Elevator[numElevators];

		// create elevators
		for (int i=0; i<numElevators; i++) {

			// gui, add elevators
			ElevatorDisplay.getInstance().addElevator(i+1,1);

			// create elevator objects
			elevators[i] = new Elevator();
			try {
				elevators[i].setElevatorNumber(i + 1);
			} catch (InvalidParameterException e) {
				System.out.println(e.getMessage());
			}
		}

		// get algorithm for choosing a proper elevator
		ElevatorChooserFactory cf = new ElevatorChooserFactory();
		myChooser = cf.getElevatorAlgorithm();

		// get algorithm for handling pending requests
		PendingRequestChooserFactory pf = new PendingRequestChooserFactory();
		myPendingRequestChooser = pf.getPendingRequestAlgorithm();
	}

	// get access to Elevator object
	private Elevator getElevator(int elevatorNumber) { return elevators[elevatorNumber-1]; }

	// get info about number of elevators
	public int getNumberElevators() { return elevators.length; }

	// ask Building about number of floors
	public static int getNumberFloors() throws InvalidParameterException {
		int numFloors = Building.getInstance().getNumFloors();
		return numFloors;
	}

	// get an array of Person objects from the Building who want to enter the elevator
	public ArrayList<Person> getRiders(int floorNumber, Direction direction) throws InvalidParameterException {
		ArrayList<Person> tempRiders = Building.getInstance().getRiders(floorNumber, direction);
		return tempRiders;
	}

	// send people who finished their ride to Building->Floor
	public void finishedRide(int floorNumber, ArrayList<Person> finishedRiders) throws InvalidParameterException {
		Building.getInstance().finishedRide(floorNumber, finishedRiders);
	}

	// calculate direction
	public static Direction getDirection(int startFloor, int endFloor) throws InvalidParameterException {

		if (startFloor < 0 | startFloor > Building.getInstance().getNumFloors()) {
			throw new InvalidParameterException("Start floor has to be within building's floor range. Value passed: "+startFloor);
		}

		if (endFloor < 0 | endFloor > Building.getInstance().getNumFloors()) {
			throw new InvalidParameterException("Destination floor has to be within building's floor range. Value passed: "+startFloor);
		}

		Direction direction;
		int difference = startFloor-endFloor;
		if (difference < 0)
			direction = Direction.UP;
		else if (difference > 0)
			direction = Direction.DOWN;
		else
			direction = Direction.IDLE;
		return direction;
	}

	// assign current request to elevator or put in pending list
    public void manageRequest(Request r) throws InvalidParameterException, EmptyReferenceException {

		if (r == null) {
			throw new EmptyReferenceException("Empty request received");
		}

		boolean bestElevatorFound = false;

		for (Elevator e : elevators) {

			// get parameters that will be passed to the chooser algorithm
			Request currentRequest = e.getCurrentRequest();
			int elevatorNumber = e.getElevatorNumber();
			int elevatorFloor = e.getCurrentFloor();
			Direction elevatorDirection = e.getDirection();
			int floorRequest = r.getFloor();
			Direction floorRequestDirection = r.getDirection();

			// check if current elevator passed the test
			boolean bestElevator = myChooser.getBestElevator(elevatorFloor, elevatorDirection, floorRequest, floorRequestDirection, currentRequest);

			// as soon as the elevator found, send a request to the elevator and break the loop
			if (bestElevator == true) {
				getElevator(elevatorNumber).addFloorRequest(r);
				bestElevatorFound = true;
				break;
			}
		}

		// if there is no available elevators, add to pending request
		if (bestElevatorFound == false) {
			pendingRequests.add(r);
			System.out.printf("%s Request %d-%s added to pending requests\n", Main.getTimeStamp(), r.getFloor(), r.getDirection());
		}
    }

	// delegate to myChooser to find best elevator
	@Override
	public boolean getBestElevator(int elevatorFloor, Direction elevatorDirection, int floorRequest, Direction floorRequestDirection, Request request) throws InvalidParameterException {
		return myChooser.getBestElevator(elevatorFloor, elevatorDirection, floorRequest, floorRequestDirection, request);
	}

	// update the state of all elevators and check if any of pending requests can be sent to work
	public void operateElevators(int time) throws InvalidParameterException, EmptyReferenceException {

		for (Elevator e : elevators) {

			// if there are requests pending
			if (!pendingRequests.isEmpty()) {

				// and there is an IDLE elevator
				if (e.getDirection() == Direction.IDLE) {

					// get all pending requests
					ArrayList<Request> requests = getRequestsList();

					// save to temp list the requests that delegator chose for current elevator
					try {
						ArrayList<Request> temp = getPendingRequests(e.getCurrentFloor(), requests);

					// add those requests to elevator requests lists
					if (!temp.isEmpty()) {
						e.addPendingRequests(temp);

						// remove assigned requests from E.Controller pending list
						for (Request r: temp) {
							pendingRequests.remove(r);
							return;
						}
					}
					} catch (EmptyReferenceException exc) {
						System.out.println(exc.getMessage());
					}
				}
			}

			// tell elevator to update its state
			e.updateElevator(time);
		}
	}

	// delegate to chose pending requests for elevators
	public ArrayList<Request> getPendingRequests(int elevatorFloor, ArrayList<Request> requests) throws EmptyReferenceException {
		return myPendingRequestChooser.getPendingRequests(elevatorFloor, requests);
	}

	// get the list of all pending requests
	private ArrayList<Request> getRequestsList() {
		ArrayList<Request> temp = new ArrayList<>();
		for (Request r: pendingRequests) {
			temp.add(r);
		}
		return temp;
	}

	// check if all elevators finished their work
	public boolean isRunning() {

		boolean simulationNotComplete = false;

		// if at least one elevator still running -> return true
		for (Elevator e: elevators) {
			if (e.getDirection() != Direction.IDLE) {
				simulationNotComplete = true;
				return simulationNotComplete;
			}
		}

		// if at least one elevator isn't on the 1st floor -> return true
		for (Elevator e: elevators) {
			if (e.getCurrentFloor() != 1) {
				simulationNotComplete = true;
				return simulationNotComplete;
			}
		}

		// if there are still pending requests -> return true
		if (!pendingRequests.isEmpty()) {
			simulationNotComplete = true;
			return simulationNotComplete;
		}

		// if there is at least on elevator, who has a request
		for (Elevator e: elevators) {
			if (e.haveRequests() == true) {
				simulationNotComplete = true;
				return simulationNotComplete;
			}
		}
		return simulationNotComplete;
	}
}
