package com.example.compoundinggrowth

import android.util.Log
import com.example.compoundinggrowth.model.Budget
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.compoundinggrowth.model.Transaction
import com.google.firebase.firestore.Filter

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootTxnCollection = "allTransaction"
    private val rootBudgetCollection = "allBudget"

    private fun limitAndGetTransactions(
        user: User,
        query: Query,
        resultListener: (List<Transaction>)->Unit
    ) {
        query
            .limit(100)
            .where(Filter.or(
                Filter.equalTo("ownerUid", user.uid),
                Filter.equalTo("viewer", user.email)))
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

    private fun limitAndGetBudgets(
        user : User,
        query: Query,
        resultListener: (List<Budget>)->Unit
    ) {
        query
            .limit(100)
            .where(Filter.equalTo("ownerUid", user.uid))
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(Budget::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    fun fetchTransaction(
        user : User,
        resultListener: (List<Transaction>) -> Unit
    ) {

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING // else Query.Direction.DESCENDING
        limitAndGetTransactions(user,
            db.collection(rootTxnCollection).orderBy(orderBy, ascending),
            resultListener)
    }

    fun createTransaction(
        user : User,
        transaction: Transaction,
        resultListener: (List<Transaction>)->Unit
    ) {

        val transactionRef = db.collection(rootTxnCollection)

        transactionRef.document(transaction.uuid)
            .set(transaction)
            .addOnSuccessListener { Log.d("createTransaction", "transaction successfully added!") }
            .addOnFailureListener { e -> Log.w("createTransaction", "Error adding document", e) }

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING // else Query.Direction.DESCENDING

        limitAndGetTransactions(user,
            transactionRef.orderBy(orderBy, ascending),
            resultListener)
    }

    fun removeTransaction(
        user: User,
        transaction: Transaction,
        resultListener: (List<Transaction>)->Unit
    ) {
        val transactionRef = db.collection(rootTxnCollection)

        transactionRef.document(transaction.uuid)
            .delete()
            .addOnSuccessListener { Log.d("removeTransaction", "Transaction successfully deleted!") }
            .addOnFailureListener { e -> Log.w("removeTransaction", "Error deleting document", e) }

        val orderBy = "date"
        val ascending = Query.Direction.ASCENDING

        limitAndGetTransactions(user,
            transactionRef.orderBy(orderBy, ascending),
            resultListener)
    }

    fun updateTransaction(
        user: User,
        transaction: Transaction,
        resultListener: (List<Transaction>)->Unit
    ) {
        val transactionRef = db.collection(rootTxnCollection)

        transactionRef.document(transaction.uuid)
            .update("category", transaction.category,
                "viewer", transaction.viewer)
            .addOnSuccessListener { Log.d("updateTransaction", "Transaction successfully updated!") }
            .addOnFailureListener { e -> Log.w("updateTransaction", "Error updating document", e) }

        limitAndGetTransactions(user,
            transactionRef,
            resultListener)
    }

    fun fetchBudget(
        user: User,
        resultListener: (List<Budget>) -> Unit
    ) {
        limitAndGetBudgets(user,
            db.collection(rootBudgetCollection),
            resultListener)
    }

    fun createBudget(
        user: User,
        budget: Budget,
        resultListener: (List<Budget>)->Unit
    ) {

        val budgetRef = db.collection(rootBudgetCollection)

        budgetRef.document(budget.uuid)
            .set(budget)
            .addOnSuccessListener { Log.d("createBudget", "budget successfully added!") }
            .addOnFailureListener { e -> Log.w("createBudget", "Error adding document", e) }

        limitAndGetBudgets(user,
            budgetRef,
            resultListener)
    }

    fun updateBudget(
        user: User,
        budget: Budget,
        resultListener: (List<Budget>)->Unit
    ) {
        val budgetRef = db.collection(rootBudgetCollection)

        budgetRef.document(budget.uuid)
            .update("budgeted", budget.budgeted,
                "remaining", budget.remaining)
            .addOnSuccessListener { Log.d("updateBudget", "Budget successfully updated!") }
            .addOnFailureListener { e -> Log.w("updateBudget", "Error updating document", e) }

        limitAndGetBudgets(user,
            budgetRef,
            resultListener)
    }
}