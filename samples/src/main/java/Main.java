import hash.DefaultHasher;
import node.Node;
import node.SimpleNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        ConsistentHash<SimpleNode> murmurRing = HashRing.<SimpleNode>newBuilder()
                .name("murmur_hash_ring")
                .hasher(DefaultHasher.MURMUR_3)
                .partitionRate(1000)
                .build();

        murmurRing.add(SimpleNode.of("dc1.projector-node.1"));
        murmurRing.add(SimpleNode.of("dc1.projector-node.2"));
        murmurRing.add(SimpleNode.of("dc1.projector-node.3"));

        Map<Node, Integer> stats = new HashMap<>();
        for (int i = 0; i < 1_000_000; i++) {
            Optional<SimpleNode> node = murmurRing.locate(UUID.randomUUID().toString());
            node.ifPresent(n -> {
                int cnt = stats.getOrDefault(n, 0) + 1;
                stats.put(n, cnt);
            });
        }

        for (Node node : stats.keySet()) {
            System.out.println("[" + node + "]: " + stats.get(node));
        }
    }
}
