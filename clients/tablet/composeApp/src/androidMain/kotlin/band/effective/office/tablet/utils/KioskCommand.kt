package band.effective.office.tablet.utils

sealed interface KioskCommand {
    object Enable : KioskCommand
    object Disable : KioskCommand
}

object KioskCommandMapper {
    fun mapToKioskCommand(isKioskModeActive: Boolean): KioskCommand {
        return if (isKioskModeActive) KioskCommand.Enable else KioskCommand.Disable
    }
}
