echo -n -e "\033]0;$HACKYSTAT_VERSION DPD\007"; cd $HACKYSTAT_SERVICE_DIST/hackystat-analysis-dailyprojectdata; java -Xmx512M -jar dailyprojectdata.jar
