FROM ibm-semeru-runtimes:open-jdk-25.0.3.0-jdk-noble AS base

RUN apt-get -y update && \
    apt-get install -y netcat-traditional && \
    apt-get -y autoremove && \
    apt-get clean && \
    mkdir /app && \
    chown -R ubuntu:ubuntu /app

WORKDIR /app

USER ubuntu:ubuntu


FROM base AS dev

CMD /bin/bash
