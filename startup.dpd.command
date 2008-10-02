echo -n -e "\033]0;DailyProjectData $HACKYSTAT_VERSION\007"
java -Xmx512M -jar $HACKYSTAT_SERVICE_DIST/hackystat-analysis-dailyprojectdata/dailyprojectdata.jar
