package jp.ac.asojuku.st.chirusapo

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.*
import java.util.*

open class Book : RealmObject() {
    @PrimaryKey open var Ruser_id : Int = 0
    @Required open var Ruser_name: String = ""
    open var Ruser_icon: String? = ""
    open var Rtoken: String = ""
}

//open class allergy : RealmObject(){
//    @PrimaryKey open var allergy_name: String? = ""
//}
//
//open class vaccine : RealmObject(){
//    @PrimaryKey open var vaccine_name: String? = ""
//}
//
//open class join_group : RealmObject(){
//    @PrimaryKey open var Rgroup_id : Int = 0
//    @Required open var Rgroup_name : String = ""
//}