/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

import ExceptionHandling.InvalidParameterException;
import building.Request;
import setup.JsonWorker;
import building.Person;
import gui.ElevatorDisplay;
import setup.Main;
import java.util.ArrayList;
import java.util.Collections;

public class Elevator {

	// general elevator info
	private int elevatorNumber;
	private int maxCapacity;	    // max people per Elevator
	private int speedPerFloor;		// speed of the elevator (1000 ms = 1 sec per floor)
	private boolean doorsOpen;		// if the door is open
	private int doorOpenTime;		// how long the door will be open
	private int doorOpenCounter;	// to keep track of how long the E doors have been open
	private int elevatorTimeOut;	// when E goes idle
	private int timeOutCounter; 	// to keep track of how long before E goes IDLE
	// movement
    private Direction elevatorDirection;
	private int currentFloor;		// which floor the elevator at
	// riders
	private ArrayList<Person> riders = new ArrayList<>();			// hold person object here: P10, P12
	private ArrayList<Request> floorRequests = new ArrayList<>(); 	// contains floor requests 7-UP; 15-DOWN
	private ArrayList<Integer> stopFloors = new ArrayList<>(); 		// combined list of all stops elevator has to do (riders and floor requests)

	// constructor
	public Elevator() throws InvalidParameterException {

		// load general elevator information from file
		JsonWorker jw = new JsonWorker("data/buildingInput.json");

		int maxCapacityIn = jw.getMaxCapacity();
		if (maxCapacityIn < 1) {
			throw new InvalidParameterException("Elevator should fit at least 1 person. Value passed: "+maxCapacityIn);
		}

		int speedPerFloorIn = jw.getFloorTime();
		if (speedPerFloorIn < 1) {
			throw new InvalidParameterException("Speed can't be less than zero. Value "+speedPerFloorIn);
		}

		int doorOpenTimeIn = jw.getDoorOpenTime();
		if (doorOpenTimeIn < 1) {
			throw new InvalidParameterException("Doors have to be open at least for 1 second. Value passed "+doorOpenTimeIn);
		}

		int elevatorTimeOutIn = jw.getElevatorTimeOut();
		if (elevatorTimeOutIn < 1) {
			throw new InvalidParameterException("Elevator should go IDLE at least after 1 second. Value passed "+elevatorTimeOutIn);
		}

		// set number of max people that can be in the E
		maxCapacity = maxCapacityIn;

		// set speed per floor
		speedPerFloor = speedPerFloorIn;

		// how long the doors should stay open
		doorOpenTime = doorOpenTimeIn;

		// how long before elevator goes IDLE
		elevatorTimeOut = elevatorTimeOutIn;


		// all elevators are on the first floor by default when created
		elevatorDirection = Direction.IDLE;
		currentFloor = 1;
		doorsOpen = false;
		doorOpenCounter = 0;
		timeOutCounter = 0;
	}

	// set elevator values
	public void setElevatorNumber(int elevatorNumberIn) throws InvalidParameterException {
		if (elevatorNumber < 0) {
			throw new InvalidParameterException("Number of elevator can't be less than 0");
		}
		elevatorNumber = elevatorNumberIn;}

	// getters
    public int getCurrentFloor () { return currentFloor; }
    public int getElevatorNumber() {return elevatorNumber;}
    public Direction getDirection() { return elevatorDirection; }

    // add floor request to pick up a person
	public void addFloorRequest(Request r) throws InvalidParameterException {

		if (r != null) {
			int floor = r.getFloor();
			Direction direction = r.getDirection();

			// don't duplicate the request if already in the list
			if (!floorRequests.isEmpty()) {
				for (Request fr : floorRequests) {
					if (fr.getFloor() == floor && fr.getDirection() == direction)
						System.out.println("Elevator already has this request");
					continue;
				}
			}
			// otherwise add to the list
			floorRequests.add(r);    // add to floor requests
			addToStopFloors(floor);    // add to combined list of stop floors
			System.out.printf("%s Elevator %d New Floor request to go to Floor %d-%s [Floor Requests: %s][Rider Requests: %s]\n",
					Main.getTimeStamp(), elevatorNumber, floor, r.getDirection(), getFloorRequestString(), getRidersRequestString());
		}
	}

    // add to combined list of stops
	private void addToStopFloors(int stop) throws InvalidParameterException {

		if (stop > ElevatorController.getNumberFloors() | stop < 0) { // if requested floor is out of range
			throw new InvalidParameterException("There is no floor " + stop);
		}

		// if floor isn't in the list -> add
		if (!stopFloors.contains(stop)) {
			stopFloors.add(stop);
		} else
			System.out.println("Elevator is already going to the floor");
	}

    // when arrived at a floor, remove from both lists of requests and stops
	private void removeFromStopFloors() {

		// remove from stops
		if (!stopFloors.isEmpty()) {
			int position = stopFloors.indexOf(currentFloor);
			stopFloors.remove(position);
		}

		// remove from requests
		if (!floorRequests.isEmpty()) {
			for (Request r : floorRequests) {
				if (r.getFloor() == currentFloor) {
					floorRequests.remove(r);
					break;
				}
			}
		}
	}

	// if pending request passed, add to lists
	public void addPendingRequests(ArrayList<Request> pendingRequests) throws InvalidParameterException {

		if (!pendingRequests.isEmpty()) {
			for (Request r : pendingRequests) {
				addFloorRequest(r);
			}

			// check that elevator direction is set to the direction of the first (priority request)
			elevatorDirection = pendingRequests.get(0).getDirection();
		}
	}

	// get information about current (priority) request if present
	public Request getCurrentRequest() {

		if (!floorRequests.isEmpty()) {
			return floorRequests.get(0);
		} else
			return null;
	}

	// get list of riders for output
	public String getRidersString(){
		String personsInElevator = "";

		for (Person p: riders){
			personsInElevator = personsInElevator + p.getPersonName() + " ";
		}
		return personsInElevator;
	}

    // get list of all stops for output
    public String getRidersRequestString() {
        String allRequests = "";

        if (!stopFloors.isEmpty()){
            for (int i=0; i < stopFloors.size(); i++){
                allRequests = allRequests + stopFloors.get(i) + " ";
            }
        }
        return allRequests;
    }

    // get floor requests for output
    public String getFloorRequestString() {
		String allRequests = "";

		if (!floorRequests.isEmpty()){
			for (int i=0; i < floorRequests.size(); i++){
				String dir = floorRequests.get(i).getDirection().toString().substring(0,1);
				allRequests = allRequests + floorRequests.get(i).getFloor() + "-" + dir + " ";
			}
		}
		return allRequests;
	}

	// check if elevator is still working on requests
	public boolean haveRequests() {

		if (!stopFloors.isEmpty())
			return true;

		return false;
	}

	// gui and output when doors open
	private void openDoors(){

		if (stopFloors.contains(currentFloor)) // if arrived to requested floor, remove request from the list
			removeFromStopFloors();

		doorsOpen = true;

		System.out.printf("%s Elevator %d Doors Open\n", Main.getTimeStamp(), elevatorNumber);

		// gui
		ElevatorDisplay.getInstance().openDoors(elevatorNumber);

		if (elevatorDirection == Direction.DOWN) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);
		}
		else if (elevatorDirection == Direction.UP) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.UP);
		}
		else if (elevatorDirection == Direction.IDLE) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
		}
	}

	// gui and output when doors closed
	private void closeDoors(){

		doorsOpen = false;

		System.out.printf("%s Elevator %d Doors Close\n", Main.getTimeStamp(), elevatorNumber);

		// gui
		ElevatorDisplay.getInstance().closeDoors(elevatorNumber);

		if (elevatorDirection == Direction.DOWN) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);
		}
		else if (elevatorDirection == Direction.UP) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.UP);
		}
		else if (elevatorDirection == Direction.IDLE) {
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
		}
	}

	// if it's person's destination -> exit elevator and get to floor
	private void exitElevator() throws InvalidParameterException {

		ArrayList<Person> temp = new ArrayList<>(); // to hold "done riders" list

		if (!riders.isEmpty()) { // if there is anyone in the elevator

			for (Person p : riders) {
				if (p.getDestinationFloor() == currentFloor) { 	// if it's person's requested floor
					riders.remove(p); 		// take out of riders list
					temp.add(p);			// add to the list that will be sent to the floor
					System.out.printf("%s Person %s has left Elevator %d [Riders: %s]\n",
							Main.getTimeStamp(), p.getPersonName(), elevatorNumber, getRidersString());
					try {
						p.setRideTimeFinish(System.currentTimeMillis()); // stop ride timer
					} catch (InvalidParameterException e) {
						System.out.println(e.getMessage());
					}
				}
				break;
			}

			// send this temp list to the building and the to the floor
			if (!temp.isEmpty())
				ElevatorController.getInstance().finishedRide(currentFloor, temp);

			// update gui
			if (elevatorDirection == Direction.DOWN) {
				ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);
			}
			else if (elevatorDirection == Direction.UP) {
				ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.UP);
			}
			else if (elevatorDirection == Direction.IDLE) {
				ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
			}
		}
	}

	// get information from the building if there are any people who want to get in -> add as riders
	private void enterElevator() throws InvalidParameterException {

		ArrayList<Person> temp = ElevatorController.getInstance().getRiders(currentFloor, elevatorDirection);

		if (!temp.isEmpty()) { 	// check if list isn't empty
			for (Person p : temp) {
				if (riders.size() < maxCapacity) { 	// if elevator can still fit people
					riders.add(p);					// add to elevator
					try {
						p.setWaitTimeFinish(System.currentTimeMillis());	// stop wait timer
						p.setRideTimeStart(System.currentTimeMillis());		// start ride timer
					} catch (InvalidParameterException e) {
						System.out.println(e.getMessage());
					}
					stopFloors.add(p.getDestinationFloor());			// add to elevator stops

					System.out.printf("%s Person %s entered Elevator %d [Riders: %s]\n",
						Main.getTimeStamp(), p.getPersonName(), elevatorNumber, getRidersString());
					System.out.printf("%s Elevator %d Rider Request made for Floor %d [Current Floor Requests %s][Current Rider Requests %s]\n",
							Main.getTimeStamp(), elevatorNumber, p.getDestinationFloor(), getFloorRequestString(), getRidersRequestString());

					// update gui
					if (elevatorDirection == Direction.DOWN) {
						ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);
					}
					else if (elevatorDirection == Direction.UP) {
						ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.UP);
					}
					else if (elevatorDirection == Direction.IDLE) {
						ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
					}

				} else {
					System.out.println("Elevator is full"); // nothing is implemented here in this version (shouldn't be the case in our test)
					return;
				}
			}
		}
	}

	// check if elevator has to move or open doors, etc.
	public void updateElevator(int speedPerFloor) throws InvalidParameterException {

		int run = 1;
		while (run == 1) { // update elevator until otherwise specified

			// DOORS OPEN -> let people in/out
			if (doorsOpen == true) {

				// ENTER: if there is anyone who wants to go at the same direction
				enterElevator();

				// EXIT: if there is anyone who's desired floor == current floor -> move out
				exitElevator();

				doorOpenCounter++;

				// close the doors when counter gets to door open time
				if (doorOpenCounter >= doorOpenTime) {
					closeDoors();
				}
				return;
			}

			// DOORS CLOSED -> check requests
			else {

				// if have REQUESTS
				if (!stopFloors.isEmpty()) {

					// ON THE requested FLOOR -> open doors
					if (stopFloors.contains(currentFloor)) {
						if (!floorRequests.isEmpty()) {    // if have to pick somebody up, check the direction of the priority request
							Request r = getCurrentRequest();
							elevatorDirection = r.getDirection();
						}

						System.out.printf("%s Elevator %d has arrived at Floor %d [Current Floor Requests %s][Current Rider Requests %s]\n",
								Main.getTimeStamp(), elevatorNumber, currentFloor, getFloorRequestString(), getRidersRequestString());

						openDoors();

						return;
					}

					// MOVING because have requests
					else {

						if (!stopFloors.isEmpty()) {
							int maxFloor = Collections.max(stopFloors); // get max value in the requests list and compare it to the current floor

							// actual elevator movement here
							if (maxFloor < currentFloor) {
								// moving DOWN to the request
								elevatorDirection = Direction.DOWN;

								System.out.printf("%s Elevator %d moving from Floor %d to Floor %d [Current Floor Requests %s][Current Rider Requests %s]\n",
										Main.getTimeStamp(), elevatorNumber, currentFloor, currentFloor - 1, getFloorRequestString(), getRidersRequestString());

								ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);

								currentFloor--;
								run = 0; // exit after moved to one floor

							} else {
								// moving UP to the request
								elevatorDirection = Direction.UP;

								System.out.printf("%s Elevator %d moving from Floor %d to Floor %d [Current Floor Requests %s][Current Rider Requests %s]\n",
										Main.getTimeStamp(), elevatorNumber, currentFloor, currentFloor + 1, getFloorRequestString(), getRidersRequestString());

								ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor + 1, riders.size(), ElevatorDisplay.Direction.UP);

								currentFloor++;
								run = 0; // exit after moved to one floor
							}
						}
					}
				}

				// NO REQUESTS -> go IDLE
				else {
					// start the counter
					timeOutCounter++;

					// set direction to IDLE right away
					elevatorDirection = Direction.IDLE;

					// when counter aexpired, move to the 1st floor
					if (timeOutCounter > elevatorTimeOut) {
						setIdle();
						return;
					}
					// update gui
					ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
					return;
				}
			}
		}
	}

	// move to the 1st floor if been IDLE for certain amount of time
	private void setIdle(){

		elevatorDirection = Direction.IDLE;

		// move to 1st floor
		while (currentFloor != 1) {

			currentFloor --;

			System.out.printf("%s Elevator %d moving from Floor %d to Floor %d\n",
					Main.getTimeStamp(), elevatorNumber, currentFloor + 1, currentFloor);

			// update gui
			ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.DOWN);

			break;
		}

		// clear up the counter
		if (currentFloor == 1)
			timeOutCounter = 0;

		// update gui
        ElevatorDisplay.getInstance().updateElevator(elevatorNumber, currentFloor, riders.size(), ElevatorDisplay.Direction.IDLE);
	}
}

