package com.shadov.codehellven.api.infrastructure

import com.shadov.codehellven.api.infrastructure.filters.RequestIdentifiersFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration
internal open class FiltersConfiguration {
    @Bean
    @Order(1)
    open fun requestIdentifiersFilter() = RequestIdentifiersFilter()
}