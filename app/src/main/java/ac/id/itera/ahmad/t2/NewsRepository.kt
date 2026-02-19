package ac.id.itera.ahmad.t2.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class NewsRepository {

    private val categories = listOf("Technology", "Sports", "Politics")

    fun getNewsStream(): Flow<News> = flow {
        var id = 1
        while (true) {
            delay(2000)

            val news = News(
                id = id,
                title = "Breaking News #$id",
                category = categories.random(),
                content = "This is detailed content for news #$id"
            )

            emit(news)
            id++
        }
    }

    suspend fun getNewsDetail(news: News): String {
        delay(1000)
        return "DETAIL: ${news.content}"
    }
}
