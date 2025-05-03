// Andy Nguyen
// CS 4348.501 - 2025 Spring
// Project 3: Index Files

public class project3 {
    public static void main(String[] args) {
        // print the error message if the command is not in correct format.
        if (args.length < 2) {
            System.err.println("Usage: project3 <command> <file> [...]");
            return;
        }

        String command = args[0];
        String indexFilename = args[1];

        try {
            switch (command) { // list of possible user commands
                case "create":
                    BTreeIndex.create(indexFilename);
                    break;
                case "insert":
                    long key = Long.parseLong(args[2]);
                    long value = Long.parseLong(args[3]);
                    BTreeIndex index = new BTreeIndex(indexFilename, false);
                    index.insert(key, value);
                    break;
                case "search":
                    key = Long.parseLong(args[2]);
                    index = new BTreeIndex(indexFilename, false);
                    Long foundValue = index.search(key);
                    if (foundValue != -1)
                        System.out.println(key + "," + foundValue);
                    else
                        System.out.println("Key not found.");
                    break;
                case "load":
                    String csvFile = args[2];
                    BTreeIndex.load(indexFilename, csvFile);
                    break;
                case "print":
                    index = new BTreeIndex(indexFilename, false);
                    index.print();
                    break;
                case "extract":
                    String outputFile = args[2];
                    index = new BTreeIndex(indexFilename, false);
                    index.extract(outputFile);
                    break;
                default:
                    System.err.println("Invalid command.");
            }
        } 
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
