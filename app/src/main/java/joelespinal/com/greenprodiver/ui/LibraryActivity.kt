package joelespinal.com.greenprodiver.ui

import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import joelespinal.com.greenprodiver.R
import joelespinal.com.greenprodiver.constants.BookColumn
import joelespinal.com.greenprodiver.data.BookContentProvider
import joelespinal.com.greenprodiver.ui.adapters.BookCursorRecyclerViewAdapter
import java.util.*


class LibraryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val offset = 30
    private var page = 0

    private var bookRecyclerView: RecyclerView? = null
    private var loadingMore = false
    private var shortToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)


        var uri2: Uri = BookContentProvider.urlForItems(0)

        var query2: Cursor? = contentResolver.query(uri2, null, null, null, null)


        val layoutManager = LinearLayoutManager(this)
        var bookAdapter = BookCursorRecyclerViewAdapter(this, query2)

        bookRecyclerView = findViewById(R.id.bookRecycleView)
        bookRecyclerView!!.setLayoutManager(layoutManager)
        bookRecyclerView!!.setAdapter(bookAdapter)

        Log.d("library", "oncreate")

        val itemsCountLocal = getItemsCountLocal()
        if (itemsCountLocal == 0) {
            fillTestElements()
        }

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
                    supportLoaderManager.restartLoader(0, null, this@LibraryActivity)//-------------
                }
            }
        }

        bookRecyclerView!!.addOnScrollListener(scrollListener)
        Log.d("CREATE", "CREATE")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }


    fun fillTestElements() {
        val size: Int = 1000
        var contentValuesArray: Array<ContentValues?> = arrayOfNulls(size)
        for (i in 0..(size - 1)) {
            var contentValues: ContentValues = ContentValues()
            contentValues.put(BookColumn.TITLE, "Book $i")
            contentValues.put(BookColumn.AUTHOR, "UNKNOWN")
            contentValues.put(BookColumn.PUBLICATION_DATE, Calendar.getInstance().timeInMillis)
            contentValuesArray[i] = contentValues

            var uri: Uri = BookContentProvider.urlForItems(0)
            contentResolver.insert(uri, contentValues)
        }

        // contentResolver.insert(LibraryContract.CONTENT_URI, contentValuesArray)
    }

    private fun getItemsCountLocal(): Int {
        var itemCount: Int = 0

        var uri: Uri = BookContentProvider.urlForItems(0)

        var query: Cursor? = contentResolver.query(uri, null, null, null, null)
        if (query != null) {
            itemCount = query.count
            query.close()
        }

        return itemCount
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            0 -> return CursorLoader(this, BookContentProvider.urlForItems(offset * page), null, null, null, null)
            else -> throw  IllegalArgumentException("no id handled!")
        }
    }


    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            0 -> {
                Log.d("LIBRARY", "onLoadFinished: loading MORE")
                shortToast!!.setText("loading MORE " + page)
                shortToast!!.show()

                var cursor: Cursor = (bookRecyclerView!!.adapter as BookCursorRecyclerViewAdapter).getCursor()!!

                var matrixCursor: MatrixCursor = MatrixCursor(BookColumn.COLUMNS)
                fillMx(cursor, matrixCursor)

                (bookRecyclerView!!.adapter as BookCursorRecyclerViewAdapter).swapCursor(matrixCursor)

                var runnable = Runnable {
                    run { loadingMore = false }
                }

                handlerToWait.postDelayed(runnable, 2000)
            }
            else -> throw IllegalArgumentException("no loader id handled!")
        }
    }

    var handlerToWait: Handler = Handler()

    override fun onLoaderReset(p0: Loader<Cursor>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun fillMx(data: Cursor, mx: MatrixCursor) {
        if (data == null)
            return

        data.moveToPosition(-1)
        while (data.moveToNext()) {
            mx.addRow(
                arrayOf(
                    data.getColumnIndex(BookColumn._ID),
                    data.getColumnIndex(BookColumn.TITLE),
                    data.getColumnIndex(BookColumn.AUTHOR),
                    data.getColumnIndex(BookColumn.PUBLICATION_DATE)
                )
            )
        }
    }
}
