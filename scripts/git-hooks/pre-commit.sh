#!/bin/sh

echo "🔍 Running Gitleaks scan..."

gitleaks detect --source . --log-opts "--all" --verbose

if [ $? -ne 0 ]; then
    echo "🚨 Gitleaks detected potential secrets in staged changes. Commit aborted."
    exit 1
fi

echo "✅ Gitleaks passed. Proceeding with commit."
exit 0