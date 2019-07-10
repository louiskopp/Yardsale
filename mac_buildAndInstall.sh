#!/bin/bash

# 2018-11-14
# Written by Matt Blessed to debug apps more quickly...

# builds the app -> installs the app -> starts the app
gradle assembleDebug && gradle installDebug && gradle appStart

