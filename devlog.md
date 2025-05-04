## CS 4348 Project 3 Devlog

[2025-05-01 9:01]
Hello, this is Andy! Welcome to the Project 3 Devlog.
So, this project is about creating and managing index files...
The program should allow the user to perform operations on an index file (create, insert, search, load, print, extract).
The index file will be devided into blocks of 512 bytes.
Each node of the btree will fit in one 512 byte block, and the file header will use the entire first block. New nodes will be appended to the end of the file.
The btree should have minimal degree 10. This should give 19 key/value pairs, and 20 child pointers.
The sequence of keys, values, and child pointers correspond to each other.
First, I will be creating a simple Java program to create, insert, and search an index file using Map/HashMap.

[2025-05-02 17:52]
I tested my program, but the last two records (even though they exist in the data file) are not displayed on the console.
Updated: This issue was caused by inaccurate byte position when I called `createIndex()` after inserting new records, which incorrectly assumed that each line in the file is terminated by exactly 1 character, however, on Windows, line endings are typically `\r\n` (2 characters). Instead, I used `RandomAccessFile` object to read lines and track the exact byte offset. I also ensured that `insertRecord()` properly creates and loads index.

[2025-05-03 17:28]
The last program was only the simple version of the actual project utilising `HashMap` so that I could see and learn how to actively manipulate index files. After that, I updated my program to the actual low-level, disk-based b-tree with binary formats for both header and node blocks, and limiting memory usage to a maximum of 3 nodes. First, I defined important constants, including a fixed block size of 512 bytes, degree of 10, 19 keys (152-byte), 19 values (152-byte), and 20 child pointers (160-byte). Second, I implemented the main program structure (`project3`) and provided a list of possible valid commands (`create`, `insert`, `search`, `load`, `print`, and `extract`). Third, I implemented a new `BTreeNode` class with important fields and methods (`readNode` and `writeNode`) to create, read, and write new nodes to the B-tree. Fourth, I implemented the new `BTreeIndex` class that handles operations like initialising new index files, loading and updating header, inserting keys & values, searching for keys, creating index files, loading from CSV file into the B-tree, printing all key-value nodes to the console, and writing all keys & values to CSV file (extracting).

[2025-05-03 18:10]
I would say that the process of implementing `BTreeIndex` was very long because there were many helper methods to create that helps the main methods of operations, which makes it sometimes difficult to follow. I must make sure that all methods and the main program (`project3`) connect and interact with each other.
I will be testing all commands right now...
So for `create` command, if the file already exists, error message should be printed, which means every time I want to test this command, I have to delete and recreate this file. And the file should remain unmodified.
The `insert` command works as expected. New node is added to the b-tree.
The `search` command also works as expected. It returns the right result from the user.
The `load` command is successful. I tested it by searching for a value from the input CSV file and it returns the correct result.
The `print` command is successful.
The `extract` command is successful. New output CSV files are created as expected. And they are remain unmodified.
The whole implementation of B-tree is like implementing a regular tree data structure with root and nodes and all the operations involving manipulating a tree.