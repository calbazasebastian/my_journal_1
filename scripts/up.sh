#/bin/bash
SCRIPTPATH=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd "$SCRIPTPATH"
export TAG=$(git rev-parse --short HEAD)

export COMPOSE_HTTP_TIMEOUT=12000

export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1
export BUILDKIT_PROGRESS=plain
export PROJECT="omni"
docker-compose -p $PROJECT -f compose/app.yml up --build "$@"