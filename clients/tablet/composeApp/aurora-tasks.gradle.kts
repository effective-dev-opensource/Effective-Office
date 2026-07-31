// Связка деплоя со сборкой: `runReleaseOnDevice` (плагин aurora-devices) сначала
// прогоняет полный конвейер сборки `buildReleasePipeline` (плагин aurora-build:
// initSysroot > link > package в Docker).
//
// Таски обоих плагинов регистрируются в afterEvaluate, поэтому используем
// `tasks.matching { … }.configureEach` — `tasks.named(...)` на этом этапе упадёт.
if (providers.gradleProperty("buildVariant").orNull == "aurora") {
    tasks.matching { it.name == "runReleaseOnDevice" }.configureEach {
        dependsOn("buildReleasePipeline")
    }
}
