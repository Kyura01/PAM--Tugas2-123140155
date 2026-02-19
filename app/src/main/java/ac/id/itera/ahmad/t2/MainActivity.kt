package ac.id.itera.ahmad.t2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.AssistChipDefaults.assistChipColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ac.id.itera.ahmad.t2.data.News
import ac.id.itera.ahmad.t2.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // 🔥 CUSTOM DARK THEME LANGSUNG DI SINI
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF00BFA6),
                    secondary = Color(0xFF7C4DFF),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel) {

    val newsList by viewModel.newsList.collectAsState()
    val readCount by viewModel.readCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf("Semua", "Technology", "Sports", "Politics")

    var selectedNews by remember { mutableStateOf<News?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ================= HEADER =================
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "📰 News Feed",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Surface(
                color = Color(0xFF00BFA6),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    "Dibaca: $readCount",
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= CATEGORY =================
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                AssistChip(
                    onClick = { viewModel.changeCategory(category) },
                    label = { Text(category) },
                    colors = assistChipColors(
                        containerColor =
                            if (selectedCategory == category)
                                Color(0xFF7C4DFF)
                            else Color(0xFF2A2A2A),
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= NEWS LIST =================
        LazyColumn {
            items(newsList) { news ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedNews = news
                            scope.launch { sheetState.show() }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "[${news.category.uppercase()}] ${news.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Tap untuk membaca detail...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }

    // ================= BOTTOM SHEET =================
    if (selectedNews != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedNews = null },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E1E)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Detail Berita",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ini adalah konten untuk artikel ${selectedNews!!.id}. " +
                            "Artikel ini masuk dalam kategori ${selectedNews!!.category} dan berjudul '${selectedNews!!.title}'. ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { selectedNews = null },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF)
                    )
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}
