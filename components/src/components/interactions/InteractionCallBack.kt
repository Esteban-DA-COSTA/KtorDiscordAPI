package components.interactions

import components.Message
import components.enums.InteractionCallbackTypes
import kotlinx.serialization.Serializable

@Serializable
class InteractionCallBack(val type: InteractionCallbackTypes, var data: Message? = null)