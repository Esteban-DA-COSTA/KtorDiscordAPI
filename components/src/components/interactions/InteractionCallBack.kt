package ktordiscord.components.interactions

import ktordiscord.components.MessagePayload
import ktordiscord.components.enums.InteractionCallbackTypes
import kotlinx.serialization.Serializable

@Serializable
class InteractionCallBack(val type: InteractionCallbackTypes, var data: MessagePayload? = null)