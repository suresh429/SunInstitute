package com.sun.institute.network;

import com.sun.institute.response.DepartmentResponse;
import com.sun.institute.response.SectionResponse;
import com.sun.institute.response.StatusResponse;
import com.sun.institute.response.StudentsResponse;
import com.sun.institute.response.SubjectResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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


    @GET("app.php?act=facultyInsert")
    Call<StatusResponse> saveFinger(@Query("facultyInsert") String facultyInsert,
                                       @Query("fname") String fname,
                                       @Query("last_name") String last_name,
                                       @Query("email") String email,
                                       @Query("mobile") String mobile,
                                       @Query("type") String type,
                                       @Query("thumb") String thumb
    );


}
