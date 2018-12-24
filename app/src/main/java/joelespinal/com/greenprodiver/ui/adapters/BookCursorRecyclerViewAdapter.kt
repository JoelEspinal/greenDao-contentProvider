package joelespinal.com.greenprodiver.ui.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import joelespinal.com.greenprodiver.R

class BookCursorRecyclerViewAdapter : CursorRecyclerViewAdapter<CursorRecyclerViewAdapter.VH> {


    constructor(context: Context, cursor: Cursor?) : super(context, cursor)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursorRecyclerViewAdapter.VH {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_book_card, parent, false)
        return CursorRecyclerViewAdapter.VH(view)
    }

    override fun onBindViewHolder(viewHolder: CursorRecyclerViewAdapter.VH, cursor: Cursor) {
        var holder: CursorRecyclerViewAdapter.VH = viewHolder
        cursor.moveToPosition(cursor.position)
        holder.setData(cursor)
    }
}