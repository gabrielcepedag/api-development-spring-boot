#BY PROF. FREDDY PEÑA

.PHONY: default test help

default: help

PROJECT_VERSION := $(shell ./gradlew properties -q | grep "^version:" | awk '{print $$2}')
PROJECT_NAME := $(shell ./gradlew properties -q | grep "^name:" | awk '{print $$2}')
PROJECT_DOCKER_REPOSITORY=gcr.io/consummate-atom-423313-s2/${PROJECT_NAME}
LATEST=latest

COMMIT := $(shell git rev-parse --short HEAD)

# COLORS
GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
WHITE  := $(shell tput -Txterm sgr0)
RESET  := $(shell tput -Txterm sgr0)


TARGET_MAX_CHAR_NUM=20
# Show this help.
help:
	@echo ''
	@echo '       ${YELLOW}Project ${GREEN}${PROJECT_NAME}${RESET}'
	@echo ''
	@echo 'Usage:'
	@echo '  ${YELLOW}make${RESET} ${GREEN}<target>${RESET}'
	@echo ''
	@echo 'Targets:'
	@awk '/^[a-zA-Z\-\_0-9]+:/ { \
	helpMessage = match(lastLine, /^## (.*)/); \
	if (helpMessage) { \
		helpCommand = substr($$1, 0, index($$1, ":")-1); \
		helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
		printf "  ${YELLOW}%-$(TARGET_MAX_CHAR_NUM)s${RESET} ${GREEN}%s${RESET}\n", helpCommand, helpMessage; \
	} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)

## Clean and build
PROJECT_DEPENDENCY=dependencies --refresh-dependencies
PROJECT_CLEAN_PACKAGE=clean build -x test

build:
	@echo '$(GREEN)Building the project: $(RESET)$(PROJECT_NAME)'
	./gradlew $(PROJECT_DEPENDENCY)
	./gradlew $(PROJECT_CLEAN_PACKAGE)

ifeq ($(OS),Windows_NT)
	@echo '${YELLOW}OS:${RESET}'$(OS)
	@.\gradlew.bat ${PROJECT_DEPENDENCY}
	@.\gradlew.bat ${PROJECT_CLEAN_PACKAGE}
else
	@echo '${YELLOW}OS: ${RESET}'$(shell uname)
	@./gradlew ${PROJECT_DEPENDENCY}
	@./gradlew ${PROJECT_CLEAN_PACKAGE}
endif

## Create the docker image
image:
	@echo '${GREEN}Building Docker image for ${RESET}'$(PROJECT_NAME)
	@echo '${GREEN}PROJECT_DOCKER_REPOSITORY ${RESET}'$(PROJECT_DOCKER_REPOSITORY):$(PROJECT_VERSION)
	@docker build -t ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} .

image-amd64:
	@echo '${GREEN}Building Docker image for ${RESET}'$(PROJECT_NAME)
	@echo '${GREEN}PROJECT_DOCKER_REPOSITORY ${RESET}'$(PROJECT_DOCKER_REPOSITORY):$(PROJECT_VERSION)
ifeq ($(OS),Windows_NT)
	@docker build -t ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} .
else
	@docker buildx build --platform linux/amd64 -o type=docker -t ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} .
endif

## Clean docker image
clean:
	@echo '${GREEN}Clean docker image ${RESET}'$(PROJECT_NAME)
	docker-compose down -v
	docker-compose up -d

restart: clean
	@echo '${GREEN}Restart docker image ${RESET}'$(PROJECT_NAME)
ifeq ($(OS),Windows_NT)
	@.\gradlew.bat
else
	@./gradlew
endif

restart-with-backup: clean
	@echo '${GREEN}Restart docker image with backup ${RESET}'$(PROJECT_NAME)
	@echo '${GREEN}Waiting for docker ${RESET}'$(PROJECT_NAME)
	$(shell sleep 1)
	@docker-compose exec -T postgres psql -U postgres -d timesched < $(shell pwd)/backup/backup.sql

ifeq ($(OS),Windows_NT)
	@.\gradlew.bat
else
	@./gradlew
endif

## start full docker app (Using flyway)
start-app:
	@echo "Starting app (Using flyway)"
	@docker-compose up -d
	@docker-compose -f docker-compose-app.yml up -d

## Create the docker image [build, image]
build-image: build image

build-image-amd64: build image-amd64

## Push images to dockerhub
build-push-image: build-image
	@echo Pushing image to Docker Hub
	@echo '${GREEN}PROJECT_DOCKER_REPOSITORY ${RESET}'$(PROJECT_DOCKER_REPOSITORY):${PROJECT_VERSION}
	@docker push ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION}

## Push images to dockerhub
build-push-image-amd64: build-image-amd64
	@echo Pushing image to Docker Hub
	@echo '${GREEN}PROJECT_DOCKER_REPOSITORY ${RESET}'$(PROJECT_DOCKER_REPOSITORY):${PROJECT_VERSION}
	@docker push ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION}

## Create Release Tags version
create-release: build-image
	@echo Creating Release ${COMMIT} ${PROJECT_VERSION}
	@docker tag ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} ${PROJECT_DOCKER_REPOSITORY}:${COMMIT}
	@docker tag ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} ${PROJECT_DOCKER_REPOSITORY}:${LATEST}
	@docker push ${PROJECT_DOCKER_REPOSITORY}
	@docker push ${PROJECT_DOCKER_REPOSITORY}:${COMMIT}
	@docker push ${PROJECT_DOCKER_REPOSITORY}:${LATEST}

## Create Release Tags version
create-release-amd64: build-image-amd64
	@echo Creating Release ${COMMIT} ${PROJECT_VERSION}
	@docker push ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION}
	@gcloud run deploy barcamp-2024 --image ${PROJECT_DOCKER_REPOSITORY}:${PROJECT_VERSION} --allow-unauthenticated --max-instances=1 --min-instances=0 --platform managed --region=us-east1

