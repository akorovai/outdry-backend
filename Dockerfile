FROM ubuntu:latest
LABEL authors="aktey"

ENTRYPOINT ["top", "-b"]