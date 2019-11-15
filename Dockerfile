FROM openjdk:12
COPY build/install /gossip
CMD /gossip/Gossip/bin/Gossip
