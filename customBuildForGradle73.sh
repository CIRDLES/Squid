#!/bin/bash

echo "Hello Travis ****************************************"
echo ""
echo "getting Liberica 8322 full   **************************"
wget -O  libjava8322.tar.gz https://download.bell-sw.com/java/8u322+6/bellsoft-jdk8u322+6-linux-amd64-full.tar.gz
tar -xf libjava8322.tar.gz
ls jdk8u322-full
echo ""

echo "getting Gradle7            **************************"
wget -O  gradle7.zip https://services.gradle.org/distributions/gradle-7.3.3-bin.zip
mkdir gradle7
unzip -qd gradle7 gradle7.zip
ls gradle7/gradle-7.3.3
echo ""

echo "building Squid          **************************"
gradle7/gradle-7.3.3/bin/gradle clean build --refresh-dependencies -Dorg.gradle.java.home=./jdk8u322-full/