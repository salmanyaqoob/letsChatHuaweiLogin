package com.mrspd.letschats.ui.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.mrspd.letschats.models.GroupName
import com.mrspd.letschats.models.User
import com.mrspd.letschats.util.FirestoreUtil


class SharedViewModel : ViewModel() {


    private var friendsListMutableLiveData =
        MutableLiveData<List<User>>()
    private var usersCollectionRef: CollectionReference =
        FirestoreUtil.firestoreInstance.collection("users")



    fun loadMembers(group: GroupName): LiveData<List<User>> {

        val friendsIds = group.chat_members_in_group
        if (!friendsIds.isNullOrEmpty()) {
            val mFriendList = mutableListOf<User>()
            for (friendId in friendsIds) {
                usersCollectionRef.document(friendId).get()
                    .addOnSuccessListener { friendUser ->
                        val friend =
                            friendUser.toObject(User::class.java)
                        friend?.let { user -> mFriendList.add(user) }
                        friendsListMutableLiveData.value = mFriendList
                    }
            }
        } else {
            //user has no friends
            friendsListMutableLiveData.value = null
        }

        return friendsListMutableLiveData
    }

    fun loadFriends(loggedUser: User): MutableLiveData<List<User>> {

        val friendsIds = loggedUser.friends
        if (!friendsIds.isNullOrEmpty()) {
                    val mFriendList = mutableListOf<User>()
                    for (friendId in friendsIds) {
                        usersCollectionRef.document(friendId).get()
                            .addOnSuccessListener { friendUser ->
                            val friend =
                                friendUser.toObject(User::class.java)
                            friend?.let { user -> mFriendList.add(user) }
                            friendsListMutableLiveData.value = mFriendList
                        }
                    }
                } else {
            //user has no friends
                    friendsListMutableLiveData.value = null
                }

        return friendsListMutableLiveData
    }



}