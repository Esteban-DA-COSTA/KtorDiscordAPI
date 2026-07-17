package components.interactions

import components.MessagePayload
import components.enums.InteractionCallbackTypes
import kotlinx.serialization.Serializable

@Serializable
class InteractionCallBack(val type: InteractionCallbackTypes, var data: MessagePayload? = null)