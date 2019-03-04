#!/bin/bash

DIR=$2

set -o xtrace

echo "Running Dummy Deploy.sh"
pwd
ls -al

echo inputs: $*

ls -al $4

cat $4/infrastructure.properties

echo END.
