package com.abhat.cleannews_compose.data.models

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * Created by Anirudh Uppunda on 27/10/20.
 */
@Root(name = "channel", strict = false)
data class Channel @JvmOverloads constructor(
        @field:ElementList(inline = true) var items: List<Item>? = null
)