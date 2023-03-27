/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.indexmanagement.controlcenter.notification

import org.opensearch.common.UUIDs
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.commons.authuser.User
import org.opensearch.core.xcontent.ToXContent
import org.opensearch.indexmanagement.IndexManagementPlugin
import org.opensearch.indexmanagement.controlcenter.notification.action.get.GetLRONConfigsResponse
import org.opensearch.indexmanagement.controlcenter.notification.model.LRONCondition
import org.opensearch.indexmanagement.controlcenter.notification.model.LRONConfig
import org.opensearch.indexmanagement.controlcenter.notification.util.getDocID
import org.opensearch.indexmanagement.controlcenter.notification.util.getPriority
import org.opensearch.indexmanagement.controlcenter.notification.util.supportedActions
import org.opensearch.indexmanagement.common.model.notification.Channel
import org.opensearch.indexmanagement.indexstatemanagement.randomChannel
import org.opensearch.indexmanagement.opensearchapi.string
import org.opensearch.indexmanagement.randomUser
import org.opensearch.tasks.TaskId
import org.opensearch.test.OpenSearchTestCase.randomBoolean
import org.opensearch.test.OpenSearchTestCase.randomLong
import org.opensearch.test.rest.OpenSearchRestTestCase

fun randomLRONConfig(
    lronCondition: LRONCondition = randomLRONCondition(),
    taskId: TaskId? = randomTaskId(),
    actionName: String? = randomActionName(),
    channels: List<Channel>? = List(OpenSearchRestTestCase.randomIntBetween(1, 10)) { randomChannel() },
    user: User? = randomUser()
): LRONConfig {
    val priority = getPriority(taskId, actionName)
    return LRONConfig(
        lronCondition = lronCondition,
        taskId = taskId,
        actionName = actionName,
        channels = channels,
        user = user,
        priority = priority
    )
}

fun randomLRONCondition(
    success: Boolean = randomBoolean(),
    failure: Boolean = randomBoolean()
): LRONCondition {
    return LRONCondition(success, failure)
}

fun randomTaskId(
    nodeId: String = UUIDs.randomBase64UUID(),
    id: Long = randomLong()
): TaskId {
    return TaskId(nodeId, id)
}

fun randomActionName(): String {
    return supportedActions.random()
}

fun randomLRONConfigResponse(
    lronConfig: LRONConfig = randomLRONConfig()
): LRONConfigResponse {
    val id = getDocID(lronConfig.taskId, lronConfig.actionName)
    return LRONConfigResponse(
        id = id,
        lronConfig = lronConfig
    )
}

fun randomLRONConfigsResponse(
    size: Int = 10
): GetLRONConfigsResponse {
    return GetLRONConfigsResponse(
        lronConfigResponses = List(size) { randomLRONConfigResponse() },
        size,
        randomBoolean()
    )
}

fun LRONConfig.toJsonString(params: ToXContent.Params = ToXContent.EMPTY_PARAMS): String = this.toXContent(
    XContentFactory.jsonBuilder(), params
).string()

fun getResourceURI(taskId: TaskId?, actionName: String?): String {
    return "${IndexManagementPlugin.LRON_BASE_URI}/${getDocID(taskId, actionName)}"
}