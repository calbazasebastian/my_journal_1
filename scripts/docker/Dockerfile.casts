# syntax=docker/dockerfile:experimental
FROM debian:buster as base
RUN apt-get update && apt-get install -y curl unzip zip locales \
    && rm -rf /var/lib/apt/lists/* \
    && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
    && groupadd -g 1000 dev;  useradd dev -u 1000 -m -g dev; echo "dev:dev" | chpasswd \
   echo "dev ALL=(ALL) NOPASSWD:ALL" | tee -a /etc/sudoers
ENV LANG en_US.utf8
USER dev

RUN curl -s "https://get.sdkman.io" | bash
WORKDIR /home/dev

RUN  bash -c  "source /home/dev/.sdkman/bin/sdkman-init.sh && sdk i java 11.0.9-zulu && sdk i scala 2.13.3 && sdk i sbt 1.4.4 "

ADD --chown=dev:dev api/project api/project
RUN --mount=type=cache,id=deps,target=/home/dev/.ivy2,uid=1000,gid=1000 \
  bash -c  "source /home/dev/.sdkman/bin/sdkman-init.sh  && sdk current  &&  cd api && sbt version"

FROM base as artifacts
ADD --chown=dev:dev api api
WORKDIR /home/dev/api
RUN --mount=type=cache,id=deps,target=/home/dev/.ivy2,uid=1000,gid=1000  \
  bash -c  "source /home/dev/.sdkman/bin/sdkman-init.sh  && sdk current && sbt 'set test in assembly := {}' assembly"

FROM base
ENV DB_PASSWORD=123456
ENV DB_USER=admin
ENV DB_URL="http://couchdb:5984"
COPY --from=artifacts  /home/dev/api/target/scala-2.13/ominstack-assembly-0.0.1-SNAPSHOT.jar /ominstack-assembly-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["/home/dev/.sdkman/candidates/java/current/bin/java", "-jar",  "/ominstack-assembly-0.0.1-SNAPSHOT.jar"]
