#!/bin/bash

echo "getting Gradle7            **************************"
wget -O  gradle7.zip https://services.gradle.org/distributions/gradle-7.3.3-bin.zip
mkdir gradle7
unzip -qd gradle7 gradle7.zip
ls gradle7/gradle-7.3.3
echo ""

echo "building Squid          **************************"
gradle7/gradle-7.3.3/bin/gradle clean build