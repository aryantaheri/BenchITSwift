#!/bin/sh
#####################################################################
# BenchIT - Performance Measurement for Scientific Applications
# Contact: developer@benchit.org
#
# $Id: COMPILE.SH 1 2009-09-11 12:26:19Z william $
# $URL: svn+ssh://william@rupert.zih.tu-dresden.de/svn-base/benchit-root/BenchITv6/kernel/utilities/skeleton/Java/0/0/0/COMPILE.SH $
# For license details see COPYING in the package base directory
#####################################################################
# Kernel: swift java kernel 
#####################################################################

# If kernel name string is set, this COMPILE.SH is sourced
# from the global COMPILE.SH. If it's not set, the user
# executed this script directly so we need to call configure.
if [ -z "${BENCHIT_KERNELNAME}" ]; then
	# Go to COMPILE.SH directory
	cd `dirname ${0}` || exit 1

	# Set configure mode
	CONFIGURE_MODE="COMPILE"

	# Load the definitions
	. ../../../../../../../tools/configure
fi

# check wether compilers and libraries are available
if [ -z "${BENCHIT_JAVAC}" ]; then
	echo "No Java compiler specified - check \$BENCHIT_JAVAC"
	exit 1
fi

BENCHIT_COMPILER="${BENCHIT_JAVAC}"
BENCHIT_COMPILERFLAGS="${BENCHIT_JAVAC_FLAGS_HIGH}"
BENCHIT_JAVAKERNELCLASS="SwiftGetKernelMain"

BENCHIT_COMMENT="Swift Java kernels"
export BENCHIT_COMMENT

export BENCHIT_COMPILER BENCHIT_COMPILERFLAGS BENCHIT_JAVAKERNELCLASS

#
# ENVIRONMENT HASHING - creates jbi/BIEnvHash.java
#
cd ${BENCHITROOT}/tools/
set > tmp.env
./fileversion >> tmp.env
./envhashbuilder
cd ${KERNELDIR}

DEST="${BENCHIT_KERNELBINARY}"
if [ ! -d ${DEST} ]; then
	mkdir ${DEST}
fi

rm -f ${DEST}/*.class
rm -f ${DEST}/*.java

cp -f SwiftDataObject.java ${DEST}
cp -f SwiftWork.java ${DEST}
cp -f conf/*.properties ${DEST}
cp -f ${BENCHIT_JAVAKERNELCLASS}.java ${DEST}
cp -f ${BENCHITROOT}/jbi/*.java ${DEST}
cp -f lib/*.jar ${DEST}
cp -f run.sh  ${DEST}

cd ${DEST}

export CLASSPATH="${CLASSPATH}:${DEST}/commons-cli-1.1.jar:${DEST}/commons-codec-1.3.jar:${DEST}/commons-io-1.4.jar:${DEST}/commons-lang-2.4.jar:${DEST}/commons-logging-1.1.1.jar:${DEST}/httpclient-4.1.3.jar:${DEST}/httpcore-4.1.4.jar:${DEST}/java-cloudfiles.jar:${DEST}/junit.jar:${DEST}/xercesImpl-2.8.1.jar:${DEST}/log4j-1.2.15.jar"

# compile
printf "${BENCHIT_COMPILER} ${BENCHIT_COMPILERFLAGS} *.java\n"
${BENCHIT_COMPILER} ${BENCHIT_COMPILERFLAGS} *.java
rm -f *.java

