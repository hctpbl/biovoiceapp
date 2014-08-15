package com.hctpbl.biovoiceapp.api;

import com.hctpbl.biovoiceapp.api.model.NewUserResponse;
import com.hctpbl.biovoiceapp.api.model.User;
import com.hctpbl.biovoiceapp.api.model.UserStatus;
import com.hctpbl.biovoiceapp.api.model.VoiceAccessResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface BioVoiceAPI {
    @POST("/v1/users")
    public NewUserResponse createUser(@Body User user);

    @GET("/v1/voiceaccess/status/{username}")
    public UserStatus userStatus(@Path("username") String username);

    @Multipart
    @POST("/v1/voiceaccess/enroll/{username}")
    public VoiceAccessResponse enrollUser(@Path("username") String username, @Part("audiofile") TypedFile audioFile);

    @Multipart
    @POST("/v1/voiceaccess/test/{username}")
    public VoiceAccessResponse testUser(@Path("username") String username, @Part("audiofile") TypedFile audioFile);

}
