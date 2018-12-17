package joelespinal.com.greenprodiver.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

class LibraryContract {

    companion object {
        const val CONTENT_AUTHORITY: String = "com.joelespinal.library"
        val BASE_CONTENT_URI: Uri = Uri.parse("content://${CONTENT_AUTHORITY}")
        const val PATH_BOOKS = "books"
        val CONTENT_URI: Uri? = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS)

        val BOOK_LIST_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/${CONTENT_AUTHORITY}/${PATH_BOOKS}"
        val BOOK_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/${CONTENT_AUTHORITY}/${PATH_BOOKS}"
        val _ID: String = BaseColumns._ID


    }
}