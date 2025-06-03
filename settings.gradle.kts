rootProject.name = "effective-office"

include(":backend")
include(
    "backend:app",
    "backend:core:domain",
    "backend:core:repository",
    "backend:feature",
    "backend:feature:authorization",
    "backend:feature:user",
    "backend:feature:booking:core",
    "backend:feature:booking:calendar:google",
    "backend:feature:booking:calendar:dummy",
)
