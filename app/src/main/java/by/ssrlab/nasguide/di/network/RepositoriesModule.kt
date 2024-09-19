package by.ssrlab.nasguide.di.network

import by.ssrlab.domain.repository.network.CommonSearchRepository
import by.ssrlab.domain.repository.network.DevelopmentsRepository
import by.ssrlab.domain.repository.network.EventsRepository
import by.ssrlab.domain.repository.network.OrgsRepository
import by.ssrlab.domain.repository.network.PersonsRepository
import by.ssrlab.domain.repository.network.PlacesRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoriesModule = module {

    single(named("network")) { DevelopmentsRepository(get(named("network"))) }

    single(named("network")) { EventsRepository(get(named("network"))) }

    single(named("network")) { OrgsRepository(get(named("network"))) }

    single(named("network")) { PersonsRepository(get(named("network"))) }

    single(named("network")) { PlacesRepository(get(named("network"))) }

    single(named("network")) {
        CommonSearchRepository(
            devApi = get(named("network")),
            eventApi = get(named("network")),
            orgsApi = get(named("network")),
            personsApi = get(named("network")),
            placesApi = get(named("network"))
        )
    }
}