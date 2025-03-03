FROM quay.io/jupyter/minimal-notebook:latest

RUN mkdir /home/jovyan/data

USER root
WORKDIR /home/jovyan

# Install dependencies: Java 21 and Maven
RUN apt-get update && apt-get install -y openjdk-21-jdk maven

# Copy the pre-created Maven project and jjava-glue project
COPY ./jupyter/java /home/jovyan/java
COPY ./jupyter/install.py /home/jovyan/install.py
COPY ./jjava-glue /home/jovyan/jjava-glue

# Build the jjava-glue project first and install it locally
WORKDIR /home/jovyan/jjava-glue
RUN mvn clean install -DskipTests

# Create a list of jjava-glue artifacts directly
RUN mvn -q dependency:build-classpath -Dmdep.outputFile=/home/jovyan/jjava-glue-classpath.txt

# Use Maven to download dependencies for JJava
WORKDIR /home/jovyan/java

# Download the JJava jar directly
RUN mvn dependency:get -Dartifact=org.dflib.jjava:jjava:1.0-M3 -Ddest=./ -Dtransitive=false
RUN mv jjava-1.0-M3.jar jjava.jar

# Download all dependencies
RUN mvn dependency:copy-dependencies -DoutputDirectory=./lib

# Create a list of dependencies for the classpath
RUN find ./lib -name "*.jar" | tr '\n' ':' > classpath.txt
# Add the jjava.jar to the classpath
RUN echo -n "/home/jovyan/java/jjava.jar:" >> classpath.txt
# Add the jjava-glue JARs to the classpath
RUN cat /home/jovyan/jjava-glue-classpath.txt >> classpath.txt
# Add the jjava-glue module JARs explicitly to ensure they're included
RUN echo -n ":/home/jovyan/jjava-glue/dflib-glue/target/dflib-glue-0.1.0-SNAPSHOT.jar:" >> classpath.txt

# Install the kernel with classpath configuration
WORKDIR /home/jovyan
RUN python install.py --prefix /opt/conda/ --classpath $(cat /home/jovyan/java/classpath.txt)

# Clean up Maven artifacts but keep the jjava.jar and lib directory
RUN rm -rf /home/jovyan/java/target /home/jovyan/java/.m2 /home/jovyan/java/pom.xml \
    /home/jovyan/java/classpath.txt /home/jovyan/jjava-glue-classpath.txt \
    && rm -f /home/jovyan/install.py

# Install conda packages from environment.yml
COPY ./jupyter/environment.yml /tmp/
RUN conda env update -f /tmp/environment.yml && \
    conda clean --all -f -y && \
    fix-permissions "${CONDA_DIR}" && \
    fix-permissions "/home/${NB_USER}"

# Install jupyter_darkmodern theme
RUN pip install jupyter_darkmodern && \
    fix-permissions "${CONDA_DIR}" && \
    fix-permissions "/home/${NB_USER}"

WORKDIR /home/jovyan
USER $NB_UID