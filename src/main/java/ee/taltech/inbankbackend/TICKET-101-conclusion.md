# TICKET-101 conclusion
## Changes made
The biggest problem to me seemed to be using some arbitrary ranges instead of the given fixed values to calculate credit scores, 
but I also changed some other stuff.

### Changing data transfer objects
It was a good idea by the intern to use classes to pass info between 
components, but there were two objects, Decision and DecisionResponse, that were almost completely identical.
I removed the Decision object, created a new folder for the objects and named them more appropriately as DTO's.

### Error handling
What the intern did well was to handle errors, but there seemed to be two different approaches:
returning a ResponseDTO object with an error message or throwing errors inside DecisionEngine and handling them on the controller layer.
I removed error handling from DecisionEngineController and DecisionEngine and moved 
it to a GlobalExceptionHandler class and changed DecisionEngine to not return results in case of errors.
I changed the exceptions to implement RuntimeExceptions instead of just throwables because
Otherwise, they were getting caught by a general Exception clause, which I didn't want.

### DecisionEngine file
I noticed that in the DecisionEngineConstants file, the maximum loan period was incorrectly set to 60, not 48 months, so I fixed that.
The project incorrectly used id ranges for detecting people's credit scores,
but actually the task was to hard code 4 values (if I understood it correctly:), so I added a map of values to the DecisionEngineConstants file.
The verifyInputs function seemed to be working fine, so I didn't touch that

### Tests
Since there were some mistakes in the original project then the tests weren't working properly
so I changed them to fit the actual requirements and later added a test for age verification functionality.