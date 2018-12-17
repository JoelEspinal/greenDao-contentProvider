package joelespinal.com.greenprodiver.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import joelespinal.com.greenprodiver.constants.BookColumn
import joelespinal.com.greenprodiver.constants.Book_Table_Name
import joelespinal.com.greenprodiver.models.Book
import joelespinal.com.greenprodiver.models.BookDao
import joelespinal.com.greenprodiver.models.DaoMaster
import joelespinal.com.greenprodiver.models.DaoSession
import org.greenrobot.greendao.query.QueryBuilder
import java.util.*


class BookContentProvider : ContentProvider() {

    private var dbHelper: DaoMaster.DevOpenHelper? = null

    private val BOOKS: Int = 100
    private val BOOKS_ID: Int = 101
    private var uriMaster: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)


    companion object {
        fun urlForItems(limit: Int): Uri {
            return Uri.parse("content://" + LibraryContract.CONTENT_AUTHORITY + "/" + LibraryContract.PATH_BOOKS + "/offset/" + limit)
        }
    }


    override fun onCreate(): Boolean {
        TODO("Implement this to initialize your content provider on startup.")
        val dbHelper = DaoMaster.DevOpenHelper(context, "library.db")
        return false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        TODO("Implement this to handle query requests from clients.")

        val cursor: Cursor

        val database: SQLiteDatabase = dbHelper!!.readableDatabase
        val daoSession: DaoSession = DaoMaster(database).newSession()
        var bookDao = daoSession.bookDao
        val match: Int = uriMaster.match(uri)

        when (match) {
            BOOKS -> {
                val queryBuilder: QueryBuilder<Book> = bookDao.queryBuilder().where(BookDao.Properties.Id.isNotNull)
                cursor = queryBuilder.buildCursor().query()
            }
            BOOKS_ID -> {
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                val queryBuilder: QueryBuilder<Book> =
                    bookDao.queryBuilder().where(BookDao.Properties.Id.eq(selectionArgs))
                cursor = queryBuilder.buildCursor().query()
            }
            else -> throw IllegalArgumentException("cannot query unknown URI " + uri)
        }

        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor

    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        TODO("Implement this to handle requests to insert a new row.")

        val match = uriMaster.match(uri)
        when (match) {
            BOOKS -> return insertBook(uri, values)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
        val match = uriMaster.match(uri)
        when (match) {
            BOOKS -> return updateBook(uri, values, selection, selectionArgs)
            BOOKS_ID -> {
                selection = BookColumn._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return updateBook(uri, values!!, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }

    private fun updateBook(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if (values!!.size() == 0) {
            return 0
        }

        val title: String = values.getAsString(BookColumn.TITLE)
        if (title.isNullOrEmpty()) {
            throw IllegalArgumentException("Book Requires a title")
        }

        var author: String = values.getAsString(BookColumn.AUTHOR)
        if (author.isNullOrBlank()) {
            throw IllegalArgumentException("Book Requires at less an Author")
        }

        val dateLong: Long = values.getAsLong(BookColumn.PUBLICATION_DATE)
        val publicationDate: Date
        if (dateLong > 0L) {
            throw IllegalArgumentException("Book Requires a Date")
        } else {
            publicationDate = Date(dateLong)
        }

        val database: SQLiteDatabase = dbHelper!!.writableDatabase

        val rowsUpdated: Int
        rowsUpdated = database.update(Book_Table_Name, values, selection, selectionArgs)

        if (rowsUpdated > 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return rowsUpdated
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")

        val database: SQLiteDatabase = dbHelper!!.writableDatabase
        val match: Int = uriMaster.match(uri)

        val rowsDeleted: Int

        when (match) {
            BOOKS -> rowsDeleted = database.delete(Book_Table_Name, selection, selectionArgs)
            BOOKS_ID -> {
                selection = BookColumn._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(Book_Table_Name, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }

        if (rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )


        val match: Int = uriMaster.match(uri)
        when (match) {
            BOOKS -> return LibraryContract.BOOK_LIST_TYPE
            BOOKS -> return LibraryContract.BOOK_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }


    private fun insertBook(uri: Uri, values: ContentValues): Uri {

        val title: String = values.getAsString(BookColumn.TITLE)
        if (title.isNullOrEmpty()) {
            throw IllegalArgumentException("Book Requires a title")
        }

        var author: String = values.getAsString(BookColumn.AUTHOR)
        if (author.isNullOrBlank()) {
            throw IllegalArgumentException("Book Requires at less an Author")
        }

        val dateLong: Long = values.getAsLong(BookColumn.PUBLICATION_DATE)
        val publicationDate: Date
        if (dateLong > 0L) {
            throw IllegalArgumentException("Book Requires a Date")
        } else {
            publicationDate = Date(dateLong)
        }

        val database: SQLiteDatabase = dbHelper!!.writableDatabase
        val daoSession: DaoSession = DaoMaster(database).newSession()
        var bookDao = daoSession.bookDao
        val match: Int = uriMaster.match(uri)


        val book: Book = Book()
        book.title = title
        book.author = author
        book.publicationDate = publicationDate

        val id: Long = bookDao.insert(book)

        context.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

}
