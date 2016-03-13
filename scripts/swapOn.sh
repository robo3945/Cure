echo "swap off"
sudo swapoff -a

echo "restore the fstab"
sudo sh -c "sed -e '/^\/var\/swapfile.*$/,/^/d' /etc/fstab > /etc/fstab.new"
sudo cp /etc/fstab.new /etc/fstab

echo "set the new Swap"
sudo dd if=/dev/zero of=/var/swapfile bs=1M count=3072 &&
sudo chmod 600 /var/swapfile &&
sudo mkswap /var/swapfile &&
echo /var/swapfile none swap defaults 0 0 | sudo tee -a /etc/fstab &&
sudo swapon -a
sudo swapon -s 