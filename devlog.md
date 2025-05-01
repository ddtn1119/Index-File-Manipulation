## CS 4348 Project 3 Devlog

[2025-01-05 9:01]
Hello, this is Andy! Welcome to the Project 3 Devlog.
So, this project is about creating and managing index files...
The program should allow the user to perform operations on an index file (create, insert, search, load, print, extract).
The index file will be devided into blocks of 512 bytes.
Each node of the btree will fit in one 512 byte block, and the file header will use the entire first block. New nodes will be appended to the end of the file.
The btree should have minimal degree 10. This should give 19 key/value pairs, and 20 child pointers.
The sequence of keys, values, and child pointers correspond to each other.
First, I will be creating a simple Java program to create, insert, and search an index file using Map/HashMap.