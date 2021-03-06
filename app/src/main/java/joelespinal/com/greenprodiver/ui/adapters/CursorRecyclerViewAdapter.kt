package joelespinal.com.greenprodiver.ui.adapters

import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import joelespinal.com.greenprodiver.R
import joelespinal.com.greenprodiver.constants.BookColumn

abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH> {

    protected var context: Context? = null
    private var cursor: Cursor? = null
    private var dataValid: Boolean = false
    private var rowIdColumn: Int = 0
    private var dataSetObserver: DataSetObserver? = null


    constructor(context: Context, cursor: Cursor?) {
        this.context = context
        this.cursor = cursor
        dataValid = cursor != null
        rowIdColumn = if (dataValid) cursor!!.getColumnIndex("_id") else -1
        dataSetObserver = NotifyingDataSetObserver(this)
        cursor?.registerDataSetObserver(dataSetObserver)
    }


    fun getCursor(): Cursor? {
        return cursor
    }

    override fun getItemCount(): Int {
        if (dataValid && cursor != null) {
            return cursor!!.count
        }
        return 0
    }

    override fun getItemId(position: Int): Long {
        if (dataValid && cursor != null && cursor!!.moveToPosition(position)) {
            return cursor!!.getLong(rowIdColumn)
        }
        return 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor)


    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        if (!dataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }

        onBindViewHolder(viewHolder, cursor!!)
    }

    fun changeCursor(cursor: Cursor) {
        swapCursor(cursor)?.close()
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == this.cursor) {
            return null
        }

        val oldCursor: Cursor? = this.cursor
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver)
        }

        cursor = newCursor
        if (cursor != null) {
            cursor!!.registerDataSetObserver(dataSetObserver)

            rowIdColumn = newCursor!!.getColumnIndexOrThrow("_id")
            dataValid = true
            notifyDataSetChanged()
        } else {
            rowIdColumn = -1
            dataValid = false
            notifyDataSetChanged()
        }

        return oldCursor
    }

    fun setDataValid(dataValid: Boolean) {
        this.dataValid = dataValid
    }

    private class NotifyingDataSetObserver : DataSetObserver {
        private var adapter: RecyclerView.Adapter<*>? = null

        constructor(adapter: RecyclerView.Adapter<*>?) {
            this.adapter = adapter
        }

        override fun onChanged() {
            super.onChanged()
            (this.adapter as CursorRecyclerViewAdapter).setDataValid(true)
        }

        override fun onInvalidated() {
            super.onInvalidated()
            (this.adapter as CursorRecyclerViewAdapter).setDataValid(false)
        }
    }

    class VH : RecyclerView.ViewHolder {

        var titleTextView: TextView? = null
        var authorTextView: TextView? = null
        var publicationDateTextView: TextView? = null

        constructor(itemView: View) : super(itemView) {
            this.titleTextView = itemView.findViewById(R.id.titleText)
            this.authorTextView = itemView.findViewById(R.id.authorTextView)
            this.publicationDateTextView = itemView.findViewById(R.id.publicationDateTextView)
        }

        fun setData(cursor: Cursor) {
            val titleColumnIndex: Int = cursor.getColumnIndex(BookColumn.TITLE)
            val authorColumnIndex: Int = cursor.getColumnIndex(BookColumn.AUTHOR)
            val publicationDateColumnIndex: Int = cursor.getColumnIndex(BookColumn.PUBLICATION_DATE)

            val title = cursor.getString(titleColumnIndex)
            val author = cursor.getString(authorColumnIndex)
            val publicationDate = cursor.getString(publicationDateColumnIndex)

            titleTextView!!.text = title
            authorTextView!!.text = author
            publicationDateTextView!!.text = publicationDate
        }
    }
}