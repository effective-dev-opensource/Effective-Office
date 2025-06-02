rootProject.name = "effective-office"

include(":backend")
include(
    "backend:app",
    "backend:domain",
    "backend:repository",
    "backend:feature",
    "backend:feature:authorization",
    "backend:feature:user"
)
