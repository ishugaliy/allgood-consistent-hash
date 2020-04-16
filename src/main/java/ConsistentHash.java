import node.Node;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ConsistentHash<T extends Node> {

    String geName();

    boolean add(T node);

    boolean addAll(Collection<T> nodes);

    boolean contains(T node);

    boolean remove(T node);

    Set<T> getNodes();

    Optional<T> locate(String key);

    Set<T> locateN(String key, int count);

    int size();
}
