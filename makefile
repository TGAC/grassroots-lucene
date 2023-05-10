project=grassroots-search

current.version=0.1
classes.dir=classes
source.dir=src

core.jar = ${project}-core-${current.version}.jar
lucene.jar = ${project}-lucene-app-${current.version}.jar
solr.jar = ${project}-solr-app-${current.version}.jar

	
-include grassroots-lucene.properties

install.lib.dir = ${install.dir}/lib

# shared libs
shared.path = \
	lib/json-simple-1.1.1.jar:lib/slf4j-api-2.0.7.jar
	

# Add in the appropriate irods libs and dependencies
LUCENE_VERSION_MAJOR := $(shell echo $(lucene.version) | cut -f1 -d ".")
SOLR_VERSION_MAJOR := $(shell echo $(lucene.version) | cut -f1 -d ".")

LUCENE_GE_9 := $(shell [ $(LUCENE_VERSION_MAJOR) -gt 8 ] && echo true)
SOLR_GE_9 := $(shell [ $(SOLR_VERSION_MAJOR) -gt 8 ] && echo true)

ifeq ($(LUCENE_GE_9), true)
lucene.path += \
	${lucene.dir}/modules/lucene-analysis-common-${lucene.version}.jar:${lucene.dir}/modules/lucene-core-${lucene.version}.jar:${lucene.dir}/modules/lucene-queryparser-${lucene.version}.jar:${lucene.dir}/modules/lucene-facet-${lucene.version}.jar:${lucene.dir}/modules/lucene-highlighter-${lucene.version}.jar
else
lucene.path += \
	${lucene.dir}/analysis/common/lucene-analyzers-common-${lucene.version}.jar:${lucene.dir}/core/lucene-core-${lucene.version}.jar:${lucene.dir}/queryparser/lucene-queryparser-${lucene.version}.jar:${lucene.dir}/facet/lucene-facet-${lucene.version}.jar:${lucene.dir}/highlighter/lucene-highlighter-${lucene.version}.jar
endif		# ifeq ($(LUCENE_GE_9), true)



ifeq ($(SOLR_GE_9), true)
solr.path = \
	${solr.dir}/server/solr-webapp/webapp/WEB-INF/lib/solr-core-${solr.version}.jar:${solr.dir}/server/solr-webapp/webapp/WEB-INF/lib/solr-solrj-${solr.version}.jar
else
solr.path = \
	${solr.dir}/dist/solr-core-${solr.version}.jar:${solr.dir}/dist/solr-solrj-${solr.version}.jar
endif		# ifeq ($(LUCENE_GE_9), true)


source.files = $(shell find ${source.dir} -type f -name '*.java')


jars: core-jar lucene-app-jar solr-app-jar
	
clean:
	rm -fr ${classes.dir}/*
	
init:
	mkdir -p ${classes.dir}

compile: init
	javac -g -d ${classes.dir} --class-path ${shared.path}:${lucene.path}:${solr.path} ${source.files}

core-jar: compile
	jar --create --file ${core.jar} -C ${classes.dir} uk/ac/earlham/grassroots/document


lucene-app-jar: compile
	jar --create --file ${lucene.jar} -C ${classes.dir} uk/ac/earlham/grassroots/app/lucene

solr-app-jar: compile
	jar --create --file ${solr.jar} -C ${classes.dir} uk/ac/earlham/grassroots/app/solr
	
	

run-lucene-indexer: jars
	java -cp ${ant.project.name}-${current.version}.jar;$LUCENE_CLASS_PATH; $SOLR_CLASS_PATH:$shared.class.path uk.ac.earlham.grassroots.app.Indexer index ${index.dir} -tax ${tax.dir} -data ~/Desktop/lucene_test/docs

	

install: jars
	mkdir -p ${install.lib.dir}
	cp ${core.jar} ${install.lib.dir}/${core.jar}
	cp ${lucene.jar} ${install.lib.dir}/${lucene.jar}
	cp ${solr.jar} ${install.lib.dir}/${solr.jar}


echo-paths:
	@echo "core ${install.lib.dir}/${core.jar}"
	@echo "lucene ${install.lib.dir}/${lucene.jar}" 
	@echo "solr ${install.lib.dir}/${solr.jar}" 
	@echo "lucene.version ${lucene.version}" 
	@echo "lucene.path ${lucene.path}" 
	@echo "solr.path ${solr.path}" 
	@echo "shared.path ${shared.path}" 
	@echo "source.files ${source.files}"


