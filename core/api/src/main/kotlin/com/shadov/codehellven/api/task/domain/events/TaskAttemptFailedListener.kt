package com.shadov.codehellven.api.task.domain.events

import com.shadov.codehellven.api.code.model.CodeResponseEntity
import com.shadov.codehellven.api.task.domain.TaskRepository
import com.shadov.codehellven.common.lazyLogger
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent

internal class TaskAttemptFailedListener(
        private val taskRepository: TaskRepository
) : AbstractMongoEventListener<CodeResponseEntity>() {
    private val log by lazyLogger()

    override fun onAfterSave(event: AfterSaveEvent<CodeResponseEntity>) {
        log.info("CodeResponse was saved, TaskAttemptFailed event called - checking if response was a failed attempt")

        if (event.source.completed.not()) {
            log.info("Saved CodeResponse was a failed attempt, incrementing matching task's failed attempts count")

            val task = event.source.codeRequest.task
            taskRepository.failedAttempt(task)
                    .onFailure { ex -> log.error("Could not add failed attempt to task ${task.name}", ex) }
        }
    }
}