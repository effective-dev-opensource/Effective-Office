package band.effective.mattermost.models.response

import band.effective.mattermost.models.response.models.Posts
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseGetPostsForChannel(
        @Json(name = "has_next")
        val hasNext: Boolean? = null,
        @Json(name = "next_post_id")
        val nextPostId: String,
        @Json(name = "order")
        val order: List<String>,
        @Json(name = "posts")
        val posts: Posts,
        @Json(name = "prev_post_id")
        val prevPostId: String
)