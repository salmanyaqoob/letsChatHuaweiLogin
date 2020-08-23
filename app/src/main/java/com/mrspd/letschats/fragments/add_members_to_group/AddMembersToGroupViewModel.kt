package com.mrspd.letschats.fragments.add_members_to_group

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.mrspd.letschats.models.GroupName
import com.mrspd.letschats.models.User
import com.mrspd.letschats.util.AuthUtil
import com.mrspd.letschats.util.FirestoreUtil
import com.mrspd.letschats.util.LetsChatActivity


class AddMembersToGroupViewModel : ViewModel() {


    var calledBefore = false

    init {
        getUserData()
    }

    private val messageCollectionReference = FirestoreUtil.firestoreInstance.collection("messages")

    //  val    grouplist = ArrayList<String>()
    private val groupParticipantList: MutableList<GroupName> by lazy { mutableListOf<GroupName>() }
    private val grouplist =
        MutableLiveData<MutableList<GroupName>>()
    val loggedUserMutableLiveData = MutableLiveData<User>()


    fun getAllMembers(loggedUser: User): MutableLiveData<MutableList<GroupName>> {

        //this method is called each time user document changes but i want to attach listener only once so check with calledBefore
        if (calledBefore) {
            return grouplist

        }

        calledBefore = true

        val loggedUserId = loggedUser.uid.toString()

        val query: Query = FirestoreUtil.firestoreInstance.collection("messages")
            .whereArrayContains("chat_members_in_group", loggedUserId)

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                groupParticipantList.clear()

                if (!querySnapshot?.documents.isNullOrEmpty()) {
                    println("Aree  hua bhai ")



                    querySnapshot?.documents?.forEach { document ->
                        val groupName = GroupName()
                        groupName.group_name = document.get("group_name") as String?
                        groupName.imageurl = document.get("imageurl") as String?
                        groupName.description = document.get("description") as String?
                        groupName.chat_members_in_group =
                            document.get("chat_members_in_group") as List<String>?
                        groupParticipantList.add(groupName)
                        grouplist.value = groupParticipantList
                    }
                } else {
                    println("Aree nahi hua bhai sorry $loggedUserId")
                    //user has no chats
                    grouplist.value = null
                }
            } else {
                //error
                println("Aree error hua bhai sorry $loggedUserId")

                println("HomeViewModel.getChats:${firebaseFirestoreException.message}")
                grouplist.value = null
            }
        }
        return grouplist
    }

    fun updateUserProfileForGroups(groupname: String, members: ArrayList<String>) {

        messageCollectionReference.document(groupname).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    //this node exists send your message
                    messageCollectionReference.document(groupname)
                        .update("chat_members_in_group", members)

                }


                ////////////////////////////////////////////////////////////////////////////////////////////////////
                var userDocRef: DocumentReference? = AuthUtil.getAuthId().let {
                    FirestoreUtil.firestoreInstance.collection("users").document(it)
                }
                userDocRef?.update(
                    "groups_in",
                    FieldValue.arrayUnion(groupname, groupname)
                )
                    ?.addOnSuccessListener {
                        // bioLoadState.value = LoadState.SUCCESS
                        Log.d("gghh", "added group in user succesfully")
                    }
                    ?.addOnFailureListener {
                        // bioLoadState.value = LoadState.FAILURE
                        Log.d("gghh", "added group in user failurly")
                    }
            }

    }
        fun getUserData() {

            FirestoreUtil.firestoreInstance.collection("users").document(AuthUtil.getAuthId())
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException == null) {
                        val loggedUser = documentSnapshot?.toObject(User::class.java)
                        if (loggedUser != null) {
                            loggedUserMutableLiveData.value = loggedUser
                        }
                    } else {
                        println("HomeViewModel.getUserData:${firebaseFirestoreException.message}")
                    }
                }
        }


    }

