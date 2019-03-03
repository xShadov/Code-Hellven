package com.shadov.codehellven.api.solution.domain

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shadov.codehellven.api.model.PageInput
import com.shadov.codehellven.api.model.PagedResponse
import com.shadov.codehellven.api.model.asPagedResponse
import com.shadov.codehellven.api.model.asRequest
import com.shadov.codehellven.api.solution.model.*
import com.shadov.codehellven.api.task.domain.TaskRepository
import com.shadov.codehellven.api.user.domain.UserRepository
import io.vavr.collection.List as VavrList

internal class SolutionQuery(
        private val solutionRepository: SolutionRepository,
        private val taskRepository: TaskRepository,
        private val userRepository: UserRepository
) : GraphQLQueryResolver {

    fun searchSolutions(filter: SolutionFilter, sort: VavrList<SolutionSort>, page: PageInput): PagedResponse<SolutionQL> {
        val user = filter.finisherName?.let { userName ->
            userRepository.findByName(userName)
                    .orElseThrow { IllegalArgumentException("User with name $userName not found") }
        }

        val task = filter.taskName?.let { taskName ->
            taskRepository.findByNameIgnoreCase(taskName)
                    .orElseThrow { IllegalArgumentException("Task with name $taskName not found") }
        }

        return solutionRepository.findAll(filter.toPredicate(user, task), page.asRequest(sort.asSort()))
                .map(SolutionEntity::asGraphQL)
                .asPagedResponse()
    }
}
