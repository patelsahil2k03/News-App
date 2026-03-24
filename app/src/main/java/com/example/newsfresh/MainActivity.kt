package com.example.newsfresh

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var newsAdapter: NewsListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyStateText = findViewById(R.id.emptyStateText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsListAdapter(this)
        recyclerView.adapter = newsAdapter

        fetchData()
    }

    private fun fetchData() {
        showLoading(true)

        val apiKey = BuildConfig.NEWS_API_KEY
        if (apiKey.isBlank()) {
            showLoading(false)
            emptyStateText.visibility = View.VISIBLE
            emptyStateText.text = getString(R.string.error_missing_api_key)
            return
        }

        val url =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=$apiKey"

        val request = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val newsJsonArray = response.optJSONArray("articles")
                val newsArray = ArrayList<News>()

                if (newsJsonArray != null) {
                    for (i in 0 until newsJsonArray.length()) {
                        val newsJsonObject = newsJsonArray.optJSONObject(i) ?: continue

                        val title = newsJsonObject.optString("title", "Untitled")
                        val author = newsJsonObject.optString("author", "Unknown author")
                        val articleUrl = newsJsonObject.optString("url", "")
                        val imageUrl = newsJsonObject.optString("urlToImage", "")

                        if (articleUrl.isNotBlank()) {
                            newsArray.add(
                                News(
                                    title = title,
                                    author = author,
                                    url = articleUrl,
                                    imageUrl = imageUrl
                                )
                            )
                        }
                    }
                }

                showLoading(false)
                newsAdapter.updateNews(newsArray)
                emptyStateText.visibility = if (newsArray.isEmpty()) View.VISIBLE else View.GONE
                if (newsArray.isEmpty()) {
                    emptyStateText.text = getString(R.string.error_no_articles)
                }
            },
            { error ->
                showLoading(false)
                emptyStateText.visibility = View.VISIBLE
                emptyStateText.text = getString(R.string.error_fetch_news)
                Toast.makeText(
                    this,
                    getString(R.string.error_fetch_news_toast, error.localizedMessage ?: "unknown"),
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("User-Agent" to "NewsFresh-Android/1.0")
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        if (isLoading) {
            emptyStateText.visibility = View.GONE
        }
    }

    override fun onItemClicked(item: News) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
