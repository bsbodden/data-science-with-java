FROM quay.io/jupyter/minimal-notebook:latest

RUN mkdir /home/jovyan/data

USER root
WORKDIR /home/jovyan

# Install dependencies: Java 21 and Maven
RUN apt-get update && apt-get install -y openjdk-21-jdk maven

# Copy the pre-created Maven project
COPY ./java /home/jovyan/java
COPY ./install.py /home/jovyan/install.py

# Use Maven to download dependencies
WORKDIR /home/jovyan/java

# Download the JJava jar directly
RUN mvn dependency:get -Dartifact=org.dflib.jjava:jjava:1.0-M3 -Ddest=./ -Dtransitive=false
RUN mv jjava-1.0-M3.jar jjava.jar

# Download all dependencies
RUN mvn dependency:copy-dependencies -DoutputDirectory=./lib

# Create a list of dependencies for the classpath
RUN find ./lib -name "*.jar" | tr '\n' ':' > classpath.txt

# Install the kernel with classpath configuration
WORKDIR /home/jovyan
RUN python install.py --prefix /opt/conda/ --classpath $(cat /home/jovyan/java/classpath.txt)

# Clean up Maven artifacts but keep the jjava.jar and lib directory
RUN rm -rf /home/jovyan/java/target /home/jovyan/java/.m2 /home/jovyan/java/pom.xml \
    /home/jovyan/java/classpath.txt \
    && rm -f /home/jovyan/install.py

# Install conda packages from environment.yml
COPY environment.yml /tmp/
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