# Git Flow for Effective Office

This document describes the Git workflow used in the Effective Office project.

## Branching Strategy

We follow a modified Git Flow branching model:

### Main Branches
- **main**: Production-ready code. All releases are tagged from this branch.
- **develop**: Integration branch for features. This is where all feature branches are merged.

### Supporting Branches
- **feature/**: For developing new features (e.g., `feature/user-authentication`)
- **bugfix/**: For fixing bugs that are not critical (e.g., `bugfix/login-validation`)
- **hotfix/**: For critical fixes that need to be applied to production (e.g., `hotfix/security-vulnerability`)
- **release/**: For preparing releases (e.g., `release/1.0.0`)

## Workflow

### Feature Development
1. Create a feature branch from `develop`:
   ```
   git checkout develop
   git pull
   git checkout -b feature/your-feature-name
   ```

2. Work on your feature, committing changes regularly:
   ```
   git add .
   git commit -m "Descriptive message about your changes"
   ```

3. Push your branch to the remote repository:
   ```
   git push -u origin feature/your-feature-name
   ```

4. When the feature is complete, create a Pull Request to merge into `develop`.
5. After code review and approval, the feature branch is merged into `develop`.

### Bug Fixes
1. For non-critical bugs, create a bugfix branch from `develop`:
   ```
   git checkout develop
   git pull
   git checkout -b bugfix/bug-description
   ```

2. For critical bugs that need immediate fixes in production, create a hotfix branch from `main`:
   ```
   git checkout main
   git pull
   git checkout -b hotfix/critical-bug-description
   ```

3. After fixing the bug, create a Pull Request.
4. Hotfixes should be merged into both `main` and `develop`.

### Releases
1. When `develop` has accumulated enough features for a release, create a release branch:
   ```
   git checkout develop
   git pull
   git checkout -b release/version-number
   ```

2. On the release branch, only bug fixes, documentation, and other release-oriented tasks should be performed.
3. When the release is ready, merge it into `main` and tag the release:
   ```
   git checkout main
   git merge --no-ff release/version-number
   git tag -a v1.0.0 -m "Version 1.0.0"
   git push origin main --tags
   ```

4. Also merge the release back into `develop`:
   ```
   git checkout develop
   git merge --no-ff release/version-number
   git push origin develop
   ```

## Monorepo Release Management

Since Effective Office is organized as a monorepo containing multiple components (backend, client:tablet, etc.), we need a specific approach to versioning and tagging releases for individual components.

### Component Versioning

Each component in the monorepo maintains its own semantic versioning:

- **Backend**: `backend-vX.Y.Z` (e.g., `backend-v1.2.3`)
- **Client:Tablet**: `tablet-vX.Y.Z` (e.g., `tablet-v2.0.1`)

### Tagging Component Releases

When releasing a specific component:

1. Ensure you're on the `main` branch after merging a release branch:
   ```
   git checkout main
   git pull
   ```

2. Tag the release with the component prefix:
   ```
   # For backend
   git tag -a backend-v1.0.0 -m "Backend Release v1.0.0"

   # For tablet client
   git tag -a tablet-v1.0.0 -m "Tablet Client Release v1.0.0"
   ```

3. Push the tags to the remote repository:
   ```
   git push origin --tags
   ```

### Coordinated Releases

When releasing multiple components together:

1. Create separate tags for each component as described above
2. Additionally, you may create an overall project release tag:
   ```
   git tag -a v1.0.0 -m "Project Release v1.0.0 (Backend v1.0.0, Tablet v1.0.0)"
   ```

3. Document the component versions included in each project release

## Commit Message Guidelines

We follow these conventions for commit messages:

- Start with a type: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- Use the imperative mood ("Add feature" not "Added feature")
- Keep the first line under 50 characters
- Provide more detailed explanations in subsequent lines if necessary

Example:
```
feat: Add user authentication system

- Implement JWT token generation
- Add login and registration endpoints
- Create user repository and service
```

## Pull Request Process

1. Update the README.md or documentation with details of changes if appropriate
2. Update the version numbers in any examples files and the README.md to the new version
3. You may merge the Pull Request once you have the sign-off of at least one other developer
4. Ensure all CI checks pass before merging

## Git Hooks

We use pre-commit hooks to ensure code quality and prevent sensitive information from being committed:

1. Ensure you've run the installation script (`./scripts/install.sh`)
2. The pre-commit hook will run automatically when you commit changes
3. If the hook fails, address the issues before committing again
