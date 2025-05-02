// Andy Nguyen
// CS 4348.501 - 2025 Spring
// Project 3: Index Files

import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class IndexFile {
    // create an index file map
    private Map<String, Integer> indexFileMap = new HashMap<>();
    private File dataFile;
    private File indexFile;

    // index file constructor
    public IndexFile(String dataFileName, String indexFileName) {
        this.dataFile = new File(dataFileName);
        this.indexFile = new File(indexFileName);
    }

    // method to create index file
    public void createIndex() throws IOException {
        indexFileMap.clear();
        try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r")) {
            long position;
            String line;
            while ((position = raf.getFilePointer()) != -1 && (line = raf.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    indexFileMap.put(parts[0], (int) position);
                }
            }
        }
        saveIndex();
    }    

    // method to save index file
    private void saveIndex() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile))) {
            oos.writeObject(indexFileMap);
        }
    }

    // method to load index file
    @SuppressWarnings("unchecked")
    public void loadIndex() throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile))) {
            try {
                indexFileMap = (Map<String, Integer>) ois.readObject();
            } 
            catch (ClassNotFoundException e) {
                throw new IOException("Class not found while reading index file", e);
            }
        }
    }

    // method to insert record
    public void insertRecord(String key, String value) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true))) {
            bufferedWriter.write(key + "," + value);
            bufferedWriter.newLine();
        }
        createIndex();   // rebuild index
        loadIndex();     // load it into memory
    }

    // method to search record
    public String searchRecord(String key) throws IOException {
        if (indexFileMap.containsKey(key)) {
            int position = indexFileMap.get(key);
            try (RandomAccessFile file = new RandomAccessFile(dataFile, "r")) {
                file.seek(position);
                return file.readLine();
            }
        }
        return null;
    }

    // main method
    public static void main(String[] args) {
        String dataFileName = "data.txt";
        String indexFileName = "index.idx";
        IndexFile indexFile = new IndexFile(dataFileName, indexFileName);

        try {
            // Create and populate data file
            try (FileWriter writer = new FileWriter(dataFileName)) {
                writer.write("1,Value 1\n");
                writer.write("2,Value 2\n");
                writer.write("3,Value 3\n");
            }

            // Create index
            indexFile.createIndex();

            // Insert record
            indexFile.insertRecord("4", "Value 4");
            indexFile.insertRecord("5", "Value 5");
            indexFile.insertRecord("6", "Value 6");

            // Search record
            String record = indexFile.searchRecord("2");
            System.out.println("Found record: " + record);

            record = indexFile.searchRecord("5");
            System.out.println("Found record: " + record);

            record = indexFile.searchRecord("3");
            System.out.println("Found record: " + record);

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}