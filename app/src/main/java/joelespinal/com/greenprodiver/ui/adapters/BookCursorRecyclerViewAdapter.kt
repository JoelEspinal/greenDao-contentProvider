package joelespinal.com.greenprodiver.ui.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import joelespinal.com.greenprodiver.R

class BookCursorRecyclerViewAdapter : CursorRecyclerViewAdapter {

    constructor(context: Context, cursor: Cursor?) : super(context, cursor!!)

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_book_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(viewHolder: VH, cursor: Cursor) {
        var holder: VH = viewHolder
        cursor.moveToPosition(cursor.position)
        holder.setData(cursor)
    }

}