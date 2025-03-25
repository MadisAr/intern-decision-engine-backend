# TICKET-101 conclusion
## Changes made
I made quite a lot of changes (I'm sorry if I changed too much I got carried away:).
Biggest problem to me seemed to be wrongly implementing not fixed values for credit modifiers, but
instead some arbitrary ranges, the code was also wrongly finding the biggest possible loan we could provide. 

### Changing data transfer objects
It was a good idea by the intern to use classes to pass info between 
components, but there were two objects Decision and DecisionResponse that almost completely identical.
I removed the Decision object, created a new folder for the objects and named them more appropriately as DTO's.

### Error handle'ing
What the intern did well was to handle errors, but there seemed to be two different approaches:
returning a ResponseDTO object with a error message or throwing errors inside DecisionEngine and handle'ing
them on the controller layer.
I removed error handle'ing from DecisionEngineController and DecisionEngine and moved 
it to a GlobalExceptionHandler class and changed DecisionEngine
to not return results in case of errors.
I changed the exceptions to implement RuntimeException's instead of just throwable's because
otherwise they were getting caught by a general Exception e clause which I didn't want.

### DecisionEngine file
I noticed that in the DecisionEngineConstants file the maximum loan period was incorrectly set to 60 not 48 months so I fixed that.
The project incorrectly used id ranges for detecting people's credit scores,
but actually the task was to hard code 4 values (if i understood it correctly:), so i added a map of values to the DecisionEngineConstants file.
I also added a creditScore calculating function, before this wasn't being implemented.
The verifyInputs function seemed to be working fine so I didn't touch that

### Tests
Since there were some mistakes in the original project then the tests weren't working properly
so I changed them to fit the actual requirements.

