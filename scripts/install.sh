#!/bin/sh
cd ..
cp scripts/git-hooks/pre-commit.sh .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
