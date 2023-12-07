#!/usr/bin/env bash

branches=("1.20.2")
#branches=("1.18.2" "1.19.2" "1.19.3" "1.19.4" "1.20.1" "1.20.2")
echo "Make sure there are no un-committed changes! Press Enter to continue..."
read
echo "Propagating commit updates..."

for branch in "${branches[@]}"; do
  git checkout $branch
  git cherry-pick $1
  git push
done