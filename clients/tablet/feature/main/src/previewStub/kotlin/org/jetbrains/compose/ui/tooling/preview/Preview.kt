package org.jetbrains.compose.ui.tooling.preview

// compose-ui-tooling-preview publishes no linux targets, so the aurora variant squats its package.
// The parameter set has to cover every call site in common code, or the module will not compile.

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Retention(AnnotationRetention.BINARY)
annotation class Preview(
    val name: String = "",
    val widthDp: Int = -1,
    val heightDp: Int = -1,
    val locale: String = "",
)
