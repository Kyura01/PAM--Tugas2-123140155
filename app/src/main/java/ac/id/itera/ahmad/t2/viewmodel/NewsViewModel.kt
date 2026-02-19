package ac.id.itera.ahmad.t2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ac.id.itera.ahmad.t2.data.News
import ac.id.itera.ahmad.t2.data.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()

    private val _allNews = MutableStateFlow<List<News>>(emptyList())
    private val _selectedCategory = MutableStateFlow("Semua")

    val selectedCategory: StateFlow<String> = _selectedCategory

    val newsList: StateFlow<List<News>> =
        combine(_allNews, _selectedCategory) { news, category ->
            if (category == "Semua") news
            else news.filter { it.category == category }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    init {
        startStream()
    }

    private fun startStream() {
        viewModelScope.launch {
            repository.getNewsStream()
                .onEach { news ->
                    _readCount.value++
                    _allNews.value = _allNews.value + news
                }
                .collect()
        }
    }

    fun changeCategory(category: String) {
        _selectedCategory.value = category
    }

    suspend fun getDetail(news: News): String {
        return repository.getNewsDetail(news)
    }
}
