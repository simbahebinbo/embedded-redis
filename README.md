embedded-redis
==============

Redis embedded server for Java integration testing

Fork Notes
==============
This repository is a fork of https://github.com/ozimov/embedded-redis, which is in turn a fork
of https://github.com/kstyrc/embedded-redis. We've updated the embedded Redis binaries to version 7.4.1 so we can write
tests that use recent Redis features without imposing dependencies that are not well-encapsulated by a single
Maven/Gradle build.

Maven dependency
==============

Maven Central:

```
<dependency>
    <groupId>com.github.lansheng228</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>7.4.1</version>
</dependency>
```

Usage
==============

Running RedisServer is as simple as:

```
RedisServer redisServer = RedisServer.builder().port(6379).build();
redisServer.start();
// do some work
redisServer.stop();
```

You can also provide RedisServer with your own executable:

```
// 1) given explicit file (os-independence broken!)
RedisServer redisServer = new RedisServer("/path/to/your/redis", 6379);

// 2) given os-independent matrix
RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
  .override(OS.UNIX, "/path/to/unix/redis")
  .override(OS.MAC_OS_X, Architecture.x86, "/path/to/macosx/redis")
  .override(OS.MAC_OS_X, Architecture.x86_64, "/path/to/macosx/redis")
  
RedisServer redisServer = new RedisServer(customProvider, 6379);
```

You can also use fluent API to create RedisServer:

```
RedisServer redisServer = RedisServer.builder()
  .redisExecProvider(customRedisProvider)
  .port(6379)
  .replicaOf("locahost", 6378)
  .configFile("/path/to/your/redis.conf")
  .build();
```

Or even create simple redis.conf file from scratch:

```
RedisServer redisServer = RedisServer.builder()
  .redisExecProvider(customRedisProvider)
  .port(6379)
  .setting("bind 127.0.0.1")
  .replicaOf("locahost", 6378)
  .setting("daemonize no")
  .setting("appendonly no")
  .setting("maxmemory 128M")
  .build();
```

## Setting up a bunch

Our Embedded Redis has support for HA Redis bunchs with Sentinels and master-slave replication

#### Using ephemeral ports

A simple redis integration test with Redis bunch on ephemeral ports, with setup similar to that from production would
look like this:

```
public class SomeIntegrationTestThatRequiresRedis {
  private RedisBunch bunch;
  private Set<String> jedisSentinelHosts;

  @Before
  public void setup() throws Exception {
    //creates a bunch with 3 sentinels, quorum size of 2 and 3 replication groups, each with one master and one slave
    bunch = RedisBunch.builder().ephemeral().sentinelCount(3).quorumSize(2)
                    .replicationGroup("master1", 1)
                    .replicationGroup("master2", 1)
                    .replicationGroup("master3", 1)
                    .build();
    bunch.start();

    //retrieve ports on which sentinels have been started, using a simple Jedis utility class
    jedisBunchHosts = JedisUtil.bunchJedisHosts(bunch);
  }
  
  @Test
  public void test() throws Exception {
    // testing code that requires redis running
    JedisSentinelPool pool = new JedisSentinelPool("master1", jedisSentinelHosts);
  }
  
  @After
  public void tearDown() throws Exception {
    cluster.stop();
  }
}
```

#### Retrieving ports

The above example starts Redis bunch on ephemeral ports, which you can later get with ```bunch.ports()```,
which will return a list of all ports of the bunch. You can also get ports of sentinels
with ```bunch.sentinelPorts()```
or servers with ```bunch.serverPorts()```. ```JedisUtil``` class contains utility methods for use with Jedis client.

#### Using predefined ports

You can also start Redis bunch on predefined ports and even mix both approaches:

```
public class SomeIntegrationTestThatRequiresRedis {
    private RedisBunch bunch;

    @Before
    public void setup() throws Exception {
        final Set<Integer> sentinels = Set.of(26739, 26912);
        final Set<Integer> group1 = Set.of(6667, 6668);
        final Set<Integer> group2 = Set.of(6387, 6379);
        //creates a bunch with 3 sentinels, quorum size of 2 and 3 replication groups, each with one master and one slave
        bunch = RedisBunch.builder().sentinelPorts(sentinels).quorumSize(2)
                .serverPorts(group1).replicationGroup("master1", 1)
                .serverPorts(group2).replicationGroup("master2", 1)
                .ephemeralServers().replicationGroup("master3", 1)
                .build();
        bunch.start();
    }
    //(...)
}

```

The above will create and start a bunch with sentinels on ports ```26739, 26912```, first replication group
on ```6667, 6668```,
second replication group on ```6387, 6379``` and third replication group on ephemeral ports.

## Setting up a cluster

Our Embedded Redis has support for HA Redis clusters

#### Using ephemeral ports

A simple redis integration test with Redis cluster on ephemeral ports, with setup similar to that from production would
look like this:

```
public class SomeIntegrationTestThatRequiresRedis {
  private RedisCluster cluster;
  private Set<String> jedisClusterHosts;

  @Before
  public void setup() throws Exception {
    //creates a cluster with 3 nodes, each with one master and one slave
    Set<Integer> nodePorts = Set.of(16379, 16380, 16381, 16382, 16383, 16384);

    cluster =
                RedisCluster.builder()
                        .nodePorts(nodePorts)
                        .clusterReplicas(1)
                        .build();
    cluster.start();
        
    jedisClusterHosts = JedisUtil.clusterJedisHosts(cluster);
  }
  
  @Test
  public void test() throws Exception {
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        nodes.add(new HostAndPort("127.0.0.1", 16379));
        nodes.add(new HostAndPort("127.0.0.1", 16380));
        nodes.add(new HostAndPort("127.0.0.1", 16381));
        nodes.add(new HostAndPort("127.0.0.1", 16382));
        nodes.add(new HostAndPort("127.0.0.1", 16383));
        nodes.add(new HostAndPort("127.0.0.1", 16384));

        JedisCluster jedisCluster = new JedisCluster(nodes);
  }
  
  @After
  public void tearDown() throws Exception {
    cluster.stop();
  }
}
```

## Setting up a multiple

Our Embedded Redis has support for HA Redis multiples

#### Using ephemeral ports

A simple redis integration test with Redis cluster on ephemeral ports, with setup similar to that from production would
look like this:

```
public class SomeIntegrationTestThatRequiresRedis {
  private RedisMultiple multiple;
  private Set<String> jedisMultipleHosts;

  @Before
  public void setup() throws Exception {
    //creates a multiple with 3 masters
    Set<Integer> masterPorts = Set.of(masterPort1, masterPort2, masterPort3);

    multiple = RedisMultiple.builder()
                .masterPorts(masterPorts)
                .build();
    multiple.start();
        
    jedisMultipleHosts = JedisUtil.multipleJedisHosts(multiple);
  }
  
  @Test
  public void test() throws Exception {
       JedisPool masterPool1 = new JedisPool(masterHost1, masterPort1);
        JedisPool masterPool2 = new JedisPool(masterHost2, masterPort2);
        JedisPool masterPool3 = new JedisPool(masterHost3, masterPort3);

        Jedis masterJedis1 = masterPool1.getResource();
        Jedis masterJedis2 = masterPool2.getResource();
        Jedis masterJedis3 = masterPool3.getResource();

  }
  
  @After
  public void tearDown() throws Exception {
    multiple.stop();
  }
}
```

## Setting up a gather

Our Embedded Redis has support for HA Redis gathers

#### Using ephemeral ports

A simple redis integration test with Redis gather on ephemeral ports, with setup similar to that from production would
look like this:

```
public class SomeIntegrationTestThatRequiresRedis {
  private RedisGather gather;
  private Set<String> jedisGatherHosts;

  @Before
  public void setup() throws Exception {
    //creates a gather with 1 gather, each with one master and two slave
   Set<Integer> slavePorts = Set.of(slavePort1, slavePort2);

   gather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(2)
                .build();
    gather.start();
        
    jedisGatherHosts = JedisUtil.gatherJedisHosts(gather);
  }
  
  @Test
  public void test() throws Exception {
    JedisPool masterPool = new JedisPool(masterHost, masterPort);
        JedisPool slavePool1 = new JedisPool(slaveHost1, slavePort1);
        JedisPool slavePool2 = new JedisPool(slaveHost2, slavePort2);

        Jedis masterJedis = masterPool.getResource();
        Jedis slaveJedis1 = slavePool1.getResource();
        Jedis slaveJedis2 = slavePool2.getResource();
  }
  
  @After
  public void tearDown() throws Exception {
    gather.stop();
  }
}
```

Redis version
==============

By default, RedisServer runs an OS-specific executable enclosed in the `embedded-redis` jar. The jar includes:

- Redis 7.4.1 for Linux/Unix (amd64 and arm64)
- Redis 7.4.1 for macOS (amd64 and arm64)

The enclosed binaries are built from source from the [`7.4.1` tag](https://github.com/antirez/redis/releases/tag/7.4.1)
in the official Redis repository. The Linux binaries are statically-linked amd64 and x86 executables built using
the `build-server-binaries.sh` script included in this repository at `/src/main/docker`. The macOS binaries are built
according to
the [instructions in the README](https://github.com/antirez/redis/blob/51efb7fe25753867d39aa88a521f7c275fd8cddb/README.md#building-redis).
Windows binaries are not included because Windows is not officially supported by Redis.

Callers may provide a path to a specific `redis-server` executable if needed.

Note:
Running the `build-server-binaries.sh` script, compile Linux and macOS binary files from the source code,

License
==============
Licensed under the Apache License, Version 2.0


