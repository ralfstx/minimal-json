#!/bin/bash
# Script to release to maven central

NEXUS_URL=https://oss.sonatype.org/service/local/staging/deploy/maven2/
NEXUS_REPO=sonatype-nexus-staging

ID=minimal-json
USER_NAME=$(git config user.name)
VERSION=$(sed -e '/<version>/ { s/<\/\?version>//g; s/-SNAPSHOT//; s/\s\+//g; q}; d' pom.xml)

changes=$(git status --porcelain) && [ -z "$changes" ] || {
  echo -e "there are changes:\n$changes"
  exit 1
}

echo "Build and release ${ID}"
echo "User name: $USER_NAME"
echo "Version: $VERSION"
echo "ok?"
read

sed -e "s/-SNAPSHOT//; s/HEAD/${VERSION}/" pom.xml > .pom.xml || exit 1
mv .pom.xml pom.xml || exit 1
git diff

echo "POM rewritten, okay?"
read

mvn -T 1C -Duser.name="${USER_NAME}" clean package || exit 1

echo "Ready, okay to tag and upload?"
read

git tag -a -s -m "${VERSION} release" ${VERSION} || exit 1

cp pom.xml ${ID}-${VERSION}.pom

mvn -T 1C gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}.jar || exit 1
mvn -T 1C gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}-sources.jar -Dclassifier=sources || exit 1
mvn -T 1C gpg:sign-and-deploy-file -Durl=${NEXUS_URL} -DrepositoryId=${NEXUS_REPO} -DpomFile=${ID}-${VERSION}.pom -Dfile=target/${ID}-${VERSION}-javadoc.jar -Dclassifier=javadoc || exit 1

rm ${ID}-${VERSION}.pom ${ID}-${VERSION}.pom.asc

echo "done"
echo "* login to https://oss.sonatype.org"
echo "* close staging repository"
echo "* test"
echo "* release staging repository"
echo "* increment patch version"
echo "* push tag and changes"
echo "* publish on github release page"
