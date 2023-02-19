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
    clojure -X:antq

# Checks (or formats) the source code
@format action="check" files="":
    clojure -M:{{action}} {{files}}

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

# Run the Docker services, e.g., PostgreSQL, Redis...
@up:
    docker compose -f scripts/docker/docker-compose-services.yml up

# Stop running the Docker services
@down:
    docker compose -f scripts/docker/docker-compose-services.yml down

# Install pre-commit (https://pre-commit.com/)
@pre-commit-install:
    pre-commit install

# Run pre-commit hooks (to verify at any point, not just on commit)
@pre-commit-run hook-id="":
    pre-commit run --all-files {{hook-id}}

# Drops and recreates the startrek database
@recreate-startrek:
    psql -h localhost -d postgres -U postgres -f scripts/sql/200-drop-create-startrek-db.sql

# Run the UberJAR locally
@run-local: build
    bin/run-local

# Run the Docker container locally
@run-docker: imagify
    bin/run-docker

# Build, imagify and publish the container
@all: build imagify publish

# vim: expandtab:ts=4:sw=4:ft=just
