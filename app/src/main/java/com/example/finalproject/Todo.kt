package com.example.finalproject

import com.google.firebase.firestore.DocumentId


data class Todo (
    @DocumentId
    var id: String = "",
    var task: String = "",
   var status: Boolean = false,
     var user: String = "",
)