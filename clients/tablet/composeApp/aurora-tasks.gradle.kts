// Ties deploy to build: `runReleaseOnDevice` (aurora-devices plugin) first runs the full
// build pipeline `buildReleasePipeline` (aurora-build plugin: initSysroot > link > package
// in Docker).
//
// Both plugins register their tasks in afterEvaluate, hence
// `tasks.matching { … }.configureEach` — `tasks.named(...)` throws at this point.
if (providers.gradleProperty("buildVariant").orNull == "aurora") {
    tasks.matching { it.name == "runReleaseOnDevice" }.configureEach {
        dependsOn("buildReleasePipeline")
    }
}
