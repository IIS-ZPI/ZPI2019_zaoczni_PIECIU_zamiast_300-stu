#!/bin/bash

increment_version() {
 local v=$1
 if [ -z $2 ]; then 
    local rgx='^((?:[0-9]+\.)*)([0-9]+)($)'
 else 
    local rgx='^((?:[0-9]+\.){'$(($2-1))'})([0-9]+)(\.|$)'
    for (( p=`grep -o "\."<<<".$v"|wc -l`; p<$2; p++)); do 
       v+=.0; done; fi
 val=`echo -e "$v" | perl -pe 's/^.*'$rgx'.*$/$2/'`
 echo "$v" | perl -pe s/$rgx.*$'/${1}'`printf %0${#val}s $(($val+1))`/
}

PATH_TO_APK=$(find -type f -name "*.apk")
APK_NAME=$(basename ${PATH_TO_APK})
APK_EXT="${APK_NAME##*.}"
APK_FILENAME="${APK_NAME%.*}"

GIT_LATEST_TAG=$(git describe --abbrev=0 --tags)
GIT_NEWEST_TAG=$(echo ${GIT_LATEST_TAG} | awk -F. -v OFS=. 'NF==1{print ++$NF}; NF>1{if(length($NF+1)>length($NF))$(NF-1)++; $NF=sprintf("%0*d", length($NF), ($NF+1)%(10^length($NF))); print}')

cp ${PATH_TO_APK} ./"${APK_FILENAME}-1.4.${TRAVIS_BUILD_NUMBER}.${APK_EXT}"