#!/bin/bash
export MOZILLA_FIVE_HOME=/usr/athena/lib/mozilla/
LD_LIBRARY_PATH=./needed:$LD_LIBRARY_PATH java -jar rssplane.jar