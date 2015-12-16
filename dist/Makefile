# explicitly define shell to execute targes for resolving ambiguity of /bin/sh on different machines
#SHELL=/bin/sh
BASE_DIR=..
DATA_DIR=${BASE_DIR}/data
JAR=${BASE_DIR}/target/WalkAuth-1.0-SNAPSHOT-jar-with-dependencies.jar

.PHONY: all clean copydata compile pack readme

all: clean copydata compile readme pack
	
copydata:
	cp -r ${DATA_DIR} .

compile:
	cd ${BASE_DIR} && \
		mvn clean install
	cp ${JAR} WalkAuth.jar

readme:
	echo "Pro spuštění programu je potřeba mít nainstalovanou Javu verze 1.8. Poté jde program spustit následujícím příkazem.\n\n\
$$ java -jar WalkAuth.jar\n\n\
Pro zobrazení nápovědy k používání programu stačí spistit s paramertem -h.\n\n\
$$ java -jar WalkAuth.jar -h" \
		>> README

pack:
	zip walkauth.zip README data WalkAuth.jar

clean:
	@echo "Cleanig up..."
	-rm -r data
	-rm WalkAuth.jar
	-rm README
