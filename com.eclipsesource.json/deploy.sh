#!/bin/bash
# Script to deploy snapshots to maven central

NEXUS_URL=https://oss.sonatype.org/content/repositories/snapshots/
NEXUS_REPO=sonatype-nexus-snapshots

ID=minimal-json
USER_NAME=$(git config user.name)
VERSION=$(sed -e '/<version>/ { s/<\/\?version>//g; s/\s\+//g; q}; d' pom.xml)

changes=$(git status --porcelain) && [ -z "$changes" ] || {
  echo -e "there are changes:\n$changes"
  exit 1
}

echo "Build and deploy ${ID}"
echo "User name: $USER_NAME"
echo "Version: $VERSION"
echo "ok?"
read

mvn -Duser.name="${USER_NAME}" clean package || exit 1

echo "Ready, okay to deploy?"
read

cp pom.xml ${ID}-${VERSION}.pom

mvn gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}.jar || exit 1
mvn gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}-sources.jar -Dclassifier=sources || exit 1
mvn gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}-javadoc.jar -Dclassifier=javadoc || exit 1

rm ${ID}-${VERSION}.pom ${ID}-${VERSION}.pom.asc

echo "done"
echo "${NEXUS_URL%/}/com/eclipsesource/minimal-json/minimal-json/"
