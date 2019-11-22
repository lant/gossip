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

This will start the instances (10), and after 5 seconds it will start another one that will
propagate a new value to the rest of the system. 

