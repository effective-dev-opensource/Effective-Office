package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.repository.OrganizerRepository

/**Use case for get organizers list*/
class OrganizersInfoUseCase(private val repository: OrganizerRepository) {
    suspend operator fun invoke() = repository.getOrganizersList()
}