#!/bin/bash

while :
  do
    clear
    echo "CHAT-SVR-01 NETSTAT -naopt | grep 20111"
    timestamp=`date +%Y/%m/%d,%H:%M:%S -d +9hour`
    echo "$timestamp KST"

    netstat -naopt | grep 20111
    sleep 1
  done