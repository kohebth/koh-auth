#!/bin/bash

cp develop.env .env
./gradlew wrapper
./gradlew build
docker compose up --build
