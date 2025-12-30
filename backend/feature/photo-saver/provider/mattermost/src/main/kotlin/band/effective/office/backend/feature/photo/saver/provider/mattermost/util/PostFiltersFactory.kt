package band.effective.office.backend.feature.photo.saver.provider.mattermost.util

import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.PostDTO

/**
 * Factory for filtering posts based on reactions.
 * Provides utility methods to filter posts that need to be synchronized.
 */
object PostFiltersFactory {

    fun filterPostsForSync(
        posts: Collection<PostDTO>,
        requestSaveEmoji: String,
        saveSuccessEmoji: String
    ): List<PostDTO> {
        return posts.filter { post ->
            hasReaction(post, requestSaveEmoji) && !hasReaction(post, saveSuccessEmoji)
        }
    }
    
    /**
     * Checks if a post has a specific reaction.
     */
    private fun hasReaction(post: PostDTO, emojiName: String): Boolean {
        return post.metadata?.reactions?.any { it.emojiName == emojiName } == true
    }
}
