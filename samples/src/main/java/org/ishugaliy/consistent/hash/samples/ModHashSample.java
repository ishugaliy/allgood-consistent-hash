package org.ishugaliy.consistent.hash.samples;

import org.ishugaliy.consistent.hash.node.SimpleNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModHashSample {

    private static final int NODES_COUNT = 100;
    private static final int NODES_TO_REMOVE = 1;
    private static final int REQUEST_COUNT = 100_000;

    public static void main(String[] args) {
        List<SimpleNode> nodes = buildNodes();
        ModHash hash = new ModHash(nodes);

        // Send request to gather hits stats
        sendRequests(hash);

        // delete nodes
        Random rand = new Random();
        for (int i = 0; i < NODES_TO_REMOVE; i++) {
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

        public List<SimpleNode> getNodes() {
            return nodes;
        }

        public float getMissHitsPercentage() {
            return Math.round((float) missHits / (float) REQUEST_COUNT * 100);
        }
    }
}
