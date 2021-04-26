package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentsResponse {


    @SerializedName("msg")
    private int msg;
    @SerializedName("info")
    private List<InfoBean> info;

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean {
        @SerializedName("stu_id")
        private String stuId;
        @SerializedName("admission_no")
        private String admissionNo;
        @SerializedName("course_id")
        private String courseId;
        @SerializedName("dept_id")
        private String deptId;
        @SerializedName("sec_id")
        private String secId;
        @SerializedName("stu_name")
        private String stuName;
        @SerializedName("stu_lname")
        private String stuLname;
        @SerializedName("gender")
        private String gender;
        @SerializedName("dob")
        private String dob;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("email")
        private String email;
        @SerializedName("caste")
        private String caste;
        @SerializedName("blood")
        private String blood;
        @SerializedName("father_name")
        private String fatherName;
        @SerializedName("mother_name")
        private String motherName;
        @SerializedName("father_contact")
        private String fatherContact;
        @SerializedName("admission_date")
        private String admissionDate;
        @SerializedName("admission_fee")
        private String admissionFee;
        @SerializedName("payed_amount")
        private String payedAmount;
        @SerializedName("pendding_fee")
        private String penddingFee;
        @SerializedName("stu_pic")
        private String stuPic;
        @SerializedName("address")
        private String address;
        @SerializedName("status")
        private String status;
        @SerializedName("create_date")
        private String createDate;
        @SerializedName("update_date")
        private String updateDate;
        @SerializedName("uid")
        private String uid;
        @SerializedName("tution_fee")
        private String tutionFee;
        @SerializedName("hostel_fee")
        private String hostelFee;
        @SerializedName("bus_fee")
        private String busFee;

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getAdmissionNo() {
            return admissionNo;
        }

        public void setAdmissionNo(String admissionNo) {
            this.admissionNo = admissionNo;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getSecId() {
            return secId;
        }

        public void setSecId(String secId) {
            this.secId = secId;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public String getStuLname() {
            return stuLname;
        }

        public void setStuLname(String stuLname) {
            this.stuLname = stuLname;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCaste() {
            return caste;
        }

        public void setCaste(String caste) {
            this.caste = caste;
        }

        public String getBlood() {
            return blood;
        }

        public void setBlood(String blood) {
            this.blood = blood;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public String getFatherContact() {
            return fatherContact;
        }

        public void setFatherContact(String fatherContact) {
            this.fatherContact = fatherContact;
        }

        public String getAdmissionDate() {
            return admissionDate;
        }

        public void setAdmissionDate(String admissionDate) {
            this.admissionDate = admissionDate;
        }

        public String getAdmissionFee() {
            return admissionFee;
        }

        public void setAdmissionFee(String admissionFee) {
            this.admissionFee = admissionFee;
        }

        public String getPayedAmount() {
            return payedAmount;
        }

        public void setPayedAmount(String payedAmount) {
            this.payedAmount = payedAmount;
        }

        public String getPenddingFee() {
            return penddingFee;
        }

        public void setPenddingFee(String penddingFee) {
            this.penddingFee = penddingFee;
        }

        public String getStuPic() {
            return stuPic;
        }

        public void setStuPic(String stuPic) {
            this.stuPic = stuPic;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTutionFee() {
            return tutionFee;
        }

        public void setTutionFee(String tutionFee) {
            this.tutionFee = tutionFee;
        }

        public String getHostelFee() {
            return hostelFee;
        }

        public void setHostelFee(String hostelFee) {
            this.hostelFee = hostelFee;
        }

        public String getBusFee() {
            return busFee;
        }

        public void setBusFee(String busFee) {
            this.busFee = busFee;
        }
    }
}
