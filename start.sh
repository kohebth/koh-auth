#!/bin/bash

cp develop.env .env
./gradlew build
docker compose up --build
