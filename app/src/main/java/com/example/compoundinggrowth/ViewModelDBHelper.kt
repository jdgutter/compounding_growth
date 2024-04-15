package com.example.compoundinggrowth

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.compoundinggrowth.model.Transaction

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allTransaction"

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(query: Query,
                            resultListener: (List<Transaction>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(Transaction::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchTransaction(
        resultListener: (List<Transaction>) -> Unit
    ) {

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING // else Query.Direction.DESCENDING
        limitAndGet(db.collection(rootCollection).orderBy(orderBy, ascending), resultListener)

    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createTransaction(
        transaction: Transaction,
        resultListener: (List<Transaction>)->Unit
    ) {

        val transactionRef = db.collection(rootCollection)

        transactionRef.document(transaction.uuid)
            .set(transaction)
            .addOnSuccessListener { Log.d("createTransaction", "transaction successfully added!") }
            .addOnFailureListener { e -> Log.w("createTransaction", "Error adding document", e) }

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING // else Query.Direction.DESCENDING

        limitAndGet(transactionRef.orderBy(orderBy, ascending), resultListener)
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeTransaction(
        transaction: Transaction,
        resultListener: (List<Transaction>)->Unit
    ) {
        val transactionRef = db.collection(rootCollection)

        transactionRef.document(transaction.uuid)
            .delete()
            .addOnSuccessListener { Log.d("removeTransaction", "Transaction successfully deleted!") }
            .addOnFailureListener { e -> Log.w("removeTransaction", "Error deleting document", e) }

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING // else Query.Direction.DESCENDING

        limitAndGet(transactionRef.orderBy(orderBy, ascending), resultListener)
    }
}