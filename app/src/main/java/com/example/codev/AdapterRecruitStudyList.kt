package com.example.codev

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecycleRecruitListBinding

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterRecruitStudyList(private val context: Context, private val listData: ArrayList<SData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  StudyItemViewHolder(context, RecycleRecruitListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is StudyItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class StudyItemViewHolder(val context: Context, private val binding: RecycleRecruitListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: SData, position: Int){
            //스터디 제목
            binding.title.text = data.co_title

            //스터디 디데이
            if(data.co_deadLine.toInt() == 0) {
                binding.dday.text = "D-Day"
            }
            else if(data.co_deadLine.toInt() < 0){ //기간 지남
                val deadline = data.co_deadLine
                val dday = deadline.substring(1,deadline.length)

                binding.dday.text = "D+" + dday
            }
            else {binding.dday.text = "D-" + data.co_deadLine}

            //스터디 총 인원
            binding.num.text = data.co_total.toString()

            //기술 스택
            if (!data.co_languages.isNullOrBlank()){
                val languages = data.co_languages
                binding.stack.adapter = AdapterRecruitStack(context,languages.split(","))
            }

            //스터디 분야
            binding.partlist.text = data.co_part

            //북마크
            binding.heart.isChecked = listData[position].co_heart
            binding.heart.setOnClickListener {
                listData[position].co_heart = binding.heart.isChecked
                request(data.co_studyId)
            }

            binding.item.setOnClickListener {
                val intent = Intent(binding.item.context, RecruitDetailActivity::class.java)
                intent.putExtra("id",data.co_studyId)
                intent.putExtra("type","STUDY")
                intent.putExtra("dday",binding.dday.text)
                Log.d("test : 선택한 스터디 아이디", data.co_studyId.toString())
                startActivity(binding.item.context, intent, null)
            }
        }
    }

    private fun request(co_studyId: Int){
        RetrofitClient.service.requestStudyBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),co_studyId).enqueue(object:
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: AdapterS__request() 성공! ", "\n${it.toString()}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: AdapterS__request()실패 : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}