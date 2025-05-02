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