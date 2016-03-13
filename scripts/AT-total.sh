#!/bin/bash

echo "-- Time update"
ntpdate europe.pool.ntp.org north-america.pool.ntp.org

# schedule the entire set

echo "--  Scheduling Jobs"
for i in {0..0}
do
	 delta=`expr \( ${2} / 60 + ${2} / 60 / 4 + 10 \)`
     echo "$4/VM-run.sh $1 $2 $4" | at now + `expr $3 + $delta \* $i` minutes
done