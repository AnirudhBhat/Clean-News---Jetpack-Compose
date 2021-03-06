package com.abhat.cleannews_compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import com.abhat.cleannews_compose.di.NetworkGraphImpl
import com.abhat.cleannews_compose.di.NewsGraph
import com.abhat.cleannews_compose.di.ViewModelFactory
import com.abhat.cleannews_compose.ui.theme.CleanNewsComposeTheme
import com.abhat.cleannews_compose.ui.theme.WhiteWithAlpha
import com.abhat.cleannews_compose.ui.viewmodel.NewsViewModel
import com.abhat.cleannews_compose.ui.viewmodel.state.NewsUIState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class NewsActivity : ComponentActivity() {

    private val newsGraph: NewsGraph by lazy { NetworkGraphImpl(this) }
    val newsViewModel: NewsViewModel by viewModels {
        ViewModelFactory(this, newsGraph.newsRepository)
    }

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onNewsClick = { url: String ->
            newsViewModel.validateAndTriggerOpenLinkCommand(url)
        }
        setContent {
            CleanNewsComposeTheme {
                val systemUiController = rememberSystemUiController()
                if (isSystemInDarkTheme()) {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent
                    )
                } else {
                    systemUiController.setSystemBarsColor(
                        color = Color.White
                    )
                }
                observeEvent()
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BottomAppBarComposable(newsViewModel, onNewsClick)
                }
                SideEffect {

                }
            }
        }
    }

    private fun observeEvent() {
        newsViewModel.event.observe(this, Observer { event ->
            when (event) {
                is NewsViewModel.Event.OpenLink -> openLinkInBrowser(this, event.url)
                is NewsViewModel.Event.ShareNews -> shareNews(this, event.newsUrl)
            }
        })
    }
}

private fun openLinkInBrowser(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(context, browserIntent, null)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun BottomAppBarComposable(
    newsViewModel: NewsViewModel,
    onNewsClick: (String) -> Unit
) {
    val news: NewsUIState by newsViewModel.viewState.observeAsState(NewsUIState.Loading)
    val selectedItem = remember { mutableStateOf(news.newsList?.get(0)?.source ?: "") }
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    selectedItem.value = news.newsList?.get(1)?.source ?: "dd"

    Column {
        Scaffold(
            topBar = {
                TopAppBar {
                    Text(
                        text = "Clean News",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(vertical = 8.dp)
                    )
                }
            },
            content = {
                Box {
                    when (news) {
                        is NewsUIState.Loading -> {
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
                        is NewsUIState.Content -> {
                            News(newsList = news.newsList, newsViewModel = newsViewModel, onNewsClick = onNewsClick)
                        }
                        is NewsUIState.Error -> {
                            News(newsList = news.newsList, newsViewModel = newsViewModel, onNewsClick = onNewsClick)
                            coroutineScope.launch {
                                scaffoldState.showSnackbar(
                                    message = news.error?.localizedMessage ?: "Something went wrong"
                                )
                            }
                            SnackbarHost(
                                hostState = scaffoldState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 56.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    elevation = 8.dp,
                    backgroundColor = if (isSystemInDarkTheme()) {
                        Color.DarkGray
                    } else {
                        WhiteWithAlpha
                    },
                    content = {
                        Row {
                            BottomNavigationItem(
                                icon = {
                                    //Icon(Icons.Filled.Favorite , "")
                                },
                                label = { Text(text = "DD") },
                                selected = selectedItem.value == "dd",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://ddnews.gov.in/rss-feeds")
                                    selectedItem.value = "dd"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.Search , "")
                                },
                                label = { Text(text = "AIR") },
                                selected = selectedItem.value == "newsonair",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://www.newsonair.gov.in/top_rss.aspx")
//                                result.value = "Save icon clicked"
                                    selectedItem.value = "newsonair"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.Notifications ,  "")
                                },


                                label = { Text(text = "TOI") },
                                selected = selectedItem.value == "timesofindia",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://timesofindia.indiatimes.com/rssfeedstopstories.cms")
//                                result.value = "Upload icon clicked"
                                    selectedItem.value = "timesofindia"
                                },
                                alwaysShowLabel = true
                            )

                            BottomNavigationItem(
                                icon = {
//                                    Icon(Icons.Filled.LocationOn , "")
                                },
                                label = { Text(text = "Economic Times") },
                                selected = selectedItem.value == "economictimes",
                                onClick = {
                                    newsViewModel.getNewsAsync("https://economictimes.indiatimes.com/rssfeedstopstories.cms")
//                                result.value = "Download icon clicked"
                                    selectedItem.value = "economictimes"
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

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun News(
    newsList: List<NewsViewModel.News>?,
    newsViewModel: NewsViewModel,
    onNewsClick: (String) -> Unit) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    var description = remember {
        mutableStateOf("")
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(800.dp)
            ) {
                Text(
                    text = description.value,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 8.dp)
                )
            }
        }, sheetPeekHeight = 0.dp
    ) {
        Column {
            if (!newsList.isNullOrEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 0.dp,
                        top = 0.dp,
                        end = 0.dp,
                        bottom = 56.dp
                    )
                ) {
                    items(newsList?.size ?: 0) {
                        Card(
                            elevation = 16.dp,
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    onNewsClick(newsList!![it].link!!)
                                },
                                onLongClick = {
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                    description.value = newsList!![it].description!!
                                }
                            )
                        ) {
                            Column {
                                val format =
                                    if (newsList!![it].link?.isEmpty()!!) {
                                        "dd-mm-yyyy | HH:mm a"
                                    } else {
                                        "EEE, d MMM yyyy HH:mm:ss"
                                    }
                                Text(
                                    text = newsList!![it].title,
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .padding(vertical = 8.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = formatDate(
                                            newsList!![it].pubDate.replace(
                                                "pubDate",
                                                ""
                                            ), format
                                        ),
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier
                                            .padding(horizontal = 24.dp)
                                            .padding(vertical = 12.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (
                                        newsList!![it].link?.isEmpty() == false
                                        && newsList!![it].link?.equals(
                                            "link",
                                            ignoreCase = true
                                        ) == false
                                    ) {
                                        IconButton(
                                            onClick = {
                                                newsViewModel.shareNews(newsList!![it].link!!)
                                            },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_share),
                                                contentDescription = "Share news",
                                                tint = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
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

private fun shareNews(context: Context, url: String) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    val shareBody = url
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CleanNewsComposeTheme {
    }
}