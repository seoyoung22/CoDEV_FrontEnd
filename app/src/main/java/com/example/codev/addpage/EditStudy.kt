package com.example.codev.addpage

import java.io.Serializable

data class EditStudy(
    val studyId: String,
    val title: String,
    val content: String,
    val imageUrl: List<String>, //리스트로 이미지 Url(co_fileUrl)를 보내주세요, 없으면 null값으로

    val partName: String,
    val partPeople: Int,

    //hashMap으로 모든 언어의 Id와 이름을 추가해서 보내주세요!, 없으면 null으로 보내주세요
    val languageIdNameMap: HashMap<Int, String>,
    val location: String,
    val deadLine: String //마감시간은 YYYY-MM-DD 형식으로 보내주세면 감사하겠습니다!
) : Serializable
