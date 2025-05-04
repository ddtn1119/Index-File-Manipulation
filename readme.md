## CS 4348 Project 3: Index Files

This is an interactive program that creates and manages index files. The index files contain/represent a B-tree.

How to interact with this program?
* First, open a new terminal in your IDE and compile all Java files using this command: `javac *.java`.
* Type in any commands (one at a time):
1. Create a new index file. Example: "`project3 create test.idx`".
2. Insert a new pair of key and value into the B-tree. Example: "`project3 insert test.idx 15 100`".
3. Search for the key in the B-tree. Example: "`project3 search test.idx 15`".
4. Load the input file into the B-tree (add the values in the input CSV file to the B-tree). Example: "`project3 load test.idx input.csv`".
5. Print the index file to the console. Example: "`project3 print test.idx`".
6. Extract the index file to the output file (print the B-tree to an output CSV file). Example: "`project3 extract test.idx output.csv`".

Enjoy and have fun!