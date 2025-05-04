// Andy Nguyen
// CS 4348.501 - 2025 Spring
// Project 3: Index Files

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BTreeIndex {
    public static final int BLOCK_SIZE = 512;
    private static final String MAGIC = "4348PRJ3"; // 8-byte ASCII
    private RandomAccessFile raf;
    private long rootBlockId;
    private long nextBlockId;

    // constructor for BTreeIndex
    public BTreeIndex(String fileName, boolean createNew) throws IOException {
        File file = new File(fileName);

        if (createNew) {
            if (file.exists()) {
                throw new IOException("File already exists.");
            } 
            this.raf = new RandomAccessFile(file, "rw");
            initialiseFile();
        } 
        else {
            if (!file.exists()) {
                throw new IOException("Index file does not exist. Check the name of your index file and try again or create a new index file.");
            } 
            this.raf = new RandomAccessFile(file, "rw");
            loadHeader();
        }
    }

    // method to initialise index file
    private void initialiseFile() throws IOException {
        this.rootBlockId = 0;
        this.nextBlockId = 1;

        ByteBuffer bb = ByteBuffer.allocate(BLOCK_SIZE).order(ByteOrder.BIG_ENDIAN);
        bb.put(MAGIC.getBytes());
        bb.position(8);
        bb.putLong(rootBlockId);
        bb.putLong(nextBlockId);
        raf.seek(0);
        raf.write(bb.array());
    }

    // method to load header
    private void loadHeader() throws IOException {
        raf.seek(0);
        byte[] buffer = new byte[BLOCK_SIZE];
        raf.readFully(buffer);
        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN);

        byte[] magicBytes = new byte[8];
        bb.get(magicBytes);
        String fileMagic = new String(magicBytes);
        if (!fileMagic.equals(MAGIC)) {
            throw new IOException("Invalid index file format.");
        } 

        rootBlockId = bb.getLong();
        nextBlockId = bb.getLong();
    }

    // method to update header
    private void updateHeader() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(BLOCK_SIZE).order(ByteOrder.BIG_ENDIAN);
        bb.put(MAGIC.getBytes());
        bb.position(8);
        bb.putLong(rootBlockId);
        bb.putLong(nextBlockId);
        raf.seek(0);
        raf.write(bb.array());
    }

    // methods to allocate, save, and load nodes
    private BTreeNode allocateNode() {
        BTreeNode btnode = new BTreeNode(nextBlockId++);
        return btnode;
    }

    private void saveNode(BTreeNode btnode) throws IOException {
        btnode.writeNode(raf);
    }
    
    private BTreeNode loadNode(long blockId) throws IOException {
        return BTreeNode.readNode(raf, blockId);
    }    

    // method to insert a new pair of key and value to the b-tree
    public void insert(long key, long value) throws IOException {
        if (rootBlockId == 0) {
            BTreeNode root = allocateNode();
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            rootBlockId = root.blockId;
            saveNode(root);
            updateHeader();
            return;
        }
    
        BTreeNode root = loadNode(rootBlockId);
        BTreeNode newChild = insertNonFull(root, key, value);
    
        if (newChild != null) {
            BTreeNode newRoot = allocateNode();
            newRoot.keys[0] = newChild.keys[0];
            newRoot.values[0] = newChild.values[0];
            newRoot.children[0] = root.blockId;
            newRoot.children[1] = newChild.blockId;
            newRoot.numKeys = 1;
            root.parentId = newRoot.blockId;
            newChild.parentId = newRoot.blockId;
    
            rootBlockId = newRoot.blockId;
            saveNode(root);
            saveNode(newChild);
            saveNode(newRoot);
            updateHeader();
        } 
        else {
            saveNode(root);
        }
    }

    // method to insert the node to a b-tree even if it is non-full.
    private BTreeNode insertNonFull(BTreeNode node, long key, long value) throws IOException {
        int i = node.numKeys - 1;
    
        if (node.children[0] == 0) {  // leaf node
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.numKeys++;
            return null;
        } 
        else {
            // find child to descend
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            BTreeNode child = loadNode(node.children[i]);
            if (child.numKeys == BTreeNode.MAX_KEYS) {
                BTreeNode sibling = splitChild(node, i, child);
                if (key > node.keys[i]) {
                    child = sibling;
                }
            }
            BTreeNode newChild = insertNonFull(child, key, value);
            saveNode(child);
            return newChild;
        }
    }
    
    // method to split the child
    private BTreeNode splitChild(BTreeNode parent, int index, BTreeNode fullChild) throws IOException {
        BTreeNode newChild = allocateNode();
        newChild.parentId = parent.blockId;
    
        newChild.numKeys = BTreeNode.DEGREE - 1;
        for (int j = 0; j < BTreeNode.DEGREE - 1; j++) {
            newChild.keys[j] = fullChild.keys[j + BTreeNode.DEGREE];
            newChild.values[j] = fullChild.values[j + BTreeNode.DEGREE];
        }
    
        if (fullChild.children[0] != 0) {
            for (int j = 0; j < BTreeNode.DEGREE; j++) {
                newChild.children[j] = fullChild.children[j + BTreeNode.DEGREE];
            }
        }
    
        fullChild.numKeys = BTreeNode.DEGREE - 1;
    
        for (int j = parent.numKeys; j >= index + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = newChild.blockId;
    
        for (int j = parent.numKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }
    
        parent.keys[index] = fullChild.keys[BTreeNode.DEGREE - 1];
        parent.values[index] = fullChild.values[BTreeNode.DEGREE - 1];
        parent.numKeys++;
    
        saveNode(fullChild);
        saveNode(newChild);
        return newChild;
    }    
    
    // method to search for key in a b-tree
    public long search(long key) throws IOException {
        if (rootBlockId == 0) return -1;
        return searchRecursive(rootBlockId, key);
    }
    
    // helper method to search for keys in a b-tree recursively
    private long searchRecursive(long blockId, long key) throws IOException {
        BTreeNode node = loadNode(blockId);
    
        int i = 0;
        while (i < node.numKeys && key > node.keys[i]) {
            i++;
        }
    
        if (i < node.numKeys && key == node.keys[i]) {
            return node.values[i];
        }
    
        if (node.children[0] == 0) {
            return -1; // leaf and not found
        }
    
        return searchRecursive(node.children[i], key);
    }

    // method to create index file
    public static void create(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            throw new IOException("File already exists.");
        }
    
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // write header: magic number, rootBlockId = 0, nextBlockId = 1
            byte[] header = new byte[BLOCK_SIZE];
            ByteBuffer buffer = ByteBuffer.wrap(header);
            buffer.put("4348PRJ3".getBytes()); // 8 bytes magic
            buffer.putLong(0L); // rootBlockId
            buffer.putLong(1L); // nextBlockId
            raf.write(header);
        }
    }
    
    // method to load index file
    public static void load(String indexFilename, String csvFilename) throws IOException {
        BTreeIndex index = new BTreeIndex(indexFilename, false);
        File csvFile = new File(csvFilename);
        if (!csvFile.exists()) throw new IOException("CSV file does not exist.");
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) continue;
                long key = Long.parseLong(parts[0].trim());
                long value = Long.parseLong(parts[1].trim());
                index.insert(key, value);
            }
        }
    }

    // method to print the b-tree
    public void print() throws IOException {
        if (rootBlockId == 0) return;
        printRecursive(rootBlockId);
    }
    
    // helper method for printing the b-tree recursively (in order)
    private void printRecursive(long blockId) throws IOException {
        BTreeNode node = loadNode(blockId);
        for (int i = 0; i < node.numKeys; i++) {
            if (node.children[i] != 0) {
                printRecursive(node.children[i]);
            }
            System.out.println(node.keys[i] + "," + node.values[i]);
        }
        if (node.children[node.numKeys] != 0) {
            printRecursive(node.children[node.numKeys]);
        }
    }

    // method to extract CSV file
    public void extract(String outputCsvFile) throws IOException {
        File outFile = new File(outputCsvFile);
        if (outFile.exists()) {
            throw new IOException("Output file already exists. Please create a new file.");
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            extractRecursive(rootBlockId, writer);
        }
    }
    
    // helper method for extracting CSV file recursively
    private void extractRecursive(long blockId, BufferedWriter writer) throws IOException {
        if (blockId == 0) return;
        BTreeNode node = loadNode(blockId);
        for (int i = 0; i < node.numKeys; i++) {
            if (node.children[i] != 0) {
                extractRecursive(node.children[i], writer);
            }
            writer.write(node.keys[i] + "," + node.values[i]);
            writer.newLine();
        }
        if (node.children[node.numKeys] != 0) {
            extractRecursive(node.children[node.numKeys], writer);
        }
    }    
}