import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 class Solution {
    public static void main(String[] args) {
        // create new command tracking map and objects for testing
        CommandTracking commandTracking_test = new CommandTracking();
        CommandTracking commandTracking_test1 = new CommandTracking();

        Map<String, CommandTracking> commandTrackingTest = new HashMap<>();
        // Create a new FlowControl object
        FlowControlSystem flowControl = new FlowControlSystem(commandTrackingTest);

        // Register the test and test1 commands with the FlowControl object
        flowControl.register("test", new CommandConfig(290, 300, 5000, 2, 1000));
        flowControl.register("test1", new CommandConfig(290, 300, 5000, 2, 1000));


        //AVG 0
        System.out.println(flowControl.isAllowed("test")); // should print true
        flowControl.tracking("test", 200);

       //avg 200
        System.out.println(flowControl.isAllowed("test")); // should print true
        flowControl.tracking("test", 200);

        //avg 200
        System.out.println(flowControl.isAllowed("test")); // should print true

       //avg 0
        System.out.println(flowControl.isAllowed("test1")); // should print true
        flowControl.tracking("test1", 200);

        // avg 200
        System.out.println(flowControl.isAllowed("test")); //true
        flowControl.tracking("test", 400);

        // avg 266.66
        System.out.println(flowControl.isAllowed("test")); //true
        flowControl.tracking("test", 200);

        //avg 250
        System.out.println(flowControl.isAllowed("test")); //true
        flowControl.tracking("test", 400);

        //avg 280
        System.out.println(flowControl.isAllowed("test")); //true
        flowControl.tracking("test", 400);

        //avg 300
        System.out.println(flowControl.isAllowed("test")); //true
        flowControl.tracking("test", 500);

        //avg 500

    }
     static class CommandConfig {
         public  int X;
         public int Y;
         public int T;
         public int R;
         public int T1;

         public CommandConfig(int x, int y, int t, int r, int t1) {
             this.X = x;
             this.Y = y;
             this.T = t;
             this.R = r;
             this.T1 = t1;
         }

     }

     static class CommandTracking {
         public List<Integer> responseTimes ;
         public List<Long>timestamps;

         public CommandTracking() {
             this.responseTimes = new ArrayList<Integer>();
             this.timestamps = new ArrayList<Long>();
         }

         public void addResponseTime(Integer responseTime) {
             // Add the given response time and the current timestamp to the end of the lists
             // so that for every response time there will be a sys timestamp
             this.responseTimes.add(responseTime);
             this.timestamps.add(System.currentTimeMillis());
         }

         public double getAverageResponseTime(int numRequests) {
             // Calculate the average response time of the last numRequests requests by iterating over the responseTimes
             // and timestamps lists and only including response times that have a corresponding timestamp within the last numRequests requests
             int sum = 0;
             int count = 0;
             for (int i = this.responseTimes.size() - 1; i >= 0 && count < numRequests; i--) {
                 sum += this.responseTimes.get(i);
                 count++;
             }

             if(sum ==0 || count ==0) {
                 return 0;
             }
             System.out.println("Avergae response time: " + sum/count );
             return sum / count;
         }

         public int getNumRequestsInTimeWindow(long timeWindow) {
             // Calculate the number of requests within the given time window by iterating over the timestamps list
             // and checking if each timestamp is within the time window
             int numRequests = 0;
             for (Long timestamp : this.timestamps) {
                 if (System.currentTimeMillis() - timestamp <= timeWindow) {
                     numRequests++;
                 }
             }
             System.out.println("NumRequests In TimeWindow: "+ numRequests );
             return numRequests;
         }

         public double getAverageResponseTimeInTimeWindow(long timeWindow) {
             // Calculate the average response time within the last timeWindow milliseconds by iterating over the responseTimes and timestamps lists and only including response times that have a corresponding timestamp within the last timeWindow milliseconds
             int sum = 0;
             int count = 0;
             for (int i = this.responseTimes.size() - 1; i >= 0; i--) {
                 if (System.currentTimeMillis() - this.timestamps.get(i) <= timeWindow) {
                     sum += this.responseTimes.get(i);
                     count++;
                 }
             }
             if(sum ==0 || count ==0) {
                 System.out.println("Average response time within the time window: 0"  );
                 return 0;
             }
             System.out.println("Average response time within the time window: " + sum/count );
             return sum / count;
         }
     }

     static class FlowControlSystem {
         private final Map<String, CommandConfig> commands; // dictionary to store commands and their respective configurations
         private final Map<String, CommandTracking> commandTracking;

         public FlowControlSystem(Map<String, CommandTracking> commandTracking) {
             this.commandTracking = commandTracking;
             this.commands = new HashMap<>();
         }

         public void register(String commandName, CommandConfig commandConfig) {
             // store the command and its configuration
             // Check if the commandName or commandConfig parameters are null
             if (commandName == null || commandConfig == null) {
                 // If either of them is null, throw an IllegalArgumentException
                 throw new IllegalArgumentException("commandName or commandConfig cannot be null");}

             this.commands.put(commandName, commandConfig);
             this.commandTracking.put(commandName, new CommandTracking());
         }

         public void tracking(String commandName, int responseTime) throws IllegalCallerException {
             // if the command has not been registered,
             if (!this.commands.containsKey(commandName)) {
                 throw new IllegalArgumentException("command not registered!");
             }
             if(commandName == null ) {
                 // If either of them is null, throw an IllegalArgumentException
                 throw new IllegalArgumentException("commandName cannot be null");}

             // get the configuration for the command
             CommandConfig config = this.commands.get(commandName);

             // if the tracked calls for the command do not exist, create an empty list
             if (!this.commandTracking.containsKey(commandName)) {
                 this.commandTracking.put(commandName, new CommandTracking());
             }

             // add the response time to the list of tracked calls
             this.commandTracking.get(commandName).addResponseTime(responseTime);
         }

         public boolean isAllowed(String commandName) {
             // Get the configuration and tracking information for the given command
             CommandConfig config = this.commands.get(commandName);
             CommandTracking tracking = this.commandTracking.get(commandName);

             // Check if the average response time for the given command is greater than X within the last T ms
             if (tracking.getAverageResponseTimeInTimeWindow(config.T) > config.X){
                 // If the number of requests in the last T1 ms is greater than R, return false
                 if (tracking.getNumRequestsInTimeWindow(config.T1) > config.R) {
                     return false;
                 }

                 // If the average response time of the last R requests is greater than Y, return false
                 if (tracking.getAverageResponseTime(config.R) > config.Y) {
                     return false;
                 }
                 // if the command isAllowed we add the response time to our tracker
                 return true;

             }
             // Otherwise, return true
             return true;
         }

     }

}

