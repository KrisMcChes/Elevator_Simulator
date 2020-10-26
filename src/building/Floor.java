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
import setup.Main;
import java.util.ArrayList;

public class Floor {

	private int floorNumber;                                 // floor ID
	private ArrayList<Person> waitList = new ArrayList<>();  // people waiting for an Elevator
	private ArrayList<Person> doneList = new ArrayList<>();  // people completed their trips

	// constructor
	public Floor(int floorNumberIn) throws InvalidParameterException {
		if (floorNumberIn < 1) {
			throw new InvalidParameterException("Floor number can'be less than 1. Value received: "+floorNumberIn);
		}
		floorNumber = floorNumberIn;
	}

	// add people to the floor when created
	public void addToFloor(Person person) throws EmptyReferenceException {
		if (person == null) {
			throw new EmptyReferenceException("No person parameter passed");
		}
			waitList.add(person);
	}

	// remove from the floor when people enter the elevator
	private void removeFromWaitList(ArrayList<Person> startedRide) {
		if (!startedRide.isEmpty()) {
			for (Person p : startedRide) {
				waitList.remove(p);
				System.out.printf("%s Person %s has left Floor %d\n", Main.getTimeStamp(), p.getPersonName(), floorNumber);
			}
		}
	}

	// if there is anyone on the floor who wants to get in the elevator that goes the same direction
	public ArrayList<Person> getRiders (Direction direction) {
		ArrayList<Person> temp = new ArrayList<>();
		if (!waitList.isEmpty()) {
			for (Person p : waitList) {
				if (p.getPersonTravelDirection() == direction || direction == Direction.IDLE) {
					temp.add(p); // list of people who want to become riders
				}
			}
		}

		removeFromWaitList(temp); // remove from the floor

		return temp;
	}

	// add people who moved from the elevator to this floor
	public void addToDoneList(ArrayList<Person> finishedRiders) {
		if (!finishedRiders.isEmpty()) {
			for (Person p : finishedRiders) {
				doneList.add(p);
				System.out.printf("%s Person %s entered Floor %d\n", Main.getTimeStamp(), p.getPersonName(), floorNumber);
			}
		}
	}
}


