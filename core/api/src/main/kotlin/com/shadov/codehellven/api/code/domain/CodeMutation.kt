package com.shadov.codehellven.api.code.domain

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shadov.codehellven.api.code.model.*
import com.shadov.codehellven.api.model.exception.orNotFoundException
import com.shadov.codehellven.api.task.domain.TaskRepository
import com.shadov.codehellven.api.user.domain.UserRepository
import com.shadov.codehellven.common.lazyLogger
import com.shadov.codehellven.common.model.CodeSubmission

internal class CodeMutation(
        private val codeService: CodeService,
        private val userRepository: UserRepository,
        private val taskRepository: TaskRepository,
        private val codeRequestRepository: CodeRequestRepository,
        private val codeResponseRepository: CodeResponseRepository
) : GraphQLMutationResolver {
    private val log by lazyLogger()

    fun submitCodeRequest(codeRequest: SubmitCodeRequest): CodeRequestQL {
        log.info("Submitting code request for user = ${codeRequest.submitterName} in task ${codeRequest.taskName}")

        val submitter = userRepository.findByName(codeRequest.submitterName)
                .orNotFoundException("User with name = ${codeRequest.submitterName} not found")

        val task = taskRepository.findByNameIgnoreCase(codeRequest.taskName)
                .orNotFoundException("Task with name = ${codeRequest.taskName} not found")

        val savedRequest = codeRequestRepository.insert(codeRequest.asEntity(submitter, task))

        val codeSubmission = CodeSubmission(
                callbackId = savedRequest.messageId!!.toHexString(),
                codeSnipet = savedRequest.codeSnippet
        )

        log.info("CodeRequest saved correctly, sending submission to external system")

        return codeService.executeCode(codeSubmission)
                .map { savedRequest.asGraphQL() }
                .onFailure { log.error("Could not run code request", it) }
                .getOrElseThrow(::CodeExecutionException)
    }

    fun acceptCodeResponse(codeResponse: AcceptCodeResponse): CodeResponseQL {
        log.info("Accepting code response for request with callbackId = ${codeResponse.callbackId}")

        val matchingRequest = codeRequestRepository.findById(codeResponse.callbackId)
                .orNotFoundException("Code request with callbackId = ${codeResponse.callbackId} not found")

        return codeResponseRepository.insert(codeResponse.asEntity(matchingRequest)).asGraphQL()
    }
}