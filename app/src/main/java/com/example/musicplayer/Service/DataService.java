package com.example.musicplayer.Service;

import com.example.musicplayer.Model.Baihat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DataService {
    //gửi phương thức và nhận dữ liệu từ phía server

    @GET("danhsachbaihat.php")
    Call<List<Baihat>> GetDanhSachBaiHat();
}
