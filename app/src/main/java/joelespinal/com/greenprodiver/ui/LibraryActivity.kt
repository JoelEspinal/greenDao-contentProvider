package joelespinal.com.greenprodiver.ui

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import joelespinal.com.greenprodiver.R
import joelespinal.com.greenprodiver.constants.BookColumn
import joelespinal.com.greenprodiver.data.BookContentProvider
import joelespinal.com.greenprodiver.ui.adapters.BookCursorRecyclerViewAdapter
import java.util.*

class LibraryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val LIBRARY_LOADER = 0
    private val offset = 10
    private var page = 0

    private lateinit var bookRecyclerView: RecyclerView
    private var loadingMore = false
    private lateinit var bookAdapter: BookCursorRecyclerViewAdapter
    private lateinit var shortToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val layoutManager = LinearLayoutManager(this)
        bookAdapter = BookCursorRecyclerViewAdapter(this, null)

        bookRecyclerView = findViewById(R.id.bookRecycleView)
        bookRecyclerView!!.layoutManager = layoutManager
        bookRecyclerView!!.adapter = bookAdapter

        shortToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        var scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                var lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()
                var maxPosition = layoutManager.itemCount

                if (lastVisibleItemPosition == maxPosition - 1) {
                    if (loadingMore)
                        return

                    loadingMore = true
                    page++
                    LoaderManager.getInstance(this@LibraryActivity).restartLoader(0, null, this@LibraryActivity)//-------------
                }
            }
        }

        bookRecyclerView!!.addOnScrollListener(scrollListener)

        LoaderManager.getInstance(this).initLoader(LIBRARY_LOADER, null, this)

        Log.d("CREATE", "CREATE")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_add -> {
                fillTestElements()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun fillTestElements() {
        val size = 10
        for (i in 1..size) {
            val contentValues = ContentValues()
            contentValues.put(BookColumn.TITLE, "Book $i")
            contentValues.put(BookColumn.AUTHOR, "UNKNOWN")
            contentValues.put(BookColumn.PUBLICATION_DATE, Calendar.getInstance().timeInMillis)

            val uri: Uri = BookContentProvider.urlForItems(0)
            contentResolver.insert(uri, contentValues)

            LoaderManager.getInstance(this).restartLoader(LIBRARY_LOADER, null, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            LIBRARY_LOADER -> {
                return CursorLoader(
                    this,
                    BookContentProvider.urlForItems(offset * page),
                    BookColumn.COLUMNS,
                    null,
                    null,
                    null
                )

            }
            else -> throw  IllegalArgumentException("no id handled!")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            LIBRARY_LOADER -> {
                shortToast!!.setText("loading MORE " + page)
                shortToast!!.show()

                bookAdapter.changeCursor(data!!)
            }
            else -> throw IllegalArgumentException("no loader id handled!")
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {
        bookAdapter!!.swapCursor(null)
    }
}
