# FlowControlSystem-rate-limiter
Flow control is the process of managing the rate of calls between services. The system is told to track calls between services and at any point the system can be asked, whether an api call is allowed or not .

## Problem Statement
You need to design a system that is able to control the rate of calls between services.
There are 3 parts to this problem statement. register,trackCall, isAllowed.

When a call is being tracked, you will be provided with a commandName, and response time.
When isAllowed is called, you will be provided with a commandName for which true or false need to be returned based on below criteria.

## Criteria:
If average response time of calls is greater than X ms within T ms then allow only R requests for T1 ms
If R requests average response time or average response time of total requests accumulated in T1 ms is greater than Y ms then do not allow any request for another T1 ms .
Else allow 100% request
Here T, and T1 are whole numbers(integers). Also X and Y is the response time for individual calls.
Average response time = sum of response time/No. of request .
(Should become clear with the example shown below).

The following is a guide for the interfaces you may have in your system.

An interface to register the command.
void register(String commandName,CommandConfig commandConfig);
where,
commandName = Identifier of call
commandConfig = flow control config.

An interface to track the calls between services
void tracking (String commandName,Integer responseTime);
where,
commandName = Identifier of the call
responseTime = responseTime of call in MilliSecond

An interface to permit a call
boolean isAllowed(String commandName);
where,
commandName = Identifier of call

Below is cycles for success and fail scenario .
Whichever condition will met first window will end there only
T (Success), T .....
Avg. response time in T ms does not cross the threshold limit .

T (Fail), T1 (Success), T ....
Avg response time in T ms crosses threshold limit and avg response time in T1 ms does not crosses limit .
T (Fail), T1 (Fail), T1, T …
Avg response time in T ms crossed threshold limit and avg response time in T1 ms crosses limit so do not allow any request for next T1 ms .

### Sample
The following is just a sample for your understanding.
Please remember: You are expected to write the system which mirrors production quality code, rather than just implementing these functions
```
commandName: test
commandName: test1
register(test,commandConfig)
register(test1,commandConfig)
commandConfig:
X = 290
Y =  300
T = 5000 
R = 2
T1 = 1000 
At time 1666781180(In sec): isAllowed(test); returns true  // Avg 0
At time 1666781180:  tracking(test, 200);  
At time 1666781180: isAllowed(test); returns true // Avg 200
At time 1666781180:  tracking(test, 200); 
At time 1666781181: isAllowed(test); returns true // Avg 200
 
At time 1666781181: isAllowed(test1); returns true   // Avg 0
At time 1666781181:  tracking(test1, 200); 
At time 1666781181: isAllowed(test); returns true // Avg 200
At time 1666781181:  tracking(test, 400);
At time 1666781181: isAllowed(test); returns true  // Avg 266.66
At time 1666781181:  tracking(test, 200);
At time 1666781181: isAllowed(test); returns true  // Avg 250
At time 1666781181:  tracking(test, 400);
At time 1666781182: isAllowed(test); returns true // Avg 280 
At time 1666781182:  tracking(test, 400); 
At time 1666781182: isAllowed(test); returns true  //  Avg is 300 which is greater than 290 but nos of request in t1 ms is 0
At time 1666781182:  tracking(test, 500); 
At time 1666781183: isAllowed(test); returns true  // Avg is 500 and no of request in t1 ms is 1
At time 1666781183:  tracking(test, 200);
At time 1666781183: isAllowed(test); returns false  // No of request in already reached   2   and avg is 350  so do not allow it , and begin next t1 window(in which do not allow any request) 
At time 1666781183: isAllowed(test); returns false // As avg was 350 for 2 request so do not allow  any request in t1 window 
At time 1666781183: isAllowed(test1); returns true  // Avg 200
At time 1666781183:  tracking(test1, 200);
```

## Requirements P0
Implement the above with appropriate assumptions for the example shown above.
Optimize your solution for time/space complexity taking reasonable tradeoffs.
Think of your system as a central tool/library used by multiple teams.
You can simulate the operations of register,isAllowed and tracking as shown in the example, using a main function or test cases
You should have a working code that demonstrates the same.
Handle error scenarios appropriately
Requirements P1
Percentile computation of latency and error rate inclusion for making algo generic
Things to keep in mind
You are only allowed to use in-memory data structures
You are NOT allowed to use any databases
You are NOT required to have a full-fledged web service or APIs exposed
A working code is ABSOLUTELY NECESSARY. Evaluation will not be done if your code is not running. So ensure you time yourselves accordingly
You are required to showcase the working of the above concept
Just a main class that simulates the above operations is enough
Should you have any doubts, you are allowed to make appropriate assumptions, as long as you can explain them during the evaluation.
You are allowed to code on your favourite IDEs as long as you paste the code back into the tool within the allotted time frame
You are NOT allowed to use any in-built library
How you will be evaluated
You are expected to write production quality code while implementing the requirements.

We look for the following:

Separation of concerns
Abstractions
Application of OO design principles
Testability
Code readability
Language proficiency
