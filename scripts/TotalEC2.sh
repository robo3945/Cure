#!/bin/bash

basedir=/home/ubuntu

# host list

ec2host[0]="ec2-54-228-0-152.eu-west-1.compute.amazonaws.com"
ec2host[1]="ec2-46-51-139-8.eu-west-1.compute.amazonaws.com"
ec2host[2]="ec2-54-228-59-234.eu-west-1.compute.amazonaws.com"
ec2host[3]="ec2-54-228-50-84.eu-west-1.compute.amazonaws.com"
ec2host[4]="ec2-54-228-55-25.eu-west-1.compute.amazonaws.com"
ec2host[5]="ec2-54-247-148-0.eu-west-1.compute.amazonaws.com"
ec2host[6]="ec2-54-228-48-26.eu-west-1.compute.amazonaws.com"

# ip list

ip[0]=10.227.166.59
ip[1]=10.227.113.104
ip[2]=10.227.115.172
ip[3]=10.48.211.15
ip[4]=10.226.117.136
ip[5]=10.226.195.131
ip[6]=10.48.237.19

# duration time

delay[0]=1800
delay[1]=3600
delay[2]=5400
delay[3]=7200
delay[4]=10800
delay[5]=14400
delay[6]=21600
delay[7]=28800


# Synchronize the local dir to the EC2 hosts and Remove the JOBS
for h in ${ec2host[@]}
do
   echo ===============================
echo ---- RemoveJobs
ssh -i ./key/T0.pem ubuntu@$h "for i in `atq | awk '{print $1}'`;do atrm $i;done"
   echo ---- Managing Tasks	
   ssh -i ./key/T0.pem ubuntu@$h "stop cron ; pkill java ; rm * -rf"
   echo ---- Synchronize $h
   rsync -auvz --delete -e "ssh -i ./key/T0.pem " ./* ubuntu@$h:$basedir
echo ---- RemoveJobs
ssh -i ./key/T0.pem ubuntu@$h "for i in `atq | awk '{print $1}'`;do atrm $i;done"
done


# Execute the AT script on the remote host

#delta after start
mins=10
numtest=1 # it is the test number in AT-total.sh
for d in ${delay[@]}
do
	echo "**************** TEST WITH ${d} SECONDS"
	echo Delay hours are: $hours
	
	index=0
	for h in ${ec2host[@]}
	do
		echo ---- AT Command on $h
		echo "=== SSH: $basedir/AT-total.sh ${ip[index]} ${d} ${mins}"
		ssh -i ./key/T0.pem ubuntu@$h "$basedir/AT-total.sh ${ip[index]} ${d} $mins $basedir"		
		index=$index+1
	done
	# calculate the delay time of a test set
	mins=`expr $mins + \( ${d} / 60 + ${d} / 60 / 4 + 10 \) \* $numtest`	
done