package com.example.eventoscomunitarios.data.repository

import com.example.eventoscomunitarios.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun registerWithEmail(
        name: String,
        community: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onResult(false, task.exception?.message)
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid ?: ""
                val user = User(
                    id = uid,
                    name = name,
                    email = email,
                    community = community
                )

                firestore.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginWithCredential(
        credential: AuthCredential,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onResult(false, task.exception?.message)
                    return@addOnCompleteListener
                }

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    onResult(false, "No se pudo obtener el usuario.")
                    return@addOnCompleteListener
                }

                val uid = firebaseUser.uid
                val userDoc = firestore.collection("users").document(uid)

                userDoc.get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            // Crear perfil básico si viene de Google por primera vez
                            val user = User(
                                id = uid,
                                name = firebaseUser.displayName ?: "",
                                email = firebaseUser.email ?: "",
                                community = ""
                            )
                            userDoc.set(user)
                                .addOnSuccessListener { onResult(true, null) }
                                .addOnFailureListener { e -> onResult(false, e.message) }
                        } else {
                            onResult(true, null)
                        }
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun logout() {
        auth.signOut()
    }
}
