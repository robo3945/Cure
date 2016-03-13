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


# Synchronize the local dir to the EC2 hosts and Remove the JOBS
for h in ${ec2host[@]}
do
   echo ===============================
   echo ---- Halting Tasks	
   ssh -i ./key/T0.pem ubuntu@$h "reboot"
done