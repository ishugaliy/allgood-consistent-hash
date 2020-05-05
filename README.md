![Logo](logo.png)

# AllGood Consistent-Hash

![build](https://github.com/ishugaliy/consistent-hash/workflows/build/badge.svg?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/44b0ef5de107b4e0cf7b/maintainability)](https://codeclimate.com/github/ishugaliy/consistent-hash/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/44b0ef5de107b4e0cf7b/test_coverage)](https://codeclimate.com/github/ishugaliy/consistent-hash/test_coverage)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

AllGood Consistent Hash is a Java implementation of [Consistent Hash Ring with Virtual Nodes](http://theory.stanford.edu/~tim/s17/l/l1.pdf) that supports customization of hashing and partition rate.
<br>AllGood Consistent Hash is very user-friendly, which provides several examples, making it easy to understand and use.

## Download

<i>Maven</i>

    <dependency>
        <groupId>org.ishugaliy</groupId>
        <artifactId>allgood-consistent-hash</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

<i>Gradle</i>

    dependencies {
        implementation 'org.ishugaliy:allgood-consistent-hash:1.0.0'
    }

## Usage
### Structure

- [ConsistentHash](src/main/java/org/ishugaliy/allgood/consistent/hash/ConsistentHash.java) - consistent-hash implementation abstraction.
    - [HashRing](src/main/java/org/ishugaliy/allgood/consistent/hash/HashRing.java) - consistent-hash ring with virtual nodes implementation.
- [Node](src/main/java/org/ishugaliy/allgood/consistent/hash/node/Node.java) - consistent-hash nodes abstraction.
    - [SimpleNode](src/main/java/org/ishugaliy/allgood/consistent/hash/node/SimpleNode.java) - a simple node implementation, represents a single value. 
    - [ServerNode](src/main/java/org/ishugaliy/allgood/consistent/hash/node/ServerNode.java) - represents server or host. 
- [Hasher](src/main/java/org/ishugaliy/allgood/consistent/hash/hasher/Hasher.java) - hash function abstraction.
    - [DefaultHasher](src/main/java/org/ishugaliy/allgood/consistent/hash/hasher/DefaultHasher.java) - list of build-in hash functions.

### Basic 

    // Build hash ring
    ConsistentHash<SimpleNode> ring = HashRing.<SimpleNode>newBuilder().build();

    // Add nodes
    ring.add(SimpleNode.of("dc1.node.1"));
    ring.add(SimpleNode.of("dc2.node.2"));
    ring.add(SimpleNode.of("dc3.node.3"));

    // Locate node
    Optional<SimpleNode> node = ring.locate("your_key");

### Advanced

    // Create nodes
    ServerNode n1 = new ServerNode("192.168.1.1", 80);
    ServerNode n2 = new ServerNode("192.168.1.132", 80);
    ServerNode n3 = new ServerNode("aws", "11.32.98.1", 9231);
    ServerNode n4 = new ServerNode("aws", "11.32.328.1", 9231);

    // Build hash ring
    ConsistentHash<ServerNode> ring = HashRing.<ServerNode>newBuilder()
            .name("file_cache_hash_ring")       // set hash ring name
            .hasher(DefaultHasher.METRO_HASH)   // hash function to distribute partitions
            .partitionRate(10)                  // number of partitions per node
            .nodes(Arrays.asList(n1, n2))       // initial nodes set
            .build();

    // add nodes
    ring.addAll(Arrays.asList(n3, n4));        

    // Locate 2 nodes
    Set<ServerNode> nodes = ring.locate("your_key", 2);

<i><b>More samples</b> can be found [here](samples/src/main/java/org/ishugaliy/allgood/consistent/hash/samples/)</i>

### Sandboxes
The sources contains <b>sandboxes</b> to analyze different load metrics (<i>standard deviation, nodes miss-hits, etc</i>):
- [HasherLoadDistributionSandbox](samples/src/main/java/org/ishugaliy/allgood/consistent/hash/samples/HasherLoadDistributionSandbox.java) </br>
The sandbox allows checking consistent hash load distribution between nodes with different hash functions.
Showing how the distribution rate depends on the hash function and partition rate.
- [NodesMissHitsSandbox](samples/src/main/java/org/ishugaliy/allgood/consistent/hash/samples/NodesMissHitsSandbox.java) </br>
The sandbox allows checking consistent hash nodes miss-hits.
Showing dependency between miss-hits and partition rate.

<i><b>More sandboxes</b> can be found [here](samples/src/main/java/org/ishugaliy/allgood/consistent/hash/samples/)</i>

## Logging 
AllGood Consistent Hash library uses a [slf4](http://www.slf4j.org/) logging facade that allows plug it in the desired logging framework at deployment time.
</br></br>
Namespace: `org.ishugaliy.allgood.consistent.hash`

<i>Logback example:</i>

    <configuration>
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <logger name="org.ishugaliy.allgood.consistent.hash" level="error"/>
        <root level="debug">
            <appender-ref ref="STDOUT" />
        </root>
    </configuration>

## Requirements
Compile requirements: JDK 8+ and Maven 3.2.5+
  
## References
- [Consistent Hashing: Algorithmic Tradeoffs](https://medium.com/@dgryski/consistent-hashing-algorithmic-tradeoffs-ef6b8e2fcae8)
- [Stanfort: Consistent Hashing](http://theory.stanford.edu/~tim/s17/l/l1.pdf)
- [DataStax: Consistent Hash](https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/architecture/archDataDistributeHashing.html)
- [Hash Function Comparison](https://www.strchr.com/hash_functions)
- [StackOverflow: Hashing Algorithms Comparison](https://softwareengineering.stackexchange.com/questions/49550/which-hashing-algorithm-is-best-for-uniqueness-and-speed?newreg=43801d5b7b124771bac93907a47783a1)
- [Google: Consistent Hashing with Bounded Loads](https://ai.googleblog.com/2017/04/consistent-hashing-with-bounded-loads.html)


## License
    The MIT License

    Copyright (c) 2020 Iurii Shugalii

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.