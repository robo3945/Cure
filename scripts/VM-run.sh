echo ">>>>>>>>>>> Now" >> $3/log/stat.txt
date  >> $3/log/stat.txt
echo ">>>>>>>>>>> Kill java processes" >> $3/log/stat.txt
pkill java >> $3/log/stat.txt
echo ">>>>>>>>>>> Java processes" >> $3/log/stat.txt
ps aux>> $3/log/stat.txt
echo ">>>>>>>>>>> Start --> CloRoFor.jar" >> $3/log/stat.txt
vmstat >> $3/log/stat.txt
java -XX:ErrorFile=/root/java_error%p.log -Xmx256M -jar $3/CloRoFor.jar -kp "$3/clorofor.keystore" -tf $3/topology.prop.xml -tIp  $1 -d $2 -td $3/traces/ -cfg "$3/config.prop.xml"
vmstat >> $3/log/stat.txt
echo ">>>>>>>>>>> End 	--> CloRoFor.jar" >> "$3/log/stat.txt"
echo ">>>>>>>>>>> Netstat" >> $3/log/stat.txt
netstat -u -s >> $3/log/stat.txt
echo ">>>>>>>>>>> /proc/net/snmp" >> $3/log/stat.txt
cat /proc/net/snmp >> $3/log/stat.txt
echo ">>>>>>>>>>> Calculate traces" >> $3/log/stat.txt
./runParser.sh ./traces >> $3/log/stat.txt