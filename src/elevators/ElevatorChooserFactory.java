/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

// factory for elevator chooser algorithm
public class ElevatorChooserFactory {

    public static ElevatorChooser getElevatorAlgorithm () {
        return new ElevatorChooserImpl();
    }
}
