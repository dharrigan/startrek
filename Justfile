#
# And awayyyy we go!
#

set dotenv-load := true
set positional-arguments := true

# List all recipes (_ == hidden recipe)
_default:
    @just --list

# Upgrade dependencies
@deps:
    clj -X:antq

# Checks (or formats) the source code
@format action="check" files="":
    clj -M:{{action}} {{files}}

# Test the application
@test:
    bin/test

# Build the application
@build:
    bin/build

# Create the Docker container
@imagify: build
    bin/imagify

# Publish the Docker container
@publish: build imagify
    bin/publish

# Run Docker services, e.g., PostgreSQL...
@up:
    docker compose -f scripts/docker/docker-compose-services.yml up

# Stop running Docker services
@down:
    docker compose -f scripts/docker/docker-compose-services.yml down

# Run the UberJAR locally
@run-local: build
    bin/run-local

# Run the Docker container locally
@run-docker: imagify
    bin/run-docker

# Build, imagify and publish the container
@all: build imagify publish

# vim: expandtab:ts=4:sw=4:ft=just
