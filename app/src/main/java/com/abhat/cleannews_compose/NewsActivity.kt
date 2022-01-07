package com.abhat.cleannews_compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.abhat.cleannews_compose.di.NetworkGraphImpl
import com.abhat.cleannews_compose.di.NewsGraph
import com.abhat.cleannews_compose.ui.theme.CleanNewsComposeTheme
import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import java.text.SimpleDateFormat

class NewsActivity : ComponentActivity() {

    private val newsGraph: NewsGraph by lazy { NetworkGraphImpl(this) }
    private val newsViewModel: NewsViewModel by lazy { newsGraph.newsViewModel }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanNewsComposeTheme {
                // A surface container using the 'background' color from the theme
                val onNewsClick = {
                        url: String -> openLinkInBrowser(this, url)
                }
                Surface(color = MaterialTheme.colors.background) {
                    BottomAppBarComposable(newsViewModel, onNewsClick)
                    newsViewModel.getNewsAsync("https://ddnews.gov.in/rss-feeds")
                }
            }
        }
    }
}

private fun openLinkInBrowser(context: Context, url: String) {
    if (!url.isNullOrEmpty()) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, browserIntent, null)
    }
}

@ExperimentalMaterialApi
@Composable
private fun BottomAppBarComposable(
    newsViewModel: NewsViewModel,
    onNewsClick: (String) -> Unit
) {
    val selectedItem = remember { mutableStateOf("upload") }
    val news: NewsUIState by newsViewModel.viewState.observeAsState(NewsUIState.Loading)

    Column {
        Scaffold(
            content = {
                Box {
                    if (news is NewsUIState.Loading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = if (isSystemInDarkTheme()) {
                                    Color.White
                                } else {
                                    Color.Black
                                }
                            )
                        }
                    }
                    Column {
                        if (!news.newsList.isNullOrEmpty()) {
                            LazyColumn(modifier = Modifier.padding(
                                start = 0.dp,
                                top = 0.dp,
                                end = 0.dp,
                                bottom = 56.dp
                            )) {
                                items(news.newsList?.size ?: 0) {
                                    Card(
                                        elevation = 16.dp,
                                        onClick = {
                                            onNewsClick(news.newsList!![it].link!!)
                                        }
                                    ) {
                                        Column {
                                            val format = if (news.newsList!![it].link?.isEmpty()!!) {
                                                "dd-mm-yyyy | HH:mm a"
                                            } else {
                                                "EEE, d MMM yyyy HH:mm:ss"
                                            }
                                            Text(
                                                text = news.newsList!![it].title,
                                                style = MaterialTheme.typography.body1,
                                                textAlign = TextAlign.Left,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 24.dp)
                                                    .padding(vertical = 8.dp)
                                            )
                                            Text(
                                                text = formatDate(news.newsList!![it].pubDate.replace("pubDate", ""), format),
                                                style = MaterialTheme.typography.subtitle1,
                                                textAlign = TextAlign.Left,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 24.dp)
                                                    .padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    content = {
                        BottomNavigation {
                            BottomNavigationItem(
                                icon = {
                                    //Icon(Icons.Filled.Favorite , "")
                                },
                                label = { Text(text = "DD")},
                                selected = selectedItem.value == "DD",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://ddnews.gov.in/rss-feeds")
                                    selectedItem.value = "DD"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.Search , "")
                                },
                                label = { Text(text = "AIR")},
                                selected = selectedItem.value == "AIR",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://www.newsonair.gov.in/top_rss.aspx")
//                                result.value = "Save icon clicked"
                                    selectedItem.value = "AIR"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.Notifications ,  "")
                                },


                                label = { Text(text = "TOI")},
                                selected = selectedItem.value == "TOI",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://timesofindia.indiatimes.com/rssfeedstopstories.cms")
//                                result.value = "Upload icon clicked"
                                    selectedItem.value = "TOI"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.LocationOn , "")
                                },
                                label = { Text(text = "Economic Times")},
                                selected = selectedItem.value == "EconomicTimes",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://economictimes.indiatimes.com/rssfeedstopstories.cms")
//                                result.value = "Download icon clicked"
                                    selectedItem.value = "EconomicTimes"
                                },
                                alwaysShowLabel = true
                            )
                        }
                    }
                )
            }
        )
    }
}

private fun formatDate(date: String, format: String): String {
    /*
        dd -> (03-01-2022 | 12:15 pm -> dd-mm-yyyy | HH:mm a)
        AIR -> (EEE, d MMM yyyy HH:mm:ss Z ---> Wed, 4 Jul 2001 12:08:56 -0700)
        TOI -> (EEE, d MMM yyyy HH:mm:ss Z ---> Wed, 4 Jul 2001 12:08:56 -0700)
        The Hindu -> (EEE, d MMM yyyy HH:mm:ss Z ---> Wed, 4 Jul 2001 12:08:56 -0700)
     */
    return try {
        val formatter = SimpleDateFormat(format)
        if (DateUtils.isToday(formatter.parse(date).time)) {
            "Today"
        } else if (isYesterday(date, format)) {
            "Yesterday"
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}

private fun isYesterday(date: String, format: String): Boolean {
    val formatter = SimpleDateFormat(format)
    return DateUtils.isToday(formatter.parse(date).time + DateUtils.DAY_IN_MILLIS)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CleanNewsComposeTheme {
    }
}