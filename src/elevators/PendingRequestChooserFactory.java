/*
Kristina McChesney
SE 450
Spring 2019
Elevator project - Part II
 */
package elevators;

// factory to chose algorithm for picking up pending requests
public class PendingRequestChooserFactory {
    public static PendingRequestChooser getPendingRequestAlgorithm () {
        return new PendingRequestChooserImpl();
    }
}