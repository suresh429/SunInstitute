package com.sun.institute.network;

import com.sun.institute.response.DepartmentResponse;
import com.sun.institute.response.FacultyList;
import com.sun.institute.response.HomeResponse;
import com.sun.institute.response.LoginResponse;
import com.sun.institute.response.SectionResponse;
import com.sun.institute.response.StatusResponse;
import com.sun.institute.response.StudentsResponse;
import com.sun.institute.response.SubjectResponse;
import com.sun.institute.response.TimeTableResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("app.php")
    Call<DepartmentResponse> getDepartment(@Query("act") String cls);

    @GET("app.php")
    Call<SectionResponse> getSection(@Query("act") String section,@Query("id") String id);

    @GET("app.php")
    Call<SubjectResponse> getSubject(@Query("act") String subject);

    @GET("app.php")
    Call<StudentsResponse> getAllStudents(@Query("act") String subject, @Query("sec_id") String id);


    @GET("app.php")
    Call<StatusResponse> setAttendance(@Query("act") String student_attendance,
                                       @Query("stu_id") String stu_id,
                                       @Query("status") String status,
                                       @Query("sub_id") String sub_id,
                                       @Query("dept_id") String dept_id,
                                       @Query("sec_id") String sec_id
                                      );


    @FormUrlEncoded
    @POST("app.php?act=facultyInsert")
    Call<StatusResponse> registerFinger(
                                       @Field("fname") String fname,
                                       @Field("last_name") String last_name,
                                       @Field("email") String email,
                                       @Field("mobile") String mobile,
                                       @Field("type") String type,
                                       @Field("thumb") String thumb
    );




   /* @POST("app.php?act=facultLogin&mobile=8465945100")
    Call<FacultyList> loginFinger(
           // @Query("mobile") String thumb
    );*/

    @POST("app.php?act=facultLogin")
    Call<FacultyList> loginFinger(
             @Query("mobile") String thumb,
             @Query("carrent_time") String carrent_time,
             @Query("carrent_time1") String carrent_time1

    );

    @POST("app.php?act=facultAtt")
    Call<ResponseBody> facultyAtt(
            @Query("id") String id

    );

    @Multipart
    @POST("app.php?act=cpldata")
    Call<ResponseBody> cplData(
            @Part("timetable_id") RequestBody timetable_id,
            @Part("note") RequestBody note,
            @Part MultipartBody.Part file

    );


    @GET("app.php?act=Timetable")
    Call<TimeTableResponse> timeTable(
            @Query("id") String id

    );

    @GET("app.php?act=Dashboard")
    Call<HomeResponse> dashBoard(
            @Query("timetable_id") String timetable_id

    );



    @GET("app.php?act=ThumbData")
    Call<FacultyList> allLogin(
    );




}
