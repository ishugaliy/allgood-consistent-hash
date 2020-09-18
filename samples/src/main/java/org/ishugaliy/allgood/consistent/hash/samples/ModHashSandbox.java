package org.ishugaliy.allgood.consistent.hash.samples;

import org.ishugaliy.allgood.consistent.hash.node.SimpleNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Sandbox allows checking load distribution with `mod n` hash function.
 * <p>
 *
 * Case:
 * 1. Create basic implementation of `Mod N Hash` {@link ModHash}
 * 2. Add {@link ModHashSandbox#NODES_COUNT} nodes to the hash
 * 3. Send {@link ModHashSandbox#REQUEST_COUNT} requests by locating node with {@link ModHash#locate(String)}
 *    and gather nodes hits statistics.
 * 4. Randomly remove {@link ModHashSandbox#NODES_COUNT_TO_REMOVE} nodes from hash
 * 5. Repeat sending request from #3
 * 6. Print node miss and load distribution reports
 *
 * @author Yuriy Shugaliy
 */
public class ModHashSandbox {

    private static final int NODES_COUNT = 100;
    private static final int NODES_COUNT_TO_REMOVE = 1;
    private static final int REQUEST_COUNT = 100_000;

    public static void main(String[] args) {
        List<SimpleNode> nodes = buildNodes();
        ModHash hash = new ModHash(nodes);

        // Send request to gather hits stats
        sendRequests(hash);

        // delete nodes
        Random rand = new Random();
        for (int i = 0; i < NODES_COUNT_TO_REMOVE; i++) {
            hash.nodes.remove(rand.nextInt(nodes.size()));
        }

        // Send same requests again and calculate miss hits
        sendRequests(hash);

        // Print stats
        System.out.println("\n##################");
        System.out.println("Miss hits: [" + hash.getMissHits() + "] - " + hash.getMissHitsPercentage() + "%");
    }

    private static List<SimpleNode> buildNodes() {
        return IntStream.range(0, NODES_COUNT)
                .mapToObj(idx -> SimpleNode.of("key_" + idx + "_PoW_" + Math.pow(idx, 3)))
                .collect(Collectors.toList());
    }

    private static void sendRequests(ModHash hash) {
        for (int i = 0; i < REQUEST_COUNT; i++) {
            String key = "key" + i;
            hash.locate(key);
        }
    }

    private static class ModHash {
        private final List<SimpleNode> nodes;
        private final Map<String, SimpleNode> hits = new HashMap<>();
        private int missHits = 0;

        public ModHash(List<SimpleNode> nodes) {
            this.nodes = nodes;
        }

        public SimpleNode locate(String key) {
            int idx = Math.abs(key.hashCode() % nodes.size());

            SimpleNode node = nodes.get(idx);
            if (hits.containsKey(key) && !hits.get(key).equals(node)) {
                missHits++;
            }
            hits.put(key, node);
            return node;
        }

        public int getMissHits() {
            return missHits;
        }

        public float getMissHitsPercentage() {
            return Math.round((float) missHits / (float) REQUEST_COUNT * 100);
        }
    }
}
