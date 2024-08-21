#!/bin/bash

KEY_NAME=$1
openssl genrsa --out "$KEY_NAME.key"
openssl rsa -in "$KEY_NAME.key" -pubout -outform PEM -out "$KEY_NAME.key.pub"
