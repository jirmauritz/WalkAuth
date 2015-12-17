# explicitly define shell to execute targes for resolving ambiguity of /bin/sh on different machines
#SHELL=/bin/sh
BASE_DIR=..
DATA_DIR=data
SOURCE_DIR=src
DIST_DIR=dist
JAR=${BASE_DIR}/target/WalkAuth-1.0-SNAPSHOT-jar-with-dependencies.jar

.PHONY: all clean compile pack

all: clean compile pack
	
compile:
	cd ${BASE_DIR} && \
		mvn clean install
	cp ${JAR} WalkAuth.jar

pack:
	zip walkauth.zip README WalkAuth.jar
	cd ${BASE_DIR} && \
		zip -r ${DIST_DIR}/walkauth.zip ${DATA_DIR} ${SOURCE_DIR}

clean:
	@echo "Cleanig up..."
	-rm WalkAuth.jar
	-rm walkauth.zip
