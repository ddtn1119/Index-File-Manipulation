// Andy Nguyen
// CS 4348.501 - 2025 Spring
// Project 3: Index Files

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BTreeNode {
    // define important constants for BTreeNode
    public static final int BLOCK_SIZE = 512;
    public static final int DEGREE = 10;
    public static final int MAX_KEYS = 2 * DEGREE - 1;
    public static final int MAX_CHILDREN = 2 * DEGREE;

    // define important data
    public long blockId, parentId;
    public int numKeys;
    public long[] keys = new long[MAX_KEYS];
    public long[] values = new long[MAX_KEYS];
    public long[] children = new long[MAX_CHILDREN];

    // parameterised constructor for BTreeNode
    public BTreeNode(long blockId) {
        this.blockId = blockId;
        this.parentId = 0;
        this.numKeys = 0;
    }

    // method to read b-tree node
    public static BTreeNode readNode(RandomAccessFile raf, long blockId) throws IOException {
        raf.seek(blockId * BLOCK_SIZE);
        byte[] buffer = new byte[BLOCK_SIZE];
        raf.readFully(buffer);

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN);
        BTreeNode btnode = new BTreeNode(bb.getLong());
        btnode.parentId = bb.getLong();
        btnode.numKeys = (int) bb.getLong();

        for (int i = 0; i < MAX_KEYS; i++) {
            btnode.keys[i] = bb.getLong();
        }
        for (int i = 0; i < MAX_KEYS; i++) {
            btnode.values[i] = bb.getLong();
        }
        for (int i = 0; i < MAX_CHILDREN; i++) {
            btnode.children[i] = bb.getLong();
        }

        return btnode;
    }

    // method to write b-tree node
    public void writeNode(RandomAccessFile raf) throws IOException {
        raf.seek(blockId * BLOCK_SIZE);
        ByteBuffer bb = ByteBuffer.allocate(BLOCK_SIZE).order(ByteOrder.BIG_ENDIAN);

        bb.putLong(blockId);
        bb.putLong(parentId);
        bb.putLong(numKeys);

        for (int i = 0; i < MAX_KEYS; i++) {
            bb.putLong(keys[i]);
        }
        for (int i = 0; i < MAX_KEYS; i++) {
            bb.putLong(values[i]);
        }
        for (int i = 0; i < MAX_CHILDREN; i++) {
            bb.putLong(children[i]);
        }

        raf.write(bb.array());
    }
}