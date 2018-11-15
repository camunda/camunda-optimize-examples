FROM docker.elastic.co/elasticsearch/elasticsearch-oss:6.0.0
ENV ES_JAVA_OPTS="-Xms512m -Xmx512m -Djava.io.tmpdir=/usr/share/elasticsearch/tmp"
ADD elasticsearch.yml /usr/share/elasticsearch/config/
USER root
RUN mkdir /usr/share/elasticsearch/tmp
RUN chown elasticsearch:elasticsearch /usr/share/elasticsearch/tmp
RUN mkdir /usr/share/elasticsearch/config/scripts
RUN chown elasticsearch:elasticsearch /usr/share/elasticsearch/config/scripts
RUN chown elasticsearch:elasticsearch /usr/share/elasticsearch/config/elasticsearch.yml
USER elasticsearch
EXPOSE 9200
EXPOSE 9300
