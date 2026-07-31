package org.jetbrains.compose.ui.tooling.preview

// compose-ui-tooling-preview под linux не публикуется, а @Preview стоит в общем коде
// feature/main. Занимаем пакет аннотацией-заглушкой: подключается только aurora-вариантом
// (kotlin.srcDir в build.aurora.gradle.kts), импорты вызывающего кода не меняются.
// Набор параметров обязан покрывать все места вызова, иначе модуль не соберётся.

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
