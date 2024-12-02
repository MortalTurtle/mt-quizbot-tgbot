
.PHONY: docker-build
docker-build:
	docker compose build

.PHONY: docker-run
docker-run:
	docker compose build && docker compose up

.PHONY: docker-clean-data
docker-clean-data:
	docker compose down -v

.PHONY: docker-clean-db-data
docker-clean-db-data:
	rm -rf .pgdata && rm -rf .redisdata
