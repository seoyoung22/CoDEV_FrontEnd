package com.example.codev.addpage

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.codev.*
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class Project2Server {

    fun createPartNumList(
        partNumberList: List<Int>
    ): List<PartNameAndPeople> {
        var partList = ArrayList<PartNameAndPeople>()
        partList.add(PartNameAndPeople("기획", partNumberList[0]))
        partList.add(PartNameAndPeople("디자인", partNumberList[1]))
        partList.add(PartNameAndPeople("프론트엔드", partNumberList[2]))
        partList.add(PartNameAndPeople("백엔드", partNumberList[3]))
        partList.add(PartNameAndPeople("기타", partNumberList[4]))
        return partList.toList()
    }

    fun createImageMultiPartList(imagePathList: ArrayList<String>, newName:String = ""): List<MultipartBody.Part>{
        var fileMultipartList = ArrayList<MultipartBody.Part>()
        for (i in imagePathList) {
            var fileFromPath = File(i)
            var fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),fileFromPath)
            val filePart = MultipartBody.Part.createFormData("files", fileFromPath.name, fileBody)
            fileMultipartList.add(filePart)
        }
        return fileMultipartList.toList()
    }

    fun createImageMultiPartListUsingFile(imageFileList: ArrayList<File>, newName:String = ""): List<MultipartBody.Part>{
        val fileMultipartList = ArrayList<MultipartBody.Part>()
        for (i in imageFileList) {
            val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),i)
            val filePart = MultipartBody.Part.createFormData("files", i.name, fileBody)
            fileMultipartList.add(filePart)
        }
        return fileMultipartList.toList()
    }

    fun postNewProject(context: Context, title: String, content: String, location: String, stackList: List<Int>, deadLine: String, numPerPart: List<PartNameAndPeople>, imagePartList: List<MultipartBody.Part>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqCreateNewProject = ReqCreateNewProject(
            title,
            content,
            location,
            stackList,
            deadLine,
            numPerPart
        )
        val jsonObject = Gson().toJson(reqCreateNewProject)
        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)
        Log.d("postJson", jsonObject.toString())

        var emptyList = ArrayList<MultipartBody.Part>()
        var emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("files", null, emptyFileBody)
        emptyList.add(emptyFilePart)

        var finalArray: List<MultipartBody.Part> = emptyList.toList()
        if(imagePartList.isNotEmpty()) {
            finalArray = imagePartList.toList()
        }


        RetrofitClient.service.createNewProject(userToken, requestBody, finalArray).enqueue(object:
            Callback<ResCreateNewProject> {
            override fun onResponse(
                call: Call<ResCreateNewProject>,
                response: Response<ResCreateNewProject>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("Fail",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                response.body()?.let { Log.d("Success: testCreateNewProject", "\n${it.result.message}")}
                finishPage()
            }
            override fun onFailure(call: Call<ResCreateNewProject>, t: Throwable) {
                Log.d("FAIL", "[Fail]${t.toString()}")
            }
        })
    }

    fun updateProject(context: Context, projectId: String, title: String, content: String, location: String, stackList: List<Int>, deadLine: String, numPerPart: List<PartNameAndPeople>, imagePartList: List<MultipartBody.Part>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqUpdateProject = ReqUpdateProject(
            title,
            content,
            location,
            stackList,
            deadLine,
            numPerPart,
            "ING"
        )
        val jsonObject = Gson().toJson(reqUpdateProject)
        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)
        Log.d("postJson", jsonObject.toString())

        var emptyList = ArrayList<MultipartBody.Part>()
        var emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("files", null, emptyFileBody)
        emptyList.add(emptyFilePart)

        var finalArray: List<MultipartBody.Part> = emptyList.toList()
        if(imagePartList.isNotEmpty()) {
            finalArray = imagePartList.toList()
        }

        RetrofitClient.service.updateProject(projectId, userToken, requestBody, finalArray).enqueue(object:
            Callback<ResCreateNewProject> {
            override fun onResponse(
                call: Call<ResCreateNewProject>,
                response: Response<ResCreateNewProject>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("Fail",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                response.body()?.let { Log.d("Success: testCreateNewProject", "\n${it.result.message}")}
                finishPage()
            }

            override fun onFailure(call: Call<ResCreateNewProject>, t: Throwable) {
                Log.d("FAIL", "[Fail]${t.toString()}")
            }
        })
    }

    fun postNewStudy(context: Context, title: String, content: String, location: String, stackList: List<Int>, deadLine: String, stack1Name: String, peopleNumber: Int, imagePartList: List<MultipartBody.Part>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqCreateNewStudy = ReqCreateNewStudy(
            title,
            content,
            location,
            stackList,
            deadLine,
            stack1Name,
            peopleNumber,
        )
        val jsonObject = Gson().toJson(reqCreateNewStudy)
        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)
        Log.d("postJson", jsonObject.toString())

        var emptyList = ArrayList<MultipartBody.Part>()
        var emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("files", null, emptyFileBody)
        emptyList.add(emptyFilePart)

        var finalArray: List<MultipartBody.Part> = emptyList.toList()
        if(imagePartList.isNotEmpty()) {
            finalArray = imagePartList
        }

        RetrofitClient.service.createNewStudy(userToken, requestBody, finalArray).enqueue(object:
            Callback<ResCreateNewStudy> {
            override fun onResponse(
                call: Call<ResCreateNewStudy>,
                response: Response<ResCreateNewStudy>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("FailOnResponse",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                Toast.makeText(context, "업로드 성공!!!", Toast.LENGTH_SHORT).show()
                response.body()?.let { Log.d("Success: testCreateNewStudy", "\n${it.result.message}")}
                finishPage()
            }

            override fun onFailure(call: Call<ResCreateNewStudy>, t: Throwable) {
                Log.d("OnFailure", "[Fail]${t.toString()}")

            }
        })
    }

    fun updateStudy(context: Context, studyId: String, title: String, content: String, location: String, stackList: List<Int>, deadLine: String, stack1Name: String, peopleNumber: Int, imagePartList: List<MultipartBody.Part>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqUpdateStudy = ReqUpdateStudy(
            title,
            content,
            location,
            stackList,
            deadLine,
            stack1Name,
            peopleNumber,
            "ING"
        )
        val jsonObject = Gson().toJson(reqUpdateStudy)
        var requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)
        Log.d("postJson", jsonObject.toString())

        var emptyList = ArrayList<MultipartBody.Part>()
        var emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("files", null, emptyFileBody)
        emptyList.add(emptyFilePart)

        var finalArray: List<MultipartBody.Part> = emptyList.toList()
        if(imagePartList.isNotEmpty()) {
            finalArray = imagePartList
        }

        RetrofitClient.service.updateStudy(studyId, userToken, requestBody, finalArray).enqueue(object:
            Callback<ResCreateNewStudy> {
            override fun onResponse(
                call: Call<ResCreateNewStudy>,
                response: Response<ResCreateNewStudy>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("FailOnResponse",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                Toast.makeText(context, "업로드 성공!!!", Toast.LENGTH_SHORT).show()
                response.body()?.let { Log.d("Success: testCreateNewStudy", "\n${it.result.message}")}
                finishPage()
            }

            override fun onFailure(call: Call<ResCreateNewStudy>, t: Throwable) {
                Log.d("OnFailure", "[Fail]${t.toString()}")

            }
        })
    }


    fun postNewPF(context: Context, title: String, level: String, intro: String, content: String, stackList: List<Int>, linkList: List<String>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqCreateNewPF = ReqCreateNewPF(
            title,
            level,
            intro,
            content,
            stackList,
            linkList
        )
        Log.d("postClass", reqCreateNewPF.toString())
        RetrofitClient.service.createNewPF(userToken, reqCreateNewPF).enqueue(object:
            Callback<ResCreateNewPF> {
            override fun onResponse(
                call: Call<ResCreateNewPF>,
                response: Response<ResCreateNewPF>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("FailOnResponse",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                response.body()?.let { Log.d("Success: testCreateNewStudy", "\n${it.result.message}")}
                finishPage()
            }

            override fun onFailure(call: Call<ResCreateNewPF>, t: Throwable) {
                Log.d("OnFailure", "[Fail]${t.toString()}")
            }
        })
    }

    fun updatePF(context: Context, pfId: String, title: String, level: String, intro: String, content: String, stackList: List<Int>, linkList: List<String>, finishPage: () -> Unit){
        AndroidKeyStoreUtil.init(context)
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)
        val reqUpdatePF = ReqUpdatePF(
            title,
            level,
            intro,
            content,
            stackList,
            linkList
        )
        Log.d("postClass", reqUpdatePF.toString())
        RetrofitClient.service.updatePF(pfId, userToken, reqUpdatePF).enqueue(object:
            Callback<ResCreateNewPF> {
            override fun onResponse(
                call: Call<ResCreateNewPF>,
                response: Response<ResCreateNewPF>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("FailOnResponse",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()

                }
                response.body()?.let { Log.d("Success: testCreateNewStudy", "\n${it.result.message}")}
                finishPage()
            }

            override fun onFailure(call: Call<ResCreateNewPF>, t: Throwable) {
                Log.d("OnFailure", "[Fail]${t.toString()}")
            }
        })
    }
}
