/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package building;

import ExceptionHandling.InvalidParameterException;
import elevators.Direction;
import elevators.ElevatorController;
import org.omg.CORBA.INV_FLAG;

public class Person {

	// general person information
	private String personName;
	private int startFloor;
	private int destinationFloor;
	private Direction personTravelDirection;
	// time statistics
	private float waitTime;
	private long waitTimeStart;
	private long waitTimeFinish;
	private float rideTime;
	private long rideTimeStart;
	private long rideTimeFinish;
	private float totalTime;

	// constructor
	public Person(String personNameIn, int startFloorIn, int destinationFloorIn) throws InvalidParameterException {
		if (personNameIn == null) {
			throw new InvalidParameterException("Need person's name");
		}
		if (startFloorIn < 1 | startFloorIn > Building.getInstance().getNumFloors()) {
			throw new InvalidParameterException("Person floor has to be withing the range of Building floors. Created on floor " + startFloorIn);
		}
		if (destinationFloorIn < 1 | destinationFloorIn > Building.getInstance().getNumFloors()) {
			throw new InvalidParameterException("Person destination floor has to be withing the range of Building floors. Created on floor " + destinationFloorIn);
		}

		personName = personNameIn;
		startFloor = startFloorIn;
		destinationFloor = destinationFloorIn;
		personTravelDirection = ElevatorController.getDirection(startFloor, destinationFloor);

	}

	// set start and stop timers
	public void setRideTimeStart(long rideTimeStartIn) throws InvalidParameterException {
		if (rideTimeStartIn < 0) {
			throw new InvalidParameterException("Time can't be less than 0");
		}
		rideTimeStart = rideTimeStartIn;
	}

	public void setRideTimeFinish(long rideTimeFinishIn) throws InvalidParameterException {
		if (rideTimeFinishIn < 0 ) {
			throw new InvalidParameterException("Time can't be less than 0");
		}
		rideTimeFinish = rideTimeFinishIn;
	}

	public void setWaitTimeStart(long waitTimeStartIn) throws InvalidParameterException {
		if (waitTimeStartIn < 0 ) {
			throw new InvalidParameterException("Time can't be less than 0");
		}
		waitTimeStart = waitTimeStartIn;
	}

	public void setWaitTimeFinish(long waitTimeFinishIn) throws InvalidParameterException {
		if (waitTimeFinishIn < 0) {
			throw new InvalidParameterException("Time can't be less than 0");
		}
		waitTimeFinish = waitTimeFinishIn;
	}

	// calculate total ride and wait time
	private void calculateRideTime () {	rideTime = (rideTimeFinish - rideTimeStart) / 1000; }
	private void calculateWaitTime () {	waitTime = (waitTimeFinish - waitTimeStart) / 1000; }
	private void calculateTotalTime () { totalTime = (rideTime + waitTime); }

	// getters
	public String getPersonName() {	return personName; }
	public int getStartFloor() {return startFloor;}
	public int getDestinationFloor() { return destinationFloor; }
	public Direction getPersonTravelDirection(){ return personTravelDirection; }

	public float getRideTime() {
		calculateRideTime();
		return rideTime;
	}
	public float getWaitTime() {
		calculateWaitTime();
		return waitTime;
	}

	public float getTotalTime() {
		calculateTotalTime();
		return totalTime;
	}
}
