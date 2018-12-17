package joelespinal.com.greenprodiver.constants

const val Book_Table_Name: String = "BOOKS"
class BookColumn {
    companion object {
        const val _ID: String = "_id"
        const val TITLE: String = "TITLE"
        const val AUTHOR: String = "AUTHOR"
        const val PUBLICATION_DATE: String = "PUBLICATION_DATE"

        val COLUMNS: Array<String> = arrayOf(_ID, TITLE, AUTHOR, PUBLICATION_DATE)
    }
}