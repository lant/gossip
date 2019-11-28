# Gossip protocol experiment    

This repo holds a toy gossip protocol implementation, basically used for didactic reasons. It can 
be used as a base to build and test gossip protocols.

The system contains a java server that listens to incoming calls, everything wrapped in a Docker container. 
The system also has a `docker-compose` file to simulate the distributed system. 

The gossip protocol is basically a propagation using random IP's from the system range, so there is no 
assurance that it will work fine in all the cases. The file that contains this decision is `com.github.lant.gossip.GossipStrategy` . 
Modify it to get different results. 

## Execute
To run the system you need to compile the code: 
```bash
./gradlew installDist
```

and after that you can execute the docker: 
```bash
docker-compose build
docker-compose up --scale node=10
```

This will start the 10 instances that will listen for new propagation values. 

There is a script called `propagate` that's using the class: `com.github.lant.gossip.Propagate` in order to 
propagate values. It takes two parameters: `port` and `value`. 

It will propagate the `value` to the node listening to that port. In order to execute it run `docker ps` to get
the port mappings: 

```bash
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
b636473bfd3e        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 15 seconds       0.0.0.0:7012->7000/tcp   gossip_node_9
9922040ab0b9        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 14 seconds       0.0.0.0:7016->7000/tcp   gossip_node_6
4520094644e3        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 14 seconds       0.0.0.0:7017->7000/tcp   gossip_node_10
e54d26064f8e        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 15 seconds       0.0.0.0:7013->7000/tcp   gossip_node_8
41179de9eec4        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 14 seconds       0.0.0.0:7014->7000/tcp   gossip_node_5
70b2b5cc6831        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 15 seconds       0.0.0.0:7018->7000/tcp   gossip_node_7
11de74af17f6        gossip_node         "/bin/sh -c /gossip/…"   17 seconds ago      Up 15 seconds       0.0.0.0:7015->7000/tcp   gossip_node_4
b857e04524eb        gossip_node         "/bin/sh -c /gossip/…"   8 minutes ago       Up 17 seconds       0.0.0.0:7011->7000/tcp   gossip_node_3
25d4bd3acfc0        gossip_node         "/bin/sh -c /gossip/…"   8 minutes ago       Up 17 seconds       0.0.0.0:7009->7000/tcp   gossip_node_1
bf28387cf26c        gossip_node         "/bin/sh -c /gossip/…"   8 minutes ago       Up 16 seconds       0.0.0.0:7010->7000/tcp   gossip_node_2
```

and then just use one of the mapped ones: 

`./propagate -p 7009 --value asdf`

This will propagate the value `asdf` into the nodes. It will use `node_1` as the initial propagator.
