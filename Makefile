COMPOSE=docker-compose

.PHONY: all
all: build

.PHONY: run
run:
	$(COMPOSE) -f docker-compose.yml up -d

.PHONY: dev
dev: build
	$(COMPOSE) -f docker-compose.yml -f docker-compose.dev.yml up -d

.PHONY: build
build:
	./gradlew --no-daemon bootJar
