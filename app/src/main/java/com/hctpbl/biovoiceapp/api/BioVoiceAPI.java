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

/**
 * Retrofit interface to access the REST API
 */
public interface BioVoiceAPI {

    /**
     * Creates a new User
     * @param user User to create
     * @return response of type new user
     */
    @POST("/v1/users")
    public NewUserResponse createUser(@Body User user);

    /**
     * Gets the status of a User
     * @param username User to check status to
     * @return response of type user status
     */
    @GET("/v1/voiceaccess/status/{username}")
    public UserStatus userStatus(@Path("username") String username);

    /**
     * Uploads an audio file to perform enrollment
     * @param username User to enroll
     * @param audioFile Audio file with the audio sample
     * @return Response of type voice access
     */
    @Multipart
    @POST("/v1/voiceaccess/enroll/{username}")
    public VoiceAccessResponse enrollUser(@Path("username") String username, @Part("audiofile") TypedFile audioFile);

    /**
     * Uploads an audio file to perform verification
     * @param username User to verify
     * @param audioFile Audio file with the sample
     * @return Response of type voice access
     */
    @Multipart
    @POST("/v1/voiceaccess/test/{username}")
    public VoiceAccessResponse testUser(@Path("username") String username, @Part("audiofile") TypedFile audioFile);

}
