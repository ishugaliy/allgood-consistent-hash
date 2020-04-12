import java.util.Collection;
import java.util.stream.Stream;

public interface ConsistentHash<T extends Node> {

    boolean addNode(T node);

    boolean addNodes(Collection<T> nodes);

    boolean containsNode(T node);

    boolean removeNode(T node);

    Stream<T> getNodes();

    T route(String key);
}
