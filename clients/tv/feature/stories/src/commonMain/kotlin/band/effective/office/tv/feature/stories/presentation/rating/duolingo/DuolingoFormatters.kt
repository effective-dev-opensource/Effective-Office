package band.effective.office.tv.feature.stories.presentation.rating.duolingo

import androidx.compose.runtime.Composable
import band.effective.office.tv.feature.stories.Res
import band.effective.office.tv.feature.stories.*
import band.effective.office.tv.feature.stories.domain.model.DuolingoKey
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Maps Duolingo language codes to flag drawables.
 */
fun mapLanguageToFlag(language: String): DrawableResource =
    when {
        language.contains("ar") -> Res.drawable.ar
        language.contains("ca") -> Res.drawable.ca
        language.contains("cs") -> Res.drawable.cs
        language.contains("cy") -> Res.drawable.cy
        language.contains("da") -> Res.drawable.da
        language.contains("de") -> Res.drawable.de
        language.contains("el") -> Res.drawable.el
        language.contains("en") -> Res.drawable.en
        language.contains("eo") -> Res.drawable.eo
        language.contains("fi") -> Res.drawable.fi
        language.contains("fr") -> Res.drawable.fr
        language.contains("ga") -> Res.drawable.ga
        language.contains("gd") -> Res.drawable.gd
        language.contains("gn") -> Res.drawable.gn
        language.contains("ha") -> Res.drawable.ha
        language.contains("he") -> Res.drawable.he
        language.contains("hi") -> Res.drawable.hi
        language.contains("ht") -> Res.drawable.ht
        language.contains("hu") -> Res.drawable.hu
        language.contains("hv") -> Res.drawable.hv
        language.contains("id") -> Res.drawable.id
        language.contains("it") -> Res.drawable.it
        language.contains("ja") -> Res.drawable.ja
        language.contains("kl") -> Res.drawable.kl
        language.contains("ko") -> Res.drawable.ko
        language.contains("la") -> Res.drawable.la
        language.contains("nl") -> Res.drawable.nl
        language.contains("no") -> Res.drawable.no_bo
        language.contains("nv") -> Res.drawable.nv
        language.contains("pl") -> Res.drawable.pl
        language.contains("pt") -> Res.drawable.pt_copy
        language.contains("ro") -> Res.drawable.ro
        language.contains("ru") -> Res.drawable.ru
        language.contains("sv") -> Res.drawable.sv
        language.contains("sw") -> Res.drawable.sw
        language.contains("th") -> Res.drawable.th
        language.contains("tr") -> Res.drawable.tr
        language.contains("uk") -> Res.drawable.uk
        language.contains("vi") -> Res.drawable.vi
        language.contains("zh") -> Res.drawable.zh
        else -> Res.drawable.dualingo
    }

fun mapLanguagesToFlags(languages: List<String>): List<DrawableResource> =
    languages.map { mapLanguageToFlag(it) }

/**
 * Formats indicator text for Duolingo rating rows.
 */
@Composable
fun formatDuolingoIndicator(key: DuolingoKey, user: DuolingoUser): String =
    when (key) {
        DuolingoKey.Xp -> "${user.totalXp} ${stringResource(Res.string.xp_label)}"
        DuolingoKey.Streak -> "${user.streak} ${pluralStringResource(Res.plurals.days, user.streak)}"
    }

