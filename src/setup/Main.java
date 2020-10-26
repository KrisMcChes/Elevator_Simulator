/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */

package setup;

import ExceptionHandling.EmptyReferenceException;
import ExceptionHandling.InvalidParameterException;
import building.Building;
import building.Person;
import elevators.ElevatorController;
import gui.ElevatorDisplay;
import java.util.ArrayList;
import java.util.Random;

public class Main {

	public static long startTime;
	private static int testDuration;
	private static int personGenerationTime;
	private static Random randomObj = new Random(1234); // for debugging purpose the seed is fixed
	private static int personCounter = 0;
	private static Building myBuilding;
	private static ElevatorController myController;
	private static ArrayList<Person> people = new ArrayList<>();

	public static void main(String[] args) throws InvalidParameterException, EmptyReferenceException {

		// get test duration values
		JsonWorker jw = new JsonWorker("data/buildingInput.json");
		testDuration = jw.getTestDuration();
		personGenerationTime = jw.getPersonGenerationTime();

		// initialize Building and Elevator controller
		myBuilding = Building.getInstance();
		myController = ElevatorController.getInstance();

		testPart2();

		// close gui after test
		ElevatorDisplay.getInstance().shutdown();
	}

	private static void testPart2() throws InvalidParameterException, EmptyReferenceException {

		startTime = System.currentTimeMillis();
		int numFloors = myBuilding.getNumFloors();

		if (testDuration <= 0 | personGenerationTime <= 0) {
			throw new InvalidParameterException("Time can't be less than 1 second");
		}

		for (int i = 0; i < testDuration; i++) {

			if (i % personGenerationTime == 0) { // generate person every each personGenerationTime second
				int startFloor = (int) (randomObj.nextDouble() * numFloors + 1);
				int endFloor = (int) (randomObj.nextDouble() * numFloors + 1);
				if (startFloor == endFloor) {
					endFloor = (int) (randomObj.nextDouble() * numFloors + 1);
				}
				addPerson(startFloor, endFloor);
			}

			// tell all elevator to update their state
            myController.operateElevators(1000);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}

		// after we generated all people, elevator should finish their work
		System.out.println("-- Person creation complete--");

		while (myController.isRunning()) {
			System.out.println("--Waiting the simulation to complete--");
			myController.operateElevators(1000);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// output the result of the test
		printReport();
	}

	// add person to the list to generate statistics later and send to the simulation
	private static void addPerson(int startFloorIn, int endFloorIn) throws InvalidParameterException {

		// get person name
		personCounter++;
		String name = "P" + personCounter;

		// create a person
		Person p = new Person(name, startFloorIn, endFloorIn);
		System.out.printf("%s Person %s created on Floor %d, wants to go %s to Floor %d\n",
				getTimeStamp(), name, startFloorIn, ElevatorController.getDirection(startFloorIn, endFloorIn), endFloorIn);

		// add to list for statistics
		people.add(p);

		// send to the building
		try {
			myBuilding.addPerson(p);
		} catch (EmptyReferenceException e) {
			System.out.println(e.getMessage());
		}
	}

	// get readable time format
	public static String getTimeStamp() {

		long now = (System.currentTimeMillis() - startTime) / 1000;

		long hours = (now / (60 * 60)) % 24;
		now -= (hours / (60 * 60)) % 24;

		long minutes = (now / 60) % 60;
		now -= (minutes / 60) % 60;

		long seconds = now % 60;

	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	// output test statistics
	private static void printReport() {

		System.out.printf("\nSimulation complete with %d floors, %d elevators, %d seconds of person generation, 1 person each %d seconds\n\n",
				myBuilding.getNumFloors(), myController.getNumberElevators(), testDuration, personGenerationTime);

		getStatistics();

		System.out.printf("%15s %15s %15s %15s %15s %15s %15s\n", "Person", "StartFloor", "End Floor", "Direction", "Wait Time", "Ride Time", "Total Time");
		for (Person p: people) {
			System.out.printf("%15s %15d %15d %15s %15.1f %15.1f %15.1f\n",
					p.getPersonName(), p.getStartFloor(), p.getDestinationFloor(), p.getPersonTravelDirection().toString(),
					p.getWaitTime(), p.getRideTime(), p.getTotalTime());
		}
	}

	// calculate and output statistics about wait and ride time
	private static void getStatistics() {

		float totalWaitTime = 0;
		float totalRideTime = 0;

		// generate info about min wait time
		float minWaitTime = people.get(0).getWaitTime();
		String minWaitPerson = "";
		totalWaitTime += minWaitTime;
		for (int i=1; i<people.size(); i++) {
			if(people.get(i).getWaitTime() < minWaitTime){
				minWaitTime = people.get(i).getWaitTime();
				minWaitPerson = people.get(i).getPersonName();
			}
			totalWaitTime += people.get(i).getWaitTime();
		}

		// generate info about min ride time
		float minRideTime = people.get(0).getRideTime();
		String minRidePerson = "";
		totalRideTime += minRideTime;
		for (int i=1; i<people.size(); i++) {
			if(people.get(i).getRideTime() < minRideTime){
				minRideTime = people.get(i).getRideTime();
				minRidePerson = people.get(i).getPersonName();
			}
			totalRideTime += people.get(i).getRideTime();
		}

		// generate info about max wait time
		float maxWaitTime = people.get(0).getWaitTime();
		String maxWaitPerson = "";
		for (int i=1; i<people.size(); i++) {
			if(people.get(i).getWaitTime() > maxWaitTime){
				maxWaitTime = people.get(i).getWaitTime();
				maxWaitPerson = people.get(i).getPersonName();
			}
		}

		// generate info about max ride time
		float maxRideTime = people.get(0).getRideTime();
		String maxRidePerson = "";
		for (int i=1; i<people.size(); i++) {
			if(people.get(i).getRideTime() > maxRideTime){
				maxRideTime = people.get(i).getRideTime();
				maxRidePerson = people.get(i).getPersonName();
			}
		}

		System.out.printf("Avg Wait Time: %.1f sec\n", totalWaitTime/people.size());
		System.out.printf("Avg Ride Time: %.1f sec\n\n", totalRideTime/people.size());

		System.out.printf("Min Wait Time: %.1f sec (%s)\n", minWaitTime, minWaitPerson);
		System.out.printf("Min Ride Time: %.1f sec (%s)\n\n", minRideTime, minRidePerson);

		System.out.printf("Max Wait Time: %.1f sec (%s)\n", maxWaitTime, maxWaitPerson);
		System.out.printf("Max Ride Time: %.1f sec (%s)\n\n", maxRideTime, maxRidePerson);

	}
}
