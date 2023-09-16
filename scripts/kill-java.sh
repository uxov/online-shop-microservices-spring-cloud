#!/bin/bash

jps | grep '\-0.0.1-SNAPSHOT.jar' | awk '{ print $1 }' | xargs -r kill
