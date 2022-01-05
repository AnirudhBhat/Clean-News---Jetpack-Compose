package com.abhat.cleannews_compose.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * Created by Anirudh Uppunda on 27/10/20.
 */
@Root(name = "rss", strict = false)
data class Rss @JvmOverloads constructor(
        @field:Element(name = "channel", required = false) var channel: Channel? = null
)