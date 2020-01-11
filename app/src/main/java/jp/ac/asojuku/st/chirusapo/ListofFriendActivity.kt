package jp.ac.asojuku.st.chirusapo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import jp.ac.asojuku.st.chirusapo.adapters.ChildFriendAdapter
import jp.ac.asojuku.st.chirusapo.adapters.ChildFriendList
import jp.ac.asojuku.st.chirusapo.apis.Api
import jp.ac.asojuku.st.chirusapo.apis.ApiError
import jp.ac.asojuku.st.chirusapo.apis.ApiGetTask
import jp.ac.asojuku.st.chirusapo.apis.ApiParam
import kotlinx.android.synthetic.main.activity_listof_friend.*

@SuppressLint("Registered")
class ListofFriendActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listof_friend)

        supportActionBar?.let {
            title = "友達一覧"
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        realm = Realm.getDefaultInstance()

        getFriendList()
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getFriendList(){

        realm.executeTransaction {
            val account: Account? = realm.where<Account>().findFirst()
            val childId = intent.getStringExtra("user_id")

            if (account !== null) {
                val token = account.Rtoken
                ApiGetTask {
                    if (it == null) {
                        ApiError.showToast(this, ApiError.CONNECTION_ERROR, Toast.LENGTH_LONG)
                    } else {
                        when (it.getString("status")) {
                            "200" -> {
                                val friendMember =
                                    it.getJSONObject("data").getJSONArray("friend_list")
                                val list = ArrayList<ChildFriendList>()
                                for (i in 0 until friendMember.length()) {
                                    val memberInfo = friendMember.getJSONObject(i)
                                    val memberName = memberInfo.getString("user_name")
                                    val friendId = memberInfo.getString("id")
                                    val memberIcon = if (memberInfo.isNull("user_icon")) {
                                        null
                                    } else {
                                        memberInfo.getString("user_icon")
                                    }

                                    val item = ChildFriendList()
                                    item.id = i.toLong()
                                    item.userName = memberName
                                    item.userIcon = memberIcon
                                    item.friendId = friendId
                                    list.add(item)
                                }
                                val listView = listview
                                val adapter = ChildFriendAdapter(this)
                                adapter.setSampleListItem(list)
                                adapter.notifyDataSetChanged()
                                listView.adapter = adapter
                                listView.setOnItemClickListener { adapterView, _, i, _ ->
                                    val item = adapterView.getItemAtPosition(i) as ChildFriendList
                                    val intent =
                                        Intent(this, CheckFriendActivity::class.java)
                                    intent.putExtra("childId", childId)
                                    intent.putExtra("friendId",item.friendId)
                                    startActivity(intent)
                                }
                            }
                            "400" -> {
                                val errorArray = it.getJSONArray("message")
                                for (i in 0 until errorArray.length()) {
                                    when (errorArray.getString(i)) {
                                        // 値が不足している場合に表示
                                        ApiError.REQUIRED_PARAM -> {
                                            ApiError.showToast(
                                                this,
                                                errorArray.getString(i),
                                                Toast.LENGTH_LONG
                                            )
                                        }
                                        ApiError.UNAUTHORIZED_OPERATION -> {
                                            ApiError.showToast(
                                                this,
                                                errorArray.getString(i),
                                                Toast.LENGTH_LONG
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.execute(
                    ApiParam(
                        Api.SLIM + "child/friend/get",
                        hashMapOf("token" to token, "child_id" to childId)
                    )
                )
            }
        }

    }

}
