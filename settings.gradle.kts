rootProject.name = "effective-office"

include(":backend")
include(
    "backend:app",
    "backend:domain",
    "backend:repository",
    "backend:core:domain",
    "backend:core:repository",
    "backend:feature",
    "backend:feature:authorization",
    "backend:feature:user",
)
