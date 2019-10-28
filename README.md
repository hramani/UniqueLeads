1. How to run project from command line with argument(Path to the input file)
java -jar UniqueLeads.jar leads.json

2. Output file will be generate at the same location with name "uniqueleads.json"

Output file with dups reconciled according to the following rules:

 - The data from the newest date should be preferred
 - duplicate IDs count as dups. Duplicate emails count as dups. Both must be unique in our dataset. Duplicate values elsewhere do not count as dups.
 - If the dates are identical the data from the record provided last in the list should be preferred.