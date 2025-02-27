FROM quay.io/jupyter/minimal-notebook:latest

RUN mkdir /home/jovyan/data

USER root
WORKDIR /home/jovyan
RUN apt-get update && apt-get install -y openjdk-21-jdk
COPY ./java java
COPY ./install.py install.py
RUN python install.py --prefix /opt/conda/
RUN rm -rf java install.py

# Install conda pacakages from environment.yml
COPY environment.yml /tmp/
RUN conda env update -f /tmp/environment.yml && \
    conda clean --all -f -y && \
    fix-permissions "${CONDA_DIR}" && \
    fix-permissions "/home/${NB_USER}"

USER $NB_UID