#!/usr/bin/env bash

branches=("1.18.2" "1.19.2" "1.19.3" "1.19.4" "1.20.1" "1.20.2")
echo -e "Make sure there are no un-committed changes and that the commit is common (not unique per branch)\nPress Enter to continue..."
read
echo "Propagating commit updates..."

for branch in "${branches[@]}"; do
  git checkout $branch
  git cherry-pick $1
#  ./gradlew build # ensure `java -version` is 17+
  git push
done

echo "Updates were committed!"