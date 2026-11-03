FROM ibm-semeru-runtimes:open-21.0.11.0-jdk-noble AS base

RUN apt-get -y update && \
    apt-get install -y netcat-traditional && \
    apt-get -y autoremove && \
    apt-get clean && \
    mkdir /app && \
    mkdir /home/ubuntu/.m2 && \
    chown -R ubuntu:ubuntu /home/ubuntu/.m2

WORKDIR /app

USER ubuntu:ubuntu


FROM base AS dev

CMD /bin/bash
