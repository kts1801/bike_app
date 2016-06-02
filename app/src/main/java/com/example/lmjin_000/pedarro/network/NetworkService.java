package com.example.lmjin_000.pedarro.network;

import com.example.lmjin_000.pedarro.model.Bike;
import com.example.lmjin_000.pedarro.model.BikeTmap;
import com.example.lmjin_000.pedarro.model.Rent;
import com.example.lmjin_000.pedarro.model.Station;
import com.example.lmjin_000.pedarro.model.TestTmapapi;
import com.example.lmjin_000.pedarro.model.registerJ;
import com.example.lmjin_000.pedarro.model.v2.ResultObj;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface NetworkService {

    /**
     * 다음api에서 제공해주는 API_KEY를 String 형으로 저장
     * GET 어노테이션과 메소드 구현
     * 서버에 요청할 디렉토리를 GET 어노테이션에 인자로 넣어줌
     * Call<받고자 하는 데이터 타입> (request에 추가될 사항들)
     * request에 추가될 사항들을 예로 들면
     * Body가 될 수도 있고(POST 방식의 경우)
     * Path가 될 수도 있고
     * QueryMap, Query가 될 수도 있고
     * Body, Path, Query가 다 들어갈 수도 있습니다.
     */

    @POST("/Tmap/{UserID}/{Arrivest}/{currentX}/{currentY}")
    Call<BikeTmap> getData(@Body BikeTmap bikeTmap, @Path("UserID") String UserID,
                           @Path("Arrivest") String arrivest,@Path("currentX") String currentX
                            ,@Path("currentY") String currentY);

    @POST("/Tmap/post")
    Call<BikeTmap> insertone(@Body BikeTmap bikeTmap);

    @GET("/Station/{id}")
    Call<Station> getStation(@Path("id") String id);

    @GET("/Station")
    Call<List<Station>> allStation();

    @GET("/Push/{registerID}")
    Call<String> pushGCM(@Path("registerID") String id);

    @POST("/Login/")
    Call<registerJ> post_register(@Body registerJ register);

    @POST("/Login/update/{id}")
    Call<registerJ> edit_user(@Body registerJ register,@Path("id") String id);

    @POST("/Login/changePW/{id}")
    Call<registerJ> change_pw(@Body registerJ register,@Path("id") String id);

    @GET("/Login/{id}")
    Call<registerJ> getUserInfo(@Path("id") String id);

    @GET("/Login/{loginId}/{pass}")
    Call <registerJ> getNameLogin( @Path("loginId") String loginId , @Path("pass") String pass );

    @POST("/Rent")
    Call<Rent> randomnum(@Body Rent rent);

    @GET("/Rent/{id}")
    Call<Rent> usingbike(@Path("id") String id);

    @GET("/Rent/delete/{id}")
    Call<Rent> deleteRent(@Path("id") String id);

    @GET("/Tmap/{id}/{del}")
    Call<BikeTmap> getBikeid(@Path("id") String id,@Path("del") int del);

    @GET("/Tmap/Navi/{Arrivest}/{currentX}/{currentY}")
    Call<TestTmapapi> testTmapapi(@Path("Arrivest") String Arrivest,@Path("currentX") String currentX,@Path("currentY") String currentY);

    //길찾기 테스트
    @GET("/Tmap/Road/{startX}/{startY}/{endX}/{endY}")
    Call<ResultObj> roadtest(@Path("startX") String startX,@Path("startY") String startY,@Path("endX") String endX,@Path("endY") String endY);

    @POST("/Bike")
    Call<Bike> biketrouble(@Body Bike bike);

    @GET("/Bike/Arrive/{Stationid}")
    Call<List<BikeTmap>> arrive_info(@Path("Stationid") String stationid);

    @GET("/Bike/Recommend/{Stationid}")
    Call<List<Bike>> recommend_info(@Path("Stationid") String stationid);
}
