package org.jetbrains.compose.ui.tooling.preview

// compose-ui-tooling-preview publishes no linux target, and @Preview is used in feature/main's
// common code. We squat the package with a stub annotation: wired in by the aurora variant only
// (kotlin.srcDir in build.aurora.gradle.kts), callers' imports stay untouched.
// The parameter set has to cover every call site or the module will not compile.

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Retention(AnnotationRetention.BINARY)
annotation class Preview(
    val name: String = "",
    val group: String = "",
    val widthDp: Int = -1,
    val heightDp: Int = -1,
    val locale: String = "",
    val fontScale: Float = 1f,
    val showBackground: Boolean = false,
    val backgroundColor: Long = 0,
)
