FROM gitpod/workspace-full

USER gitpod

# Install OpenJDK 11
RUN  bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 11.0.2-open"
