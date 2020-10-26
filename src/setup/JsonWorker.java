/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ExceptionHandling.InvalidParameterException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonWorker {

	private static int testDuration;
	private static int personGenerationTime;
	private static int numberFloors;
	private static int numberElevators;
	private static int maxCapacity;
	private static int floorTime;
	private static int doorOpenTime;
	private static int elevatorTimeOut;
	private static boolean isLoaded = false; // are the values from the file already loaded; no need to call constructor again
	private String fileName;

	private void setNumberFloors(int numberFloors) throws InvalidParameterException {this.numberFloors = numberFloors;}
	private void setNumberElevators(int numberElevators) throws InvalidParameterException {this.numberElevators = numberElevators;}
	private void setMaxCapacity(int maxCapacity) throws InvalidParameterException {this.maxCapacity = maxCapacity;}
	private void setFloorTime(int floorTime) throws InvalidParameterException {this.floorTime = floorTime;}
	private void setDoorOpenTime(int doorOpenTime) throws InvalidParameterException {this.doorOpenTime = doorOpenTime;}
	private void setElevatorTimeOut(int elevatorTimeOut) throws InvalidParameterException {this.elevatorTimeOut = elevatorTimeOut;}
	private void setTestDuration(int testDuration) throws InvalidParameterException {this.testDuration = testDuration;}
	private void setPersonGenerationTime(int personGenerationTime) throws InvalidParameterException {this.personGenerationTime = personGenerationTime;}

	public int getNumberFloors() {return numberFloors;}
	public int getNumberElevators() {return numberElevators;}
	public int getMaxCapacity() {return maxCapacity;}
	public int getFloorTime() {return floorTime;}
	public int getDoorOpenTime() {return doorOpenTime;}
	public int getElevatorTimeOut() {return elevatorTimeOut;}
	public int getTestDuration() {return testDuration;}
	public int getPersonGenerationTime() {return personGenerationTime;}

	public JsonWorker(String fileNameIn) {

		// set values from the file if not loaded yet
		if (isLoaded == false) {

			int numberFloorsIn;
			int numberElevatorsIn;
			int maxCapacityIn;
			int floorTimeIn;
			int doorOpenTimeIn;
			int elevatorTimeOutIn;
			int testDurationIn;
			int personGenerationTimeIn;

			fileName = fileNameIn;
			FileReader reader;
			try {
				reader = new FileReader(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			JSONParser jsonParser = new JSONParser();
			JSONObject jObj = null;

			try {
				jObj = (JSONObject) jsonParser.parse(reader);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
				return;
			}

			try {
				// get test values
				JSONObject testingObj = (JSONObject) jObj.get("test");
				testDurationIn = Integer.parseInt((String) testingObj.get("testDuration"));
				setTestDuration(testDurationIn);

				personGenerationTimeIn = Integer.parseInt((String) testingObj.get("personGenerationTime"));
				setPersonGenerationTime(personGenerationTimeIn);

				// get general information about building
				JSONObject buildingObj = (JSONObject) jObj.get("building");
				numberFloorsIn = Integer.parseInt((String) buildingObj.get("numberFloors"));
				setNumberFloors(numberFloorsIn);

				numberElevatorsIn = Integer.parseInt((String) buildingObj.get("numberElevators"));
				setNumberElevators(numberElevatorsIn);


				// get general information about elevators
				JSONObject elevatorObj = (JSONObject) jObj.get("elevator");
				maxCapacityIn = Integer.parseInt((String) elevatorObj.get("maxPersonPerElevator"));
				setMaxCapacity(maxCapacityIn);

				floorTimeIn = Integer.parseInt((String) elevatorObj.get("timePerFloor"));
				setFloorTime(floorTimeIn);

				doorOpenTimeIn = Integer.parseInt((String) elevatorObj.get("doorOpenTime"));
				setDoorOpenTime(doorOpenTimeIn);

				elevatorTimeOutIn = Integer.parseInt((String) elevatorObj.get("timeOut"));
				setElevatorTimeOut(elevatorTimeOutIn);

			} catch (InvalidParameterException e) {
				System.out.println(e.getMessage());
			}

			// set boolean to true after all loaded
			isLoaded = true;

		}
	}
}
